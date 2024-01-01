package filefrontend.server.message.signup;

import filefrontend.server.message.signin.SignInMessageProperties;
import utils.message.MessageAnalyser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpMessageAnalyser extends MessageAnalyser {

    private final Pattern pattern = Pattern.compile(String.format(
            "(?i)SIGNUP (?<%s>[a-z0-9]{5,20}) (?<%s>[^!\r\n]{5,50})\r?\n?",
            SignUpMessageProperties.LOGIN, SignUpMessageProperties.PASSWORD));

    public boolean analyse(String message) {
        final Matcher matcher = pattern.matcher(message);
        if(!matcher.matches()) return false;
        set(SignInMessageProperties.LOGIN, matcher.group(SignInMessageProperties.LOGIN));
        set(SignInMessageProperties.PASSWORD, matcher.group(SignInMessageProperties.PASSWORD));
        return true;
    }
}
