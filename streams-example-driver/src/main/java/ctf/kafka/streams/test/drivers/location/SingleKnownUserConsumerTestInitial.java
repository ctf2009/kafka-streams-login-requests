package ctf.kafka.streams.test.drivers.location;

import ctf.kafka.streams.common.kafka.Topics;
import ctf.kafka.streams.common.model.LoginRequestRaw;
import ctf.kafka.streams.test.utils.KafkaUtil;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.net.UnknownHostException;
import java.time.Instant;
import java.util.UUID;

public class SingleKnownUserConsumerTestInitial {

    // 10.0.0.200 = Canada  | British Columbia      | Vancouver
    // 10.0.0.201 = England | Greater Manchester    | Manchester
    // 10.0.0.202 = USA     | California            | Las Vegas

    public static void main(String[] args) {
        try {
            final KafkaProducer<String, LoginRequestRaw> producer = KafkaUtil.getKafkaJsonProducer(new String[] {"localhost:9092"});

            final LoginRequestRaw loginRequest = LoginRequestRaw.builder()
                    .userId("testUser2")
                    .status("Success")
                    .timestamp(Instant.now().toEpochMilli())
                    .ipAddress("10.0.0.200")
                    .build();

            loginRequest.setHash(UUID.randomUUID().toString());

            final ProducerRecord record = new ProducerRecord<>(Topics.LOGIN_REQUEST_RAW_TOPIC, loginRequest.getHash(), loginRequest);

            producer.send(record);
            producer.close();
        } catch(UnknownHostException e) {
            e.printStackTrace();
        }
    }

}
