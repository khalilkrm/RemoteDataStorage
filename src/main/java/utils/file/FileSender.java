package utils.file;

import java.io.*;

public class FileSender {
    private static final int DEFAULT_BUFFER=8000;

    /**
     * Send a file stored in given <code>directory</code> and named <code>filename</code>
     *
     * @param filename the filename of the file to send
     * @param directory the directory where the file to send is stored
     * @param out the output to send to
     *
     * @return true if the file was sent successfully else false
     */
    public boolean sendFile(final String filename, final String directory, final OutputStream out) {
        BufferedInputStream bisFile = null;
        int bytesRead = 0;

        try {
            File f = new File(String.format("%s/%s", directory, filename));
            long fileSize = f.length();
            if(f.exists()) {
                byte[] buffer = new byte[DEFAULT_BUFFER];
                bisFile = new BufferedInputStream(new FileInputStream(f));
                long currentOffset = 0;
                while((currentOffset < fileSize) && (bytesRead = bisFile.read(buffer)) > 0) {
                    out.write(buffer, 0, bytesRead); out.flush();
                    currentOffset+= bytesRead;
                }
                bisFile.close();
                return true;
            } else
                return false;
        } catch(IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }
}