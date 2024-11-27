import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TodoListManager {
    private Scanner sc;
    private LocalDate dateNow;
    private LocalTime timeNow;
    private InputManager inputManager;
    private List<TodoList> todoList;
    private List<RegularList> regulerList;

    private enum TYPE {DEAD,START,BOTH};

    public TodoListManager(Scanner sc, LocalDate dateNow,
                           LocalTime timeNow,
                           List<TodoList> todoList,
                           List<RegularList> regulerList,
                           InputManager inputManager) {
        this.sc = sc;
        this.dateNow = dateNow;
        this.timeNow = timeNow;
        this.todoList = todoList;
        this.regulerList = regulerList;
        this.inputManager = inputManager;
    }
    // 할 일 추가 메소드
    // fix: 마감일 입력받도록 수정
    public void addList(List<TodoList> todoList) {
        String title;
        outerLoop1: while(true) {
            title = getTitle("할일 추가");
            if (title == null) {
                return;
            }
            LocalDate deadlineDate;
            LocalTime deadlineTime;
            LocalDate startDate;
            LocalTime startTime;

            outerLoop2: while(true){
                if(getConfirm("바쁨할일로")){
                    System.out.println("바쁨 할일은 마감기한이 필수입니다.");
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
                    };
                    if(!canCreateBusyJob(deadlineDate,deadlineTime)){
                        System.out.println("일정이 겹쳐 바쁨 할일을 생성할수없습니다.");
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
                        String title = getTitle("할일 수정");
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

        showList(todoList);
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
    // 반복 할일 체크하기
    public void checkRegularList(List<RegularList> regulerList) {
        System.out.println("======== 반복 할일 체크 및 해제하기 ========");

        showRegularList(regulerList);
        System.out.println("반복할일 목록 번호를 선택해주세요.");
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
                if (idx < 0 || idx >= regulerList.size()) {
                    System.out.println("잘못된 숫자를 입력하셨습니다.");
                    showList(todoList);
                    continue;
                }
                List<TodoList> list = regulerList.get(idx).getTodoList();
                showRegularListDetail(list);
                while (true) {
                    System.out.println("체크 및 해제할 회차 번호를 입력해주세요.");
                    System.out.println("취소하고 싶으면 c를 입력해주세요");
                    input = sc.nextLine();
                    if (input.equals("c")) {
                        System.out.println("이전단계로 돌아갑니다.");
                        break;
                    } else if (inputManager.isNumeric(input)) {
                        int idx2 = Integer.parseInt(input) - 1;
                        if (idx2 < 0 || idx2 >= list.size()) {
                            System.out.println("잘못된 숫자를 입력하셨습니다.");
                            showRegularListDetail(list);
                            continue;
                        }
                        TodoList todo = list.get(idx2);
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
                            System.out.println((idx + 1) + " 회차에 체크를 해제합니다.");
                        } else {
                            System.out.println((idx + 1) + " 회차에 체크합니다.");
                        }
                        todo.setCheck(!check); // 체크 상태 반전
                        showRegularList(regulerList);
                        return;
                    } else {
                        System.out.println("숫자를 입력해주세요.");
                    }
                }
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
        System.out.println("===== 반복되는 할일 추가하기 =====");
        CycleType cType = CycleType.NONE;
        // 주기 및 제목입력받기
        outerLoop0:while(true){
            System.out.println("원하시는 반복주기를 입력해주세요(WEEKLY,MONTHLY).");
            System.out.println("취소하고싶으면 c를 입력해주세요");
            String type = sc.nextLine();
            if(type.equals("WEEKLY")){
                cType = CycleType.WEEKLY;
            }else if(type.equals("MONTHLY")){
                cType = CycleType.MONTHLY;
            }else if(type.equals("c")){
                return;
            }else{
                System.out.println("잘못입력하셨습니다.");
                continue;
            }
            System.out.println(cType + " 주기를 선택하셨습니다.");
            String title = getTitle("반복되는 할일 추가");
            if (title == null) {
                continue;
            }
            // 바쁨, 마감일, 체크가능시작일, 마감일이후 체크가능입력받기
            outerLoop1:while(true){
                LocalDate deadline = null;
                LocalTime deadtime = null;
                LocalDate startDate = null;
                LocalTime startTime = null;
                boolean canCheckAfterD = false;
                BusyType bType = BusyType.BUSY_N;
                TYPE stdType = TYPE.BOTH;
                if(getConfirm("바쁨할일로 설정")){
                    System.out.println("바쁨할일은 마감일이 필수입니다.");
                    deadline = getDeadlineDate("마감일을",dateNow);
                    if(deadline == null){
                        continue outerLoop0;
                    }
                    deadtime = getDeadlineTime(deadline,dateNow,timeNow,
                            "마감시간을");
                    if(deadtime == null){
                        continue outerLoop0;
                    }
                    stdType = TYPE.DEAD;
                    if(getConfirm("체크가능 시작시점을 설정")){
                        startDate = getDeadlineDate("체크가능 시작일을",deadline);
                        if(startDate == null){
                            continue outerLoop0;
                        }
                        startTime = getDeadlineTime(deadline,startDate,
                                deadtime,"체크가능 시점을");
                        if(startTime == null){
                            continue outerLoop0;
                        }
                        // 주기에 따른 제한범위 체크 
                        if(cType == CycleType.MONTHLY){
                            int dayGap = getDayGap(deadline, startDate, deadtime,
                                    startTime);
                            if(dayGap > 30){
                                System.out.println("마감일과 체크 가능 시작일이 월단위 주기범위를" +
                                        " " +
                                        "넘을수는 없습니다.");
                                continue ;
                            }
                        }else if(cType == CycleType.WEEKLY){
                            int dayGap = getDayGap(deadline, startDate, deadtime,
                                    startTime);
                            if(dayGap > 7){
                                System.out.println("마감일과 체크 가능 시작일이 주단위 주기범위를" +
                                        " " +
                                        "넘을수는 없습니다.");
                                continue ;
                            }
                        }
                        if(getConfirm("마감이후 체크 가능하도록 설정")){
                                canCheckAfterD = true;
                        }
                        stdType = TYPE.BOTH;
                    }else{
                        if(getConfirm("마감이후 체크 가능하도록 설정")){
                            canCheckAfterD = true;
                        }
                        stdType = TYPE.DEAD;
                    }
                    bType = BusyType.BUSY_Y;
                }else{
                    if(getConfirm("마감일을 설정")){
                        deadline = getDeadlineDate("마감일을",dateNow);
                        if(deadline == null){
                            continue outerLoop0;
                        }
                        deadtime = getDeadlineTime(deadline,dateNow,timeNow,
                                "마감시간을");
                        if(deadtime == null){
                            continue outerLoop0;
                        }
                        stdType = TYPE.DEAD;
                        if(getConfirm("체크가능 시작시점을 설정")){
                            startDate = getDeadlineDate("체크가능 시작일을",deadline);
                            if(startDate == null){
                                continue outerLoop0;
                            }
                            startTime = getDeadlineTime(deadline,startDate,
                                    deadtime,"체크가능 시점을");
                            if(startTime == null){
                                continue outerLoop0;
                            }
                            // 주기에 따른 제한범위 체크
                            if(cType == CycleType.MONTHLY){
                                int dayGap = getDayGap(deadline, startDate, deadtime,
                                        startTime);
                                if(dayGap > 30){
                                    System.out.println("마감일과 체크 가능 시작일이 월단위 주기범위를" +
                                            " " +
                                            "넘을수는 없습니다.");
                                    continue ;
                                }
                            }else if(cType == CycleType.WEEKLY){
                                int dayGap = getDayGap(deadline, startDate, deadtime,
                                        startTime);
                                if(dayGap > 7){
                                    System.out.println("마감일과 체크 가능 시작일이 주단위 주기범위를" +
                                            " " +
                                            "넘을수는 없습니다.");
                                    continue ;
                                }
                            }
                            if(getConfirm("마감이후 체크 가능하도록 설정")){
                                canCheckAfterD = true;
                            }
                            stdType = TYPE.BOTH;
                        }else{
                            if(getConfirm("마감이후 체크 가능하도록 설정")){
                                canCheckAfterD = true;
                            }
                            stdType = TYPE.DEAD;
                        }
                    }else{
                        System.out.println("마감일을 설정하지않으셨습니다. ");
                        System.out.println("체크가능 시점을 입력해주세요.");
                        startDate = getDeadlineDate("체크가능 시작일을",dateNow);
                        if(startDate == null){
                            continue outerLoop0;
                        }
                        startTime = getDeadlineTime(dateNow,startDate,
                                timeNow,"체크가능 시점을");
                        if(startTime == null){
                            continue outerLoop0;
                        }
                        canCheckAfterD = false;
                        stdType = TYPE.START;
                    }
                }
                // 반복이 끝나는 시점 입력받기
                // 기준점 : 마감일 , 시작시점, 마감일 + 시작시점
                // 주기 범위는 제한 끝남
                outerLoop3:while(true){
                    LocalDate endDate = getDeadlineDate("반복 마감일",null);
                    if(endDate == null){
                        continue outerLoop1;
                    }
                    LocalTime endTime = getDeadlineTime(endDate,null,null,"반복 마감" +
                            " 시간을");
                    if(endTime == null){
                        continue outerLoop1;
                    }
                    // 끝나는날은 시작일 or 마감일보다 작을수 없다.
                    if(stdType == TYPE.DEAD || stdType == TYPE.BOTH){
                        if(!inputManager.checkDateIsAfter(endDate,deadline) ||
                                !inputManager.checkTimeIsAfter(endTime,
                                        deadtime,endDate,deadline)){
                            continue;
                        }
                    }else if(stdType == TYPE.START){
                        if(!inputManager.checkDateIsAfter(endDate,startDate) ||
                                !inputManager.checkTimeIsAfter(endTime,
                                        startTime,endDate,startDate)){
                            continue;
                        }
                    }
                    // 각 주기별 생성 가능 여부 확인
                    /*
                        1. 바쁨할일일시
                        - 반복 종료까지 기준일시 ~ 마감일 사이에 바쁨 할일이 있으면 안됨.
                        - 반복 종료까지 기준일시 ~ 마감일 안에 들어가는 일반 할일이 있으면안됨.
                        2. 안바쁨할일
                        - 반복 종료까지 안바쁨할일의 기준일시~마감일시가 바쁨할일 안에 들어가면안됨.

                        체크가능 시작일만 있는것은 마감일이 없음으로 고려사항 x
                     */
                    while(true){
                        boolean canCreate = true;
                        LocalDate currDate = null;
                        LocalTime currTime = null;
                        LocalDate currStartDate = startDate;
                        LocalTime currStartTime = startTime;
                        List<TodoList> newList = new ArrayList<>();
                        // 마감일이 지정된것만
                        if(stdType == TYPE.DEAD || stdType == TYPE.BOTH){
                            currDate = deadline;
                            currTime = deadtime;
                            if(cType == CycleType.WEEKLY){
                            // 주 단위 바쁨할일이라면
                                if(bType == BusyType.BUSY_Y){
                                    while(currDate.isBefore(endDate) ||
                                            (currDate.isEqual(endDate) && currTime.isBefore(endTime))){
                                        if(!checkCreateRegularBusyJob(currDate,
                                                currTime,
                                                currDate.with(
                                                        TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)))){
                                            canCreate = false;
                                            break;
                                        }else{
                                            TodoList newTodo;
                                            if(stdType == TYPE.BOTH){
                                                newTodo = TodoList.createRegularList(
                                                        title, true,
                                                        currDate, currTime,
                                                        BusyType.BUSY_Y, true,
                                                        currStartDate, currStartTime,
                                                        canCheckAfterD
                                                );
                                            }else{
                                                newTodo = TodoList.createRegularList(
                                                        title, true,
                                                        currDate, currTime,
                                                        BusyType.BUSY_Y, false,
                                                        null, null,
                                                        canCheckAfterD
                                                );
                                            }
                                            newList.add(newTodo);
                                        }
                                        currDate = currDate.plusWeeks(1);
                                        if(currStartDate != null)
                                            currStartDate = currStartDate.plusWeeks(1);
                                    }

                                }
                            // 주 단위 안바쁨할일 이라면
                                else{
                                    while(currDate.isBefore(endDate) ||
                                            (currDate.isEqual(endDate) && currTime.isBefore(endTime))){
                                        if(!checkCreateRegularJob(currDate,
                                                currTime,currDate.with(
                                                        TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)))){
                                            canCreate = false;
                                            break;
                                        }else{
                                            TodoList newTodo;
                                            if(stdType == TYPE.BOTH){
                                                newTodo = TodoList.createRegularList(
                                                        title, true,
                                                        currDate, currTime,
                                                        BusyType.BUSY_N, true,
                                                        currStartDate, currStartTime,
                                                        canCheckAfterD
                                                );
                                            }else{
                                                newTodo = TodoList.createRegularList(
                                                        title, true,
                                                        currDate, currTime,
                                                        BusyType.BUSY_N, false,
                                                        null, null,
                                                        canCheckAfterD
                                                );
                                            }
                                            newList.add(newTodo);
                                        }
                                        if(currStartDate != null)
                                            currStartDate = currStartDate.plusWeeks(1);
                                        currDate = currDate.plusWeeks(1);
                                    }
                                }
                            }
                            else{
                            // 월 단위 바쁨할일 이라면
                                if(bType == BusyType.BUSY_Y){
                                    while(currDate.isBefore(endDate) ||
                                            (currDate.isEqual(endDate) && currTime.isBefore(endTime))){
                                        if(!checkCreateRegularBusyJob(currDate,
                                                currTime,
                                                currDate.withDayOfMonth(1))){
                                            canCreate = false;
                                            break;
                                        }else{
                                            TodoList newTodo;
                                            if(stdType == TYPE.BOTH){
                                                newTodo = TodoList.createRegularList(
                                                        title, true,
                                                        currDate, currTime,
                                                        BusyType.BUSY_Y, true,
                                                        currStartDate, currStartTime,
                                                        canCheckAfterD
                                                );
                                            }else{
                                                newTodo = TodoList.createRegularList(
                                                        title, true,
                                                        currDate, currTime,
                                                        BusyType.BUSY_Y, false,
                                                        null, null,
                                                        canCheckAfterD
                                                );
                                            }
                                            newList.add(newTodo);
                                        }
                                        if(currStartDate != null)
                                            currStartDate = currStartDate.plusWeeks(1);
                                        currDate = currDate.plusMonths(1);
                                    }
                                }
                            // 월 단위 안바쁨할일 이라면
                                else{
                                    while(currDate.isBefore(endDate) ||
                                            (currDate.isEqual(endDate) && currTime.isBefore(endTime))){
                                        if(!checkCreateRegularJob(currDate,
                                                currTime,
                                                currDate.withDayOfMonth(1))){
                                            canCreate = false;
                                            break;
                                        }else{
                                            TodoList newTodo;
                                            if(stdType == TYPE.BOTH){
                                                newTodo = TodoList.createRegularList(
                                                        title, true,
                                                        currDate, currTime,
                                                        BusyType.BUSY_N, true,
                                                        currStartDate, currStartTime,
                                                        canCheckAfterD
                                                );
                                            }else{
                                                newTodo = TodoList.createRegularList(
                                                        title, true,
                                                        currDate, currTime,
                                                        BusyType.BUSY_N, false,
                                                        null, null,
                                                        canCheckAfterD
                                                );
                                            }
                                            newList.add(newTodo);
                                        }
                                        if(currStartDate != null)
                                            currStartDate = currStartDate.plusWeeks(1);
                                        currDate = currDate.plusMonths(1);
                                    }
                                }
                            }
                        }
                        // 마감일 선택 x, 바쁨 불가. 체크 가능 시작일만 고려
                        else{
                            currDate = startDate;
                            currTime = startTime;
                            if(cType == CycleType.MONTHLY){
                                while(currDate.isBefore(endDate) ||
                                        (currDate.isEqual(endDate) && currTime.isBefore(endTime))){
                                    TodoList newTodo;
                                    newTodo = TodoList.createRegularList(
                                            title, false,
                                            null, null,
                                            BusyType.BUSY_N, true,
                                            currStartDate, currStartTime,
                                            canCheckAfterD
                                    );

                                    newList.add(newTodo);
                                    if(currStartDate != null)
                                        currStartDate = currStartDate.plusWeeks(1);
                                    currDate = currDate.plusMonths(1);
                                }
                            }else{
                                while(currDate.isBefore(endDate) ||
                                        (currDate.isEqual(endDate) && currTime.isBefore(endTime))){
                                    TodoList newTodo;
                                    newTodo = TodoList.createRegularList(
                                            title, false,
                                            null, null,
                                            BusyType.BUSY_N, true,
                                            currStartDate, currStartTime,
                                            canCheckAfterD
                                    );

                                    newList.add(newTodo);
                                    if(currStartDate != null)
                                        currStartDate = currStartDate.plusWeeks(1);
                                    currDate = currDate.plusWeeks(1);
                                }
                            }

                        }
                        // 생성가능하면, 리스트에 모든 할일이 생성된채로 리스트에 추가되어있을것임
                        if(!canCreate){
                            System.out.println("겹치는 일정이있어 생성 불가합니다.");
                            continue outerLoop3;
                        }
                        // 새로운 반복 할일용 자료구조에 추가하고 끝
                        regulerList.add(new RegularList(newList,cType));
                        for(TodoList a : newList){
                            System.out.println(a);
                        }
                        System.out.println("반복 할일이 정상적으로 생성되었습니다.");
                        showRegularList(regulerList);
                        return;
                    }
                }
            }
        }
    }
    // 입력은 마감일
    private boolean checkCreateRegularBusyJob(LocalDate currDate,
                                          LocalTime currTime,
                                      LocalDate startDate) {
        // 해당 주의 월요일 구하기 (기준)
        LocalDateTime start = LocalDateTime.of(startDate,LocalTime.of(0,0));
        LocalDateTime end = LocalDateTime.of(currDate,currTime);
        for(TodoList list : todoList){
            if(list.getBusy().equals(BusyType.BUSY_Y)){
                LocalDateTime dateTime = LocalDateTime.of(list.getDeadline(),
                        list.getDeadTime());
                if(dateTime.isAfter(start) && dateTime.isBefore(end)){
                    return false;
                }
            }else{
                if(list.isHasDeadline()){
                    LocalDateTime dateTime = LocalDateTime.of(list.getDeadline(),
                            list.getDeadTime());
                    LocalDateTime toDay = LocalDateTime.of(dateNow,timeNow);

                    if(dateTime.isAfter(toDay)){
                        if(toDay.isAfter(start) && dateTime.isBefore(end)){
                            return false;
                        }
                    }
                }
            }
        }
        for(RegularList list : regulerList){
            CycleType cycleType = list.getCycleType();
            for(TodoList todo : list.getTodoList()){
                if(!todo.isHasDeadline()) {
                    continue;
                }
                // 바쁨 할일간에는 교차 불가
                LocalDateTime listEnd = LocalDateTime.of(todo.getDeadline(),
                        todo.getDeadTime());
                LocalDateTime listStart;
                // 시작일 설정
                if(cycleType == CycleType.WEEKLY){
                    listStart =
                            LocalDateTime.of(todo.getDeadline().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)),LocalTime.of(0,0));
                }else{
                    listStart =
                            LocalDateTime.of(todo.getDeadline().withDayOfMonth(1),
                                    LocalTime.of(0,0));
                }
                if(todo.getBusy() == BusyType.BUSY_Y){
                    if(start.isAfter(listStart) && start.isBefore(listEnd)){
                        return false;
                    }
                    if(end.isAfter(listStart) && end.isBefore(listEnd)){
                        return false;
                    }
                }else{
                    if(listStart.isAfter(start) && listEnd.isBefore(end)){
                        return false;
                    }
                }
            }
        }
        // 모든 리스트에서 문제가 없으면 true
        return true;
    }

    private boolean checkCreateRegularJob(LocalDate currDate,
                                          LocalTime currTime,LocalDate startDate) {
        LocalDateTime start = LocalDateTime.of(startDate,LocalTime.of(0,0));
        LocalDateTime end = LocalDateTime.of(currDate,currTime);
        for(TodoList list : todoList){
            if(list.getBusy().equals(BusyType.BUSY_Y)){
                LocalDateTime dateTime = LocalDateTime.of(list.getDeadline(),
                        list.getDeadTime());
                if(start.isAfter(dateTime) && end.isBefore(dateTime)){
                    return false;
                }
            }
        }
        // 모든 리스트에서 문제가 없으면 true
        for(RegularList list : regulerList){
            CycleType cycleType = list.getCycleType();
            for(TodoList todo : list.getTodoList()){
                if(!todo.isHasDeadline()) {
                    continue;
                }
                // 바쁨 할일간에는 교차 불가
                LocalDateTime listEnd = LocalDateTime.of(todo.getDeadline(),
                        todo.getDeadTime());
                LocalDateTime listStart;
                // 시작일 설정
                if(cycleType == CycleType.WEEKLY){
                    listStart =
                            LocalDateTime.of(todo.getDeadline().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)),LocalTime.of(0,0));
                }else{
                    listStart =
                            LocalDateTime.of(todo.getDeadline().withDayOfMonth(1),
                                    LocalTime.of(0,0));
                }
                if(todo.getBusy() == BusyType.BUSY_Y){
                    if(start.isAfter(listStart) && end.isBefore(listEnd)){
                        return false;
                    }
                }
            }
        }
        return true;
    }

    // 시간차이를 분단위로 계산
    private int getDayGap(LocalDate deadline, LocalDate startDate,
                        LocalTime deadtime, LocalTime startTime) {
        LocalDateTime d = LocalDateTime.of(deadline,deadtime);
        LocalDateTime s = LocalDateTime.of(startDate,startTime);
        // 아닐땐 요일 차이 반환
        return (int) (ChronoUnit.MINUTES.between(d,s) / 60 / 24);
    }

    // 할 일 목록 출력 메소드
    public void showList(List<TodoList> list) {
        System.out.println("========== 현재 시각  ==========");
        System.out.println(dateNow + "일 "+timeNow +"분");
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
    // 반복 할일 개별 출력 메소드
    public void showRegularListDetail(List<TodoList> list){
        int count = 1;
        LocalDateTime today = LocalDateTime.of(dateNow,timeNow);
        System.out.println("------------");
        for(TodoList todo : list){
            LocalDateTime dateTime;
            // 마감일이 미설정시
            if(!todo.isHasDeadline()){
                dateTime = LocalDateTime.of(todo.getCheckStartDate(),
                        todo.getCheckStartTime());
                // 체크안된것 및 미래 가까운 회차 1개만 출력
                if(!todo.isCheck()){
                    showTodo(todo,count);
                    if(dateTime.isAfter(today)){
                        break;
                    }
                }

            }
            // 마감일이 있음
            else{
                dateTime = LocalDateTime.of(todo.getDeadline(),
                        todo.getDeadTime());
                //마감일 이후 체크 가능
                if(todo.isCanCheckAfterDeadline()){
                    if(!todo.isCheck()){
                        showTodo(todo,count);
                        if(dateTime.isAfter(today)){
                            break;
                        }
                    }
                }else{
                    if(dateTime.isAfter(today)){
                        showTodo(todo,count);
                        break;
                    }
                }
            }
            count++;
        }
        System.out.println("------------");
    }
    // 반복 할일 출력 메소드
    public void showRegularList(List<RegularList> regularList){
        System.out.println("========== 반복 할일 목록 ==========");
        LocalDateTime today = LocalDateTime.of(dateNow,timeNow);
        int dix = 1;
        for(RegularList list : regularList){
            System.out.println("------ " + dix + " ------");
            int count = 1;
            for(TodoList todo : list.getTodoList()){
                LocalDateTime dateTime;

                // 마감일이 미설정시
                if(!todo.isHasDeadline()){
                    dateTime = LocalDateTime.of(todo.getCheckStartDate(),
                            todo.getCheckStartTime());
                    // 체크안된것 및 미래 가까운 회차 1개만 출력
                    if(!todo.isCheck()){
                        showTodo(todo,count);
                        if(dateTime.isAfter(today)){
                            break;
                        }
                    }

                }
                // 마감일이 있음
                else{
                    dateTime = LocalDateTime.of(todo.getDeadline(),
                            todo.getDeadTime());
                    //마감일 이후 체크 가능
                    if(todo.isCanCheckAfterDeadline()){
                        if(!todo.isCheck()){
                            showTodo(todo,count);
                            if(dateTime.isAfter(today)){
                                break;
                            }
                        }
                    }else{
                        if(dateTime.isAfter(today)){
                            showTodo(todo,count);
                            break;
                        }
                    }
                }
                count++;
            }
            System.out.println("------------");
            dix++;
        }
        System.out.println("==============================\n");
    }
    // 제목 입력받기
    private String getTitle(String type) {
        while (true) {
            System.out.println("======== "+type+"하기 ========");
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
            if(!inputManager.checkDateVaildation(input) || !inputManager.isNumeric(input))
                continue;
            LocalDate deadline =
                    inputManager.stringToLocalDate(inputManager.formatDate(input));
            if (inputManager.checkDateIsAfter(deadline,dateNow)) {
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
            if(!inputManager.isNumeric(input) || !inputManager.checkTimeValidation(input))
                continue;
            LocalTime time =
                    inputManager.stringToLocalTime(inputManager.formatTime(input));
            if (inputManager.checkTimeIsAfter(time,timeNow,today,dateNow)) {
                return time;
            }
        }
    }
    // 바쁨 할일 생성시  중복시간 겹치는지 검증하는 함수
    // 기준날짜 ~ 마감일
    private boolean canCreateBusyJob(LocalDate date,LocalTime time){
        // date는 생성되려는 바쁨 할일
        LocalDateTime create = LocalDateTime.of(date,time);
        LocalDateTime today = LocalDateTime.of(dateNow,timeNow);
        for(TodoList list : todoList){
            if(!list.isHasDeadline()) {
                continue;
            }
            // 바쁨 할일간에는 교차 불가
            LocalDateTime exist = LocalDateTime.of(list.getDeadline(),
                    list.getDeadTime());

            if (list.getBusy().equals(BusyType.BUSY_Y)) {
                // 오늘 날짜 이후의 바쁨할일이 존재해서는 안됨
                if(exist.isAfter(today)){
                    System.out.println(exist.isAfter(today));
                    return false;
                }

            }
            // 기존 할일과는 범위가있으면 생성 가능
            else{
                //  생성하려는 바쁨보다, 더 작은 구간에 있는 할일이 있으면 안됨
                if(exist.isAfter(today) && exist.isBefore(create)){
                    System.out.println("일반" + exist.isAfter(today));
                    return false;
                }
            }
        }
        for(RegularList list : regulerList){
            CycleType cycleType = list.getCycleType();
            for(TodoList todo : list.getTodoList()){
                if(!todo.isHasDeadline()) {
                    continue;
                }
                // 바쁨 할일간에는 교차 불가
                LocalDateTime end = LocalDateTime.of(todo.getDeadline(),
                        todo.getDeadTime());
                LocalDateTime start;
                // 시작일 설정
                if(cycleType == CycleType.WEEKLY){
                    start =
                            LocalDateTime.of(todo.getDeadline().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)),LocalTime.of(0,0));
                }else{
                    start =
                            LocalDateTime.of(todo.getDeadline().withDayOfMonth(1),
                                    LocalTime.of(0,0));
                }
                if(todo.getBusy() == BusyType.BUSY_Y){
                    if(today.isAfter(start) && today.isBefore(end)){
                        return false;
                    }
                    if(create.isAfter(start) && create.isBefore(end)){
                        return false;
                    }
                }else{
                    if(today.isBefore(start) && create.isAfter(end)){
                        return false;
                    }
                }
            }
        }
        return true;
    }
    // 일반 할일 생성시 생성 가능한지 확인하는 함수
    private boolean canCreateNormalJob(LocalDate date,LocalTime time){
        LocalDateTime create = LocalDateTime.of(date,time);
        LocalDateTime today = LocalDateTime.of(dateNow,timeNow);
        for(TodoList list : todoList){
            // 바쁨 할일만 고려하면됨
            if (list.getBusy().equals(BusyType.BUSY_Y)) {
                LocalDateTime end = LocalDateTime.of(list.getDeadline(),
                        list.getDeadTime());
                //  존재하는 바쁨할일보다 구간이 커야함 그래야 할 시간이 생김.
                if(create.isBefore(end)){
                    return false;
                }
            }
        }
        for(RegularList list : regulerList){
            CycleType cycleType = list.getCycleType();
            for(TodoList todo : list.getTodoList()){
                if(!todo.isHasDeadline()) {
                    continue;
                }
                // 바쁨 할일간에는 교차 불가
                LocalDateTime end = LocalDateTime.of(todo.getDeadline(),
                        todo.getDeadTime());
                LocalDateTime start;
                // 시작일 설정
                if(cycleType == CycleType.WEEKLY){
                    start =
                            LocalDateTime.of(todo.getDeadline().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)),LocalTime.of(0,0));
                }else{
                    start =
                            LocalDateTime.of(todo.getDeadline().withDayOfMonth(1),
                                    LocalTime.of(0,0));
                }
                if(todo.getBusy() == BusyType.BUSY_Y){
                    if(today.isAfter(start) && create.isBefore(end)){
                        System.out.println(today);
                        System.out.println(start);
                        System.out.println(create);
                        System.out.println(end);
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private void showTodo(TodoList todo,int count){
        if(todo.getBusy().equals(BusyType.BUSY_Y)){
            System.out.print("[!] ");
        }else{
            System.out.print("[ ] ");
        }
        String isCheck = todo.isCheck() ? " [v]" : " [ ]";
        if(!todo.isHasDeadline() && !todo.isCanCheckAfterCheckStartDate()){
            System.out.println(count +"회차" + todo.showOnlyTitle() + isCheck);
        }else if(todo.isHasDeadline() && !todo.isCanCheckAfterCheckStartDate()){
            System.out.println(count +"회차" + todo.showDeadline() + isCheck);
        }else if(!todo.isHasDeadline() && todo.isCanCheckAfterCheckStartDate()){
            System.out.println(count +"회차" + todo.showStartDate() + isCheck);
        }else if(todo.isHasDeadline() && todo.isCanCheckAfterCheckStartDate()){
            System.out.println(count +"회차" + todo.showDeadlineAndStartDate() + isCheck);
        }
    }
}
