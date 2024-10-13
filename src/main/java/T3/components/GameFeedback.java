package T3.components;/*
 * Name: Chew-Yi
 * Surname: Feng
 * StudentID: 1431319
 */

import T3.UIConstants;
import T3.models.ClientGame;
import T3.modelview.GUIEvents;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import models.Game;
import models.PlayerRole;

public class GameFeedback extends VBox {

    private ClientGame game;

    public GameFeedback(ClientGame game) {
        super();
        this.game = game;
        this.setSpacing(8);
        var gameStatus = renderGameStatus();

        this.getChildren().addAll(gameStatus);
    }

    private Node renderGameStatus() {
        var gameState = game.getGameData().getGameState();
        HBox hbox = new HBox();
        if (gameState == Game.State.Stopped) {
            renderStopped(hbox);
        } else if (gameState == Game.State.CrossWin || gameState == Game.State.NoughtWin) {
            renderWined(hbox);
        } else if (gameState == Game.State.Draw) {
            renderWined(hbox);
        } else if (game.isSuspended()) {
            renderSuspended(hbox);
        } else {
            renderPlaying(hbox, gameState);
        }
        if (game.getGameData().getStateMessage() != null && !game.getGameData().getStateMessage().isEmpty()) {
            var serverMsg = new Label(game.getGameData().getStateMessage());
            serverMsg.setWrapText(true);
            this.getChildren().add(serverMsg);
        }
        return hbox;
    }

    private void renderSuspended(HBox hbox) {
        var cpnt = new HBox(renderIndicator(),
                new Label("Please wait for your opponent to reconnect..."));
        cpnt.setSpacing(4);
        cpnt.setAlignment(Pos.CENTER_LEFT);
        hbox.getChildren().add(cpnt);
    }

    private void renderPlaying(HBox content, Game.State gameState) {
        content.setAlignment(Pos.BASELINE_LEFT);

        var isUserTurn = gameState == Game.State.CrossTurn && game.getRole() == PlayerRole.Cross
                || gameState == Game.State.NoughtTurn && game.getRole() == PlayerRole.Naught;

        var indicator = renderIndicator();
        var label = new Label(isUserTurn ?
                "Your turn!. Click to make a move!" :
                "Please wait for your opponent...");

        var message = new HBox(
                indicator,
                label
        );
        message.setAlignment(Pos.CENTER_LEFT);
        message.setSpacing(8);


        content.getChildren().addAll(message);
    }

    private Node renderIndicator() {
        var gameState = game.getGameData().getGameState();
        var isUserTurn = gameState == Game.State.CrossTurn && game.getRole() == PlayerRole.Cross
                || gameState == Game.State.NoughtTurn && game.getRole() == PlayerRole.Naught;

        var isUserWin = gameState == Game.State.CrossWin && game.getRole() == PlayerRole.Cross
                || gameState == Game.State.NoughtWin && game.getRole() == PlayerRole.Naught;
        ;
        if (isUserWin || isUserTurn && !game.isSuspended()) {
            return new Circle(4, UIConstants.SuccessColor);
        } else {
            return new Circle(4, UIConstants.NetrualColor);
        }
    }

    private void renderWined(HBox content) {
        var restartBtn = new Button("Rematch");
        restartBtn.setOnAction(w -> {
            this.fireEvent(new GUIEvents.RematchEvent());
        });

        var gameState = game.getGameData().getGameState();
        String text;
        if (gameState != Game.State.Draw) {
            text = "Player " + game.getGameData().getPlayerByRole(
                    gameState == Game.State.NoughtWin ? PlayerRole.Naught : PlayerRole.Cross
            ) + (gameState == Game.State.NoughtWin ? "(O)" : "(X)") + " Wins! Congratulation!";
        } else {
            text = "Draw! Good Game.";
        }


        content.setAlignment(Pos.BASELINE_LEFT);
        content.setSpacing(4);
        content.getChildren().addAll(
                renderIndicator(),
                new Label(text),
                restartBtn
        );
    }

    private void renderStopped(HBox content) {
        content.setAlignment(Pos.BASELINE_LEFT);
        var startBtn = new Button("Start");
        startBtn.setOnMouseClicked(w -> {
            this.fireEvent(new GUIEvents.StartGameEvent());
        });
        content.getChildren().addAll(
                new Label("Click Start to make a dual!"),
                startBtn
        );
    }
}
