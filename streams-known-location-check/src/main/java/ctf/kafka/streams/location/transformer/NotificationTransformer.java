package ctf.kafka.streams.location.transformer;

import ctf.kafka.streams.common.kafka.Headers;
import ctf.kafka.streams.common.model.LoginRequest;
import ctf.kafka.streams.common.model.Notification;
import ctf.kafka.streams.common.model.Tuple;
import ctf.kafka.streams.common.model.UserLocation;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Transformer;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NotificationTransformer implements Transformer<String, Tuple<LoginRequest, UserLocation>, KeyValue<String, Notification>> {

    private static Logger LOG = LoggerFactory.getLogger(NotificationTransformer.class);

    private ProcessorContext context;

    @Override
    public void init(final ProcessorContext context) {
        this.context = context;
    }

    @Override
    public KeyValue<String, Notification> transform(String key, Tuple<LoginRequest, UserLocation> value) {
        final LoginRequest loginRequest = value.getValue1();

        LOG.debug("Checking LoginRequest: {} and UserLocation: {}", value.getValue1(), value.getValue2());
        addHeaders();

        final UserLocation userKnownLocation = value.getValue2();
        if (userKnownLocation == null) {
            return KeyValue.pair(loginRequest.getUserId(), Notification.builder()
                    .reference(loginRequest.getUserId())
                    .timestamp(loginRequest.getTimestamp())
                    .severity("High")
                    .message("Login detected from a new Location: " + loginRequest.getCountry())
                    .build());
        } else {
            //  Known location but over 6 months ago
            if (loginRequest.getTimestamp() - userKnownLocation.getLastLoginFromLocation() > 15552000000L) {
                return KeyValue.pair(loginRequest.getUserId(), Notification.builder()
                        .reference(loginRequest.getUserId())
                        .severity("Medium")
                        .timestamp(loginRequest.getTimestamp())
                        .message("Login detected from an old known location: " + loginRequest.getCountry()
                                + ". Last logged in from here: " + userKnownLocation.getLastLoginFromLocation())
                        .build());
            }
        }

        // Login was from a known location and was last accessed recently
        LOG.info("{} has logged in recently from this location", value.getValue1());
        return null;
    }

    @Override
    public void close() {
        // Not Used
    }

    private void addHeaders() {
        context.headers().add(Headers.TYPE_HEADER, Headers.TYPE_MAP.get(Notification.class.getName()).getBytes());
    }
}
