import javax.swing.*;
import java.util.Timer;
import java.util.TimerTask;

public class TimerManager {
    private int timeRemaining;
    private int initialTimeRemaining;
    private double caloriesPerMinute;
    private Timer timer;
    private boolean isRunning;

    // 초기 시간과 칼로리를 설정하는 생성자
    public TimerManager(int hours, int minutes, int seconds, double caloriesPerMinute) {
        this.initialTimeRemaining = (hours * 3600) + (minutes * 60) + seconds;
        this.timeRemaining = initialTimeRemaining;
        this.caloriesPerMinute = caloriesPerMinute;
        this.isRunning = false;
    }

    // 경과된 시간(초) 반환
    public int getElapsedSeconds() {
        return initialTimeRemaining - timeRemaining;
    }

    // 소모 칼로리 계산
    public double calculateCalories(int elapsedSeconds) {
        double elapsedMinutes = elapsedSeconds / 60.0;
        return elapsedMinutes * caloriesPerMinute;
    }

    // 타이머 실행 여부 반환
    public boolean isRunning() {
        return isRunning;
    }

    // 타이머 시작
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

    // 타이머 중지
    public void stopTimer() {
        if (timer != null) timer.cancel();
        isRunning = false;
    }

    // 타이머 초기화
    public void resetTimer(JLabel timerLabel) {
        stopTimer();
        timeRemaining = initialTimeRemaining;
        timerLabel.setText(formatTime(initialTimeRemaining));
    }

    // 초 단위를 "hh:mm:ss" 형식으로 변환
    public String formatTime(int totalSeconds) {
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    // 현재 남은 시간을 "hh:mm:ss" 형식으로 반환
    public String formatTime() {
        return formatTime(timeRemaining);
    }
}
