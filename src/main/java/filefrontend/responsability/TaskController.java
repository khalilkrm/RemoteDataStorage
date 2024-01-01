package filefrontend.responsability;

import filefrontend.client.TaskResolver;
import filefrontend.server.ClientHandler;
import utils.task.Task;

public interface TaskController {
    void pushTask(final Task task);
    boolean addTaskResolver(final String id, final TaskResolver taskResolver);
    boolean addClientHandler(final String login, final ClientHandler clientHandler);
    void removeClientHandler(ClientHandler client);
    boolean isClientConnected(final String login);
}
