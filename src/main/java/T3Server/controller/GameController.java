package T3Server.controller;/*
 * Name: Chew-Yi
 * Surname: Feng
 * StudentID: 1431319
 */

import T3.controller.GameError;
import models.Game;
import models.PlayerRole;

import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.stream.IntStream;

public class GameController {
    private Game gameModel;

    public GameController(Game gameModel) {
        this.gameModel = gameModel;
    }

    public Game getGameModel() {
        return gameModel;
    }

    public void setGameModel(Game gameModel) {
        this.gameModel = gameModel;
    }


    public Game.State getGameState() {
        return gameModel.getGameState();
    }

    public Game.Piece[][] getBoardValue() {
        return Arrays.copyOf(gameModel.getBoardValue(), gameModel.getBoardValue().length);
    }

    public int[][] getWiningPath() {
        return Arrays.copyOf(gameModel.getWiningPath(), gameModel.getWiningPath().length) ;
    }

    public void updateGame(Game.Move move) throws GameError {
        var gameState = gameModel.getGameState();
        if(gameState == Game.State.Stopped || gameState == Game.State.NoughtWin || gameState == Game.State.CrossWin) {
            return;
        }
        if(gameState == Game.State.NoughtTurn && move.piece() == Game.Piece.Cross){
            throw new GameError("A Cross cannot be placed during Nought turn");
        }
        if(gameState == Game.State.CrossTurn && move.piece() == Game.Piece.Naught){
            throw new GameError("A Nought cannot be placed during Cross turn");
        }

        int position = move.position();
        int row = position / 3; // integer division
        int col = position % 3;

        var boardValue = gameModel.getBoardValue();

        if(boardValue[row][col]!= Game.Piece.Vacant){
            throw new GameError("A piece cannot be placed here: The cell is not vacant");
        }

        boardValue[row][col] = move.piece();
        checkEndOfGame().ifPresentOrElse(endGameCondition -> {
            this.gameModel.setGameState(endGameCondition.state);
            this.gameModel.setWiningPath(endGameCondition.path);
        }, () -> {
            if(gameState == Game.State.CrossTurn) {
                gameModel.setGameState(Game.State.NoughtTurn);
            } else {
                gameModel.setGameState(Game.State.CrossTurn);
            }
        });
    }

    public void resetGame() {
        var boardValue = new Game.Piece[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                boardValue[i][j] = Game.Piece.Vacant;
            }
        }

        gameModel.setBoardValue(boardValue);
        gameModel.setGameState(Game.State.Stopped);
        gameModel.setWiningPath(null);
    }

    private record EndGame(int [][] path, Game.State state){
        static Optional<EndGame> ofOpt(int [][] path, Game.State state) {
            return Optional.of(new EndGame(path, state));
        }
    }
    private Optional<EndGame> checkEndOfGame() {
        var boardValue = gameModel.getBoardValue();
        for (int i = 0; i < 3; i++) {
            var sumOfRowI = IntStream.of(boardValue[i][0].value, boardValue[i][1].value, boardValue[i][2].value).sum();
            var sumOfColI = IntStream.of(boardValue[0][i].value,boardValue[1][i].value,boardValue[2][i].value).sum();
            if(sumOfRowI == 3) {
                return EndGame.ofOpt(new int[][]{{ i, 0 },{ i, 1 },{ i, 2 }}, Game.State.NoughtWin);
            }else if(sumOfRowI == 12) {
                return EndGame.ofOpt(new int[][]{{ i, 0 },{ i, 1 },{ i, 2 }}, Game.State.CrossWin);
            } else if (sumOfColI == 3) {
                return EndGame.ofOpt(new int[][]{{ 0, i },{ 1, i },{ 2, i }}, Game.State.NoughtWin);
            } else if (sumOfColI == 12) {
                return EndGame.ofOpt(new int[][]{{ 0, i },{ 1, i },{ 2, i }}, Game.State.CrossWin);
            }
        }

        var lDiagSum = IntStream.of(boardValue[0][0].value,boardValue[1][1].value,boardValue[2][2].value).sum();
        if(lDiagSum == 3 || lDiagSum == 12){
            var winner = lDiagSum == 3? Game.State.NoughtWin : Game.State.CrossWin;
            return EndGame.ofOpt(new int[][]{{ 0, 0 },{ 1, 1 },{ 2, 2 },},winner);
        }
        var rDiagSum = IntStream.of(boardValue[2][0].value, boardValue[1][1].value, boardValue[0][2].value).sum();
        if(rDiagSum == 3 || rDiagSum == 12) {
            var winner = rDiagSum == 3? Game.State.NoughtWin : Game.State.CrossWin;
            return EndGame.ofOpt(new int[][]{{ 2, 0 },{ 1, 1 },{ 0, 2 },}, winner);
        }

        var hasVacant = false;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if(boardValue[i][j] == Game.Piece.Vacant) {
                    hasVacant = true;
                    break;
                }
            }
            if(hasVacant){
                break;
            }
        }
        if(!hasVacant) {
            return EndGame.ofOpt(null, Game.State.Draw);
        }
        return Optional.empty();
    }

    public void assignWinner(PlayerRole role) {
        if(role == PlayerRole.Cross) {
            this.gameModel.setGameState(Game.State.CrossWin);
        }else {
            this.gameModel.setGameState(Game.State.NoughtWin);
        }
    }
}
