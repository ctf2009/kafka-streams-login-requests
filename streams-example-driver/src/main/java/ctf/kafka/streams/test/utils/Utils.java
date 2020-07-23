package ctf.kafka.streams.test.utils;

import ctf.kafka.streams.common.model.LoginRequestRaw;
import org.apache.commons.codec.binary.Hex;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Utils {

    public static void applyHashToLoginRequest(final LoginRequestRaw loginRequestRaw) {
        try {
            final MessageDigest md5 = MessageDigest.getInstance("md5");

            final String hash = Hex.encodeHexString(md5.digest((loginRequestRaw.getUserId() +
                    loginRequestRaw.getStatus() +
                    loginRequestRaw.getTimestamp() +
                    loginRequestRaw.getIpAddress())
                    .getBytes()));

            loginRequestRaw.setHash(hash);
        } catch (NoSuchAlgorithmException e) {
            // No MD5?ß
        }
    }
}
