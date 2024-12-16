import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.Timer;

public class ExerciseManager {
    private JFrame frame;
    private JTextField currentWeightField, targetWeightField, daysField;
    private JTextArea logArea;
    private JPanel calendarPanel;
    private Calendar calendar;
    private JLabel selectedDateLabel;
    private double dailyCaloriesToBurn = 0.0;
    private Timer exerciseTimer;
    private int timeRemaining;
    private JLabel timerLabel;
    private boolean timerRunning = false;

// createExercisePanel 메서드 추가
public JPanel createExercisePanel() {
    // JPanel panel 변수를 선언하고 초기화
    JPanel panel = new JPanel(new BorderLayout());

    // 뒤로 가기 버튼
    JButton backButton = new JButton("뒤로가기");
    backButton.addActionListener(e -> JOptionPane.showMessageDialog(null, "뒤로가기 버튼 눌림"));
    panel.add(backButton, BorderLayout.NORTH);

    // 중앙 텍스트 영역
    JTextArea exerciseArea = new JTextArea("운동 관리 화면입니다.");
    exerciseArea.setEditable(false);
    JScrollPane scrollPane = new JScrollPane(exerciseArea);
    panel.add(scrollPane, BorderLayout.CENTER);

    // 하단 버튼 패널
    JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 10));
    JButton timerButton = new JButton("타이머 시작");
    JButton calorieButton = new JButton("소모 칼로리 보기");

    timerButton.addActionListener(e -> JOptionPane.showMessageDialog(null, "타이머 시작"));
    calorieButton.addActionListener(e -> JOptionPane.showMessageDialog(null, "소모 칼로리 보기"));

    buttonPanel.add(timerButton);
    buttonPanel.add(calorieButton);

    panel.add(buttonPanel, BorderLayout.SOUTH);

    return panel; // 완성된 panel 반환
}

public JPanel createMainPanel() {
    JPanel mainPanel = new JPanel(new BorderLayout());

    // 달력 패널 추가
    JPanel calendarPanel = createCalendarPanel();
    mainPanel.add(calendarPanel, BorderLayout.CENTER);

    // 버튼 패널 추가
    JPanel buttonPanel = createButtonPanel();
    mainPanel.add(buttonPanel, BorderLayout.SOUTH);

    return mainPanel;
}

// 버튼 패널 생성
private JPanel createButtonPanel() {
    JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 10));

    JButton timerButton = new JButton("타이머 시작");
    JButton calorieButton = new JButton("소모 칼로리 보기");
    JButton weightSettingButton = new JButton("감량 무게 설정");

    weightSettingButton.addActionListener(e -> showWeightSettingPanel());
    timerButton.addActionListener(e -> JOptionPane.showMessageDialog(null, "타이머 시작"));
    calorieButton.addActionListener(e -> JOptionPane.showMessageDialog(null, "소모 칼로리 보기"));

    buttonPanel.add(weightSettingButton);
    buttonPanel.add(timerButton);
    buttonPanel.add(calorieButton);
    
    return buttonPanel;
}
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ExerciseManager().createAndShowGUI());
    }

    // GUI 생성 메서드
    private void createAndShowGUI() {
        frame = new JFrame("운동 관리 시스템");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 600);
        frame.setLayout(new BorderLayout());

        // 상단 버튼 패널
        JPanel buttonPanel = new JPanel(new GridLayout(1, 4, 10, 10));
        JButton weightButton = new JButton("감량 무게 설정");
        JButton logButton = new JButton("운동 기록 보기");
        JButton timerButton = new JButton("타이머 & 칼로리 계산");
        JButton exitButton = new JButton("종료");

        buttonPanel.add(weightButton);
        buttonPanel.add(logButton);
        buttonPanel.add(timerButton);
        buttonPanel.add(exitButton);

        weightButton.addActionListener(e -> showWeightSettingPanel());
        logButton.addActionListener(e -> showExerciseLog());
        timerButton.addActionListener(e -> showTimerPanel());
        exitButton.addActionListener(e -> System.exit(0));

        // 중앙 달력 패널
        calendarPanel = createCalendarPanel();
        JPanel centerPanel = new JPanel(new BorderLayout());
        selectedDateLabel = new JLabel("선택한 날짜: 없음", JLabel.CENTER);
        centerPanel.add(selectedDateLabel, BorderLayout.NORTH);
        centerPanel.add(calendarPanel, BorderLayout.CENTER);

        // 하단 로그 영역
        logArea = new JTextArea("운동 기록이 여기에 표시됩니다.\n");
        logArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logArea);

        // 전체 컴포넌트 배치
        frame.add(buttonPanel, BorderLayout.NORTH);
        frame.add(centerPanel, BorderLayout.CENTER);
        frame.add(scrollPane, BorderLayout.SOUTH);
        frame.setVisible(true);
    }

    // 달력 패널 생성
    private JPanel createCalendarPanel() {
        JPanel panel = new JPanel(new GridLayout(6, 7, 5, 5));
        calendar = new GregorianCalendar(2024, Calendar.DECEMBER, 1);

        for (int i = 0; i < calendar.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
            JButton dayButton = new JButton(String.valueOf(i + 1));
            dayButton.addActionListener(e -> {
                int day = Integer.parseInt(((JButton) e.getSource()).getText());
                selectedDateLabel.setText("선택한 날짜: 2024년 12월 " + day + "일");
                showCaloriesForDay();
            });
            panel.add(dayButton);
        }
        return panel;
    }

    // 감량 무게 설정 메서드
    private void showWeightSettingPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        currentWeightField = new JTextField();
        targetWeightField = new JTextField();
        daysField = new JTextField();

        panel.add(new JLabel("현재 몸무게 (kg):"));
        panel.add(currentWeightField);
        panel.add(new JLabel("목표 몸무게 (kg):"));
        panel.add(targetWeightField);
        panel.add(new JLabel("목표 감량 일수:"));
        panel.add(daysField);

        JButton calculateButton = new JButton("계산하기");
        panel.add(calculateButton);

        calculateButton.addActionListener(e -> {
            try {
                double currentWeight = Double.parseDouble(currentWeightField.getText());
                double targetWeight = Double.parseDouble(targetWeightField.getText());
                int days = Integer.parseInt(daysField.getText());

                if (days <= 0 || currentWeight <= targetWeight) {
                    JOptionPane.showMessageDialog(frame, "올바른 값을 입력하세요.", "오류", JOptionPane.ERROR_MESSAGE);
                } else {
                    dailyCaloriesToBurn = (currentWeight - targetWeight) * 7700 / days;
                    JOptionPane.showMessageDialog(frame, "하루 감량해야 할 칼로리: " + String.format("%.2f kcal", dailyCaloriesToBurn));
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "숫자를 입력하세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
            }
        });

        JOptionPane.showMessageDialog(frame, panel, "감량 무게 설정", JOptionPane.PLAIN_MESSAGE);
    }

    // 타이머 & 소모 칼로리 계산 메서드
    private void showTimerPanel() {
        JDialog timerDialog = new JDialog(frame, "타이머 & 소모 칼로리", true);
        timerDialog.setSize(300, 200);
        timerDialog.setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        inputPanel.add(new JLabel("운동 시간 (초):"));
        JTextField timeField = new JTextField();
        inputPanel.add(timeField);
        inputPanel.add(new JLabel("소모 칼로리 (kcal):"));
        JTextField calorieField = new JTextField();
        inputPanel.add(calorieField);

        timerLabel = new JLabel("타이머: 00:00", JLabel.CENTER);
        JButton startButton = new JButton("시작");
        JButton stopButton = new JButton("중지");

        startButton.addActionListener(e -> {
            try {
                timeRemaining = Integer.parseInt(timeField.getText());
                double calories = Double.parseDouble(calorieField.getText());
                startTimer(timerLabel, calories);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(timerDialog, "숫자를 입력하세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
            }
        });

        stopButton.addActionListener(e -> stopTimer());

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 5, 5));
        buttonPanel.add(startButton);
        buttonPanel.add(stopButton);

        timerDialog.add(inputPanel, BorderLayout.NORTH);
        timerDialog.add(timerLabel, BorderLayout.CENTER);
        timerDialog.add(buttonPanel, BorderLayout.SOUTH);
        timerDialog.setVisible(true);
    }

    private void startTimer(JLabel timerLabel, double calories) {
        if (timerRunning) return;
        timerRunning = true;
        exerciseTimer = new Timer(1000, e -> {
            if (timeRemaining > 0) {
                timeRemaining--;
                timerLabel.setText("타이머: " + timeRemaining + "초 남음");
            } else {
                exerciseTimer.stop();
                timerRunning = false;
                JOptionPane.showMessageDialog(frame, "운동 완료! 소모 칼로리: " + calories + " kcal", "완료", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        exerciseTimer.start();
    }

    private void stopTimer() {
        if (exerciseTimer != null) {
            exerciseTimer.stop();
            timerRunning = false;
        }
    }

    private void showCaloriesForDay() {
        if (dailyCaloriesToBurn > 0) {
            JOptionPane.showMessageDialog(frame, "하루 감량해야 할 칼로리: " + String.format("%.2f kcal", dailyCaloriesToBurn),
                    "칼로리 정보", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(frame, "먼저 감량 무게를 설정하세요.", "경고", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void showExerciseLog() {
        JOptionPane.showMessageDialog(frame, logArea.getText(), "운동 기록", JOptionPane.INFORMATION_MESSAGE);
    }
}
