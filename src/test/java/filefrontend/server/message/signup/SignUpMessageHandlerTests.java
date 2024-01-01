package filefrontend.server.message.signup;

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

public class SignUpMessageHandlerTests {

    private static final String TOKEN = "SuperToken";
    private static final String LOGIN = "SuperLogin";
    private static final String ERROR_MESSAGE = "SIGN_ERROR\r\n";
    private static final String SUCCESS_MESSAGE = String.format("SIGN_OK %s\r\n", TOKEN);

    private static final SignUpMessageAnalyser ANALYSER = Mockito.mock(SignUpMessageAnalyser.class);
    private static final ClientHandler CLIENT = Mockito.mock(ClientHandler.class);

    private static final Core CORE = Mockito.mock(Core.class);
    private static final Configuration CONFIGURATION = Mockito.mock(Configuration.class);

    private static final ConfigurationProvider PROVIDER = Mockito.mock(ConfigurationProvider.class);
    private static final ClientBaseMessageHandler BASE_HANDLER = new ClientBaseMessageHandler(CORE, PROVIDER);
    private static final SignUpMessageHandler HANDLER = new SignUpMessageHandler(ANALYSER);

    @BeforeAll
    public static void setup() {
        BASE_HANDLER.setNext(HANDLER);
        Mockito.when(PROVIDER.getReadInstance()).thenReturn(CONFIGURATION);
        Mockito.when(PROVIDER.getWriteInstance()).thenReturn(CONFIGURATION);
        Mockito.when(ANALYSER.analyse(Mockito.any())).thenReturn(true);
        Mockito.doNothing().when(CLIENT).send(Mockito.any());
        Mockito.doNothing().when(CLIENT).setLogin(Mockito.any());
        Mockito.when(CONFIGURATION.generateRandomToken()).thenReturn(TOKEN);
        Mockito.when(ANALYSER.get(SignUpMessageProperties.LOGIN)).thenReturn(LOGIN);
    }

    @AfterEach
    public void reset() {
        Mockito.reset(CLIENT, CONFIGURATION, CORE);
    }

    @ParameterizedTest
    @MethodSource("provideParameters")
    public void givenSignUpMessage_WhenHandleWithGivenConditions_ThenSentMessageIsCorrect (
            final boolean isClientExists,
            final boolean isClientHasPendingConnexion,
            final boolean isClientRegisteredSuccessfully,
            final String expectedMessage) {

        Mockito.when(CONFIGURATION.isLoginExist(Mockito.any())).thenReturn(isClientExists);
        Mockito.when(CONFIGURATION.isClientHasPendingConnexion(LOGIN)).thenReturn(isClientHasPendingConnexion);
        Mockito.when(CONFIGURATION.addClient(Mockito.any())).thenReturn(isClientRegisteredSuccessfully);
        Mockito.when(CONFIGURATION.setClientPendingConnexion(LOGIN, true)).thenReturn(true);

        BASE_HANDLER.handle(Mockito.any(), CLIENT);
        Mockito.verify(CLIENT, Mockito.atLeastOnce()).send(expectedMessage);
        Mockito.verify(CLIENT, Mockito.atMostOnce()).send(expectedMessage);
        Mockito.verify(CLIENT, Mockito.never()).send(expectedMessage.equals(ERROR_MESSAGE) ? SUCCESS_MESSAGE : ERROR_MESSAGE);
    }

    private static Stream<Arguments> provideParameters() {
        return Stream.of(
                Arguments.of(false, true, true, SUCCESS_MESSAGE),
                Arguments.of(true, false, true, ERROR_MESSAGE),
                Arguments.of(true, false , false, ERROR_MESSAGE),
                Arguments.of(false, false, false, ERROR_MESSAGE)
        );
    }

}
