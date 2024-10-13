package T3.modelview;/*
 * Name: Chew-Yi
 * Surname: Feng
 * StudentID: 1431319
 */
import T3.components.ChatPanel;
import T3.components.CountDown;
import T3.components.GameInfoDescription;
import T3.components.board.Board;
import T3.eventbus.AppEvents;
import T3.eventbus.MyEventbus;
import T3.models.ClientGame;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.util.Pair;
import models.Game;
import models.PlayerRole;

import java.util.ArrayList;
import java.util.Random;
import java.util.function.Consumer;

public class GameViewCtr {
    public BorderPane root;
    public StackPane boardWrap;
    public ChatPanel chatPanel;
    public CountDown countdown;
    public GameInfoDescription infoDesc;
    private ClientGame game;

    private Board board;

    public void setErrorMessage(String errorMessage) {
        var errText = new Text(errorMessage);
        root.setBottom(errText);
    }


    public void displayTerminalMessage(String message) {
        this.displayTerminalMessage(new Label(message));
    }

    /**
     *  Would clear the whole page
     *   Used only before exit!
     * @param message
     */
    public void displayTerminalMessage(Node message) {
        root.setLeft(null);
        root.setRight(null);
        root.setCenter(
                new StackPane(
                        message
                )
        );
    }

    public Game getGame() {
        return game.getGameData();
    }

    public void setGame(Game game) {
        var role = this.game.getRole();
        var username = this.game.getUserName();
        var newClientGame= new ClientGame(role, game, username);
        this.setGame(newClientGame);
    }



    public void setGame(ClientGame clientGame) {
        // TODO: Optimize performance: compare game status determine change
        this.game = clientGame;
        render(clientGame);
        var gameState = game.getGameData().getGameState();

        if(gameState == Game.State.CrossTurn || gameState == Game.State.NoughtTurn) {
            this.countdown.setVisible(true);
            var isUserTurn = gameState == Game.State.CrossTurn && game.getRole() == PlayerRole.Cross
                    || gameState == Game.State.NoughtTurn && game.getRole() == PlayerRole.Naught;
            this.countdown.setIsUserTurn(isUserTurn);
            this.countdown.startCountDown();
        }else {
            this.countdown.setVisible(false);
        }

    }

    private void render(ClientGame clientGame) {
        this.rerenderBoard(clientGame);
        infoDesc.setGame(clientGame);
    }

    @FXML
    public void initialize() {
        MyEventbus.getInstance().addEventListener(AppEvents.ReceiveChatMessage.class, event -> {
            Platform.runLater(() -> {
                chatPanel.putMessage(event.message());
            });
        });
        countdown.addEventHandler(GUIEvents.COUNT_DOWN_COMPLETE, w -> {
            var gameState = game.getGameData().getGameState();
            var isUserTurn = gameState == Game.State.CrossTurn && game.getRole() == PlayerRole.Cross
                    || gameState == Game.State.NoughtTurn && game.getRole() == PlayerRole.Naught;
            if(isUserTurn) {
                var randomMove = randomMove(game.getGameData());
                this.board.fireEvent(new GUIEvents.MoveEvent(randomMove));
            }
        });

        Consumer<AppEvents.Signal> signalHandler = signal -> {
            switch (signal.signal()) {
                case GameSuspend -> {
                    var newGame = ClientGame.copy(this.game);
                    newGame.setSuspended(true);
                    this.rerenderBoard(newGame);
                    countdown.setSuspended(true);
                }
                case GameResume -> {
                    var newGame = ClientGame.copy(this.game);
                    newGame.setSuspended(false);
                    this.rerenderBoard(newGame);
                    countdown.setSuspended(false);
                }
            }
        };
        MyEventbus.getInstance().addEventListener(AppEvents.Signal.class, signalHandler);
        root.sceneProperty().addListener((observableValue, oldValue, newValue) -> {
            if (oldValue != null && newValue == null) {
                MyEventbus.getInstance().removeEventListener(AppEvents.Signal.class, signalHandler);
            }
        });
    }

    private void rerenderBoard(ClientGame clientGame) {
        Platform.runLater(() -> {
            this.game = clientGame;
            board = new Board(clientGame); // all the client board logics are in Board component
            boardWrap.getChildren().clear();
            boardWrap.getChildren().add(board);
        });
    }

    static Game.Move randomMove(Game game) {
        var boardValue = game.getBoardValue();
        var vacentPositions = new ArrayList<Pair<Integer, Integer>>();

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if(boardValue[i][j] == Game.Piece.Vacant){
                    vacentPositions.add(new Pair<>(i, j));
                }
            }
        }

        Random random = new Random();
        int randomIndex = random.nextInt(vacentPositions.size());
        var randomPicked =  vacentPositions.get(randomIndex);
        var currentPiece = game.getGameState() == Game.State.CrossTurn ? Game.Piece.Cross : Game.Piece.Naught;

        return new Game.Move(randomPicked.getKey() * 3 + randomPicked.getValue(), currentPiece);

    }

    public void uiQuitAction(ActionEvent actionEvent) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Do you want to quit the game?", ButtonType.YES, ButtonType.NO);
        confirm.setGraphic(null);
        confirm.setHeaderText(null);
        confirm.showAndWait();

        if(confirm.getResult() == ButtonType.YES) {
            root.fireEvent(new GUIEvents.ExitEvent());
        }
    }

    public void setRematching(boolean b) {
        board.setRematching(b);
    }
}
