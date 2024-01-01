package storbackend.sever.unicast.message.retrieve;

import utils.message.MessageAnalyser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RetrieveMessageAnalyser extends MessageAnalyser {

    final Pattern pattern = Pattern.compile(String.format("^RETRIEVEFILE (?<%s>\\w{50,200})\r?\n?$", RetrieveMessageProperties.FILENAME));

    /**
     * Return false if the announcement does not have the expected format, otherwise true;
     * */
    public boolean analyse(final String message) {
        final Matcher matcher = pattern.matcher(message);
        if(!matcher.matches()) return false;
        set(RetrieveMessageProperties.FILENAME, matcher.group(RetrieveMessageProperties.FILENAME));
        return true;
    }
}
