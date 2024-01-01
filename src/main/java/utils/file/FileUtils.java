package utils.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileUtils {

    public static boolean createFile(final String directory, final String filename) throws IOException {
        return doCreateFile(createDirectory(directory), filename);
    }

    private static boolean doCreateFile(final Path directory, final String filename) throws IOException {
        final Path p = Path.of(directory.toAbsolutePath().toString(), filename);
        Files.createFile(p);
        return true;
    }

    public static Path createDirectory(final String path) throws IOException {
        return Files.createDirectories(toPath(path));
    }

    public static Path toPath(final String path) {
        return Path.of(path);
    }

    private static boolean exists(final Path path) {
        return Files.exists(path);
    }
}
