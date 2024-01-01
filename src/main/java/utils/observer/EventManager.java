package utils.observer;

import java.util.*;
import java.util.function.Consumer;

public class EventManager {

    private final Map<EventType, Set<EventListener>> listeners = new HashMap<>();

    public EventManager() {
        final EventType[] eventTypes = EventType.values();
        for (EventType eventType : eventTypes)
            listeners.put(eventType, new HashSet<>());
    }

    public void subscribe(final EventType eventType, EventListener listener) {
        final Set<EventListener> listeners = getSubscribers(eventType);
        listeners.add(listener);
    }

    public void unsubscribe(final EventType eventType, final EventListener listener) {
        final Set<EventListener> listeners = getSubscribers(eventType);
        listeners.remove(listener);
    }

    public void publish(final EventType eventType, final Event event, Consumer<String> consumer) {
        final Set<EventListener> listeners = getSubscribers(eventType);
        listeners.forEach(eventListener -> eventListener.update(event, consumer));
    }

    private Set<EventListener> getSubscribers(EventType eventType) {
        return this.listeners.get(eventType);
    }
}
