package filefrontend.client.task.retrievefile;

import utils.message.MessageAnalyser;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RetrieveResultMessageAnalyser extends MessageAnalyser {

    private static final Logger LOGGER = Logger.getLogger(RetrieveResultMessageAnalyser.class.getSimpleName());

    private final Pattern pattern = Pattern.compile(String.format(
            "(?i)^RETRIEVE_OK (?<%s>\\w{50,200}) (?<%s>\\d{1,10}) (?<%s>\\w{50,200})\n?[\\x22-\\xff]+$|^RETRIEVE_ERROR$",
            RetrieveResultProperties.FILENAME,
            RetrieveResultProperties.SIZE,
            RetrieveResultProperties.CONTENT));

    @Override
    public boolean analyse(final String message) {
        LOGGER.log(Level.INFO, String.format("RetrieveResult Analysing message: %s", message));
        final Matcher matcher = pattern.matcher(message);
        if(!matcher.matches()) return false;
        if(message.equals("RETRIEVE_ERROR")) {
            set(RetrieveResultProperties.IS_ERROR, "true");
        } else {
            set(RetrieveResultProperties.IS_ERROR, "false");
            final long size = Long.parseLong(matcher.group(RetrieveResultProperties.SIZE));
            if(size <= -1L) return false;
            set(RetrieveResultProperties.SIZE, matcher.group(RetrieveResultProperties.SIZE));
            set(RetrieveResultProperties.FILENAME, matcher.group(RetrieveResultProperties.FILENAME));
            set(RetrieveResultProperties.CONTENT, matcher.group(RetrieveResultProperties.CONTENT));
        }
        return true;
    }
}