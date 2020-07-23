package ctf.kafka.streams.login.processor.mapper;

import ctf.kafka.streams.common.model.LoginRequest;
import ctf.kafka.streams.common.model.LoginRequestRaw;
import org.apache.kafka.streams.kstream.ValueMapper;


public class LoginRequestMapper implements ValueMapper<LoginRequestRaw, LoginRequest> {

    @Override
    public LoginRequest apply(LoginRequestRaw value) {
        return LoginRequest.builder()
                .userId(value.getUserId())
                .status(value.getStatus())
                .timestamp(value.getTimestamp())
                .ipAddress(value.getIpAddress())
                .hash(value.getHash())
                .build();
    }
}
