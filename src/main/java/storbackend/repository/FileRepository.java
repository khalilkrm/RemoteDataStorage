package storbackend.repository;

import utils.file.FileReceiver;
import utils.file.FileSender;
import utils.file.FileUtils;
import utils.file.RepositoryFileCouldNotBeCreated;
import utils.security.Encryptor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public class FileRepository implements Repository {

    private final FileSender sender;
    private final FileReceiver receiver;
    private final String directory;
    private final Encryptor encryptor;

    public FileRepository(final FileSender sender, final FileReceiver receiver, final Encryptor encryptor, final String directory) {
        this.sender = sender;
        this.receiver = receiver;
        this.directory = directory;
        this.encryptor = encryptor;
        try { FileUtils.createDirectory(directory); }
        catch (final IOException e) { throw new RepositoryFileCouldNotBeCreated(e.getMessage()); }
    }

    @Override
    public boolean saveFile(final InputStream in, final String newFilename, final long filesize, final String fingerprint) {
        try {
            if(receiver.receiveFile(in, newFilename, directory, filesize)) {
                final byte[] received = Files.readAllBytes(Path.of(directory, newFilename));
                return checkFingerprint(received, fingerprint);
            } else return false;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public boolean sendFile(final OutputStream out, final String filename) {
        return sender.sendFile(filename, directory, out);
    }

    @Override
    public boolean fileExists(String filename) {
        return Files.exists(Path.of(directory, filename));
    }

    @Override
    public boolean deleteFile(String filename) {
        try {
            Files.deleteIfExists(Path.of(directory, filename));
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private boolean checkFingerprint(final byte[] content, final String fingerprint) {
        final String f = encryptor.SHA384EncryptAsHex(content);
        return f.equals(fingerprint);
    }
}
