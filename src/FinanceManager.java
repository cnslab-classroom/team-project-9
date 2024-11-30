import java.util.ArrayList;
import java.util.Scanner;

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

    // 데이터 삭제
    public void deleteRecord(int index) {
        if (index >= 0 && index < records.size()) {
            Record removed = records.remove(index);
            if (removed.amount > 0) {
                totalIncome -= removed.amount;
            } else {
                totalExpense += removed.amount; // 음수 값을 더해야 하므로 += 사용
            }
            System.out.println("기록이 삭제되었습니다: " + removed);
        } else {
            System.out.println("잘못된 인덱스입니다. 삭제 실패!");
        }
    }

    // 특정 카테고리 검색
    public void searchByCategory(String category) {
        System.out.println("카테고리: " + category + "의 기록:");
        for (Record record : records) {
            if (record.category.equals(category)) {
                System.out.println(record);
            }
        }
    }

    // 전체 기록 출력
    public void printRecords() {
        System.out.println("재정 기록:");
        for (int i = 0; i < records.size(); i++) {
            System.out.println("[" + i + "] " + records.get(i));
        }
        System.out.println("총 수입: " + totalIncome);
        System.out.println("총 지출: " + totalExpense);
        System.out.println("순자산: " + (totalIncome - totalExpense));
    }

    // 사용자 입력을 통한 기능 실행
    public void run() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\n=== 재정 관리 시스템 ===");
            System.out.println("1. 수입 추가");
            System.out.println("2. 지출 추가");
            System.out.println("3. 기록 삭제");
            System.out.println("4. 카테고리별 검색");
            System.out.println("5. 전체 기록 보기");
            System.out.println("0. 종료");
            System.out.print("메뉴 선택: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // 버퍼 정리

            if (choice == 0) {
                System.out.println("시스템을 종료합니다.");
                break;
            }

            switch (choice) {
                case 1:
                    System.out.print("수입 카테고리: ");
                    String incomeCategory = scanner.nextLine();
                    System.out.print("금액: ");
                    double incomeAmount = scanner.nextDouble();
                    scanner.nextLine(); // 버퍼 정리
                    System.out.print("설명: ");
                    String incomeDescription = scanner.nextLine();
                    addIncome(incomeCategory, incomeAmount, incomeDescription);
                    System.out.println("수입이 추가되었습니다.");
                    break;
                case 2:
                    System.out.print("지출 카테고리: ");
                    String expenseCategory = scanner.nextLine();
                    System.out.print("금액: ");
                    double expenseAmount = scanner.nextDouble();
                    scanner.nextLine(); // 버퍼 정리
                    System.out.print("설명: ");
                    String expenseDescription = scanner.nextLine();
                    addExpense(expenseCategory, expenseAmount, expenseDescription);
                    System.out.println("지출이 추가되었습니다.");
                    break;
                case 3:
                    printRecords();
                    System.out.print("삭제할 기록 번호: ");
                    int recordIndex = scanner.nextInt();
                    deleteRecord(recordIndex);
                    break;
                case 4:
                    System.out.print("검색할 카테고리: ");
                    String searchCategory = scanner.nextLine();
                    searchByCategory(searchCategory);
                    break;
                case 5:
                    printRecords();
                    break;
                default:
                    System.out.println("잘못된 입력입니다. 다시 시도해주세요.");
                    break;
            }
        }
        scanner.close();
    }

    public static void main(String[] args) {
        FinanceManager manager = new FinanceManager();
        manager.run();
    }
}
