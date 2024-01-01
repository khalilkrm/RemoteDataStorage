package filefrontend.server.message.getfile;

import utils.message.MessageAnalyser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GetFileMessageAnalyser extends MessageAnalyser {

    private final Pattern pattern = Pattern.compile(String.format(
            "(?i)^GETFILE (?<%s>[\\x22-\\xff]{1,20})\r?\n?$",
            GetFileMessageProperties.FILENAME));

    public boolean analyse(final String message) {
        final Matcher matcher = pattern.matcher(message);
        if(!matcher.matches()) return false;
        set(GetFileMessageProperties.FILENAME, matcher.group(GetFileMessageProperties.FILENAME));
        return true;
    }
}
