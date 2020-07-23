package ctf.kafka.streams.ip;

import ctf.kafka.streams.common.kafka.Headers;
import ctf.kafka.streams.common.kafka.Topics;
import ctf.kafka.streams.common.model.GroupedLoginRequestByIp;
import ctf.kafka.streams.common.model.LoginRequestRaw;
import ctf.kafka.streams.common.model.Notification;
import ctf.kafka.streams.common.streams.StreamsConfiguration;
import ctf.kafka.streams.common.streams.serdes.JsonSerde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.Suppressed;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.kstream.Transformer;
import org.apache.kafka.streams.kstream.Windowed;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.WindowStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.Properties;

public class SuspiciousIpStreams {

    private static final Logger LOG = LoggerFactory.getLogger(SuspiciousIpStreams.class);

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

        final JsonSerde<LoginRequestRaw> loginRequestRawJsonSerde = new JsonSerde<>(LoginRequestRaw.class);
        final JsonSerde<GroupedLoginRequestByIp> groupedIpAddressJsonSerde = new JsonSerde<>(GroupedLoginRequestByIp.class);
        final JsonSerde<Notification> notificationJsonSerde = new JsonSerde<>(Notification.class);

        // Aggregating with time-based windowing (1 Minute Window)
        final KTable<Windowed<String>, GroupedLoginRequestByIp> groupedIpAddresses = builder
                .stream(Topics.LOGIN_REQUEST_IP_PROCESSING_TOPIC, Consumed.with(Serdes.String(), loginRequestRawJsonSerde))
                // Here we are grouping by the Key (IP Address)
                // We want to group all the LoginRequests that cam from the same IP Address together
                .groupByKey()
                // We want to look over the past X number of minutes
                .windowedBy(TimeWindows.of(Duration.ofMinutes(1)).grace(Duration.ofMinutes(0)))
                // Here we are aggregating the grouped Login Requests and producing a GroupedLoginRequestByIp Object
                .aggregate(GroupedLoginRequestByIp::new,
                        (key, value, aggregate) -> {
                            if (value.getStatus().equalsIgnoreCase("Success")) {
                                aggregate.getSuccessfulRequests().add(value);
                            } else {
                                aggregate.getFailedRequests().add(value);
                            }
                            return aggregate;
                        },
                        Materialized.<String, GroupedLoginRequestByIp, WindowStore<Bytes, byte[]>>as("grouped-ip-addresses-timed-windowed")
                                .withValueSerde(groupedIpAddressJsonSerde))

                // Suppress currently only works on Stream Time. This means if we suddenly stop receiving messages then the Stream Time freezes and
                // we may not get an event output until the next message arrives
                // See https://issues.apache.org/jira/browse/KAFKA-7748 for information on an update to this
                .suppress(Suppressed.untilWindowCloses(Suppressed.BufferConfig.unbounded()));

        // Get IP addresses With large number of failures in short period of time
        final KStream<String, Notification> highFailuresNotifications = groupedIpAddresses
                .toStream()
                .filter((key, value) -> value.getFailedRequests().size() >= 5)
                .map((key, value) -> KeyValue.pair(key.key(), Notification.builder()
                        .reference(key.key())
                        .severity("High")
                        .timestamp(Instant.now().toEpochMilli())
                        .message(String.format("Large Number of failures detected from IP Address: %s, Count: %d, Window: %s",
                                key.key(), value.getFailedRequests().size(), key.window())).build()));


        // Get IP addresses With large numbers of overall logins in short period of time
        final KStream<String, Notification> highLoginCountNotifications = groupedIpAddresses
                .toStream()
                .filter((key, value) -> value.getFailedRequests().size() + value.getSuccessfulRequests().size() >= 15)
                .map((key, value) -> KeyValue.pair(key.key(), Notification.builder()
                        .reference(key.key())
                        .severity("High")
                        .timestamp(Instant.now().toEpochMilli())
                        .message(String.format("Large Number of Login attempts from the same IP Address: %s, Count: %d, Window: %s",
                                key.key(), value.getFailedRequests().size() + value.getSuccessfulRequests().size(), key.window())).build()));

        // Merge the Streams and send the Notifications
        highFailuresNotifications
                .merge(highLoginCountNotifications)
                .transform(NotificationHeadersProcessor::new)
                .to(Topics.NOTIFICATION_TOPIC, Produced.with(Serdes.String(), notificationJsonSerde));

    }

    /**
     * Transformer for applying Headers
     */
    private static class NotificationHeadersProcessor implements Transformer<String, Notification, KeyValue<String, Notification>> {

        private ProcessorContext context;

        @Override
        public void init(final ProcessorContext context) {
            this.context = context;
        }

        @Override
        public KeyValue<String, Notification> transform(final String key, final Notification value) {
            addHeaders();
            return KeyValue.pair(key, value);
        }

        @Override
        public void close() {
            // Not Used
        }

        private void addHeaders() {
            context.headers().add(Headers.TYPE_HEADER, Headers.TYPE_MAP.get(Notification.class.getName()).getBytes());
        }
    }

}
