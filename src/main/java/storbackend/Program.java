package storbackend;

import storbackend.configuration.BlockingConfigurationProvider;
import storbackend.configuration.Configuration;
import storbackend.configuration.ConfigurationProvider;
import storbackend.configuration.ConfigurationReader;
import storbackend.repository.FileRepository;
import storbackend.repository.Repository;
import storbackend.sever.multicast.MulticastServer;
import storbackend.sever.unicast.FFEHandler;
import storbackend.sever.unicast.UnicastServer;
import storbackend.sever.unicast.message.erase.EraseMessageAnalyser;
import storbackend.sever.unicast.message.erase.EraseMessageHandler;
import storbackend.sever.unicast.message.retrieve.RetrieveMessageAnalyser;
import storbackend.sever.unicast.message.retrieve.RetrieveMessageHandler;
import storbackend.sever.unicast.message.sendfile.SendFileMessageAnalyser;
import storbackend.sever.unicast.message.sendfile.SendFileMessageHandler;
import utils.file.FileReceiver;
import utils.file.FileSender;
import utils.message.BaseMessageHandler;
import utils.message.FFEBaseMessageHandler;
import utils.security.Encryptor;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.file.Path;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Program {

    private final static Logger LOGGER = Logger.getLogger(Program.class.getSimpleName());

    private static final String DOMAIN = "SuperSBE";
    private static final String MULTICAST_ADDRESS = "224.66.66.1";
    private static final int SBE_PORT = 15508;
    private static final Path REPOSITORY_PATH = Path.of(Path.of("src").toAbsolutePath().getRoot().toString(), "cloud");

    private static final String ANNOUNCE = String.format("HELLO %s %d\r\n", DOMAIN, SBE_PORT);
    private static final Path CONFIGURATION_PATH = Path.of("src", "main", "resources", String.format("%s_%s", DOMAIN, "configuration.json"));
    private static final Configuration DEFAULT_CONFIGURATION = new Configuration(Collections.emptyList(), REPOSITORY_PATH.toAbsolutePath().toString(), CONFIGURATION_PATH.toAbsolutePath().toString());

    public static void main(String[] args) {
        try {
            final ConfigurationProvider configurationProvider = BlockingConfigurationProvider.from(ConfigurationReader.read(CONFIGURATION_PATH, DEFAULT_CONFIGURATION), CONFIGURATION_PATH);

            final Repository repository = new FileRepository(new FileSender(), new FileReceiver(), new Encryptor(), configurationProvider.getReadInstance().getRepoPath());

            final InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
            final DatagramSocket datagramSocket = new DatagramSocket();
            final MulticastServer multicastServer = new MulticastServer(datagramSocket, group, SBE_PORT);
            multicastServer.multicast(ANNOUNCE, 30);

            LOGGER.log(Level.INFO, "Section after multicast");

            final BaseMessageHandler<FFEHandler> handlers = new FFEBaseMessageHandler(configurationProvider);

            handlers.setNext(new EraseMessageHandler(new EraseMessageAnalyser()))
                    .setNext(new RetrieveMessageHandler(new RetrieveMessageAnalyser()))
                    .setNext(new SendFileMessageHandler(new SendFileMessageAnalyser()))
                    .setNext(new BaseMessageHandler<>() {
                        @Override
                        protected boolean canCare(String message) {
                            return true;
                        }

                        @Override
                        protected void care(FFEHandler client) {
                            client.send("ERROR");
                        }
                    });

            final UnicastServer unicastServer = new UnicastServer(SBE_PORT, handlers, repository);

            unicastServer.listen();

            unicastServer.stop();

        } catch (final IOException e) {
            e.printStackTrace();
        }
    }
}
