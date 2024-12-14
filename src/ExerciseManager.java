import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimerTask;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ExerciseManager {
    private static ExerciseManager instance; // 싱글톤 인스턴스

    // 싱글톤 인스턴스 반환 메서드
    public static ExerciseManager getExerciseManager() {
        if (instance == null) {
            instance = new ExerciseManager();
        }
        return instance;
    }

    // 운동 데이터 관리 필드
    private List<ExerciseData> exerciseDataList;
    private int timeRemaining;
    private int initialTimeRemaining;
    private double caloriesPerMinute;
    private java.util.Timer timer;
    private boolean isRunning;

    // 알람 관련 필드
    private String alarmTime = "";
    private javax.swing.Timer digitalClockTimer;

    // DateForm 관련 필드
    private JButton[] dates = new JButton[42];
    private JLabel jl_title = new JLabel();
    private int year, month, nowMonth, nowYear, today;
    private Calendar cal = Calendar.getInstance();

    // ExerciseManager 생성자
    private ExerciseManager() {
        this.exerciseDataList = new ArrayList<>();
        this.isRunning = false;

        // DateForm 초기화
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH) + 1;
        nowMonth = month;
        nowYear = year;
        today = cal.get(Calendar.DATE);
    }

    // 운동 데이터 클래스
    public static class ExerciseData {
        private double currentWeight;
        private double targetWeight;
        private int targetDays;
        private double dailyCaloriesToBurn;

        public ExerciseData(double currentWeight, double targetWeight, int targetDays) {
            this.currentWeight = currentWeight;
            this.targetWeight = targetWeight;
            this.targetDays = targetDays;
            this.dailyCaloriesToBurn = calculateDailyCaloriesToBurn();
        }

        private double calculateDailyCaloriesToBurn() {
            double totalWeightLoss = currentWeight - targetWeight;
            return (totalWeightLoss * 7700) / targetDays;
        }

        public double getDailyCaloriesToBurn() {
            return dailyCaloriesToBurn;
        }

        public int getTargetDays() {
            return targetDays;
        }

        @Override
        public String toString() {
            return String.format(
                "현재 몸무게: %.2fkg, 목표 몸무게: %.2fkg, 목표 기간: %d일, 하루 소모 칼로리: %.2fkcal",
                currentWeight, targetWeight, targetDays, dailyCaloriesToBurn
            );
        }
    }

    // 운동 데이터 추가
    public void addExerciseData(double currentWeight, double targetWeight, int targetDays) {
        ExerciseData newData = new ExerciseData(currentWeight, targetWeight, targetDays);
        exerciseDataList.add(newData);
    }

    // 마지막 운동 데이터 가져오기
    public ExerciseData getLastExerciseData() {
        if (exerciseDataList.isEmpty()) {
            return null;
        }
        return exerciseDataList.get(exerciseDataList.size() - 1);
    }

    // 타이머 관련 메서드
    public void setTimer(int hours, int minutes, int seconds, double caloriesPerMinute) {
        this.initialTimeRemaining = (hours * 3600) + (minutes * 60) + seconds;
        this.timeRemaining = initialTimeRemaining;
        this.caloriesPerMinute = caloriesPerMinute;
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
        timer = new java.util.Timer();
        isRunning = true;

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (timeRemaining > 0 && isRunning) {
                    timeRemaining--;
                    SwingUtilities.invokeLater(() -> timerLabel.setText(formatTime(timeRemaining)));
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

    // 알람 관련 메서드
    public void setAlarmTime(String time) {
        this.alarmTime = time;
    }

    public String getAlarmTime() {
        return alarmTime;
    }

    public void resetAlarmTime() {
        this.alarmTime = "";
    }

    public void updateDigitalClock(JLabel clockLabel) {
        if (digitalClockTimer != null) {
            digitalClockTimer.stop();
        }

        digitalClockTimer = new javax.swing.Timer(1000, e -> {
            String currentTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            clockLabel.setText(currentTime);
        });
        digitalClockTimer.start();
    }

    public void saveAlarmTime(JTextField alarmTimeField, JTextArea alarmDisplayField) {
        String time = alarmTimeField.getText().trim();
        if (!time.matches("\\d{2}:\\d{2}")) {
            JOptionPane.showMessageDialog(null, "올바른 형식으로 시간을 입력하세요! (예: 09:30)", "입력 오류", JOptionPane.ERROR_MESSAGE);
        } else {
            setAlarmTime(time);
            alarmDisplayField.setText("알림 시간: " + getAlarmTime());
            alarmTimeField.setText("");
            JOptionPane.showMessageDialog(null, "알림이 설정되었습니다: " + getAlarmTime(), "알림 설정", JOptionPane.INFORMATION_MESSAGE);
            scheduleAlarm(alarmDisplayField);
        }
    }

    public void scheduleAlarm(JTextArea displayField) {
        if (alarmTime.isEmpty()) return;

        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000);
                    String currentTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
                    if (currentTime.equals(alarmTime)) {
                        JOptionPane.showMessageDialog(null, "운동 시간입니다!", "알림", JOptionPane.INFORMATION_MESSAGE);
                        SwingUtilities.invokeLater(() -> {
                            displayField.setText("");
                            resetAlarmTime();
                        });
                        break;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public JPanel createDateFormPanel() {
        JPanel dateFormPanel = new JPanel(new BorderLayout());

        JButton jb_left = new JButton("◁");
        JButton jb_right = new JButton("▷");

        JPanel jp_north = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JPanel jp_center = new JPanel(new GridLayout(7, 7));

        String[] names = {"일", "월", "화", "수", "목", "금", "토"};
        for (String name : names) {
            JLabel jl = new JLabel(name, JLabel.CENTER);
            jp_center.add(jl);
            if ("일".equals(name)) jl.setForeground(Color.RED);
            else if ("토".equals(name)) jl.setForeground(Color.BLUE);
        }

        for (int i = 0; i < dates.length; i++) {
            dates[i] = new JButton();
            dates[i].setBorder(new LineBorder(Color.BLACK));
            int idx = i;

            dates[i].addActionListener(e -> {
                ExerciseData lastData = getLastExerciseData();

                if (lastData == null) {
                    JOptionPane.showMessageDialog(null,
                            "운동 데이터를 먼저 입력해주세요!",
                            "데이터 없음",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                int startDay = today;
                int endDay = startDay + lastData.getTargetDays() - 1;
                int selectedDay = Integer.parseInt(dates[idx].getText());

                if (year == nowYear && month == nowMonth && selectedDay < today) {
                    JOptionPane.showMessageDialog(null,
                            "지난 날짜는 선택할 수 없습니다.",
                            "날짜 제한",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                if (selectedDay >= startDay && selectedDay <= endDay) {
                    double dailyCalories = lastData.getDailyCaloriesToBurn();

                    JOptionPane.showMessageDialog(null,
                            String.format("%d년 %02d월 %02d일\n하루 소모해야 할 칼로리: %.2fkcal",
                                    year, month, selectedDay, dailyCalories),
                            "하루 소모 칼로리",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null,
                            "이 날짜는 설정된 감량 기간이 아닙니다.",
                            "날짜 범위 외",
                            JOptionPane.WARNING_MESSAGE);
                }
            });
            jp_center.add(dates[i]);
        }

        jp_north.add(jb_left);
        jp_north.add(jl_title);
        jp_north.add(jb_right);

        jb_left.addActionListener(e -> {
            if (year < nowYear || (year == nowYear && month <= nowMonth)) {
                JOptionPane.showMessageDialog(null, "지난 달은 확인할 수 없습니다.", "제한", JOptionPane.WARNING_MESSAGE);
                return;
            }

            month--;
            if (month < 1) {
                month = 12;
                year--;
            }
            refreshDate();
        });

        jb_right.addActionListener(e -> {
            month++;
            if (month > 12) {
                month = 1;
                year++;
            }
            refreshDate();
        });

        dateFormPanel.add(jp_north, BorderLayout.NORTH);
        dateFormPanel.add(jp_center, BorderLayout.CENTER);

        refreshDate();
        return dateFormPanel;
    }

    public void refreshDate() {
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month - 1);
        cal.set(Calendar.DATE, 1);

        int end = cal.getActualMaximum(Calendar.DATE);
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);

        jl_title.setText(String.format("%d년 %02d월", year, month));

        for (JButton date : dates) {
            date.setText("");
            date.setEnabled(false);
            date.setVisible(false);
        }

        int index = 0;
        for (int i = 1; i < dayOfWeek; i++) {
            index++;
        }

        for (int i = 1; i <= end; i++) {
            dates[index].setText(String.valueOf(i));
            dates[index].setVisible(true);
            dates[index].setEnabled(true);

            if (year == nowYear && month == nowMonth && i < today) {
                dates[index].setEnabled(false);
            }

            index++;
        }
    }

    //운동 관리 관련 메서드
    private double customCalories = 5.0; // 기본 값 (직접입력 분당 칼로리)

    // 시간과 분을 받아 총 소모 칼로리 계산
    public double calculateCaloriesWithTime(String exerciseType, double hours, double minutes) {
        double totalTimeInMinutes = (hours * 60) + minutes;

        if (totalTimeInMinutes <= 0) {
            throw new IllegalArgumentException("운동 시간은 0보다 커야 합니다.");
        }
        return calculateCalories(exerciseType, totalTimeInMinutes);
    }

    // 기존 칼로리 계산 메서드
    public double calculateCalories(String exerciseType, double timeInMinutes) {
        double caloriesPerMinute;

        switch (exerciseType) {
            case "걷기":
                caloriesPerMinute = 3.0; 
                break;
            case "달리기":
                caloriesPerMinute = 10.0;
                break;
            case "수영":
                caloriesPerMinute = 10.0;
                break;
            case "직접입력":
                caloriesPerMinute = customCalories; // 사용자가 설정한 값
                break;
            default:
                caloriesPerMinute = 5.0; // 기본 값
                break;
        }

        return timeInMinutes * caloriesPerMinute;
    }

    // 사용자 정의 분당 칼로리 설정
    public void setCustomCalories(double customCalories) {
        if (customCalories > 0) {
            this.customCalories = customCalories;
        } else {
            throw new IllegalArgumentException("분당 칼로리는 0보다 커야 합니다.");
        }
    }

    // 사용자 정의 분당 칼로리 값 가져오기
    public double getCustomCalories() {
        return customCalories;
    }

}
