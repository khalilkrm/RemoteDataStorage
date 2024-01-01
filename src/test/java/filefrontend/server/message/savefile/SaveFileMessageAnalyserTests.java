package filefrontend.server.message.savefile;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import utils.message.MessageAnalyserException;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SaveFileMessageAnalyserTests {

    private final SaveFileMessageAnalyser analyser = new SaveFileMessageAnalyser();

    @ParameterizedTest
    @ValueSource(strings = {
            "SAVEFILE 43wcp. 234\r\n",
            "SAVEFILE hm1txeh4pqn7x9fypgv. 1234567890\r\n",
            "SAVEFILE cu23uqx01d8tjh7c1. 2345\r\n",
            "SAVEFILE j. 34\r\n",
            "SAVEFILE j. 0\r\n",
    })
    public void givenCorrectMessage_WhenAnalyse_ThenMatches(final String message) {
        assertTrue(analyser.analyse(message));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "\n",
            "SAVEFILE \n",
            "SAVEFILE é\"_çà(ç'_.\n34 ",
            "SAVEFILE aaa2222 343\nm-!jO3VYge-'LVy(Y.",
            "SAVEFILE j. -1\n ",
    })
    public void givenIncorrectMessage_WhenAnalyse_ThenNotMatches(final String message) {
        assertFalse(analyser.analyse(message));
    }

    @ParameterizedTest
    @MethodSource("provideCorrectParametersToTestGetters")
    public void givenCorrectAnnounceMessage_WhenAnalyseAndGetTheDomainAndPort_ThenGotWithSuccess(
            final String message,
            final String expectedFilename,
            final long expectedSize) {
        analyser.analyse(message);
        assertEquals(expectedFilename, analyser.get(SaveFileMessageProperties.FILENAME));
        assertEquals(expectedSize, Long.parseLong(analyser.get(SaveFileMessageProperties.SIZE)));
    }

    public static Stream<Arguments> provideCorrectParametersToTestGetters() {
        return Stream.of(
                Arguments.of(
                        "SAVEFILE 43wcp. 234\r\n",
                        "43wcp.", 234L),
                Arguments.of("SAVEFILE hm1txeh4pqn7x9fypgv. 1234567890\n",
                        "hm1txeh4pqn7x9fypgv.", 1234567890L),
                Arguments.of("SAVEFILE j. 0\n", "j.", 0L),
                Arguments.of("SAVEFILE j. 34\r\n", "j.", 34L)
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "\n",
            "REMOVEFILE \n",
            "REMOVEFILE é\"_çà(ç'_.\n",
            "REMOVEFILE aaa2222 m-!jO3VYge-'LVy(Y.\n",
            "REMOVEFILE j.",
    })
    public void givenIncorrectAnnounceMessage_WhenAnalyseAndGetTheDomainAndPort_ThenThrowException(final String message) {
        analyser.analyse(message);
        Exception iseFilename = assertThrows(MessageAnalyserException.class, () -> analyser.get(SaveFileMessageProperties.FILENAME));
        Exception iseSize = assertThrows(MessageAnalyserException.class, () -> analyser.get(SaveFileMessageProperties.FILENAME));
        Exception iseContent = assertThrows(MessageAnalyserException.class, () -> analyser.get(SaveFileMessageProperties.FILENAME));

        assertTrue(iseFilename.getMessage().contains("Run analyse first"));
        assertTrue(iseFilename.getMessage().contains("analyse failed"));
        assertTrue(iseSize.getMessage().contains("Run analyse first"));
        assertTrue(iseSize.getMessage().contains("analyse failed"));
        assertTrue(iseContent.getMessage().contains("Run analyse first"));
        assertTrue(iseContent.getMessage().contains("analyse failed"));
    }
}
