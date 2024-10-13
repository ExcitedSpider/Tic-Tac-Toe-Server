package models;/*
 * Name: Chew-Yi
 * Surname: Feng
 * StudentID: 1431319
 */

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Game implements Serializable {
    public enum State {
        NoughtTurn,
        CrossTurn,
        Stopped,
        NoughtWin,
        CrossWin,
        Draw;
    }

    public enum Piece {
        Vacant(0),
        Naught(1),
        Cross(4);

        public final int value;

        Piece(int value) {this.value = value;}
    }

    public record Move(int position, Game.Piece piece) implements Serializable {
        @Override
        public String toString() {
            return "Move{" +
                    "position=" + position +
                    ", piece=" + piece +
                    '}';
        }
    };

    private State gameState = State.Stopped;
    /**
     * Client should display this message if it is non-null.
     */
    private String stateMessage;
    private Piece[][] boardValue;
    private int[][] winingPath;
    private String gameSpecifier;

    private String[] players;

    private Map<String, PlayerRole> playerRoleDict;

    public String[] getPlayers() {
        return players;
    }

    public Game(String gameSpecifier, String[] players) {
        this.players = players;
        this.gameSpecifier = gameSpecifier;
        this.playerRoleDict = new HashMap<>();
        initBoardValue();
    }

    public void setPlayerRole(String playerName, PlayerRole role) {
        this.playerRoleDict.put(playerName, role);
    }

    public PlayerRole getPlayerRole(String playerName) {
        return this.playerRoleDict.get(playerName);
    }

    public String getPlayerByRole(PlayerRole role) {
        var wantedEntry = playerRoleDict.entrySet().stream().filter(entry -> entry.getValue() == role).findFirst();
        return wantedEntry.map(Map.Entry::getKey).orElse(null);
    }

    
    public String getGameSpecifier() {
        return gameSpecifier;
    }

    public void setGameSpecifier(String gameSpecifier) {
        this.gameSpecifier = gameSpecifier;
    }

    public Game() {
        initBoardValue();
    }

    private void initBoardValue() {
        this.boardValue = new Piece[3][3];

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                boardValue[i][j] = Piece.Vacant;
            }
        }
    }

    public State getGameState() {
        return gameState;
    }

    public void setGameState(State gameState) {
        this.gameState = gameState;
    }

    public Piece[][] getBoardValue() {
        return boardValue;
    }

    public void setBoardValue(Piece[][] boardValue) {
        this.boardValue = boardValue;
    }

    public int[][] getWiningPath() {
        return winingPath;
    }

    public void setWiningPath(int[][] winingPath) {
        this.winingPath = winingPath;
    }

    public String getStateMessage() {
        return stateMessage;
    }

    public void setStateMessage(String stateMessage) {
        this.stateMessage = stateMessage;
    }
}
