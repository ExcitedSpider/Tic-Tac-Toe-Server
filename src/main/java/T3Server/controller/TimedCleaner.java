package T3Server.controller;/*
 * Name: Chew-Yi
 * Surname: Feng
 * StudentID: 1431319
 */

import T3Server.Logger.Logger;
import T3Server.model.PlayerRecord;

import java.util.Arrays;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class TimedCleaner {
    public static int TASK_INTERVAL_SECS = 20;

    private Timer task;

    private final GamesManager gamesManager;
    private final PlayersManager playersManager;

    public TimedCleaner(GamesManager gamesManager, PlayersManager playersManager) {
        this.gamesManager = gamesManager;
        this.playersManager = playersManager;
    }

    public void cancel() {
        this.task.cancel();
    }


    public Timer start() {
        var timer = new Timer();
        this.task = timer;

        AtomicInteger numOfRecycled = new AtomicInteger(0);

        timer.scheduleAtFixedRate(
                new TimerTask() {
                    @Override
                    public void run() {
                        var gameset = gamesManager.getGames();
                        numOfRecycled.set(0);
                        gameset.parallelStream().forEach(game -> {
                            var gamePlayers = game.getPlayers();
                            // 2 means could be clear
                            var playerStatusSum = Arrays.stream(gamePlayers).map(name -> {
                                if(playersManager.hasPlayer(name)) {
                                    return playersManager.getPlayerRecord(name);
                                }
                                return null;
                            }).collect(Collectors.summarizingInt(e -> {
                                if(e == null || e.getStatus() == PlayerRecord.PlayerStatus.INACTIVE) {
                                    return 1; // An "1" means ready to be clear
                                }
                                return 0;
                            }));

                            if(playerStatusSum.getSum() == 2) { // both players are inactive
                                var removedGame = gamesManager.removeRecord(game.getGameSpecifier());
                                if(removedGame != null) numOfRecycled.addAndGet(1);
                                Arrays.stream(gamePlayers).forEach(name -> {
                                    var removedPlayer = playersManager.removeRecord(name);
                                    if(removedPlayer != null) numOfRecycled.addAndGet(1);
                                });
                            }
                        });

                        Set<PlayerRecord> players = playersManager.getPlayers();
                        players.parallelStream().forEach(playerRecord -> {
                            if(playerRecord.getStatus() == PlayerRecord.PlayerStatus.INACTIVE && !gamesManager.containGame(playerRecord.getCurrentGameId())) {
                                var removedPlayer = playersManager.removeRecord(playerRecord.getUsername());
                                if(removedPlayer != null) numOfRecycled.addAndGet(1);
                            }
                        });

                        Logger.getInstance().logInfo("Recycle task end. " + numOfRecycled.get() + " objects removed");
                    }
                },
                TASK_INTERVAL_SECS * 1000L, TASK_INTERVAL_SECS * 1000L
        );

        return timer;
    }
}
