package filefrontend.server.message.filelist;

import filefrontend.server.ClientHandler;
import utils.message.MessageAnalyser;
import filefrontend.domain.exceptions.ClientNotFoundException;
import filefrontend.domain.exceptions.FileNotFoundException;
import utils.message.BaseMessageHandler;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class FileListMessageHandler extends BaseMessageHandler<ClientHandler> {

    private final MessageAnalyser messageAnalyser;

    private static final Logger LOGGER = Logger.getLogger(FileListMessageHandler.class.getSimpleName());

    public FileListMessageHandler(final MessageAnalyser messageAnalyser) {
        super();
        this.messageAnalyser = messageAnalyser;
    }

    @Override
    protected boolean canCare(final String message) {
        return messageAnalyser.analyse(message);
    }

    @Override
    protected void care(final ClientHandler clientHandler) {

        if(!controller.isClientConnected(clientHandler.getLogin())) {
            sendError(clientHandler);
            return;
        }

        final String login = clientHandler.getLogin();

        try {
            final List<String> filesName = configuration.getReadInstance().getClientFilesName(login);

            if(filesName.size() == 0) {
                sendEmpty(clientHandler);
                return;
            }

            sendFiles(clientHandler, filesName.stream().collect(
                    LinkedHashMap::new,
                    (map, name) -> {
                        try {
                            map.put(name, configuration.getReadInstance().getClientFileSize(login, name));
                        } catch (final FileNotFoundException | ClientNotFoundException e) {
                            FileListMessageHandler.LOGGER.log(Level.WARNING, e.getMessage());
                        }
                    },
                    Map::putAll));
        } catch (final ClientNotFoundException ex) {
            sendError(clientHandler);
        }
    }

    private void sendError(final ClientHandler clientHandler) {
        LOGGER.log(Level.INFO, "Sending empty files due to error");
        clientHandler.send("FILES\r\n");
    }

    private void sendEmpty(final ClientHandler clientHandler) {
        LOGGER.log(Level.INFO, "Sending empty files");
        clientHandler.send("FILES\r\n");
    }

    private void sendFiles(final ClientHandler clientHandler, final Map<String, Long> arguments) {
        LOGGER.log(Level.INFO, "Sending files");

        final String files = arguments.entrySet().stream().map(stringLongEntry ->
                String.format("%s!%d", stringLongEntry.getKey(), stringLongEntry.getValue()))
                .collect(Collectors.joining(" "));

        clientHandler.send(String.format("FILES %s\r\n", files));
    }
}
