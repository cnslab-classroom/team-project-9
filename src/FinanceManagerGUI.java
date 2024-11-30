import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class FinanceManagerGUI {
    static class Record {
        String category; // 지출/수입 카테고리
        double amount;   // 금액
        String description; // 설명

        public Record(String category, double amount, String description) {
            this.category = category;
            this.amount = amount;
            this.description = description;
        }

        @Override
        public String toString() {
            return "카테고리: " + category + ", 금액: " + amount + ", 설명: " + description;
        }
    }

    private ArrayList<Record> records; // 기록 리스트
    private double totalIncome;  // 총 수입
    private double totalExpense; // 총 지출
    private JFrame frame;

    public FinanceManagerGUI() {
        this.records = new ArrayList<>();
        this.totalIncome = 0;
        this.totalExpense = 0;
        createGUI();
    }

    private void createGUI() {
        frame = new JFrame("재정 관리 시스템");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);

        // 메인 메뉴 패널
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5, 1, 10, 10));

        // 버튼 추가
        JButton addIncomeButton = new JButton("수입 추가");
        JButton addExpenseButton = new JButton("지출 추가");
        JButton viewRecordsButton = new JButton("전체 기록 보기");
        JButton exitButton = new JButton("프로그램 종료");

        // 버튼 클릭 이벤트 처리
        addIncomeButton.addActionListener(e -> addIncome());
        addExpenseButton.addActionListener(e -> addExpense());
        viewRecordsButton.addActionListener(e -> viewRecords());
        exitButton.addActionListener(e -> System.exit(0));

        // 패널에 버튼 추가
        panel.add(addIncomeButton);
        panel.add(addExpenseButton);
        panel.add(viewRecordsButton);
        panel.add(exitButton);

        // 프레임에 패널 추가
        frame.getContentPane().add(panel);
        frame.setVisible(true);
    }

    private void addIncome() {
        // 수입 입력 폼
        String category = JOptionPane.showInputDialog(frame, "수입 카테고리 입력:");
        String amountStr = JOptionPane.showInputDialog(frame, "금액 입력:");
        String description = JOptionPane.showInputDialog(frame, "설명 입력:");

        try {
            double amount = Double.parseDouble(amountStr);
            records.add(new Record(category, amount, description));
            totalIncome += amount;
            JOptionPane.showMessageDialog(frame, "수입이 추가되었습니다.");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, "잘못된 금액 입력입니다!");
        }
    }

    private void addExpense() {
        // 지출 입력 폼
        String category = JOptionPane.showInputDialog(frame, "지출 카테고리 입력:");
        String amountStr = JOptionPane.showInputDialog(frame, "금액 입력:");
        String description = JOptionPane.showInputDialog(frame, "설명 입력:");

        try {
            double amount = Double.parseDouble(amountStr);
            records.add(new Record(category, -amount, description));
            totalExpense += amount;
            JOptionPane.showMessageDialog(frame, "지출이 추가되었습니다.");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, "잘못된 금액 입력입니다!");
        }
    }

    private void viewRecords() {
        // 기록 출력
        StringBuilder recordDisplay = new StringBuilder("재정 기록:\n");
        for (Record record : records) {
            recordDisplay.append(record).append("\n");
        }
        recordDisplay.append("\n총 수입: ").append(totalIncome);
        recordDisplay.append("\n총 지출: ").append(totalExpense);
        recordDisplay.append("\n순자산: ").append(totalIncome - totalExpense);

        JOptionPane.showMessageDialog(frame, recordDisplay.toString());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(FinanceManagerGUI::new);
    }
}
