package storbackend.sever.unicast.message.retrieve;

import storbackend.sever.unicast.FFEHandler;
import utils.message.BaseMessageHandler;
import utils.message.MessageAnalyser;

import java.util.logging.Level;
import java.util.logging.Logger;

public class RetrieveMessageHandler  extends BaseMessageHandler<FFEHandler> {

    private final static Logger LOGGER = Logger.getLogger(RetrieveMessageHandler.class.getSimpleName());
    private final MessageAnalyser analyser;

    public RetrieveMessageHandler(final MessageAnalyser retrieveMessageAnalyser) {
        this.analyser = retrieveMessageAnalyser;
    }

    @Override
    protected boolean canCare(final String message) {
        return analyser.analyse(message);
    }

    @Override
    protected void care(final FFEHandler FFEHandler) {
        // Get filename
        final String filename = analyser.get(RetrieveMessageProperties.FILENAME);

        // Send the file
        if(FFEHandler.hasFile(filename)) {
            // create the checksum
            final long size = sbe_configuration.getReadInstance().getFileSize(filename);
            final String fingerprint = sbe_configuration.getReadInstance().getFileFingerprint(filename);
            LOGGER.log(Level.INFO, String.format("File found: %s", filename));
            FFEHandler.send(String.format("RETRIEVE_OK %s %s %s\r\n", filename, size, fingerprint));
            FFEHandler.sendFile(filename);
        } else {
            FFEHandler.send("RETRIEVE_ERROR\r\n");
            LOGGER.log(Level.INFO, String.format("File not found: %s", filename));
        }
    }
}

