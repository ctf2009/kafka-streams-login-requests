package ctf.kafka.streams.loginviewer.listener;

import ctf.kafka.streams.common.kafka.Topics;
import ctf.kafka.streams.common.model.Notification;
import ctf.kafka.streams.loginviewer.processor.NotificationProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class NotificationKafkaListener {

    private NotificationProcessor notificationProcessor;

    public NotificationKafkaListener(final NotificationProcessor notificationProcessor) {
        this.notificationProcessor = notificationProcessor;
    }

    @KafkaListener(topics = Topics.NOTIFICATION_TOPIC)
    public void listen(final ConsumerRecord<String, Notification> record, final Acknowledgment ack) {
        notificationProcessor.processAndStoreNotification(record.value());
        ack.acknowledge();
    }

}
