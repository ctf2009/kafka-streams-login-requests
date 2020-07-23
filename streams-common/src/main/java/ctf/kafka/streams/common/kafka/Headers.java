package ctf.kafka.streams.common.kafka;

import ctf.kafka.streams.common.model.LoginRequest;
import ctf.kafka.streams.common.model.Notification;

import java.util.HashMap;
import java.util.Map;

public class Headers {

    public static String TYPE_HEADER = "__TypeId__";

    public static final Map<String, String> TYPE_MAP = new HashMap<>() {
        {
            put(LoginRequest.class.getName(), "loginRequest");
            put(Notification.class.getName(), "notification");
        }
    };

}