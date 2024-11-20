import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Scanner;

public class TodoListManager {
    private Scanner sc;
    private LocalDate dateNow;
    private LocalTime timeNow;
    private InputManager inputManager;
    private List<TodoList> todoList;

    public TodoListManager(Scanner sc, LocalDate dateNow,
                           LocalTime timeNow,
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
            title = getTitle("추가");
            if (title == null) {
                return;
            }
            LocalDate deadlineDate;
            LocalTime deadlineTime;
            LocalDate startDate;
            LocalTime startTime;

            outerLoop2: while(true){
                if(getConfirm("바쁨할일로")){
                    if(getConfirm("마감기한을")){
                        deadlineDate = getDeadlineDate("마감일을",dateNow);
                        if(deadlineDate == null){
                            System.out.println("이전 단계로 돌아갑니다.");
                            continue outerLoop2;
                        }
                        deadlineTime = getDeadlineTime(deadlineDate,dateNow,
                                timeNow,
                                "마감시간을");
                        if(deadlineTime == null){
                            System.out.println("이전 단계로 돌아갑니다.");
                            continue outerLoop2;
                        }
                        if(!canCreateBusyJob(deadlineDate,deadlineTime)){
                            System.out.println("바쁨 할일을 생성할수없습니다.");
                            continue outerLoop2;
                        }
                        if(getConfirm("마감일 이후 체크를 가능하도록")){
                            if(getConfirm("체크 가능 시작시점을")) {
                                // 바쁨할일 o, 마감기한 o, 마감체크 o, 체크시작 o
                                startDate = getDeadlineDate("시작날짜",deadlineDate);
                                // 반환 타입은 local 타입
                                if(startDate == null){
                                    System.out.println("이전 단계로 돌아갑니다.");
                                    continue outerLoop2;
                                }
                                startTime = getDeadlineTime(startDate,
                                        deadlineDate,
                                        deadlineTime,"시작시간");
                                if(startTime == null){
                                    System.out.println("이전 단계로 돌아갑니다.");
                                    continue outerLoop2;
                                }
                                TodoList list = TodoList.titleAndDeadlineAndStartDayCAN(
                                        title, deadlineDate, deadlineTime,
                                        startDate, startTime
                                );
                                list.setBusy(BusyType.BUSY_Y);
                                todoList.add(list);
                                showList(todoList);
                                return;
                            }else{
                                // 바쁨할일 o, 마감기한 o, 마감체크 o, 체크시작 x
                                TodoList list = TodoList.titleAndDeadlineCAN(
                                        title, deadlineDate, deadlineTime
                                );
                                list.setBusy(BusyType.BUSY_Y);
                                todoList.add(list);
                                showList(todoList);
                                return;
                            }
                        }else{
                            if(getConfirm("체크 가능 시작시점을")) {
                                // 바쁨할일 o, 마감기한 o, 마감체크 x, 체크시작 o
                                startDate = getDeadlineDate("시작날짜",deadlineDate);
                                if(startDate == null){
                                    System.out.println("이전 단계로 돌아갑니다.");
                                    continue outerLoop2;
                                }
                                startTime = getDeadlineTime(startDate,
                                        deadlineDate,deadlineTime,
                                        "시작시간");
                                if(startTime == null){
                                    System.out.println("이전 단계로 돌아갑니다.");
                                    continue outerLoop2;
                                }
                                TodoList list = TodoList.titleAndDeadlineAndStartDayCANT(
                                        title, deadlineDate, deadlineTime,
                                        startDate, startTime
                                );
                                list.setBusy(BusyType.BUSY_Y);
                                todoList.add(list);
                                showList(todoList);
                                return;
                            }else{
                                // 바쁨할일 o, 마감기한 o, 마감체크 x, 체크시작 x
                                TodoList list = TodoList.titleAndDeadlineCANT(
                                        title, deadlineDate, deadlineTime
                                );
                                list.setBusy(BusyType.BUSY_Y);
                                todoList.add(list);
                                showList(todoList);
                                return;
                            }
                        }
                    }else{
                        if(getConfirm("체크 가능 시작시점을")) {
                            // 바쁨할일 o, 마감기한 x, 마감체크 x, 체크시작 o
                            startDate = getDeadlineDate("시작날짜",dateNow);
                            if(startDate == null){
                                System.out.println("이전 단계로 돌아갑니다.");
                                continue outerLoop2;
                            }
                            startTime = getDeadlineTime(startDate,
                                    dateNow,timeNow,"시작시간");
                            if(startTime == null){
                                System.out.println("이전 단계로 돌아갑니다.");
                                continue outerLoop2;
                            }
                            TodoList list = TodoList.titleAndCheckStartDate(
                                    title, startDate, startTime
                            );
                            list.setBusy(BusyType.BUSY_Y);
                            todoList.add(list);
                            showList(todoList);
                            return;
                        }else{
                            // 바쁨할일 o, 마감기한 x, 마감체크 x, 체크시작 x
                            TodoList list = TodoList.onlyTitle(title);
                            list.setBusy(BusyType.BUSY_Y);
                            todoList.add(list);
                            showList(todoList);
                            return;
                        }
                    }
                }
                else{
                    if(getConfirm("마감기한을")){
                        deadlineDate = getDeadlineDate("마감일을",dateNow);
                        if(deadlineDate == null){
                            System.out.println("이전 단계로 돌아갑니다.");
                            continue outerLoop2;
                        }
                        deadlineTime = getDeadlineTime(deadlineDate,dateNow,
                                timeNow,
                                "마감시간을");
                        if(deadlineTime == null){
                            System.out.println("이전 단계로 돌아갑니다.");
                            continue outerLoop2;
                        }
                        if(!canCreateNormalJob(deadlineDate,deadlineTime)){
                            System.out.println("일반 할일을 생성할 수 없습니다.");
                            continue outerLoop2;
                        }
                        if(getConfirm("마감일 이후 체크를 가능하도록")){
                            if(getConfirm("체크 가능 시작시점을")) {
                                // 바쁨할일 x, 마감기한 o, 마감체크 o, 체크시작 o
                                startDate = getDeadlineDate("시작날짜",deadlineDate);
                                if(startDate == null){
                                    System.out.println("이전 단계로 돌아갑니다.");
                                    continue outerLoop2;
                                }
                                startTime = getDeadlineTime(startDate,
                                        deadlineDate,deadlineTime,"시작시간");
                                if(startTime == null){
                                    System.out.println("이전 단계로 돌아갑니다.");
                                    continue outerLoop2;
                                }
                                TodoList list = TodoList.titleAndDeadlineAndStartDayCAN(
                                        title, deadlineDate, deadlineTime,
                                        startDate, startTime
                                );
                                list.setBusy(BusyType.BUSY_N);
                                todoList.add(list);
                                showList(todoList);
                                return;
                            }else{
                                // 바쁨할일 x, 마감기한 o, 마감체크 o, 체크시작 x
                                TodoList list = TodoList.titleAndDeadlineCAN(
                                        title, deadlineDate, deadlineTime
                                );
                                list.setBusy(BusyType.BUSY_N);
                                todoList.add(list);
                                showList(todoList);
                                return;
                            }
                        }else{
                            if(getConfirm("체크 가능 시작시점을")) {
                                // 바쁨할일 x, 마감기한 o, 마감체크 x, 체크시작 o
                                startDate = getDeadlineDate("시작날짜",deadlineDate);
                                if(startDate == null){
                                    System.out.println("이전 단계로 돌아갑니다.");
                                    continue outerLoop2;
                                }
                                startTime = getDeadlineTime(startDate,
                                        deadlineDate,deadlineTime,"시작시간");
                                if(startTime == null){
                                    System.out.println("이전 단계로 돌아갑니다.");
                                    continue outerLoop2;
                                }
                                TodoList list = TodoList.titleAndDeadlineAndStartDayCANT(
                                        title, deadlineDate, deadlineTime, startDate, startTime

                                );
                                list.setBusy(BusyType.BUSY_N);
                                todoList.add(list);
                                showList(todoList);
                                return;
                            }else{
                                // 바쁨할일 x, 마감기한 o, 마감체크 x, 체크시작 x
                                TodoList list = TodoList.titleAndDeadlineCANT(
                                        title, deadlineDate, deadlineTime
                                );
                                list.setBusy(BusyType.BUSY_N);
                                todoList.add(list);
                                showList(todoList);
                                return;
                            }
                        }
                    }else{
                        if(getConfirm("체크 가능 시작시점을")) {
                            // 바쁨할일 x, 마감기한 x, 마감체크 x, 체크시작 o
                            startDate = getDeadlineDate("시작날짜",dateNow);
                            if(startDate == null){
                                System.out.println("이전 단계로 돌아갑니다.");
                                continue outerLoop2;
                            }
                            startTime = getDeadlineTime(startDate,dateNow,
                                    timeNow,
                                    "시작시간");
                            if(startTime == null){
                                System.out.println("이전 단계로 돌아갑니다.");
                                continue outerLoop2;
                            }
                            TodoList list = TodoList.titleAndCheckStartDate(
                                    title, startDate, startTime
                            );
                            list.setBusy(BusyType.BUSY_N);
                            todoList.add(list);
                            showList(todoList);
                            return;
                        }else{
                            // 바쁨할일 x, 마감기한 x, 마감체크 x, 체크시작 x
                            TodoList list = TodoList.onlyTitle(title);
                            list.setBusy(BusyType.BUSY_N);
                            todoList.add(list);
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
                        String title = getTitle("수정");
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
                            LocalDate newdeadline = getDeadlineDate("마감날짜",dateNow);
                            if(newdeadline == null){
                                continue outerLoop2;
                            }
                            LocalDate checkStartDate = todo.getCheckStartDate();
                            if(checkStartDate != null && newdeadline.isBefore(checkStartDate)){
                                if(!getConfirm("마감날짜가 체크 가능 시작일보다 이전입니다.")){
                                    continue outerLoop2;
                                }
                            }
                            LocalTime newdeadtime =
                                    getDeadlineTime(newdeadline,dateNow,timeNow,
                                            "마감시간");
                            if(newdeadtime == null){
                                continue outerLoop2;
                            }
                            LocalTime checkStartTime = todo.getCheckStartTime();
                            if(checkStartTime != null && newdeadline.equals(checkStartDate) && newdeadtime.isBefore(checkStartTime)){
                                if(!getConfirm("마감시간이 체크 가능 시간보다 이전입니다.")){
                                    continue outerLoop2;
                                }
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
                                LocalDate newdeadline = getDeadlineDate(
                                        "마감날짜",dateNow);
                                if(newdeadline == null){
                                    continue outerLoop2;
                                }
                                LocalDate checkStartDate = todo.getCheckStartDate();
                                if(checkStartDate != null && newdeadline.isBefore(checkStartDate)){
                                    if(!getConfirm("마감날짜가 체크 가능 시작일보다 이전입니다.")){
                                        continue outerLoop2;
                                    }
                                }
                                LocalTime newdeadtime =
                                        getDeadlineTime(newdeadline,dateNow,
                                                timeNow,
                                                "마감시간");
                                if(newdeadtime == null){
                                    continue outerLoop2;
                                }
                                LocalTime checkStartTime = todo.getCheckStartTime();
                                if(checkStartTime != null && newdeadline.equals(checkStartDate) && newdeadtime.isBefore(checkStartTime)){
                                    if(!getConfirm("마감시간이 체크 가능 시간보다 이전입니다.")){
                                        continue outerLoop2;
                                    }
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
                            LocalDate startDate = getDeadlineDate("시작날짜",null);
                            if(startDate == null){
                                continue outerLoop2;
                            }
                            LocalDate deadline = todo.getDeadline();
                            if(deadline != null && startDate.isAfter(deadline)){
                                if(!getConfirm("체크 가능 시작일이 마감일보다 이후입니다.")){
                                    continue outerLoop2;
                                }
                            }
                            LocalTime startTime = getDeadlineTime(startDate,
                                    deadline,null,
                                    "시작시간");
                            if (startTime == null) {
                                continue outerLoop2;
                            }
                            LocalTime deadTime = todo.getDeadTime();
                            if(deadTime != null && startDate.isEqual(deadline) && startTime.isAfter(deadTime)){
                                if(!getConfirm("체크 가능 시작시간이 마감시간보다 이후입니다.")){
                                    continue outerLoop2;
                                }
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
                                LocalDate startDate = getDeadlineDate("시작날짜",
                                        null);
                                if(startDate == null){
                                    continue outerLoop2;
                                }
                                LocalDate deadline = todo.getDeadline();
                                if(deadline != null && startDate.isAfter(deadline)){
                                    if(!getConfirm("체크 가능 시작일이 마감일보다 이후입니다.")){
                                        continue outerLoop2;
                                    }
                                }
                                LocalTime startTime =
                                        getDeadlineTime(startDate,deadline,null,
                                                "시작시간");
                                if (startTime == null) {
                                    continue outerLoop2;
                                }
                                LocalTime deadTime = todo.getDeadTime();
                                if(deadTime != null && startDate.isEqual(deadline) && startTime.isAfter(deadTime)){
                                    if(!getConfirm("체크 가능 시작시간이 마감시간보다 이후입니다.")){
                                        continue outerLoop2;
                                    }
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

                // 체크 시작일/시간 검사
                if (todo.isCanCheckAfterCheckStartDate()) {
                    LocalDate todoDate = todo.getCheckStartDate();
                    LocalTime todoTime = todo.getCheckStartTime();
                    if (dateNow.isBefore(todoDate) || (dateNow.isEqual(todoDate) && timeNow.isBefore(todoTime))) {
                        System.out.println("아직 체크 가능 기간이 아닙니다.");
                        return;
                    }
                }

                // 마감일/시간 검사
                if (todo.isHasDeadline() && !todo.isCanCheckAfterDeadline()) {
                    LocalDate todoDate = todo.getDeadline();
                    LocalTime todoTime = todo.getDeadTime();
                    if (dateNow.isAfter(todoDate) || (dateNow.isEqual(todoDate) && timeNow.isAfter(todoTime))) {
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
    // 반복되는 할일 추가하기
    public void addRegularList(List<TodoList> todoList) {
    }
    // 할 일 목록 출력 메소드
    public void showList(List<TodoList> list) {
        System.out.println("========== 할 일 목록 ==========");
        for (int i = 0; i < list.size(); i++) {
            TodoList todo = list.get(i);
            if(todo.getBusy().equals(BusyType.BUSY_Y)){
                System.out.print("[!] ");
            }else{
                System.out.print("[ ] ");
            }
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
    private String getTitle(String type) {
        while (true) {
            System.out.println("======== 할 일 "+type+"하기 ========");
            System.out.println("할일을 입력해주세요(1~20자 내외)");
            System.out.println("취소하고 싶으면 c 를 입력해주세요.");
            String title = sc.nextLine();
            if (title.equals("c")) {
                System.out.println("이전 단계로 돌아갑니다.");
                return null;
            }
            if (inputManager.checkTitleLengthAndFirstChar(title)) {
                return title;
            }
            System.out.println("잘못된 입력입니다. 다시 입력해주세요.");
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
    private LocalDate getDeadlineDate(String type,LocalDate dateNow) {
        while (true) {
            System.out.println(type + "을 입력해주세요 (YYYYMMDD).");
            System.out.println("취소하려면 c 를 입력해주세요.");
            String input = sc.nextLine();
            if (input.equals("c")) {
                return null;
            }
            LocalDate deadline =
                    inputManager.stringToLocalDate(inputManager.formatDate(input));
            if (inputManager.checkDateInput(deadline,dateNow)) {
                return deadline;
            }
        }
    }
    // 마감 시간 입력받기
    private LocalTime getDeadlineTime(LocalDate today,LocalDate dateNow,
                                      LocalTime timeNow,
                                      String type) {
        while (true) {
            System.out.println(type + "을 입력해주세요 (HHMM).");
            System.out.println("취소하려면 c 를 입력해주세요.");
            String input = sc.nextLine();
            if(input.equals("c")){
                return null;
            }
            LocalTime time =
                    inputManager.stringToLocalTime(inputManager.formatTime(input));
            if (inputManager.checkTimeInput(time,timeNow,today,dateNow)) {
                return time;
            }
        }
    }
    // 바쁨 할일 생성시  중복시간 겹치는지 검증하는 함수
    // 기준날짜 ~ 마감일
    private boolean canCreateBusyJob(LocalDate date,LocalTime time){
        for(TodoList list : todoList){
            // 바쁨 할일간에는 교차 불가
            LocalDate date1 = list.getDeadline();
            LocalTime time1 = list.getDeadTime();
            if (list.getBusy().equals(BusyType.BUSY_Y)) {
                /*
                    시작일이 없기 때문에, 오늘 날짜보다 마감일이 이전이면 겹칠일 x
                 */
                if(date1.isAfter(dateNow)){
                    return false;
                }
                if(date1.equals(dateNow) && (time1.isAfter(timeNow) || time1.equals(timeNow))){
                    return false;
                }

            }
            // 기존 할일과는 범위가있으면 생성 가능
            else{
                /*
                    생성하려는 바쁨 할일 보다 작으면 가능 (틈이 있으니까)
                 */
                if(date1.isAfter(date)){
                    return false;
                }
                if(date1.equals(date) && (time1.isAfter(time) || time1.equals(time))){
                    return false;
                }
            }
        }
        return true;
    }
    // 일반 할일 생성시 생성 가능한지 확인하는 함수
    private boolean canCreateNormalJob(LocalDate date,LocalTime time){
        for(TodoList list : todoList){
            // 바쁨 할일만 고려하면됨
            if (list.getBusy().equals(BusyType.BUSY_Y)) {
                LocalDate date1 = list.getDeadline();
                LocalTime time1 = list.getDeadTime();
                /*
                    바쁨할일보다 크거나 같으면 안됨
                 */
                if(date.isAfter(date1)){
                    return false;
                }
                if(date.equals(date1) && (time.isAfter(time1) || time.equals(time1))){
                    return false;
                }
            }
        }
        return true;
    }


}
