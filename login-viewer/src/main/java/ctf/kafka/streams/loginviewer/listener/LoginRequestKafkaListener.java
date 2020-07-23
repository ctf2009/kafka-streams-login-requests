package ctf.kafka.streams.loginviewer.listener;

import ctf.kafka.streams.common.kafka.Topics;
import ctf.kafka.streams.common.model.LoginRequest;
import ctf.kafka.streams.loginviewer.processor.LoginRequestProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LoginRequestKafkaListener {

    private LoginRequestProcessor loginRequestProcessor;

    public LoginRequestKafkaListener(final LoginRequestProcessor loginRequestProcessor) {
        this.loginRequestProcessor = loginRequestProcessor;
    }

    @KafkaListener(topics = Topics.LOGIN_REQUEST_HISTORY_TOPIC)
    public void listen(final ConsumerRecord<String, LoginRequest> record, final Acknowledgment ack) {
        loginRequestProcessor.processAndStoreLoginRequest(record.value());
        ack.acknowledge();
    }

}
