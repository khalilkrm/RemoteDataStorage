package utils.security;


import org.apache.commons.lang3.RandomStringUtils;

import java.security.SecureRandom;

public class EncryptionUtils {

    private static final int SALT_LENGTH = 12;
    private static final int INITIAL_VECTOR_LENGTH = 16;

    public static String getRandomSalt() {
        return RandomStringUtils.randomAscii(SALT_LENGTH);
    }

    public static byte[] getRandomIV() {
        final byte[] iv = new byte[INITIAL_VECTOR_LENGTH];
        new SecureRandom().nextBytes(iv);
        return iv;
    }

}
