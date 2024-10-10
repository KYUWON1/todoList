import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Main {
    static Scanner sc  = new Scanner(System.in);
    static BufferedReader reader;
    static BufferedWriter writer;
    static String dateNow;
    static HashMap<Integer, TodoList> todoMap = new HashMap<>();
    
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
        boolean dateNowExist = true;
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
            todoMap.put(num++,todo);
        }

        // 저장된 할 일 목록을 출력
        System.out.println("todoList 프로그램을 시작합니다.\n\n\n");
        showList(todoMap);
        String input = "";
        
        // 프로그램 시작
        while(true){
            System.out.println("원하는 서비스의 명령어를 입력해주세요.\na : 리스트 추가하기\nm : 리스트" +
                    " 수정하기\nd : 리스트 삭제\nc : 리스트 체크 및 해제");
            System.out.println("종료를 원하시면 exit을 입력해주세요.");
            input = sc.nextLine();
            if(input.equals("a")){
                addList(todoMap);
            }else if(input.equals("m")){
                updateList(todoMap);
            }else if(input.equals("d")){
                deleteList(todoMap);   
            }else if(input.equals("c")){
                checkList(todoMap);
            }else if(input.equals("exit")){
                break;
            }else{
                System.out.println("잘못된 입력입니다.");
            }
        }
        updateFile(todoMap);
        writer.close();
        reader.close();
    }
    // 할일 체크하기 메소드
    private static void checkList(HashMap<Integer,TodoList> todoMap) {
        System.out.println("======== 할 일 체크 및 해제하기 ========");
        System.out.println("체크하고 싶은 할일의 번호를 입력해주세요.");
        String input;
        while(true){
            System.out.println("번호를 입력해주세요.");
            System.out.println("취소하고싶으면 c를 입력해주세요");
            input = sc.nextLine();
            if(input.equals("c")){
                return;
            }else if(!isNumeric(input)){
               
            }else{
                break;
            }
        }
        int idx = Integer.parseInt(input);
        TodoList todo = todoMap.get(idx);
        if(todo == null){
            System.out.println("잘못된 숫자를 입력하셨습니다.");
            showList(todoMap);
            return;
        }
        boolean check = todo.isCheck();
        if(check){
            System.out.println(idx + "번 할일에 체크를 해제합니다.");
            todo.isCheck(!check);
        }else{
            System.out.println(idx + "번 할일에 체크합니다.");
            todo.isCheck(!check);
        }
        todoMap.put(idx,todo);
        System.out.println("==============================\n");
        showList(todoMap);
    }
    // 할일 추가하기 메소드
    private static void addList(HashMap<Integer,TodoList> todoMap) {
        while(true){
            System.out.println("======== 할 일 추가하기 ========");
            System.out.println("할일을 입력해주세요.");
            System.out.println("취소하고 싶으면 c 를 입력해주세요.");
            String title = sc.nextLine();
            if(title.equals("c")){
                break;
            }
            if(!checkTitleLength(title)){
                continue;
            }
            System.out.println("마감일을 입력해주세요 (YYYYMMDD).");
            System.out.println("마감일을 정하지않아도 됩니다."); // 데이터형식검증필요
            String deadline = sc.nextLine();
            if(deadline.equals("c")){
                break;
            }
            if(isNumeric(deadline)){
                if(checkDateVaildation(deadline) && checkDateIsAfter(deadline)){
                    deadline = formatDate(deadline);
                    int lastNum = todoMap.size()+1;
                    // 기본은 체크 해제
                    todoMap.put(lastNum,createList(title+"|"+deadline+"|N"));
                    System.out.println("할일 : " + title +" 이 추가되었습니다.");
                    System.out.println("==============================\n");
                    break;
                }
            }
        }
        showList(todoMap);
    }
    // 할일 수정 메소드 (제목, 마감일)
    public static void updateList(HashMap<Integer,TodoList> todoMap){
        System.out.println("======== 할 일 수정하기 ========");
        String input;
        while(true){
            System.out.println("번호를 입력해주세요.");
            System.out.println("취소하고싶으면 c를 입력해주세요");
            input = sc.nextLine();
            if(input.equals("c")){
                return;
            }else if(!isNumeric(input)){

            }else{
                break;
            }
        }
        int idx = (Integer.parseInt(input));
        TodoList todoList = todoMap.get(idx);
        if(todoList == null){
            System.out.println("번호를 잘못 입력하셨습니다.");
            showList(todoMap);
            return;
        }

        while(true){
            System.out.println("어떤 수정을 하실건가요?");
            System.out.println("title,deadline 중 하나를 입력하시고, 취소를 원하시면 cancel 을" +
                    " " +
                    "입력해주세요.");
            String aim = sc.nextLine();
            if(aim.equals("title")){
                System.out.println("수정할 제목을 작성해주세요.");
                String title = sc.nextLine();
                todoList.setTitle(title);
                todoMap.put(idx,todoList);
                System.out.println(idx + "번 할일의 제목을 " + title + " 로 수정했습니다.");
                System.out.println("==============================\n");
                break;
            }else if(aim.equals("deadline")){
                System.out.println("수정할 마감일을 입력해주세요 (YYYYMMDD).");
                String deadline = sc.nextLine();
                if(isNumeric(deadline)){
                    if(checkDateVaildation(deadline) || checkDateIsAfter(deadline)){
                        deadline = formatDate(deadline);
                        todoList.setDeadline(deadline);
                        todoMap.put(idx,todoList);
                        System.out.println(idx + "번 할일의 마감일을 " + deadline +
                                "으로 수정했습니다.");
                        System.out.println("==============================\n");
                        break;
                    }
                }else{
                    System.out.println("잘못된 입력입니다.");
                }
            }else if(aim.equals("cancel")){
                System.out.println("수정을 취소하셨습니다.");
                System.out.println("==============================\n");
                return;
            }else{
                System.out.println("옳바르게 입력해주세요.");
            }
        }

        showList(todoMap);
    }
    // 할일 삭제 메소드
    private static void deleteList(HashMap<Integer,TodoList> todoMap) {
        System.out.println("======== 할 일 삭제하기 ========");
        showList(todoMap);
        String input;
        while(true){
            System.out.println("삭제하고 싶은 할일의 번호를 입력해주세요.");
            System.out.println("취소하고싶으면 c를 입력해주세요");
            input = sc.nextLine();
            if(input.equals("c")){
                return;
            }else if(!isNumeric(input)){
                
            }else{
                break;
            }
        }
        int idx = Integer.parseInt(input);
        TodoList todo = todoMap.get(idx);
        if(todo == null){
            System.out.println("잘못된 번호를 입력하셨습니다.");
            showList(todoMap);
            return;
        }

        while(true){
            System.out.println("정말로 "+idx+" 번 todo를 삭제하시겠습니까?(Y/N)");
            String isDelete = sc.nextLine();
            if(isDelete.equals("N")){
                System.out.println("삭제를 취소하셨습니다. 메인으로 돌아갑니다.");
                return;
            }else if(isDelete.equals("Y")){
                System.out.println(idx+"번 todo를 삭제합니다.");
                break;
            }else{
                System.out.println("Y 또는 N을 입력해주세요");
            }
        }

        // 번호 당기는 로직
        for (int i = idx;i <= todoMap.size();i++){
            if(todoMap.get(i + 1) != null){
                todoMap.put(i,todoMap.get(i + 1));
            }
        }
        todoMap.remove(todoMap.size());  // 마지막 항목 삭제
        System.out.println("==============================\n");
        showList(todoMap);
    }
    // 종료전에 만들어진 MAP을 통해서 FILE 재설정
    private static void updateFile(HashMap<Integer,TodoList> todoMap) {
        File file = new File("./data/file.txt");
        StringBuilder fileContent = new StringBuilder();
        String line = dateNow;
        fileContent.append(line).append(System.lineSeparator());
        String check = "";
        for (Map.Entry<Integer, TodoList> entrySet : todoMap.entrySet()) {
            TodoList todo = entrySet.getValue();
            if(todo.isCheck()){
                check = "Y";
            }else{
                check = "N";
            }
            line = todo.getTitle() + "|" + todo.getDeadline() + "|" + check;
            fileContent.append(line).append(System.lineSeparator());
        }

        // 업데이트된 내용을 파일에 다시 쓰기
        try (BufferedWriter fileWriter =
                     new BufferedWriter(new FileWriter(file, false))) {
            fileWriter.write(fileContent.toString());
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("todoList File 업데이트 완료");
    }
    // txt 파일을 불러와서 List 생성
    public static TodoList createList(String text){
        String[] parts = text.split("\\|");
        String title = parts[0];
        String deadline = parts[1];
        String isCheck = parts[2];
        boolean bool;
        if(isCheck.equals("Y")){
            bool = true;
        }else if(isCheck.equals("N")){
            bool = false;
        }else{
            bool = true;
        }
        return new TodoList(title,deadline,bool);
    }
    // 리스트 보여주는 함수
    public static void showList(Map<Integer,TodoList> list){
        System.out.println("========== 할 일 목록 ==========");
        String isCheck = "";
        for (Map.Entry<Integer, TodoList> entry : list.entrySet()) {
            if(entry.getValue().isCheck()){
                isCheck = " [v]";
            }else{
                isCheck = " [ ]";
            }
            System.out.println(entry.getKey() + ". title:" + entry.getValue().getTitle()+" deadline:"+entry.getValue().getDeadline() + isCheck);
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
    public static boolean checkTitleLength(String title){
        if(title.length() < 1 || title.length() > 20){
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
