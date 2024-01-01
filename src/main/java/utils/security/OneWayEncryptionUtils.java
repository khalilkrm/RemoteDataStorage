package utils.security;

import org.apache.commons.codec.digest.DigestUtils;

import static org.apache.commons.codec.digest.MessageDigestAlgorithms.SHA_384;

public class OneWayEncryptionUtils {

    private static final DigestUtils digestUtils = new DigestUtils(SHA_384);

    public static String SHA384EncryptAsHex(final String line) {
        return digestUtils.digestAsHex(line);
    }

    public static String SHA384EncryptAsHex(final byte[] line) {
        return digestUtils.digestAsHex(line);
    }

    public static byte[] SHA384Encrypt(final byte[] line) {
        return digestUtils.digest(line);
    }
}
