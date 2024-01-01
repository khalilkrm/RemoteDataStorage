package storbackend.domain;

public class File {
    private final String filename;
    public final long size;
    public final String fingerprint;

    public File(String filename, long size, String fingerprint) {
        this.filename = filename;
        this.size = size;
        this.fingerprint = fingerprint;
    }

    public String getFilename() {
        return filename;
    }

    public String getFingerprint() {
        return fingerprint;
    }

    public long getSize() {
        return size;
    }
}
