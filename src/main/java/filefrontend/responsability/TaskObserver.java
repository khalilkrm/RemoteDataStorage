package filefrontend.responsability;

import utils.task.Task;

public interface TaskObserver {

    void notifyStatusChanges(final Task task);

}
