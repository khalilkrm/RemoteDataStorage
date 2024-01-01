package storbackend.unicast.message.retrieve;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import storbackend.sever.unicast.message.retrieve.RetrieveMessageAnalyser;
import storbackend.sever.unicast.message.retrieve.RetrieveMessageProperties;
import utils.message.MessageAnalyser;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class RetrieveMessageAnalyserTests {

    private final MessageAnalyser analyser = new RetrieveMessageAnalyser();

    @ParameterizedTest
    @ValueSource(strings = {
            "RETRIEVEFILE amsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5m\n",
            "RETRIEVEFILE amsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5md1sf5m\n",
            "RETRIEVEFILE amsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5m\n",
    })
    public void givenCorrectMessage_WhenAnalyse_ThenMatches(final String message) {
        assertTrue(analyser.analyse(message));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "\n",
            "RETRIEVEFILE amsld\n",
            "RETRIEVEFILE amsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5\n",
            "RETRIEVEFILE amsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5ms\n",
            "RETRIEVEFILE amsld1sf5ma-/-++5mamsld1s!'5mamsld1sf5md1sf5m\n",
            "RETRIEVEFILE amsld1sf5mamsld1sf5mamsld1s f5mamsld1sf5mamsld1sf5md1sf5m\n",
            "RETRIEVEFILEamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5md1sf5m\n",
    })
    public void givenIncorrectMessage_WhenAnalyse_ThenNotMatches(final String message) {
        assertFalse(analyser.analyse(message));
    }

    @ParameterizedTest
    @MethodSource("provideParameters")
    public void givenCorrectMessage_WhenAnalyse_ThenCanGetProperties(final String message, final String expectedFilename) {
        analyser.analyse(message);
        assertEquals(expectedFilename, analyser.get(RetrieveMessageProperties.FILENAME));
    }

    public static Stream<Arguments> provideParameters() {
        return Stream.of(
                Arguments.of(
                        "RETRIEVEFILE amsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5m\n",
                        "amsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5m"),
                Arguments.of(
                        "RETRIEVEFILE amsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5m\n",
                        "amsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5m"),
                Arguments.of(
                        "RETRIEVEFILE amsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5md1sf5m\n",
                        "amsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5mamsld1sf5md1sf5m")
        );
    }

}
