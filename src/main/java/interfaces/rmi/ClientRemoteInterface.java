package interfaces.rmi;/*
 * Name: Chew-Yi
 * Surname: Feng
 * StudentID: 1431319
 */

import models.ChatMessage;
import models.Game;
import models.PlayerRole;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientRemoteInterface extends Remote {
    void updateGameMsg(Game game) throws RemoteException;

    record MatchMessage(PlayerRole role, Game game) implements Serializable {};
    void matchMsg(MatchMessage message) throws RemoteException;

    void notifyChatMessage(ChatMessage message) throws RemoteException;

    /** use for check existence */
    boolean heartBeat() throws RemoteException;

    void suspendGame() throws RemoteException;

    void resumeGame() throws RemoteException;
}
