package filefrontend.repository;

public class SavedState {

    private final boolean savedState;
    private final String savedFilename;
    public final String fingerprint;

    public SavedState(final boolean savedState, final String savedFilename, final String fingerprint) {
        this.savedState = savedState;
        this.savedFilename = savedFilename;
        this.fingerprint = fingerprint;
    }

    public String getSavedFilename() {
        return savedFilename;
    }

    public boolean getSavedState() {
        return savedState;
    }

    public String getFingerprint() {
        return fingerprint;
    }
}
