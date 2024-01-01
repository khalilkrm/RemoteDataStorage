package utils.file.repository;


import filefrontend.repository.FileRepository;
import filefrontend.repository.Repository;
import filefrontend.repository.SavedState;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import utils.file.FileReceiver;
import utils.file.FileSender;
import utils.security.EncryptionUtils;
import utils.security.Encryptor;
import utils.security.OneWayEncryptionUtils;
import utils.security.TwoWayEncryptionUtils;

import javax.crypto.SecretKey;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.*;

public class FileRepositoryTests {

    private final static String REPOSITORY_PATH = Path.of("src", "test", "resources", "repository").toAbsolutePath().toString();
    private final static int BUFFER_SIZE = 8000;

    private final static String LOGIN = "SuperLogin";
    private final static String FILENAME = "SuperFilename";
    private final static String CONTENT = StringUtils.repeat("It was the start of a golden age for French restaurants in New York that continued for decades.\n", BUFFER_SIZE + 1);

    private final static Repository REPOSITORY = new FileRepository(new FileSender(), new FileReceiver(), new Encryptor(), REPOSITORY_PATH);

    private static InputStream INPUT_STREAM;
    private static long SIZE;
    private static SecretKey KEY;
    private static byte[] IV;

    @BeforeAll
    public static void setup() throws NoSuchAlgorithmException {
        final byte[] bytes = CONTENT.getBytes(StandardCharsets.UTF_8);
        INPUT_STREAM = new ByteArrayInputStream(bytes);
        SIZE = bytes.length;
        KEY = TwoWayEncryptionUtils.getRandomAesKey();
        IV = EncryptionUtils.getRandomIV();
    }

    @Test
    public void givenInputStreamWithPlainText_WhenEncryptThenSaveAndDecryptThenSend_ThenOutputIsSameAsOriginal() throws IOException {

        final String expectedFilename = OneWayEncryptionUtils.SHA384EncryptAsHex(LOGIN + FILENAME);

        // WHEN
        // WRITE ENCRYPTED
        final SavedState savedState = REPOSITORY.saveFileFromClient(INPUT_STREAM, LOGIN, FILENAME, SIZE, KEY, IV);
        assertTrue(savedState.getSavedState());
        assertEquals(expectedFilename, savedState.getSavedFilename());
        // READ DECRYPTED (GIVE A FILE AS OUTPUT)
        try(final BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(Path.of(REPOSITORY_PATH, "out").toAbsolutePath().toString()))) {
            final boolean sent = REPOSITORY.sendFileToClient(out, LOGIN, FILENAME, SIZE, KEY, IV);
            assertTrue(sent);
        }

        // THEN
        final byte[] actual = Files.readAllBytes(Path.of(REPOSITORY_PATH, "out"));
        assertArrayEquals(CONTENT.getBytes(StandardCharsets.UTF_8), actual);
    }
}
