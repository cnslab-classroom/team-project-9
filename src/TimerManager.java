import javax.swing.*;
import java.util.Timer;
import java.util.TimerTask;

public class TimerManager {
    private int timeRemaining;
    private int initialTimeRemaining;
    private double caloriesPerMinute;
    private Timer timer;
    private boolean isRunning;

    public TimerManager(int hours, int minutes, int seconds, double caloriesPerMinute) {
        this.initialTimeRemaining = (hours * 3600) + (minutes * 60) + seconds;
        this.timeRemaining = initialTimeRemaining;
        this.caloriesPerMinute = caloriesPerMinute;
        this.isRunning = false;
    }

    public int getElapsedSeconds() {
        return initialTimeRemaining - timeRemaining;
    }

    public double calculateCalories(int elapsedSeconds) {
        double elapsedMinutes = elapsedSeconds / 60.0;
        return elapsedMinutes * caloriesPerMinute;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void startTimer(JLabel timerLabel) {
        if (timer != null) timer.cancel();
        timer = new Timer();
        isRunning = true;

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (timeRemaining > 0 && isRunning) {
                    timeRemaining--;
                    timerLabel.setText(formatTime(timeRemaining));
                } else {
                    timer.cancel();
                    isRunning = false;
                }
            }
        }, 0, 1000);
    }

    public void stopTimer() {
        if (timer != null) timer.cancel();
        isRunning = false;
    }

    public void resetTimer(JLabel timerLabel) {
        stopTimer();
        timeRemaining = initialTimeRemaining;
        timerLabel.setText(formatTime(initialTimeRemaining));
    }

    public String formatTime(int totalSeconds) {
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    public String formatTime() {
        return formatTime(timeRemaining);
    }
}
