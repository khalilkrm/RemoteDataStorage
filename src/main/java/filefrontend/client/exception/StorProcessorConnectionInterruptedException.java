package filefrontend.client.exception;

public class StorProcessorConnectionInterruptedException extends RuntimeException {
    private static final long serialVersionUID = -5409594210075033119L;

    public StorProcessorConnectionInterruptedException(final String message) {
        super(message);
    }

    public StorProcessorConnectionInterruptedException(final String message, final Exception cause) {
        super(message, cause);
    }
}
