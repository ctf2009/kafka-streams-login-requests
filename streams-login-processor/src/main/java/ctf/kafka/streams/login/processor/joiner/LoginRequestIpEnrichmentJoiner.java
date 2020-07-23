package ctf.kafka.streams.login.processor.joiner;

import ctf.kafka.streams.common.model.EnrichedIpAddress;
import ctf.kafka.streams.common.model.LoginRequest;
import org.apache.kafka.streams.kstream.ValueJoiner;

public class LoginRequestIpEnrichmentJoiner implements ValueJoiner<LoginRequest, EnrichedIpAddress, LoginRequest> {

    @Override
    public LoginRequest apply(LoginRequest loginRequest, EnrichedIpAddress enrichedIpAddress) {
        if (enrichedIpAddress != null) {
            if (!enrichedIpAddress.isErrored()) {
                loginRequest.setEnriched(true);
                loginRequest.setCountry(enrichedIpAddress.getCountry());
                loginRequest.setRegion(enrichedIpAddress.getRegion());
                loginRequest.setCity(enrichedIpAddress.getCity());
                loginRequest.setType(enrichedIpAddress.getType());
            }
        }
        return loginRequest;
    }

}
