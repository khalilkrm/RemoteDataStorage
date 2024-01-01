package utils.task;

import filefrontend.responsability.TaskObserver;
import utils.observer.EventType;

import java.util.Objects;

public class Task {

    private EventType type;
    private String savedFilename;
    private String command;
    private String userLogin;
    private String sbeId;
    private Status status;
    private String size;
    private String hashFileContent;
    private String originalFilename;
    private byte[] iv;

    private TaskObserver listener;

    public Task(final TaskObserver listener) {
        status = Status.IDLE;
        this.listener = listener;
    }

    public void setStatus(final Status status) {
        this.status = status;
        if(status.equals(Status.REJECTED) || status.equals(Status.FULFILLED)) {
            listener.notifyStatusChanges(this);
        }
    }

    public void setCommand(final String command) {
        this.command = command;
    }

    public void setSavedFilename(final String filename) {
        this.savedFilename = filename;
    }

    public void setUserLogin(final String userLogin) {
        this.userLogin = userLogin;
    }

    public void setSbeId(final String sbeId) {
        this.sbeId = sbeId;
    }

    public void setType(final EventType type) {
        this.type = type;
    }

    public EventType getType() {
        return type;
    }

    public String getSavedFilename() {
        return savedFilename;
    }

    public Status getStatus() {
        return status;
    }

    public String getCommand() {
        return command;
    }

    public String getSbeId() {
        return sbeId;
    }

    public String getUserLogin() {
        return userLogin;
    }

    public void setFileSize(String size) {
        this.size = size;
    }

    public String getSize() {
        return size;
    }

    public void setFingerprint(final String hashFileContent) {
        this.hashFileContent = hashFileContent;
    }

    public String getFingerprint() {
        return hashFileContent;
    }

    public byte[] getIv() {
        return iv;
    }

    public void setIv(byte[] iv) {
        this.iv = iv;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return type == task.type && Objects.equals(savedFilename, task.savedFilename) &&
                Objects.equals(command, task.command) && Objects.equals(userLogin, task.userLogin) &&
                Objects.equals(sbeId, task.sbeId) && status == task.status &&
                Objects.equals(size, task.size);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, savedFilename, command, userLogin, sbeId, status, size);
    }

    public void setOriginalFilename(final String filename) {
        originalFilename = filename;
    }

    public String getOriginalFilename() {
        return originalFilename;
    }
}
