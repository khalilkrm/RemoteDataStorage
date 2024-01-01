package utils.message;


public class MessageAnalyserException extends RuntimeException {

    private static final long serialVersionUID = -7837956792587326950L;

    public MessageAnalyserException() {
        super("Run analyse first, if the analyse failed you could not get fields. Make sure the property is set in the analyse method.");
    }

    public MessageAnalyserException(final String message) {
        super(message);
    }

}
