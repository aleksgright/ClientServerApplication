package moviesApp.moviesAppServer.services;

import moviesApp.utils.exceptions.MoviesAppException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class SaltService {
    private static final String SECRET = "AVShtanachTiDvaHeraDerjish";
    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
    private static final int HMAC_LENGTH = 32;

    public static Boolean checkPassword(String password, String hashedPassword) {
        return hashedPassword.equals(createHash(password, extractSalt(hashedPassword)));
    }

    public static String createHash(String password) {
        SecureRandom random = new SecureRandom();
        byte[] s = new byte[16];
        random.nextBytes(s);
        String salt = bytesToHex(s);
        return createHash(password, salt);
    }

    private static String createHash(String password, String salt) {
        String hmacMD5Algorithm = "HmacMD5";
        try {
            return hmac(hmacMD5Algorithm, password + salt) + salt;
        } catch (Throwable exception) {
            throw new MoviesAppException("Hash creation failed due to '" + exception + "'");
        }
    }

    private static String hmac(String algorithm, String data)
            throws NoSuchAlgorithmException, InvalidKeyException {
        SecretKeySpec secretKeySpec = new SecretKeySpec(SECRET.getBytes(), algorithm);
        Mac mac = Mac.getInstance(algorithm);
        mac.init(secretKeySpec);
        return bytesToHex(mac.doFinal(data.getBytes()));
    }

    private static String extractSalt(String hashedPassword) {
        return hashedPassword.substring(HMAC_LENGTH);
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
