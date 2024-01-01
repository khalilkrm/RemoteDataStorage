package filefrontend.repository;

import utils.file.FileReceiver;
import utils.file.FileSender;
import utils.file.FileUtils;
import utils.file.RepositoryFileCouldNotBeCreated;
import utils.security.Encryptor;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileRepository implements Repository {

    private static final Logger LOGGER = Logger.getLogger(FileRepository.class.getSimpleName());

    private final FileSender sender;
    private final FileReceiver receiver;
    private final Encryptor encryptor;
    private final String directory;

    public FileRepository(final FileSender sender, final FileReceiver receiver, final Encryptor encryptor, final String directory) {
        this.sender = sender;
        this.receiver = receiver;
        this.encryptor = encryptor;
        this.directory = directory;
        try { FileUtils.createDirectory(directory); }
        catch (final IOException e) { throw new RepositoryFileCouldNotBeCreated(e.getMessage()); }
    }

    @Override
    public SavedState saveFileFromClient(
            final InputStream in,
            final String login,
            final String filename,
            final long filesize,
            final SecretKey key,
            final byte[] iv) {

        final String hashFilename = filename(login, filename);
        final Path savedPath = Path.of(directory, hashFilename);
        final boolean received = receiver.receiveFile(in, hashFilename, directory, filesize);

        boolean encrypted = true;
        byte[] cipher = new byte[0];
        String fingerprint = "";

        if(received) {
            try {
                final byte[] bytes = Files.readAllBytes(savedPath);
                cipher = encryptor.encrypt(bytes, key, iv);
                fingerprint = encryptor.SHA384EncryptAsHex(cipher);
            } catch (Exception e) {
                encrypted = false;
            }
        }

        boolean rewroteEncrypted = true;

        if(received && encrypted) {
            try {
                Files.write(savedPath, cipher);
            } catch (IOException e) {
                rewroteEncrypted = false;
            }
        }

        return new SavedState(received && encrypted && rewroteEncrypted, hashFilename, fingerprint);
    }

    @Override
    public boolean saveInputContentToNewFile(final InputStream in, final String login, final String filename, final long filesize) {
        return receiver.receiveFile(in, filename(login, filename), directory, filesize);
    }

    @Override
    public boolean writeFileContentToOutputStream(final OutputStream out, final String filename) {
        return sender.sendFile(filename, directory, out);
    }

    @Override
    public void removeFile(String savedFilename) {
        try {
            Files.deleteIfExists(Path.of(directory, savedFilename));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Could not remove file from tmp repo");
        }
    }

    @Override
    public boolean sendFileToClient(
            final OutputStream out,
            final String login,
            final String filename,
            final long filesize,
            final SecretKey key,
            final byte[] iv) {

        final String hashFilename = filename(login, filename);
        final Path savedPath = Path.of(directory, hashFilename);

        // read the file end decrypt it

        boolean decrypted = true;
        byte[] plain = new byte[0];

        try {
            final byte[] bytes = Files.readAllBytes(savedPath);
            plain = encryptor.decrypt(bytes, key, iv);
        } catch (Exception e) {
            decrypted = false;
        }

        // after decrypt rewrite it

        boolean rewroteDecrypted = true;

        if(decrypted) {
            try {
                Files.write(savedPath, plain);
            } catch (IOException e) {
                rewroteDecrypted = false;
            }
        }

        // Send it

        final boolean sent = sender.sendFile(hashFilename, directory, out);

        return decrypted && rewroteDecrypted && sent;

    }

    private String filename(final String login, final String filename) {
        return encryptor.SHA384EncryptAsHex(login + filename);
    }
}
