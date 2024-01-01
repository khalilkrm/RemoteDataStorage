package filefrontend.server.message.filelist;

import utils.message.MessageAnalyser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileListMessageAnalyser extends MessageAnalyser {

    private final Pattern pattern = Pattern.compile("FILELIST\r?\n?");

    @Override
    public boolean analyse(String message) {
        final Matcher matcher = pattern.matcher(message);
        return matcher.matches();
    }
}
