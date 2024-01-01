package filefrontend.server.message.signin;

import filefrontend.Core;
import filefrontend.configuration.ConfigurationProvider;
import filefrontend.server.ClientHandler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import filefrontend.configuration.Configuration;
import utils.message.ClientBaseMessageHandler;

import java.util.stream.Stream;

public class SignInMessageHandlerTests {

    private static final String ERROR_MESSAGE = "SIGN_ERROR\r\n";
    private static final String SUCCESS_MESSAGE = "SIGN_OK\r\n";

    private static final SignInMessageAnalyser ANALYSER = Mockito.mock(SignInMessageAnalyser.class);
    private static final ClientHandler CLIENT = Mockito.mock(ClientHandler.class);

    private static final Core CORE = Mockito.mock(Core.class);
    private static final Configuration CONFIGURATION = Mockito.mock(Configuration.class);

    private static final ConfigurationProvider PROVIDER = Mockito.mock(ConfigurationProvider.class);
    private static final ClientBaseMessageHandler BASE_HANDLER = new ClientBaseMessageHandler(CORE, PROVIDER);
    private static final SignInMessageHandler HANDLER = new SignInMessageHandler(ANALYSER);

    @BeforeAll
    public static void setup() {
        BASE_HANDLER.setNext(HANDLER);
        Mockito.when(PROVIDER.getReadInstance()).thenReturn(CONFIGURATION);
        Mockito.when(PROVIDER.getWriteInstance()).thenReturn(CONFIGURATION);
        Mockito.when(ANALYSER.analyse(Mockito.any())).thenReturn(true);
        Mockito.doNothing().when(CLIENT).send(Mockito.any());
        Mockito.doNothing().when(CLIENT).setLogin(Mockito.any());
    }

    @AfterEach
    public void reset() {
        Mockito.reset(CLIENT, CONFIGURATION, CORE);
    }

    @ParameterizedTest
    @MethodSource("provideParameters")
    public void givenSignInMessage_WhenHandleWithGivenConditions_ThenSentMessageIsCorrect (
            final boolean isClientAuthenticatedSuccessfully, final boolean signInState,
            final String expectedMessage) {

        Mockito.when(ANALYSER.get(SignInMessageProperties.LOGIN)).thenReturn("LOGIN");
        Mockito.when(ANALYSER.get(SignInMessageProperties.PASSWORD)).thenReturn("PASSWORD");
        Mockito.when(CONFIGURATION.authenticateClient("LOGIN", "PASSWORD")).thenReturn(isClientAuthenticatedSuccessfully);
        Mockito.when(CONFIGURATION.setClientPendingConnexion("LOGIN", true)).thenReturn(signInState);

        BASE_HANDLER.handle(Mockito.any(), CLIENT);
        Mockito.verify(CLIENT, Mockito.atLeastOnce()).send(expectedMessage);
        Mockito.verify(CLIENT, Mockito.atMostOnce()).send(expectedMessage);
        Mockito.verify(CLIENT, Mockito.never()).send(expectedMessage.equals(ERROR_MESSAGE) ? SUCCESS_MESSAGE : ERROR_MESSAGE);
    }

    private static Stream<Arguments> provideParameters() {
        return Stream.of(
                Arguments.of(false, false,ERROR_MESSAGE),
                Arguments.of(true, false,ERROR_MESSAGE),
                Arguments.of(false, true, ERROR_MESSAGE)
        );
    }
}
