import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.awt.BorderLayout;
import java.awt.GridLayout;


public class StudyManagerAppGUI {
    private static Map<String, List<String>> schedule = new HashMap<>();  // 요일별 강의 목록
    private static Map<String, StringBuilder> courseSummaries = new HashMap<>();  // 강의 요약
    private static Map<String, Integer> reviewCycle = new HashMap<>();  // 복습 주기
    private static Map<String, List<LocalDate>> reviewDates = new HashMap<>();  // 복습 날짜
    private static final String SCHEDULE_FILE = "schedule.txt";
    private static final String SUMMARY_FILE = "course_summaries.txt";
    private static final String REVIEW_FILE = "review_cycle.txt";

    private static final JFrame frame = new JFrame("Study Manager");
    private static JTextArea textArea = new JTextArea(20, 40);
    private static final JPanel panel = new JPanel();


    public JPanel createStudyPanel(Runnable backAction) {
        JPanel studyPanel = new JPanel(new BorderLayout());

        // 중앙 출력 영역
        textArea = new JTextArea("");
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(textArea);

        studyPanel.add(scrollPane, BorderLayout.CENTER);

    // 버튼 패널
    JPanel buttonPanel = new JPanel(new GridLayout(6, 1, 10, 10));
    JButton manageScheduleButton = new JButton("강의 목록 관리");
    JButton manageSummariesButton = new JButton("강의 요약 관리");
    JButton reviewNotificationsButton = new JButton("복습 알림 관리");
    JButton scheduleCheckButton = new JButton("스케줄 확인");
    JButton resetButton = new JButton("전체 데이터 초기화"); // 이름만 설정
    JButton exitButton = new JButton("실행 종료");

    // 버튼 패널에 추가
    buttonPanel.add(manageScheduleButton);
    buttonPanel.add(manageSummariesButton);
    buttonPanel.add(reviewNotificationsButton);
    buttonPanel.add(scheduleCheckButton);
    buttonPanel.add(resetButton);
    buttonPanel.add(exitButton);

    // 버튼 동작 설정 - 기존 StudyManagerAppGUI.java 코드 재사용
    StudyManagerAppGUI studyManagerAppGUI = new StudyManagerAppGUI();

    manageScheduleButton.addActionListener(e -> manageSchedule());
    manageSummariesButton.addActionListener(e -> manageCourseSummary());
    reviewNotificationsButton.addActionListener(e -> reviewNotification());
    scheduleCheckButton.addActionListener(e -> manageSchedule());

    // resetButton 동작은 기존 코드에서 처리
    resetButton.addActionListener(e -> {
        studyManagerAppGUI.resetData(); // 기존 메서드 호출
    });

    exitButton.addActionListener(e -> System.exit(0));

    // 버튼 패널을 중앙에 추가
    studyPanel.add(buttonPanel, BorderLayout.SOUTH);

    return studyPanel;
}
    public static void main(String[] args) {
        loadData();  // 프로그램 시작 시 데이터 불러오기

        // GUI 구성
        frame.setSize(500, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        panel.add(scrollPane);

        JButton manageScheduleButton = new JButton("강의 목록 관리");
        JButton manageSummaryButton = new JButton("강의 요약 관리");
        JButton reviewNotificationButton = new JButton("복습 알림 관리");
        JButton scheduleCheckButton = new JButton("스케줄 확인");
        JButton resetButton = new JButton("전체 데이터 초기화");
        JButton exitButton = new JButton("실행 종료");

        panel.add(manageScheduleButton);
        panel.add(manageSummaryButton);
        panel.add(reviewNotificationButton);
        panel.add(scheduleCheckButton);
        panel.add(resetButton);
        panel.add(exitButton);

        manageScheduleButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                manageSchedule();
            }
        });

        manageSummaryButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                manageCourseSummary();
            }
        });

        reviewNotificationButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                reviewNotification();
            }
        });

        scheduleCheckButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                printDailyScheduleAndReviewNotification();
            }
        });

        // 전체 데이터 초기화 버튼
        resetButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int option = JOptionPane.showConfirmDialog(frame, "모든 데이터를 초기화하시겠습니까?", "데이터 초기화", JOptionPane.YES_NO_OPTION);
                if (option == JOptionPane.YES_OPTION) {
                    schedule.clear();
                    courseSummaries.clear();
                    reviewCycle.clear();
                    reviewDates.clear();
                    JOptionPane.showMessageDialog(frame, "모든 데이터가 초기화되었습니다.");
                    saveData();  // 데이터 저장
                    printDailyScheduleAndReviewNotification();  // 초기화 후 스케줄 출력
                }
            }
        });

        exitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveData();  // 데이터 저장
                System.exit(0);  // 프로그램 종료
            }
        });

        frame.add(panel);
        frame.setVisible(true);
    }

    // 강의 목록 추가 / 삭제
    private static void manageSchedule() {
        String[] options = {"강의 추가", "강의 삭제", "메뉴로 돌아가기"};
        int choice = JOptionPane.showOptionDialog(frame, "원하는 기능을 선택하세요", "강의 목록 관리",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

        if (choice == 0) {
            // 강의 추가
            String day = JOptionPane.showInputDialog("요일을 입력하세요 (예: MONDAY): ");
            String courses = JOptionPane.showInputDialog("추가할 강의를 입력하세요 (쉼표로 구분): ");
            List<String> courseList = new ArrayList<>(Arrays.asList(courses.split(",")));

            if (schedule.containsKey(day)) {
                courseList.addAll(schedule.get(day));  // 기존 강의 목록에 추가
            }

            schedule.put(day, courseList);
            JOptionPane.showMessageDialog(frame, day + "의 강의 목록이 업데이트되었습니다.");
            saveData();  // 데이터 저장
            printDailyScheduleAndReviewNotification();  // 스케줄 확인
        } else if (choice == 1) {
            // 강의 삭제
            String day = JOptionPane.showInputDialog("요일을 입력하세요 (예: MONDAY): ");
            if (schedule.containsKey(day)) {
                String courseToRemove = JOptionPane.showInputDialog("삭제할 강의를 입력하세요: ");
                List<String> courseList = schedule.get(day);
                if (courseList.contains(courseToRemove)) {
                    courseList.remove(courseToRemove);  // 강의 삭제
                    schedule.put(day, courseList);  // 변경된 목록 저장
                    JOptionPane.showMessageDialog(frame, courseToRemove + "가 삭제되었습니다.");
                    saveData();  // 데이터 저장
                    printDailyScheduleAndReviewNotification();  // 스케줄 확인
                } else {
                    JOptionPane.showMessageDialog(frame, "해당 강의가 존재하지 않습니다.");
                }
            } else {
                JOptionPane.showMessageDialog(frame, "해당 요일에 강의 목록이 없습니다.");
            }
        }
    }

    // 강의 요약 저장 / 보기 / 수정
    private static void manageCourseSummary() {
        String[] options = {"강의 요약 저장", "강의 요약 보기", "강의 요약 수정", "메뉴로 돌아가기"};
        int choice = JOptionPane.showOptionDialog(frame, "원하는 기능을 선택하세요", "강의 요약 관리",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

        if (choice == 0) {
            String courseName = JOptionPane.showInputDialog("강의명을 입력하세요: ");
            String summary = JOptionPane.showInputDialog(courseName + " 강의에 대한 요약을 입력하세요: ");
            
            // 강의가 처음일 때 초기화
            courseSummaries.putIfAbsent(courseName, new StringBuilder()); 
            
            // 주차 번호 입력 받기
            String week = JOptionPane.showInputDialog("주차를 입력하세요 (예: 1주차): ");
            
            // 강의 요약에 주차별로 저장
            courseSummaries.get(courseName).append(week).append(": ").append(summary).append("\n");  

            JOptionPane.showMessageDialog(frame, courseName + " 강의의 요약이 저장되었습니다.");
            saveData();  // 데이터 저장
        } else if (choice == 1) {
            String courseName = JOptionPane.showInputDialog("요약을 보고 싶은 강의명을 입력하세요: ");
            if (courseSummaries.containsKey(courseName)) {
                textArea.setText(courseName + " 강의의 요약:\n" + courseSummaries.get(courseName).toString());
            } else {
                JOptionPane.showMessageDialog(frame, courseName + " 강의에 대한 요약이 저장되어 있지 않습니다.");
            }
        } else if (choice == 2) {
            String courseName = JOptionPane.showInputDialog("수정할 강의명을 입력하세요: ");
            if (courseSummaries.containsKey(courseName)) {
                String currentSummary = courseSummaries.get(courseName).toString();
                JOptionPane.showMessageDialog(frame, courseName + " 강의의 현재 요약:\n" + currentSummary);

                String newSummary = JOptionPane.showInputDialog("새로운 요약을 입력하세요: ");
                courseSummaries.put(courseName, new StringBuilder(newSummary));  // 기존 요약을 새로 덮어쓰지 않도록 수정 필요
                JOptionPane.showMessageDialog(frame, courseName + " 강의의 요약이 수정되었습니다.");
                saveData();  // 데이터 저장
            } else {
                JOptionPane.showMessageDialog(frame, courseName + " 강의에 대한 요약이 저장되어 있지 않습니다.");
            }
        }
    }

    // 복습 주기 설정 및 알림
    private static void reviewNotification() {
        // 복습할 강의명 입력 받기
        String courseName = JOptionPane.showInputDialog("복습 주기를 설정할 강의명을 입력하세요: ");
        
        // 복습 시작일과 종료일 입력 받기
        String startDateInput = JOptionPane.showInputDialog(courseName + " 강의의 복습 시작일을 입력하세요 (yyyy-MM-dd): ");
        LocalDate startDate = LocalDate.parse(startDateInput);
        
        String endDateInput = JOptionPane.showInputDialog(courseName + " 강의의 복습 종료일을 입력하세요 (yyyy-MM-dd): ");
        LocalDate endDate = LocalDate.parse(endDateInput);
        
        // 복습 주기 간격 입력 받기
        String cycleInput = JOptionPane.showInputDialog(courseName + " 강의의 복습 주기를 며칠 간격으로 설정하시겠습니까?");
        int cycle = Integer.parseInt(cycleInput);

        // 복습 주기 설정
        List<LocalDate> reviewDatesList = new ArrayList<>();
        LocalDate nextReviewDate = startDate;

        while (!nextReviewDate.isAfter(endDate)) {
            reviewDatesList.add(nextReviewDate);  // 복습 날짜 추가
            nextReviewDate = nextReviewDate.plusDays(cycle);  // 주기마다 날짜 추가
        }

        // 복습 날짜 설정
        reviewCycle.put(courseName, cycle);
        reviewDates.put(courseName, reviewDatesList);

        JOptionPane.showMessageDialog(frame, courseName + " 강의의 복습 주기가 " + cycle + "일로 설정되었습니다." +
                "\n복습 시작일은 " + startDate + "이고, 종료일은 " + endDate + "입니다.");
        
        saveData();  // 데이터 저장
        printDailyScheduleAndReviewNotification();  // 복습 알림 확인
    }

    // 오늘의 날짜와 강의 목록, 복습 알림 출력
    private static void printDailyScheduleAndReviewNotification() {
        LocalDate today = LocalDate.now();
        String dayOfWeek = today.getDayOfWeek().toString();
    
        textArea.setText("오늘은 " + today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + " (" + dayOfWeek + ") 입니다.\n");
    
        if (schedule.containsKey(dayOfWeek)) {
            List<String> todayCourses = schedule.get(dayOfWeek);
            textArea.append("오늘의 강의 목록:\n");
            for (String course : todayCourses) {
                textArea.append("- " + course + "\n");
            }
        } else {
            textArea.append("오늘의 강의 목록이 없습니다.\n");
        }
    
        boolean reviewReminderShown = false;
        for (Map.Entry<String, List<LocalDate>> entry : reviewDates.entrySet()) {
            String courseName = entry.getKey();
            List<LocalDate> dates = entry.getValue();
    
            // 복습 날짜 목록에 오늘 날짜가 포함되어 있다면 알림 표시
            if (dates.contains(today)) {
                textArea.append(courseName + " 복습 주기가 맞습니다. 오늘 복습해야 합니다!\n");
                reviewReminderShown = true;
            }
        }
    
        if (!reviewReminderShown) {
            textArea.append("오늘 복습할 강의는 없습니다.\n");
        }
    }
    

    // 데이터 저장
private static void saveData() {
    try (BufferedWriter scheduleWriter = new BufferedWriter(new FileWriter(SCHEDULE_FILE));
         BufferedWriter summaryWriter = new BufferedWriter(new FileWriter(SUMMARY_FILE, true));  // append 모드
         BufferedWriter reviewWriter = new BufferedWriter(new FileWriter(REVIEW_FILE))) {

        // 강의 목록 저장
        for (Map.Entry<String, List<String>> entry : schedule.entrySet()) {
            scheduleWriter.write(entry.getKey() + ": " + String.join(",", entry.getValue()));
            scheduleWriter.newLine();
        }

        // 강의 요약 저장
        for (Map.Entry<String, StringBuilder> entry : courseSummaries.entrySet()) {
            summaryWriter.write(entry.getKey() + ": " + entry.getValue().toString());
            summaryWriter.newLine();
        }

        // 복습 주기 저장
        for (Map.Entry<String, Integer> entry : reviewCycle.entrySet()) {
            reviewWriter.write(entry.getKey() + ": " + entry.getValue());
            reviewWriter.newLine();
        }

        // 복습 날짜 저장
        for (Map.Entry<String, List<LocalDate>> entry : reviewDates.entrySet()) {
            StringBuilder sb = new StringBuilder(entry.getKey() + ": ");
            for (LocalDate date : entry.getValue()) {
                sb.append(date.toString()).append(",");
            }
            sb.setLength(sb.length() - 1); // 마지막 쉼표 제거
            reviewWriter.write(sb.toString());
            reviewWriter.newLine();
        }

    } catch (IOException e) {
        System.out.println("데이터 저장 중 오류 발생: " + e.getMessage());
    }
}


    // 전체 데이터 초기화 메서드
    public void resetData() {
    // 강의 목록, 요약, 알림 등을 초기화
        schedule.clear();
        courseSummaries.clear();
        reviewCycle.clear();
        reviewDates.clear();

    // 초기화 완료 메시지
    JOptionPane.showMessageDialog(null, "전체 데이터가 초기화되었습니다.", 
                                  "데이터 초기화", JOptionPane.INFORMATION_MESSAGE);
}


// 화면 전환 메서드
public void switchScreen(String screenName) {
    // 화면 전환 로직은 GUI 클래스에서 수행되므로 호출만 넘겨줌
    JOptionPane.showMessageDialog(null, "화면 전환: " + screenName);
    // 실제 GUI 클래스 메서드를 호출하도록 수정할 수 있습니다.
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
                if (parts.length == 2) {
                    schedule.put(parts[0], Arrays.asList(parts[1].split(",")));
                }
            }
        
            // 강의 요약 불러오기
            while ((line = summaryReader.readLine()) != null) {
                String[] parts = line.split(": ");
                if (parts.length == 2) {
                    courseSummaries.put(parts[0], new StringBuilder(parts[1]));
                }
            }
        
            // 복습 주기와 복습 날짜 불러오기
            while ((line = reviewReader.readLine()) != null) {
                String[] parts = line.split(": ");
                if (parts.length == 2) {
                    // 복습 주기
                    if (!parts[1].contains(",")) { // If it's just a single number (the cycle)
                        reviewCycle.put(parts[0], Integer.parseInt(parts[1])); // 복습 주기 값
                    } else { // If it's a list of dates
                        List<LocalDate> dates = new ArrayList<>();
                        for (String dateStr : parts[1].split(",")) {
                            try {
                                dates.add(LocalDate.parse(dateStr));  // 날짜를 LocalDate로 변환
                            } catch (Exception e) {
                                System.out.println("잘못된 날짜 형식: " + dateStr);
                            }
                        }
                        reviewDates.put(parts[0], dates); // 복습 날짜 목록 설정
                    }
                }
            }
        
        } catch (IOException e) {
            System.out.println("데이터 불러오기 중 오류 발생: " + e.getMessage());
        }
    }   
}


