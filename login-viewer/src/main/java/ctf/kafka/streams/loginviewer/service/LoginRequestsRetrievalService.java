package ctf.kafka.streams.loginviewer.service;

import ctf.kafka.streams.common.model.LoginRequest;
import ctf.kafka.streams.loginviewer.store.LoginRequestStore;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class LoginRequestsRetrievalService {

    private LoginRequestStore loginRequestStore;

    public LoginRequestsRetrievalService(final LoginRequestStore loginRequestStore) {
        this.loginRequestStore = loginRequestStore;
    }

    public Set<LoginRequest> getLoginRequests() {
        return loginRequestStore.getLoginRequests();
    }

}
