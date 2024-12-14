import java.io.*;
import java.util.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.format.DateTimeParseException;
import javax.swing.*;
import java.awt.event.*;

public class StudyManagerAppGUI {
    private static Map<String, List<String>> schedule = new HashMap<>();  // 요일별 강의 목록
    private static Map<String, String> courseSummaries = new HashMap<>();  // 강의 요약
    private static Map<String, List<LocalDate>> reviewDates = new HashMap<>();  // 복습 날짜 목록
    private static final String SCHEDULE_FILE = "schedule.txt";
    private static final String SUMMARY_FILE = "course_summaries.txt";
    private static final String REVIEW_FILE = "review_cycle.txt";

    public static void main(String[] args) {
        loadData();  // 프로그램 시작 시 데이터 불러오기

        // GUI 구성
        JFrame frame = new JFrame("Study Manager");
        frame.setSize(500, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 버튼과 텍스트 영역
        JPanel panel = new JPanel();
        JTextArea textArea = new JTextArea(20, 40);
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        panel.add(scrollPane);

        JButton manageScheduleButton = new JButton("강의 목록 관리");
        JButton manageSummaryButton = new JButton("강의 요약 관리");
        JButton reviewNotificationButton = new JButton("복습 알림 관리");

        panel.add(manageScheduleButton);
        panel.add(manageSummaryButton);
        panel.add(reviewNotificationButton);

        // 각 버튼에 대한 이벤트 리스너
        manageScheduleButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                StringBuilder builder = new StringBuilder();
                builder.append("강의 목록 관리 기능을 사용합니다.\n");
                manageSchedule(builder);
                textArea.setText(builder.toString());
            }
        });

        manageSummaryButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                StringBuilder builder = new StringBuilder();
                builder.append("강의 요약 관리 기능을 사용합니다.\n");
                manageCourseSummary(builder);
                textArea.setText(builder.toString());
            }
        });

        reviewNotificationButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                StringBuilder builder = new StringBuilder();
                builder.append("복습 알림 관리 기능을 사용합니다.\n");
                reviewNotification(builder);
                textArea.setText(builder.toString());
            }
        });

        // 초기화 및 프레임 보이기
        frame.add(panel);
        frame.setVisible(true);
    }

    // 1. 요일별 강의 목록 입력하고 출력
    private static void manageSchedule(StringBuilder builder) {
        String day = JOptionPane.showInputDialog("요일을 입력하세요 (예: MONDAY): ");
        String courses = JOptionPane.showInputDialog("해당 요일에 들어야 할 강의들을 입력하세요 (쉼표로 구분): ");
        List<String> courseList = Arrays.asList(courses.split(","));
        schedule.put(day.toUpperCase(), courseList);

        builder.append(day + "의 강의 목록이 저장되었습니다.\n");
        saveData();  // 데이터 저장
    }

    // 2. 강의 요약 입력하고 출력
    private static void manageCourseSummary(StringBuilder builder) {
        while (true) {
            String[] options = {"강의 요약 저장", "강의 요약 보기", "메뉴로 돌아가기"};
            int choice = JOptionPane.showOptionDialog(null, "원하는 기능을 선택하세요", "강의 요약 관리",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

            if (choice == 0) {
                String courseName = JOptionPane.showInputDialog("강의명을 입력하세요: ");
                String summary = JOptionPane.showInputDialog(courseName + " 강의에 대한 요약을 입력하세요: ");
                courseSummaries.put(courseName, summary);

                builder.append(courseName + " 강의의 요약이 저장되었습니다.\n");
                saveData();  // 데이터 저장
            } else if (choice == 1) {
                String courseName = JOptionPane.showInputDialog("요약을 보고 싶은 강의명을 입력하세요: ");
                if (courseSummaries.containsKey(courseName)) {
                    builder.append(courseName + " 강의의 요약:\n");
                    builder.append(courseSummaries.get(courseName) + "\n");
                } else {
                    builder.append(courseName + " 강의에 대한 요약이 저장되어 있지 않습니다.\n");
                }
            } else if (choice == 2) {
                break;  // 메뉴로 돌아가기
            } else {
                builder.append("잘못된 입력입니다.\n");
            }
        }
    }

    // 3. 복습 주기 설정 및 알림 기능
    private static void reviewNotification(StringBuilder builder) {
        String courseName = JOptionPane.showInputDialog("복습 주기를 설정할 강의명을 입력하세요: ");
        String startDateInput = JOptionPane.showInputDialog(courseName + " 강의의 복습 시작일을 입력하세요 (yyyy-MM-dd): ");
        LocalDate startDate = null;
        // 날짜 형식이 맞는지 확인
        try {
            startDate = LocalDate.parse(startDateInput);
        } catch (DateTimeParseException e) {
            builder.append("잘못된 날짜 형식입니다. yyyy-MM-dd 형식으로 입력해주세요.\n");
            return;
        }

        String endDateInput = JOptionPane.showInputDialog("복습 종료일을 입력하세요 (yyyy-MM-dd): ");
        LocalDate endDate = null;
        // 날짜 형식이 맞는지 확인
        try {
            endDate = LocalDate.parse(endDateInput);
        } catch (DateTimeParseException e) {
            builder.append("잘못된 날짜 형식입니다. yyyy-MM-dd 형식으로 입력해주세요.\n");
            return;
        }

        if (endDate.isBefore(startDate)) {
            builder.append("복습 종료일은 시작일 이후여야 합니다. 다시 입력해주세요.\n");
            return;
        }

        String cycleInput = JOptionPane.showInputDialog(courseName + " 강의의 복습 주기를 며칠 간격으로 설정하시겠습니까? ");
        int cycle = Integer.parseInt(cycleInput);

        // 복습 날짜 계산
        List<LocalDate> reviewDatesList = new ArrayList<>();
        LocalDate nextReviewDate = startDate;
        while (!nextReviewDate.isAfter(endDate)) {
            reviewDatesList.add(nextReviewDate);
            nextReviewDate = nextReviewDate.plusDays(cycle);
        }

        // 복습 날짜 저장
        reviewDates.put(courseName, reviewDatesList);

        builder.append(courseName + " 강의의 복습 주기가 " + cycle + "일로 설정되었습니다. 복습 시작일은 " + startDate + "이고, 종료일은 " + endDate + "입니다.\n");
        saveData();  // 데이터 저장

        // 복습 알림 출력
        printDailyScheduleAndReviewNotification(builder);
    }

    // 복습 알림과 강의 목록 출력
    private static void printDailyScheduleAndReviewNotification(StringBuilder builder) {
        LocalDate today = LocalDate.now();
        String dayOfWeek = today.getDayOfWeek().toString();  // 요일 구하기

        builder.append("\n오늘은 " + today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + " (" + dayOfWeek + ") 입니다.\n");

        // 요일에 맞는 강의 목록 출력
        if (schedule.containsKey(dayOfWeek)) {
            List<String> todayCourses = schedule.get(dayOfWeek);
            builder.append("오늘의 강의 목록:\n");
            for (String course : todayCourses) {
                builder.append("- " + course + "\n");
            }
        } else {
            builder.append("오늘의 강의 목록이 없습니다.\n");
        }

        // 복습 알림 출력
        boolean reviewReminderShown = false;
        for (Map.Entry<String, List<LocalDate>> entry : reviewDates.entrySet()) {
            String courseName = entry.getKey();
            List<LocalDate> dates = entry.getValue();

            if (dates.contains(today)) {
                builder.append(courseName + " 복습 주기가 맞습니다. 오늘 복습해야 합니다!\n");
                reviewReminderShown = true;
            }
        }

        if (!reviewReminderShown) {
            builder.append("오늘 복습할 강의는 없습니다.\n");
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
