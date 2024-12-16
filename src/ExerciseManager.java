import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.*; 
import java.util.List;
import java.util.Timer;


public class ExerciseManager { 

    // GUI 관련 필드
    private static JFrame mainFrame;
    private static JPanel mainPanel;
    private static CardLayout cardLayout;
    private static Stack<String> screenHistory = new Stack<>();
    
    // 운동 관리 관련 필드
    private List<Double> burnedCaloriesHistory = new ArrayList<>();
    private List<ExerciseData> exerciseDataList = new ArrayList<>();
    
    // 타이머 관련 필드
    private int timeRemaining;
    private int initialTimeRemaining;
    private double caloriesPerMinute;
    private Timer timer;
    private boolean isRunning;

    // 알림 관련 필드
    private String alarmTime = "";
    private javax.swing.Timer digitalClockTimer;

    // 날짜 관리 필드
    private final Calendar cal = Calendar.getInstance();
    private int year = cal.get(Calendar.YEAR);
    private int month = cal.get(Calendar.MONTH) + 1; 
    private final int today = cal.get(Calendar.DATE);
    private final int nowYear = year;
    private final int nowMonth = month;
    private final JButton[] dates = new JButton[42]; 
    private final JLabel jl_title = new JLabel("", JLabel.CENTER);

    // 운동 기록
    private double customCalories = 5.0;
    private double totalCaloriesToBurn;
    private double remainingCaloriesToBurn;

    // main 메서드
    public static void main(String[] args) {
        ExerciseManager app = new ExerciseManager();
        app.initializeGUI();
    }

    // GUI 초기화
    private void initializeGUI() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mainFrame = new JFrame("운동 관리 프로그램");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(500, 600);
        mainFrame.setResizable(false);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        JPanel exercisePanel = createExercisePanel();
        mainPanel.add(exercisePanel, "ExercisePanel");

        mainFrame.add(mainPanel);
        switchScreen("ExercisePanel");

        mainFrame.setVisible(true);
    }

    // 화면 전환 메서드
    private void switchScreen(String screenName) {
        if (screenHistory.isEmpty() || !screenHistory.peek().equals(screenName)) {
            screenHistory.push(screenName);
        }
        cardLayout.show(mainPanel, screenName);
    }

    // 운동 관리 화면
    private JPanel createExercisePanel() {
        JPanel exercisePanel = new JPanel(new BorderLayout());
        JPanel topPanel = new JPanel(new GridLayout(1, 2, 10, 10));

        JButton backButton = new JButton("돌아가기");
        backButton.addActionListener(e -> navigateBack());

        JButton resetButton = new JButton("감량 무게 설정");
        resetButton.addActionListener(e -> showWeightInputDialog());

        topPanel.add(backButton);
        topPanel.add(resetButton);

        JPanel calendarPanel = createDateFormPanel(); 
        JPanel lowerPanel = new JPanel(new GridLayout(3, 1, 10, 10));

        JButton timerButton = new JButton("타이머 & 소모 칼로리 계산");
        timerButton.addActionListener(e -> showTimerAndCaloriesPanel());

        JButton routineButton = new JButton("알림 설정");
        routineButton.addActionListener(e -> showNotificationPanel());

        JButton recordButton = new JButton("운동 기록");
        recordButton.addActionListener(e -> showExerciseRecordPopup());

        lowerPanel.add(timerButton);
        lowerPanel.add(routineButton);
        lowerPanel.add(recordButton);

        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.add(calendarPanel, BorderLayout.CENTER);
        centerPanel.add(lowerPanel, BorderLayout.SOUTH);

        exercisePanel.add(topPanel, BorderLayout.NORTH);
        exercisePanel.add(centerPanel, BorderLayout.CENTER);

        return exercisePanel;
    }


    // 달력 관리 메서드
    private JPanel createDateFormPanel() {
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
                if (exerciseDataList.isEmpty()) {
                    JOptionPane.showMessageDialog(null,
                            "감량 목표를 먼저 설정해주세요!",
                            "설정 필요",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // 마지막 설정된 운동 데이터
                ExerciseData lastData = exerciseDataList.get(exerciseDataList.size() - 1);
                int startDay = today;
                int endDay = startDay + lastData.targetDays - 1;
                int selectedDay;

                try {
                    selectedDay = Integer.parseInt(dates[idx].getText());
                } catch (NumberFormatException ex) {
                    return; 
                }

                if (selectedDay < startDay || selectedDay > endDay) {
                    JOptionPane.showMessageDialog(null,
                            "목표 감량일이 아닙니다!",
                            "날짜 범위 외",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // 하루 소모 칼로리 계산
                double dailyCalories = lastData.getDailyCaloriesToBurn();
                JOptionPane.showMessageDialog(null,
                        String.format("%d년 %02d월 %02d일\n오늘 소모해야 하는 칼로리는 %.2fkcal 입니다.",
                                year, month, selectedDay, dailyCalories),
                        "하루 소모 칼로리",
                        JOptionPane.INFORMATION_MESSAGE);
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

    // 날짜 새로고침
    private void refreshDate() {
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
    
    // 돌아가기 버튼 
    private void navigateBack() {
        if (!screenHistory.isEmpty()) {
            screenHistory.pop(); 
            if (!screenHistory.isEmpty()) {
                String previousScreen = screenHistory.peek(); 
                switchScreen(previousScreen); 
            } else {
                JOptionPane.showMessageDialog(mainFrame, "이전 화면이 없습니다.", "오류", JOptionPane.WARNING_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(mainFrame, "이전 화면이 없습니다.", "오류", JOptionPane.WARNING_MESSAGE);
        }
    }

    

    // 몸무게 입력 데이터 관리
    private void showWeightInputDialog() {
        boolean validInput = false;
    
        while (!validInput) {
            JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
    
            JLabel weightLabel = new JLabel("현재 몸무게 (kg):");
            JTextField weightField = new JTextField();
    
            JLabel targetWeightLabel = new JLabel("희망 몸무게 (kg):");
            JTextField targetWeightField = new JTextField();
    
            JLabel daysLabel = new JLabel("목표 감량 일수 (일):");
            JTextField daysField = new JTextField();
    
            panel.add(weightLabel);
            panel.add(weightField);
            panel.add(targetWeightLabel);
            panel.add(targetWeightField);
            panel.add(daysLabel);
            panel.add(daysField);
    
            int result = JOptionPane.showConfirmDialog(mainFrame, panel, "운동 목표 설정", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
    
            if (result == JOptionPane.CANCEL_OPTION) return;
    
            try {
                double currentWeight = Double.parseDouble(weightField.getText().trim());
                double targetWeight = Double.parseDouble(targetWeightField.getText().trim());
                int targetDays = Integer.parseInt(daysField.getText().trim());
    
                if (currentWeight <= 0 || targetWeight <= 0 || targetDays <= 0) {
                    JOptionPane.showMessageDialog(mainFrame, "모든 값은 0보다 커야 합니다!", "입력 오류", JOptionPane.ERROR_MESSAGE);
                } else if (currentWeight <= targetWeight) {
                    JOptionPane.showMessageDialog(mainFrame, "현재 몸무게는 희망 몸무게보다 커야 합니다!", "입력 오류", JOptionPane.ERROR_MESSAGE);
                } else {
                    addExerciseData(currentWeight, targetWeight, targetDays);
                    JOptionPane.showMessageDialog(mainFrame,
                            "입력 완료!\n현재 몸무게: " + currentWeight + "kg\n희망 몸무게: " + targetWeight + "kg\n목표 감량 일수: " + targetDays + "일",
                            "운동 목표", JOptionPane.INFORMATION_MESSAGE);
    
                    validInput = true;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(mainFrame, "올바른 숫자를 입력하세요!", "입력 오류", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // 운동 데이터 관리 메서드
    public void addExerciseData(double currentWeight, double targetWeight, int targetDays) {
        ExerciseData newData = new ExerciseData(currentWeight, targetWeight, targetDays);
        exerciseDataList.add(newData);
        totalCaloriesToBurn = newData.getDailyCaloriesToBurn() * targetDays;
        remainingCaloriesToBurn = totalCaloriesToBurn;
    }

    public List<Double> getBurnedCaloriesHistory() {
        return burnedCaloriesHistory;
    }

    public void resetCalories() {
        this.remainingCaloriesToBurn = this.totalCaloriesToBurn;
        this.burnedCaloriesHistory.clear();
    }

    // 데이터 클래스
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

        @Override
        public String toString() {
            return String.format(
                    "현재 몸무게: %.2fkg, 목표 몸무게: %.2fkg, 목표 기간: %d일, 하루 소모 칼로리: %.2fkcal",
                    currentWeight, targetWeight, targetDays, dailyCaloriesToBurn
            );
        }
    }


    // 타이머 및 소모 칼로리 계산 
    private void showTimerAndCaloriesPanel() {
        JFrame timerFrame = new JFrame("타이머 & 소모 칼로리 계산");
        timerFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        timerFrame.setSize(500, 600);
        timerFrame.setLayout(new BorderLayout(10, 10));

        // 상단 패널
        JPanel topPanel = new JPanel(new BorderLayout());
        JButton backButton = new JButton("돌아가기");
        backButton.addActionListener(e -> timerFrame.dispose());

        JButton setTimeButton = new JButton("시간 및 칼로리 설정");
        setTimeButton.addActionListener(e -> showTimeAndCaloriesInputDialog(timerFrame));

        topPanel.add(backButton, BorderLayout.WEST);
        topPanel.add(setTimeButton, BorderLayout.EAST);

        // 디지털 타이머
        JLabel timerLabel = new JLabel("00:00:00", JLabel.CENTER);
        timerLabel.setFont(new Font("Monospaced", Font.BOLD, 80));
        timerLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        // 기록 영역
        JTextArea recordArea = new JTextArea();
        recordArea.setEditable(false);
        recordArea.setBorder(BorderFactory.createTitledBorder("기록"));
        JScrollPane recordScrollPane = new JScrollPane(recordArea);

        // 버튼 패널
        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        JButton startButton = new JButton("시작");
        JButton stopButton = new JButton("중지");
        JButton resetButton = new JButton("리셋");

        buttonPanel.add(startButton);
        buttonPanel.add(stopButton);
        buttonPanel.add(resetButton);

        // 버튼 동작 정의
        startButton.addActionListener(e -> {
            if (!isRunning) {
                startTimer(timerLabel);
            } else {
                JOptionPane.showMessageDialog(timerFrame, "타이머가 이미 실행 중입니다.", "경고", JOptionPane.WARNING_MESSAGE);
            }
        });

        stopButton.addActionListener(e -> {
            if (isRunning) {
                stopTimer();

                int elapsedTimeInSeconds = getElapsedSeconds();
                int hours = elapsedTimeInSeconds / 3600;
                int minutes = (elapsedTimeInSeconds % 3600) / 60;
                int seconds = elapsedTimeInSeconds % 60;

                double burnedCalories = calculateCalories(elapsedTimeInSeconds);

                String record = String.format("경과 시간: %02d:%02d:%02d, 소모 칼로리: %.2f kcal\n",
                        hours, minutes, seconds, burnedCalories);
                recordArea.append(record);
            } else {
                JOptionPane.showMessageDialog(timerFrame, "타이머가 실행 중이 아닙니다.", "경고", JOptionPane.WARNING_MESSAGE);
            }
        });

        resetButton.addActionListener(e -> {
            resetTimer(timerLabel);
            recordArea.setText("");
        });

        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.add(timerLabel, BorderLayout.NORTH);
        centerPanel.add(recordScrollPane, BorderLayout.CENTER);

        timerFrame.add(topPanel, BorderLayout.NORTH);
        timerFrame.add(centerPanel, BorderLayout.CENTER);
        timerFrame.add(buttonPanel, BorderLayout.SOUTH);

        timerFrame.setVisible(true);
    }

    // 시간 및 칼로리 설정 
    private void showTimeAndCaloriesInputDialog(JFrame parentFrame) {
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));

        JLabel hourLabel = new JLabel("시간 (시):");
        JTextField hourField = new JTextField();

        JLabel minuteLabel = new JLabel("분 (분):");
        JTextField minuteField = new JTextField();

        JLabel secondLabel = new JLabel("초 (초):");
        JTextField secondField = new JTextField();

        JLabel caloriesLabel = new JLabel("분당 칼로리 (kcal):");
        JTextField caloriesField = new JTextField();

        panel.add(hourLabel);
        panel.add(hourField);
        panel.add(minuteLabel);
        panel.add(minuteField);
        panel.add(secondLabel);
        panel.add(secondField);
        panel.add(caloriesLabel);
        panel.add(caloriesField);

        boolean validInput = false;
        while (!validInput) {
            int result = JOptionPane.showConfirmDialog(parentFrame, panel, "시간 및 칼로리 설정", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.CANCEL_OPTION) return;

            try {
                int hours = Integer.parseInt(hourField.getText().trim());
                int minutes = Integer.parseInt(minuteField.getText().trim());
                int seconds = Integer.parseInt(secondField.getText().trim());
                double calories = Double.parseDouble(caloriesField.getText().trim());

                if (hours < 0 || hours > 24 || minutes < 0 || minutes > 59 || seconds < 0 || seconds > 59 || calories < 0) {
                    JOptionPane.showMessageDialog(parentFrame, "올바른 값을 입력하세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
                } else {
                    setTimer(hours, minutes, seconds, calories);
                    JOptionPane.showMessageDialog(parentFrame, "설정 완료!", "설정 완료", JOptionPane.INFORMATION_MESSAGE);
                    validInput = true;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(parentFrame, "올바른 숫자를 입력하세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // 타이머 관련 메서드 (set, get, calculate, start, stop, reset)
    private void setTimer(int hours, int minutes, int seconds, double caloriesPerMinute) {
        this.initialTimeRemaining = (hours * 3600) + (minutes * 60) + seconds;
        this.timeRemaining = initialTimeRemaining;
        this.caloriesPerMinute = caloriesPerMinute;
    }

    private int getElapsedSeconds() {
        return initialTimeRemaining - timeRemaining;
    }

    private double calculateCalories(int elapsedSeconds) {
        double elapsedMinutes = elapsedSeconds / 60.0;
        return elapsedMinutes * caloriesPerMinute;
    }

    private void startTimer(JLabel timerLabel) {
        if (timer != null) timer.cancel();
        timer = new Timer();
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

    private void stopTimer() {
        if (timer != null) timer.cancel();
        isRunning = false;
    }

    private void resetTimer(JLabel timerLabel) {
        stopTimer();
        timeRemaining = initialTimeRemaining;
        timerLabel.setText(formatTime(initialTimeRemaining));
    }

    private String formatTime(int totalSeconds) {
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    
    // 알림 설정 화면
    private void showNotificationPanel() {
        JFrame notificationFrame = new JFrame("알림 설정");
        notificationFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        notificationFrame.setSize(500, 600);
        notificationFrame.setLayout(new BorderLayout(10, 10));

        // 돌아가기 버튼과 디지털 시계
        JPanel topPanel = new JPanel(new BorderLayout());
        JButton backButton = new JButton("돌아가기");
        backButton.addActionListener(e -> notificationFrame.dispose());

        JLabel currentTimeLabel = new JLabel("00:00", JLabel.CENTER);
        currentTimeLabel.setFont(new Font("Monospaced", Font.BOLD, 50));
        currentTimeLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        updateDigitalClock(currentTimeLabel);

        topPanel.add(backButton, BorderLayout.WEST);
        topPanel.add(currentTimeLabel, BorderLayout.CENTER);

        // 알림 시간 입력 
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        JLabel alarmLabel = new JLabel("알림 시간 (HH:mm):");
        alarmLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 16));
        JTextField alarmTimeField = new JTextField(10);

        inputPanel.add(alarmLabel);
        inputPanel.add(alarmTimeField);

        // 설정된 알림 시간 표시 필드
        JTextArea alarmDisplayField = new JTextArea();
        alarmDisplayField.setFont(new Font("맑은 고딕", Font.PLAIN, 20));
        alarmDisplayField.setLineWrap(true);
        alarmDisplayField.setWrapStyleWord(true);
        alarmDisplayField.setEditable(false);
        alarmDisplayField.setBorder(BorderFactory.createTitledBorder("설정된 알림 시간"));

        if (!getAlarmTime().isEmpty()) {
            alarmDisplayField.setText("알림 시간: " + getAlarmTime());
        }

        // 알림 시간 입력 필드 동작 - 엔터 키로 저장
        alarmTimeField.addActionListener(e -> saveAlarmTime(alarmTimeField, alarmDisplayField));

        // 저장 버튼
        JButton saveButton = new JButton("저장");
        saveButton.addActionListener(e -> saveAlarmTime(alarmTimeField, alarmDisplayField));

        // 리셋 버튼
        JButton resetButton = new JButton("리셋");
        resetButton.addActionListener(e -> {
            alarmTimeField.setText("");
            alarmDisplayField.setText("");
            resetAlarmTime();
            JOptionPane.showMessageDialog(notificationFrame, "알림이 리셋되었습니다.", "리셋", JOptionPane.INFORMATION_MESSAGE);
        });

        // 취소 버튼
        JButton cancelButton = new JButton("취소");
        cancelButton.addActionListener(e -> notificationFrame.dispose());

        // 버튼 패널
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(saveButton);
        buttonPanel.add(resetButton);
        buttonPanel.add(cancelButton);

        // 메인 패널 구성
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.add(inputPanel, BorderLayout.NORTH);
        centerPanel.add(new JScrollPane(alarmDisplayField), BorderLayout.CENTER);

        notificationFrame.add(topPanel, BorderLayout.NORTH);
        notificationFrame.add(centerPanel, BorderLayout.CENTER);
        notificationFrame.add(buttonPanel, BorderLayout.SOUTH);

        notificationFrame.setVisible(true);
    }

    // 알람 관련 메서드 (set, get, reset, update, save, schedule)
    private void setAlarmTime(String time) {
        this.alarmTime = time;
    }

    private String getAlarmTime() {
        return alarmTime;
    }

    private void resetAlarmTime() {
        this.alarmTime = "";
    }

    private void updateDigitalClock(JLabel clockLabel) {
        if (digitalClockTimer != null) {
            digitalClockTimer.stop();
        }

        digitalClockTimer = new javax.swing.Timer(1000, e -> {
            String currentTime = java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"));
            clockLabel.setText(currentTime);
        });
        digitalClockTimer.start();
    }

    private void saveAlarmTime(JTextField alarmTimeField, JTextArea alarmDisplayField) {
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

    private void scheduleAlarm(JTextArea displayField) {
        if (alarmTime.isEmpty()) return;

        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000);
                    String currentTime = java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"));
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



    // 운동 기록 화면
    private void showExerciseRecordPopup() {
        JFrame recordFrame = new JFrame("운동 기록");
        recordFrame.setSize(500, 600);
        recordFrame.setLayout(new BorderLayout());
        recordFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // 돌아가기 버튼
        JPanel topPanel = new JPanel(new BorderLayout());
        JButton backButton = new JButton("돌아가기");
        backButton.addActionListener(e -> recordFrame.dispose());
        topPanel.add(backButton, BorderLayout.WEST);
        recordFrame.add(topPanel, BorderLayout.NORTH);

        // 운동 선택 및 시간 입력 영역
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 10));

        JLabel exerciseLabel = new JLabel("운동 종류:");
        String[] exercises = {"걷기", "달리기", "수영", "직접입력"};
        JComboBox<String> exerciseComboBox = new JComboBox<>(exercises);
        exerciseComboBox.setBackground(Color.WHITE);

        JLabel hourLabel = new JLabel("운동 시간 (시간):");
        JTextField hourInput = new JTextField(5);

        JLabel minuteLabel = new JLabel("운동 시간 (분):");
        JTextField minuteInput = new JTextField(5);

        inputPanel.add(exerciseLabel);
        inputPanel.add(exerciseComboBox);
        inputPanel.add(hourLabel);
        inputPanel.add(hourInput);
        inputPanel.add(minuteLabel);
        inputPanel.add(minuteInput);

        centerPanel.add(inputPanel, BorderLayout.NORTH);

        // 칼로리 계산 결과 영역
        JTextArea calorieResultArea = new JTextArea("총 소모 칼로리: ");
        calorieResultArea.setEditable(false);
        calorieResultArea.setLineWrap(true);
        calorieResultArea.setWrapStyleWord(true);
        calorieResultArea.setBorder(BorderFactory.createTitledBorder("결과"));
        calorieResultArea.setFont(new Font("맑은 고딕", Font.PLAIN, 16));
        calorieResultArea.setPreferredSize(new Dimension(500, 300));
        JScrollPane calorieScrollPane = new JScrollPane(calorieResultArea);
        centerPanel.add(calorieScrollPane, BorderLayout.CENTER);

        recordFrame.add(centerPanel, BorderLayout.CENTER);

        // "직접 입력" 선택 시 칼로리 입력창 띄우기
        exerciseComboBox.addActionListener(e -> {
            String selectedExercise = (String) exerciseComboBox.getSelectedItem();
            if ("직접입력".equals(selectedExercise)) {
                String calorieInput = JOptionPane.showInputDialog(recordFrame, "분당 소모 칼로리를 입력하세요:", "직접 입력", JOptionPane.PLAIN_MESSAGE);
                try {
                    double customCalories = Double.parseDouble(calorieInput);
                    setCustomCalories(customCalories);
                    JOptionPane.showMessageDialog(recordFrame, "분당 칼로리: " + customCalories + " kcal로 설정되었습니다.", "설정 완료", JOptionPane.INFORMATION_MESSAGE);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(recordFrame, "올바른 숫자를 입력해주세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // 칼로리 계산 및 저장 버튼
        JPanel bottomPanel = new JPanel(new FlowLayout());
        JButton calculateButton = new JButton("칼로리 계산");
        JButton saveButton = new JButton("저장");

        calculateButton.addActionListener(e -> {
            try {
                String selectedExercise = (String) exerciseComboBox.getSelectedItem();
                String hourText = hourInput.getText().trim();
                String minuteText = minuteInput.getText().trim();

                double hours = hourText.isEmpty() ? 0 : Double.parseDouble(hourText);
                double minutes = minuteText.isEmpty() ? 0 : Double.parseDouble(minuteText);

                double totalCalories = calculateCaloriesWithTime(selectedExercise, hours, minutes);

                calorieResultArea.setText(String.format("총 소모 칼로리: %.2f kcal\n(운동 시간: %.0f분)", totalCalories, (hours * 60) + minutes));
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(recordFrame, "시간과 분을 올바른 숫자로 입력해주세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(recordFrame, ex.getMessage(), "입력 오류", JOptionPane.ERROR_MESSAGE);
            }
        });

        saveButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(recordFrame, "운동 기록이 저장되었습니다.", "저장 완료", JOptionPane.INFORMATION_MESSAGE);
        });

        bottomPanel.add(calculateButton);
        bottomPanel.add(saveButton);
        recordFrame.add(bottomPanel, BorderLayout.SOUTH);

        recordFrame.setVisible(true);
    }

    // 칼로리 계산 메서드
    private double calculateCaloriesWithTime(String exerciseType, double hours, double minutes) {
        double totalTimeInMinutes = (hours * 60) + minutes;

        if (totalTimeInMinutes <= 0) {
            throw new IllegalArgumentException("운동 시간은 0보다 커야 합니다.");
        }
        return calculateCalories(exerciseType, totalTimeInMinutes);
    }

    private double calculateCalories(String exerciseType, double timeInMinutes) {
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
                caloriesPerMinute = customCalories;
                break;
            default:
                caloriesPerMinute = 5.0;
                break;
        }

        return timeInMinutes * caloriesPerMinute;
    }

    private void setCustomCalories(double customCalories) {
        if (customCalories > 0) {
            this.customCalories = customCalories;
        } else {
            throw new IllegalArgumentException("분당 칼로리는 0보다 커야 합니다.");
        }
    }

    private double getCustomCalories() {
        return customCalories;
    }



}