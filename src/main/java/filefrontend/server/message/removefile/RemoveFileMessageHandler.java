package filefrontend.server.message.removefile;

import filefrontend.server.ClientHandler;
import filefrontend.server.message.getfile.GetFileMessageProperties;
import filefrontend.domain.exceptions.ClientNotFoundException;
import filefrontend.domain.exceptions.FileNotFoundException;
import utils.message.MessageAnalyser;
import utils.message.BaseMessageHandler;
import filefrontend.responsability.TaskObserver;
import utils.observer.EventType;
import utils.security.OneWayEncryptionUtils;
import utils.task.Status;
import utils.task.Task;

import java.util.logging.Level;
import java.util.logging.Logger;

public class RemoveFileMessageHandler extends BaseMessageHandler<ClientHandler> implements TaskObserver {

    private static final Logger LOGGER = Logger.getLogger(RemoveFileMessageHandler.class.getSimpleName());

    private final MessageAnalyser messageAnalyser;
    private ClientHandler client;

    public RemoveFileMessageHandler(final MessageAnalyser messageAnalyser) {
        super();
        this.messageAnalyser = messageAnalyser;
    }

    @Override
    protected boolean canCare(String message) {
        return messageAnalyser.analyse(message);
    }

    @Override
    protected void care(final ClientHandler client) {
        final String login = client.getLogin();

        if(!controller.isClientConnected(login)) {
            LOGGER.log(Level.INFO, "Remove file error: client not connected");
            client.send("REMOVEFILE_ERROR\r\n");
            return;
        }

        final String filename = messageAnalyser.get(GetFileMessageProperties.FILENAME);
        String sbeId;

        try {
           sbeId  = configuration.getReadInstance().getFilesHolderFromClient(login, filename);
        } catch (final ClientNotFoundException | FileNotFoundException exception) {
            if(exception instanceof  FileNotFoundException)
                LOGGER.log(Level.INFO, "Remove file error: file does not exist");
            else LOGGER.log(Level.INFO, "Remove file error: client does not exist");
            client.send("REMOVEFILE_ERROR\r\n");
            return;
        }

        final Task task = new Task(this);
        task.setSbeId(sbeId);
        task.setType(EventType.EraseFile);
        task.setUserLogin(login);
        task.setSavedFilename(OneWayEncryptionUtils.SHA384EncryptAsHex(login + filename));
        task.setOriginalFilename(filename);

        this.client = client;
        controller.pushTask(task);
    }

    @Override
    public void notifyStatusChanges(Task task) {
        if(task.getStatus().equals(Status.REJECTED)) {
            LOGGER.log(Level.INFO, "Remove file error: task REJECTED");
            client.send("REMOVEFILE_ERROR\r\n");
        } else if(task.getStatus().equals(Status.FULFILLED)) {
            LOGGER.log(Level.INFO, "Remove file success: task FULFILLED");
            configuration.getWriteInstance().removeFileFromClient(task.getUserLogin(), task.getOriginalFilename());
            client.send("REMOVEFILE_OK\r\n");
            LOGGER.log(Level.INFO, "Remove file success: file removed");
        }
    }
}
