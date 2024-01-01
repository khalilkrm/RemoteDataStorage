package filefrontend.server.message.signin;

import utils.message.MessageAnalyser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignInMessageAnalyser extends MessageAnalyser {

    private final Pattern pattern = Pattern.compile(String.format(
            "(?i)SIGNIN (?<%s>[a-z0-9]{5,20}) (?<%s>[^!\r\n]{5,50})\r?\n?",
            SignInMessageProperties.LOGIN, SignInMessageProperties.PASSWORD));

    public boolean analyse(final String message) {
        final Matcher matcher = pattern.matcher(message);
        boolean match = matcher.matches();
        if(!match) return false;
        set(SignInMessageProperties.LOGIN, matcher.group(SignInMessageProperties.LOGIN));
        set(SignInMessageProperties.PASSWORD, matcher.group(SignInMessageProperties.PASSWORD));
        return true;
    }
}
