package filefrontend.client.task.retrievefile;

import filefrontend.client.StorBackEnd;
import utils.message.MessageAnalyser;
import utils.observer.EventType;
import utils.task.BaseTaskHandler;
import utils.task.Status;
import utils.task.Task;

import java.util.Locale;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RetrieveFileTaskHandler extends BaseTaskHandler {

    private static final Logger LOGGER = Logger.getLogger(RetrieveFileTaskHandler.class.getSimpleName());
    private final MessageAnalyser analyser = new RetrieveResultMessageAnalyser();

    @Override
    protected boolean canCare(final Task task) {
        return task.getType().equals(EventType.RetrieveFile);
    }

    @Override
    protected void care(final Task task, final StorBackEnd storBackEnd) {

        LOGGER.log(Level.INFO, "Retrieve file result care");

        final String login = Objects.requireNonNull(task.getUserLogin());
        final String filename = Objects.requireNonNull(task.getSavedFilename());

        final String message = String.format("RETRIEVEFILE %s\r\n", filename);
        storBackEnd.send(message);

        final String answer = storBackEnd.receive();

        if (!analyser.analyse(answer)) {
            LOGGER.log(Level.INFO, String.format("Retrieve file result answer not correct: %s", answer));
            task.setStatus(Status.REJECTED);
            return;
        }

        if(analyser.get(RetrieveResultProperties.IS_ERROR).toLowerCase(Locale.ROOT).contains("true")) {
            LOGGER.log(Level.INFO, "Retrieve file result: REJECTED");
            task.setStatus(Status.REJECTED);
            return;
        }

        final String size = analyser.get(RetrieveResultProperties.SIZE);

        if(!storBackEnd.receiveFile(login, filename, Long.parseLong(size))) {
            LOGGER.log(Level.INFO, "Retrieve file result: REJECTED: could not receive file");
            task.setStatus(Status.REJECTED);
            return;
        }

        task.setFileSize(size);
        task.setStatus(Status.FULFILLED);
    }
}
