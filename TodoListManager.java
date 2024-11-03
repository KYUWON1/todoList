import com.sun.security.jgss.GSSUtil;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Scanner;

public class TodoListManager {
    private Scanner sc;
    private String dateNow;
    private String timeNow;
    private InputManager inputManager;
    private List<TodoList> todoList;

    public TodoListManager(Scanner sc, String dateNow,
                           String timeNow,
                           List<TodoList> todoList,
                           InputManager inputManager) {
        this.sc = sc;
        this.dateNow = dateNow;
        this.timeNow = timeNow;
        this.todoList = todoList;
        this.inputManager = inputManager;
    }
    // 할 일 추가 메소드
    // fix: 마감일 입력받도록 수정
    public void addList(List<TodoList> todoList) {
        String title;
        outerLoop1: while(true) {
            title = getTitle();
            if (title == null) {
                return;
            }
            LocalDate deadlineDate;
            LocalTime deadlineTime;
            LocalDate startDate;
            LocalTime startTime;

            outerLoop2: while(true){
                // 마감기한 x
                if(!getConfirm("마감기한을")){
                    // 마김가한 x, 체크 가능 시작일 x
                    outerLoop3: while(true){
                        if(!getConfirm("체크 가능 시작시점을")){
                            todoList.add(TodoList.onlyTitle(title));
                            showList(todoList);
                            return;
                        }
                        // 마감기한 x, 체크 가능 시작일 o
                        else{
                            startDate = getDeadlineDate("시작날짜");
                            if(startDate == null){
                                continue outerLoop2;
                            }
                            startTime = getDeadlineTime(startDate,"시작시간");
                            if(startTime == null){
                                continue outerLoop2;
                            }
                            todoList.add(TodoList.titleAndCheckStartDate(title,
                                    startDate,startTime));
                            showList(todoList);
                            return;
                        }
                    }

                }
                // 마감기한 o
                else{
                    deadlineDate = getDeadlineDate("마감날짜");
                    if(deadlineDate == null){
                        continue outerLoop1;
                    }
                    deadlineTime = getDeadlineTime(deadlineDate,"마감시간");
                    if(deadlineTime == null){
                        continue outerLoop1;
                    }
                    // 마감기한 o, 체크 가능 시작일 x
                    if(!getConfirm("체크 가능 시작시점을")){
                        // 마감기한 o, 체크 가능 시작일 x, 마감일 이후 체크 x
                        if(!getConfirm("마감일 이후 체크를 가능하도록")){
                            todoList.add(TodoList.titleAndDeadlineCANT(title,
                                    deadlineDate,deadlineTime));
                            showList(todoList);
                            return;
                        }
                        // 마감기한 o, 체크 가능 시작일 x, 마감일 이후 체크 o
                        else{
                            todoList.add(TodoList.titleAndDeadlineCAN(title,
                                    deadlineDate,deadlineTime));
                            showList(todoList);
                            return;
                        }
                    }
                    // 마감기한 o, 체크 가능 시작일 o
                    else{
                        startDate = getDeadlineDate("시작날짜");
                        if(startDate == null){
                            continue outerLoop2;
                        }
                        startTime = getDeadlineTime(startDate,"시작시간");
                        if(startTime == null){
                            continue outerLoop2;
                        }
                        // 마감기한 o, 체크 가능 시작일 o, 마감일 이후 체크 x
                        if(!getConfirm("마감일 이후 체크를 가능하도록")){
                            todoList.add(TodoList.titleAndDeadlineAndStartDayCANT(title,deadlineDate,deadlineTime,startDate,startTime));
                            showList(todoList);
                            return;
                        }
                        // 마감기한 o, 체크 가능 시작일 o, 마감일 이후 체크 o
                        else{
                            todoList.add(TodoList.titleAndDeadlineAndStartDayCAN(title,deadlineDate,deadlineTime,startDate,startTime));
                            showList(todoList);
                            return;
                        }
                    }
                }
            }
        }
    }
    // 할일 수정 메소드
    public void updateList(List<TodoList> todoList){
        System.out.println("======== 할 일 수정하기 ========");
        String input;
        outerLoop1: while(true){
            System.out.println("수정할 할 일의 번호를 입력해주세요.");
            System.out.println("취소하고 싶으면 c를 입력해주세요.");
            input = sc.nextLine();
            if(input.equals("c")){
                System.out.println("메인으로 돌아갑니다.");
                return;
            }else if(inputManager.isNumeric(input)){
                int idx = Integer.parseInt(input) - 1;
                if(idx < 0 || idx >= todoList.size()){
                    System.out.println("번호를 잘못 입력하셨습니다.");
                    showList(todoList);
                    continue;
                }
                TodoList todo = todoList.get(idx);
                outerLoop2: while(true){
                    System.out.println("수정할 항목을 입력해주세요. 이전으로 돌아가려면 c 를 입력하세요.");
                    System.out.println("제목 : title\n마감일 : deadline\n체크 가능 시작시점" +
                            " : check");
                    String aim = sc.nextLine();
                    if(aim.equals("c")){
                        System.out.println("이전 단계로 돌아갑니다.");
                        continue outerLoop1;
                    }
                    else if(aim.equals("title")){
                        String title = getTitle();
                        if(title == null){
                            System.out.println("이전 단계로 돌아갑니다.");
                            continue outerLoop2;
                        }
                        todo.setTitle(title);
                        System.out.println((idx + 1) + "번 할일의 제목을 " + title + "로 수정했습니다");
                        showList(todoList);
                        // 다시 번호 선택 loop1, 수정항목 선택 loop2
                        // 우선 메인으로 돌아감
                        showList(todoList);
                        return;
                    }else if(aim.equals("deadline")){
                        // 마감일 설정안되있을시 1. 마감일 설정 2. 마감이후 체크 설정
                        if(!todo.isHasDeadline()){
                            if(!getConfirm("설정된 마감일이 없습니다. 마감일을")){
                                System.out.println("이전 단계로 돌아갑니다.");
                                continue outerLoop2;
                            }
                            LocalDate newdeadline = getDeadlineDate("마감날짜");
                            if(newdeadline == null){
                                continue outerLoop2;
                            }
                            LocalTime newdeadtime =
                                    getDeadlineTime(newdeadline,"마감시간");
                            if(newdeadtime == null){
                                continue outerLoop2;
                            }
                            if(!getConfirm("마감일 이후 체크를 가능하도록")){
                                todo.setDeadline(newdeadline);
                                todo.setDeadTime(newdeadtime);
                                todo.setHasDeadline(true);
                                todo.setCanCheckAfterDeadline(false);
                            }else{
                                todo.setDeadline(newdeadline);
                                todo.setDeadTime(newdeadtime);
                                todo.setHasDeadline(true);
                                todo.setCanCheckAfterDeadline(true);
                            }
                            System.out.println((idx + 1) + "번 할일의 마감일을 수정했습니다.");
                            // 다시 번호 선택 loop1, 수정항목 선택 loop2
                            showList(todoList);
                            return;
                        }
                        // 마감일 설정되어있을때 수정 or 삭제
                        else {
                            System.out.println("마감일이 설정되어있습니다.");
                            // 삭제
                            if(!getConfirmFull("원하는 서비스를 입력해주세요.\n수정 : Y\n삭제 " +
                                    ": N\n")){
                                todo.setDeadline(null);
                                todo.setDeadTime(null);
                                todo.setHasDeadline(false);
                                todo.setCanCheckAfterDeadline(false);
                                System.out.println((idx + 1) + "번의 마감일을 " +
                                        "삭제하였습니다.");
                                // 다시 번호 선택 loop1, 수정항목 선택 loop2
                                showList(todoList);
                                return;
                            }else{
                                System.out.println("마감일을 수정합니다.");
                                LocalDate newdeadline = getDeadlineDate("마감날짜");
                                if(newdeadline == null){
                                    continue outerLoop2;
                                }
                                LocalTime newdeadtime =
                                        getDeadlineTime(newdeadline,"마감시간");
                                if(newdeadtime == null){
                                    continue outerLoop2;
                                }
                                if(!getConfirm("마감일 이후 체크를 가능하도록")){
                                    todo.setDeadline(newdeadline);
                                    todo.setDeadTime(newdeadtime);
                                    todo.setHasDeadline(true);
                                    todo.setCanCheckAfterDeadline(false);
                                }else{
                                    todo.setDeadline(newdeadline);
                                    todo.setDeadTime(newdeadtime);
                                    todo.setHasDeadline(true);
                                    todo.setCanCheckAfterDeadline(true);
                                }
                            }
                        }
                    }
                    else if(aim.equals("check")){
                        // 체크 시작일 설정안되어있을때
                        if(!todo.isCanCheckAfterCheckStartDate()){
                            System.out.println("체크 가능 시작시점이 설정되어있지 않습니다.");
                            if(!getConfirm("체크 가능 시작시점을")){
                                System.out.println("이전 단계로 돌아갑니다.");
                                continue outerLoop2;
                            }
                            LocalDate startDate = getDeadlineDate("시작날짜");
                            if(startDate == null){
                                continue outerLoop2;
                            }
                            LocalTime startTime = getDeadlineTime(startDate,
                                    "시작시간");
                            if (startTime == null) {
                                continue outerLoop2;
                            }
                            todo.setCheckStartDate(startDate);
                            todo.setCheckStartTime(startTime);
                            todo.setCanCheckAfterCheckStartDate(true);
                            System.out.println((idx + 1) + "번의 체크 가능 시작시점을 " +
                                    "추가하였습니다.");
                            // 다시 번호 선택 loop1, 수정항목 선택 loop2
                            showList(todoList);
                            return;
                        }
                        // 체크 시작일 설정되어있을때
                        else{
                            System.out.println("체크 가능 시작시점이 설정되어있습니다.");
                            // 삭제
                            if(!getConfirmFull("원하는 서비스를 입력해주세요.\n수정 : Y\n삭제 " +
                                    ": N\n")){
                                todo.setCheckStartTime(null);
                                todo.setCheckStartTime(null);
                                todo.setCanCheckAfterCheckStartDate(false);
                                System.out.println((idx + 1) + "번의 체크 가능 날짜를 " +
                                        "삭제하였습니다.");
                                // 다시 번호 선택 loop1, 수정항목 선택 loop2
                                showList(todoList);
                                return;
                            }else{
                                System.out.println("체크 가능 시작시점를 수정합니다.");
                                LocalDate startDate = getDeadlineDate("시작날짜");
                                if(startDate == null){
                                    continue outerLoop2;
                                }
                                LocalTime startTime =
                                        getDeadlineTime(startDate,"시작시간");
                                if (startTime == null) {
                                    continue outerLoop2;
                                }
                                todo.setCheckStartDate(startDate);
                                todo.setCheckStartTime(startTime);
                                todo.setCanCheckAfterCheckStartDate(true);
                                System.out.println((idx + 1) + "번의 체크 가능 " +
                                        "시작시점을 " +
                                        "추가하였습니다.");
                                // 다시 번호 선택 loop1, 수정항목 선택 loop2
                                showList(todoList);
                                return;
                            }
                        }
                    }
                }
            }
        }
    }
    // 할일 체크하기 메소드
    public void checkList(List<TodoList> todoList) {
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
            } else if (inputManager.isNumeric(input)) {
                int idx = Integer.parseInt(input) - 1;
                if (idx < 0 || idx >= todoList.size()) {
                    System.out.println("잘못된 숫자를 입력하셨습니다.");
                    showList(todoList);
                    continue;
                }
                TodoList todo = todoList.get(idx);
                LocalDate nowDate = inputManager.stringToLocalDate(dateNow);
                LocalTime nowTime = inputManager.stringToLocalTime(timeNow);

                // 체크 시작일/시간 검사
                if (todo.isCanCheckAfterCheckStartDate()) {
                    LocalDate todoDate = todo.getCheckStartDate();
                    LocalTime todoTime = todo.getCheckStartTime();
                    if (nowDate.isBefore(todoDate) || (nowDate.isEqual(todoDate) && nowTime.isBefore(todoTime))) {
                        System.out.println("아직 체크 가능 기간이 아닙니다.");
                        return;
                    }
                }

                // 마감일/시간 검사
                if (todo.isHasDeadline() && !todo.isCanCheckAfterDeadline()) {
                    LocalDate todoDate = todo.getDeadline();
                    LocalTime todoTime = todo.getDeadTime();
                    if (nowDate.isAfter(todoDate) || (nowDate.isEqual(todoDate) && nowTime.isAfter(todoTime))) {
                        System.out.println("체크 가능한 기간이 지났습니다.");
                        return;
                    }
                }

                boolean check = todo.isCheck();
                if (check) {
                    System.out.println((idx + 1) + "번 할일에 체크를 해제합니다.");
                } else {
                    System.out.println((idx + 1) + "번 할일에 체크합니다.");
                }
                todo.setCheck(!check); // 체크 상태 반전
                showList(todoList);
                return;
            } else {
                System.out.println("숫자를 입력해주세요.");
            }
        }
    }
    // 할일 삭제 메소드
    // fix: 잘못 입력시 이전단계로 돌아가도록 설정, 번호 선택시 Y,N 밖에 없어서, 이전으로 돌아가는것은 추가하지않음
    public void deleteList(List<TodoList> todoList) {
        System.out.println("======== 할 일 삭제하기 ========");
        showList(todoList);
        while (true) {
            System.out.println("삭제하고 싶은 할일의 번호를 입력해주세요.");
            System.out.println("취소하고싶으면 c를 입력해주세요");
            String input = sc.nextLine();
            if (input.equals("c")) {
                System.out.println("메인으로 돌아갑니다.");
                return;
            } else if (inputManager.isNumeric(input)) {
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
    // 할 일 목록 출력 메소드
    public void showList(List<TodoList> list) {
        System.out.println("========== 할 일 목록 ==========");
        for (int i = 0; i < list.size(); i++) {
            TodoList todo = list.get(i);
            String isCheck = todo.isCheck() ? " [v]" : " [ ]";
            if(!todo.isHasDeadline() && !todo.isCanCheckAfterCheckStartDate()){
                System.out.println((i+1) + todo.showOnlyTitle() + isCheck);
            }else if(todo.isHasDeadline() && !todo.isCanCheckAfterCheckStartDate()){
                System.out.println((i+1) + todo.showDeadline() + isCheck);
            }else if(!todo.isHasDeadline() && todo.isCanCheckAfterCheckStartDate()){
                System.out.println((i+1) + todo.showStartDate() + isCheck);
            }else if(todo.isHasDeadline() && todo.isCanCheckAfterCheckStartDate()){
                System.out.println((i+1) + todo.showDeadlineAndStartDate() + isCheck);
            }
        }
        System.out.println("==============================\n");
    }
    // 제목 입력받기
    private String getTitle() {
        while (true) {
            System.out.println("======== 할 일 추가하기 ========");
            System.out.println("할일을 입력해주세요(1~20자 내외)");
            System.out.println("취소하고 싶으면 c 를 입력해주세요.");
            String title = sc.nextLine();
            if (title.equals("c")) {
                System.out.println("이전 단계로 돌아갑니다.");
                return null;
            }
            if (inputManager.checkTitleLength(title)) {
                return title;
            }
            System.out.println("제목의 길이가 유효하지 않습니다. 다시 입력해주세요.");
        }
    }
    // 사용자 확인 받기
    private boolean getConfirm(String text) {
        while (true) {
            System.out.println(text + " 설정하시겠습니까? (Y/N)");
            String input = sc.nextLine();
            if (input.equalsIgnoreCase("Y")) {
                return true;
            } else if (input.equalsIgnoreCase("N")) {
                return false;
            } else {
                System.out.println("Y 또는 N을 정확하게 입력해주세요.");
            }
        }
    }
    // 사용자 확인 받기
    private boolean getConfirmFull(String text) {
        while (true) {
            System.out.println(text);
            String input = sc.nextLine();
            if (input.equalsIgnoreCase("Y")) {
                return true;
            } else if (input.equalsIgnoreCase("N")) {
                return false;
            } else {
                System.out.println("Y 또는 N을 정확하게 입력해주세요.");
            }
        }
    }
    // 마감일 입력받기
    private LocalDate getDeadlineDate(String type) {
        while (true) {
            System.out.println(type + "을 입력해주세요 (YYYYMMDD).");
            System.out.println("취소하려면 c 를 입력해주세요.");
            String deadline = sc.nextLine();
            if (deadline.equals("c")) {
                return null;
            }
            if (inputManager.checkDateInput(deadline,dateNow)) {
                return inputManager.stringToLocalDate(inputManager.formatDate(deadline));
            }
            System.out.println("유효하지 않은 날짜입니다. 다시 입력해주세요.");
        }
    }
    // 마감 시간 입력받기
    private LocalTime getDeadlineTime(LocalDate today,String type) {
        while (true) {
            System.out.println(type + "을 입력해주세요 (HHMM).");
            System.out.println("취소하려면 c 를 입력해주세요.");
            String time = sc.nextLine();
            if (time.equals("c")) {
                return null;
            }
            if (inputManager.checkTimeInput(time,timeNow,today,dateNow)) {
                return inputManager.stringToLocalTime(inputManager.formatTime(time));
            }
            System.out.println("유효하지 않은 시간입니다. 다시 입력해주세요.");
        }
    }
}
