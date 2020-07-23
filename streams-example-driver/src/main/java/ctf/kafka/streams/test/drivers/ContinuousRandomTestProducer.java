package ctf.kafka.streams.test.drivers;

import ctf.kafka.streams.common.kafka.Topics;
import ctf.kafka.streams.common.model.LoginRequestRaw;
import ctf.kafka.streams.test.utils.KafkaUtil;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class ContinuousRandomTestProducer {

    private static final int MAX_RANDOM_USERS = 1000;
    private static final SecureRandom RANDOM = new SecureRandom();

    public static void main(String[] args) {
        try {
            final KafkaProducer<String, LoginRequestRaw> producer = KafkaUtil.getKafkaJsonProducer(new String[] {"localhost:9092"});
            for(int i=0; i < 2000000; i++) {
                try {
                    final LoginRequestRaw request = buildLoginRequest();
                    sendLoginRequestToKafka(request, producer);

                    Thread.sleep(1000);
                    if (i % 100 == 0) {
                        System.out.println("Completed: " + i);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void sendLoginRequestToKafka(final LoginRequestRaw loginRequest, final KafkaProducer<String, LoginRequestRaw> producer) throws ExecutionException, InterruptedException {
        final ProducerRecord<String, LoginRequestRaw> record = new ProducerRecord<>(Topics.LOGIN_REQUEST_RAW_TOPIC, loginRequest.getHash(), loginRequest);
        producer.send(record).get();
    }

    private static LoginRequestRaw buildLoginRequest() {
        LoginRequestRaw loginRequestRaw = LoginRequestRaw.builder()
                .userId("testUser" + RANDOM.nextInt(MAX_RANDOM_USERS))
                .status(RANDOM.nextBoolean() ? "Success" : "Failed")
                .timestamp(Instant.now().toEpochMilli() + RANDOM.nextInt(5000))
                .ipAddress("10.0.0." + RANDOM.nextInt(200))
                .build();

        loginRequestRaw.setHash(UUID.randomUUID().toString());
        return loginRequestRaw;
    }

}
