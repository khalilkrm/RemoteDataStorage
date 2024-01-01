package utils.message;

import filefrontend.configuration.ConfigurationProvider;
import filefrontend.responsability.TaskController;
import filefrontend.configuration.Configuration;

import java.util.logging.Level;
import java.util.logging.Logger;

abstract public class BaseMessageHandler<T> implements MessageHandler<T> {

    private static final Logger LOGGER = Logger.getLogger(BaseMessageHandler.class.getSimpleName());

    private BaseMessageHandler<T> next;

    protected TaskController controller;
    protected ConfigurationProvider configuration;
    public storbackend.configuration.ConfigurationProvider sbe_configuration;

    public BaseMessageHandler(final TaskController controller, final ConfigurationProvider configuration) {
        this.controller = controller;
        this.configuration = configuration;
    }

    public BaseMessageHandler(final storbackend.configuration.ConfigurationProvider configuration) {
        sbe_configuration = configuration;
    }

    public BaseMessageHandler() {
        this.configuration = null;
        this.controller = null;
    }

    protected abstract boolean canCare(final String message);
    protected abstract void care(final T client);

    public void handle(final String message, final T ffe) {
        if(canCare(message))
            care(ffe);
        else if(next != null) {
            next.setController(controller);
            next.setConfiguration(configuration);
            next.setSbeConfiguration(sbe_configuration);
            next.handle(message, ffe);
        } else {
            LOGGER.log(Level.INFO, String.format("[%s] end of handler reached with %s", Thread.currentThread().getName(), this.getClass().getName()));
        }
    }

    public BaseMessageHandler<T> setNext(final BaseMessageHandler<T> handler) {
        next = handler;
        return handler;
    }

    protected void setController(final TaskController core) {
        controller = core;
    }

    protected void setConfiguration(final ConfigurationProvider configuration) {
        this.configuration = configuration;
    }

    protected void setSbeConfiguration(final storbackend.configuration.ConfigurationProvider configuration) {
        this.sbe_configuration = configuration;
    }
}
