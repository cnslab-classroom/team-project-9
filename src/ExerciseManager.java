import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimerTask;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ExerciseManager {
    private static ExerciseManager instance; // 싱글톤 인스턴스

    public static ExerciseManager getExerciseManager() {
        if (instance == null) {
            instance = new ExerciseManager();
        }
        return instance;
    }

     
    // 총 소모 칼로리와 남은 칼로리 관리
    private double totalCaloriesToBurn; 
    private double remainingCaloriesToBurn; 
    private List<Double> burnedCaloriesHistory; 

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

    // ExerciseManager 생성자 (DateForm 초기화)
    private ExerciseManager() {
        this.exerciseDataList = new ArrayList<>();
        this.burnedCaloriesHistory = new ArrayList<>();
        this.totalCaloriesToBurn = 0;
        this.remainingCaloriesToBurn = 0;
        this.isRunning = false;
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH) + 1;
        nowMonth = month;
        nowYear = year;
        today = cal.get(Calendar.DATE);
    }

    // 총 소모 칼로리 설정
    public void setTotalCaloriesToBurn(double totalCalories) {
        this.totalCaloriesToBurn = totalCalories;
        this.remainingCaloriesToBurn = totalCalories; 
    }

    // 소모된 칼로리 추가 및 남은 칼로리 갱신
    public void addBurnedCalories(double burnedCalories) {
        this.burnedCaloriesHistory.add(burnedCalories);
        this.remainingCaloriesToBurn -= burnedCalories;

        if (this.remainingCaloriesToBurn < 0) {
            this.remainingCaloriesToBurn = 0; 
        }
    }

    // 총 소모 칼로리 가져오기
    public double getTotalCaloriesToBurn() {
        return totalCaloriesToBurn;
    }

    // 남은 소모 칼로리 가져오기
    public double getRemainingCaloriesToBurn() {
        return remainingCaloriesToBurn;
    }

    // 하루 감량 목표 계산
    public double calculateDailyTarget(int remainingDays) {
        if (remainingDays <= 0) return 0;
        return remainingCaloriesToBurn / remainingDays;
    }

    // 소모 칼로리 기록 가져오기
    public List<Double> getBurnedCaloriesHistory() {
        return burnedCaloriesHistory;
    }

    // 소모 칼로리 기록 초기화
    public void resetCalories() {
        this.remainingCaloriesToBurn = this.totalCaloriesToBurn;
        this.burnedCaloriesHistory.clear();
    }

    
     // 몸무게 입력
     public void showWeightInputDialog(JFrame parentFrame) {
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
    
            int result = JOptionPane.showConfirmDialog(parentFrame, panel, "운동 목표 설정", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
    
            if (result == JOptionPane.CANCEL_OPTION) return;
    
            try {
                double currentWeight = Double.parseDouble(weightField.getText().trim());
                double targetWeight = Double.parseDouble(targetWeightField.getText().trim());
                int targetDays = Integer.parseInt(daysField.getText().trim());
    
                if (currentWeight <= 0 || targetWeight <= 0 || targetDays <= 0) {
                    JOptionPane.showMessageDialog(parentFrame, "모든 값은 0보다 커야 합니다!", "입력 오류", JOptionPane.ERROR_MESSAGE);
                } else if (currentWeight <= targetWeight) {
                    JOptionPane.showMessageDialog(parentFrame, "현재 몸무게는 희망 몸무게보다 커야 합니다!", "입력 오류", JOptionPane.ERROR_MESSAGE);
                } else {
                    // ExerciseManager에 데이터 저장
                    addExerciseData(currentWeight, targetWeight, targetDays);
    
                    JOptionPane.showMessageDialog(parentFrame,
                            "입력 완료!\n현재 몸무게: " + currentWeight + "kg\n희망 몸무게: " + targetWeight + "kg\n목표 감량 일수: " + targetDays + "일",
                            "운동 목표", JOptionPane.INFORMATION_MESSAGE);
    
                    System.out.println("저장된 데이터: " + getLastExerciseData());
                    validInput = true;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(parentFrame, "올바른 숫자를 입력하세요!", "입력 오류", JOptionPane.ERROR_MESSAGE);
            }
        }
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



    // 타이머 & 칼로리 계산 panel 메서드
   public void showTimerAndCaloriesPanel(JFrame parentFrame) {
    JFrame timerFrame = new JFrame("타이머 & 소모 칼로리 계산");
    timerFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    timerFrame.setSize(500, 600);
    timerFrame.setLayout(new BorderLayout(10, 10));

    // 상단 패널
    JPanel topPanel = new JPanel(new BorderLayout());
    JButton backButton = new JButton("돌아가기");
    backButton.addActionListener(e -> timerFrame.dispose());

    JButton setTimeButton = new JButton("시간 및 칼로리 설정");
    setTimeButton.addActionListener(e -> showTimeAndCaloriesInputDialog(timerFrame)); // 여기에서 호출!

    topPanel.add(backButton, BorderLayout.WEST);
    topPanel.add(setTimeButton, BorderLayout.EAST);

    // 타이머 UI
    JLabel timerLabel = new JLabel("00:00:00", JLabel.CENTER);
    timerLabel.setFont(new Font("Monospaced", Font.BOLD, 80));
    timerLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

    // 기록 영역
    JTextArea recordArea = new JTextArea();
    recordArea.setEditable(false);
    recordArea.setLineWrap(true);
    recordArea.setWrapStyleWord(true);
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

    // 타이머 동작
    startButton.addActionListener(e -> {
        if (!isRunning()) {
            startTimer(timerLabel);
        } else {
            JOptionPane.showMessageDialog(timerFrame, "타이머가 이미 실행 중입니다.", "경고", JOptionPane.WARNING_MESSAGE);
        }
    });

    stopButton.addActionListener(e -> {
        if (isRunning()) {
            stopTimer();

            // 경과된 시간 계산
            int elapsedTimeInSeconds = getElapsedSeconds();
            int hours = elapsedTimeInSeconds / 3600;
            int minutes = (elapsedTimeInSeconds % 3600) / 60;
            int seconds = elapsedTimeInSeconds % 60;

            double burnedCalories = calculateCalories(elapsedTimeInSeconds);

            // 기록 추가
            String record = String.format(
                "경과 시간: %02d시간 %02d분 %02d초, 소모 칼로리: %.2f kcal\n",
                hours, minutes, seconds, burnedCalories
            );
            recordArea.append(record);
        } else {
            JOptionPane.showMessageDialog(timerFrame, "타이머가 실행 중이 아닙니다.", "경고", JOptionPane.WARNING_MESSAGE);
        }
    });

    resetButton.addActionListener(e -> {
        resetTimer(timerLabel);
        recordArea.setText("");
    });

    // 구성 요소 배치
    JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
    centerPanel.add(timerLabel, BorderLayout.NORTH);
    centerPanel.add(recordScrollPane, BorderLayout.CENTER);

    timerFrame.add(topPanel, BorderLayout.NORTH);
    timerFrame.add(centerPanel, BorderLayout.CENTER);
    timerFrame.add(buttonPanel, BorderLayout.SOUTH);

    timerFrame.setVisible(true);
   }

    // 시간 및 칼로리 설정
    private static void showTimeAndCaloriesInputDialog(JFrame parentFrame) {
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
            int result = JOptionPane.showConfirmDialog(parentFrame, panel, "시간 및 칼로리 설정", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
    
            if (result == JOptionPane.CANCEL_OPTION) return;
    
            try {
                int hours = Integer.parseInt(hourField.getText().trim());
                int minutes = Integer.parseInt(minuteField.getText().trim());
                int seconds = Integer.parseInt(secondField.getText().trim());
                double calories = Double.parseDouble(caloriesField.getText().trim());
    
                if (hours < 0 || hours > 24 || minutes < 0 || minutes > 59 || seconds < 0 || seconds > 59 || calories < 0) {
                    JOptionPane.showMessageDialog(parentFrame, "올바른 값을 입력하세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
                } else {
                    ExerciseManager.getExerciseManager().setTimer(hours, minutes, seconds, calories);
                    JOptionPane.showMessageDialog(parentFrame, "설정 완료!", "설정 완료", JOptionPane.INFORMATION_MESSAGE);
                    validInput = true;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(parentFrame, "올바른 숫자를 입력하세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
            }
        }
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


    // 알림 panel 메서드
    public static void showNotificationPanel(JFrame parentFrame) {
    
        // 알림 설정 창
        JFrame notificationFrame = new JFrame("알림 설정");
        notificationFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        notificationFrame.setSize(500, 600);
        notificationFrame.setLayout(new BorderLayout(10, 10));
    
        // 상단 패널: 돌아가기 버튼과 디지털 시계
        JPanel topPanel = new JPanel(new BorderLayout());
    
        // 돌아가기 버튼
        JButton backButton = new JButton("돌아가기");
        backButton.addActionListener(e -> notificationFrame.dispose());
        topPanel.add(backButton, BorderLayout.WEST); 
    
        // 디지털 시계 표시
        JLabel currentTimeLabel = new JLabel("00:00", JLabel.CENTER);
        currentTimeLabel.setFont(new Font("Monospaced", Font.BOLD, 50));
        currentTimeLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        ExerciseManager.getExerciseManager().updateDigitalClock(currentTimeLabel);
        topPanel.add(currentTimeLabel, BorderLayout.CENTER);

    
        // 알림 시간 입력 패널
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
    
        ExerciseManager manager = ExerciseManager.getExerciseManager();
        if (!manager.getAlarmTime().isEmpty()) {
            alarmDisplayField.setText("알림 시간: " + manager.getAlarmTime());
        }

    
        // 알림 시간 입력 필드 동작 - 엔터 키로 저장
        alarmTimeField.addActionListener(e -> manager.saveAlarmTime(alarmTimeField, alarmDisplayField));
    
        // 저장 버튼
        JButton saveButton = new JButton("저장");
        saveButton.addActionListener(e -> {
            ExerciseManager exerciseManager = ExerciseManager.getExerciseManager(); 
            exerciseManager.saveAlarmTime(alarmTimeField, alarmDisplayField);
        });

        // 리셋 버튼
        JButton resetButton = new JButton("리셋");
        resetButton.addActionListener(e -> {
            ExerciseManager exerciseManager = ExerciseManager.getExerciseManager(); 
            alarmTimeField.setText(""); 
            alarmDisplayField.setText(""); 
            exerciseManager.resetAlarmTime();
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

        // 프레임 구성
        notificationFrame.add(topPanel, BorderLayout.NORTH); 
        notificationFrame.add(centerPanel, BorderLayout.CENTER); 
        notificationFrame.add(buttonPanel, BorderLayout.SOUTH); 
    
        notificationFrame.setVisible(true);
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

    // DateForm panel 메서드
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

    // 운동 기록 panel
    public void showExerciseRecordPopup(JFrame parentFrame) {
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
    
        // 운동 선택, 시간 입력, 결과 출력
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
    
        // 운동 선택 및 시간 입력 영역
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
    
        // 칼로리 계산 버튼 동작
        calculateButton.addActionListener(e -> {
            try {
                // 사용자 입력 가져오기
                String selectedExercise = (String) exerciseComboBox.getSelectedItem();
                String hourText = hourInput.getText().trim();
                String minuteText = minuteInput.getText().trim();
    
                // 입력값 검증
                double hours = hourText.isEmpty() ? 0 : Double.parseDouble(hourText);
                double minutes = minuteText.isEmpty() ? 0 : Double.parseDouble(minuteText);
    
                // ExerciseManager를 통해 시간과 분 단위로 칼로리 계산
                double totalCalories = calculateCaloriesWithTime(selectedExercise, hours, minutes);
    
                // 결과 출력
                calorieResultArea.setText(String.format("총 소모 칼로리: %.2f kcal\n(운동 시간: %.0f분)", totalCalories, (hours * 60) + minutes));
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(recordFrame, "시간과 분을 올바른 숫자로 입력해주세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(recordFrame, ex.getMessage(), "입력 오류", JOptionPane.ERROR_MESSAGE);
            }
        });
    
        // 저장 버튼 동작
        saveButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(recordFrame, "운동 기록이 저장되었습니다.", "저장 완료", JOptionPane.INFORMATION_MESSAGE);
        });
    
        bottomPanel.add(calculateButton);
        bottomPanel.add(saveButton);
        recordFrame.add(bottomPanel, BorderLayout.SOUTH);
    
        // 팝업 창 표시
        recordFrame.setVisible(true);
    }
    

    //운동 관리 관련 메서드
    private double customCalories = 5.0; 

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
                caloriesPerMinute = customCalories; 
                break;
            default:
                caloriesPerMinute = 5.0; 
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
