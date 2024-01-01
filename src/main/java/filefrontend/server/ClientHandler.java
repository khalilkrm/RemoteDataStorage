package filefrontend.server;

import filefrontend.repository.Repository;
import filefrontend.repository.SavedState;
import utils.message.BaseMessageHandler;

import javax.crypto.SecretKey;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handle a client communications with the FileFrontEnd.
 * */
public class ClientHandler implements Runnable {

    private final static Logger LOGGER = Logger.getLogger(ClientHandler.class.getSimpleName());

    /**
     * The socket client
     * */
    private final Socket socket;

    /**
     * The message handler who knows the nature of each message and those that can be handled
     * */
    private final BaseMessageHandler<ClientHandler> handler;

    /**
     * The repository to send and save files
     * */
    private final Repository repository;

    /**
     * The buffer where the client messages are received
     * */
    private BufferedReader reader;

    /**
     * The input stream of the client
     * */
    private InputStream in;

    /**
     * The buffer through which the client send messages
     * */
    private PrintWriter writer;

    /**
     * The output stream of this client
     * */
    private OutputStream out;

    /**
     * Thread guard. Represent the thread running state
     * */
    private boolean stop = false;

    /**
     * Client login. Could be set only one time.
     * */
    private String login = null;

    /**
     * @param socket The socket client
     * @param handler The message handler who knows the nature of each message and those that can be handled
     */
    public ClientHandler(
            final Socket socket,
            final BaseMessageHandler<ClientHandler> handler,
            final Repository repository) {
        this.socket = socket;
        this.repository = repository;
        this.handler = handler;
        tryInstantiateReaderFromSocket(socket, throwable ->
                LOGGER.log(Level.SEVERE, String.format("[%s] Buffer reader could not be created: %s", Thread.currentThread().getName(), throwable.getMessage())));
        tryInstantiateWriterFromSocket(socket, throwable ->
                LOGGER.log(Level.SEVERE, String.format("[%s] Buffer writer could not be created: %s", Thread.currentThread().getName(), throwable.getMessage())));
    }

    @Override
    public void run() {
        try {
            while (!stop) {
                final String message = reader.readLine();
                if(message != null) {
                    handler.handle(message, this);
                } else stop();
            }
        } catch (final IOException ioe) {
            LOGGER.log(Level.SEVERE, String.format("[%s] Client listening interrupted abnormally", Thread.currentThread().getName()));
        }
    }

    /**
     * @param message the message to send to the client
     */
    public void send(final String message) {
        writer.write(message);
        writer.flush();
    }

    public boolean sendFile(final String filename, final long filesize, final SecretKey key, final byte[] iv) {
        return repository.sendFileToClient(out, login, filename, filesize, key, iv);
    }

    public SavedState saveFile(final String filename, final long filesize, final SecretKey key, final byte[] iv) {
        return repository.saveFileFromClient(in, login, filename, filesize, key, iv);
    }

    /**
     * Stops for listening client messages
     */
    public void stop() {
        stop = true;
        LOGGER.log(Level.INFO, String.format("[%s] Client stoped: %s", Thread.currentThread().getName(), login));
        close();
    }

    /**
     * The login could be set only one time, if the login has been already set this method won't take effect
     *
     * @param login the client login
     */
    public void setLogin(final String login) {
        if(this.login == null)
            this.login = login;
    }

    /**
     * @return the client login
     * */
    public String getLogin() {
        return login;
    }

    /* ------------ FUNCTIONS ------------ */

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
        } catch (IOException ioe) {
            LOGGER.log(Level.SEVERE, String.format("[%s] Could not close socket client: %s", Thread.currentThread().getName(), ioe.getMessage()));
        }
    }

    public void removeFile(String savedFilename) {
        repository.removeFile(savedFilename);
    }
}