package interfaces.rmi;

import models.ChatMessage;
import models.Game;
import models.PlayerRole;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ServerRemoteInterface extends Remote {

    record LoginResponse(boolean isReconnected, String username, PlayerRole role, Game game) implements Serializable {}

    ServerResponse<LoginResponse> login(String username, ClientRemoteInterface clientRemote) throws RemoteException;

    ServerResponse<Game> createGame(ClientRequest<Object> request) throws RemoteException;

    record UpdateGameData(String gameSpecifier, Game.Move move) implements Serializable {};

    ServerResponse<Game> updateGame(ClientRequest<UpdateGameData> updateClientRequest) throws RemoteException;

    record PostChatRequest(String gameSpecifier, ChatMessage data) implements Serializable {};

    ServerResponse<ChatMessage> postChatMessage(ClientRequest<PostChatRequest> postChatMessageRequest) throws RemoteException;

    ServerResponse<Object> clientLogout(ClientRequest<Object> request) throws RemoteException;

    ServerResponse<Boolean> rematch(String username) throws RemoteException;

    record PlayerRankingQuery(String username, Integer ranking) implements Serializable {};
    ServerResponse<List<PlayerRankingQuery>> getPlayerRanking(List<String> usernames) throws RemoteException;
}
