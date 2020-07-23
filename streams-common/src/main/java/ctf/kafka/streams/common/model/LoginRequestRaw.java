package ctf.kafka.streams.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class LoginRequestRaw {

    private String userId;

    private String status;

    private Long timestamp;

    private String ipAddress;

    private String hash;

}
