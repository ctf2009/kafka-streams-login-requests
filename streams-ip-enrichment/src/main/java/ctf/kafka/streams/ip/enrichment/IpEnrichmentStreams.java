package ctf.kafka.streams.ip.enrichment;

import ctf.kafka.streams.common.kafka.Topics;
import ctf.kafka.streams.common.model.EnrichedIpAddress;
import ctf.kafka.streams.common.model.LoginRequestRaw;
import ctf.kafka.streams.common.streams.StreamsConfiguration;
import ctf.kafka.streams.common.streams.serdes.JsonSerde;
import ctf.kafka.streams.ip.enrichment.transformer.IpTransformer;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Produced;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class IpEnrichmentStreams {

    private static final Logger LOG = LoggerFactory.getLogger(IpEnrichmentStreams.class);

    public static void main(String[] args) throws Exception {

        final Properties streamsProperties = StreamsConfiguration.get();

        final StreamsBuilder builder = new StreamsBuilder();
        buildLoginStream(builder);

        final Topology topology = builder.build(streamsProperties);
        LOG.info(topology.describe().toString());

        final KafkaStreams streams = new KafkaStreams(topology, StreamsConfiguration.get());
        streams.start();

        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }

    private static void buildLoginStream(final StreamsBuilder builder) {

        final JsonSerde<LoginRequestRaw> ipProcessingJsonSerde = new JsonSerde<>(LoginRequestRaw.class);
        final JsonSerde<EnrichedIpAddress> ipEnrichmentJsonSerde = new JsonSerde<>(EnrichedIpAddress.class);

        // Consume the LOGIN_REQUEST_RAW_TOPIC
        builder.stream(Topics.LOGIN_REQUEST_IP_PROCESSING_TOPIC, Consumed.with(Serdes.String(), ipProcessingJsonSerde))
                .transform(IpTransformer::new)
                .to(Topics.IP_ENRICHED_TOPIC, Produced.with(Serdes.String(), ipEnrichmentJsonSerde));
    }
}
