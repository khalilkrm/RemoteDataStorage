package utils.task;

import filefrontend.client.StorBackEnd;

import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class BaseTaskHandler implements TaskHandler {

    private static final Logger LOGGER = Logger.getLogger(BaseTaskHandler.class.getSimpleName());

    private TaskHandler next;

    public BaseTaskHandler() {}

    protected abstract boolean canCare(final Task task);
    protected abstract void care(final Task task, final StorBackEnd storBackEnd);

    @Override
    public void handle(final Task task, final StorBackEnd storBackEnd) {
        if(canCare(task))
            care(task, storBackEnd);
        else if(next != null) {
            next.handle(task, storBackEnd);
        } else {
            LOGGER.log(Level.INFO, String.format("[%s] end of TASK handlers reached with %s", Thread.currentThread().getName(), this.getClass().getName()));
        }
    }

    public BaseTaskHandler setNext(final BaseTaskHandler next) {
        this.next = next;
        return next;
    }
}
