package filefrontend.client;

public interface StorBackEnd {
    String receive();
    void send(final String message);
    boolean sendFile(final String filename);
    boolean receiveFile(final String login, final String filename, final long filesize);
    String getDomain();
}
