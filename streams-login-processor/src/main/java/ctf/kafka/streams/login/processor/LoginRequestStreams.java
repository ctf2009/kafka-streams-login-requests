package ctf.kafka.streams.login.processor;

import ctf.kafka.streams.common.kafka.Topics;
import ctf.kafka.streams.common.model.EnrichedIpAddress;
import ctf.kafka.streams.common.model.LoginRequest;
import ctf.kafka.streams.common.model.LoginRequestRaw;
import ctf.kafka.streams.common.streams.StreamsConfiguration;
import ctf.kafka.streams.common.streams.serdes.JsonSerde;
import ctf.kafka.streams.login.processor.joiner.LoginRequestIpEnrichmentJoiner;
import ctf.kafka.streams.login.processor.mapper.LoginRequestMapper;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.JoinWindows;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.StreamJoined;
import org.apache.kafka.streams.kstream.ValueJoiner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Properties;

import static ctf.kafka.streams.common.kafka.Topics.LOGIN_REQUEST_HISTORY_TOPIC;

public class LoginRequestStreams {

    private static final Logger LOG = LoggerFactory.getLogger(LoginRequestStreams.class);

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

        final JsonSerde<LoginRequestRaw> loginRequestRawSerde = new JsonSerde<>(LoginRequestRaw.class);
        final JsonSerde<LoginRequest> loginRequestSerde = new JsonSerde<>(LoginRequest.class);
        final JsonSerde<EnrichedIpAddress> ipEnrichmentJsonSerde = new JsonSerde<>(EnrichedIpAddress.class);

        final ValueJoiner<LoginRequest, EnrichedIpAddress, LoginRequest> loginRequestIpEnrichmentJoiner = new LoginRequestIpEnrichmentJoiner();

        // Consume the IP_ENRICHED_TOPIC
        final KStream<String, EnrichedIpAddress> ipEnrichmentResults = builder.stream(
                Topics.IP_ENRICHED_TOPIC, Consumed.with(Serdes.String(), ipEnrichmentJsonSerde));

        // Consume the LOGIN_REQUEST_RAW_TOPIC
        final KStream<String, LoginRequestRaw> loginRequestsRaw =
                builder.stream(Topics.LOGIN_REQUEST_RAW_TOPIC, Consumed.with(Serdes.String(), loginRequestRawSerde));

        // Send the LoginRequestRaw to the LOGIN_REQUEST_IP_PROCESSING_TOPIC
        loginRequestsRaw.selectKey((key, value) -> value.getIpAddress())
            .to(Topics.LOGIN_REQUEST_IP_PROCESSING_TOPIC, Produced.with(Serdes.String(), loginRequestRawSerde));

        // Build a LoginRequest for each message consumed from the LOGIN_REQUEST_RAW_TOPIC
        loginRequestsRaw.mapValues(new LoginRequestMapper())
                // Here we try to see if there is an IpEnrichmentResult available for LoginRequest. The window means that
                // if we receive an IpEnrichmentResult after we have processed the LoginRequest, a join will be triggered
                // as long as the timestamp of the IpEnrichmentResult message is within the join window
                .leftJoin(ipEnrichmentResults, loginRequestIpEnrichmentJoiner,
                        JoinWindows.of(Duration.ofHours(5)).before(Duration.ZERO).grace(Duration.ZERO),
                        StreamJoined.with(Serdes.String(), loginRequestSerde, ipEnrichmentJsonSerde))
                .selectKey((key, value) -> value.getUserId())
                .to(LOGIN_REQUEST_HISTORY_TOPIC, Produced.with(Serdes.String(), loginRequestSerde));
    }

}
