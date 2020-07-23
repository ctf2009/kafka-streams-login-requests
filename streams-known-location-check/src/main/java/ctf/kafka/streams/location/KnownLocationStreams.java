package ctf.kafka.streams.location;

import ctf.kafka.streams.common.kafka.Topics;
import ctf.kafka.streams.common.model.LoginRequest;
import ctf.kafka.streams.common.model.Notification;
import ctf.kafka.streams.common.model.Tuple;
import ctf.kafka.streams.common.model.UserLocation;
import ctf.kafka.streams.common.streams.StreamsConfiguration;
import ctf.kafka.streams.common.streams.serdes.JsonSerde;
import ctf.kafka.streams.location.transformer.NotificationTransformer;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Produced;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

import static ctf.kafka.streams.common.kafka.Topics.USER_KNOWN_LOCATIONS_TOPIC;

public class KnownLocationStreams {

    private static final Logger LOG = LoggerFactory.getLogger(KnownLocationStreams.class);

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

        final JsonSerde<UserLocation> userKnownLocationJsonSerde = new JsonSerde<>(UserLocation.class);
        final JsonSerde<LoginRequest> loginRequestSerde = new JsonSerde<>(LoginRequest.class);
        final JsonSerde<Notification> notificationJsonSerde = new JsonSerde<>(Notification.class);

        // Consume the USER_KNOWN_LOCATIONS Topic as a KTable
        final KTable<String, UserLocation> userKnownLocationKTable =
                builder.table(USER_KNOWN_LOCATIONS_TOPIC, Consumed.with(Serdes.String(), userKnownLocationJsonSerde));

        // This just lets us know when we get an update to the KTable above
        userKnownLocationKTable.toStream().foreach((key, value) -> LOG.debug("Updated to Table: " + key + " : " + value));

        // Consume the LOGIN_REQUEST_HISTORY_TOPIC
        final KStream<String, LoginRequest> loginRequests =
                builder.stream(Topics.LOGIN_REQUEST_HISTORY_TOPIC, Consumed.with(Serdes.String(), loginRequestSerde));

        // Only interested in LoginRequests that are successful and have been enriched
        final KStream<String, Tuple<LoginRequest, UserLocation>> joined = loginRequests
                .filter(((key, value) -> value.getStatus().equals("Success")))
                .filter((k,v) -> v.isEnriched())

                // For the monen, ignore LoginRequests from Australia
                .filterNot((key, value) -> value.getCountry().equalsIgnoreCase("AUSTRALIA"))
                .selectKey((k,v) -> buildKey(v))
                // Try to lookup a previous login for this user in this location
                .leftJoin(userKnownLocationKTable, Tuple::new);

        // Update the Known Locations Topic. Need to do this after the Join above to keep things in order
        joined.map((key, value) -> {
            final UserLocation userLocation = UserLocation.builder()
                    .userId(value.getValue1().getUserId())
                    .country(value.getValue1().getCountry())
                    .lastLoginFromLocation(value.getValue1().getTimestamp())
                    .build();

            return KeyValue.pair(buildKey(value.getValue1()), userLocation);
        }).to(USER_KNOWN_LOCATIONS_TOPIC, Produced.with(Serdes.String(), userKnownLocationJsonSerde));

        // Perform checking on the LoginRequest. Send any notifications out to the NOTIFICATIONS_TOPIC
        joined.transform(NotificationTransformer::new)
                .peek(((key, value) -> LOG.info("Producing Notification: {}", value)))
                .to(Topics.NOTIFICATION_TOPIC, Produced.with(Serdes.String(), notificationJsonSerde));
    }

    private static String buildKey(final LoginRequest loginRequest) {
        return loginRequest.getUserId() + "#" + loginRequest.getCountry();
    }

}
