package com.chillingvan.canvasglsample.comparePerformance;

import java.util.Timer;
import java.util.TimerTask;

public class RefreshTimer {

        private Timer refreshTimer;
        private TimerTask timerTask;
        private boolean isTimerRunning;
        private Runnable timerRunnable;

        public void init(Runnable timerRunnable, int refreshIntervalTime) {
            this.timerRunnable = timerRunnable;
            if (refreshIntervalTime > 0 && refreshTimer == null) {
                refreshTimer = new Timer();
            }
        }

        public void run(int refreshIntervalTime) {
            if (refreshTimer != null && !isTimerRunning) {
                this.timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        timerRunnable.run();
                    }
                };
                isTimerRunning = true;
                refreshTimer.schedule(timerTask, refreshIntervalTime, refreshIntervalTime);
            }
        }

        public void stop() {
            if (timerTask != null) {
                isTimerRunning = false;
                timerTask.cancel();
            }
        }

        public void end() {
            if (refreshTimer != null) {
                refreshTimer.cancel();
                refreshTimer = null;
            }
        }
    }