import java.io.*;
import java.util.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;  

public class StudyManagerApp {
    private static Map<String, List<String>> schedule = new HashMap<>();  // 요일별 강의 목록
    private static Map<String, String> courseSummaries = new HashMap<>();  // 강의 요약
    private static Map<String, List<LocalDate>> reviewDates = new HashMap<>();  // 복습 날짜 목록
    private static Scanner scanner = new Scanner(System.in);
    private static final String SCHEDULE_FILE = "schedule.txt";
    private static final String SUMMARY_FILE = "course_summaries.txt";
    private static final String REVIEW_FILE = "review_cycle.txt";

    public static void main(String[] args) {
        loadData();  // 프로그램 시작 시 데이터 불러오기

        while (true) {
            // 메뉴를 선택하기 전에, 항상 그 날에 맞는 강의 목록과 복습 알림을 출력
            printDailyScheduleAndReviewNotification();

            // 메뉴 출력
            System.out.println("\n메뉴:");
            System.out.println("1. 강의 목록 관리");
            System.out.println("2. 강의 요약 관리");
            System.out.println("3. 복습 알림 관리");
            System.out.println("4. 종료");
            System.out.print("원하는 기능을 선택하세요 (1-4): ");
            int choice = scanner.nextInt();
            scanner.nextLine();  // 버퍼 비우기

            switch (choice) {
                case 1:
                    manageSchedule();
                    break;
                case 2:
                    manageCourseSummary();
                    break;
                case 3:
                    reviewNotification();
                    break;
                case 4:
                    saveData();  // 프로그램 종료 전에 데이터 저장
                    System.out.println("프로그램을 종료합니다.");
                    return;
                default:
                    System.out.println("잘못된 입력입니다. 다시 선택해주세요.");
            }
        }
    }

    // 1. 요일별 강의 목록 입력하고 출력
    private static void manageSchedule() {
        System.out.print("요일을 입력하세요 (예: MONDAY): ");
        String day = scanner.nextLine().toUpperCase();

        System.out.print("해당 요일에 들어야 할 강의들을 입력하세요 (쉼표로 구분): ");
        String courses = scanner.nextLine();
        List<String> courseList = Arrays.asList(courses.split(","));
        schedule.put(day, courseList);

        System.out.println(day + "의 강의 목록이 저장되었습니다.");
        saveData();  // 데이터 저장
    }

    // 2. 강의 요약 입력하고 출력
    private static void manageCourseSummary() {
        while (true) {
            System.out.println("1. 강의 요약 저장");
            System.out.println("2. 강의 요약 보기");
            System.out.println("3. 메뉴로 돌아가기");
            System.out.print("원하는 기능을 선택하세요 (1-3): ");
            int choice = scanner.nextInt();
            scanner.nextLine();  // 버퍼 비우기

            if (choice == 1) {
                System.out.print("강의명을 입력하세요: ");
                String courseName = scanner.nextLine();

                System.out.print(courseName + " 강의에 대한 요약을 입력하세요: ");
                String summary = scanner.nextLine();
                courseSummaries.put(courseName, summary);

                System.out.println(courseName + " 강의의 요약이 저장되었습니다.");
                saveData();  // 데이터 저장
            } else if (choice == 2) {
                System.out.print("요약을 보고 싶은 강의명을 입력하세요: ");
                String courseName = scanner.nextLine();

                if (courseSummaries.containsKey(courseName)) {
                    System.out.println(courseName + " 강의의 요약:");
                    System.out.println(courseSummaries.get(courseName));
                } else {
                    System.out.println(courseName + " 강의에 대한 요약이 저장되어 있지 않습니다.");
                }
            } else if (choice == 3) {
                break;  // 메뉴로 돌아가기
            } else {
                System.out.println("잘못된 입력입니다.");
            }
        }
    }

    // 3. 복습 주기 설정 및 알림 기능
private static void reviewNotification() {
    System.out.print("복습 주기를 설정할 강의명을 입력하세요: ");
    String courseName = scanner.nextLine();

    // 복습 시작일과 종료일을 입력받기
    System.out.print(courseName + " 강의의 복습 시작일을 입력하세요 (yyyy-MM-dd): ");
    String startDateInput = scanner.nextLine();
    LocalDate startDate = null;
    // 날짜 형식이 맞는지 확인
    try {
        startDate = LocalDate.parse(startDateInput);
    } catch (DateTimeParseException e) {
        System.out.println("잘못된 날짜 형식입니다. yyyy-MM-dd 형식으로 입력해주세요.");
        return;
    }

    System.out.print("복습 종료일을 입력하세요 (yyyy-MM-dd): ");
    String endDateInput = scanner.nextLine();
    LocalDate endDate = null;
    // 날짜 형식이 맞는지 확인
    try {
        endDate = LocalDate.parse(endDateInput);
    } catch (DateTimeParseException e) {
        System.out.println("잘못된 날짜 형식입니다. yyyy-MM-dd 형식으로 입력해주세요.");
        return;
    }

    // 종료일이 시작일 이후인지 확인
    if (endDate.isBefore(startDate)) {
        System.out.println("복습 종료일은 시작일 이후여야 합니다. 다시 입력해주세요.");
        return;
    }

    System.out.print(courseName + " 강의의 복습 주기를 며칠 간격으로 설정하시겠습니까? (숫자만 입력) ");
    while (!scanner.hasNextInt()) {
        System.out.println("잘못된 입력입니다. 숫자를 입력하세요.");
        scanner.nextLine();  // 잘못된 입력을 처리하고 다시 입력 받음
    }
    int cycle = scanner.nextInt();
    scanner.nextLine();  // 버퍼 비우기

    // 복습 날짜 계산
    List<LocalDate> reviewDatesList = new ArrayList<>();
    LocalDate nextReviewDate = startDate;
    while (!nextReviewDate.isAfter(endDate)) {
        reviewDatesList.add(nextReviewDate);
        nextReviewDate = nextReviewDate.plusDays(cycle);
    }

    // 복습 날짜 저장
    reviewDates.put(courseName, reviewDatesList);

    System.out.println(courseName + " 강의의 복습 주기가 " + cycle + "일로 설정되었습니다. 복습 시작일은 " + startDate + "이고, 종료일은 " + endDate + "입니다.");
    saveData();  // 데이터 저장

    // 복습 알림 출력
    printDailyScheduleAndReviewNotification();
}

// 복습 알림과 강의 목록 출력
private static void printDailyScheduleAndReviewNotification() {
    LocalDate today = LocalDate.now();
    String dayOfWeek = today.getDayOfWeek().toString();  // 요일 구하기

    System.out.println("\n오늘은 " + today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + " (" + dayOfWeek + ") 입니다.");

    // 요일에 맞는 강의 목록 출력
    if (schedule.containsKey(dayOfWeek)) {
        List<String> todayCourses = schedule.get(dayOfWeek);
        System.out.println("오늘의 강의 목록:");
        for (String course : todayCourses) {
            System.out.println("- " + course);
        }
    } else {
        System.out.println("오늘의 강의 목록이 없습니다.");
    }

    // 복습 알림 출력
    boolean reviewReminderShown = false;
    for (Map.Entry<String, List<LocalDate>> entry : reviewDates.entrySet()) {
        String courseName = entry.getKey();
        List<LocalDate> dates = entry.getValue();

        if (dates.contains(today)) {
            System.out.println(courseName + " 복습 주기가 맞습니다. 오늘 복습해야 합니다!");
            reviewReminderShown = true;
        }
    }

    if (!reviewReminderShown) {
        System.out.println("오늘 복습할 강의는 없습니다.");
    }
}


    // 데이터 저장
    private static void saveData() {
        try (BufferedWriter scheduleWriter = new BufferedWriter(new FileWriter(SCHEDULE_FILE));
             BufferedWriter summaryWriter = new BufferedWriter(new FileWriter(SUMMARY_FILE, true)); // true로 append 모드 설정
             BufferedWriter reviewWriter = new BufferedWriter(new FileWriter(REVIEW_FILE))) {

            // 요일별 강의 목록 저장
            for (Map.Entry<String, List<String>> entry : schedule.entrySet()) {
                scheduleWriter.write(entry.getKey() + ": " + String.join(",", entry.getValue()));
                scheduleWriter.newLine();
            }

            // 강의 요약 저장 (append 모드로 파일에 이어쓰기)
            for (Map.Entry<String, String> entry : courseSummaries.entrySet()) {
                summaryWriter.write(entry.getKey() + ": " + entry.getValue());
                summaryWriter.newLine();
            }

            // 복습 날짜 저장
            for (Map.Entry<String, List<LocalDate>> entry : reviewDates.entrySet()) {
                StringBuilder sb = new StringBuilder(entry.getKey() + ": ");
                for (LocalDate date : entry.getValue()) {
                    sb.append(date.toString()).append(",");  // LocalDate를 String 형식으로 변환하여 저장
                }
                sb.setLength(sb.length() - 1);  // 마지막 쉼표 제거
                reviewWriter.write(sb.toString());
                reviewWriter.newLine();
            }

        } catch (IOException e) {
            System.out.println("데이터 저장 중 오류 발생: " + e.getMessage());
        }
    }

    // 데이터 불러오기
private static void loadData() {
    try (BufferedReader scheduleReader = new BufferedReader(new FileReader(SCHEDULE_FILE));
         BufferedReader summaryReader = new BufferedReader(new FileReader(SUMMARY_FILE));
         BufferedReader reviewReader = new BufferedReader(new FileReader(REVIEW_FILE))) {

        String line;
        // 요일별 강의 목록 불러오기
        while ((line = scheduleReader.readLine()) != null) {
            String[] parts = line.split(": ");
            if (parts.length == 2) {  // 유효한 데이터만 처리
                schedule.put(parts[0], Arrays.asList(parts[1].split(",")));
            }
        }

        // 강의 요약 불러오기
        while ((line = summaryReader.readLine()) != null) {
            String[] parts = line.split(": ");
            if (parts.length == 2) {  // 유효한 데이터만 처리
                courseSummaries.put(parts[0], parts[1]);
            }
        }

        // 복습 날짜 불러오기
        while ((line = reviewReader.readLine()) != null) {
            String[] parts = line.split(": ");
            if (parts.length == 2) {  // 유효한 데이터만 처리
                List<LocalDate> dates = new ArrayList<>();
                for (String dateStr : parts[1].split(",")) {
                    try {
                        dates.add(LocalDate.parse(dateStr));  // 날짜 형식 맞춰서 파싱
                    } catch (DateTimeParseException e) {
                        System.out.println("잘못된 날짜 형식: " + dateStr);
                    }
                }
                reviewDates.put(parts[0], dates);
            }
        }

    } catch (IOException e) {
        System.out.println("데이터 불러오기 중 오류 발생: " + e.getMessage());
    }
}
}