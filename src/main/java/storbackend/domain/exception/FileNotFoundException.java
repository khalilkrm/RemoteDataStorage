
package storbackend.domain.exception;

public class FileNotFoundException extends RuntimeException {

    public FileNotFoundException(final String message) {
        super(message);
    }

    public FileNotFoundException(final String message, final Exception cause) {
        super(message, cause);
    }

}
