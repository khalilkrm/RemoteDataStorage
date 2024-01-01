package filefrontend.configuration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigurationReader {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static Configuration read(final Path path, final Configuration initial) {
        if(!exists(path))
            write(path, initial);
        return doRead(path);
    }

    private static Configuration doRead(final Path path) {
        try(final BufferedReader buffer = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            return gson.fromJson(buffer, Configuration.class);
        } catch (final IOException ioe) {
            // TODO handle exception
        }
        return null;
    }

    private static void write(final Path path, final Configuration initial) {
        try(final BufferedWriter writer = Files.newBufferedWriter(path)) {
            writer.write(gson.toJson(initial, Configuration.class));
        } catch (final IOException e) {
            // TODO handle exception
        }
    }

    private static boolean exists(final Path path) {
        return Files.exists(path);
    }
}
