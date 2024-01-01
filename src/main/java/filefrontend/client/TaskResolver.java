package filefrontend.client;

import utils.task.Task;

public interface TaskResolver {
    void resolve(final Task task);
    boolean isStopped();
}
