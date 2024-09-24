import java.io.*;
import java.util.*;

public class main {
    static Scanner sc  = new Scanner(System.in);
    static BufferedReader reader;
    static BufferedWriter writer;
    static String dateNow;
    static HashMap<Integer, TodoList> todoMap = new HashMap<>();
    
    public static void main(String[] args) throws IOException {
        // 폴더 생성
        File dir = new File("./data");
        if(!dir.exists()){
            dir.mkdir();
            System.out.println("Data 폴더 생성완료.");
        }

        // txt 파일 생성
        File file = new File(dir,"file.txt");
        try{
            if(file.createNewFile()){
                System.out.println("새로운 파일을 생성합니다.");
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
        int num = 1;
        while((line = reader.readLine()) != null){
            TodoList todo = createList(line);
            todoMap.put(num++,todo);
        }
        // 사용자로부터 날짜 입력받기 
        System.out.println("오늘 날짜를 입력해주세요.(yyyy-mm-dd): ");
        dateNow = sc.nextLine();
        System.out.println(dateNow);
        
        // 저장된 할 일 목록을 출력
        showList(todoMap);
        String input = "";
        
        // 프로그램 시작
        while(true){
            System.out.println("원하는 서비스를 입력해주세요.\n1.리스트 추가하기\n2.리스트 수정하기\n3" +
                    ".리스트 삭제\n4.리스트 체크 및 해제");
            System.out.println("종료를 원하시면 exit을 입력해주세요.");
            input = sc.nextLine();
            if(input.equals("1")){
                addList(todoMap);
            }else if(input.equals("2")){
                updateList(todoMap);
            }else if(input.equals("3")){
                deleteList(todoMap);   
            }else if(input.equals("4")){
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

    private static void checkList(HashMap<Integer,TodoList> todoMap) {
        System.out.println("---- 할일 체크 및 해제하기 -----");
        System.out.println("체크하고 싶은 할일의 번호를 입력해주세요.");
        String input;
        while(true){
            System.out.println("번호를 입력해주세요.");
            System.out.println("취소하고싶으면 c를 입력해주세요");
            input = sc.nextLine();
            if(input.equals("c")){
                return;
            }else if(!isNumeric(input)){
                System.out.println("아라비아 숫자를 입력해주세요.(0~9)");
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
            System.out.println("체크를 해제합니다.");
            todo.isCheck(!check);
        }else{
            System.out.println("체크합니다.");
            todo.isCheck(!check);
        }
        todoMap.put(idx,todo);
        System.out.println("----- 체크하기 종료 -----");
        showList(todoMap);
    }

    private static void addList(HashMap<Integer,TodoList> todoMap) {
        System.out.println("----- 할일 추가하기 -----");
        System.out.println("할일을 입력해주세요.");
        String title = sc.nextLine();
        System.out.println("마감일을 입력해주세요 (yyyy-mm-dd).");
        System.out.println("마감일을 정하지않아도 됩니다."); // 데이터형식검증필요
        String deadline = sc.nextLine();
        int lastNum = todoMap.size()+1;
        // 기본은 체크 해제
        todoMap.put(lastNum,createList(title+"|"+deadline+"|N"));
        System.out.println("----- 할일이 추가되었습니다 -----");
        showList(todoMap);
    }

    public static void updateList(HashMap<Integer,TodoList> todoMap){
        System.out.println("----- 할일 수정하기 -----");
        String input;
        while(true){
            System.out.println("번호를 입력해주세요.");
            System.out.println("취소하고싶으면 c를 입력해주세요");
            input = sc.nextLine();
            if(input.equals("c")){
                return;
            }else if(!isNumeric(input)){
                System.out.println("아라비아 숫자를 입력해주세요.(0~9)");
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
                System.out.println("----- 할일 제목이 수정되었습니다 -----");
                break;
            }else if(aim.equals("deadline")){
                System.out.println("수정할 마감일을 입력해주세요 (yyyy-mm-dd).");
                String deadline = sc.nextLine();
                todoList.setDeadline(deadline);
                todoMap.put(idx,todoList);
                System.out.println("----- 마감일이 수정되었습니다 -----");
                break;
            }else if(aim.equals("cancel")){
                System.out.println("수정을 취소하셨습니다.");
                return;
            }else{
                System.out.println("옳바르게 입력해주세요.");
            }
        }

        showList(todoMap);
    }

    private static void deleteList(HashMap<Integer,TodoList> todoMap) {
        System.out.println("----- 할일 삭제하기 -----");
        String input;
        while(true){
            System.out.println("삭제하고 싶은 할일의 번호를 입력해주세요.");
            System.out.println("취소하고싶으면 c를 입력해주세요");
            input = sc.nextLine();
            if(input.equals("c")){
                return;
            }else if(!isNumeric(input)){
                System.out.println("아라비아 숫자를 입력해주세요.(0~9)");
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
        System.out.println("------ 할일 삭제 완료 -----");
        showList(todoMap);
    }

    // 종료전에 만들어진 MAP을 통해서 FILE 재설정
    private static void updateFile(HashMap<Integer,TodoList> todoMap) {
        File file = new File("./data/file.txt");
        StringBuilder fileContent = new StringBuilder();
        String line;
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

    public static TodoList createList(String text){
        String[] parts = text.split("\\|");
        String title = parts[0];
        String deadline = parts[1];
        String isCheck = parts[2];
        System.out.println("리스트 불러오기 완료.");
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

    public static void showList(Map<Integer,TodoList> list){
        System.out.println("------할 일 목록-------");
        String isCheck = "";
        for (Map.Entry<Integer, TodoList> entry : list.entrySet()) {
            if(entry.getValue().isCheck()){
                isCheck = " [v]";
            }else{
                isCheck = " [ ]";
            }
            System.out.println(entry.getKey() + ". title:" + entry.getValue().getTitle()+" deadline:"+entry.getValue().getDeadline() + isCheck);
        }
        System.out.println("--------------------");
    }

    public static boolean isNumeric(String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
