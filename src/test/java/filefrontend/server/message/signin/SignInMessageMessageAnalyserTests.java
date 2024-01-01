package filefrontend.server.message.signin;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import utils.message.MessageAnalyserException;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SignInMessageMessageAnalyserTests {

    private final SignInMessageAnalyser analyser = new SignInMessageAnalyser();

    @ParameterizedTest
    @ValueSource(strings = {
            "SIGNIN aag4e0m5 {w\"idX(m-jO3VYge-'LVy(YbRl/,{o7o9xWB%\r\n",
            "SIGNIN 2dr9vzr5uhhgt4ckro1 JW_$JVsU-v#W5Pf\r\n",
            "signin 871lu 1Yv5\\7{pxmn(*7~n23f$X= {[mD2~Xg\\f\r\n",
            "SIGnIN 0i79e8dnx4it %'p/g',.xhBZzFpj07M'NZ\r\n",
            "SIGNIN 0I79E8dnX4it %'p/g',.xhBZzFpj07M'NZ\r\n",
    })
    public void givenCorrectMessage_WhenAnalyse_ThenMatches(final String message) {
        assertTrue(analyser.analyse(message));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "\n",
            "SIGNIN\n",
            "SIGNIN é\"_çà(ç'_\n",
            "SIGNIN aaa2222 m-!jO3VYge-'LVy(Y!\n",
            "SIGNIN aaa2222 m-!jO3VYge-'LVy(Y\n",
    })
    public void givenIncorrectMessage_WhenAnalyse_ThenNotMatches(final String message) {
        assertFalse(analyser.analyse(message));
    }

    @ParameterizedTest
    @MethodSource("provideCorrectParametersToTestGetters")
    public void givenCorrectMessage_WhenAnalyseAndGetTheDomainAndPort_ThenGotWithSuccess(
            final String message,
            final String expectedLogin,
            final String expectedPassword) {
        analyser.analyse(message);
        assertEquals(expectedLogin, analyser.get(SignInMessageProperties.LOGIN));
        assertEquals(expectedPassword, analyser.get(SignInMessageProperties.PASSWORD));
    }

    public static Stream<Arguments> provideCorrectParametersToTestGetters() {
        return Stream.of(
                Arguments.of("SIGNIN aag4e0m5 {w\"idX(m-jO3VYge-'LVy(YbRl/,{o7o9xWB%\r\n", "aag4e0m5", "{w\"idX(m-jO3VYge-'LVy(YbRl/,{o7o9xWB%"),
                Arguments.of("SIGNIN 2dr9vzr5uhhgt4ckro1 JW_$JVsU-v#W5Pf\r\n", "2dr9vzr5uhhgt4ckro1", "JW_$JVsU-v#W5Pf"),
                Arguments.of("SIGNIN 871lu 1Yv5\\7{pxmn(*7~n23f$X= {[mD2~Xg\\f\r\n", "871lu", "1Yv5\\7{pxmn(*7~n23f$X= {[mD2~Xg\\f")
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "\n",
            "SIGNIN\n",
            "SIGNIN é\"_çà(ç'_\n",
            "SIGNIN aaa2222 m-!jO3VYge-'LVy(Y!\n",
            "SIGNIN aaa2222 m-!jO3VYge-'LVy(Y\n",
    })
    public void givenIncorrectMessage_WhenAnalyseAndGetTheDomainAndPort_ThenThrowException(final String message) {
        analyser.analyse(message);
        Exception iseLogin = assertThrows(MessageAnalyserException.class, () -> analyser.get(SignInMessageProperties.LOGIN));
        Exception isePassword = assertThrows(MessageAnalyserException.class, () -> analyser.get(SignInMessageProperties.PASSWORD));

        assertTrue(iseLogin.getMessage().contains("Run analyse first"));
        assertTrue(iseLogin.getMessage().contains("analyse failed"));
        assertTrue(isePassword.getMessage().contains("Run analyse first"));
        assertTrue(isePassword.getMessage().contains("analyse failed"));
    }
}