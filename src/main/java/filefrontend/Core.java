package filefrontend;

import filefrontend.client.MulticastClient;
import filefrontend.client.TaskResolver;
import filefrontend.exception.StorProcessorNotFoundException;
import filefrontend.server.ClientHandler;
import filefrontend.exception.StorProcessorNotAvailableException;
import filefrontend.responsability.AnnounceController;
import filefrontend.responsability.AnnounceListener;
import filefrontend.responsability.TaskController;
import utils.task.Status;
import utils.task.Task;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Core implements AnnounceController, TaskController, AnnounceListener {

    public static int LIFETIME = 3000;

    private final static Logger LOGGER = Logger.getLogger(MulticastClient.class.getSimpleName());

    private final ConcurrentHashMap<String, TaskResolver> storProcessors;
    private final ConcurrentHashMap<String, ClientHandler> clientHandlers;
    private final ConcurrentHashMap<String, LocalDateTime> heartBeats;

    public Core() {
        storProcessors = new ConcurrentHashMap<>();
        clientHandlers = new ConcurrentHashMap<>();
        heartBeats = new ConcurrentHashMap<>();
    }

    /* ---------- CLIENT SECTION ----------*/

    /**
     * Add a clientHandler
     *
     * @param login client's login
     * @param clientHandler the client handler runnable
     * @return true if the client was added successfully otherwise false
     */
    public boolean addClientHandler(final String login, final ClientHandler clientHandler) {
        boolean exist;
        if(!(exist = clientHandlers.containsKey(login)))
            doAddClientHandler(login, clientHandler);
        return !exist;
    }

    /**
     * Remove a clientHandler
     *
     * @param client the clientHandler to remove
     */
    @Override
    public void removeClientHandler(final ClientHandler client) {
        clientHandlers.remove(client.getLogin());
        client.stop();
    }

    /**
     * Push a task to StorProcessor
     * If the does not specify any StorProcessor then it's affected to a random one.
     *
     * @param task the task to push to a StorProcessor.
     *
     * @throws StorProcessorNotFoundException if the task specifies a storProcessor domain and that one was not found
     * @throws StorProcessorNotAvailableException if the task specifies a storProcessor and that one announced himself more than {@value LIFETIME} ago
     */
    @Override
    public void pushTask(final Task task) {
        final String sbeId = task.getSbeId();
        if(sbeId != null && !sbeId.isEmpty()) {
            if(!storProcessors.containsKey(sbeId)) {
                LOGGER.log(Level.SEVERE, String.format("Le StorProcessor avec le domain \"%s\" n'a pas été trouvé", sbeId));
                task.setStatus(Status.REJECTED);
            }else if (storProcessors.get(sbeId).isStopped()) {
                LOGGER.log(Level.SEVERE, String.format("Le StorProcessor avec le domain \"%s\" n'est pas disponible", sbeId));
            }
            final TaskResolver processor = storProcessors.get(sbeId);
            processor.resolve(task);
        } else {
            storProcessors
                    .values()
                    .stream()
                    .findFirst()
                    .ifPresentOrElse(taskResolver -> taskResolver.resolve(task), () -> task.setStatus(Status.REJECTED));
        }
    }

    public boolean isClientConnected(final String login) {
        return clientHandlers.containsKey(login);
    }

    /* ---------- ANNOUNCE SECTION ----------*/

    /**
     * @param domain the StorProcessor domain
     * @return true if the StorProcess announced himself at most {@value Core#LIFETIME} milliseconds ago
     */
    @Override
    @Deprecated
    public boolean isAlive(final String domain) {
        if(!heartBeats.containsKey(domain)) return false;
        final LocalDateTime from = heartBeats.get(domain);
        final LocalDateTime to = LocalDateTime.now();
        long duration = ChronoUnit.MILLIS.between(from, to);
        return duration < LIFETIME;
    }

    /* ---------- STORPROCESSOR SECTION ----------*/

    /**
     * Add a storProcessor
     *
     * @param id the storProcessor id
     * @param taskResolver the storProcessor runnable
     * @return true is the storProcessor was added successfully otherwise false
     */
    public boolean addTaskResolver(final String id, final TaskResolver taskResolver) {
        boolean exist;
        if(!(exist = storProcessors.containsKey(id))) {
            doAddTaskResolver(id, taskResolver);
            LOGGER.log(Level.INFO, String.format("SBE registered: %s", id));
        }
        return exist;
    }

    /**
     * @param domain the storprocessor domain
     * @return true if a StorProcessor with the given domain exist otherwise false
     */
    public boolean storProcessorExist(final String domain) {
        return storProcessors.containsKey(domain);
    }

    /* -------------- FUNCTIONS --------------*/

    synchronized private void doAddTaskResolver(final String id, final TaskResolver taskResolver) {
        storProcessors.put(id, taskResolver);
    }

    synchronized private void doAddClientHandler(final String login, final ClientHandler clientHandler) {
        clientHandlers.put(login, clientHandler);
    }
}
