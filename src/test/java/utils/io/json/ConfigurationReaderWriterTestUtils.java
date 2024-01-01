package utils.io.json;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.provider.Arguments;
import filefrontend.domain.Client;
import filefrontend.configuration.Configuration;
import filefrontend.domain.File;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConfigurationReaderWriterTestUtils {

    protected static final Path PATH = Path.of("src", "test", "resources", "configuration.json");

    protected static List<Configuration> configurations = List.of(
            new Configuration(15507, "224.66.66.1", "", "", List.of(
                    new Client("client-1", "ceciestunmotdepasse", "", "", "aes-key",
                            List.of(new File("file-1", "sbe-1", 3L, ""))))),
            new Configuration(15507, "224.66.66.1", "", "",List.of(
                    new Client("client-1", "ceciestunmotdepasse", "","","aes-key",
                            List.of(new File("file-1", "sbe-1", 3L, ""))),
                    new Client("client-2", "ceciestunmotdepasse", "","","aes-key",
                            List.of(new File("file-2", "sbe-1", 3L, ""))))),
            new Configuration(15507, "224.66.66.1", "", "", List.of(
                    new Client("client-1", "ceciestunmotdepasse", "","","aes-key",
                            List.of(new File("file-1", "sbe-1", 3L, ""),
                                    new File("file-3", "sbe-1", 3L, ""))),
                    new Client("client-2", "ceciestunmotdepasse", "","","aes-key",
                            List.of(new File("file-2", "sbe-3", 3L, ""),
                                    new File("file-4", "sbe-4", 3L, "")))))
    );

    protected static void test(Configuration expected, Configuration actual) {
        try {

            // ASSERT CONFIGURATION FIELDS
            assertEquals(expected.getAddress(), actual.getAddress());
            assertEquals(expected.getPort(), actual.getPort());
            assertEquals(expected.getClientsCount(), actual.getClientsCount());

            // GET CLIENTS LOGINS
            final List<String> expectedClientsLogin = expected.getClientsLogin();
            final List<String> actualClientsLogin = actual.getClientsLogin();

            final Map<String, String> expectedClientToActualClient = IntStream
                    .range(0, expected.getClientsCount())
                    .boxed()
                    .collect(Collectors.toMap(expectedClientsLogin::get, actualClientsLogin::get));

            expectedClientToActualClient.forEach((expectedClientLogin, actualClientLogin) -> {

                // ASSERT CLIENTS FIELDS
                assertEquals(expected.getClientAes(expectedClientLogin), actual.getClientAes(actualClientLogin));
                assertEquals(expected.getClientFilesCount(expectedClientLogin), actual.getClientFilesCount(actualClientLogin));

                // GET CLIENTS FILES
                final List<String> expectedClientFilesNames = expected.getClientFilesName(expectedClientLogin);
                final List<String> actualClientFilesNames = actual.getClientFilesName(actualClientLogin);

                final Map<String, String> expectedFileNameToActualFileName = IntStream
                        .range(0, expectedClientFilesNames.size())
                        .boxed()
                        .collect(Collectors.toMap(expectedClientFilesNames::get, actualClientFilesNames::get));

                expectedFileNameToActualFileName.forEach((expectedFileName, actualFileName) -> {
                    // ASSERT CLIENTS FILES FIELDS
                    assertEquals(expected.getFilesHolderFromClient(expectedClientLogin, expectedFileName),
                            actual.getFilesHolderFromClient(actualClientLogin, actualFileName));
                    assertEquals(expected.getClientFileSize(expectedClientLogin, expectedFileName),
                            actual.getClientFileSize(actualClientLogin, actualFileName));
                });
            });

        } catch (final IllegalStateException ex) {
            ex.printStackTrace();
            Assertions.fail();
        }
    }

    public static Stream<Arguments> configurationAsStreamOfArguments() {
        return Stream.of(
                Arguments.of(configurations.get(0)),
                Arguments.of(configurations.get(1)),
                Arguments.of(configurations.get(2))
        );
    }
}
