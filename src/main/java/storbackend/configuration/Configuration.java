package storbackend.configuration;

import storbackend.domain.File;
import storbackend.domain.exception.FileNotFoundException;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Configuration {

    private static final Logger LOGGER = Logger.getLogger(Configuration.class.getSimpleName());

    private final Set<File> files;
    private final String repoPath;
    private final String path;

    public Configuration(final List<File> files, final String repositoryPath, final String configurationPath) {
        this.files = new HashSet<>(files);
        repoPath = repositoryPath;
        this.path = configurationPath;
    }

    public void addFile(final File file) {
        files.add(file);
        ConfigurationWriter.write(this, Path.of(path));
        LOGGER.log(Level.INFO, String.format("File added: %s", file.getFilename()));
    }

    public String getRepoPath() {
        return repoPath;
    }

    public long getFileSize(final String filename) {
        return getFileSize(findFileByName(filename));
    }

    private long getFileSize(final File file) {
        return file.getSize();
    }

    public String getFileFingerprint(final String filename) {
        return getFileFingerprint(findFileByName(filename));
    }

    private String getFileFingerprint(final File file) {
        return file.getFingerprint();
    }

    private File findFileByName(final String filename) {
        final File found = files
                .stream()
                .filter(client -> client.getFilename().equals(filename))
                .findFirst()
                .orElse(null);
        if(found == null)
            throw new FileNotFoundException(String.format("Client with login %s not found", filename));
        else return found;
    }

    public void removeFile(final String filename) {
        files.removeIf(file -> file.getFilename().equals(filename));
        ConfigurationWriter.write(this, Path.of(path));
    }
}
