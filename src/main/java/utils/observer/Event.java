package utils.observer;

import java.util.Collections;
import java.util.Map;
import java.util.function.Consumer;

public class Event {

    private final Map<String, String> properties;
    private final String ownerUsername;

    public Event(final String ownerUsername, final Map<String, String> properties) {
        this.properties = Collections.unmodifiableMap(properties);
        this.ownerUsername = ownerUsername;
    }

    public Event(final Map<String, String> properties) {
        this.properties = Collections.unmodifiableMap(properties);
        this.ownerUsername = "__UNKNOWN__";
    }

    public String get(final String property) {
        return properties.get(property);
    }

    public String getOwnerUsername() {
        return ownerUsername;
    }
}
