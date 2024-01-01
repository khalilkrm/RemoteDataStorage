package storbackend.sever.unicast;

import storbackend.repository.Repository;
import utils.message.MessageHandler;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UnicastServer {

    private final static Logger LOGGER = Logger.getLogger(UnicastServer.class.getSimpleName());
    private boolean stop = false;
    private final int port;

    private final MessageHandler<FFEHandler> handler;
    private final Repository repository;

    public UnicastServer(final int port, final MessageHandler<FFEHandler> handler, final Repository repository) {
        this.port = port;
        this.handler = handler;
        this.repository = repository;
    }

    public void listen() {
        try(final ServerSocket server = new ServerSocket(port)) {
            while(!stop) {
                final Socket socket = server.accept();
                LOGGER.log(Level.INFO, "FFE accepted");
                new Thread(new FFEHandler(socket , handler, repository)).start();
            }
        } catch (IOException e) {
            stop = false;
            UnicastServer.LOGGER.log(Level.INFO, String.format("[%s] Client server interrupted abnormally: %s", Thread.currentThread().getName(), e.getMessage()));
        }
    }

    public void stop() {
        stop = true;
    }
}