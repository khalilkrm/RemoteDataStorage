package filefrontend.client.exception;

public class StorProcessorMessageReceptionException extends RuntimeException {
    private static final long serialVersionUID = -2848844199862112528L;

    public StorProcessorMessageReceptionException(final String message) {
        super(message);
    }

    public StorProcessorMessageReceptionException(final String message, final Exception cause) {
        super(message, cause);
    }
}
