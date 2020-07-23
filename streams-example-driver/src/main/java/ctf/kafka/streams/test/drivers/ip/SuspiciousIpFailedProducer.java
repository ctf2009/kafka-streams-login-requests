package ctf.kafka.streams.test.drivers.ip;

import ctf.kafka.streams.common.kafka.Topics;
import ctf.kafka.streams.common.model.LoginRequestRaw;
import ctf.kafka.streams.test.utils.KafkaUtil;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.time.Instant;
import java.util.UUID;

public class SuspiciousIpFailedProducer {

    public static void main(String[] args) {
        try {
            final KafkaProducer<String, LoginRequestRaw> producer = KafkaUtil.getKafkaJsonProducer(new String[] {"localhost:9092"});

            for (int i=0; i<5; i++) {
                final LoginRequestRaw loginRequest = LoginRequestRaw.builder()
                        .userId("testUser - Suspicious IP")
                        .hash(UUID.randomUUID().toString())
                        .status("Failed")
                        .timestamp(Instant.now().toEpochMilli())
                        .ipAddress("10.0.0.20")
                        .build();

                producer.send(buildProducerRecord(loginRequest));
                Thread.sleep(100);
            }

            producer.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private static ProducerRecord<String, LoginRequestRaw> buildProducerRecord(final LoginRequestRaw loginRequestRaw) {
        return new ProducerRecord<>(Topics.LOGIN_REQUEST_RAW_TOPIC, loginRequestRaw.getHash(), loginRequestRaw);
    }

}
