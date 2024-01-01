package filefrontend.server.message.signout;

import filefrontend.server.message.signin.SignInMessageProperties;
import utils.message.MessageAnalyser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignOutMessageAnalyser extends MessageAnalyser {
    private final Pattern pattern = Pattern.compile(
            "(?i)^SIGNOUT\r?\n?$");
    @Override
    public boolean analyse(String message) {
        final Matcher matcher = pattern.matcher(message);
        return matcher.matches();
    }
}
