package filefrontend.server.message.getfile;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import utils.message.MessageAnalyserException;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GetFileMessageMessageAnalyserTests {

    private final GetFileMessageAnalyser analyser = new GetFileMessageAnalyser();

    @ParameterizedTest
    @ValueSource(strings = {
            "GETFILE 43wcp.\r\n",
            "GETFILE hm1txeh4pqn7x9fypgv.\r\n",
            "GETFILE cu23uqx01d8tjh7c1.\r\n",
            "GETFILE j.\r\n",
    })
    public void givenCorrectMessage_WhenAnalyse_ThenMatches(final String message) {
        assertTrue(analyser.analyse(message));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "\n",
            "GETFILE \n",
            "GETFILE aaa2222 m-!jO3VYge-'LVy(Y.\n",
    })
    public void givenIncorrectMessage_WhenAnalyse_ThenNotMatches(final String message) {
        assertFalse(analyser.analyse(message));
    }

    @ParameterizedTest
    @MethodSource("provideCorrectParametersToTestGetters")
    public void givenCorrectMessage_WhenAnalyseAndGetTheDomainAndPort_ThenGotWithSuccess(
            final String message,
            final String expectedFilename) {
        analyser.analyse(message);
        assertEquals(expectedFilename, analyser.get(GetFileMessageProperties.FILENAME));
    }

    public static Stream<Arguments> provideCorrectParametersToTestGetters() {
        return Stream.of(
                Arguments.of("GETFILE 43wcp.\r\n", "43wcp."),
                Arguments.of("GETFILE hm1txeh4pqn7x9fypgv.\r\n", "hm1txeh4pqn7x9fypgv."),
                Arguments.of("GETFILE cu23uqx01d8tjh7c1.\r\n", "cu23uqx01d8tjh7c1."),
                Arguments.of("GETFILE j.\r\n", "j.")
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "\n",
            "GETFILE \n",
            "GETFILE aaa2222 m-!jO3VYge-'LVy(Y.\n",
    })
    public void givenIncorrectMessage_WhenAnalyseAndGetTheDomainAndPort_ThenThrowException(final String message) {
        analyser.analyse(message);
        Exception iseFilename = assertThrows(MessageAnalyserException.class, () -> analyser.get(GetFileMessageProperties.FILENAME));

        assertTrue(iseFilename.getMessage().contains("Run analyse first"));
        assertTrue(iseFilename.getMessage().contains("analyse failed"));
    }
}
