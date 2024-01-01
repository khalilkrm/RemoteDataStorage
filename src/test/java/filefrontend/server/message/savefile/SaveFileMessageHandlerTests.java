package filefrontend.server.message.savefile;

import filefrontend.Core;
import filefrontend.configuration.ConfigurationProvider;
import filefrontend.server.ClientHandler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import filefrontend.repository.SavedState;
import filefrontend.configuration.Configuration;
import utils.message.ClientBaseMessageHandler;
import utils.observer.EventType;
import utils.security.EncryptionUtils;
import utils.security.OneWayEncryptionUtils;
import utils.security.TwoWayEncryptionUtils;
import utils.task.Task;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SaveFileMessageHandlerTests {

    private static final String SIZE = "114";
    private static final String FILENAME = "SuperFilename";
    private static final String LOGIN = "SuperLogin";
    private static final String CONTENT = "This is a file content supposed to be hashed";
    private static final byte[] IV = EncryptionUtils.getRandomIV();

    private static final String ERROR_MESSAGE = "SEND_ERROR\r\n";
    private static final String SUCCESS_MESSAGE = "SEND_OK\r\n";

    private static final SaveFileMessageAnalyser ANALYSER = Mockito.mock(SaveFileMessageAnalyser.class);
    private static final ClientHandler CLIENT = Mockito.mock(ClientHandler.class);

    private static final Core CORE = Mockito.mock(Core.class);
    private static final Configuration CONFIGURATION = Mockito.mock(Configuration.class);

    private static final ConfigurationProvider PROVIDER = Mockito.mock(ConfigurationProvider.class);
    private static final ClientBaseMessageHandler BASE_HANDLER = new ClientBaseMessageHandler(CORE, PROVIDER);
    private static final SaveFileMessageHandler HANDLER = new SaveFileMessageHandler(ANALYSER);

    private static String ENCRYPTED_CONTENT;
    private static String ENCRYPTED_FILENAME;
    private static String HASHED_CONTENT;
    private static String STRING_SECRET_KEY;

    @BeforeAll
    public static void setup() throws Exception {
        BASE_HANDLER.setNext(HANDLER);

        Mockito.when(PROVIDER.getReadInstance()).thenReturn(CONFIGURATION);
        Mockito.when(PROVIDER.getWriteInstance()).thenReturn(CONFIGURATION);
        final SecretKey key = TwoWayEncryptionUtils.getRandomAesKey();
        STRING_SECRET_KEY = TwoWayEncryptionUtils.secretKeyToString(key);

        ENCRYPTED_CONTENT = new String(TwoWayEncryptionUtils.encrypt(
                CONTENT.getBytes(StandardCharsets.UTF_8), TwoWayEncryptionUtils.getSecretKeyFromString(STRING_SECRET_KEY) , IV));

        ENCRYPTED_FILENAME = OneWayEncryptionUtils.SHA384EncryptAsHex(FILENAME);

        HASHED_CONTENT = OneWayEncryptionUtils.SHA384EncryptAsHex(ENCRYPTED_CONTENT);

        Mockito.when(CONFIGURATION.getClientAes(LOGIN)).thenReturn(STRING_SECRET_KEY);
        Mockito.when(ANALYSER.analyse(Mockito.any())).thenReturn(true);
        Mockito.doNothing().when(CLIENT).send(Mockito.any());
    }

    @AfterEach
    public void reset() {
        Mockito.reset(CLIENT, CONFIGURATION, CORE);
    }

    @Test
    public void givenSaveFileMessage_WhenHandleWithGivenConditions_ThenTaskIsCreatedWithCorrectValues () {
        ArgumentCaptor<Task> argument = ArgumentCaptor.forClass(Task.class);
        Mockito.doNothing().when(CORE).pushTask(argument.capture());
        final SecretKey key = TwoWayEncryptionUtils.getSecretKeyFromString(STRING_SECRET_KEY);

        // GIVEN
        Mockito.when(CLIENT.getLogin()).thenReturn(LOGIN);
        Mockito.when(ANALYSER.get(SaveFileMessageProperties.FILENAME)).thenReturn(FILENAME);
        Mockito.when(ANALYSER.get(SaveFileMessageProperties.SIZE)).thenReturn(SIZE);
        Mockito.when(CONFIGURATION.getRandomIV()).thenReturn(IV);
        Mockito.when(CONFIGURATION.stringToSecretKey(Mockito.any())).thenReturn(key);
        Mockito.when(CLIENT.saveFile(FILENAME, Long.parseLong(SIZE), key, IV)).thenReturn(new SavedState(true, ENCRYPTED_FILENAME, ""));
        Mockito.when(CORE.isClientConnected(Mockito.any())).thenReturn(true);

        // WHEN
        BASE_HANDLER.handle(Mockito.any(), CLIENT);

        // THEN
        assertEquals(EventType.SendFile, argument.getValue().getType());
        assertEquals(CLIENT.getLogin(), argument.getValue().getUserLogin());
        assertEquals(ENCRYPTED_FILENAME, argument.getValue().getSavedFilename());
        assertEquals("", argument.getValue().getFingerprint());
        assertEquals(SIZE, argument.getValue().getSize());
    }
}
