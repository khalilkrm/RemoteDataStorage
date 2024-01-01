package storbackend.configuration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConfigurationWriter {

    private static final Logger LOGGER = Logger.getLogger(ConfigurationWriter.class.getSimpleName());
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static void write(final Configuration configuration, final Path path) {
        doWrite(configuration, path);
    }

    private static void doWrite(final Configuration configuration, final Path path) {
        try(final BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            final String json = gson.toJson(configuration, Configuration.class);
            writer.write(json);
        } catch (final IOException ioe) {
           LOGGER.log(Level.SEVERE, String.format("[%s] Could not write configation : %s", Thread.currentThread().getName(), ioe.getMessage()));
        }
    }
}
