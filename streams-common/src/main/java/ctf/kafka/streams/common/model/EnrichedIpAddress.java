package ctf.kafka.streams.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class EnrichedIpAddress {

    private String reference;

    private String ipAddress;

    private boolean errored;

    private String country;

    private String region;

    private String city;

    private String type;

}
