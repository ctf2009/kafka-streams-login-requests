package ctf.kafka.streams.loginviewer.store;

import ctf.kafka.streams.common.model.LoginRequest;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.TreeSet;

@Component
public class LoginRequestStore {

    private TreeSet<LoginRequest> store;

    public LoginRequestStore() {
        this.store = new TreeSet<>((r1, r2) -> {
            if (r1.getHash().equals(r2.getHash())) {
                return 0;
            }
            return r1.getTimestamp() <= r2.getTimestamp() ? 1 : -1;
        });

//        this.store = new TreeSet<>((r1, r2) -> {
//            int result = r1.getTimestamp().compareTo(r2.getTimestamp());
//            if (result != 0) {
//                return result;
//            }
//            return (r1.getHash().compareTo(r2.getHash()));
//        });

    }

    public synchronized void store(final LoginRequest loginRequest) {
        store.remove(loginRequest);
        store.add(loginRequest);

        while(store.size() > 200) {
            store.remove(store.last());
        }
    }

    public synchronized Set<LoginRequest> getLoginRequests() {
        return new TreeSet<>(store);
    }

}
