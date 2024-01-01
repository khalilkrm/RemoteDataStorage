package filefrontend.exception;

public class StorProcessorNotAvailableException extends RuntimeException {
    private static final long serialVersionUID = -3929178990049372210L;

    public StorProcessorNotAvailableException(final String message) {
        super(message);
    }
}
