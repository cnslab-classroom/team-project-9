import javax.swing.*;
import java.awt.*;

public class FinanceManagerGUI {
    private FinanceManager financeManager; // FinanceManager 참조

    public FinanceManagerGUI(FinanceManager financeManager) {
        this.financeManager = financeManager;
    }

    // Finance Manager 패널 생성 메서드
    public JPanel createFinancePanel(Runnable backAction) {
        JPanel financePanel = new JPanel(new BorderLayout());

        // 상단 뒤로가기 버튼
        JButton backButton = new JButton("뒤로가기");
        backButton.addActionListener(e -> backAction.run());
        financePanel.add(backButton, BorderLayout.NORTH);

        // 중앙 출력 영역
        JTextArea recordsArea = new JTextArea();
        recordsArea.setEditable(false);
        recordsArea.setLineWrap(true);
        recordsArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(recordsArea);

        // 버튼 패널
        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        JButton addIncomeButton = new JButton("수입 추가");
        JButton addExpenseButton = new JButton("지출 추가");
        JButton viewGraphButton = new JButton("잔액 그래프 보기");

        // 버튼 동작 설정
        addIncomeButton.addActionListener(e -> handleAddIncome(recordsArea));
        addExpenseButton.addActionListener(e -> handleAddExpense(recordsArea));
        viewGraphButton.addActionListener(e -> financeManager.showGraph());

        buttonPanel.add(addIncomeButton);
        buttonPanel.add(addExpenseButton);
        buttonPanel.add(viewGraphButton);

        // 메인 패널에 추가
        financePanel.add(scrollPane, BorderLayout.CENTER);
        financePanel.add(buttonPanel, BorderLayout.SOUTH);

        return financePanel;
    }

    // 수입 추가 처리
    private void handleAddIncome(JTextArea recordsArea) {
        String category = JOptionPane.showInputDialog("수입 카테고리:");
        String amountStr = JOptionPane.showInputDialog("금액:");
        String description = JOptionPane.showInputDialog("설명:");
        try {
            double amount = Double.parseDouble(amountStr);
            financeManager.addIncome(category, amount, description);
            recordsArea.setText(getRecordsText());
            JOptionPane.showMessageDialog(null, "수입이 추가되었습니다.");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "잘못된 입력입니다!", "입력 오류", JOptionPane.ERROR_MESSAGE);
        }
    }

    // 지출 추가 처리
    private void handleAddExpense(JTextArea recordsArea) {
        String category = JOptionPane.showInputDialog("지출 카테고리:");
        String amountStr = JOptionPane.showInputDialog("금액:");
        String description = JOptionPane.showInputDialog("설명:");
        try {
            double amount = Double.parseDouble(amountStr);
            financeManager.addExpense(category, amount, description);
            recordsArea.setText(getRecordsText());
            JOptionPane.showMessageDialog(null, "지출이 추가되었습니다.");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "잘못된 입력입니다!", "입력 오류", JOptionPane.ERROR_MESSAGE);
        }
    }

    // 기록 텍스트 반환
    private String getRecordsText() {
        StringBuilder recordsText = new StringBuilder("전체 기록:\n");
        for (FinanceManager.Record record : financeManager.getRecords()) {
            recordsText.append(record).append("\n");
        }
        return recordsText.toString();
    }
}
