package T3.eventbus;/*
 * Name: Chew-Yi
 * Surname: Feng
 * StudentID: 1431319
 */

import models.ChatMessage;
import models.Game;
import models.PlayerRole;

public class AppEvents {
    public record MatchEvent(PlayerRole role, Game game) { }

    public record UpdateGameEvent(Game game) {}

    public record ReceiveChatMessage(ChatMessage message) {}

    public record TestEvent(String message) {}

    public enum GameSignal {
        GameSuspend,
        GameResume
    }
    public record Signal(GameSignal signal) {}
}

