package ctf.kafka.streams.loginviewer.store;

import ctf.kafka.streams.common.model.Notification;
import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.Set;

@Component
public class NotificationStore {

    private LinkedHashSet<Notification> store;

    public NotificationStore() {
        this.store = new LinkedHashSet<>();
    }

    public synchronized void store(final Notification notification) {
        this.store.add(notification);

        if (store.size() > 50) {
            store.remove(store.iterator().next());
        }
    }

    public synchronized Set<Notification> getNotifications() {
        return new LinkedHashSet<>(this.store);
    }

}
