package utils.message;

import java.util.HashMap;
import java.util.Map;

abstract public class MessageAnalyser {

    private final Map<String, String> properties;

    public MessageAnalyser() {
        properties = new HashMap<>();
    }

    public abstract boolean analyse(final String message);

    protected void set(final String key, final String value) {
        properties.put(key, value);
    }

    public String get(final String property) {
        if(!properties.containsKey(property))
            throw new MessageAnalyserException();
        return properties.get(property);
    }
}
