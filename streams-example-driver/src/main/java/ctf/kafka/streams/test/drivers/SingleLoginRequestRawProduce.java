package ctf.kafka.streams.test.drivers;

import ctf.kafka.streams.common.kafka.Topics;
import ctf.kafka.streams.common.model.LoginRequestRaw;
import ctf.kafka.streams.test.utils.KafkaUtil;
import ctf.kafka.streams.test.utils.Utils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.net.UnknownHostException;
import java.time.Instant;

public class SingleLoginRequestRawProduce {

    public static void main(String[] args) {
        try {
            final KafkaProducer<String, LoginRequestRaw> producer = KafkaUtil.getKafkaJsonProducer(new String[] {"localhost:9092"});

            final LoginRequestRaw loginRequest = LoginRequestRaw.builder()
                    .userId("testUser")
                    .status("Success")
                    .timestamp(Instant.now().toEpochMilli())
                    .ipAddress("10.0.0.1")
                    .build();

            Utils.applyHashToLoginRequest(loginRequest);

            final ProducerRecord record = new ProducerRecord<>(Topics.LOGIN_REQUEST_RAW_TOPIC, loginRequest.getHash(), loginRequest);

            producer.send(record);
            producer.close();
        } catch(UnknownHostException e) {
            e.printStackTrace();
        }
    }

}
