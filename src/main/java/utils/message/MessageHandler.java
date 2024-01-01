package utils.message;

public interface MessageHandler<T> {
    void handle(final String message, final T client);
}
