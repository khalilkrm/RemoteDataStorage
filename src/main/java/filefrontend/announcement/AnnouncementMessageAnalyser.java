package filefrontend.announcement;

import utils.message.MessageAnalyser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AnnouncementMessageAnalyser extends MessageAnalyser {

    private final Pattern pattern = Pattern.compile(String.format(
            "(?i)^hello (?<%s>[a-z0-9\\.]{5,20}) (?<%s>\\d+)\r?\n?$",
            AnnouncementMessageProperties.DOMAIN, AnnouncementMessageProperties.PORT));

    /**
     * Return false if the announcement does not have the expected format, otherwise true;
     * */
    public boolean analyse(final String announce) {
        final Matcher matcher = pattern.matcher(announce);
        if(!matcher.matches()) return false;
        final int port = Integer.parseInt(matcher.group(AnnouncementMessageProperties.PORT));
        if(port > 65535 || port <= 1024) return false;
        set(AnnouncementMessageProperties.PORT, matcher.group(AnnouncementMessageProperties.PORT));
        set(AnnouncementMessageProperties.DOMAIN, matcher.group(AnnouncementMessageProperties.DOMAIN));
        return true;
    }
}
