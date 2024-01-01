package filefrontend.server.message.savefile;

import filefrontend.responsability.TaskObserver;
import filefrontend.server.ClientHandler;
import filefrontend.repository.SavedState;
import filefrontend.domain.File;
import filefrontend.domain.exceptions.ClientNotFoundException;
import utils.message.MessageAnalyser;
import utils.message.BaseMessageHandler;
import utils.observer.EventType;
import utils.task.Status;
import utils.task.Task;

import javax.crypto.SecretKey;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SaveFileMessageHandler extends BaseMessageHandler<ClientHandler> implements TaskObserver {

    private final Logger LOGGER = Logger.getLogger(SaveFileMessageHandler.class.getSimpleName());

    private final MessageAnalyser messageAnalyser;
    private ClientHandler client;

    public SaveFileMessageHandler(final MessageAnalyser messageAnalyser) {
        super();
        this.messageAnalyser = messageAnalyser;
    }

    @Override
    protected boolean canCare(final String message) {
        return messageAnalyser.analyse(message);
    }

    @Override
    protected void care(final ClientHandler client) {

        LOGGER.log(Level.INFO, "Save file care");

        final String login = client.getLogin();

        if(!controller.isClientConnected(login)) {
            LOGGER.log(Level.INFO, "Save file error: client not connected");
            client.send("SAVEFILE_ERROR\r\n");
            return;
        }

        final String filename = messageAnalyser.get(SaveFileMessageProperties.FILENAME);
        final String size = messageAnalyser.get(SaveFileMessageProperties.SIZE);

        boolean secretFound = true;
        SecretKey key = null;

        // Generate a iv
        byte[] iv = configuration.getReadInstance().getRandomIV();

        try {
            key = configuration.getReadInstance().stringToSecretKey(configuration.getReadInstance().getClientAes(login));
        } catch (final ClientNotFoundException e) {
            secretFound = false;
        }

        SavedState savedState = null;

        if (secretFound) {
            savedState = client.saveFile(filename, Long.parseLong(size), key, iv);
        }

        if(secretFound && savedState.getSavedState()) {
            final Task task = new Task(this);
            task.setType(EventType.SendFile);

            task.setOriginalFilename(filename);
            task.setSavedFilename(savedState.getSavedFilename());
            task.setUserLogin(login);
            task.setFingerprint(savedState.getFingerprint());
            task.setFileSize(size);
            task.setIv(iv);

            this.client = client;
            LOGGER.log(Level.INFO, "Save file: task pushed");
            controller.pushTask(task);
        } else {
            LOGGER.log(Level.INFO, "Save file error: client not connected");
            client.send("SAVEFILE_ERROR\r\n");
        }
    }

    @Override
    public void notifyStatusChanges(final Task task) {
        if(task.getStatus().equals(Status.REJECTED)) {

            LOGGER.log(Level.INFO, "Save file error: task REJECTED");
            client.send("SAVEFILE_ERROR\r\n");

        } else if(task.getStatus().equals(Status.FULFILLED)) {
            LOGGER.log(Level.INFO, "Save file success: task FULFILLED");

            final String filename = Objects.requireNonNull(task.getOriginalFilename());
            final String login = Objects.requireNonNull(task.getUserLogin());
            final String holder = Objects.requireNonNull(task.getSbeId());
            final String size = Objects.requireNonNull(task.getSize());
            final byte[] iv = Objects.requireNonNull(task.getIv());

            final File file = new File(filename, holder, Long.parseLong(size), new String(iv));
            configuration.getWriteInstance().addFileToClient(login, file);
            client.removeFile(task.getSavedFilename());

            client.send("SAVEFILE_OK\r\n");
            LOGGER.log(Level.INFO, "Save file success");
        }
    }
}