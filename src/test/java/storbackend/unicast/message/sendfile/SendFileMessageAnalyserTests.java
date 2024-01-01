package storbackend.unicast.message.sendfile;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import storbackend.sever.unicast.message.sendfile.SendFileMessageAnalyser;
import storbackend.sever.unicast.message.sendfile.SendFileMessageProperties;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class SendFileMessageAnalyserTests {

    private final SendFileMessageAnalyser analyser = new SendFileMessageAnalyser();

    @ParameterizedTest
    @ValueSource(strings = {
            "SENDFILE amsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5m 1 amsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5m\n0659af",
            "SENDFILE amsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5m 1 amsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5m\n",
            "SENDFILE amsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5md1sf5m 1523654125 amsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5m\n0659af656",
            "SENDFILE amsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5m 102547 amsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5m\n0659af",
            "SENDFILE amsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5m 1025442 amsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5m\n0659af",
    })
    public void givenCorrectMessage_WhenAnalyse_ThenMatches(final String message) {
        assertTrue(analyser.analyse(message));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "\n",
            "SENDFILE amsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5\n",
            "SENDFILE amsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5m 14785695254\n",
            "SENDFILE amsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5m 10 amsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5ms\n0659af",
            "SENDFILE amsld1sf5ma-/-++5mamsld1s!'5mamsld1sf5md1sf5m\n",
            "SENDFILE amsld1sf5mamsld1sf5mamsld1s f5mamsld1sf5mamsld1sf5md1sf5m\n",
            "SENDFILEamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5md1sf5m\n",
    })
    public void givenIncorrectMessage_WhenAnalyse_ThenNotMatches(final String message) {
        assertFalse(analyser.analyse(message));
    }

    @ParameterizedTest
    @MethodSource("provideParameters")
    public void givenCorrectMessage_WhenAnalyse_ThenCanGetProperties(
            final String message, final String expectedFilename,
            final String expectedSize, final String expectedFingerprint) {
        analyser.analyse(message);
        assertEquals(expectedFilename, analyser.get(SendFileMessageProperties.FILENAME));
        assertEquals(expectedSize, analyser.get(SendFileMessageProperties.SIZE));
        assertEquals(expectedFingerprint, analyser.get(SendFileMessageProperties.FINGERPRINT));
    }

    public static Stream<Arguments> provideParameters() {
        return Stream.of(
                Arguments.of(
                        "SENDFILE amsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5m 1025442 amsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5m\n0659af",
                        "amsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5m",
                        "1025442",
                        "amsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5m"),
                Arguments.of(
                        "SENDFILE amsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5m 1 amsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5m\n0659af",
                        "amsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5m",
                        "1",
                        "amsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5m")
        );
    }
}
