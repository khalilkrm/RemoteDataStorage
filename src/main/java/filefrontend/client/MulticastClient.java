package filefrontend.client;

import filefrontend.Core;
import filefrontend.announcement.AnnouncementMessageProperties;
import filefrontend.server.ClientHandler;
import filefrontend.repository.Repository;
import utils.message.MessageAnalyser;
import utils.task.TaskHandler;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Listen for messages in a given group,
 * each message is analysed if it turns out that the message
 * is an announcement from a sbe then the port is extract from it and start a thread {@link StorProcessor}
 * who will listen for tasks he could receive from a {@link ClientHandler}
 * */
public class MulticastClient {

    private final byte[] buf = new byte[1024];
    private final MulticastSocket multicastSocket;
    private final MessageAnalyser messageAnalyser;
    private final TaskHandler taskHandler;
    private final Core controller;
    private final Repository repository;

    private final static Logger LOGGER = Logger.getLogger(MulticastClient.class.getSimpleName());

    /**
     * @param multicastSocket the multicast socket already bound to a port
     * @param repository
     */
    public MulticastClient(
            final MulticastSocket multicastSocket,
            final MessageAnalyser messageAnalyser,
            final Core core,
            final TaskHandler taskHandler,
            final Repository repository) {
        this.multicastSocket = multicastSocket;
        this.messageAnalyser = messageAnalyser;
        this.controller = core;
        this.taskHandler = taskHandler;
        this.repository = repository;
    }

    /**
     * @param mcastaddr {@link MulticastSocket#joinGroup(SocketAddress, NetworkInterface)}
     * @param netIf {@link MulticastSocket#joinGroup(SocketAddress, NetworkInterface)}
     *
     * Start a listening for multicast messages from <code>mcastaddr</code>,
     * in this business application context each message is called an <code>announce</code>.
     * After the announcement a {@link StorProcessor} is started
     *
     * @see MulticastSocket#joinGroup(SocketAddress, NetworkInterface)
     * */
    public void joinGroup(final SocketAddress mcastaddr, final NetworkInterface netIf) throws IOException {
        multicastSocket.joinGroup(mcastaddr, netIf);
        while (!multicastSocket.isClosed()) {
            final DatagramPacket packet = new DatagramPacket(buf, buf.length);

            multicastSocket.receive(packet);
            final String announce = new String(
                    packet.getData(),
                    packet.getOffset(),
                    packet.getLength(),
                    StandardCharsets.UTF_8);

            if(canCare(announce))
                connect(packet.getAddress().getHostAddress(),
                        Integer.parseInt(messageAnalyser.get(AnnouncementMessageProperties.PORT)));
        }
    }

    private boolean canCare(final String announce) {
        return messageAnalyser.analyse(announce);
    }

    /**
     * Connect to a server after his announcement
     * */
    public void connect(final String address, final int port) {

        final String domain = messageAnalyser.get(AnnouncementMessageProperties.DOMAIN);

        if(!controller.storProcessorExist(domain)) {
            try {
                final Socket socket = new Socket(address, port);
                final StorProcessor processor = new StorProcessor(socket, taskHandler, repository);
                processor.setDomain(domain);
                controller.addTaskResolver(domain, processor);
                (new Thread(processor)).start();
            } catch (final IOException ioe) {
                MulticastClient.LOGGER.log(Level.SEVERE, String.format("[%s] Could not connect to a SBE: %s", Thread.currentThread().getName(), ioe.getMessage()));
            }
        }
    }
}