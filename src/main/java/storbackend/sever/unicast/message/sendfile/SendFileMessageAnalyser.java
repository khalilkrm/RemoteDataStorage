package storbackend.sever.unicast.message.sendfile;

import utils.message.MessageAnalyser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SendFileMessageAnalyser extends MessageAnalyser {

    final Pattern pattern = Pattern.compile(String.format("^SENDFILE (?<%s>\\w{50,200}) (?<%s>\\d{1,10}) (?<%s>\\w{50,200})\n?.*$",
            SendFileMessageProperties.FILENAME,
            SendFileMessageProperties.SIZE,
            SendFileMessageProperties.FINGERPRINT));

    /**
     * Return false if the announcement does not have the expected format, otherwise true;
     * */
    public boolean analyse(final String announce) {
        final Matcher matcher = pattern.matcher(announce);
        if(!matcher.matches()) return false;
        set(SendFileMessageProperties.FILENAME, matcher.group(SendFileMessageProperties.FILENAME));
        set(SendFileMessageProperties.SIZE, matcher.group(SendFileMessageProperties.SIZE));
        set(SendFileMessageProperties.FINGERPRINT, matcher.group(SendFileMessageProperties.FINGERPRINT));
        return true;
    }
}
