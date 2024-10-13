package T3.components;/*
 * Name: Chew-Yi
 * Surname: Feng
 * StudentID: 1431319
 */

import T3.models.ClientGame;
import T3.utils.MyExecutor;
import T3.utils.ServerRmiResolver;
import interfaces.rmi.ServerRemoteInterface;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import models.PlayerRole;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;
import java.util.stream.Collectors;

import static T3.UIConstants.*;

public class GameInfoDescription extends VBox {
    private ClientGame game;

    private final HashMap<String, ServerRemoteInterface.PlayerRankingQuery> playerRankingCache = new HashMap<>();

    public GameInfoDescription() {
        this.getChildren().add(new Label("Tik-Tac-Toe"));
    }

    public GameInfoDescription(ClientGame game) {
        this.game = game;
        this.fetchAndUpdatePlayerRanking();
        this.render();
    }

    public void setGame(ClientGame game) {
        this.game = game;
        this.fetchAndUpdatePlayerRanking();
        this.render();
    }

    void render() {
        this.getChildren().clear();
        this.setSpacing(16);
        this.setAlignment(Pos.CENTER_LEFT);
        this.setPadding(new Insets(8));

        var currentUserName = game.getUserName();

        var playerRole = game.getRole();

        var playerView = new KVview(Painter.paintPieceByRole(playerRole),
                new Label(currentUserName + "(You)" + rankingString(currentUserName))
        );

        var opponentUserName = Arrays.stream(game.getGameData().getPlayers())
                .filter(name -> !Objects.equals(name, currentUserName))
                .findFirst();

        var opponentRole = playerRole == PlayerRole.Cross ? PlayerRole.Naught : PlayerRole.Cross;
        Node opponentView;
        if(opponentUserName.isPresent()) {
            var username = opponentUserName.get();
            opponentView = new KVview(Painter.paintPieceByRole(opponentRole), new Label(username + rankingString(username)));
        }else {
            opponentView = new KVview(Painter.paintPieceByRole(opponentRole), new Label("Unknown"));
        }

        this.getChildren().addAll(
                playerView,
                opponentView
        );
    }

    private String rankingString(String username) {
        if(!this.playerRankingCache.containsKey(username)){
            return "";
        }
        var playerRanking = playerRankingCache.get(username).ranking();
        if(playerRanking!=null) {
            return " #" + (playerRanking + 1);
        }
        return "";
    }

    private void fetchAndUpdatePlayerRanking() {
        var noRankings = Arrays.stream(game.getGameData().getPlayers())
//                .filter(playerName -> !this.playerRankingCache.containsKey(playerName))
                .collect(Collectors.toSet());

        if(!noRankings.isEmpty()) {
            MyExecutor.getInstance().execute(() -> {
                var needToQuery = noRankings.stream().toList();

                try {
                    var playerRankings = ServerRmiResolver.getInstance().getServerRemote().getPlayerRanking(needToQuery);
                    if(playerRankings.code == 200) {
                        playerRankings.data.forEach(item -> {
                            GameInfoDescription.this.playerRankingCache.put(item.username(), item);
                        });
                        Platform.runLater(GameInfoDescription.this::render);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }

    }



    static class KVview extends HBox {
        private final Node key;
        private final Node value;

        public KVview(String key, String value) {
            var keyLabel = new Label(key.stripTrailing() + ":");
            keyLabel.setTextFill(SecondaryTextColor);
            this.key =  keyLabel;

            var valueLabel = new Label(value);
            valueLabel.setWrapText(true);
            this.value = valueLabel;
            this.render();
        }

        public KVview(Node key, Node value) {
            this.key = key;
            this.value = value;
            this.render();
        }

        void render() {
            this.getChildren().clear();
            this.setSpacing(2);
            this.getChildren().addAll(key,value);
        }
    }

    static class Painter {
        static final int PIECE_SIZE = 10;

        static Node paintPieceByRole(PlayerRole role) {
            if(role == PlayerRole.Cross) {
                Line line1 = new Line(0, PIECE_SIZE, PIECE_SIZE, 0);
                Line line2 = new Line(0, 0, PIECE_SIZE, PIECE_SIZE);
                line1.setStroke(CrossColor);
                line1.setStrokeWidth(PIECE_STORE_WIDTH );
                line2.setStroke(CrossColor);
                line2.setStrokeWidth(PIECE_STORE_WIDTH);

                var node = new Group(line1, line2);
                node.maxWidth(12);
                return node;
            }else if(role == PlayerRole.Naught){
                var radius = (double) PIECE_SIZE / 2;
                Circle cir =  new Circle(radius, radius, radius);
                cir.setFill(Color.TRANSPARENT);
                cir.setStroke(NoughtColor);
                cir.setStrokeWidth(PIECE_STORE_WIDTH);

                var node = new HBox(cir);
                node.maxWidth(12);
                return node;
            }else {
                throw new RuntimeException("Unknown Player Role");
            }
        }
    }
}
