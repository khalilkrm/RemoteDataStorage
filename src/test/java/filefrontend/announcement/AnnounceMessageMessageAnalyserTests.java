package filefrontend.announcement;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import utils.message.MessageAnalyser;
import utils.message.MessageAnalyserException;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class AnnounceMessageMessageAnalyserTests {

    private final MessageAnalyser messageAnalyser = new AnnouncementMessageAnalyser();

    @ParameterizedTest
    @ValueSource(strings = {
            "hello iamsbe 15401\r\n",
            "HELLO iamanothersbe 15484\r\n",
            "helLO thebestsbethere 1025\r\n",
            "helLO thebest000s 1025\r\n",
            "helLO thebest000sbethere 1025\r\n",
            "helLO 000000000000 1025\r\n",
            "helLO 01234567890123456789 1025\r\n",
            "helLO azertyuiopqsdfghjklm 1025\r\n",
            "helLO imasbe007. 000001025\r\n"})
    public void givenCorrectAnnounceMessage_WhenAnalyse_ThenMatches(final String message) {
        assertTrue(messageAnalyser.analyse(message));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "",
            "iam not an announce\r\n",
            "HELLO almost an announce\r\n",
            "HELLO domain port\r\n",
            "helLO azer_eijde_. port\r\n",
            "helLO imasbe007. port\r\n",
            "helLO imasbe007. 0\r\n",
            "helLO imasbe007. 0000\r\n",
            "helLO imasbe007. 0001024\r\n",
            "helLO imasbe007. 65536\r\n",
            "helLO imasbe007. " + Integer.MAX_VALUE + "\r\n",
            "helLO imasbe007. " + Integer.MIN_VALUE + "\r\n",
            "helLO imasbe007. 0.1025\r\n",
            "helLO imasbe007. 0,1025\r\n"
    })
    public void givenIncorrectAnnounceMessage_WhenAnalyse_ThenNotMatches(final String message) {
        assertFalse(messageAnalyser.analyse(message));
    }

    @ParameterizedTest
    @MethodSource("provideCorrectParametersToTestGetters")
    public void givenCorrectAnnounceMessage_WhenAnalyseAndGetTheDomainAndPort_ThenGotWithSuccess(
            final String message,
            final String expectedDomain,
            final int expectedPort) {
        messageAnalyser.analyse(message);
        assertEquals(expectedDomain, messageAnalyser.get(AnnouncementMessageProperties.DOMAIN));
        assertEquals(expectedPort, Integer.parseInt(messageAnalyser.get(AnnouncementMessageProperties.PORT)));
    }

    public static Stream<Arguments> provideCorrectParametersToTestGetters() {
        return Stream.of(
                Arguments.of("hello iamsbe. 15401\r\n", "iamsbe.", 15401),
                Arguments.of("HELLO iamanothersbe. 15484\r\n", "iamanothersbe.", 15484),
                Arguments.of("helLO thebestsbethere. 1025\r\n", "thebestsbethere.", 1025)
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "",
            "iam not an announce\r\n",
            "HELLO almost an announce\r\n",
            "HELLO domain port\r\n",
            "helLO azer_eijde_. port\r\n",
            "helLO imasbe007. port\r\n",
            "helLO imasbe007. 0\r\n",
            "helLO imasbe007. 0000\r\n",
            "helLO imasbe007. 0001024\r\n",
            "helLO imasbe007. 65536\r\n",
            "helLO imasbe007. " + Integer.MAX_VALUE + "\r\n",
            "helLO imasbe007. " + Integer.MIN_VALUE + "\r\n",
            "helLO imasbe007. 0.1025\r\n",
            "helLO imasbe007. 0,1025\r\n",
            "helLO thebest000s. 80\r\n"
    })
    public void givenIncorrectAnnounceMessage_WhenAnalyseAndGetTheDomainAndPort_ThenThrowException(final String message) {
        messageAnalyser.analyse(message);
        Exception iseDomain = assertThrows(MessageAnalyserException.class, () -> messageAnalyser.get(AnnouncementMessageProperties.PORT));
        Exception isePort = assertThrows(MessageAnalyserException.class, () -> Integer.parseInt(messageAnalyser.get(AnnouncementMessageProperties.PORT)));

        assertTrue(iseDomain.getMessage().contains("Run analyse first"));
        assertTrue(iseDomain.getMessage().contains("analyse failed"));
        assertTrue(isePort.getMessage().contains("Run analyse first"));
        assertTrue(isePort.getMessage().contains("analyse failed"));
    }
}
