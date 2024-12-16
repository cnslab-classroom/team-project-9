import javax.swing.*;
import java.awt.*;
import java.util.Stack;

public class ExerciseManagerGUI {
    private static JFrame mainFrame;
    private static JPanel mainPanel;
    private static CardLayout cardLayout;
    private static Stack<String> screenHistory = new Stack<>(); 
    private static ExerciseManager exerciseManagerInstance = ExerciseManager.getExerciseManager(); 
    public static ExerciseManager getExerciseManager() {return exerciseManagerInstance;}

    public static void main(String[] args) {
        // 기본 폰트 설정
        UIManager.put("Label.font", new Font("맑은 고딕", Font.PLAIN, 14));
        UIManager.put("Button.font", new Font("맑은 고딕", Font.PLAIN, 14));

        // JFrame 설정
        mainFrame = new JFrame("운동관리 프로그램");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(500, 600);

        // CardLayout과 메인 패널 설정
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // 화면 추가
        JPanel exercisePanel = createExercisePanel();
        mainPanel.add(exercisePanel, "운동 관리");

        // 메인 패널 추가 및 초기 화면 설정
        mainFrame.add(mainPanel);
        switchScreen("운동 관리");

        // JFrame 표시
        mainFrame.setVisible(true);
    }

    // 화면 전환 함수
    private static void switchScreen(String screenName) {
        if (screenHistory.isEmpty() || !screenHistory.peek().equals(screenName)) {
            screenHistory.push(screenName); 
        }
        cardLayout.show(mainPanel, screenName); 
    }

    // 운동 관리 패널 생성
    private static JPanel createExercisePanel() {
        JPanel exercisePanel = new JPanel(new BorderLayout());
    
        // 상단 버튼 영역
        JPanel topPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        JButton backButton = new JButton("돌아가기");
        backButton.addActionListener(e -> navigateBack());
    
        JButton resetButton = new JButton("감량 무게 설정");
        resetButton.addActionListener(e -> ExerciseManager.getExerciseManager().showWeightInputDialog(mainFrame));

    
        topPanel.add(backButton);
        topPanel.add(resetButton);
    
        // 중앙 영역: 달력 추가
        JPanel calendarPanel = ExerciseManager.getExerciseManager().createDateFormPanel();
        exercisePanel.add(calendarPanel, BorderLayout.CENTER);

        // 하단 버튼 영역
        JPanel lowerPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        JButton timerButton = new JButton("타이머 & 소모 칼로리 계산");
        timerButton.addActionListener(e -> ExerciseManager.getExerciseManager().showTimerAndCaloriesPanel(mainFrame));
    
        JButton routineButton = new JButton("알림 설정");
        routineButton.addActionListener(e -> ExerciseManager.showNotificationPanel(mainFrame));


        JButton recordButton = new JButton("운동 기록 보기");
        recordButton.addActionListener(e -> ExerciseManager.getExerciseManager().showExerciseRecordPopup(mainFrame));

    
        lowerPanel.add(timerButton);
        lowerPanel.add(routineButton);
        lowerPanel.add(recordButton);
    
        // 패널 구성
        exercisePanel.add(topPanel, BorderLayout.NORTH);
        exercisePanel.add(lowerPanel, BorderLayout.SOUTH);
    
        return exercisePanel;
    }
    
    
    private static void navigateBack() {
        if (!screenHistory.isEmpty()) {
            screenHistory.pop(); 
            if (!screenHistory.isEmpty()) {
                cardLayout.show(mainPanel, screenHistory.peek());
            }
        }
    }

}