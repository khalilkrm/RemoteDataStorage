package filefrontend.server.message.filelist;

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

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class FileListMessageHandlerTests {

    private static final Long SIZE = 4L;

    private static final String LOGIN = "LOGIN";
    private static final String FILE_1 = "FILE-1";
    private static final String FILE_2 = "FILE-2";
    private static final String FILE_3 = "FILE-3";
    private static final String FILE_4 = "FILE-4";
    private static final String FILE_5 = "FILE-5";

    private static final List<String> FILES_NAME = List.of(
            FILE_1,
            FILE_2,
            FILE_3,
            FILE_4,
            FILE_5
    );

    private static final String EMPTY_FILES_MESSAGE = "FILES\r\n";

    private static final String FILES_MESSAGE = String.format("FILES %s!%d %s!%d %s!%d %s!%d %s!%d\r\n",
            FILE_1, SIZE, FILE_2, SIZE, FILE_3, SIZE, FILE_4, SIZE, FILE_5, SIZE);

    private static final FileListMessageAnalyser ANALYSER = Mockito.mock(FileListMessageAnalyser.class);
    private static final ClientHandler CLIENT = Mockito.mock(ClientHandler.class);

    private static final Core CORE = Mockito.mock(Core.class);
    private static final ConfigurationProvider PROVIDER = Mockito.mock(ConfigurationProvider.class);
    private static final Configuration CONFIGURATION = Mockito.mock(Configuration.class);

    private static final ClientBaseMessageHandler BASE_HANDLER = new ClientBaseMessageHandler(CORE, PROVIDER);
    private static final FileListMessageHandler HANDLER = new FileListMessageHandler(ANALYSER);


    @BeforeAll
    public static void setup() {
        BASE_HANDLER.setNext(HANDLER);
        Mockito.when(PROVIDER.getReadInstance()).thenReturn(CONFIGURATION);
        Mockito.when(PROVIDER.getWriteInstance()).thenReturn(CONFIGURATION);
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
    public void givenFileListMessage_WhenHandleWithGivenConditions_ThenSentMessageIsCorrect (
            final int sendCalledAtLeast, final int sendCalledAtMost,
            final List<String> clientFilesName, final boolean connected,
            final long defaultSizeForAllFiles, final String expectedMessage) {

        // GIVEN
        Mockito.when(CORE.isClientConnected(LOGIN)).thenReturn(connected);
        Mockito.when(CONFIGURATION.getClientFilesName(Mockito.any())).thenReturn(clientFilesName);
        Mockito.when(CONFIGURATION.getClientFileSize(Mockito.any(), Mockito.any())).thenReturn(defaultSizeForAllFiles);

        // WHEN
        BASE_HANDLER.handle(Mockito.any(), CLIENT);

        // THEN
        Mockito.verify(CLIENT, Mockito.atLeast(sendCalledAtLeast)).send(expectedMessage);
        Mockito.verify(CLIENT, Mockito.atMost(sendCalledAtMost)).send(expectedMessage);
        if(sendCalledAtLeast > 0 && sendCalledAtMost > 0) {
            Mockito.verify(CLIENT, Mockito.never()).send(expectedMessage.equals(FILES_MESSAGE) ? EMPTY_FILES_MESSAGE : FILES_MESSAGE);
        }
    }

    private static Stream<Arguments> provideParameters() {
        return Stream.of(
                Arguments.of(1, 1,FILES_NAME, true, SIZE, FILES_MESSAGE),
                Arguments.of(0, 0, FILES_NAME, false, SIZE, FILES_MESSAGE),
                Arguments.of(1, 1, Collections.emptyList(), true, SIZE, EMPTY_FILES_MESSAGE)

        );
    }
}
