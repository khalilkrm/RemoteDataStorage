package storbackend.sever.unicast;

import storbackend.repository.Repository;
import utils.message.MessageHandler;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FFEHandler implements Runnable {

    private final Socket socket;
    private final MessageHandler<FFEHandler> messageHandler;
    private boolean stop = false;

    private final static Logger LOGGER = Logger.getLogger(FFEHandler.class.getSimpleName());


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

    private final Repository repository;


    public FFEHandler(final Socket socket,
                      final MessageHandler<FFEHandler> messageHandler,
                      final Repository repository) {
        this.socket = socket;
        this.messageHandler = messageHandler;
        this.repository = repository;
        tryInstantiateReaderFromSocket(socket, throwable ->
                LOGGER.log(Level.SEVERE, String.format("[%s] Buffer reader could not be created: %s", Thread.currentThread().getName(), throwable.getMessage())));
        tryInstantiateWriterFromSocket(socket, throwable ->
                LOGGER.log(Level.SEVERE, String.format("[%s] Buffer writer could not be created: %s", Thread.currentThread().getName(), throwable.getMessage())));
    }

    public void receive() {
        try {
            reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            while (!stop) {
                final String message = reader.readLine();
                if(message != null) {
                    LOGGER.log(Level.INFO, String.format("Message received : %s", message));
                    messageHandler.handle(message, this);
                } else stop();
            }
        } catch (final IOException ioe){
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

    public boolean storeFile(final String newFilename, final long filesize, final String fingerprint) {
        return repository.saveFile(in, newFilename, filesize, fingerprint);
    }

    public boolean sendFile(final String filename) {
        return repository.sendFile(out, filename);
    }

    public boolean hasFile(String filename) {
        return repository.fileExists(filename);
    }

    public boolean deleteFile(String filename) {
        return repository.deleteFile(filename);
    }

    /**
     * Stops for listening client messages
     */
    public void stop() {
        stop = true;
        close();
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
}
