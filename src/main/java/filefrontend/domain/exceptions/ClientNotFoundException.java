package filefrontend.domain.exceptions;

public class ClientNotFoundException extends RuntimeException {

    private static final long serialVersionUID = -7616158357004316213L;

    public ClientNotFoundException(final String message) {
        super(message);
    }

    public ClientNotFoundException(final String message, final Exception cause) {
        super(message, cause);
    }
}
