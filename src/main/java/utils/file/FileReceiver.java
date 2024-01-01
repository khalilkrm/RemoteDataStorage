package utils.file;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

public class FileReceiver {
    private static final int DEFAULT_BUFFER = 8000;

    public boolean receiveFile(final InputStream input, final String filename, final String directory, final long fileSize) {
        int bytesReceived = 0;
        BufferedOutputStream bosFile = null;

        try {
            byte[] buffer = new byte[DEFAULT_BUFFER];
            bosFile = new BufferedOutputStream(new FileOutputStream(String.format("%s/%s", directory, filename)));
            long currentOffset = 0;

            while((currentOffset < fileSize) && ((bytesReceived = input.read(buffer)) > 0)) {
                bosFile.write(buffer, 0, bytesReceived);
                currentOffset += bytesReceived;
            }
            bosFile.flush();
            bosFile.close();

            return true;
        } catch(Exception ex) {
            ex.printStackTrace();
            if(bosFile != null) { try { bosFile.close(); } catch(Exception e) {} }
            return false;
        }
    }

}

