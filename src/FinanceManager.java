import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class FinanceManager {
    // 재정 기록 클래스
    static class Record {
        String category; // 카테고리
        double amount;   // 금액
        String description; // 설명
        double balance;  // 해당 시점의 잔액

        public Record(String category, double amount, String description, double balance) {
            this.category = category;
            this.amount = amount;
            this.description = description;
            this.balance = balance;
        }

        @Override
        public String toString() {
            return "카테고리: " + category + ", 금액: " + amount + ", 설명: " + description + ", 잔액: " + balance;
        }
    }

    private List<Record> records; // 기록 리스트
    private double currentBalance; // 현재 잔액

    // 생성자
    public FinanceManager() {
        this.records = new ArrayList<>();
        this.currentBalance = 0;
    }

    // 수입 추가
    public void addIncome(String category, double amount, String description) {
        currentBalance += amount;
        records.add(new Record(category, amount, description, currentBalance));
    }

    // 지출 추가
    public void addExpense(String category, double amount, String description) {
        currentBalance -= amount;
        records.add(new Record(category, -amount, description, currentBalance));
    }

    // 기록 반환
    public List<Record> getRecords() {
        return records;
    }

    // 현재 잔액 반환
    public double getCurrentBalance() {
        return currentBalance;
    }

// GUI 통합용 JPanel 생성
public JPanel createFinancePanel() {
    JPanel panel = new JPanel(new BorderLayout());

    // 상단 타이틀
    JLabel titleLabel = new JLabel("재정 관리 화면", JLabel.CENTER);
    titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 20));
    panel.add(titleLabel, BorderLayout.NORTH);

    // 기록 표시 영역
    JTextArea recordsArea = new JTextArea(getRecordsText());
    recordsArea.setEditable(false);
    recordsArea.setLineWrap(true);
    recordsArea.setWrapStyleWord(true);
    JScrollPane scrollPane = new JScrollPane(recordsArea);
    scrollPane.setBorder(BorderFactory.createTitledBorder("재정 기록"));

    // 버튼 패널
    JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 10, 10));
    JButton addIncomeButton = new JButton("수입 추가");
    JButton addExpenseButton = new JButton("지출 추가");
    JButton viewGraphButton = new JButton("잔액 그래프 보기");

    // 버튼 동작
    addIncomeButton.addActionListener(e -> {
        String category = JOptionPane.showInputDialog("수입 카테고리:");
        String amountStr = JOptionPane.showInputDialog("금액:");
        String description = JOptionPane.showInputDialog("설명:");
        try {
            double amount = Double.parseDouble(amountStr);
            addIncome(category, amount, description);
            recordsArea.setText(getRecordsText());
            JOptionPane.showMessageDialog(null, "수입이 추가되었습니다.");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "잘못된 입력입니다!", "입력 오류", JOptionPane.ERROR_MESSAGE);
        }
    });

    addExpenseButton.addActionListener(e -> {
        String category = JOptionPane.showInputDialog("지출 카테고리:");
        String amountStr = JOptionPane.showInputDialog("금액:");
        String description = JOptionPane.showInputDialog("설명:");
        try {
            double amount = Double.parseDouble(amountStr);
            addExpense(category, amount, description);
            recordsArea.setText(getRecordsText());
            JOptionPane.showMessageDialog(null, "지출이 추가되었습니다.");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "잘못된 입력입니다!", "입력 오류", JOptionPane.ERROR_MESSAGE);
        }
    });

    viewGraphButton.addActionListener(e -> showGraph());

    buttonPanel.add(addIncomeButton);
    buttonPanel.add(addExpenseButton);
    buttonPanel.add(viewGraphButton);

    panel.add(scrollPane, BorderLayout.CENTER);
    panel.add(buttonPanel, BorderLayout.SOUTH);

    return panel;
}


    // GUI 실행
    public void launchGUI() {
        JFrame frame = new JFrame("재정 관리 시스템");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 500);

        JPanel panel = new JPanel(new BorderLayout());

        // 상단 버튼 패널
        JPanel topPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        JButton addIncomeButton = new JButton("수입 추가");
        JButton addExpenseButton = new JButton("지출 추가");
        JButton viewGraphButton = new JButton("잔액 그래프 보기");
        topPanel.add(addIncomeButton);
        topPanel.add(addExpenseButton);
        topPanel.add(viewGraphButton);

        // 중앙 기록 표시
        JTextArea recordsArea = new JTextArea();
        recordsArea.setEditable(false);
        recordsArea.setLineWrap(true);
        recordsArea.setWrapStyleWord(true);

        // 스크롤 가능한 텍스트 영역
        JScrollPane scrollPane = new JScrollPane(recordsArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("재정 기록"));

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        frame.add(panel);

        // 버튼 동작
        addIncomeButton.addActionListener(e -> {
            String category = JOptionPane.showInputDialog(frame, "수입 카테고리:");
            String amountStr = JOptionPane.showInputDialog(frame, "금액:");
            String description = JOptionPane.showInputDialog(frame, "설명:");

            try {
                double amount = Double.parseDouble(amountStr);
                addIncome(category, amount, description);
                recordsArea.setText(getRecordsText());
                JOptionPane.showMessageDialog(frame, "수입이 추가되었습니다.");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "잘못된 금액 입력입니다!", "입력 오류", JOptionPane.ERROR_MESSAGE);
            }
        });

        addExpenseButton.addActionListener(e -> {
            String category = JOptionPane.showInputDialog(frame, "지출 카테고리:");
            String amountStr = JOptionPane.showInputDialog(frame, "금액:");
            String description = JOptionPane.showInputDialog(frame, "설명:");

            try {
                double amount = Double.parseDouble(amountStr);
                addExpense(category, amount, description);
                recordsArea.setText(getRecordsText());
                JOptionPane.showMessageDialog(frame, "지출이 추가되었습니다.");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "잘못된 금액 입력입니다!", "입력 오류", JOptionPane.ERROR_MESSAGE);
            }
        });

        viewGraphButton.addActionListener(e -> showGraph());

        frame.setVisible(true);
    }

    // 기록 텍스트 반환
    private String getRecordsText() {
        StringBuilder recordsText = new StringBuilder("전체 기록:\n");
        for (int i = 0; i < records.size(); i++) {
            recordsText.append("[").append(i).append("] ").append(records.get(i)).append("\n");
        }
        return recordsText.toString();
    }

    // 그래프 표시
public void showGraph() {
    JFrame graphFrame = new JFrame("잔액 변화 그래프");
    graphFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    graphFrame.setSize(800, 600);

    JPanel graphPanel = new JPanel() {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            // 배경 색상
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, getWidth(), getHeight());

            // 선 그래프 그리기
            g.setColor(Color.BLUE);
            int width = getWidth();
            int height = getHeight();
            int padding = 50;

            // x축, y축 그리기
            g.setColor(Color.BLACK);
            g.drawLine(padding, height - padding, width - padding, height - padding); // x축
            g.drawLine(padding, padding, padding, height - padding); // y축

            // 그래프 데이터 계산
            int numPoints = records.size();
            if (numPoints == 0) return;

            int graphWidth = width - 2 * padding;
            int graphHeight = height - 2 * padding;
            int pointSpacing = numPoints > 1 ? graphWidth / (numPoints - 1) : graphWidth;

            // 최대/최소 값 계산
            double maxBalance = records.stream().mapToDouble(record -> record.balance).max().orElse(1);
            double minBalance = records.stream().mapToDouble(record -> record.balance).min().orElse(0);

            // 각 점 그리기
            int[] xPoints = new int[numPoints];
            int[] yPoints = new int[numPoints];

            for (int i = 0; i < numPoints; i++) {
                xPoints[i] = padding + i * pointSpacing;
                yPoints[i] = (int) (height - padding - (records.get(i).balance - minBalance) / (maxBalance - minBalance) * graphHeight);
            }

            // 선 연결
            for (int i = 0; i < numPoints - 1; i++) {
                g.setColor(Color.BLUE);
                g.drawLine(xPoints[i], yPoints[i], xPoints[i + 1], yPoints[i + 1]);
            }

            // 각 점 및 잔액 값 표시
            for (int i = 0; i < numPoints; i++) {
                g.setColor(Color.RED);
                g.fillOval(xPoints[i] - 4, yPoints[i] - 4, 8, 8); // 점 그리기

                // 잔액 값 표시
                g.setColor(Color.BLACK);
                String balanceText = String.format("%.2f", records.get(i).balance);
                g.drawString(balanceText, xPoints[i] - 15, yPoints[i] - 10); // 점 위에 잔액 표시
            }

            // 축 레이블 추가
            g.setColor(Color.BLACK);
            g.drawString("시간(기록 순서)", width / 2, height - 20); // x축 레이블
            g.drawString("잔액", 20, padding / 2); // y축 레이블
        }
    };

    graphFrame.add(graphPanel);
    graphFrame.setVisible(true);
}

    // main 메서드: 실행 시작
    public static void main(String[] args) {
        FinanceManager manager = new FinanceManager();
        manager.launchGUI();
    }
}
