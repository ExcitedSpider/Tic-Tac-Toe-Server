package T3.modelview;/*
 * Name: Chew-Yi
 * Surname: Feng
 * StudentID: 1431319
 */

import javafx.event.Event;
import javafx.event.EventType;
import models.Game;

public class GUIEvents {
    public static final EventType<MoveEvent> Move = new EventType<>(Event.ANY, "Move");
    public static final EventType<StartGameEvent> StartGame = new EventType<>(Event.ANY, "START_GAME");
    public static final EventType<RematchEvent> Rematch = new EventType<>(Event.ANY, "REMATCH");
    public static final EventType<ExitEvent> EXIT = new EventType<>(Event.ANY, "EXIT");
    public static final EventType<LoginEvent> LoginEvent = new EventType<>(Event.ANY, "LOGIN");
    public static final EventType<SendChatMessage> SENT_CHAT = new EventType<>(Event.ANY, "SENT_CHAT");
    public static final EventType<CountDownComplete> COUNT_DOWN_COMPLETE = new EventType<>(Event.ANY, "COUNT_DOWN_COMPLETE");

    public static class LoginEvent extends Event {
        public final String username;

        public LoginEvent(String username) {
            super(LoginEvent);
            this.username = username;
        }
    }
    public static class MoveEvent extends Event {
        public final Game.Move move;
        public MoveEvent(Game.Move move) {
            super(Move);
            this.move = move;
        }
    }
    public static class StartGameEvent extends Event {
        public StartGameEvent() {
            super(StartGame);
        }
    }
    public static class RematchEvent extends Event {
        public RematchEvent() {
            super(Rematch);
        }
    }

    public static class ExitEvent extends Event {
        public ExitEvent() {
            super(EXIT);
        }
    }

    public static final class SendChatMessage extends Event {
        public final String message;

        public SendChatMessage(String message) {
            super(SENT_CHAT);
            this.message = message;
        }
    }

    public static final class CountDownComplete extends Event {

        public CountDownComplete() {
            super(COUNT_DOWN_COMPLETE);
        }
    }
}
