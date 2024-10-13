package T3.remote;/*
 * Name: Chew-Yi
 * Surname: Feng
 * StudentID: 1431319
 */

import T3.GlobalState;
import T3.eventbus.AppEvents;
import T3.eventbus.MyEventbus;
import T3Server.Logger.Logger;
import interfaces.common.DateUtil;
import interfaces.rmi.ClientRemoteInterface;
import models.ChatMessage;
import models.Game;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import static T3.GlobalState.Key.HeartBeatSince;

public class ClientRemote extends UnicastRemoteObject implements ClientRemoteInterface, Serializable  {

    public ClientRemote() throws RemoteException {
        super();
    }

    @Override
    public void updateGameMsg(Game game) {
        Logger.getInstance().logInfo("Receive Update Game Message From Server");
        MyEventbus.getInstance().emitEvent(new AppEvents.UpdateGameEvent(game));
    }

    @Override
    public void matchMsg(MatchMessage message) {
        Logger.getInstance().logInfo("Receive Match Message From Server" + message);
        MyEventbus.getInstance().emitEvent(new AppEvents.MatchEvent(message.role(), message.game()));
    }

    @Override
    public void notifyChatMessage(ChatMessage message) throws RemoteException {
        Logger.getInstance().logInfo("Receive Chat Message" + message);
        MyEventbus.getInstance().emitEvent(new AppEvents.ReceiveChatMessage(message));
    }

    @Override
    public boolean heartBeat() {
        GlobalState.put(HeartBeatSince, DateUtil.now());
        return true;
    }

    @Override
    public void suspendGame() throws RemoteException {
        Logger.getInstance().logInfo("Receive game suspend signal");
        MyEventbus.getInstance().emitEvent(new AppEvents.Signal(AppEvents.GameSignal.GameSuspend));
    }

    @Override
    public void resumeGame() throws RemoteException {
        Logger.getInstance().logInfo("Receive game resume signal");
        MyEventbus.getInstance().emitEvent(new AppEvents.Signal(AppEvents.GameSignal.GameResume));
    }


}
