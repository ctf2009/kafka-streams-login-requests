package ctf.kafka.streams.common.streams;

import org.apache.kafka.common.config.TopicConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsConfig;

import java.io.IOException;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class StreamsConfiguration {

    private static final String STREAMS_PROPERTIES_FILE_NAME = "streams.properties";

    public static Properties get() throws Exception {
        final Properties propertiesFileProps = loadClassPathProperties();

        final Properties streamsConfiguration = new Properties();

        // Give the Streams application a unique name.  The name must be unique in the Kafka cluster against which the application is run.
        if (propertiesFileProps.get("application-id") != null) {
            streamsConfiguration.put(StreamsConfig.APPLICATION_ID_CONFIG, propertiesFileProps.get("application-id"));
        } else {
            throw new IllegalStateException("An application-id must be provided in the streams.properties file");
        }

        // Where to find Kafka broker(s).
        streamsConfiguration.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, propertiesFileProps.getOrDefault("bootstrap-servers", "localhost:9092"));

        // Dont add any additional time to Window Store Logs
        streamsConfiguration.put(StreamsConfig.WINDOW_STORE_CHANGE_LOG_ADDITIONAL_RETENTION_MS_CONFIG, 0);

        // Number of Stream Threads
        streamsConfiguration.put(StreamsConfig.NUM_STREAM_THREADS_CONFIG, propertiesFileProps.getProperty("num.stream.threads", "1"));

        // Specify default (de)serializers for record keys and for record values.
        streamsConfiguration.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        streamsConfiguration.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());

        // Records should be flushed every 5 seconds.
        streamsConfiguration.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 5 * 1000);

        // Optimise
        streamsConfiguration.put(StreamsConfig.TOPOLOGY_OPTIMIZATION, StreamsConfig.OPTIMIZE);

        return streamsConfiguration;
    }

    private static Properties loadClassPathProperties() throws IOException {
        final Properties properties = new Properties();
        properties.load(Objects.requireNonNull(StreamsConfiguration.class.getClassLoader().getResourceAsStream(STREAMS_PROPERTIES_FILE_NAME)));
        return properties;
    }

}
