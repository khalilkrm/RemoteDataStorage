package filefrontend.server.message.signcode;

import utils.message.MessageAnalyser;

import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignCodeMessageAnalyser extends MessageAnalyser {
    private final static Logger LOGGER = Logger.getLogger(SignCodeMessageAnalyser.class.getSimpleName());

    private final Pattern pattern = Pattern.compile(String.format(
            "(?i)SIGNCODE (?<%s>[a-z0-9]{5,20}) (?<%s>[0-9]{6})\r?\n?",
            SignCodeMessageProperties.LOGIN,
            SignCodeMessageProperties.CODE));

    public boolean analyse(final String message) {
        final Matcher matcher = pattern.matcher(message);
        boolean match = matcher.matches();
        if(!match) return false;
        set(SignCodeMessageProperties.CODE, matcher.group(SignCodeMessageProperties.CODE));
        set(SignCodeMessageProperties.LOGIN, matcher.group(SignCodeMessageProperties.LOGIN));
        return true;
    }
}
