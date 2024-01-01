package utils.message;

import storbackend.configuration.ConfigurationProvider;
import storbackend.sever.unicast.FFEHandler;

public class FFEBaseMessageHandler extends BaseMessageHandler<FFEHandler> {

    public FFEBaseMessageHandler(final ConfigurationProvider configuration) {
        super(configuration);
    }

    @Override
    protected boolean canCare(String message) {
        return false;
    }

    @Override
    protected void care(FFEHandler client) {
        // Noting
    }
}
