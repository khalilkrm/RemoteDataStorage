package filefrontend.repository;

import javax.crypto.SecretKey;
import java.io.InputStream;
import java.io.OutputStream;

public interface Repository {

    /**
     * Read the given input, encrypt his content and write it in a new file og the filename
     *
     * @param in the input to read
     * @param login the user login
     * @param filename the filename to write
     * @param filesize the filesize
     * @param key the key to encrypt the file content
     * @param iv the iv to encrypt the file content
     *
     * @return true in the input read, encrypt and write successfully otherwise false
     */
    SavedState saveFileFromClient(final InputStream in, final String login, final String filename, final long filesize, final SecretKey key, final byte[] iv);


    /**
     * Read an encrypted file of the given filename decrypt it and write it in the given output
     *
     * @param out the output to write into
     * @param login the user login
     * @param filename the filename to write
     * @param filesize the filesize
     * @param key the key to decrypt the file content
     * @param iv the iv to decrypt the file content
     *
     * @return true of the decrypted and wrote successfully otherwise false
     */
    boolean sendFileToClient(final OutputStream out, final String login, final String filename, final long filesize, final SecretKey key, final byte[] iv);

    /**
     * Read the given input and save his content in a new file of the given filename
     *
     * @param in the input to read
     * @param login the user login
     * @param newFilename the filename of the new file
     * @param filesize the filesize
     *
     * @return true if the input read and wrote his content successfully otherwise false
     */
    boolean saveInputContentToNewFile(final InputStream in, final String login, final String newFilename, final long filesize);

    /**
     * Read a file of the given filename and write it in the given output
     *
     * @param out the output to write into
     * @param filename the filename to write
     *
     * @return true if wrote successfully otherwise false
     */
    boolean writeFileContentToOutputStream(final OutputStream out, final String filename);

    void removeFile(String savedFilename);
}
