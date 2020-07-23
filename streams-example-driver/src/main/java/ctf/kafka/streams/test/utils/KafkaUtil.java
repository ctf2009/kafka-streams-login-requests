package ctf.kafka.streams.test.utils;

import io.confluent.kafka.serializers.KafkaJsonSerializer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Properties;

public class KafkaUtil {

    public static <V> KafkaProducer<String, V> getKafkaJsonProducer(final String[] hosts) throws UnknownHostException {
        final Properties config = new Properties();
        config.put("client.id", InetAddress.getLocalHost().getHostName());
        config.put("acks", "all");
        config.put("bootstrap.servers", String.join(",", hosts));

        KafkaJsonSerializer<V> kafkaJsonSerializer = new KafkaJsonSerializer<>();
        kafkaJsonSerializer.configure(Collections.EMPTY_MAP, false);
        return new KafkaProducer<>(config, new StringSerializer(), kafkaJsonSerializer);
    }

}
