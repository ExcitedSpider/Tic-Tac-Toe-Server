package T3Server.model;/*
 * Name: Chew-Yi
 * Surname: Feng
 * StudentID: 1431319
 */

import T3Server.Logger.Logger;
import interfaces.rmi.ClientRemoteInterface;

/**
 * Server side player record
 */
public class PlayerRecord {

    private final String username;

    private ClientRemoteInterface remoteCallback;

    private String currentGameId;

    private PlayerStatus status;


    public PlayerRecord(String username, ClientRemoteInterface remoteCallback) {
        this.username = username;
        this.remoteCallback = remoteCallback;
        this.status = PlayerStatus.UNKNOWN;
    }

    public PlayerRecord(String username, ClientRemoteInterface remoteCallback, String currentGameId, PlayerStatus status) {
        this.username = username;
        this.remoteCallback = remoteCallback;
        this.currentGameId = currentGameId;
        this.status = status;
    }

    public String getUsername() {
        return username;
    }

    public ClientRemoteInterface getRemoteCallback() {
        return remoteCallback;
    }

    public void setRemoteCallback(ClientRemoteInterface remoteCallback) {
        this.remoteCallback = remoteCallback;
    }

    public String getCurrentGameId() {
        return currentGameId;
    }

    public void setCurrentGameId(String currentGameId) {
        this.currentGameId = currentGameId;
    }

    public PlayerStatus getStatus() {
        return status;
    }

    public void setStatus(PlayerStatus status) {
        this.status = status;
    }

    public static enum PlayerStatus {
        INGAME,
        ACTIVE,
        INACTIVE,
        FINISHED,
        UNKNOWN,
        DISCONNECTED // wait for 30 seconds to inavtive
    }

    @Override
    public String toString() {
        return "PlayerRecord{" +
                "username='" + username + '\'' +
                ", currentGameId='" + currentGameId + '\'' +
                ", status=" + status +
                '}';
    }
}
