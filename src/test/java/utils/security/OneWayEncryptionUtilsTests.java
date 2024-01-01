package utils.security;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OneWayEncryptionUtilsTests {

    private final static String LINE = "this is to encrypt";
    private final static String SALT = EncryptionUtils.getRandomSalt();

    @ParameterizedTest
    @MethodSource("provideParameters")
    public void givenLineToEncrypt_WhenEncryptTwoTime_ThenBothEncryptionShouldBeEqual(final String disclosed, final String expected) {
        final String actual = OneWayEncryptionUtils.SHA384EncryptAsHex(disclosed);
        assertEquals(expected, actual);
    }

    public static Stream<Arguments> provideParameters() {
        return Stream.of(
                Arguments.of(LINE, OneWayEncryptionUtils.SHA384EncryptAsHex(LINE), null),
                Arguments.of(LINE + SALT, OneWayEncryptionUtils.SHA384EncryptAsHex(LINE + SALT)));
    }

}
