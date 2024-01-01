package filefrontend.server.message.removefile;

import utils.message.MessageAnalyser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RemoveFileMessageAnalyser extends MessageAnalyser {

    private final Pattern pattern = Pattern.compile(String.format(
            "(?i)^REMOVEFILE (?<%s>[\\x22-\\xff]{1,20})\r?\n?$",
            RemoveFileMessageProperties.FILENAME));

    public boolean analyse(final String message) {
        final Matcher matcher = pattern.matcher(message);
        if(!matcher.matches()) return false;
        set(RemoveFileMessageProperties.FILENAME, matcher.group(RemoveFileMessageProperties.FILENAME));
        return true;
    }
}
