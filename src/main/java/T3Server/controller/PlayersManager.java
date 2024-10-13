package T3Server.controller;/*
 * Name: Chew-Yi
 * Surname: Feng
 * StudentID: 1431319
 */

import T3.remote.ClientRemote;
import T3Server.Logger.Logger;
import T3Server.model.PlayerRecord;
import interfaces.rmi.ClientRemoteInterface;
import models.Game;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.Function;

import static T3Server.model.PlayerRecord.PlayerStatus.*;

public class PlayersManager {
    private static final Lock lock = new ReentrantLock();

    private static final int HEARTBET_INTERVAL_SECS = 3;
    private static final int DISCONNECT_WAIT_SECS = 30;
    private final ConcurrentHashMap<String, PlayerRecord> playerData = new ConcurrentHashMap<>();
    private final Queue<String> activePlayerList = new LinkedList<>();
    private Function<PlayerMatch, String> gameMatchConsumer;

    private Consumer<PlayerRecord> handleHeartBeatCheck;

    private ExecutorService heartBeatCheckThreads = Executors.newFixedThreadPool(2);

    public boolean login(String userName, ClientRemoteInterface clientRemote) {
        lock.lock();
        if (playerData.containsKey(userName)) {
            // re-login
            var playerRecord = playerData.get(userName);
            var newRecord = new PlayerRecord(
                    userName,
                    clientRemote,
                    playerRecord.getCurrentGameId(),
                    playerRecord.getStatus()
            );
            Logger.getInstance().logInfo("Player remote updated: " + userName);
            playerData.remove(userName);
            playerData.put(userName, newRecord);
        } else {
            var playerRecord = new PlayerRecord(userName, clientRemote);
            playerRecord.setStatus(PlayerRecord.PlayerStatus.ACTIVE);
            playerData.put(userName, playerRecord);
            activePlayerList.add(userName);
            Logger.getInstance().logInfo("Start Heartbeat Check: " + userName);
        }
        if (gameMatchConsumer != null) {
            tryMatchAGame();
        }
        lock.unlock();

        return true;
    }

    public void onHeartBeatCheckFailed(Consumer<PlayerRecord> handleHeartBeatCheck) {
        this.handleHeartBeatCheck = handleHeartBeatCheck;
    }

    public Timer startHeartBeatCheck(
            String username,
            ClientRemoteInterface remote,
            Consumer<Object> heartbeatFailed
    ) {

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    remote.heartBeat();
                    if(playerData.get(username).getStatus() == DISCONNECTED) {
                        // TODO: Check the game still alive
                        playerData.get(username).setStatus(INGAME);
                    }
                } catch (Exception e) {
                    Logger.getInstance().logErr("Heart Beat Failed");
                    heartbeatFailed.accept(username);
                    timer.cancel();
                }
            }
        }, 0, HEARTBET_INTERVAL_SECS * 1000);

        return timer;

//        var username = playerRecord.getUsername();
//        heartBeatCheckThreads.execute(() -> {
//            try {
//                Thread.sleep(HEARTBET_INTERVAL_SECS * 1000);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//            while (true) {
//                var latestRecord = this.playerData.get(username);
//                if(latestRecord == null) {
//                    Logger.getInstance().logInfo("Stop heartbeat check user has been removed:" + username);
//                    break;
//                };
//                try {
//                    var status = latestRecord.getStatus();
//                    if (status == INACTIVE || status == DISCONNECTED) {
//                        Logger.getInstance().logInfo("Stop heartbeat check because user is inactive:" + username);
//                        break;
//                    };
//                    var remote = getRemote(username);
//                    if (remote.isEmpty()) throw new Exception("Empty Remote");
//                    remote.get().heartBeat();
//                    Thread.sleep(HEARTBET_INTERVAL_SECS * 1000);
//                } catch (Exception e) {
//                    Logger.getInstance().logInfo(e.getMessage());
//                    handleHeartBeatCheck.accept(latestRecord);
//                    break;
//                }
//            }
//        });
    }

    public int getUserSize() {
        return this.playerData.size();
    }

    ;

    public PlayerRecord getPlayerRecord(String playerName) {
        return this.playerData.get(playerName);
    }

    public PlayerRecord removeRecord(String playerName) {
        lock.lock();
        var result = this.playerData.remove(playerName);
        lock.unlock();
        return result;
    }

    public Set<PlayerRecord> getPlayers() {
        return new HashSet<>(this.playerData.values());
    }

    public void rematch(String username) throws Exception {
        if (!this.playerData.containsKey(username)) throw new Exception("Cannot Rematch: User is not exist");

        var playerRecord = this.playerData.get(username);
        if (playerRecord.getStatus() == INGAME) throw new Exception(("Cannot Rematch: User is in game"));
        if (playerRecord.getStatus() == INACTIVE)
            throw new Exception(("Cannot Rematch: User is inactive. Should call login instead."));

        // Do not need lock because its idempotent
        playerRecord.setStatus(ACTIVE);
        playerRecord.setCurrentGameId(null);

        lock.lock();
        activePlayerList.add(username);
        lock.unlock();

        tryMatchAGame();
    }

    public void disconnectAndWait(String username, Consumer<PlayerRecord> endWaitCallback) {
        var playerRecord = this.getPlayerRecord(username);
        if(playerRecord == null) {
            return;
        }

        playerRecord.setStatus(DISCONNECTED);
        var timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                var latestRecord = PlayersManager.this.getPlayerRecord(username);
                if(latestRecord == null) {
                    Logger.getInstance().logInfo("User record not found " + username);
                    timer.cancel();
                    return;
                }
                // Check whether it is reconnected
                if(latestRecord.getStatus() == DISCONNECTED){
                    endWaitCallback.accept(playerRecord);
                }else {
                    Logger.getInstance().logInfo("User reconnected " + username);
                }
            }
        }, DISCONNECT_WAIT_SECS * 1000);

    };

    public void removeRemote(String playerName) {
        if(this.playerData.containsKey(playerName)) {
            this.playerData.get(playerName).setRemoteCallback(null);
        }
    }

    public record PlayerMatch(String player1, String player2) {
    }

    ;

    /**
     * @param getGameSpecifier consume players and return a game id
     */
    public void onMatchAGame(Function<PlayerMatch, String> getGameSpecifier) {
        this.gameMatchConsumer = getGameSpecifier;
        tryMatchAGame();
    }


    private void tryMatchAGame() {
        if (activePlayerList.size() >= 2) {
            lock.lock();
            var player1 = activePlayerList.remove();
            var player2 = activePlayerList.remove();
            lock.unlock();

            var gameSpecifier = gameMatchConsumer.apply(new PlayerMatch(player1, player2));
            Arrays.asList(player1, player2).parallelStream().forEach(player -> {
                var record = this.playerData.get(player);
                record.setStatus(INGAME);
                record.setCurrentGameId(gameSpecifier);
            });

            tryMatchAGame();
        }
    }

    public Optional<ClientRemoteInterface> getRemote(String username) {
        if (playerData.containsKey(username)
                && playerData.get(username) != null
                && playerData.get(username).getRemoteCallback() != null
        ) {
            return Optional.of(playerData.get(username).getRemoteCallback());
        }
        return Optional.empty();
    }

    public boolean hasPlayer(String username) {
        return playerData.containsKey(username);
    }

    public void logout(String username) {
        if (playerData.containsKey((username))) {
            lock.lock();
            playerData.remove(username);
            activePlayerList.remove(username);
            lock.unlock();
        }
    }
}
