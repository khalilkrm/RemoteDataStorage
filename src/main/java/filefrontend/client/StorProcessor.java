package filefrontend.client;

import filefrontend.client.exception.StorProcessorConnectionInterruptedException;
import filefrontend.client.exception.StorProcessorMessageReceptionException;
import filefrontend.repository.Repository;
import utils.task.Task;
import utils.task.TaskHandler;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handle FileFrontEnd communication with a StorBackEnd
 * */
public class StorProcessor implements Runnable, StorBackEnd, TaskResolver {

    private static final Logger LOGGER = Logger.getLogger(StorProcessor.class.getSimpleName());

    private final Socket socket;
    private final TaskHandler handler;
    private final BlockingQueue<Task> tasks;
    private final Repository repository;
    private volatile boolean stop = false;

    private BufferedReader reader;
    private PrintWriter writer;
    private OutputStream out;
    private InputStream in;

    private String domain;

    public StorProcessor(final Socket socket, final TaskHandler taskHandler, final Repository repository) {
        this.socket = socket;
        this.tasks = new LinkedBlockingQueue<>();
        this.handler = taskHandler;
        this.repository = repository;
        tryInstantiateReaderFromSocket(socket, throwable ->
                LOGGER.log(Level.SEVERE, String.format("[%s] Buffer reader could not be created: %s", Thread.currentThread().getName(), throwable.getMessage())));
        tryInstantiateWriterFromSocket(socket, throwable ->
                LOGGER.log(Level.SEVERE, String.format("[%s] Buffer writer could not be created: %s", Thread.currentThread().getName(), throwable.getMessage())));
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getDomain() {
        return domain;
    }

    /* -------------- TASK RESOLVER SECTION -------------- */

    /**
     * Add a task to queue
     * @param task the task to offer
     */
    public void resolve(final Task task) {
        try {
            task.setSbeId(domain);
            tasks.put(task);
            LOGGER.log(Level.INFO, String.format("[%s] %s Task received: %s", Thread.currentThread().getName(), domain, task.getType()));
        } catch (final InterruptedException e) {
            StorProcessor.LOGGER.log(Level.SEVERE, String.format("[%s] Task offering failed: %s", Thread.currentThread().getName(), e.getMessage()));
        }
    }

    @Override
    public boolean isStopped() {
        return stop;
    }

    /* -------------- STORBACKEND SECTION -------------- */

    /**
     * Blocks until receives a message.
     *
     * @throws StorProcessorConnectionInterruptedException if the connection is no longer reachable
     * @throws StorProcessorMessageReceptionException if the message could not be receipted
     * */
    @Override
    public String receive() {
        try {
            final String message = reader.readLine();
            if(message != null) {
                LOGGER.log(Level.INFO, String.format("[%s] %s Message received: %s", Thread.currentThread().getName(), domain, message));
                return message;
            } else {
                stop();
                LOGGER.log(Level.SEVERE, String.format("The connection has been interrupted: %s", domain));
                throw new StorProcessorConnectionInterruptedException("The connection has been interrupted");
            }
        } catch (final IOException e) {
            LOGGER.log(Level.SEVERE, String.format("Could not receive message: %s", domain));
            stop();
            throw new StorProcessorMessageReceptionException("Could not receive message", e);
        }
    }


    /**
     * @param message the message to send
     */
    @Override
    public void send(final String message) {
        writer.write(message);
        writer.flush();
    }

    @Override
    public boolean sendFile(final String filename) {
        return repository.writeFileContentToOutputStream(out, filename);
    }

    @Override
    public boolean receiveFile(final String login, final String filename, final long filesize) {
        return repository.saveInputContentToNewFile(in, login, filename, filesize);
    }

    /* -------------- RUNNABLE SECTION -------------- */

    @Override
    public void run() {
        while (!stop) {
            try {
                final Task task = tasks.take();
                LOGGER.log(Level.INFO, "Task took");
                handler.handle(task, this);
            } catch (final InterruptedException e) {
                LOGGER.log(Level.SEVERE, String.format("Task watching interrupted: %s", domain));
            }
        }
    }

    /* -------------- FUNCTIONS -------------- */

    private void tryInstantiateReaderFromSocket(final Socket socket, final Consumer<Throwable> onException) {
        try {
            this.in = socket.getInputStream();
            this.reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
        } catch (final IOException ioe) {
            onException.accept(ioe);
            close();
        }
    }

    private void tryInstantiateWriterFromSocket(final Socket socket, final Consumer<Throwable> onException) {
        try {
            this.out = socket.getOutputStream();
            this.writer = new PrintWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8), true);
        } catch (final IOException ioe) {
            onException.accept(ioe);
            close();
        }
    }

    private void close() {
        try {
            socket.close();
        } catch (final IOException ioe) {
            throw new IllegalStateException("Could not close client socket", ioe);
        }
    }

    private void stop() {
        stop = true;
        LOGGER.log(Level.SEVERE, String.format("sbe closed: %s", domain));
        try {
            socket.close();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, String.format("Could not close sbe socket %s", domain));
        }
    }
}
