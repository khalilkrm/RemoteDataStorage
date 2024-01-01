package filefrontend.server.message.getfile;

import filefrontend.responsability.TaskObserver;
import filefrontend.server.ClientHandler;
import filefrontend.domain.exceptions.ClientNotFoundException;
import filefrontend.domain.exceptions.FileNotFoundException;
import utils.message.MessageAnalyser;
import utils.message.BaseMessageHandler;
import utils.observer.EventType;
import utils.security.OneWayEncryptionUtils;
import utils.task.Status;
import utils.task.Task;

import javax.crypto.SecretKey;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GetFileMessageHandler extends BaseMessageHandler<ClientHandler> implements TaskObserver {

    private static final Logger LOGGER = Logger.getLogger(GetFileMessageHandler.class.getSimpleName());


    private final MessageAnalyser messageAnalyser;
    private ClientHandler client;

    public GetFileMessageHandler(final MessageAnalyser messageAnalyser) {
        super();
        this.messageAnalyser = messageAnalyser;
    }

    @Override
    protected boolean canCare(final String message) {
        return messageAnalyser.analyse(message);
    }

    @Override
    protected void care(final ClientHandler client) {

        final String login = client.getLogin();

        if(!controller.isClientConnected(login)) {
            LOGGER.log(Level.INFO, "Get file error: client not connected");
            client.send("GETFILE_ERROR\r\n");
            return;
        }

        final String filename = messageAnalyser.get(GetFileMessageProperties.FILENAME);
        String sbeId;

        try {
           sbeId = configuration.getReadInstance().getFilesHolderFromClient(login, filename);
        } catch (final ClientNotFoundException | FileNotFoundException exception) {
            if(exception instanceof  FileNotFoundException)
                LOGGER.log(Level.INFO, "Get file error: file does not exist");
            else LOGGER.log(Level.INFO, "Get file error: client does not exist");
            client.send("GETFILE_ERROR\r\n");
            return;
        }

        final Task task = new Task(this);

        task.setType(EventType.RetrieveFile);
        task.setUserLogin(login);
        task.setSbeId(sbeId);
        task.setSavedFilename(OneWayEncryptionUtils.SHA384EncryptAsHex(login + filename));
        task.setOriginalFilename(filename);

        this.client = client;
        controller.pushTask(task);
    }

    @Override
    public void notifyStatusChanges(Task task) {
        if(task.getStatus().equals(Status.REJECTED)) {
            LOGGER.log(Level.INFO, "Get file error: task REJECTED");
            client.send("GETFILE_ERROR\r\n");
        } else if(task.getStatus().equals(Status.FULFILLED)) {

            LOGGER.log(Level.INFO, "Get file success: task FULFILLED");

            final String original = Objects.requireNonNull(task.getOriginalFilename());
            final String size = Objects.requireNonNull(task.getSize());
            final String filename = Objects.requireNonNull(task.getSavedFilename());
            final String login = Objects.requireNonNull(task.getUserLogin());

            final SecretKey key = configuration.getReadInstance().stringToSecretKey(configuration.getReadInstance().getClientAes(login));
            final byte[] iv = configuration.getReadInstance().getFileIVFromClient(login, original);

            client.send(String.format("GETFILE_OK %s %s", original, size));
            client.sendFile(filename, Long.parseLong(size), key, iv);
            client.removeFile(filename);

            LOGGER.log(Level.INFO, "Get file success: file sent");
        }
    }
}
