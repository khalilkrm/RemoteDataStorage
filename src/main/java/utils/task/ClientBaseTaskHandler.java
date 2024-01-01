package utils.task;

import filefrontend.client.StorBackEnd;

public class ClientBaseTaskHandler extends BaseTaskHandler {

    public TaskHandler next;

    @Override
    protected boolean canCare(Task task) {
        return false;
    }

    @Override
    protected void care(final Task task, final StorBackEnd storBackEnd) {
        // Nothing
    }
}
