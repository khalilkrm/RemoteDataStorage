package storbackend.repository;

import java.io.InputStream;
import java.io.OutputStream;

public interface Repository {

    /**
     * Read the given input and save his content in a new file of the given filename
     *
     * @param in the input to read
     * @param newFilename the filename of the new file
     * @param filesize the filesize
     *
     * @return true if the input read and wrote his content successfully otherwise false
     */
    boolean saveFile(final InputStream in, final String newFilename, final long filesize, final String fingerprint);

    /**
     * Read a file of the given filename and write it in the given output
     *
     * @param out the output to write into
     * @param filename the filename to write
     *
     * @return true if wrote successfully otherwise false
     */
    boolean sendFile(final OutputStream out, final String filename);

    boolean fileExists(final String filename);

    boolean deleteFile(final String filename);
}
