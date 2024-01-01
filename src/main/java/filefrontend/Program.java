package filefrontend;

import filefrontend.announcement.AnnouncementMessageAnalyser;
import filefrontend.client.MulticastClient;
import filefrontend.client.task.erasefile.EraseFileTaskHandler;
import filefrontend.client.task.retrievefile.RetrieveFileTaskHandler;
import filefrontend.client.task.sendfile.SendFileTaskHandler;
import filefrontend.configuration.ConfigurationProvider;
import filefrontend.server.ClientHandler;
import filefrontend.server.UnicastServer;
import filefrontend.server.message.filelist.FileListMessageAnalyser;
import filefrontend.server.message.filelist.FileListMessageHandler;
import filefrontend.server.message.getfile.GetFileMessageAnalyser;
import filefrontend.server.message.getfile.GetFileMessageHandler;
import filefrontend.server.message.removefile.RemoveFileMessageAnalyser;
import filefrontend.server.message.removefile.RemoveFileMessageHandler;
import filefrontend.server.message.savefile.SaveFileMessageAnalyser;
import filefrontend.server.message.savefile.SaveFileMessageHandler;
import filefrontend.server.message.signcode.SignCodeMessageAnalyser;
import filefrontend.server.message.signcode.SignCodeMessageHandler;
import filefrontend.server.message.signin.SignInMessageAnalyser;
import filefrontend.server.message.signin.SignInMessageHandler;
import filefrontend.server.message.signout.SignOutMessageAnalyser;
import filefrontend.server.message.signout.SignOutMessageHandler;
import filefrontend.server.message.signup.SignUpMessageAnalyser;
import filefrontend.server.message.signup.SignUpMessageHandler;
import utils.file.FileReceiver;
import filefrontend.repository.FileRepository;
import utils.file.FileSender;
import filefrontend.repository.Repository;
import filefrontend.configuration.ConfigurationReader;
import filefrontend.configuration.BlockingConfigurationProvider;
import filefrontend.configuration.Configuration;
import utils.message.BaseMessageHandler;
import utils.message.ClientBaseMessageHandler;
import utils.network.NetChooser;
import utils.security.Encryptor;
import utils.task.BaseTaskHandler;
import utils.task.ClientBaseTaskHandler;
import utils.task.TaskHandler;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.nio.file.Path;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Program {

    private static final Logger LOGGER = Logger.getLogger(Program.class.getSimpleName());

    private static final String MULTICAST_ADDRESS = "224.66.66.1";
    private static final int UNICAST_PORT = 15507;
    private static final int MULTICAST_PORT = 15508;
    private static final Path CONFIGURATION_PATH = Path.of("src", "main", "resources", "configuration.json");
    private static final Path REPOSITORY_PATH = Path.of(CONFIGURATION_PATH.toAbsolutePath().getRoot().toString(), "repository");

    private static final Configuration DEFAULT_CONFIG = new Configuration(MULTICAST_PORT, MULTICAST_ADDRESS, REPOSITORY_PATH.toAbsolutePath().toString(), CONFIGURATION_PATH.toAbsolutePath().toString(), Collections.emptyList());

    public static void main(String[] args) {
        try {

            final ConfigurationProvider configurationProvider = BlockingConfigurationProvider.from(ConfigurationReader.read(CONFIGURATION_PATH, DEFAULT_CONFIG), CONFIGURATION_PATH);

            final Repository repository = new FileRepository(new FileSender(), new FileReceiver(), new Encryptor(), configurationProvider.getReadInstance().getRepositoryPath());

            final Core core = new Core();

            final MulticastSocket multicast = new MulticastSocket(MULTICAST_PORT);
            multicast.setNetworkInterface(new NetChooser().getNetworkInterface());

            final BaseTaskHandler taskHandler = new ClientBaseTaskHandler();

            taskHandler.setNext(new EraseFileTaskHandler()).setNext(new SendFileTaskHandler()).setNext(new RetrieveFileTaskHandler());

            final MulticastClient client = new MulticastClient(multicast, new AnnouncementMessageAnalyser(), core, taskHandler, repository);

            new Thread(() -> {
                try {
                    client.joinGroup(new InetSocketAddress(InetAddress.getByName(MULTICAST_ADDRESS), MULTICAST_PORT), null);
                } catch (final IOException e) {
                    e.printStackTrace();
                }
            }).start();

            final BaseMessageHandler<ClientHandler> headHandler = new ClientBaseMessageHandler(core, configurationProvider);

            headHandler.setNext(new FileListMessageHandler(new FileListMessageAnalyser()))
                    .setNext(new GetFileMessageHandler(new GetFileMessageAnalyser()))
                    .setNext(new RemoveFileMessageHandler(new RemoveFileMessageAnalyser()))
                    .setNext(new SignInMessageHandler(new SignInMessageAnalyser()))
                    .setNext(new SaveFileMessageHandler(new SaveFileMessageAnalyser()))
                    .setNext(new SignOutMessageHandler(new SignOutMessageAnalyser()))
                    .setNext(new SignUpMessageHandler(new SignUpMessageAnalyser()))
                    .setNext(new SignCodeMessageHandler(new SignCodeMessageAnalyser()))
                    .setNext(new BaseMessageHandler<>() {
                        @Override
                        protected boolean canCare(final String message) {
                            LOGGER.log(Level.SEVERE, String.format("Reached end of handlers with message: %s", message));
                            return true;
                        }

                        @Override
                        protected void care(ClientHandler client) {
                            client.send("ERROR");
                        }
                    });

            final UnicastServer server = new UnicastServer(UNICAST_PORT, headHandler, repository);

            server.listen();

            // TODO When reach this section of code stop unicast server, clients, multicast listening and stopprocessors

            server.stop();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
