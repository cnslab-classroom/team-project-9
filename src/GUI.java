import javax.swing.*;
import java.awt.*;
import java.util.Stack;


public class GUI {
    private static JFrame mainFrame;
    private static JPanel mainPanel;
    private static CardLayout cardLayout;
    private static Stack<String> screenHistory = new Stack<>(); //화면 기록
    private static ExerciseManager exerciseManagerInstance = ExerciseManager.getExerciseManager(); 
    public static ExerciseManager getExerciseManager() {return exerciseManagerInstance;}

    public static void main(String[] args) {
        // 기본 폰트 설정
        UIManager.put("Label.font", new Font("맑은 고딕", Font.PLAIN, 14));
        UIManager.put("Button.font", new Font("맑은 고딕", Font.PLAIN, 14));

        // 메인 프레임 생성
        mainFrame = new JFrame("라이프 매니저");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(500, 600);

        // CardLayout과 메인 패널 설정
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // 화면 추가
        JPanel menuPanel = createMenuPanel();
        JPanel exercisePanel = createExercisePanel();
        JPanel financePanel = createFinancePanel(); 
        JPanel studyPanel = createStudyPanel();    
  

        mainPanel.add(menuPanel, "메인 메뉴");
        mainPanel.add(exercisePanel, "운동 관리");
        mainPanel.add(financePanel, "재정 관리"); 
        mainPanel.add(studyPanel, "학습 관리");

        mainFrame.add(mainPanel);
        switchScreen("메인 메뉴"); 
        mainFrame.setVisible(true);
    }

    // 화면 전환 함수
    private static void switchScreen(String screenName) {
        if (screenHistory.isEmpty() || !screenHistory.peek().equals(screenName)) {
            screenHistory.push(screenName); 
        }
        cardLayout.show(mainPanel, screenName); 
    }

    // 메인 메뉴 패널 생성
    private static JPanel createMenuPanel() {
        JPanel menuPanel = new JPanel(new BorderLayout());

        JLabel titleLabel = new JLabel("라이프 매니저에 오신 것을 환영합니다!", JLabel.CENTER);
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        menuPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        JButton financeButton = new JButton("재정 관리");
        JButton studyButton = new JButton("학습 관리");
        JButton exerciseButton = new JButton("운동 관리");

        // 메인 메뉴 버튼 동작
        financeButton.addActionListener(e -> switchScreen("재정 관리"));
        studyButton.addActionListener(e -> switchScreen("학습 관리"));
        exerciseButton.addActionListener(e -> switchScreen("운동 관리"));

        buttonPanel.add(financeButton);
        buttonPanel.add(studyButton);
        buttonPanel.add(exerciseButton);

        menuPanel.add(buttonPanel, BorderLayout.CENTER);

        JButton exitButton = new JButton("나가기");
        exitButton.addActionListener(e -> System.exit(0)); 
        JPanel exitPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        exitPanel.add(exitButton);
        menuPanel.add(exitPanel, BorderLayout.SOUTH);

        return menuPanel;
    }

    // 재정 관리 패널 생성
    private static JPanel createFinancePanel() {
        // FinanceManager 객체 생성
        FinanceManager financeManager = new FinanceManager();
        FinanceManagerGUI financeManagerGUI = new FinanceManagerGUI(financeManager);
         // FinanceManager의 GUI 패널 반환 메서드 호출

        // 전체 패널 생성
        JPanel financePanel = new JPanel(new BorderLayout());

        // 뒤로가기 버튼 생성
        JButton backButton = new JButton("뒤로가기");
        backButton.addActionListener(e -> switchScreen("메인 메뉴")); // 메인 화면으로 돌아가기

        // 뒤로가기 버튼을 상단에 배치
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(backButton);

        // FinanceManager에서 가져온 패널 (재정 관리 내용)
        JPanel contentPanel = financeManager.createFinancePanel();

        // 최종 패널에 뒤로가기 버튼과 재정 관리 패널 추가
        financePanel.add(topPanel, BorderLayout.NORTH); // 상단에 뒤로가기 버튼 추가
        financePanel.add(contentPanel, BorderLayout.CENTER); // 중앙에 재정 관리 패널 추가

        return financeManagerGUI.createFinancePanel(() -> switchScreen("메인 메뉴"));
    }

    // 학습 관리 패널 생성
    private static JPanel createStudyPanel() {
        JPanel studyPanel = new JPanel(new BorderLayout());

    // 상단 뒤로가기 버튼
    JButton backButton = new JButton("뒤로가기");
    backButton.addActionListener(e -> switchScreen("메인 메뉴"));
    JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    topPanel.add(backButton);

    // 텍스트 영역 (결과 출력용)
    JTextArea textArea = new JTextArea(20, 40);
    textArea.setEditable(false);
    JScrollPane scrollPane = new JScrollPane(textArea);

    // 버튼 패널 (기능 버튼 추가)
    JPanel buttonPanel = new JPanel(new GridLayout(4, 1, 10, 10));
    JButton scheduleButton = new JButton("강의 목록 관리");
    JButton summaryButton = new JButton("강의 요약 관리");
    JButton reviewButton = new JButton("복습 알림 관리");
    JButton exitButton = new JButton("나가기");

    // 버튼 이벤트 설정 - 별도의 작업 스레드로 실행
    scheduleButton.addActionListener(e -> {
        new Thread(() -> {
            StudyManagerApp.manageSchedule();
            SwingUtilities.invokeLater(() -> textArea.setText("강의 목록 관리가 완료되었습니다."));
        }).start();
    });

    summaryButton.addActionListener(e -> {
        new Thread(() -> {
            StudyManagerApp.manageCourseSummary();
            SwingUtilities.invokeLater(() -> textArea.setText("강의 요약 관리가 완료되었습니다."));
        }).start();
    });

    reviewButton.addActionListener(e -> {
        new Thread(() -> {
            StudyManagerApp.reviewNotification();
            SwingUtilities.invokeLater(() -> textArea.setText("복습 알림 관리가 완료되었습니다."));
        }).start();
    });

    exitButton.addActionListener(e -> System.exit(0));

    buttonPanel.add(scheduleButton);
    buttonPanel.add(summaryButton);
    buttonPanel.add(reviewButton);
    buttonPanel.add(exitButton);

    // 레이아웃 구성
    studyPanel.add(topPanel, BorderLayout.NORTH); // 뒤로가기 버튼
    studyPanel.add(scrollPane, BorderLayout.CENTER); // 텍스트 출력 영역
    studyPanel.add(buttonPanel, BorderLayout.EAST); // 버튼 패널

    return studyPanel;
    }

    // 운동 관리 패널 생성
    private static JPanel createExercisePanel() {
        JPanel exercisePanel = new JPanel(new BorderLayout());

        // 상단 버튼 영역
        JPanel topPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        JButton backButton = new JButton("돌아가기");
        backButton.addActionListener(e -> navigateBack());

        JButton resetButton = new JButton("감량 무게 설정");
        resetButton.addActionListener(e -> showWeightInputDialog());

        topPanel.add(backButton);
        topPanel.add(resetButton);

        // 중앙 영역: 달력 추가
        JPanel calendarPanel = ExerciseManager.getExerciseManager().createDateFormPanel();

        // 하단 버튼 영역
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

        // 메인 레이아웃 구성 (버튼)
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.add(calendarPanel, BorderLayout.CENTER); 
        centerPanel.add(lowerPanel, BorderLayout.SOUTH); 

        exercisePanel.add(topPanel, BorderLayout.NORTH); 
        exercisePanel.add(centerPanel, BorderLayout.CENTER); 

        return exercisePanel;
    }


    // 돌아가기 버튼 
    private static void navigateBack() {
        if (!screenHistory.isEmpty()) {
            screenHistory.pop(); // 현재 화면 제거
            if (!screenHistory.isEmpty()) {
                String previousScreen = screenHistory.pop(); // 이전 화면 가져오기
                switchScreen(previousScreen);
            }
        } else {
            JOptionPane.showMessageDialog(mainFrame, "이전 화면이 없습니다.", "오류", JOptionPane.WARNING_MESSAGE);
        }
    }

    // 몸무게 입력
    private static void showWeightInputDialog() {
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
                    // ExerciseManager에 데이터 저장
                    ExerciseManager.getExerciseManager().addExerciseData(currentWeight, targetWeight, targetDays);
    
                    JOptionPane.showMessageDialog(mainFrame,
                            "입력 완료!\n현재 몸무게: " + currentWeight + "kg\n희망 몸무게: " + targetWeight + "kg\n목표 감량 일수: " + targetDays + "일",
                            "운동 목표", JOptionPane.INFORMATION_MESSAGE);
    
                            System.out.println("저장된 데이터: " + ExerciseManager.getExerciseManager().getLastExerciseData());
                    validInput = true;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(mainFrame, "올바른 숫자를 입력하세요!", "입력 오류", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // 타이머 및 소모 칼로리 계산 창 표시
    private static void showTimerAndCaloriesPanel() {
        JFrame timerFrame = new JFrame("타이머 & 소모 칼로리 계산");
        timerFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        timerFrame.setSize(500, 600);
        timerFrame.setResizable(false);
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
            ExerciseManager manager = GUI.getExerciseManager();
            if (!manager.isRunning()) {
                manager.startTimer(timerLabel);
            } else {
                JOptionPane.showMessageDialog(timerFrame, "타이머가 이미 실행 중입니다.", "경고", JOptionPane.WARNING_MESSAGE);
            }
        });

        stopButton.addActionListener(e -> {
            ExerciseManager manager = GUI.getExerciseManager();
            if (manager.isRunning()) {
                manager.stopTimer();
        
                // 경과된 시간 계산
                int elapsedTimeInSeconds = manager.getElapsedSeconds();
                int hours = elapsedTimeInSeconds / 3600;
                int minutes = (elapsedTimeInSeconds % 3600) / 60;
                int seconds = elapsedTimeInSeconds % 60;
        
                double burnedCalories = manager.calculateCalories(elapsedTimeInSeconds);
        
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
            ExerciseManager manager = GUI.getExerciseManager();
            manager.resetTimer(timerLabel);
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
    

    
    // 알림 설정 화면 생성
    
    private static void showNotificationPanel() {
    
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
    
        ExerciseManager manager = GUI.getExerciseManager();
        if (!manager.getAlarmTime().isEmpty()) {
            alarmDisplayField.setText("알림 시간: " + manager.getAlarmTime());
        }

    
        // 알림 시간 입력 필드 동작 - 엔터 키로 저장
        alarmTimeField.addActionListener(e -> manager.saveAlarmTime(alarmTimeField, alarmDisplayField));
    
        // 저장 버튼
        JButton saveButton = new JButton("저장");
        saveButton.addActionListener(e -> {
            ExerciseManager exerciseManager = GUI.getExerciseManager(); 
            exerciseManager.saveAlarmTime(alarmTimeField, alarmDisplayField);
        });

        // 리셋 버튼
        JButton resetButton = new JButton("리셋");
        resetButton.addActionListener(e -> {
            ExerciseManager exerciseManager = GUI.getExerciseManager(); 
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

    // 운동 기록 팝업 창 열기
    private static void showExerciseRecordPopup() {
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
                    ExerciseManager.getExerciseManager().setCustomCalories(customCalories);
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
                ExerciseManager manager = ExerciseManager.getExerciseManager();
                double totalCalories = manager.calculateCaloriesWithTime(selectedExercise, hours, minutes);

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
    
}
