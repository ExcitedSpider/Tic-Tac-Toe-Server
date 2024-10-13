/*
 * Name: Chew-Yi
 * Surname: Feng
 * StudentID: 1431319
 */
package T3;

import T3.eventbus.AppEvents;
import T3.eventbus.MyEventbus;
import T3.models.ClientGame;
import T3.modelview.GUIEvents;
import T3.modelview.GameViewCtr;
import T3.modelview.ServerAliveChecker;
import T3.modelview.WaitingRoomViewCtr;
import T3.remote.ClientRemote;
import T3.utils.MyExecutor;
import T3.utils.ServerRmiResolver;
import T3Server.Logger.Logger;
import interfaces.common.DateUtil;
import interfaces.rmi.ClientRequest;
import interfaces.rmi.ServerRemoteInterface;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import models.ChatMessage;
import models.Game;
import models.PlayerRole;

import java.io.IOException;
import java.util.function.Consumer;

import static T3.UIConstants.DEFAULT_WINDOW_SIZE;

/**
 * JavaFX App
 */
public class App extends Application {
    private ClientRemote clientRemote;

    private Stage primaryStage;

    private ClientGame gameData;

    private GameViewCtr mainAppViewCtrl;

    @Override
    public void start(Stage stage) throws IOException {
        // Create client remote
        this.clientRemote = new ClientRemote();

        stage.setOnCloseRequest(w -> {
            System.exit(0);
        });
        this.primaryStage = stage;

        // start UI
        this.showWaitingRoom();
    }

    private void tryClearEffects()  {
        var username = GlobalState.get(GlobalState.Key.Username);

        if(username != null) {
            try {
                Logger.getInstance().logInfo("User logout");
                ServerRemoteInterface serverRemote = ServerRmiResolver.getInstance().getServerRemote();
                serverRemote.clientLogout(new ClientRequest<>(username, null));
            } catch (Exception e) {
                Logger.getInstance().logErr("Cannot clear effects: Server is dead");
            }
        }
    }

    private void showWaitingRoom() throws IOException {
        var waitingRoomLoader = new FXMLLoader(getClass().getResource("waiting-room.fxml"));
        var waitingRoomScene = new Scene(waitingRoomLoader.load(), DEFAULT_WINDOW_SIZE[0], DEFAULT_WINDOW_SIZE[1]);
        var viewController = (WaitingRoomViewCtr) waitingRoomLoader.getController();
        viewController.setUsername(GlobalState.get(GlobalState.Key.Username));

        Consumer<AppEvents.MatchEvent> onMatch = matchEvent -> {
            Logger.getInstance().logInfo("Match Success. Change View...");
            Platform.runLater(() -> {
                try {
                    this.showGameScene(matchEvent.role(), matchEvent.game());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        };
        MyEventbus.getInstance().addEventListener(AppEvents.MatchEvent.class, onMatch);

        waitingRoomScene.addEventHandler(T3.modelview.GUIEvents.LoginEvent, w -> {
            var username = w.username;
            viewController.setLoading(true);
            try {
                var registerResponse = ServerRmiResolver.getInstance().getServerRemote().login(username, clientRemote);
                Logger.getInstance().logInfo("Register" + registerResponse);
                if (registerResponse.code != 200) {
                    throw new Exception("Failed to login to server" + registerResponse.message);
                }
                GlobalState.put(GlobalState.Key.Username, w.username);
                if(registerResponse.data.isReconnected()) {
                    onMatch.accept(new AppEvents.MatchEvent(
                            registerResponse.data.role(),
                            registerResponse.data.game()
                    ));
                }

            } catch (Exception e) {
                viewController.setLoading(false);
                Logger.getInstance().logErr("Login Failed" + e.getMessage());
                viewController.setErrorMessage(e.getMessage());
                throw new RuntimeException(e);
            }

        });

        primaryStage.setScene(waitingRoomScene);
        primaryStage.show();
    }

    private void popErrorMessage(Exception e) {
        Logger.getInstance().logErr(e);
        if(this.mainAppViewCtrl != null) {
            this.mainAppViewCtrl.setErrorMessage(e.getMessage());
        }
    }

    private void showGameScene(PlayerRole role, Game game) throws IOException {
        var username = GlobalState.get(GlobalState.Key.Username);

        var appLoader = new FXMLLoader(getClass().getResource("app.fxml"));
        Scene gameAppScene = new Scene(appLoader.load(), DEFAULT_WINDOW_SIZE[0], DEFAULT_WINDOW_SIZE[1]);
        var viewCtr = (GameViewCtr) appLoader.getController();
        this.mainAppViewCtrl = viewCtr;
        var clientGame = new ClientGame(role, game, username);
        viewCtr.setGame(clientGame);
        this.gameData = clientGame;

        gameAppScene.addEventHandler(GUIEvents.Move, playerMoveHandler(username, viewCtr));
        gameAppScene.addEventHandler(GUIEvents.EXIT, playerExitHandler(viewCtr, "Thanks for playing"));
        gameAppScene.addEventHandler(GUIEvents.SENT_CHAT, getSendChatMessageEventHandler(this.gameData));
        gameAppScene.addEventHandler(GUIEvents.Rematch, handleRematchRequest(username));
        MyEventbus.getInstance().addEventListener(AppEvents.UpdateGameEvent.class, event -> {
            this.gameData = new ClientGame(role, event.game(), username);
            Platform.runLater(() -> viewCtr.setGame(event.game()));
        });

        primaryStage.setScene(gameAppScene);
        primaryStage.show();

        var serverAliveChecker = new ServerAliveChecker();
        serverAliveChecker.start((w) -> {
            Logger.getInstance().logErr("Cannot connect to server. Client would exit.");
            var exit = playerExitHandler(mainAppViewCtrl, "Server Unavailable\nGame would exit soon"
            );
            exit.handle(null);
        });
    }

    private EventHandler<? super GUIEvents.RematchEvent> handleRematchRequest(String username) {
        return (w) -> {
            try {
                ServerRmiResolver.getInstance().getServerRemote().rematch(username);
                this.mainAppViewCtrl.setRematching(true);
            } catch (Exception e) {
                this.mainAppViewCtrl.setRematching(false);
                popErrorMessage(e);
                throw new RuntimeException(e);
            }
        };
    }

    private static EventHandler<GUIEvents.SendChatMessage> getSendChatMessageEventHandler(ClientGame game) {
        return event -> {
            try {
                var remoteServer = ServerRmiResolver.getInstance().getServerRemote();
                var username = game.getUserName();
                var gameSpecifier = game.getGameData().getGameSpecifier();
                remoteServer.postChatMessage(
                        new ClientRequest<>(username, new ServerRemoteInterface.PostChatRequest(
                                gameSpecifier,
                                new ChatMessage(event.message,username,DateUtil.now())
                        ))
                );
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }

    private EventHandler<GUIEvents.ExitEvent> playerExitHandler(GameViewCtr viewCtr, String message) {
        return w -> {
            Platform.runLater(() -> {
                viewCtr.displayTerminalMessage(message);
            });
            this.tryClearEffects();

            MyExecutor.getInstance().execute(() -> {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                Platform.exit();
                System.exit(0);
            });
        };
    }

    private static EventHandler<GUIEvents.MoveEvent> playerMoveHandler(String usertoken, GameViewCtr viewCtr) {
        return w -> {
            try {
                var remoteServer = ServerRmiResolver.getInstance();
                var updateGameResp = remoteServer.getServerRemote().updateGame(
                        new ClientRequest<>(
                                usertoken,
                                new ServerRemoteInterface.UpdateGameData(viewCtr.getGame().getGameSpecifier(), w.move)
                        ));
                var newGame = updateGameResp.data;
                viewCtr.setGame(newGame);
            } catch (Exception e) {
                viewCtr.setErrorMessage(e.getMessage());
                throw new RuntimeException(e);
            }
        };
    }
    

    public static void main(String[] args) {
        if(args.length < 3) {
            System.out.println("""
                    Invalid arguments: lack of basic arguments: server location and user name.
                    Example:
                    java -jar app.jar Chew 127.0.0.1 8899
                    """);
            System.exit(0);
            return;
        }

        GlobalState.put(GlobalState.Key.Username,args[0]);
        GlobalState.put(GlobalState.Key.ServerAddress,args[1]);
        GlobalState.put(GlobalState.Key.ServerPort,args[2]);
        launch();
    }
}