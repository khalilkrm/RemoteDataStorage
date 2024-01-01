package utils.task;

import filefrontend.client.StorBackEnd;

public interface TaskHandler {
    void handle(final Task task, StorBackEnd storBackEnd);
}
