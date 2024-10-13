package T3.utils;/*
 * Name: Chew-Yi
 * Surname: Feng
 * StudentID: 1431319
 */

import T3.GlobalState;
import interfaces.rmi.ServerRemoteInterface;
import java.rmi.Naming;
import java.rmi.registry.Registry;

public class ServerRmiResolver {
    private static ServerRmiResolver instance;
    public static ServerRmiResolver getInstance() throws Exception {
        if(instance==null){
            instance = new ServerRmiResolver();
        }
        return instance;
    }

    private final ServerRemoteInterface serverRemote;

    private ServerRmiResolver() throws Exception{
        serverRemote = (ServerRemoteInterface) Naming.lookup("rmi://" + GlobalState.get(GlobalState.Key.ServerAddress) + ":" + GlobalState.get(GlobalState.Key.ServerPort) + "/Game");
    }

    public ServerRemoteInterface getServerRemote() {
        return serverRemote;
    }

}
