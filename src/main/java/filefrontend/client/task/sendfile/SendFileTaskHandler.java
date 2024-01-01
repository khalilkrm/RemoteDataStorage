package filefrontend.client.task.sendfile;

import filefrontend.client.StorBackEnd;
import utils.observer.EventType;
import utils.task.BaseTaskHandler;
import utils.task.Status;
import utils.task.Task;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class SendFileTaskHandler extends BaseTaskHandler {

    private final Logger LOGGER = Logger.getLogger(SendFileTaskHandler.class.getSimpleName());

    private final Pattern errorPattern = Pattern.compile("^SEND_ERROR\r?\n?$");
    private final Pattern okPattern = Pattern.compile("^SEND_OK\r?\n?$");

    @Override
    protected boolean canCare(final Task task) {
        return task.getType()==EventType.SendFile;
    }

    @Override
    protected void care(final Task task, final StorBackEnd storBackEnd) {

        LOGGER.log(Level.INFO, "Send file care");

        // BUILD MESSAGE
        final String filename = Objects.requireNonNull(task.getSavedFilename());
        final String fingerprint = Objects.requireNonNull(task.getFingerprint());
        final String size = Objects.requireNonNull(task.getSize());

        final String message = String.format("SENDFILE %s %s %s\r\n", filename, size, fingerprint);

        // SEND THE MESSAGE
        storBackEnd.send(message);

        // todo en donne le nom hashé dans le save et ici il va le rehashé, choisir
        // SEND THE FILE
        if(!storBackEnd.sendFile(filename)) {
            LOGGER.log(Level.INFO, "Send file rejected: could not send file");
            task.setStatus(Status.REJECTED);
            return;
        }

        // WAIT FOR ANSWER
        final String answer = storBackEnd.receive();

        // HANDLE ANSWER
        if(okPattern.matcher(answer).matches()) {
            LOGGER.log(Level.INFO, "Send file sent and saved");
            task.setStatus(Status.FULFILLED);
        } else if(errorPattern.matcher(answer).matches()) {
            LOGGER.log(Level.INFO, "Send file not send but not saved");
            task.setStatus(Status.REJECTED);
        } else {
            LOGGER.log(Level.INFO, "Send file answer not recognized");
            task.setStatus(Status.REJECTED);
        }
    }
}