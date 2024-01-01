package filefrontend.domain;

import com.google.gson.Gson;
import filefrontend.domain.exceptions.FileNotFoundException;
import utils.security.OneWayEncryptionUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class Client {

    private final String login;
    private final String password;
    private final String salt;
    private final String aes;
    private final String token;
    private boolean signInState;
    private final Set<File> files;

    /**
     * @param login the client login
     * @param password the client password
     * @param aes the client aes key used to hash the content files
     * @param files the client files
     */
    public Client(final String login, final String password, final String salt, final String aes, final String token, final List<File> files) {
        this.login = login;
        this.aes = aes;
        this.token = token;
        this.files = new HashSet<>(files);
        this.salt = salt;
        this.password = password;
        this.signInState = true;
    }

    public boolean addFile(final File file) {
        return files.add(file);
    }

    public boolean removeFileByName(String file) {
        return files.remove(findFileByName(file));
    }

    /**
     * @return the client's aes key used to hash the file's content
     */
    public String getAes() {
        return aes;
    }

    /**
     * @return the client's login
     */
    public String getLogin() {
        return login;
    }

    /**
     * Return the token used to authenticate the user
     * */
    public String getToken() {
        return token;
    }

    public void setSignInState(boolean state) {
        this.signInState = state;
    }

    public boolean getSignState() {
        return signInState;
    }

    /**
     * @param fileName the file's name
     * @return the file with the given name
     * @throws FileNotFoundException if the file was not found
     */
    public File getFileByName(final String fileName) throws FileNotFoundException {
        return findFileByName(fileName);
    }

    /**
     * @return client's files name
     */
    public List<String> getFilesName() {
        return files.stream()
                .map(File::getName)
                .collect(Collectors.toList());
    }

    /**
     * @return how many files the user own
     */
    public int getFilesCount() {
        return files.size();
    }

    public String getSalt() {
        return salt;
    }

    public String getPassword() {
        return password;
    }

    /**
     * @param password the client password
     * @return true if the given password is correct, otherwise false
     */
    public boolean isPasswordCorrect(final String password) {
        return this.password.equals(hashPassword(password));
    }

    private File findFileByName(final String fileName) throws FileNotFoundException {
        final File found = files
            .stream()
            .filter(file -> file.getName().equals(fileName))
            .findFirst()
            .orElse(null);
        if(found == null)
            throw new FileNotFoundException("File not found");
        else return found;
    }

    private String hashPassword(final String password) {
        return OneWayEncryptionUtils.SHA384EncryptAsHex(password + salt);
    }

    @Override
    public String toString() {
        return new Gson().toJson(this, Client.class);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Client client = (Client) o;
        return login.equals(client.login);
    }

    @Override
    public int hashCode() {
        return Objects.hash(login);
    }
}

