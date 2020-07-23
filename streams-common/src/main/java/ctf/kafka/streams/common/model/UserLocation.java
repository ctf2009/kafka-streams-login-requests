package ctf.kafka.streams.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserLocation {

    private String userId;

    private String country;

    private long lastLoginFromLocation;

}
