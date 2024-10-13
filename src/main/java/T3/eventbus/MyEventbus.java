package T3.eventbus;/*
 * Name: Chew-Yi
 * Surname: Feng
 * StudentID: 1431319
 */

import java.util.*;
import java.util.function.Consumer;

public class MyEventbus {

    private static MyEventbus instance;
    public static MyEventbus getInstance(){
        if(instance == null){
            instance = new MyEventbus();
        }
        return instance;
    }

    private MyEventbus() {}

    private HashMap<Class, Set<Consumer>> eventConsumerMap = new HashMap<>();

    public <T> void addEventListener(Class<T> eventClass, Consumer<T> listener) {
        if(!this.eventConsumerMap.containsKey(eventClass)) {
            this.eventConsumerMap.put(eventClass, new HashSet<>());
        };

        this.eventConsumerMap.get(eventClass).add(listener);
    }
    public <T> void removeEventListener(Class<T> eventClass, Consumer<T> listener) {
        if(!this.eventConsumerMap.containsKey(eventClass)) {
           return;
        };

        var listeners = this.eventConsumerMap.get(eventClass);
        listeners.remove(listener);
    }

    public <T> void emitEvent(T event) {
        var eventClass = event.getClass();
        if(this.eventConsumerMap.containsKey(eventClass)) {
            this.eventConsumerMap.get(eventClass).forEach(consumer -> consumer.accept(event));
        }
    }
}
