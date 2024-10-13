package T3.components.board;
/*
 * Name: Chew-Yi
 * Surname: Feng
 * StudentID: 1431319
 */

import T3.components.GameFeedback;
import T3.eventbus.AppEvents;
import T3.eventbus.MyEventbus;
import T3.models.ClientGame;
import T3.modelview.GUIEvents;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import models.Game;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Group;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import models.PlayerRole;

import java.util.Arrays;
import java.util.function.Consumer;

import static T3.UIConstants.*;

public class Board extends BorderPane {
    private final GridPane playZone;
    private final StackPane messagePad;
    private ClientGame game;
    private Tile[][] tiles = new Tile[3][3];

    public Board(ClientGame game) {
        this.game = game;
        this.setPadding(new Insets(16));

        this.playZone = new GridPane();
        this.setCenter(playZone);
        messagePad = new StackPane();

        var top = new VBox(this.messagePad);
        top.setPadding(new Insets(0, 0, 8, 0));

        this.setTop(top);
        playZone.setMinSize(TILE_SIZE * 3, TILE_SIZE * 3);
        this.layoutGrid(this.playZone);

        this.initBoard();
        this.renderBoard();
    }


    private void renderBoard() {
        this.renderMessagePad();
        this.renderGameArea();
        this.clearWinningPathIfStart();
    }

    private void clearWinningPathIfStart() {
        var state = game.getGameData().getGameState();
        if(state != Game.State.CrossWin && state!= Game.State.NoughtWin) {
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    this.tiles[i][j].getStyleClass().clear();
                }
            }
        }
    }

    private void renderGameArea() {
        var boardValue = game.getGameData().getBoardValue();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                var cellValue = boardValue[i][j];
                var cell = this.tiles[i][j];
                if (cellValue == Game.Piece.Vacant) {
                    cell.setState(TileStatus.Vacant);
                } else if (cellValue == Game.Piece.Naught) {
                    cell.setState(TileStatus.Nought);
                } else if (cellValue == Game.Piece.Cross) {
                    cell.setState(TileStatus.Cross);
                }
            }
        }
        var gameState = game.getGameData().getGameState();
        if(gameState == Game.State.CrossWin || gameState == Game.State.NoughtWin) {
            // winning path
            var wPath = game.getGameData().getWiningPath();
            if(wPath != null) {
                Arrays.stream(wPath).forEach(pair -> {
                    var row = pair[0];
                    var col = pair[1];
                    this.tiles[row][col].getStyleClass().add("fulfilled-cell");
                });
            }
        }

    }

    private void renderMessagePad() {
        var pad = new GameFeedback(game);
        messagePad.getChildren().clear();
        messagePad.getChildren().add(pad);
        messagePad.addEventHandler(GUIEvents.StartGame, w-> {
            this.fireEvent(new GUIEvents.StartGameEvent());
            this.renderMessagePad();
        });
    }

    private void layoutGrid(GridPane playZone) {
        for (int grid = 0; grid < 3; grid++) {
            ColumnConstraints colConstraints = new ColumnConstraints();
            colConstraints.setPercentWidth(100.0 / 3);
            playZone.getColumnConstraints().add(colConstraints);
            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setPercentHeight(100.0 / 3);
            playZone.getRowConstraints().add(rowConstraints);
        }
        playZone.setStyle("-fx-background-color: white");
    }

    private void initBoard() {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                Tile tile = new Tile(row * 3 + col);

                var gameState = game.getGameData().getGameState();
                var isUserTurn = gameState == Game.State.CrossTurn && game.getRole() == PlayerRole.Cross
                        || gameState == Game.State.NoughtTurn && game.getRole() == PlayerRole.Naught;

                if(isUserTurn) {
                    tile.addEventHandler(Tile.TileEvent.MOVE, oneMoveHandler);
                }

                playZone.add(tile, col, row);
                tiles[row][col] = tile;
            }
        }
    }

    private final EventHandler<Tile.TileEvent> oneMoveHandler = w -> {
        var state = game.getGameData().getGameState();
        Game.Piece currentPiece = null;
        if (state == Game.State.CrossTurn) {
            currentPiece = Game.Piece.Cross;
        } else if (state == Game.State.NoughtTurn) {
            currentPiece = Game.Piece.Naught;
        }
        this.fireEvent(new GUIEvents.MoveEvent(new Game.Move(w.data.tileIndex(), currentPiece)));
        this.renderBoard();
    };

    public void setRematching(boolean b) {
        if(b) {
            this.setTop(new Label("Rematching..."));
        }else {
            this.setTop(new GameFeedback(game));
        }
    }
}

class Tile extends StackPane {
    private TileStatus state = TileStatus.Vacant;


    public Tile(int index) {
        EventHandler<Event> onClickTile = w -> {
            if (this.state == TileStatus.Vacant) {
                this.fireEvent(new TileEvent(TileEvent.MOVE, new TileEvent.TileEventData(index, this)));
            }
        };
        this.setOnMouseClicked(onClickTile);


        this.setStyle("""
                -fx-border-style: solid;
                -fx-border-color: #e9e9e9;
                -fx-border-width: 1px;
                """);
    }

    public TileStatus getState() {
        return this.state;
    }

    public void setState(TileStatus newsStatus) {
        if(this.state == newsStatus){
            return;
        }
        this.state = newsStatus;
        this.getChildren().clear();
        switch (newsStatus) {
            case Cross -> this.getChildren().add(new Cross());
            case Nought -> this.getChildren().add(new Nought());
            case Vacant -> {this.getChildren().clear();}
        }
    }


    private static class Cross extends StackPane {
        public Cross() {
            Line line1 = new Line(10, 90, 90, 10);
            Line line2 = new Line(10, 10, 90, 90);
            line1.setStroke(CrossColor);
            line1.setStrokeWidth(PIECE_STORE_WIDTH);
            line2.setStroke(CrossColor);
            line2.setStrokeWidth(PIECE_STORE_WIDTH);
            var group = new Group(line1, line2);
            this.getChildren().add(group);
        }
    }

    private static class Nought extends StackPane {
        public Nought() {
            Circle cir = new Circle(45, 45, 45);
            cir.setFill(Color.TRANSPARENT);
            cir.setStroke(NoughtColor);
            cir.setStrokeWidth(PIECE_STORE_WIDTH);
            this.getChildren().add(cir);
        }
    }

    public static class TileEvent extends Event {
        public static final EventType<TileEvent> MOVE = new EventType<>(Event.ANY, "MOVE");

        record TileEventData(int tileIndex, Tile tile) {
        }

        ;

        public final TileEventData data;

        public TileEvent(EventType<? extends Event> eventType) {
            super(eventType);
            this.data = null;
        }

        public TileEvent(EventType<? extends Event> eventType, TileEventData tileEventData) {
            super(eventType);
            this.data = tileEventData;
        }
    }

}

enum TileStatus {
    Nought(1),
    Cross(4),
    Vacant(0);

    final public int value;

    TileStatus(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return Integer.toString(value);
    }

}

