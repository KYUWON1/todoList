import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Main {
    static Scanner sc  = new Scanner(System.in);
    static BufferedReader reader;
    static BufferedWriter writer;
    static String dateNow;
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

        // reader,writer 초기화 및 기존 list 맵에 추가
        System.out.println("reader 생성");
        reader = new BufferedReader(new FileReader(file));
        System.out.println("writer 생성");
        writer = new BufferedWriter(new FileWriter(file,true));
        String line = null;
        // Txt 파일 날짜 처리
        if ((line = reader.readLine()) != null) {
            dateNow = line;
            System.out.println("사용자가 마지막으로 입력한 날짜 : " + dateNow);
        }else{
            System.out.println("설정된 날짜가 없습니다. 최초 오늘 날짜를 설정합니다.");
        }

        while(true){
            // 사용자로부터 오늘의 날짜 입력받기
            System.out.println("오늘 날짜를 입력해주세요.(YYYYMMDD): ");
            String today = sc.nextLine();
            if(isNumeric(today) && checkDateVaildation(today) && checkDateIsAfter(today)){
                dateNow = formatDate(today);
                System.out.println("오늘의 날짜 : " + dateNow);
                break;
            }
        }

        // Txt 파일 todoList 처리
        int num = 1;
        while((line = reader.readLine()) != null){
            TodoList todo = createList(line);
            todoList.add(todo);
        }
        // 검색용 Map 초기화
        initTitleIndex(todoList);
        showTitleIndex();

        // 저장된 할 일 목록을 출력
        System.out.println("todoList 프로그램을 시작합니다.\n\n\n");
        showList(todoList);
        String input = "";
        
        // 프로그램 시작
        while(true){
            System.out.println("원하는 서비스의 명령어를 입력해주세요.\na : 리스트 추가하기\nm : 리스트" +
                    " 수정하기\nd : 리스트 삭제\nc : 리스트 체크 및 해제");
            System.out.println("종료를 원하시면 exit을 입력해주세요.");
            input = sc.nextLine();
            if(input.equals("a")){
                addList(todoList);
            }else if(input.equals("m")){
                updateList(todoList);
            }else if(input.equals("d")){
                deleteList(todoList);
            }else if(input.equals("c")){
                checkList(todoList);
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
    // 할일 체크하기 메소드
    private static void checkList(List<TodoList> todoList) {
        System.out.println("======== 할 일 체크 및 해제하기 ========");
        System.out.println("체크하고 싶은 할일의 번호를 입력해주세요.");
        String input;
        while (true) {
            System.out.println("번호를 입력해주세요.");
            System.out.println("취소하고 싶으면 c를 입력해주세요");
            input = sc.nextLine();
            if (input.equals("c")) {
                System.out.println("메인으로 돌아갑니다.");
                break;
            } else if (isNumeric(input)) {
                int idx = Integer.parseInt(input) - 1;
                if (idx < 0 || idx >= todoList.size()) {
                    System.out.println("잘못된 숫자를 입력하셨습니다.");
                    showList(todoList);
                    continue;
                }
                TodoList todo = todoList.get(idx);
                boolean check = todo.isCheck();
                if (check) {
                    System.out.println((idx + 1) + "번 할일에 체크를 해제합니다.");
                } else {
                    System.out.println((idx + 1) + "번 할일에 체크합니다.");
                }
                todo.isCheck(!check);
                showList(todoList);
                return;
            } else {
                System.out.println("숫자를 입력해주세요.");
            }
        }
    }
    // 할일 추가하기 메소드
    // fix: 마감일 입력받도록 수정
    private static void addList(List<TodoList> todoList) {
        while (true) {
            System.out.println("======== 할 일 추가하기 ========");
            System.out.println("할일을 입력해주세요(1~20자 내외)");
            System.out.println("취소하고 싶으면 c 를 입력해주세요.");
            String title = sc.nextLine();
            if (title.equals("c")) {
                System.out.println("메인으로 돌아갑니다.");
                break;
            }
            if (!checkTitleLength(title)) {
                continue;
            }
            while (true) {
                System.out.println("마감일을 입력해주세요 (YYYYMMDD).");
                System.out.println("취소하려면 c 를 입력해주세요.");
                String deadline = sc.nextLine();
                if (deadline.equals("c")) {
                    System.out.println("이전 단계로 돌아갑니다.");
                    showList(todoList);
                    break;
                }
                if (isNumeric(deadline)) {
                    if (checkDateVaildation(deadline) && checkDateIsAfter(deadline)) {
                        deadline = formatDate(deadline);
                        todoList.add(createList(title + "|" + deadline + "|N"));
                        System.out.println("할일 : " + title + " 이 추가되었습니다.");
                        System.out.println("==============================\n");
                        showList(todoList);
                        return;
                    }
                }
            }
        }
        showList(todoList);
    }
    // 할일 수정 메소드 (제목, 마감일)
    // fix : 취소시 이전단계로 돌아가도록 설정, deadline 검증 수정
    public static void updateList(List<TodoList> todoList) {
        System.out.println("======== 할 일 수정하기 ========");
        String input;
        while (true) {
            System.out.println("수정할 할 일의 번호를 입력해주세요.");
            System.out.println("취소하고 싶으면 c를 입력해주세요");
            input = sc.nextLine();
            if (input.equals("c")) {
                System.out.println("메인으로 돌아갑니다.");
                return;
            } else if (isNumeric(input)) {
                int idx = Integer.parseInt(input) - 1;
                if (idx < 0 || idx >= todoList.size()) {
                    System.out.println("번호를 잘못 입력하셨습니다.");
                    showList(todoList);
                    continue;
                }
                TodoList todo = todoList.get(idx);
                while (true) {
                    System.out.println("어떤 수정을 하실건가요? title, deadline 중 하나를 입력하시고, 취소를 원하시면 c 를 입력해주세요.");
                    String aim = sc.nextLine();
                    if (aim.equals("title")) {
                        while (true) {
                            System.out.println("수정할 제목을 작성해주세요(1~20자 내외)");
                            System.out.println("취소하려면 c 을 입력해주세요.");
                            String title = sc.nextLine();
                            if (title.equals("c")) {
                                System.out.println("이전 단계로 돌아갑니다.");
                                break;
                            }
                            if (checkTitleLength(title)) {
                                todo.setTitle(title);
                                System.out.println((idx + 1) + "번 할일의 제목을 " + title + " 로 수정했습니다.");
                                showList(todoList);
                                return;
                            }
                        }
                    } else if (aim.equals("deadline")) {
                        while (true) {
                            System.out.println("수정할 마감일을 입력해주세요 (YYYYMMDD).");
                            System.out.println("취소하려면 c 를 입력해주세요.");
                            String deadline = sc.nextLine();
                            if (deadline.equals("c")) {
                                System.out.println("이전 단계로 돌아갑니다.");
                                break;
                            }
                            if (isNumeric(deadline) && checkDateVaildation(deadline) && checkDateIsAfter(deadline)) {
                                deadline = formatDate(deadline);
                                todo.setDeadline(stringToLocalDate(deadline));
                                System.out.println((idx + 1) + "번 할일의 마감일을 " + deadline + "으로 수정했습니다.");
                                showList(todoList);
                                return;
                            } else {
                                System.out.println("잘못된 입력입니다.");
                            }
                        }
                    } else if (aim.equals("c")) {
                        System.out.println("이전 단계로 돌아갑니다.");
                        showList(todoList);
                        break;
                    } else {
                        System.out.println("올바르게 입력해주세요.");
                    }
                }
            } else {
                System.out.println("숫자를 입력해주세요.");
            }
        }
    }
    // 할일 삭제 메소드
    // fix: 잘못 입력시 이전단계로 돌아가도록 설정, 번호 선택시 Y,N 밖에 없어서, 이전으로 돌아가는것은 추가하지않음
    private static void deleteList(List<TodoList> todoList) {
        System.out.println("======== 할 일 삭제하기 ========");
        showList(todoList);
        while (true) {
            System.out.println("삭제하고 싶은 할일의 번호를 입력해주세요.");
            System.out.println("취소하고싶으면 c를 입력해주세요");
            String input = sc.nextLine();
            if (input.equals("c")) {
                System.out.println("메인으로 돌아갑니다.");
                return;
            } else if (isNumeric(input)) {
                int idx = Integer.parseInt(input) - 1;
                if (idx < 0 || idx >= todoList.size()) {
                    System.out.println("잘못된 번호를 입력하셨습니다.");
                    showList(todoList);
                    continue;
                }
                TodoList todo = todoList.get(idx);
                while (true) {
                    System.out.println("정말로 " + (idx + 1) + " 번 todo를 삭제하시겠습니까?(Y/N)");
                    String isDelete = sc.nextLine();
                    if (isDelete.equals("N")) {
                        System.out.println("이전 단계로 돌아갑니다.");
                        break;
                    } else if (isDelete.equals("Y")) {
                        System.out.println((idx + 1) + "번 todo를 삭제합니다.");
                        todoList.remove(idx);
                        showList(todoList);
                        return;
                    } else {
                        System.out.println("Y 또는 N을 입력해주세요");
                    }
                }
            } else {
                System.out.println("숫자를 입력해주세요.");
            }
        }
    }
    // 종료전에 만들어진 MAP을 통해서 FILE 재설정
    private static void updateFile(List<TodoList> todoList) {
        File file = new File("./data/file.txt");
        StringBuilder fileContent = new StringBuilder();
        fileContent.append(dateNow).append(System.lineSeparator());
        for (TodoList todo : todoList) {
            String check = todo.isCheck() ? "Y" : "N";
            String line = todo.getTitle() + "|" + todo.getDeadline() + "|" + check;
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
    public static TodoList createList(String text) {
        String[] parts = text.split("\\|");
        String title = parts[0];
        String deadline = parts[1];
        boolean isCheck = "Y".equals(parts[2]);
        return new TodoList(title, stringToLocalDate(deadline), isCheck);
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

    // 리스트 보여주는 함수
    public static void showList(List<TodoList> list) {
        System.out.println("========== 할 일 목록 ==========");
        for (int i = 0; i < list.size(); i++) {
            TodoList todo = list.get(i);
            String isCheck = todo.isCheck() ? " [v]" : " [ ]";
            System.out.println((i + 1) + ". title: " + todo.getTitle() + " deadline: " + todo.getDeadline() + isCheck);
        }
        System.out.println("==============================\n");
    }
    // YYYYMMDD의 날짜 형식을 YYYY-MM-DD로 변경해주는 함수
    public static String formatDate(String date) {
        // 년, 월, 일을 추출하여 형식 변경
        String year = date.substring(0, 4);
        String month = date.substring(4, 6);
        String day = date.substring(6, 8);

        return year + "-" + month + "-" + day;
    }
    // 날짜의 유효성 검증 년,월,일의 범위 제한
    public static boolean checkDateVaildation(String date){
        if(date.length() != 8){
            System.out.println("년,월,일을 정확하게 입력해주세요.");
            return false;
        }

        int year = Integer.parseInt(date.substring(0, 4));
        int month = Integer.parseInt(date.substring(4, 6));
        int day = Integer.parseInt(date.substring(6, 8));

        if(year <= 2014 || year >= 2034){
            System.out.println("년도의 범위가 잘못되었습니다. (2014 ~ 2034 사이)");
            return false;
        }else if(month <= 0 || month >= 13){
            System.out.println("월의 범위가 잘못되었습니다. (1~12 사이)");
            return false;
        }else if(day <= 0 || day >= 32){
            System.out.println("일의 범위가 잘못되었습니다. (1~31 사이)");
            return false;
        }
        
        return true;
    }
    // 초기에 입력한 날짜보다 이전인지 확인하는 메소드
    public static boolean checkDateIsAfter(String date){
        LocalDate inputDate = stringToLocalDate(formatDate(date));
        if(dateNow == null){
            return true;
        }
        LocalDate savedDate = stringToLocalDate(dateNow);
        if(inputDate.isBefore(savedDate)){
            System.out.println("사용자가 초기에 입력한 날짜보다 이전의 날짜는 허용되지않습니다.");
            return false;
        }
        return true;
    }
    // 제목의 길이가 1~20 사이인지 확인하는 메소드
    // fix 1. 공백 입력 불가하도록 수정
    public static boolean checkTitleLength(String title){
        if(title.trim().length() < 1 || title.length() > 20){
            System.out.println("제목의 이름은 1~20자 사이입니다.");
            return false;
        }
        return true;
    }
    // 사용자가 입력한 값이 정수인지 확인하는 메소드
    public static boolean isNumeric(String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch (NumberFormatException e) {
            System.out.println("숫자만 입력해주세요.(0~9 사이)");
            return false;
        }
    }
    // 날짜 계산을 위해 YYYY-MM-DD 형식을 LocalDate로 변경해주는 함수
    public static LocalDate stringToLocalDate(String dateStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.parse(dateStr, formatter);
    }
}
