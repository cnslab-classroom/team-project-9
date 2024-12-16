import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Stack;

public class GUI {
    private static JFrame mainFrame;
    private static JPanel mainPanel;
    private static CardLayout cardLayout;
    private static Stack<String> screenHistory = new Stack<>(); // 화면 전환 기록

    private static FinanceManagerGUI financeManagerGUI;
    private static StudyManagerAppGUI studyManagerGUI;
    private static ExerciseManager exerciseManager = new ExerciseManager();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GUI().createAndShowGUI());
    }

    private void createAndShowGUI() {
        mainFrame = new JFrame("라이프 매니저");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(800, 600);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // 패널 생성 및 추가
        mainPanel.add(createMenuPanel(), "메인 메뉴");
        mainPanel.add(createFinancePanel(), "재정 관리");
        mainPanel.add(createStudyPanel(), "학습 관리");
        mainPanel.add(createExercisePanel(), "운동 관리");

        mainFrame.add(mainPanel);
        cardLayout.show(mainPanel, "메인 메뉴");
        mainFrame.setVisible(true);
    }

    // 메인 메뉴 패널
    private JPanel createMenuPanel() {
        JPanel menuPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("라이프 매니저에 오신 것을 환영합니다!", JLabel.CENTER);
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        menuPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        JButton financeButton = new JButton("재정 관리");
        JButton studyButton = new JButton("학습 관리");
        JButton exerciseButton = new JButton("운동 관리");

        financeButton.addActionListener(e -> switchScreen("재정 관리"));
        studyButton.addActionListener(e -> switchScreen("학습 관리"));
        exerciseButton.addActionListener(e -> switchScreen("운동 관리"));

        buttonPanel.add(financeButton);
        buttonPanel.add(studyButton);
        buttonPanel.add(exerciseButton);

        menuPanel.add(buttonPanel, BorderLayout.CENTER);
        return menuPanel;
    }

    // 재정 관리 패널
    private JPanel createFinancePanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // 뒤로가기 버튼
        JButton backButton = new JButton("메인 메뉴로 돌아가기");
        backButton.addActionListener(e -> switchScreen("메인 메뉴"));
        panel.add(backButton, BorderLayout.NORTH);

        // FinanceManager GUI
        financeManagerGUI = new FinanceManagerGUI(new FinanceManager());
        panel.add(financeManagerGUI.createFinancePanel(() -> switchScreen("메인 메뉴")), BorderLayout.CENTER);

        return panel;
    }

    // 학습 관리 패널
    private JPanel createStudyPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // 뒤로가기 버튼
        JButton backButton = new JButton("메인 메뉴로 돌아가기");
        backButton.addActionListener(e -> switchScreen("메인 메뉴"));
        panel.add(backButton, BorderLayout.NORTH);

        // StudyManagerApp GUI
        studyManagerGUI = new StudyManagerAppGUI();
        panel.add(studyManagerGUI.createStudyPanel(() -> switchScreen("메인 메뉴")), BorderLayout.CENTER);

        return panel;
    }

    // 운동 관리 패널
    private JPanel createExercisePanel() {
        JPanel exercisePanel = new JPanel(new BorderLayout());

        // 뒤로가기 버튼
        JButton backButton = new JButton("메인 메뉴로 돌아가기");
        backButton.addActionListener(e -> switchScreen("메인 메뉴"));
        exercisePanel.add(backButton, BorderLayout.NORTH);

        // ExerciseManager의 패널 추가
        exercisePanel.add(exerciseManager.createMainPanel(), BorderLayout.CENTER);
        return exercisePanel;
    }

    // 화면 전환 메서드
    private void switchScreen(String screenName) {
        if (screenHistory.isEmpty() || !screenHistory.peek().equals(screenName)) {
            screenHistory.push(screenName);
        }
        cardLayout.show(mainPanel, screenName);
    }
}
