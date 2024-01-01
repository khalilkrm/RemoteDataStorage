package storbackend.sever.unicast.message.erase;
import storbackend.sever.unicast.FFEHandler;
import storbackend.sever.unicast.message.retrieve.RetrieveMessageHandler;
import utils.message.BaseMessageHandler;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EraseMessageHandler extends BaseMessageHandler<FFEHandler> {

    private final static Logger LOGGER = Logger.getLogger(EraseMessageHandler.class.getSimpleName());
    private final EraseMessageAnalyser analyser;

    public EraseMessageHandler(final EraseMessageAnalyser analyser) {
        this.analyser = analyser;
    }

    @Override
    protected boolean canCare(final String message) {
        return analyser.analyse(message);
    }

    @Override
    protected void care(final FFEHandler FFEHandler) {
        final String filename = analyser.get(EraseMessageProperties.FILENAME);

        if(FFEHandler.deleteFile(filename)) {
            sbe_configuration.getWriteInstance().removeFile(filename);
            LOGGER.log(Level.INFO, String.format("File erased successfully: %s", filename));
            FFEHandler.send("ERASE_OK\r\n");
        } else {
            LOGGER.log(Level.INFO, String.format("Could not erase file: %s", filename));
            FFEHandler.send("ERASE_ERROR\r\n");
        }
    }
}
