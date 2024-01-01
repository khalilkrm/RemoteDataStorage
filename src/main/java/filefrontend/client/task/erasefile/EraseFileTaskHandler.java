package filefrontend.client.task.erasefile;

import filefrontend.client.StorBackEnd;
import utils.observer.EventType;
import utils.task.BaseTaskHandler;
import utils.task.Status;
import utils.task.Task;

import java.util.regex.Pattern;

public class EraseFileTaskHandler extends BaseTaskHandler {

    private final Pattern errorPattern = Pattern.compile("^ERASE_ERROR\r?\n?$");
    private final Pattern okPattern = Pattern.compile("^ERASE_OK\r?\n?$");

    @Override
    protected boolean canCare(final Task task) {
        return task.getType().equals(EventType.EraseFile);
    }

    @Override
    protected void care(final Task task, final StorBackEnd storBackEnd) {

        final String hashFileName = task.getSavedFilename();
        final String message = String.format("ERASEFILE %s\r\n", hashFileName);

        storBackEnd.send(message);
        final String answer = storBackEnd.receive();

        if(okPattern.matcher(answer).matches()) {
            task.setStatus(Status.FULFILLED);
        }else if(errorPattern.matcher(answer).matches()) {
            task.setStatus(Status.REJECTED);
        }else {
            task.setStatus(Status.REJECTED);
        }
    }
}
