package utils.io.json;

import filefrontend.configuration.ConfigurationWriter;
import com.google.gson.Gson;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import filefrontend.configuration.Configuration;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ConfigurationWriterTests {

    private final Gson gson = new Gson().newBuilder().setPrettyPrinting().create();

    @ParameterizedTest
    @MethodSource("provideConfigurations")
    public void givenConfiguration_WhenWrite_ThenAllFieldAreWrote(final Configuration expected) {

        // WHEN
        ConfigurationWriter.write(expected, ConfigurationReaderWriterTestUtils.PATH);

        // READ JSON
        final Configuration actual = gson.fromJson(read(), Configuration.class);

        //THEN
        ConfigurationReaderWriterTestUtils.test(expected, actual);
    }

    private String read() {
        try(BufferedReader reader = Files.newBufferedReader(ConfigurationReaderWriterTestUtils.PATH)) {
            return reader.lines().collect(Collectors.joining());
        } catch (IOException ex) {
            ex.printStackTrace();
            Assertions.fail();
            throw new IllegalStateException(String.format("Could not read: %s", ex.getMessage()), ex);
        }
    }

    private static Stream<Arguments> provideConfigurations() {
        return ConfigurationReaderWriterTestUtils.configurationAsStreamOfArguments();
    }
}
