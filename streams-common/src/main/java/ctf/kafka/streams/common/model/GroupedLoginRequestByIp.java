package ctf.kafka.streams.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class GroupedLoginRequestByIp {

    private String ipAddress;

    final private List<LoginRequestRaw> failedRequests = new ArrayList<>();

    final private List<LoginRequestRaw> successfulRequests = new ArrayList<>();

}
