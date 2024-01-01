package utils.security;

import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;

public class Encryptor {

    public SecretKey getRandomAesKey() throws NoSuchAlgorithmException {
        return TwoWayEncryptionUtils.getRandomAesKey();
    }

    public SecretKey getSecretKeyFromString(final String secretKeyAsString) {
        return TwoWayEncryptionUtils.getSecretKeyFromString(secretKeyAsString);
    }

    public String secretKeyToString(final SecretKey secretKey) {
        return TwoWayEncryptionUtils.secretKeyToString(secretKey);
    }


    public byte[] encrypt(final byte[] plain, final SecretKey key, final byte[] initialVector) throws Exception {
        return TwoWayEncryptionUtils.encrypt(plain, key, initialVector);
    }

    public byte[] decrypt(final byte[] encrypted, final SecretKey key, final byte[] initialVector) throws Exception {
        return TwoWayEncryptionUtils.decrypt(encrypted, key, initialVector);
    }

    public String SHA384EncryptAsHex(final String line) {
        return OneWayEncryptionUtils.SHA384EncryptAsHex(line);
    }

    public String SHA384EncryptAsHex(final byte[] line) {
        return OneWayEncryptionUtils.SHA384EncryptAsHex(line);
    }

    public byte[] SHA384Encrypt(final byte[] line) {
        return OneWayEncryptionUtils.SHA384Encrypt(line);
    }

}
