package T3.components;/*
 * Name: Chew-Yi
 * Surname: Feng
 * StudentID: 1431319
 */

import T3.eventbus.AppEvents;
import T3.eventbus.MyEventbus;
import T3.modelview.GUIEvents;
import T3Server.Logger.Logger;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

public class CountDown extends VBox {
    private final int countDownSeconds;
    private static final int DEFAULT_COUNTDOWN = 20;

    private volatile int secondsLeft;

    private boolean isUserTurn;

    public void setIsUserTurn(boolean userTurn) {
        isUserTurn = userTurn;
    }

    private Timer countdownTimer = null;

    private boolean suspended = false;

    public CountDown() {
        this.countDownSeconds = DEFAULT_COUNTDOWN;
        this.secondsLeft = countDownSeconds;
    }

    public void setSuspended(boolean suspended) {
        if(this.suspended != suspended) {
            if(suspended) {
                suspendCounting();
            }else {
                resumeCounting();
            }
            this.suspended = suspended;
        }
    }

    public void startCountDown() {
        if(countdownTimer != null) {
            countdownTimer.cancel();
            countdownTimer = null;
        }
        this.secondsLeft = countDownSeconds;
        this.render();


        Consumer<AppEvents.Signal> signalhandler = signal -> {
            switch (signal.signal()) {
                case GameSuspend -> {
                    this.suspendCounting();
                }
                case GameResume -> {
                    this.resumeCounting();
                }
            }
        };
        MyEventbus.getInstance().addEventListener(AppEvents.Signal.class, signalhandler);
        this.sceneProperty().addListener((observableValue, oldValue, newValue) -> {
            if (oldValue != null && newValue == null) {
                MyEventbus.getInstance().removeEventListener(AppEvents.Signal.class, signalhandler);
            }
        });

        countDownFromSecondsLeft();
    }

    private void countDownFromSecondsLeft() {
        countdownTimer = new Timer();
        countdownTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if(CountDown.this.secondsLeft > 0) {
                    CountDown.this.secondsLeft -= 1;
                    Platform.runLater(CountDown.this::render);
                } else  {
                    if(isUserTurn) {
                        CountDown.this.triggerCountdownComplete();
                    }
                    if(countdownTimer!= null) {
                        countdownTimer.cancel();
                        countdownTimer = null;
                    }
                }
            }
        }, 1000, 1000);
    }

    private void suspendCounting() {
        if(countdownTimer!=null) {
            countdownTimer.cancel();
            countdownTimer = null;
        }
        suspended = true;

        Platform.runLater(this::render);
    }

    private void resumeCounting() {
        if(countdownTimer!=null) {
            countdownTimer.cancel();
            countdownTimer = null;
        }
        suspended = false;
        this.countDownFromSecondsLeft();
    }

    private void triggerCountdownComplete() {
        Platform.runLater(() -> {
            this.fireEvent(new GUIEvents.CountDownComplete());
        });
    }


    private void render() {
        this.getChildren().clear();
        this.setAlignment(Pos.CENTER);

        var timeIndicator = new ProgressBar();
        timeIndicator.setProgress((double) secondsLeft / countDownSeconds);

        var leftTimeLabel = new Label(countDownSeconds + "Seconds");
        leftTimeLabel.setText(secondsLeft + " seconds left");

        var indicatorText = new Text("Make a move in time");

        if(suspended) {
            timeIndicator.setDisable(true);
            indicatorText.setText("Game Suspended");
        }

        this.getChildren().addAll(
                indicatorText,
                timeIndicator,
                leftTimeLabel
        );
    }
}
