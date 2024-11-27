import javax.swing.*;
import java.awt.*;
import java.util.Stack;

public class GUI {
    private static JFrame mainFrame;
    private static JPanel mainPanel;
    private static CardLayout cardLayout;
    private static Stack<String> screenHistory = new Stack<>(); // 화면 기록 스택
    private static TimerManager timerManager; // TimerManager 인스턴스

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
            screenHistory.push(screenName); // 현재 화면 기록
        }
        cardLayout.show(mainPanel, screenName); // 화면 전환
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
        exitButton.addActionListener(e -> System.exit(0)); // 프로그램 종료
        JPanel exitPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        exitPanel.add(exitButton);
        menuPanel.add(exitPanel, BorderLayout.SOUTH);

        return menuPanel;
    }

    // 재정 관리 패널 생성
    private static JPanel createFinancePanel() {
        JPanel financePanel = new JPanel(new BorderLayout());
    
        // 상단 버튼 영역
        JPanel topPanel = new JPanel(new GridLayout(1, 1, 10, 10));
        JButton backButton = new JButton("돌아가기");
        backButton.addActionListener(e -> navigateBack());
        topPanel.add(backButton);
    
        // 중앙 텍스트 영역
        JTextArea financeTextArea = new JTextArea("재정 관리 인터페이스\n여기서 예산 및 지출을 관리하세요.");
        financeTextArea.setLineWrap(true);
        financeTextArea.setWrapStyleWord(true);
        financeTextArea.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        financePanel.add(financeTextArea, BorderLayout.CENTER);
    
        financePanel.add(topPanel, BorderLayout.NORTH);
    
        return financePanel;
    }

    // 학습 관리 패널 생성
    private static JPanel createStudyPanel() {
        JPanel studyPanel = new JPanel(new BorderLayout());
    
        // 상단 버튼 영역
        JPanel topPanel = new JPanel(new GridLayout(1, 1, 10, 10));
        JButton backButton = new JButton("돌아가기");
        backButton.addActionListener(e -> navigateBack());
        topPanel.add(backButton);
    
        // 중앙 텍스트 영역
        JTextArea studyTextArea = new JTextArea("학습 관리 인터페이스\n여기서 학습 일정을 관리하세요.");
        studyTextArea.setLineWrap(true);
        studyTextArea.setWrapStyleWord(true);
        studyTextArea.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        studyPanel.add(studyTextArea, BorderLayout.CENTER);
    
        studyPanel.add(topPanel, BorderLayout.NORTH);
    
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

        // 중간 상단 텍스트 영역
        JTextArea upperTextArea = new JTextArea("운동 관리 인터페이스\n운동 진행 상황 및 기록을 확인하세요.");
        upperTextArea.setLineWrap(true);
        upperTextArea.setWrapStyleWord(true);
        upperTextArea.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        upperTextArea.setPreferredSize(new Dimension(400, 100));

        // 하단 버튼 영역
        JPanel lowerPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        JButton timerButton = new JButton("타이머 & 소모 칼로리 계산");
        timerButton.addActionListener(e -> showTimerAndCaloriesPanel());

        JButton routineButton = new JButton("알림 설정");
        routineButton.addActionListener(e -> JOptionPane.showMessageDialog(mainFrame, "알림 설정 창을 엽니다.", "알림 설정", JOptionPane.INFORMATION_MESSAGE));

        JButton recordButton = new JButton("운동 기록");
        recordButton.addActionListener(e -> JOptionPane.showMessageDialog(mainFrame, "운동 기록 창을 엽니다.", "운동 기록", JOptionPane.INFORMATION_MESSAGE));

        lowerPanel.add(timerButton);
        lowerPanel.add(routineButton);
        lowerPanel.add(recordButton);

        // 메인 레이아웃 
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.add(upperTextArea, BorderLayout.CENTER);
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

        // 하단 버튼 패널
        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        JButton startButton = new JButton("시작");
        JButton stopButton = new JButton("중지");
        JButton resetButton = new JButton("리셋");

        buttonPanel.add(startButton);
        buttonPanel.add(stopButton);
        buttonPanel.add(resetButton);

        // 타이머 동작
        startButton.addActionListener(e -> {
            if (timerManager == null) {
                JOptionPane.showMessageDialog(timerFrame, "시간을 먼저 설정하세요.", "경고", JOptionPane.WARNING_MESSAGE);
            } else {
                timerManager.startTimer(timerLabel);
            }
        });

        stopButton.addActionListener(e -> {
            if (timerManager != null && timerManager.isRunning()) {
                timerManager.stopTimer();
        
                // 경과된 시간 계산
                int elapsedTimeInSeconds = timerManager.getElapsedSeconds();
                int hours = elapsedTimeInSeconds / 3600;
                int minutes = (elapsedTimeInSeconds % 3600) / 60;
                int seconds = elapsedTimeInSeconds % 60;
        
                double burnedCalories = timerManager.calculateCalories(elapsedTimeInSeconds);
        
                // 기록 추가
                String record = String.format(
                    "경과 시간: %02d시간 %02d분 %02d초, 소모 칼로리: %.2f kcal\n",
                    hours, minutes, seconds, burnedCalories
                );
                recordArea.append(record);
            }
        });
        
        resetButton.addActionListener(e -> {
            if (timerManager != null) {
                timerManager.resetTimer(timerLabel);
                recordArea.setText(""); // 기록 초기화
            }
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

                if (hours < 0 || hours > 24 ) {
                    JOptionPane.showMessageDialog(parentFrame, "범위 내 값을 입력해주세요.\n시간: 0~24", "입력 오류", JOptionPane.ERROR_MESSAGE);
                } else if (minutes < 0 || minutes > 59) {
                    JOptionPane.showMessageDialog(parentFrame, "범위 내 값을 입력해주세요.\n분: 0~59", "입력 오류", JOptionPane.ERROR_MESSAGE);

                } else if (seconds < 0 || seconds > 59) {
                    JOptionPane.showMessageDialog(parentFrame, "범위 내 값을 입력해주세요.\n초: 0~59", "입력 오류", JOptionPane.ERROR_MESSAGE);
                } else if (calories < 0) {
                    JOptionPane.showMessageDialog(parentFrame, "칼로리는 0 이상이어야 합니다.", "입력 오류", JOptionPane.ERROR_MESSAGE);
                } else {
                    timerManager = new TimerManager(hours, minutes, seconds, calories);
                    JOptionPane.showMessageDialog(parentFrame, "설정 완료: " + timerManager.formatTime() + ", 분당 칼로리: " + calories, "설정 완료", JOptionPane.INFORMATION_MESSAGE);
                    validInput = true;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(parentFrame, "올바른 숫자를 입력하세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
