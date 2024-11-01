import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Main {
    static Scanner sc  = new Scanner(System.in);
    static BufferedReader reader;
    static BufferedWriter writer;
    static String dateNow;
    static String timeNow;
    /*
        확장 대비 목록
        1. 날짜검색
        2. 제목검색
        3. 검색을 위한 자료를 따로 ? 좋은것같다.
     */
    // TodoList 저장용 자료구조
    static List<TodoList> todoList = new ArrayList<>();
    // 검색 대비용 자료구조
    static Map<String,List<Integer>> titleIndex = new HashMap<>();
    /*
        메인 프롬프트 
        1. 데이터 로딩 txt 파일 -> 이전에 입력한 날짜 및 Data Map 에 자료저장
        2. 사용자로부터 오늘의 날짜 입력받음
        3. 서비스 목록을 제공하고 입력을 대기 받음
        4. exit 입력 시, Map에 저장된 자료들 txt파일에 저장, 오늘의 날짜 업데이트 YYYY-MM-DD 형식
        5. 파일 close
     */
    public static void main(String[] args) throws IOException {
        // 폴더 생성
        File dir = new File("./data");
        if(!dir.exists()){
            dir.mkdir();
            System.out.println("Data 폴더가 존재하지않습니다. 폴더를 생성합니다.");
        }

        // txt 파일 생성
        File file = new File(dir,"file.txt");
        try{
            if(file.createNewFile()){
                System.out.println("파일이 존재하지않습니다. 새로운 파일을 생성합니다.");
            }else{
                System.out.println("파일이 이미 존재합니다 데이터를 불러옵니다.");
            }
            
        }catch (IOException e) {
            e.printStackTrace();
        }
        // InputManager 생성
        InputManager inputManager = new InputManager();
        // reader,writer 초기화 및 기존 list 맵에 추가
        System.out.println("reader 생성");
        reader = new BufferedReader(new FileReader(file));
        System.out.println("writer 생성");
        writer = new BufferedWriter(new FileWriter(file,true));
        String line = null;
        // Txt 파일 날짜 처리
        if ((line = reader.readLine()) != null) {
            String[] dateAndTime = line.split("\\|");
            if(dateAndTime.length == 2){
                dateNow = dateAndTime[0];
                timeNow = dateAndTime[1];
                System.out.println("사용자가 마지막으로 입력한 날짜와 시간 :" + dateNow+"일 "+timeNow);
            }
        }else{
            System.out.println("설정된 날짜가 없습니다. 최초 오늘 날짜를 설정합니다.");
        }
        String beforeDate = dateNow;
        while(true){
            System.out.println("오늘 날짜를 입력해주세요.(YYYYMMDD): ");
            String today = sc.nextLine();
            if(inputManager.checkDateInput(today,dateNow)){
                dateNow = inputManager.formatDate(today);
                System.out.println("오늘의 날짜 : " + dateNow);
                break;
            }
        }

        while(true){
            System.out.println("현재 시간을 입력해주세요.(HHMM): ");
            String time = sc.nextLine();
            if(inputManager.checkTimeInput(time,timeNow,stringToLocalDate(dateNow),beforeDate)){
                timeNow = inputManager.formatTime(time);
                System.out.println("오늘의 시간 : " + timeNow);
                break;
            }
        }

        // Txt 파일 todoList 처리
        while((line = reader.readLine()) != null){
            TodoList todo = createList(line);
            todoList.add(todo);
        }
        // 검색용 Map 초기화
        initTitleIndex(todoList);
        showTitleIndex();

        // 저장된 할 일 목록을 출력
        System.out.println("todoList 프로그램을 시작합니다.\n\n\n");

        TodoListManager todoListManager = new TodoListManager(sc,dateNow,
                timeNow,todoList,inputManager);
        todoListManager.showList(todoList);

        String input = "";
        // 프로그램 시작
        while(true){
            System.out.println("원하는 서비스의 명령어를 입력해주세요.\na : 리스트 추가하기\nm : 리스트" +
                    " 수정하기\nd : 리스트 삭제\nc : 리스트 체크 및 해제");
            System.out.println("종료를 원하시면 exit을 입력해주세요.");
            input = sc.nextLine();
            if(input.equals("a")){
                todoListManager.addList(todoList);
            }else if(input.equals("m")){
                todoListManager.updateList(todoList);
            }else if(input.equals("d")){
                todoListManager.deleteList(todoList);
            }else if(input.equals("c")){
                todoListManager.checkList(todoList);
            }else if(input.equals("exit")){
                break;
            }else{
                System.out.println("잘못된 입력입니다.");
            }
        }
        updateFile(todoList);
        writer.close();
        reader.close();
    }
    // 종료전에 만들어진 MAP을 통해서 FILE 재설정
    private static void updateFile(List<TodoList> todoList) {
        File file = new File("./data/file.txt");
        StringBuilder fileContent = new StringBuilder();
        fileContent.append(dateNow).append("|").append(timeNow).append(System.lineSeparator());
        for (TodoList todo : todoList) {
            String check = todo.isCheck() ? "Y" : "N";
            String checkAfterDeadline = todo.isCanCheckAfterDeadline() ? "Y":
                    "N";
            String line = todo.getTitle() + "|"
                    + (todo.getDeadline() != null ? todo.getDeadline() : "") + "|"
                    + (todo.getDeadTime() != null ? todo.getDeadTime() : "") + "|"
                    + checkAfterDeadline + "|"
                    + (todo.getCheckStartDate() != null ? todo.getCheckStartDate() : "") + "|"
                    + (todo.getCheckStartTime() != null ? todo.getCheckStartTime() : "") + "|"
                    + check;
            fileContent.append(line).append(System.lineSeparator());
        }

        try (BufferedWriter fileWriter = new BufferedWriter(new FileWriter(file, false))) {
            fileWriter.write(fileContent.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("todoList File 업데이트 완료");
    }
    // txt 파일을 불러와서 List 생성
    /*
        parts[0]: 제목
        parts[1]: 마감일
        parts[2]: 마감시간
        parts[3]: 마감이후 체크 가능 여부
        parts[4]: 체크 가능 일
        parts[5]: 체크 가능 시간
        parts[6]: 체크 여부 - Y : 체크, N : 미체크
     */
    public static TodoList createList(String text) {
        String[] parts = text.split("\\|");
        String title = parts[0];
        String deadline = parts[1];
        String deadTime = parts[2];
        boolean isCheckAfterDeadline = "Y".equals(parts[3]);
        String checkDate = parts[4];
        String checkTime = parts[5];
        boolean isCheck = "Y".equals(parts[6]);

        LocalDate date1 = null;
        LocalTime time1 = null;
        LocalDate date2 = null;
        LocalTime time2 = null;

        // 마감일, 시간 변환 (빈 문자열이 아닐 때만)
        if (!deadline.isEmpty()) {
            date1 = stringToLocalDate(deadline);
        }
        if (!deadTime.isEmpty()) {
            time1 = stringToLocalTime(deadTime);
        }
        // 체크 시작일, 시간 변환 (빈 문자열이 아닐 때만)
        if (!checkDate.isEmpty()) {
            date2 = stringToLocalDate(checkDate);
        }
        if (!checkTime.isEmpty()) {
            time2 = stringToLocalTime(checkTime);
        }

        TodoList todo;
        // 마감일 x, 체크 시작/마감 x
        if (date1 == null && time1 == null) {
            todo = TodoList.onlyTitle(title);
        }
        // 마감일 o, 체크 시작/마감 x
        else if (date2 == null && time2 == null) {
            if (isCheckAfterDeadline) {
                todo = TodoList.titleAndDeadlineCAN(title, date1, time1);
            } else {
                todo = TodoList.titleAndDeadlineCANT(title, date1, time1);
            }
        }
        // 마감일 o, 체크 시작/마감 o
        else {
            if (isCheckAfterDeadline) {
                todo = TodoList.titleAndDeadlineAndStartDayCAN(title, date1, time1, date2, time2);
            } else {
                todo = TodoList.titleAndDeadlineAndStartDayCANT(title, date1, time1, date2, time2);
            }
        }
        todo.setCheck(isCheck);
        return todo;
    }

    // 검색용 Map 초기화 함수
    public static void initTitleIndex(List<TodoList> todoList){
        titleIndex.clear();
        for (int i = 0; i < todoList.size(); i++) {
            String title = todoList.get(i).getTitle().toLowerCase();
            if(!titleIndex.containsKey(title)){
                titleIndex.put(title,new ArrayList<>());
            }
            titleIndex.get(title).add(i);
        }
    }
    // Map의 내용을 출력하는 메소드
    public static void showTitleIndex() {
        System.out.println("========== 제목 인덱스 목록 ==========");
        for (Map.Entry<String, List<Integer>> entry : titleIndex.entrySet()) {
            String title = entry.getKey();
            List<Integer> indices = entry.getValue();

            System.out.print("Title: " + title + " -> Indices: ");
            for (int index : indices) {
                System.out.print(index + " ");
            }
            System.out.println();
        }
        System.out.println("=====================================\n");
    }
    // 날짜 계산을 위해 YYYY-MM-DD 형식을 LocalDate로 변경해주는 함수
    public static LocalDate stringToLocalDate(String dateStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.parse(dateStr, formatter);
    }
    // 시간 계산을 위해 HHMM 형식을 LocalTime으로 변경해주는 함수
    public static LocalTime stringToLocalTime(String time){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return LocalTime.parse(time,formatter);
    }
    public static String LocalDateToString(String date){
        String[] strr = date.split(":");
        return strr[0] + strr[1];
    }
}
