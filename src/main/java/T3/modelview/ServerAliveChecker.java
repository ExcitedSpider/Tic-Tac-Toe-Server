package T3.modelview;/*
 * Name: Chew-Yi
 * Surname: Feng
 * StudentID: 1431319
 */

import T3.GlobalState;
import interfaces.common.DateUtil;

import java.time.LocalDateTime;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

public class ServerAliveChecker {

    private static final int CHECK_INTERVAL_SECS = 5;
    private static final int UNFRESH_THRESHOLD_SECS = 10;
    private Timer timer;

    public record HeartBeatStopEvent(LocalDateTime lastHeartBeat, LocalDateTime now){};
    public void start(Consumer<HeartBeatStopEvent> handleHeartBeatStop) {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                String lastBeatTimeStr = GlobalState.get(GlobalState.Key.HeartBeatSince);

                // no record is a sin
                if(lastBeatTimeStr == null) {
                    handleHeartBeatStop.accept(new HeartBeatStopEvent(null, LocalDateTime.now()));
                    timer.cancel();
                } else {
                    LocalDateTime lastHeartBeat = DateUtil.parse(GlobalState.get(GlobalState.Key.HeartBeatSince));
                    LocalDateTime now = LocalDateTime.now();

                    var expireTime = lastHeartBeat.plusSeconds(UNFRESH_THRESHOLD_SECS);

                    if(now.isAfter(expireTime)) {
                        handleHeartBeatStop.accept(new HeartBeatStopEvent(lastHeartBeat, now));
                        timer.cancel();
                    }
                }
            }
        }, CHECK_INTERVAL_SECS * 1000, CHECK_INTERVAL_SECS * 1000);
    }

    public void cancel() {
        timer.cancel();
    }
}
