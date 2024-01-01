package filefrontend.server.message.signin;

import filefrontend.server.ClientHandler;
import utils.message.MessageAnalyser;
import utils.message.BaseMessageHandler;

import java.util.logging.Level;
import java.util.logging.Logger;

public class SignInMessageHandler extends BaseMessageHandler<ClientHandler> {

    private final MessageAnalyser messageAnalyser;
    private static final Logger LOGGER = Logger.getLogger(SignInMessageHandler.class.getSimpleName());

    public SignInMessageHandler(final MessageAnalyser messageAnalyser) {
        super();
        this.messageAnalyser = messageAnalyser;
    }

    @Override
    protected boolean canCare(final String message) {
        return messageAnalyser.analyse(message);
    }

    @Override
    protected void care(final ClientHandler client) {
        // Is login exists and password correct
        final String login = messageAnalyser.get(SignInMessageProperties.LOGIN);
        final String password = messageAnalyser.get(SignInMessageProperties.PASSWORD);
        final boolean authenticated = configuration.getReadInstance().authenticateClient(login, password);
        LOGGER.log(Level.INFO, String.format("[%s] %s analysing password: %s", Thread.currentThread().getName(), this.getClass().getName(), authenticated));

        // set the log in state in order to accept client for double auth confirmation
        if(authenticated && configuration.getWriteInstance().setClientPendingConnexion(login, true)) {
            succeed(client);
        } else {
            fail(client);
        }
    }

    private void fail(final ClientHandler clientHandler) {
        LOGGER.log(Level.INFO, String.format("[%s] %s sending SIGN_ERROR", Thread.currentThread().getName(), this.getClass().getName()));
        clientHandler.send("SIGN_ERROR\r\n");
        clientHandler.stop();
    }

    private void succeed(final ClientHandler clientHandler) {
        LOGGER.log(Level.INFO, String.format("[%s] %s sending SIGN_IN_OK", Thread.currentThread().getName(), this.getClass().getName()));
        clientHandler.send("SIGN_IN_OK\r\n");
    }
}
