package filefrontend.server.message.removefile;

import filefrontend.Core;
import filefrontend.configuration.ConfigurationProvider;
import filefrontend.server.ClientHandler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import filefrontend.configuration.Configuration;
import utils.message.ClientBaseMessageHandler;
import utils.observer.EventType;
import utils.security.OneWayEncryptionUtils;
import utils.task.Task;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RemoveFileMessageHandlerTests {

    private static final String FILENAME = "SuperFilename";
    private static final String SBE_ID = "SuperSbe";
    private static final String LOGIN = "SuperLogin";

    private static final RemoveFileMessageAnalyser ANALYSER = Mockito.mock(RemoveFileMessageAnalyser.class);
    private static final ClientHandler CLIENT = Mockito.mock(ClientHandler.class);

    private static final Core CORE = Mockito.mock(Core.class);
    private static final Configuration CONFIGURATION = Mockito.mock(Configuration.class);

    private static final ConfigurationProvider PROVIDER = Mockito.mock(ConfigurationProvider.class);
    private static final ClientBaseMessageHandler BASE_HANDLER = new ClientBaseMessageHandler(CORE, PROVIDER);
    private static final RemoveFileMessageHandler HANDLER = new RemoveFileMessageHandler(ANALYSER);

    private static String ENCRYPTED_FILENAME;

    @BeforeAll
    public static void setup() {
        BASE_HANDLER.setNext(HANDLER);

        Mockito.when(PROVIDER.getReadInstance()).thenReturn(CONFIGURATION);
        Mockito.when(PROVIDER.getWriteInstance()).thenReturn(CONFIGURATION);
        ENCRYPTED_FILENAME = OneWayEncryptionUtils.SHA384EncryptAsHex(LOGIN + FILENAME);

        Mockito.when(ANALYSER.analyse(Mockito.any())).thenReturn(true);
        Mockito.when(CLIENT.getLogin()).thenReturn(LOGIN);
        Mockito.doNothing().when(CLIENT).send(Mockito.any());
    }

    @AfterEach
    public void reset() {
        Mockito.reset(CLIENT, CONFIGURATION, CORE);
    }

    @ParameterizedTest
    @MethodSource("provideParameters")
    public void givenRemoveFileMessage_WhenHandleWithGivenConditions_ThenTaskIsCreatedWithCorrectValues (
            final String filename,
            final String expectedFilename,
            final String expectedSbeId) {

        ArgumentCaptor<Task> argument = ArgumentCaptor.forClass(Task.class);
        Mockito.doNothing().when(CORE).pushTask(argument.capture());

        // GIVEN
        Mockito.when(ANALYSER.get(RemoveFileMessageProperties.FILENAME)).thenReturn(filename);
        Mockito.when(CONFIGURATION.getFilesHolderFromClient(Mockito.any(), Mockito.any())).thenReturn(SBE_ID);
        Mockito.when(CORE.isClientConnected(Mockito.any())).thenReturn(true);

        // WHEN
        BASE_HANDLER.handle(Mockito.any(), CLIENT);

        // THEN
        assertEquals(expectedFilename, argument.getValue().getSavedFilename());
        assertEquals(expectedSbeId, argument.getValue().getSbeId());
        assertEquals(EventType.EraseFile, argument.getValue().getType());
    }

    private static Stream<Arguments> provideParameters() {
        return Stream.of(Arguments.of(FILENAME, ENCRYPTED_FILENAME, SBE_ID));
    }
}