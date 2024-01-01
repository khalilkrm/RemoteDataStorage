package storbackend.unicast.message.erase;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import storbackend.sever.unicast.message.erase.EraseMessageAnalyser;
import storbackend.sever.unicast.message.erase.EraseMessageProperties;
import utils.message.MessageAnalyser;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class EraseMessageAnalyserTests {

    private final MessageAnalyser analyser = new EraseMessageAnalyser();

    @ParameterizedTest
    @ValueSource(strings = {
            "ERASEFILE amsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5m\n",
            "ERASEFILE amsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5md1sf5m\n",
            "ERASEFILE amsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5m\n",
    })
    public void givenCorrectMessage_WhenAnalyse_ThenMatches(final String message) {
        assertTrue(analyser.analyse(message));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "\n",
            "ERASEFILE amsld\n",
            "ERASEFILE amsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5\n",
            "ERASEFILE amsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5ms\n",
            "ERASEFILE amsld1sf5ma-/-++5mamsld1s!'5mamsld1sf5md1sf5m\n",
            "ERASEFILE amsld1sf5mamsld1sf5mamsld1s f5mamsld1sf5mamsld1sf5md1sf5m\n",
            "ERASEFILEamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5md1sf5m\n",
    })
    public void givenIncorrectMessage_WhenAnalyse_ThenNotMatches(final String message) {
        assertFalse(analyser.analyse(message));
    }

    @ParameterizedTest
    @MethodSource("provideParameters")
    public void givenCorrectMessage_WhenAnalyse_ThenCanGetProperties(final String message, final String expectedFilename) {
        analyser.analyse(message);
        assertEquals(expectedFilename, analyser.get(EraseMessageProperties.FILENAME));
    }

    public static Stream<Arguments> provideParameters() {
        return Stream.of(
                Arguments.of(
                        "ERASEFILE amsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5m\n",
                        "amsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5m"
                ),
                Arguments.of(
                        "ERASEFILE amsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5m\n",
                        "amsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5m"
                ),
                Arguments.of(
                        "ERASEFILE amsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5md1sf5m\n",
                        "amsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5md1sf5m")
        );
    }
}
