package ctf.kafka.streams.loginviewer.processor;

import ctf.kafka.streams.common.model.LoginRequest;
import ctf.kafka.streams.loginviewer.store.LoginRequestStore;
import org.springframework.stereotype.Component;

@Component
public class LoginRequestProcessor {

    private LoginRequestStore loginRequestStore;

    public LoginRequestProcessor(final LoginRequestStore loginRequestStore) {
        this.loginRequestStore = loginRequestStore;
    }

    public void processAndStoreLoginRequest(final LoginRequest loginRequest) {
        loginRequestStore.store(loginRequest);
    }

}
