package moviesApp.moviesAppServer.services;

import com.sun.javafx.scene.traversal.Algorithm;
import moviesApp.utils.exceptions.MoviesAppException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class TokenService {
    private static final String SECRET = "OdinDlyaLudeiDrugoiDlyaMonstrov";
    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    public String createToken(String login) {
        String hmacMD5Algorithm = "HmacMD5";
        try {
            return hmac(hmacMD5Algorithm, login);
        } catch (Throwable exception) {
            throw new MoviesAppException("Token creation failed due to '" + exception + "'");
        }
    }

    private String hmac(String algorithm, String data)
            throws NoSuchAlgorithmException, InvalidKeyException {
        SecretKeySpec secretKeySpec = new SecretKeySpec(SECRET.getBytes(), algorithm);
        Mac mac = Mac.getInstance(algorithm);
        mac.init(secretKeySpec);
        return bytesToHex(mac.doFinal(data.getBytes()));
    }

    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }
}
