package filefrontend.server.message.signout;

import filefrontend.server.ClientHandler;
import utils.message.MessageAnalyser;
import utils.message.BaseMessageHandler;

import java.util.logging.Level;
import java.util.logging.Logger;

public class SignOutMessageHandler extends BaseMessageHandler<ClientHandler> {

    private static final Logger LOGGER = Logger.getLogger(SignOutMessageHandler.class.getSimpleName());
    private final MessageAnalyser messageAnalyser;

    public SignOutMessageHandler(final MessageAnalyser messageAnalyser) {
        super();
        this.messageAnalyser = messageAnalyser;
    }

    @Override
    protected boolean canCare(final String message) {
        return messageAnalyser.analyse(message);
    }

    @Override
    protected void care(final ClientHandler client) {
        LOGGER.log(Level.INFO, String.format("Sign Out remove client: %s", client.getLogin()));
        controller.removeClientHandler(client);
    }
}
