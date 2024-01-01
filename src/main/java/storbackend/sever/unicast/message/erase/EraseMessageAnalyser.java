package storbackend.sever.unicast.message.erase;

import utils.message.MessageAnalyser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EraseMessageAnalyser extends MessageAnalyser {

    final Pattern pattern = Pattern.compile(String.format("^ERASEFILE (?<%s>\\w{50,200})\r?\n?$", EraseMessageProperties.FILENAME));

    /**
     * Return false if the announcement does not have the expected format, otherwise true;
     * */
    public boolean analyse(final String announce) {
        final Matcher matcher = pattern.matcher(announce);
        if(!matcher.matches()) return false;
        set(EraseMessageProperties.FILENAME, matcher.group(EraseMessageProperties.FILENAME));
        return true;
    }
}
