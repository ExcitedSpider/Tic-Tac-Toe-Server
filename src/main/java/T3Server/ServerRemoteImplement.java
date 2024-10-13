package T3Server;

import T3Server.Logger.Logger;
import T3Server.controller.GameController;
import T3Server.controller.GamesManager;
import T3Server.controller.PlayersManager;
import T3Server.model.PlayerRankings;
import T3Server.model.PlayerRecord;
import interfaces.rmi.ClientRemoteInterface;
import interfaces.rmi.ClientRequest;
import interfaces.rmi.ServerRemoteInterface;
import interfaces.rmi.ServerResponse;
import models.ChatMessage;
import models.Game;
import models.PlayerRole;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
/*
 * Name: Chew-Yi
 * Surname: Feng
 * StudentID: 1431319
 */

public class ServerRemoteImplement extends UnicastRemoteObject implements ServerRemoteInterface {
    private final PlayersManager playersManager;
    private final GamesManager gamesManager;
    private final PlayerRankings playerRankings;

    public ServerRemoteImplement(
            PlayersManager playersManager,
            GamesManager gamesManager,
            PlayerRankings playerRankings
    ) throws RemoteException {
        this.playersManager = playersManager;
        this.gamesManager = gamesManager;
        this.playerRankings = playerRankings;
        this.playersManager.onMatchAGame(gameMatchHandler());
        this.playersManager.onHeartBeatCheckFailed(handleHeartbeatFailed());
    }

    private Consumer<PlayerRecord> handleHeartbeatFailed() {
        return playerRecord -> {
            var playername = playerRecord.getUsername();

            // last chance
            try {
                var remote = this.playersManager.getRemote(playername);
                if(remote.isPresent()) {
                    remote.get().heartBeat();
                    Logger.getInstance().logInfo("Player is alive!" + playername);
                };
            }catch (Exception e) {
                Logger.getInstance().logErr(e);
                // last chance failed
                Logger.getInstance().logInfo("Player heartbeat stopped " + playername);
                if (playerRecord.getStatus() == PlayerRecord.PlayerStatus.INGAME) {
                    this.disconnectPlayer(playername);
                } else {
                    this.logoutPlayer(playername);
                }
            }

        };
    }

    private Function<PlayersManager.PlayerMatch, String> gameMatchHandler() {
        return (match) -> {
            var player1Username = match.player1();
            var player2Username = match.player2();

            Logger.getInstance().logInfo("Match 2 users: " + player1Username + ", " + player2Username);

            var game = gamesManager.allocateResourceForGame(player1Username, player2Username);
            var roleList = new PlayerRole[]{PlayerRole.Cross, PlayerRole.Naught};
            var playList = new String[]{player1Username, player2Username};

            Arrays.stream(playList).forEach(player -> {
                playersManager.getPlayerRecord(player).setStatus(PlayerRecord.PlayerStatus.INGAME);
            });

            for (int i = 0; i < playList.length; i++) {
                var username = playList[i];
                var role = roleList[i];

                game.setPlayerRole(username, role);

                playersManager.getRemote(username).ifPresentOrElse(remote ->
                {
                    try {
                        remote.matchMsg(new ClientRemoteInterface.MatchMessage(role, game));
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                }, () -> {
                    Logger.getInstance().logErr("Client Remote Not Found!");
                });
            }

            return game.getGameSpecifier();
        };
    }

    @Override
    public ServerResponse<LoginResponse> login(String userName, ClientRemoteInterface clientRemote) {
        var previousRecord = playersManager.getPlayerRecord(userName);
        var isUserDisconnected = previousRecord != null &&
                Set.of(PlayerRecord.PlayerStatus.DISCONNECTED, PlayerRecord.PlayerStatus.INGAME).contains(previousRecord.getStatus());

        playersManager.login(userName, clientRemote);
        playersManager.startHeartBeatCheck(userName, clientRemote, w -> {
            this.disconnectPlayer(userName);
        });

        Logger.getInstance().logInfo("Player login:" + userName + ". Current player: " + playersManager.getUserSize());

        if (isUserDisconnected) {
            Logger.getInstance().logInfo("User " + userName + " reconnected.");
            var game = getGameByUser(userName);
            assert game != null;
            game.setStateMessage(null);
            Arrays.stream(game.getPlayers()).map(playersManager::getRemote).forEach(maybeRemote -> {
                maybeRemote.ifPresent(remote -> {
                    try {
                        remote.resumeGame();
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                });
            });
            return ServerResponse.success(new LoginResponse(true, userName, game.getPlayerRole(userName), game));
        } else {
            return ServerResponse.success(new LoginResponse(false, userName, null, null));
        }


    }

    /**
     * @deprecated old version. do not use this
     */
    @Override
    public ServerResponse<Game> createGame(ClientRequest<Object> request) throws RemoteException {
        String username = request.username;
        return ServerResponse.badRequest("This method is deprecated");
    }

    @Override
    public ServerResponse<Game> updateGame(ClientRequest<UpdateGameData> request) throws RemoteException {
        Logger.getInstance().logInfo("Update Game: " + request.data.move());
        var gameQuery = this.gamesManager.queryGameById(request.data.gameSpecifier());

        if (gameQuery.isEmpty())
            return ServerResponse.badRequest("Game Not Found. Use `createGame` to create a new game");
        if (!new HashSet<>(Arrays.asList(gameQuery.get().getPlayers())).contains(request.username))
            return ServerResponse.badRequest("Permission Error: User cannot update this game");

        try {
            Game model = this.gamesManager.updateGame(request.data.move(), request.data.gameSpecifier());
            Set<Game.State> finishedState = Set.of(
                    Game.State.CrossWin,
                    Game.State.NoughtWin,
                    Game.State.Draw
            );
            if(finishedState.contains(model.getGameState())) {
                Arrays.stream(model.getPlayers()).forEach(player -> playersManager.getPlayerRecord(player).setStatus(PlayerRecord.PlayerStatus.FINISHED));
            }
            updateRankings(model);
            notifyClientGameState(model);
            return ServerResponse.success(model);
        } catch (Exception e) {
            return ServerResponse.serverError(e.getMessage());
        }
    }

    private void updateRankings(Game model) {
        Game.State gameState = model.getGameState();
        if(gameState == Game.State.CrossWin || gameState == Game.State.NoughtWin) {
            var winedPlayer = model.getPlayerByRole(gameState == Game.State.CrossWin ? PlayerRole.Cross : PlayerRole.Naught);
            this.playerRankings.modifyUserMarks(winedPlayer, 5);
            var failedPlayer = Arrays.stream(model.getPlayers()).filter(player -> !player.equals(winedPlayer)).findFirst();
            failedPlayer.ifPresent(name -> this.playerRankings.modifyUserMarks(name, -5));
            Logger.getInstance().logInfo("Player "+winedPlayer + " beat " + failedPlayer.orElse("Unknown"));
        }else if(gameState == Game.State.Draw) {
            Arrays.stream(model.getPlayers()).forEach(player -> {
                this.playerRankings.modifyUserMarks(player, 2);
            });

            Logger.getInstance().logInfo("Players" +
                    String.join(" and ", model.getPlayers()) + " draw a game");
        }
    }

    @Override
    public ServerResponse<ChatMessage> postChatMessage(ClientRequest<PostChatRequest> postChatMessageRequest) throws RemoteException {
        var username = postChatMessageRequest.username;
        if (!playersManager.hasPlayer(username)) {
            return ServerResponse.badRequest("User is not registered");
        }
        ;
        var gameSpecifier = postChatMessageRequest.data.gameSpecifier();
        var gameQuery = gamesManager.queryGameById(gameSpecifier);
        if (gameQuery.isEmpty()) {
            return ServerResponse.badRequest("Game Not Found");
        }
        var game = gameQuery.get();

        var message = postChatMessageRequest.data.data();
        List.of(game.getPlayers()).parallelStream().forEach(player -> {
            playersManager.getRemote(player).ifPresentOrElse(remote -> {
                try {
                    remote.notifyChatMessage(message);
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }, () -> Logger.getInstance().logErr("User remote not found. Cannot notify user"));
        });

        return ServerResponse.success(message);
    }

    @Override
    public ServerResponse<Object> clientLogout(ClientRequest<Object> request) throws RemoteException {
        Logger.getInstance().logInfo("Player logout" + ". Current player: " + playersManager.getUserSize());
        var result = this.logoutPlayer(request.username);
        if (result) {
            return ServerResponse.success(null);
        } else {
            return ServerResponse.serverError(null);
        }
    }

    @Override
    public ServerResponse<Boolean> rematch(String username) throws RemoteException {
        try {
            playersManager.rematch(username);
            Logger.getInstance().logInfo("User " + username + " rematch a new game!");
            return ServerResponse.success(true);
        } catch (Exception e) {
            Logger.getInstance().logErr(e);
            return ServerResponse.serverError(e.getMessage());
        }
    }

    @Override
    public ServerResponse<List<PlayerRankingQuery>> getPlayerRanking(List<String> usernames) throws RemoteException {
        var records = usernames
                .stream()
                .map(w -> new PlayerRankingQuery(w, playerRankings.getUserRanking(w)))
                .toList();
        return ServerResponse.success(records);
    }

    private void notifyClientGameState(Game game) {
        Stream.of(game.getPlayers()).forEach(player -> {
            this.notifyClientGameState(game, player);
        });
    }

    private void notifyClientGameState(Game game, String player) {
        this.playersManager.getRemote(player).ifPresent(
                clientRemote -> {
                    try {
                        clientRemote.updateGameMsg(game);
                    } catch (RemoteException e) {
                        Logger.getInstance().logErr("Cannot Connect to client");
                        this.playersManager.removeRemote(player);
                    }
                }
        );
    }

    private boolean logoutPlayer(String playerName) {
        if (!playersManager.hasPlayer(playerName)) {
            return false;
        }
        var playerRecord = playersManager.getPlayerRecord(playerName);
        if (playerRecord == null) return true;
        Logger.getInstance().logInfo("Logout " + playerRecord);
        playersManager.logout(playerRecord.getUsername());

        var maybeAGame = gamesManager.queryGameById(playerRecord.getCurrentGameId());

        maybeAGame.ifPresent(this::assignWinnerForAlivePlayer);

        return true;
    }

    private boolean disconnectPlayer(String playerName) {
        if (!playersManager.hasPlayer(playerName)) {
            return false;
        }
        var playerRecord = playersManager.getPlayerRecord(playerName);
        assert playerRecord != null;

        var game = getGameByUser(playerName);
        if(game.getGameState() == Game.State.CrossTurn || game.getGameState() == Game.State.NoughtTurn) {
            Logger.getInstance().logInfo("Disconnect " + playerRecord);
            disconnectUserInGame(playerName);
        }
        return true;
    }

    private void disconnectUserInGame(String playerName)  {
        Logger.getInstance().logInfo("User " + playerName + " is disconnected. Suspend game...");

        var game = getGameByUser(playerName);
        assert game != null;

        Arrays.stream(game.getPlayers()).forEach(player -> {
            playersManager.getRemote(player).ifPresent(remote -> {
                try {
                    remote.suspendGame();
                } catch (RemoteException e) {
                    Logger.getInstance().logErr("Cannot Suspend Game: User Remote is invalid");
                }
            });
        });

        Logger.getInstance().logInfo("Start counting down...");
        playersManager.disconnectAndWait(playerName, playerRecord -> {
            // if wait end, just logout
            this.playersManager.logout(playerName);
            if (playerRecord.getCurrentGameId() != null) {
                var latestGame = gamesManager.queryGameById(playerRecord.getCurrentGameId()).orElse(null);
                if(latestGame == null) {
                    // game has gone. nothing need to be done here
                    return;
                }
                if (latestGame.getGameState() == Game.State.CrossTurn || latestGame.getGameState() == Game.State.NoughtTurn) {
                    assignWinnerForAlivePlayer(latestGame);
                }
            }
        });
    }

    private Game getGameByUser(String playername) {
        var playerRecord = playersManager.getPlayerRecord(playername);
        if (playerRecord == null) return null;
        var gameId = playerRecord.getCurrentGameId();
        if (gameId == null) return null;

        return gamesManager.queryGameById(gameId).orElse(null);
    }

    private void assignWinnerForAlivePlayer(Game game) {
        var alivePlayers = Arrays.stream(game.getPlayers())
                .map(playersManager::getPlayerRecord)
                .filter(record -> record != null && record.getStatus() == PlayerRecord.PlayerStatus.INGAME)
                .collect(Collectors.toSet());

        if (alivePlayers.size() == 1) { // Exactly One player alive
            var alivePlayerRecord = alivePlayers.iterator().next();
            var alivePlayer = alivePlayerRecord.getUsername();
            playersManager.getPlayerRecord(alivePlayer).setStatus(PlayerRecord.PlayerStatus.FINISHED);
            new GameController(game).assignWinner(game.getPlayerRole(alivePlayer));
            updateRankings(game);
            game.setStateMessage("You wined because your opponent has left the game.");

            notifyClientGameState(game, alivePlayer);
            Logger.getInstance().logInfo("Assign winner to game since one of the player logout");
        } else if (alivePlayers.size() > 1) {
            // Not suppose being here
            throw new RuntimeException("Invalid game status");
        }
    }
}
