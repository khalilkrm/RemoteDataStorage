package utils.message;

import filefrontend.Core;
import filefrontend.configuration.ConfigurationProvider;
import filefrontend.server.ClientHandler;
import filefrontend.configuration.Configuration;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientBaseMessageHandler extends BaseMessageHandler<ClientHandler> {

    public ClientBaseMessageHandler(final Core core, final ConfigurationProvider configuration) {
        super(core, configuration);
    }

    @Override
    protected boolean canCare(final String message) {
        return false;
    }

    @Override
    protected void care(final ClientHandler client) {
        /* NOTHING */
    }
}
