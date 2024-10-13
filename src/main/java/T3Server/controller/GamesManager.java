package T3Server.controller;/*
 * Name: Chew-Yi
 * Surname: Feng
 * StudentID: 1431319
 */

import T3.controller.GameError;
import models.Game;

import java.rmi.RemoteException;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class GamesManager {
    private final HashMap<String, Game> gamePool = new HashMap<>();

    static Lock lock = new ReentrantLock();

    public boolean containGame(String gameId) {
        return gamePool.containsKey(gameId);
    }

    public Optional<Game> queryGameById(String gameId) {
        if(gamePool.containsKey(gameId)) {
            return Optional.of(gamePool.get(gameId));
        }
        return Optional.empty();
    }

    public Game allocateResourceForGame(String player1, String player2) {
        lock.lock();
        var uniqueGameId = UUID.randomUUID().toString();
        var game = new Game(uniqueGameId, new String[]{ player1, player2 });

        // randomly assign the first turn
        Game.State firstTurn = new Game.State[] {Game.State.CrossTurn, Game.State.NoughtTurn } [new Random().nextInt(2)];
        game.setGameState(firstTurn);

        gamePool.put(uniqueGameId, game);
        lock.unlock();
        return game;
    }

    public Game updateGame(Game.Move move, String gameId) throws Exception {
        if(!gamePool.containsKey(gameId) || gamePool.get(gameId) == null) {
            throw new Exception("Game Not Found Error. Use `createGame` to create a new game");
        }
        var game = gamePool.get(gameId);

        var gameCtr = new GameController(game);
        try {
            lock.lock();
            gameCtr.updateGame(move);
            lock.unlock();
        } catch (GameError e) {
            throw new RemoteException("Invalid Move");
        }
        return gameCtr.getGameModel();
    }

    public Set<Game> getGames() {
        return new HashSet<>(this.gamePool.values());
    }

    public Game removeRecord(String gameSpecifier) {
        lock.lock();
        var result = gamePool.remove(gameSpecifier);
        lock.unlock();
        return result;
    }
}
