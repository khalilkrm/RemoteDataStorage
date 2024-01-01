package filefrontend.server.message.savefile;

import utils.message.MessageAnalyser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SaveFileMessageAnalyser extends MessageAnalyser {

    private final Pattern pattern = Pattern.compile(String.format(
            "(?i)^SAVEFILE (?<%s>[\\x22-\\xff]{1,20}) (?<%s>[0-9]{1,20})\r?\n?$",
            SaveFileMessageProperties.FILENAME,
            SaveFileMessageProperties.SIZE));

    public boolean analyse(final String message) {
        final Matcher matcher = pattern.matcher(message);
        if(!matcher.matches()) return false;
        final long size = Long.parseLong(matcher.group(SaveFileMessageProperties.SIZE));
        if(size <= -1L) return false;
        set(SaveFileMessageProperties.SIZE, matcher.group(SaveFileMessageProperties.SIZE));
        set(SaveFileMessageProperties.FILENAME, matcher.group(SaveFileMessageProperties.FILENAME));
        return true;
    }
}
