package T3;/*
 * Name: Chew-Yi
 * Surname: Feng
 * StudentID: 1431319
 */

import java.util.HashMap;

public class GlobalState {
    static private final HashMap<Key, String> state = new HashMap<>();

    public enum Key {
        Username,
        ServerAddress,
        ServerPort,
        HeartBeatSince
    }

    public static void put(Key key, String value) {
        state.put(key, value);
    }
    public static String get(Key key) {
        return state.get(key);
    }
}
