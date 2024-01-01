package utils.observer;

import java.util.function.Consumer;

public interface EventListener {
    void update(final Event event, Consumer<String> callback);
}
