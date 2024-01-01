package filefrontend.exception;

public class StorProcessorNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 3408973825169295629L;

    public StorProcessorNotFoundException(final String message) {
        super(message);
    }
}
