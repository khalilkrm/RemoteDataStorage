package utils.security;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * @see <a href="https://mkyong.com/java/java-aes-encryption-and-decryption/">Java AES encryption and decryption by Mkyong</a>
 *
 * Encrypt and decrypt using AES algorithm
 * */
public class TwoWayEncryptionUtils {

    private static final String ALGORITHM_ENCRYPTION = "AES/GCM/NoPadding";
    private static final String ALGORITHM = "AES";

    private static final int GCM_AUTH_TAG_LENGTH = 128;

    public static SecretKey getRandomAesKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
        keyGen.init(256, SecureRandom.getInstanceStrong());
        return keyGen.generateKey();
    }

    public static SecretKey getSecretKeyFromString(final String secretKeyAsString) {
        final byte[] decodedKey = Base64.getDecoder().decode(secretKeyAsString);
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
    }

    public static String secretKeyToString(final SecretKey secretKey) {
        return Base64.getEncoder().encodeToString(secretKey.getEncoded());
    }

    private static byte[] doEncrypt(final byte[] plain, final SecretKey key, final byte[] initialVector) throws Exception {
        final Cipher cipher = Cipher.getInstance(ALGORITHM_ENCRYPTION);
        cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(GCM_AUTH_TAG_LENGTH, initialVector));
        return cipher.doFinal(plain);
    }

    public static byte[] encrypt(final byte[] plain, final SecretKey key, final byte[] initialVector) throws Exception {

        final byte[] cipherText = doEncrypt(plain, key, initialVector);

        return ByteBuffer.allocate(initialVector.length + cipherText.length)
                .put(initialVector)
                .put(cipherText)
                .array();
    }

    private static byte[] doDecrypt(final byte[] encrypted, final SecretKey key, final byte[] initialVector) throws Exception {
        final Cipher cipher = Cipher.getInstance(ALGORITHM_ENCRYPTION);
        cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(GCM_AUTH_TAG_LENGTH, initialVector));
        final byte[] plainText = cipher.doFinal(encrypted);
        return plainText;
    }

    public static byte[] decrypt(final byte[] encrypted, final SecretKey key, final byte[] initialVector) throws Exception {

        final ByteBuffer buffer = ByteBuffer.wrap(encrypted);

        final byte[] initialVectorFromBuffer = new byte[initialVector.length];
        buffer.get(initialVectorFromBuffer);

        final byte[] cipherText = new byte[buffer.remaining()];
        buffer.get(cipherText);

        return doDecrypt(cipherText, key, initialVector);
    }
}
