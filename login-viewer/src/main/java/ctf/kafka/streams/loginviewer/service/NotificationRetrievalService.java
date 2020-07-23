package ctf.kafka.streams.loginviewer.service;

import ctf.kafka.streams.common.model.Notification;
import ctf.kafka.streams.loginviewer.store.NotificationStore;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class NotificationRetrievalService {

    private NotificationStore notificationStore;

    public NotificationRetrievalService(final NotificationStore notificationStore) {
        this.notificationStore = notificationStore;
    }

    public Set<Notification> getNotifications() {
        return notificationStore.getNotifications();
    }

}
