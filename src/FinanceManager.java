import java.util.ArrayList;

public class FinanceManager {
    // 재정 기록 클래스
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

    public FinanceManager() {
        this.records = new ArrayList<>();
        this.totalIncome = 0;
        this.totalExpense = 0;
    }

    // 수입 추가
    public void addIncome(String category, double amount, String description) {
        records.add(new Record(category, amount, description));
        totalIncome += amount;
    }

    // 지출 추가
    public void addExpense(String category, double amount, String description) {
        records.add(new Record(category, -amount, description));
        totalExpense += amount;
    }

    // 전체 기록 출력
    public void printRecords() {
        System.out.println("재정 기록:");
        for (Record record : records) {
            System.out.println(record);
        }
        System.out.println("총 수입: " + totalIncome);
        System.out.println("총 지출: " + totalExpense);
        System.out.println("순자산: " + (totalIncome - totalExpense));
    }

    public static void main(String[] args) {
        FinanceManager manager = new FinanceManager();

        // 테스트 데이터
        manager.addIncome("월급", 5000000, "10월 월급");
        manager.addExpense("월세", 1200000, "10월 월세");
        manager.addExpense("식비", 300000, "주간 식비");
        manager.printRecords();
    }
}
