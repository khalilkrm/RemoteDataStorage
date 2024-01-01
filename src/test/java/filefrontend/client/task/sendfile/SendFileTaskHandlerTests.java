package filefrontend.client.task.sendfile;

import filefrontend.client.StorBackEnd;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import utils.observer.EventType;
import utils.security.OneWayEncryptionUtils;
import utils.task.Task;

public class SendFileTaskHandlerTests {

    private static final String SIZE = "114";
    private static final String FILENAME = "SuperFilename";
    private static final String LOGIN = "SuperLogin";
    private static final String CONTENT = "This is a file content supposed to be hashed";
    private static final String HASH_CONTENT = OneWayEncryptionUtils.SHA384EncryptAsHex(CONTENT);

    private final static String EXPECTED_MESSAGE = String.format("SENDFILE %s %s %s\r\n",
            FILENAME, SIZE, HASH_CONTENT);

    private static final Task TASK = Mockito.mock(Task.class);
    private static final StorBackEnd STOR_BACK_END = Mockito.mock(StorBackEnd.class);

    private static final SendFileTaskHandler handler = new SendFileTaskHandler();

    @BeforeAll
    public static void setup() {
       Mockito.when(TASK.getType()).thenReturn(EventType.SendFile);
    }

    @Test
    public void givenTask_WhenHandle_ThenStorBackEndReceiveTheCorrectMessageToSend() {

        Mockito.when(STOR_BACK_END.receive()).thenReturn("");

        Mockito.when(TASK.getSavedFilename()).thenReturn(FILENAME);
        Mockito.when(TASK.getSize()).thenReturn(SIZE);
        Mockito.when(TASK.getFingerprint()).thenReturn(HASH_CONTENT);
        Mockito.when(TASK.getUserLogin()).thenReturn(LOGIN);

        handler.handle(TASK, STOR_BACK_END);
        Mockito.verify(STOR_BACK_END).send(EXPECTED_MESSAGE);
    }
}
