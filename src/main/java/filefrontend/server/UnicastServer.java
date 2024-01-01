package filefrontend.server;

import filefrontend.repository.Repository;
import utils.message.BaseMessageHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Unicast server. Handles clients connections. Each accepted client is handled by a {@link ClientHandler}
 * */
public class UnicastServer {

    private boolean stop = false;
    private final int port;
    private final BaseMessageHandler<ClientHandler> messageHandler;

    private final static Logger LOGGER = Logger.getLogger(UnicastServer.class.getSimpleName());
    private final Repository repository;

    public UnicastServer(final int port, final BaseMessageHandler<ClientHandler> messageHandler, final Repository repository) {
        this.port = port;
        this.repository = repository;
        this.messageHandler = messageHandler;
    }

    /**
     * Listen for client connections, when accept client start a {@link ClientHandler} thread
     * */
    public void listen() {
        try(final ServerSocket server = new ServerSocket(port)) {
            while(!stop) {
                final Socket socket = server.accept();
                new Thread(new ClientHandler(socket, messageHandler, repository)).start();
            }
        } catch (IOException e) {
            UnicastServer.LOGGER.log(Level.INFO, String.format("[%s] Client server interrupted abnormally: %s", Thread.currentThread().getName(), e.getMessage()));
        }
    }

    public void stop() {
        stop = true;
    }
}
