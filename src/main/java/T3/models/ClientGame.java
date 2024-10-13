package T3.models;/*
 * Name: Chew-Yi
 * Surname: Feng
 * StudentID: 1431319
 */

import models.Game;
import models.PlayerRole;

public class ClientGame {
    private final PlayerRole role;

    private final Game gameData;

    private final String userName;

    private boolean suspended = false;

    public ClientGame(PlayerRole role, Game gameData, String userName) {
        this.role = role;
        this.gameData = gameData;
        this.userName = userName;
    }

    public boolean isSuspended() {
        return suspended;
    }

    public void setSuspended(boolean suspended) {
        this.suspended = suspended;
    }

    public PlayerRole getRole() {
        return role;
    }

    public Game getGameData() {
        return gameData;
    }

    public String getUserName() {
        return userName;
    }

    public static ClientGame copy(ClientGame from) {
        return new ClientGame(
                from.role,
                from.gameData,
                from.userName
        );
    }
}
