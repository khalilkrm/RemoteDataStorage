package filefrontend.server.message.signup;

import filefrontend.server.ClientHandler;
import utils.message.MessageAnalyser;
import filefrontend.domain.Client;
import utils.message.BaseMessageHandler;

import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SignUpMessageHandler extends BaseMessageHandler<ClientHandler> {

    private final static Logger LOGGER = Logger.getLogger(SignUpMessageHandler.class.getSimpleName());
    private final MessageAnalyser messageAnalyser;

    public SignUpMessageHandler(final MessageAnalyser messageAnalyser) {
        super();
        this.messageAnalyser = messageAnalyser;
    }

    @Override
    protected boolean canCare(final String message) {
        return messageAnalyser.analyse(message);
    }

    @Override
    protected void care(final ClientHandler clientHandler) {

        LOGGER.log(Level.INFO, String.format("[%s] Sign Up process", Thread.currentThread().getName()));

        final String login = messageAnalyser.get(SignUpMessageProperties.LOGIN);
        final String password = messageAnalyser.get(SignUpMessageProperties.PASSWORD);

        if(configuration.getReadInstance().isLoginExist(login)) {
            LOGGER.log(Level.INFO, String.format("[%s] Login exists", Thread.currentThread().getName()));
            if(configuration.getReadInstance().authenticateClient(login, password) && configuration.getReadInstance().isClientHasPendingConnexion(login)) {
                LOGGER.log(Level.INFO, String.format("[%s] Pending connexion", Thread.currentThread().getName()));
                final String token = configuration.getReadInstance().getClientToken(login);
                succeed(clientHandler, token);
            } else {
                LOGGER.log(Level.INFO, String.format("[%s] client already exists", Thread.currentThread().getName()));
                fail(clientHandler);
            }
            return;
        }

        LOGGER.log(Level.INFO, String.format("[%s] New clients", Thread.currentThread().getName()));

        // New client

        String aes = null;

        // Create an aes key
        try {
            aes = configuration.getReadInstance().secretKeyToString(configuration.getReadInstance().getRandomSecretKey());
        } catch (NoSuchAlgorithmException e) {
            fail(clientHandler);
            return;
        }

        // Create token for double auth factor
        final String token = configuration.getReadInstance().generateRandomToken();

        final String salt = configuration.getReadInstance().getRandomSalt();

        // Hash the password
        final String hashedPassword = configuration.getReadInstance().hashTheClientPassword(password, salt);

        // Create the client
        final Client newClient = new Client(login, hashedPassword, salt, aes, token, Collections.emptyList());

        // Signup the client
        final boolean signed = configuration.getWriteInstance().addClient(newClient);

        if(signed && configuration.getWriteInstance().setClientPendingConnexion(login, true)) {
            LOGGER.log(Level.INFO, String.format("[%s] SignUp succeed", Thread.currentThread().getName()));
            succeed(clientHandler, token);
        } else {
            LOGGER.log(Level.INFO, String.format("[%s] SignUp failed", Thread.currentThread().getName()));
            fail(clientHandler);
        }
    }

    private void fail(final ClientHandler clientHandler) {
        clientHandler.send("SIGN_ERROR\r\n");
        clientHandler.stop();
    }

    private void succeed(final ClientHandler clientHandler, final String token) {
        clientHandler.send(String.format("SIGN_OK %s\r\n", token));
    }
}
