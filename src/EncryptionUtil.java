import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

public class EncryptionUtil {

    // ── FIX 3: Key is derived via SHA-256 so it's always exactly 32 bytes ──
    // (plain "BoniSecretKey123".getBytes() was only 16 — worked for
    // AES-128, breaks silently if key changes length)
    // TODO: replace with a user-derived key in future iterations
    private static final String SECRET_KEY = "BoniSecretKey123";

    private static SecretKeySpec buildKey() throws Exception {
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        byte[] raw = sha.digest(SECRET_KEY.getBytes("UTF-8"));
        return new SecretKeySpec(raw, "AES"); // 256-bit AES
    }

    // ── FIX 4: AES/CBC/PKCS5Padding with a random IV per encryption ──
    // Old code used AES/ECB (default), which is deterministic and
    // reveals duplicate passwords. IV is returned as Base64 so
    // DatabaseManager can store it in the iv column.

    /** Returns [encryptedBase64, ivBase64] */
    public static String[] encrypt(String data) {
        try {
            byte[] ivBytes = new byte[16];
            new SecureRandom().nextBytes(ivBytes);
            IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, buildKey(), ivSpec);
            byte[] encrypted = cipher.doFinal(data.getBytes("UTF-8"));

            return new String[] {
                    Base64.getEncoder().encodeToString(encrypted),
                    Base64.getEncoder().encodeToString(ivBytes)
            };
        } catch (Exception e) {
            e.printStackTrace();
            return new String[] { data, "" };
        }
    }

    /** Decrypt using the stored IV. */
    public static String decrypt(String encryptedData, String ivBase64) {
        try {
            byte[] ivBytes = Base64.getDecoder().decode(ivBase64);
            IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, buildKey(), ivSpec);
            byte[] decoded = Base64.getDecoder().decode(encryptedData);
            return new String(cipher.doFinal(decoded), "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            return encryptedData;
        }
    }

    // SHA-256 one-way hash — used for master password verification (unchanged)
    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(password.getBytes("UTF-8"));
            return Base64.getEncoder().encodeToString(hashBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return password;
        }
    }
}