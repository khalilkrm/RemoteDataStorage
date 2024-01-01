package filefrontend.configuration;

import com.google.gson.Gson;
import utils.authenticator.TokenGenerator;
import filefrontend.domain.Client;
import filefrontend.domain.File;
import filefrontend.domain.exceptions.ClientNotFoundException;
import filefrontend.domain.exceptions.FileNotFoundException;
import utils.security.EncryptionUtils;
import utils.security.OneWayEncryptionUtils;
import utils.security.TwoWayEncryptionUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Configuration {

    private static final Logger LOGGER = Logger.getLogger(Configuration.class.getSimpleName());
    private int port;
    private String address;
    protected Set<Client> clients;
    private String repositoryPath;
    private String path;

    /**
     * @param port the port used for multicast listening
     * @param address the address used for multicast listening
     * @param clients the known clients
     */
    public Configuration(final int port, final String address, final String repositoryPath, final String configurationPath, final List<Client> clients) {
        this.port = port;
        this.address = address;
        this.clients = new HashSet<>(clients);
        this.repositoryPath = repositoryPath;
        path = configurationPath;
    }

    public Configuration() {}

    /* ------ METHODS ------*/

    /* ---------------- READ ----------------*/

    /**
     * @param login the client login
     * @return true if the client was found and just signed in and waiting for double auth confirmation otherwise false
     */
    public boolean isClientHasPendingConnexion(final String login) {
        try {
            final Client found = findClientByLogin(login);
            return found.getSignState();
        } catch (final ClientNotFoundException ex) {
            return false;
        }
    }

    /**
     * @return the address used for multicast listening
     */
    public String getAddress() {
        return address;
    }


    public String getPath() {
        return path;
    }

    /**
     * @return the port used for multicast listening
     */
    public int getPort() {
        return port;
    }

    /**
     * @return the client's count
     */
    public int getClientsCount() {
        return clients.size();
    }


    /**
     * @return the repository path
     */
    public String getRepositoryPath() {
        return repositoryPath;
    }

    /**
     * @return all existing clients login
     * */
    public List<String> getClientsLogin() {
        return clients
                .stream()
                .map(Client::getLogin)
                .collect(Collectors.toList());
    }

    /**
     * @param login the clients login
     * @return all files name that the user with the given login own
     * @throws ClientNotFoundException if the login does not belong to any client
     * */
    public List<String> getClientFilesName(final String login) throws ClientNotFoundException {
        return getClientFilesName(findClientByLogin(login));
    }

    /**
     * @param login the client's login
     * @return the client aes
     * @throws ClientNotFoundException if the login does not belong to any client
     */
    public String getClientAes(final String login) throws ClientNotFoundException {
        return getAesFromClient(findClientByLogin(login));
    }

    /**
     * @param login the client's login
     * @return how many files own a client
     * @throws ClientNotFoundException if the login does not belong to any client
     */
    public int getClientFilesCount(final String login) throws ClientNotFoundException {
        return getFileCountFromClient(findClientByLogin(login));
    }

    /**
     * @param login the client's login
     * @param filesName the file's name
     * @return the holder of a file
     * @throws ClientNotFoundException if the login does not belong to any client
     * @throws FileNotFoundException if the client does not have any file with the given name
     */
    public String getFilesHolderFromClient(final String login, final String filesName) throws ClientNotFoundException, FileNotFoundException {
        return getHolderFromFile(findFileByName(findClientByLogin(login), filesName));
    }

    /**
     * @param login the client's login
     * @param fileName the file's name
     * @return the size of a filename that a user with the given login awn
     * @throws ClientNotFoundException if the login does not belong to any client
     * @throws FileNotFoundException if the client does not have any file with the given name
     */
    public long getClientFileSize(final String login, final String fileName) throws ClientNotFoundException, FileNotFoundException {
        return getSizeFromFile(findFileByName(findClientByLogin(login), fileName));
    }

    public String getClientToken(final String login) {
        return getClientToken(findClientByLogin(login));
    }

    public byte[] getFileIVFromClient(final String login, String filename) {
        return getIvFromFile(findFileByName(findClientByLogin(login), filename));
    }

    private byte[] getIvFromFile(File file) {
        return file.getIv().getBytes(StandardCharsets.UTF_8);
    }

    /**
     * @param login the client's login
     * @return true if the client exist otherwise false
     */
    public boolean isLoginExist(final String login) {
        try {
            findClientByLogin(login);
            return true;
        }catch (final ClientNotFoundException ex) {
            LOGGER.log(Level.INFO, String.format("Client not found %s", login));
            return false;
        }
    }

    /**
     * @param login the client's login
     * @param password password to test
     * @return true if the client was found and the password is correct otherwise false
     */
    public boolean authenticateClient(final String login, final String password) {
        try {
            final Client found = findClientByLogin(login);
            return found.isPasswordCorrect(password);
        }catch (final ClientNotFoundException ex) {
            return false;
        }
    }

    /**
     * @return the configuration on json format
     */
    @Override
    public String toString() {
        return new Gson().toJson(this, Configuration.class);
    }

    /* ---------------- WRITE ----------------*/

    /**
     * Set the client sign in state. When the client signed in we wait for double auth.
     *
     * @param login the user login of client that just signed in
     * @param state the state, set it to true if you wait for double auth otherwise if the double auth was confirmed set it to true
     * @return true if the login was found
     */
    public boolean setClientPendingConnexion(final String login, boolean state) {
        try {
            final Client found = findClientByLogin(login);
            found.setSignInState(state);
            ConfigurationWriter.write(this, Path.of(path));
            return true;
        } catch (final ClientNotFoundException ex) {
            LOGGER.log(Level.INFO, String.format("[%s] Could not set sign state: %s", Thread.currentThread().getName(), ex.getMessage()));
            return false;
        }
    }

    public boolean addClient(final Client client) {
        boolean added = clients.add(client);
        ConfigurationWriter.write(this, Path.of(path));
        return added;
    }

    public boolean addFileToClient(final String login, final File file) {
        boolean added = addFileToClient(findClientByLogin(login), file);
        ConfigurationWriter.write(this, Path.of(path));
        return added;
    }

    public boolean removeFileFromClient(final String login, final String filename) {
        boolean removed = removeFileFromClient(findClientByLogin(login), filename);
        ConfigurationWriter.write(this, Path.of(path));
        return removed;
    }

    /* ---------------- SECURITY ----------------*/

    public byte[] getRandomIV() {
        return EncryptionUtils.getRandomIV();
    }

    public SecretKey getRandomSecretKey() throws NoSuchAlgorithmException {
        return TwoWayEncryptionUtils.getRandomAesKey();
    }

    public SecretKey stringToSecretKey(final String secret) {
        return TwoWayEncryptionUtils.getSecretKeyFromString(secret);
    }

    public String secretKeyToString(final SecretKey key) {
        return TwoWayEncryptionUtils.secretKeyToString(key);
    }

    public String hashTheClientPassword(final String password, final String salt) {
        return OneWayEncryptionUtils.SHA384EncryptAsHex(password + salt);
    }

    public String generateRandomToken() {
        return TokenGenerator.generateSecretKey();
    }

    public String getRandomSalt() {
        return EncryptionUtils.getRandomSalt();
    }

    /* ------ FUNCTIONS ------*/

    private String getAesFromClient(final Client client) {
        return client.getAes();
    }

    private int getFileCountFromClient(final Client client) {
        return client.getFilesCount();
    }

    private String getHolderFromFile(final File file) {
        return file.getHolder();
    }

    private long getSizeFromFile(final File file) {
        return file.getSize();
    }

    private List<String> getClientFilesName(final Client client) {
        return client.getFilesName();
    }

    private String getClientToken(final Client client) {
        return client.getToken();
    }

    private boolean addFileToClient(final Client client, final File file) {
        return client.addFile(file);
    }

    private boolean removeFileFromClient(final Client client, final String file) {
        return client.removeFileByName(file);
    }

    private File findFileByName(final Client client, final String fileName) throws FileNotFoundException {
        File found = null;
        FileNotFoundException exception = null;
        try {
            found = client.getFileByName(fileName);
        } catch (final FileNotFoundException ex) {
            exception = new FileNotFoundException(String.format("Client with login %s does not own any file named %s", client.getLogin(), fileName));
        }
        if(exception != null)
            throw exception;
        else return found;
    }

    private Client findClientByLogin(final String login) throws ClientNotFoundException {
        final Client found = clients
                .stream()
                .filter(client -> client.getLogin().equals(login))
                .findFirst()
                .orElse(null);
        if(found == null)
            throw new ClientNotFoundException(String.format("Client with login %s not found", login));
        else return found;
    }
}
