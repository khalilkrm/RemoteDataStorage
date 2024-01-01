package utils.security;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TwoWayEncryptionUtilsTests {

    private static final String MESSAGE = "this is a message to be encrypted";
    private static String SECRET_KEY;

    @BeforeAll
    private static void setup() {
        try {
            SECRET_KEY = TwoWayEncryptionUtils.secretKeyToString(TwoWayEncryptionUtils.getRandomAesKey());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    @ParameterizedTest
    @MethodSource("provideParameters")
    public void givenPlainMessage_WhenEncrypt_ThenMessageEncrypted(
            final byte[] plain, final String keyAsString, byte[] initialVector) {

        final SecretKey key = TwoWayEncryptionUtils.getSecretKeyFromString(keyAsString);

        try {
            final byte[] encrypted = TwoWayEncryptionUtils.encrypt(plain, key, initialVector);

            final byte[] decrypted = TwoWayEncryptionUtils.decrypt(encrypted, key, initialVector);

            assertArrayEquals(plain, decrypted);
        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail();
        }
    }

    public static Stream<Arguments> provideParameters() {
        return Stream.of(
                Arguments.of(
                        MESSAGE.getBytes(StandardCharsets.UTF_8),
                        SECRET_KEY,
                        EncryptionUtils.getRandomIV())
        );
    }
}
