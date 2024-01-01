package filefrontend.server.message.signcode;

import filefrontend.server.ClientHandler;
import filefrontend.server.message.signin.SignInMessageHandler;
import utils.authenticator.TokenGenerator;
import utils.message.BaseMessageHandler;
import utils.message.MessageAnalyser;

import java.util.logging.Level;
import java.util.logging.Logger;

public class SignCodeMessageHandler extends BaseMessageHandler<ClientHandler> {
    private final MessageAnalyser messageAnalyser;
    private static final Logger LOGGER = Logger.getLogger(SignInMessageHandler.class.getSimpleName());

    public SignCodeMessageHandler(final MessageAnalyser messageAnalyser) {
        super();
        this.messageAnalyser = messageAnalyser;
    }

    @Override
    protected boolean canCare(final String message) {
        return messageAnalyser.analyse(message);
    }

    @Override
    protected void care(final ClientHandler client) {

        final String login = messageAnalyser.get(SignCodeMessageProperties.LOGIN);
        final boolean isClientHasPendingConnexion = configuration.getReadInstance().isClientHasPendingConnexion(login);

        if(!isClientHasPendingConnexion) {
            fail(client);
            return;
        }

        final String token = configuration.getReadInstance().getClientToken(login);
        final String code = messageAnalyser.get(SignCodeMessageProperties.CODE);
        boolean isCodeCorrect = TokenGenerator.getTOTPCode(token).equals(code);
        boolean isClientAdded = controller.addClientHandler(login, client);
        LOGGER.log(Level.INFO, String.format("[%s] %s analysing code : %s ClientAdded : %s", Thread.currentThread().getName(), this.getClass().getName(), isCodeCorrect, isClientAdded));

        if(isCodeCorrect && isClientAdded ) {
            configuration.getWriteInstance().setClientPendingConnexion(login, false);
            client.setLogin(login);
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
        LOGGER.log(Level.INFO, String.format("[%s] %s sending SIGN_CODE_OK", Thread.currentThread().getName(), this.getClass().getName()));
        clientHandler.send("SIGN_CODE_OK\r\n");
    }
}
