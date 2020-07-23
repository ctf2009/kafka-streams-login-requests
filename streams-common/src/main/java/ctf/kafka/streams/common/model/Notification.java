package ctf.kafka.streams.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Notification {

    private String reference;

    private String message;

    private String severity;

    private long timestamp;

}
