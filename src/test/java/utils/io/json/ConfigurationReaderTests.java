package utils.io.json;

import filefrontend.configuration.ConfigurationReader;
import com.google.gson.Gson;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import filefrontend.configuration.Configuration;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Collections;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConfigurationReaderTests {

    final Gson gson = new Gson();

    @BeforeEach
    public void setup() {
        try {
            if(Files.exists(ConfigurationReaderWriterTestUtils.PATH))
                Files.delete(ConfigurationReaderWriterTestUtils.PATH);
        } catch (IOException e) {
            e.printStackTrace();
            Assertions.fail();
        }
    }

    @ParameterizedTest
    @MethodSource("provideConfigurations")
    public void givenPath_WhenRead_ThenGetConfigurationWithAllFields(final Configuration expected) {

        // GIVEN
        write(gson.toJson(expected, Configuration.class));

        // WHEN
        final Configuration actual = ConfigurationReader.read(
                ConfigurationReaderWriterTestUtils.PATH,
                new Configuration(0, "", "", "",Collections.emptyList()));

        // THEN
        ConfigurationReaderWriterTestUtils.test(expected, actual);
    }

    @Test
    public void givenPathWithNoConfiguration_WhenRead_ThenEmptyConfigurationIsWroteAndReturned() {

        // GIVEN
        final Configuration expected = new Configuration(0, "", "", "", Collections.emptyList());

        // WHEN
        final Configuration actual = ConfigurationReader.read(ConfigurationReaderWriterTestUtils.PATH, expected);

        // THEN
        ConfigurationReaderWriterTestUtils.test(expected, actual);
        assertTrue(Files.exists(ConfigurationReaderWriterTestUtils.PATH));
    }

    public void write(final String json) {
        try(final BufferedWriter writer = Files.newBufferedWriter(ConfigurationReaderWriterTestUtils.PATH, StandardCharsets.UTF_8)) {
            writer.write(json);
        } catch (final IOException ex) {
            ex.printStackTrace();
            Assertions.fail();
        }
    }

    public static Stream<Arguments> provideConfigurations() {
        return ConfigurationReaderWriterTestUtils.configurationAsStreamOfArguments();
    }
}
