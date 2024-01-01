package storbackend.sever.unicast.message.sendfile;

import storbackend.domain.File;
import storbackend.sever.unicast.FFEHandler;
import utils.message.BaseMessageHandler;
import utils.message.MessageAnalyser;

import java.lang.ref.PhantomReference;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SendFileMessageHandler extends BaseMessageHandler<FFEHandler> {

    private static final Logger LOGGER = Logger.getLogger(SendFileMessageHandler.class.getSimpleName());
    private final MessageAnalyser analyser;


    public SendFileMessageHandler(final MessageAnalyser sendFileMessageAnalyser) {
        this.analyser = sendFileMessageAnalyser;
    }

    @Override
    protected boolean canCare(String message) {
        return analyser.analyse(message);
    }

    @Override
    protected void care(final FFEHandler ffeHandler) {

        final String filename = analyser.get(SendFileMessageProperties.FILENAME);
        final String size = analyser.get(SendFileMessageProperties.SIZE);
        final String fingerprint = analyser.get(SendFileMessageProperties.FINGERPRINT);

        if (ffeHandler.hasFile(filename)) {
            LOGGER.log(Level.INFO, "File already exists");
            ffeHandler.send("SEND_ERROR\r\n");
            return;
        }

        // use the repository to store the file from the client
        final boolean stored = ffeHandler.storeFile(filename, Long.parseLong(size), fingerprint);

        if(!stored) {
            ffeHandler.send("SEND_ERROR\r\n");
            LOGGER.log(Level.INFO, "Could not store file");
        } else {
            final File file = new File(filename, Long.parseLong(size), fingerprint);
            sbe_configuration.getWriteInstance().addFile(file);
            ffeHandler.send("SEND_OK\r\n");
            LOGGER.log(Level.INFO, "File stored");
        }
    }
}
