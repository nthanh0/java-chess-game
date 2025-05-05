package chess.util;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class TimerClock implements Runnable {
    private int remainingTimeMillis;
    private final Object lock = new Object();
    private boolean isPaused = false;
    private volatile boolean isFinished = false;

    public TimerClock(LocalTime timerTime) {
        this.remainingTimeMillis = timerTime.toSecondOfDay() * 1000;
    }

    public TimerClock(String timerTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalTime time = LocalTime.parse(timerTime, formatter);
        this.remainingTimeMillis = time.toSecondOfDay() * 1000;
    }

    public TimerClock(double timerTimeMinutes) {
        this.remainingTimeMillis = (int)(timerTimeMinutes * 60 * 1000);
    }

    public void pause() {
        isPaused = true;
    }

    public void resume() {
        synchronized (lock) {
            isPaused = false;
            lock.notify();
        }
    }

    @Override
    public void run() {
        while (remainingTimeMillis > 0) {
            synchronized (lock) {
                while (isPaused) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        System.out.println("\nTimer interrupted while paused.");
                        return;
                    }
                }
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                System.out.println("\nTimer interrupted.");
                return;
            }

            remainingTimeMillis -= 100;
        }
        isFinished = true;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public int getRemainingTimeMillis() {
        return remainingTimeMillis;
    }

    public String getTimeLeftString() {
        int millis = Math.max(0, remainingTimeMillis);
        int remainingTimeSeconds = millis / 1000;
        int hours = remainingTimeSeconds / 3600;
        int minutes = (remainingTimeSeconds % 3600) / 60;
        int seconds = remainingTimeSeconds % 60;
        int tenths = (millis % 1000) / 100;

        if (hours > 0) {
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        } else if (remainingTimeSeconds < 10) {
            return String.format("00:%02d.%d", seconds, tenths);
        } else {
            return String.format("%02d:%02d", minutes, seconds);
        }
    }


    public void addTimeSeconds(int seconds) {
        remainingTimeMillis += seconds * 1000;
    }
    public void addTimeMillis(int milliseconds) { remainingTimeMillis += milliseconds; }

    public boolean isPaused() {
        return isPaused;
    }
}
