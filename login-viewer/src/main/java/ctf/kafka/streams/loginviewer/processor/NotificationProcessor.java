package ctf.kafka.streams.loginviewer.processor;

import ctf.kafka.streams.common.model.Notification;
import ctf.kafka.streams.loginviewer.store.NotificationStore;
import org.springframework.stereotype.Component;

@Component
public class NotificationProcessor {

    private final NotificationStore notificationStore;

    public NotificationProcessor(final NotificationStore notificationStore) {
        this.notificationStore = notificationStore;
    }

    public void processAndStoreNotification(final Notification notification) {
        this.notificationStore.store(notification);
    }
}
