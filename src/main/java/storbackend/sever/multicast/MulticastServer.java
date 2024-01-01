package storbackend.sever.multicast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MulticastServer implements Runnable {

    private final InetAddress group;
    private final DatagramSocket socket;
    private final int port;

    private String message;

    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> future;

    private final static Logger LOGGER = Logger.getLogger(MulticastServer.class.getSimpleName());

    public MulticastServer(final DatagramSocket socket, final InetAddress group, final int port) {
        this.socket = socket;
        this.group = group;
        this.port = port;
    }

    public void multicast(final String message, final int period) {
        this.message = message;
        future = executor.scheduleAtFixedRate(this, 0, period, TimeUnit.SECONDS);
    }

    @Override
    public void run() {
        try {
            final byte[] bytes = message.getBytes(StandardCharsets.UTF_8);
            final DatagramPacket packet = new DatagramPacket(bytes, bytes.length, group, port);
            socket.send(packet);
            LOGGER.log(Level.INFO, String.format("[%s] Announce sent", Thread.currentThread().getName()));
        } catch (final IOException e) {
            LOGGER.log(Level.SEVERE, String.format("Could not multicast to %s in port %d", group.getHostAddress(), port));
            future.cancel(false);
        }
    }
}

