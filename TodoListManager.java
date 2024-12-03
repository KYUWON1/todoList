import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
                boolean canCheckAfterD = false;
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
                    System.out.println("바쁨 할일은 시작시점이 필수입니다.");
                    // 바쁨할일 o, 마감기한 o, 마감체크 o, 체크시작 o
                    startDate = getStartDate("시작날짜",deadlineDate);
                    // 반환 타입은 local 타입
                    if(startDate == null){
                        System.out.println("이전 단계로 돌아갑니다.");
                        continue outerLoop2;
                    }
                    startTime = getStartTime(startDate,
                            deadlineDate,
                            deadlineTime,"시작시간");
                    if(startTime == null){
                        System.out.println("이전 단계로 돌아갑니다.");
                        continue outerLoop2;
                    }
                    if(!canCreateBusyJob(deadlineDate,deadlineTime,startDate,
                            startTime)){
                        System.out.println("일정이 겹쳐 바쁨 할일을 생성할수없습니다.");
                        continue outerLoop2;
                    }
                    TodoList list = null;
                    if(getConfirm("마감이후 체크 가능하도록")){
                        list = TodoList.titleAndDeadlineAndStartDayCAN(
                                title, deadlineDate, deadlineTime,
                                startDate, startTime
                        );
                    }else{
                        list = TodoList.titleAndDeadlineAndStartDayCANT(
                                title, deadlineDate, deadlineTime,
                                startDate, startTime
                        );
                    }
                    list.setBusy(BusyType.BUSY_Y);
                    todoList.add(list);
                    showList(todoList);
                    return;
                }
                // 안바쁨할일 -> 시작 마감 있을때만 검증 필요
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
                        if(getConfirm("체크 가능 시작시점을")) {
                            // 바쁨할일 x, 마감기한 o, 마감체크 o, 체크시작 o
                            startDate = getStartDate("시작날짜",deadlineDate);
                            if(startDate == null){
                                System.out.println("이전 단계로 돌아갑니다.");
                                continue outerLoop2;
                            }
                            startTime = getStartTime(startDate,
                                    deadlineDate,deadlineTime,"시작시간");
                            if(startTime == null){
                                System.out.println("이전 단계로 돌아갑니다.");
                                continue outerLoop2;
                            }
                            if(!canCreateNormalJob(deadlineDate,
                                    deadlineTime,startDate,startTime)){
                                System.out.println("일반 할일을 생성할 수 없습니다.");
                                continue outerLoop2;
                            }
                            TodoList list;
                            if(getConfirm("마감이후 체크 가능하도록")){
                                list = TodoList.titleAndDeadlineAndStartDayCAN(
                                        title, deadlineDate, deadlineTime,
                                        startDate, startTime
                                );
                            }else{
                                list = TodoList.titleAndDeadlineAndStartDayCANT(
                                        title, deadlineDate, deadlineTime,
                                        startDate, startTime
                                );
                            }
                            list.setBusy(BusyType.BUSY_N);
                            todoList.add(list);
                            showList(todoList);
                            return;
                        }else{
                            TodoList list;
                            if(getConfirm("마감이후 체크 가능하도록")){
                                list = TodoList.titleAndDeadlineCAN(
                                        title, deadlineDate, deadlineTime);
                            }else{
                                list = TodoList.titleAndDeadlineCANT(
                                        title, deadlineDate, deadlineTime);
                            }
                            list.setBusy(BusyType.BUSY_N);
                            todoList.add(list);
                            showList(todoList);
                            return;
                        }
                    }else{
                        if(getConfirm("체크 가능 시작시점을")) {
                            // 바쁨할일 x, 마감기한 x, 마감체크 x, 체크시작 o
                            startDate = getStartDate("시작날짜",dateNow);
                            if(startDate == null){
                                System.out.println("이전 단계로 돌아갑니다.");
                                continue outerLoop2;
                            }
                            startTime = getStartTime(startDate,dateNow,
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
                    System.out.println("제목 : title\n마감일 : deadline\n시작시점" +
                            " : start\n바쁨설정 : busy");
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
                        return;
                    }else if(aim.equals("deadline")){
                        // 마감일 설정안되있을시 1. 마감일 설정 2. 마감이후 체크 설정
                        while(true){
                            LocalDate deadline;
                            LocalTime deadTime;
                            // 마감일 입력받기
                            if(!todo.isHasDeadline()) {
                                if (!getConfirm("설정된 마감일이 없습니다. 마감일을")) {
                                    System.out.println("이전 단계로 돌아갑니다.");
                                    continue outerLoop2;
                                }
                                deadline = getDeadlineDate("마감날짜",dateNow);
                                if(deadline == null){
                                    continue outerLoop2;
                                }
                                deadTime =
                                        getDeadlineTime(deadline,dateNow,timeNow,
                                                "마감시간");
                                if(deadTime == null){
                                    continue outerLoop2;
                                }
                            }else {
                                System.out.println("설정된 마감알이 존재합니다. 마감일을 " +
                                        "변경합니다.");
                                deadline = getDeadlineDate("마감날짜",dateNow);
                                if(deadline == null){
                                    continue outerLoop2;
                                }
                                deadTime =
                                        getDeadlineTime(deadline,dateNow,timeNow,
                                                "마감시간");
                                if(deadTime == null){
                                    continue outerLoop2;
                                }
                            }
                            // 시작일이 지정되어있다면 로직점검
                            if(todo.isCanCheckAfterCheckStartDate()){
                                LocalDateTime start =
                                        LocalDateTime.of(todo.getCheckStartDate(),todo.getCheckStartTime());
                                LocalDateTime end =
                                        LocalDateTime.of(deadline, deadTime);
                                if(end.isBefore(start)){
                                    System.out.println("마감시간이 시작시간보다 이후일수 없습니다" +
                                            ".");
                                    continue;
                                }
                                if(todo.getBusy() == BusyType.BUSY_Y){
                                    if(!canCreateBusyJob(deadline,deadTime,
                                            todo.getCheckStartDate(),
                                            todo.getCheckStartTime())){
                                        System.out.println("일정이 겹쳐 수정할 수 " +
                                                "없습니다.");
                                        continue;
                                    }
                                }else{
                                    if(!canCreateNormalJob(deadline,deadTime,
                                            todo.getCheckStartDate(),
                                            todo.getCheckStartTime())){
                                        System.out.println("일정이 겹쳐 수정할 수 없습니다.");
                                        continue;
                                    }
                                }
                            }
                            // 마감일 이후 체크 여부 입력받음
                            if(!getConfirm("마감일 이후 체크를 가능하도록")){
                                todo.setDeadline(deadline);
                                todo.setDeadTime(deadTime);
                                todo.setHasDeadline(true);
                                todo.setCanCheckAfterDeadline(false);
                                System.out.println((idx + 1) + "번의 마감일을 " +
                                        "변경하였습니다.");
                                return;
                            }else{
                                todo.setDeadline(deadline);
                                todo.setDeadTime(deadTime);
                                todo.setHasDeadline(true);
                                todo.setCanCheckAfterDeadline(true);
                                System.out.println((idx + 1) + "번의 마감일을 " +
                                        "변경하였습니다.");
                                return;
                            }
                        }
                    }
                    else if(aim.equals("start")){
                        while(true){
                            // 체크 시작일 설정안되어있을때
                            LocalDate startDate;
                            LocalTime startTime;
                            if(!todo.isCanCheckAfterCheckStartDate()){
                                System.out.println("시작날짜가 설정되어있지 않습니다.");
                                if(!getConfirm("시작날짜를")){
                                    System.out.println("이전 단계로 돌아갑니다.");
                                    continue outerLoop2;
                                }
                                startDate = getStartDate("시작날짜",dateNow);
                                if(startDate == null){
                                    continue outerLoop2;
                                }
                                startTime = getStartTime(startDate,
                                        dateNow,timeNow,
                                        "시작시간");
                                if (startTime == null) {
                                    continue outerLoop2;
                                }
                            }else{
                                System.out.println("시작날짜가 설정되어있습니다. 시작날짜를 변경합니다.");
                                startDate = getStartDate("시작날짜",dateNow);
                                if(startDate == null){
                                    continue outerLoop2;
                                }
                                startTime = getStartTime(startDate,
                                        dateNow,timeNow,
                                        "시작시간");
                                if (startTime == null) {
                                    continue outerLoop2;
                                }
                            }
                            if(todo.isHasDeadline()){
                                LocalDateTime end =
                                        LocalDateTime.of(todo.getDeadline(),
                                                todo.getDeadTime());
                                LocalDateTime start =
                                        LocalDateTime.of(startDate,startTime);
                                if(start.isAfter(end)){
                                    System.out.println("시작일이 마감일보다 이후일수 없습니다.");
                                    continue;
                                }
                                if(todo.getBusy() == BusyType.BUSY_Y){
                                    if(!canCreateBusyJob(todo.getDeadline(),
                                            todo.getDeadTime(),
                                            startDate,
                                            startTime)){
                                        System.out.println("일정이 겹쳐 수정할 수 " +
                                                "없습니다.");
                                        continue;
                                    }
                                }else{
                                    if(!canCreateNormalJob(todo.getDeadline(),
                                            todo.getDeadTime(),
                                            startDate,
                                            startTime)){
                                        System.out.println("일정이 겹쳐 수정할 수 없습니다.");
                                        continue;
                                    }
                                }
                            }
                            todo.setCheckStartDate(startDate);
                            todo.setCheckStartTime(startTime);
                            todo.setCanCheckAfterCheckStartDate(true);
                            System.out.println((idx + 1) + "번의 시작날짜를 " +
                                    "변경하였습니다.");
                            // 다시 번호 선택 loop1, 수정항목 선택 loop2
                            showList(todoList);
                            return;
                        }
                    }else if(aim.equals("busy")){
                        while(true){
                            LocalDateTime start = null;
                            LocalDateTime end = null;
                            if(todo.getBusy() == BusyType.BUSY_N){
                                LocalDate endDate = null;
                                LocalTime endTime = null;
                                LocalDate startDate = null;
                                LocalTime startTime = null;
                                // 마감과 시작일 필수
                                if(!getConfirm("일반할일을 바쁨할일로")){
                                    System.out.println("취소하셨습니다.");
                                    continue outerLoop2;
                                }
                                // 마감시간 확인
                                if(!todo.isHasDeadline()){
                                    System.out.println("바쁨할일은 마감일이 필수입니다.");
                                    endDate = getDeadlineDate(
                                            "마감일을",dateNow);
                                    if(endDate == null){
                                        continue;
                                    }
                                    endTime = getDeadlineTime(endDate,dateNow,
                                                    timeNow, "마감 시간을");
                                    if(endTime == null){
                                        continue;
                                    }
                                    end = LocalDateTime.of(endDate,endTime);
                                }else{
                                    end = LocalDateTime.of(todo.getDeadline()
                                            ,todo.getDeadTime());
                                }
                                // 시작시간 확인
                                if(!todo.isCanCheckAfterCheckStartDate()){
                                    System.out.println("바쁨할일은 시작일이 필수입니다.");
                                    startDate = getStartDate(
                                            "마감일을",endDate);
                                    if(startDate == null){
                                        continue;
                                    }
                                    startTime = getStartTime(endDate,startDate,
                                                    endTime, "마감 시간을");
                                    if(startTime == null){
                                        continue;
                                    }
                                    start = LocalDateTime.of(startDate,startTime);
                                }else{
                                    start = LocalDateTime.of(todo.getCheckStartDate()
                                            ,todo.getCheckStartTime());
                                }
                                if(!canCreateBusyJob(endDate,endTime,startDate
                                        ,startTime)){
                                    System.out.println("일정이 겹쳐 변경할 수 없습니다.");
                                    continue;
                                }
                                todo.setBusy(BusyType.BUSY_Y);
                                todo.setHasDeadline(true);
                                todo.setDeadline(endDate);
                                todo.setDeadTime(endTime);
                                todo.setCanCheckAfterCheckStartDate(true);
                                todo.setCheckStartDate(startDate);
                                todo.setCheckStartTime(startTime);
                                System.out.println((idx + 1) + "번을 바쁨할일로 " +
                                        "변경하였습니다.");
                                showList(todoList);
                                return;
                            }else{
                                // 일반 할일로 변경시엔 시작,마감이 반드시 존재
                                if(!getConfirm("바쁨할일을 일반할일로")){
                                    System.out.println("취소하셨습니다.");
                                    continue outerLoop2;
                                }
                                if(!canCreateNormalJob(todo.getDeadline(),
                                        todo.getDeadTime(),
                                        todo.getCheckStartDate(),todo.getCheckStartTime())){
                                    System.out.println("일정이 겹처 변경할 수 없습니다.");
                                }
                                todo.setBusy(BusyType.BUSY_N);
                                System.out.println((idx + 1) + "번을 일반할일로 " +
                                        "변경하였습니다.");
                                showList(todoList);
                                return;
                            }
                        }
                    }else{
                        System.out.println("잘못 입력하셨습니다.");
                    }
                }
            }
        }
    }
    // 반복 할일 수정 메소드
    public void updateRegularList(List<RegularList> regulerList) {
        System.out.println("======== 할 일 수정하기 ========");
        String input;
        outerLoop1: while(true){
            System.out.println("수정할 반복 할일 목록의 번호를 입력해주세요.");
            System.out.println("취소하고 싶으면 c를 입력해주세요.");
            input = sc.nextLine();
            if(input.equals("c")){
                System.out.println("메인으로 돌아갑니다.");
                return;
            }else if(inputManager.isNumeric(input)){
                int idx = Integer.parseInt(input) - 1;
                if(idx < 0 || idx >= regulerList.size()){
                    System.out.println("번호를 잘못 입력하셨습니다.");
                    showRegularList(regulerList);
                    continue;
                }
                RegularList regularList = regulerList.get(idx);
                List<TodoList> lists = regulerList.get(idx).getTodoList();
                outerLoop2: while(true){
                    System.out.println("수정할 항목을 입력해주세요. 이전으로 돌아가려면 c 를 입력하세요.");
                    System.out.println("제목 : title\n마감일 : deadline\n시작시점" +
                            " : start\n바쁨설정 : busy\n마감 주기 : cycle\n마감 종료일 : " +
                            "end");
                    String aim = sc.nextLine();
                    if(aim.equals("c")){
                        System.out.println("이전 단계로 돌아갑니다.");
                        continue outerLoop1;
                    }
                    else if(aim.equals("title")){
                        String title = getTitle("이름 변경");
                        if(title == null){
                            System.out.println("이전 단계로 돌아갑니다.");
                            continue outerLoop2;
                        }
                        regularList.setListName(title);
                        System.out.println((idx + 1) + "번 목록의 제목을 " + title +
                                "로 " +
                                "수정했습니다");
                        showRegularList(regulerList);
                        return;
                    }else if(aim.equals("deadline")){
                        outerLoop3:while(true){
                            LocalDate deadline;
                            LocalTime deadTime;
                            TodoList first = lists.getFirst();
                            // 마감일 입력받기
                            if(!first.isHasDeadline()) {
                                if (!getConfirm("설정된 마감일이 없습니다. 마감일을")) {
                                    System.out.println("이전 단계로 돌아갑니다.");
                                    continue outerLoop2;
                                }
                                deadline = getDeadlineDate("마감날짜",dateNow);
                                if(deadline == null){
                                    continue outerLoop2;
                                }
                                deadTime =
                                        getDeadlineTime(deadline,dateNow,timeNow,
                                                "마감시간");
                                if(deadTime == null){
                                    continue outerLoop2;
                                }
                            }else {
                                System.out.println("설정된 마감알이 존재합니다. 마감일을 " +
                                        "변경합니다.");
                                deadline = getDeadlineDate("마감날짜",dateNow);
                                if(deadline == null){
                                    continue outerLoop2;
                                }
                                deadTime =
                                        getDeadlineTime(deadline,dateNow,timeNow,
                                                "마감시간");
                                if(deadTime == null){
                                    continue outerLoop2;
                                }
                            }
                            LocalDateTime start = null;
                            LocalDateTime end = LocalDateTime.of(deadline, deadTime);
                            if(first.isCanCheckAfterCheckStartDate()){
                                start = LocalDateTime.of(first.getCheckStartDate(),first.getCheckStartTime());
                                if(end.isBefore(start)){
                                    System.out.println("마감시간이 시작시간보다 이전일수 " +
                                            "없습니다" + ".");
                                    continue;
                                }
                            }
                            List<TodoList> updateList = createRegularList(
                                    first,
                                    start,end,
                                    regularList.getCycleEnd(),
                                    regularList.getCycleType());
                            if(updateList == null){
                                continue;
                            }
                            if(!getConfirm("마감일 이후 체크를 가능하도록")){
                                updateList.forEach(todo -> todo.setCanCheckAfterDeadline(false));
                            }else{
                                updateList.forEach(todo -> todo.setCanCheckAfterDeadline(true));
                            }
                            regularList.setTodoList(updateList);
                            System.out.println("해당 반복할일의 마감일이 업데이트 되었습니다.");
                            showRegularList(regulerList);
                            return;
                        }
                    } else if(aim.equals("start")){
                        outerLoop3:while(true){
                            LocalDate startDate;
                            LocalTime startTime;
                            TodoList first = lists.getFirst();
                            // 마감일 입력받기
                            if(!first.isCanCheckAfterCheckStartDate()) {
                                if (!getConfirm("설정된 시작날짜가 없습니다. 시작날짜를")) {
                                    System.out.println("이전 단계로 돌아갑니다.");
                                    continue outerLoop2;
                                }
                                startDate = getStartDate("시작날짜",dateNow);
                                if(startDate == null){
                                    continue outerLoop2;
                                }
                                startTime =
                                        getStartTime(startDate,dateNow,timeNow,
                                                "시작시간");
                                if(startTime == null){
                                    continue outerLoop2;
                                }
                            }else {
                                System.out.println("설정된 시작날짜가 존재합니다. 시작날짜를 " +
                                        "변경합니다.");
                                startDate = getStartDate("시작날짜",dateNow);
                                if(startDate == null){
                                    continue outerLoop2;
                                }
                                startTime =
                                        getStartTime(startDate,dateNow,timeNow,
                                                "시작시간");
                                if(startTime == null){
                                    continue outerLoop2;
                                }
                            }
                            // 마감일이 지정되어있다면 로직점검 -> 바쁨 or 일반인데 설정시간 o
                            LocalDateTime start = LocalDateTime.of(startDate, startTime);
                            LocalDateTime end = null;
                            if(first.isHasDeadline()){
                                end = LocalDateTime.of(first.getDeadline(), first.getDeadTime());
                                if(end.isBefore(start)){
                                    System.out.println("마감시간이 시작시간보다 이전일수 " +
                                            "없습니다" + ".");
                                    continue;
                                }
                            }
                            List<TodoList> updateList = createRegularList(
                                    first,
                                    start,end,
                                    regularList.getCycleEnd(),
                                    regularList.getCycleType());
                            if(updateList == null){
                                continue;
                            }
                            regularList.setTodoList(updateList);
                            System.out.println("해당 반복할일의 시작일이 업데이트 되었습니다.");
                            showRegularList(regulerList);
                            return;
                        }
                    }else if(aim.equals("busy")){
                        while(true){
                            LocalDateTime start = null;
                            LocalDateTime end = null;
                            // 해당 할일의 바쁨 파악
                            TodoList todo = lists.getFirst();
                            if(todo.getBusy() == BusyType.BUSY_N){
                                LocalDate endDate = null;
                                LocalTime endTime = null;
                                LocalDate startDate = null;
                                LocalTime startTime = null;
                                // 마감과 시작일 필수
                                if(!getConfirm("일반할일을 바쁨할일로")){
                                    System.out.println("취소하셨습니다.");
                                    continue outerLoop2;
                                }
                                // 마감시간 확인
                                if(!todo.isHasDeadline()){
                                    System.out.println("바쁨할일은 마감일이 필수입니다.");
                                    endDate = getDeadlineDate(
                                            "마감일을",dateNow);
                                    if(endDate == null){
                                        continue;
                                    }
                                    endTime = getDeadlineTime(endDate,dateNow,
                                            timeNow, "마감 시간을");
                                    if(endTime == null){
                                        continue;
                                    }
                                    end = LocalDateTime.of(endDate,endTime);
                                }else{
                                    end = LocalDateTime.of(todo.getDeadline()
                                            ,todo.getDeadTime());
                                }
                                // 시작시간 확인
                                if(!todo.isCanCheckAfterCheckStartDate()){
                                    System.out.println("바쁨할일은 시작일이 필수입니다.");
                                    startDate = getStartDate(
                                            "시작일을",endDate);
                                    if(startDate == null){
                                        continue;
                                    }
                                    startTime = getStartTime(endDate,startDate,
                                            endTime, "시작 시간을");
                                    if(startTime == null){
                                        continue;
                                    }
                                    start = LocalDateTime.of(startDate,startTime);
                                }else{
                                    start = LocalDateTime.of(todo.getCheckStartDate()
                                            ,todo.getCheckStartTime());
                                }
                                if(start.isAfter(end)){
                                    System.out.println("시작일이 마감일보다 이후일수 없습니다.");
                                    continue;
                                }
                                if(regularList.getCycleType() == CycleType.WEEKLY){
                                    while(end.isBefore(regularList.getCycleEnd())){
                                        if(!checkCreateRegularBusyJob(start,end)){
                                            System.out.println("일정이 겹처 " +
                                                    "바쁨설정을 변경할 수 없습니다.");
                                            continue;
                                        }
                                        end = end.plusDays(7);
                                        start = start.plusDays(7);
                                    }
                                }else{
                                    while(end.isBefore(regularList.getCycleEnd())){
                                        if(!checkCreateRegularBusyJob(start,end)){
                                            System.out.println("일정이 겹처 " +
                                                    "바쁨설정을 변경할 수 없습니다.");
                                            continue;
                                        }
                                        end = end.plusMonths(1);
                                        start = start.plusMonths(1);
                                    }
                                }
                                lists.forEach(todoList -> todoList.setBusy(BusyType.BUSY_Y));
                                System.out.println((idx + 1) + "번 목록을 바쁨할일로 " +
                                        "변경하였습니다.");
                                showRegularList(regulerList);
                                return;
                            }else{
                                // 일반 할일로 변경시엔 시작,마감이 반드시 존재
                                if(!getConfirm("바쁨할일을 일반할일로")){
                                    System.out.println("취소하셨습니다.");
                                    continue outerLoop2;
                                }
                                LocalDateTime existStart =
                                        LocalDateTime.of(todo.getCheckStartDate(),todo.getCheckStartTime());
                                LocalDateTime existEnd =
                                        LocalDateTime.of(todo.getDeadline(),
                                                todo.getDeadTime());
                                if(regularList.getCycleType() == CycleType.WEEKLY){
                                    while(existEnd.isBefore(regularList.getCycleEnd())){
                                        if(!checkCreateRegularJob(existStart, existEnd)){
                                            System.out.println("일정이 겹처 변경할 수 없습니다.");
                                            continue;
                                        }
                                        existStart = existStart.plusDays(7);
                                        existEnd = existEnd.plusDays(7);
                                    }
                                }else{
                                    while(existEnd.isBefore(regularList.getCycleEnd())){
                                        if(!checkCreateRegularJob(existStart, existEnd)){
                                            System.out.println("일정이 겹처 변경할 수 없습니다.");
                                            continue;
                                        }
                                        existStart = existStart.plusMonths(1);
                                        existEnd = existEnd.plusMonths(1);
                                    }
                                }
                                lists.forEach(todoList -> todoList.setBusy(BusyType.BUSY_N));
                                System.out.println((idx + 1) + "번 목록을 일반할일로" +
                                        " " +
                                        "변경하였습니다.");
                                showRegularList(regulerList);
                                return;
                            }
                        }
                    }else if(aim.equals("cycle")){
                        while(true){
                            TodoList first = lists.getFirst();
                            LocalDateTime start = null;
                            LocalDateTime end = null;
                            if(first.isHasDeadline())
                                end = LocalDateTime.of(first.getDeadline(),first.getDeadTime());
                            if(first.isCanCheckAfterCheckStartDate())
                                start = LocalDateTime.of(first.getCheckStartDate(),first.getCheckStartTime());

                            if(regularList.getCycleType() == CycleType.WEEKLY){
                                System.out.println("해당 할일은 주단위 반복입니다.");
                                if(!getConfirm("매월 반복으로")){
                                    System.out.println("이전단계로 돌아갑니다.");
                                    continue outerLoop2;
                                }
                                List<TodoList> updateList = createRegularList(
                                        first,
                                        start,end,
                                        regularList.getCycleEnd(),
                                        CycleType.MONTHLY);
                                if(updateList == null){
                                    continue;
                                }
                                regularList.setCycleType(CycleType.MONTHLY);
                                System.out.println("해당 목록의 주기를 매월로 " +
                                        "변경하였습니다.");
                                showRegularList(regulerList);
                                return;
                            }
                            else{
                                System.out.println("해당 할일은 월단위 반복입니다.");
                                if(!getConfirm("매주 반복으로")){
                                    System.out.println("이전단계로 돌아갑니다.");
                                    continue outerLoop2;
                                }
                                List<TodoList> updateList = createRegularList(
                                        first,
                                        start,end,
                                        regularList.getCycleEnd(),
                                        CycleType.WEEKLY);
                                if(updateList == null){
                                    continue;
                                }
                                regularList.setCycleType(CycleType.WEEKLY);
                                System.out.println("해당 목록의 주기를 매주로 " +
                                        "변경하였습니다.");
                                showRegularList(regulerList);
                                return;
                            }
                        }
                    }else if(aim.equals("end")){
                        while(true){
                            System.out.println(regularList.getCycleEnd() +
                                    "해당 목록의 마감시간입니다.");
                            if(!getConfirm("마감시간을 변경해서")){
                                System.out.println("이전 단계로 돌아갑니다.");
                                continue outerLoop2;
                            }
                            // 목록의 가장 앞 할일의 기한 가져오기 
                            TodoList todo = lists.getFirst();
                            LocalDate firstDate = null;
                            LocalTime firstTime = null;
                            LocalDateTime start = null;
                            LocalDateTime end = null;
                            LocalDateTime gizun = null;
                            // 마감일이 있으면 마감일 기준
                            if(todo.isCanCheckAfterCheckStartDate()){
                                start = LocalDateTime.of(todo.getCheckStartDate(),todo.getCheckStartTime());
                                firstDate = start.toLocalDate();
                                firstTime = start.toLocalTime();
                                gizun = start;
                            }
                            if(todo.isHasDeadline()){
                                end = LocalDateTime.of(todo.getDeadline(),todo.getDeadTime());
                                firstDate = end.toLocalDate();
                                firstTime = end.toLocalTime();
                                gizun = end;
                            }

                            // 반복마감시간 입력받기
                            LocalDate cDate = getDeadlineDate("반복 마감일을",
                                    firstDate);
                            if(cDate == null){
                                System.out.println("이전 단계로 돌아갑니다.");
                                continue outerLoop2;
                            }
                            LocalTime cTime = getDeadlineTime(firstDate,cDate,
                                    firstTime, "반복 마감시간을");
                            if(cTime == null){
                                System.out.println("이전 단계로 돌아갑니다.");
                                continue outerLoop2;
                            }
                            LocalDateTime cycleEnd = LocalDateTime.of(cDate, cTime);
                            List<TodoList> updateList = createRegularList(
                                    todo,
                                    start,end,
                                    cycleEnd,
                                    regularList.getCycleType());
                            if(updateList == null){
                                continue;
                            }
                            regularList.setTodoList(updateList);
                            System.out.println("해당 목록의 마감일을 " + cycleEnd +
                                    "로 변경하였습니다.");
                            showRegularList(regulerList);
                            return;
                        }
                    }else{
                        System.out.println("잘못 입력하셨습니다.");
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
    // 반복 할일 일괄 삭제하기
    public void deleteRegularList(List<RegularList> regulerList) {
        System.out.println("======== 반복 할 일 삭제하기 ========");
        showRegularList(regulerList);
        while(true){
            System.out.println("삭제하고싶은 반복 할일의 목록 번호를 선택해주세요.");
            System.out.println("취소하고 싶으면 c 를 입력해주세요.");
            String input = sc.nextLine();
            if(input.equals("c")){
                System.out.println("메인으로 돌아갑니다.");
                return;
            } else if (inputManager.isNumeric(input)) {
                int idx = Integer.parseInt(input) - 1;
                if (idx < 0 || idx >= regulerList.size()) {
                    System.out.println("잘못된 번호를 입력하셨습니다.");
                    showRegularList(regulerList);
                    continue;
                }
                while (true) {
                    System.out.println("정말로 " + (idx + 1) + " 번 반복 할일을 " +
                            "삭제하시겠습니까?(Y/N)");
                    System.out.println("해당 반복할일의 모든 회차가 삭제됩니다.");
                    String isDelete = sc.nextLine();
                    if (isDelete.equals("N")) {
                        System.out.println("이전 단계로 돌아갑니다.");
                        break;
                    } else if (isDelete.equals("Y")) {
                        System.out.println((idx + 1) + "번 반복 할일을 삭제합니다.");
                        regulerList.remove(idx);
                        showRegularList(regulerList);
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
    public void addRegularList(List<RegularList> regulerList) {
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
                LocalDateTime end = null;
                LocalDateTime start = null;
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
                    end = LocalDateTime.of(deadline,deadtime);
                    System.out.println("바쁨할일은 시작일이 필수입니다.");
                    startDate = getStartDate("시작일을",deadline);
                    if(startDate == null){
                        continue outerLoop0;
                    }
                    startTime = getStartTime(deadline,startDate,
                            deadtime,"시작시간을");
                    if(startTime == null){
                        continue outerLoop0;
                    }
                    start = LocalDateTime.of(startDate,startTime);
                    if(end.isAfter(start)){
                        System.out.println("마감일은 시작일보다 이후일 수 없습니다.");
                        continue outerLoop1;
                    }
                    // 주기에 따른 제한범위 체크
                    if(cType == CycleType.MONTHLY){
                        if(!checkMonthGap(start,end)){
                            System.out.println("마감일과 체크 가능 시작일이 월단위 주기범위를" +
                                    " " +
                                    "넘을수는 없습니다.");
                            continue ;
                        }
                    }else if(cType == CycleType.WEEKLY){
                        if(!checkWeekGap(start,end)){
                            System.out.println("마감일과 체크 가능 시작일이 주단위 주기범위를" +
                                    " " +
                                    "넘을수는 없습니다.");
                            continue ;
                        }
                    }
                    // 체크 가능 여부 설정
                    if(getConfirm("마감이후 체크 가능하도록 설정")){
                        canCheckAfterD = true;
                    }
                    stdType = TYPE.BOTH;
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
                        end = LocalDateTime.of(deadline,deadtime);
                        stdType = TYPE.DEAD;
                        if(getConfirm("체크가능 시작시점을 설정")){
                            startDate = getStartDate("체크가능 시작일을",deadline);
                            if(startDate == null){
                                continue outerLoop0;
                            }
                            startTime = getStartTime(deadline,startDate,
                                    deadtime,"체크가능 시점을");
                            if(startTime == null){
                                continue outerLoop0;
                            }
                            start = LocalDateTime.of(startDate,
                                    startTime);
                            // 주기에 따른 제한범위 체크
                            if(cType == CycleType.MONTHLY){
                                if(!checkMonthGap(start,end)){
                                    System.out.println("마감일과 체크 가능 시작일이 월단위 주기범위를" +
                                            " " +
                                            "넘을수는 없습니다.");
                                    continue ;
                                }
                            }else if(cType == CycleType.WEEKLY){
                                if(!checkWeekGap(start,end)){
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
                        startDate = getStartDate("체크가능 시작일을",dateNow);
                        if(startDate == null){
                            continue outerLoop0;
                        }
                        startTime = getStartTime(dateNow,startDate,
                                timeNow,"체크가능 시점을");
                        if(startTime == null){
                            continue outerLoop0;
                        }
                        start = LocalDateTime.of(startDate,startTime);
                        canCheckAfterD = false;
                        stdType = TYPE.START;
                    }
                }
                // 반복이 끝나는 시점 입력받기
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
                    LocalDateTime cycleEnd = LocalDateTime.of(endDate,endTime);
                    // 끝나는날은 시작일 or 마감일보다 작을수 없다.
                    if(stdType == TYPE.DEAD || stdType == TYPE.BOTH){
                        if(cycleEnd.isBefore(end)){
                            System.out.println("반복 마감일이 최초 종료일보다 이전일수 없습니다.");
                            continue outerLoop3;
                        }
                    }else if(stdType == TYPE.START){
                        if(cycleEnd.isBefore(start)){
                            System.out.println("반복 마감일이 최초 시작일보다 이전일수 없습니다.");
                            continue outerLoop3;
                        }
                    }
                    TodoList newTodo = new TodoList();
                    newTodo.setTitle(title);
                    newTodo.setBusy(bType);
                    newTodo.setCanCheckAfterDeadline(canCheckAfterD);
                    List<TodoList> newTodoList = createRegularList(newTodo,
                            start, end, cycleEnd, cType);
                    if(newTodoList == null){
                        continue outerLoop3;
                    }
                    // 새로운 반복 할일용 자료구조에 추가하고 끝
                    regulerList.add(new RegularList(newTodoList,cType,title,cycleEnd));
                    for(TodoList a : newTodoList){
                        System.out.println(a);
                    }
                    System.out.println("반복 할일이 정상적으로 생성되었습니다.");
                    showRegularList(regulerList);
                    return;
                }
            }
        }
    }
    // 반복할일 만들어서 List 반환하는 함수
    private List<TodoList> createRegularList(TodoList list, LocalDateTime start,
                                             LocalDateTime end,
                                             LocalDateTime cycleEnd,
                                             CycleType type) {
        // 시작일 또는 마감일이 null 일때는?
        List<TodoList> result = new ArrayList<>();
        while(end.isBefore(cycleEnd)){
            if(end != null && start != null){
                if(list.getBusy() == BusyType.BUSY_Y){
                    if(!checkCreateRegularBusyJob(start,end)){
                        System.out.println("일정이 겹처 " +
                                "수정할 수 없습니다.");
                        return null;
                    }
                }else if(list.getBusy() == BusyType.BUSY_N){
                    if(!checkCreateRegularJob(start,end)){
                        System.out.println("일정이 겹처 " +
                                "수정할 수 없습니다.");
                        return null;
                    }
                }
            }
            LocalDate endDate = null;
            LocalTime endTime = null;
            boolean hasD = false;
            LocalDate startDate = null;
            LocalTime startTime = null;
            boolean hasS = false;

            if(end != null){
                endDate = end.toLocalDate();
                endTime = end.toLocalTime();
                hasD = true;
            }
            if(start != null){
                startDate = start.toLocalDate();
                startTime = start.toLocalTime();
                hasS = true;
            }

            // 할일 생성 및 리스트에 추가
            TodoList newTodo =
                    TodoList.createRegularList(
                            list.getTitle(), hasD,
                            endDate,
                            endTime,
                            list.getBusy(), hasS,
                            startDate,
                            startTime,
                            list.isCanCheckAfterDeadline()
                    );
            result.add(newTodo);
            if(type == CycleType.WEEKLY){
                if(start != null)
                    start = start.plusDays(7);
                if(end != null)
                    end = end.plusDays(7);
            }else{
                if(start != null)
                    start = start.plusMonths(1);
                if(end != null)
                    end = end.plusMonths(1);
            }
        }
        return result;
    }
    // 바쁨 할일 생성 가능 확인 함수
    private boolean checkCreateRegularBusyJob(LocalDateTime currStart,
                                              LocalDateTime currEnd) {
        // 기존 리스트와 비교
        for(TodoList list : todoList){
            if(list.getBusy().equals(BusyType.BUSY_Y)){
                LocalDateTime st = LocalDateTime.of(list.getDeadline(),
                        list.getDeadTime());
                LocalDateTime ed =
                        LocalDateTime.of(list.getCheckStartDate(),
                                list.getCheckStartTime());
                if(st.isAfter(currStart) && st.isBefore(currStart)){
                    return false;
                }
                if(ed.isAfter(currEnd) && ed.isBefore(currEnd)){
                    return false;
                }
            }else{
                if(list.isHasDeadline() && list.isCanCheckAfterCheckStartDate()){
                    LocalDateTime st = LocalDateTime.of(list.getDeadline(),
                            list.getDeadTime());
                    LocalDateTime ed =
                            LocalDateTime.of(list.getCheckStartDate(),
                                    list.getCheckStartTime());
                    if(st.isAfter(currStart) && ed.isBefore(currEnd)){
                        return false;
                    }
                }
            }
        }
        // 반복 리스트와 비교
        for(RegularList lists : regulerList){
            for(TodoList list : lists.getTodoList()){
                if(list.getBusy().equals(BusyType.BUSY_Y)){
                    LocalDateTime dateTime = LocalDateTime.of(list.getDeadline(),
                            list.getDeadTime());
                    if(dateTime.isAfter(currStart) && dateTime.isBefore(currStart)){
                        return false;
                    }
                    if(dateTime.isAfter(currEnd) && dateTime.isBefore(currEnd)){
                        return false;
                    }
                }else{
                    if(list.isHasDeadline() && list.isCanCheckAfterCheckStartDate()){
                        LocalDateTime st = LocalDateTime.of(list.getDeadline(),
                                list.getDeadTime());
                        LocalDateTime ed =
                                LocalDateTime.of(list.getCheckStartDate(),
                                        list.getCheckStartTime());
                        if(st.isAfter(currStart) && ed.isBefore(currEnd)){
                            return false;
                        }
                    }
                }
            }
        }
        // 모든 리스트에서 문제가 없으면 true
        return true;
    }
    // 일반 할일 생성 가능 확인 함수
    private boolean checkCreateRegularJob(LocalDateTime currStart,
                                          LocalDateTime currEnd) {
        // 기존 리스트와 비교
        for(TodoList list : todoList){
            if(list.getBusy().equals(BusyType.BUSY_Y)){
                LocalDateTime st = LocalDateTime.of(list.getCheckStartDate(),
                        list.getCheckStartTime());
                LocalDateTime ed = LocalDateTime.of(list.getDeadline(),
                        list.getDeadTime());
                if(currStart.isAfter(st) && currEnd.isBefore(ed)){
                    return false;
                }
            }
        }
        // 반복 리스트와 비교
        for(RegularList lists : regulerList){
            for(TodoList list : lists.getTodoList()){
                if(list.getBusy().equals(BusyType.BUSY_Y)){
                    LocalDateTime st = LocalDateTime.of(list.getCheckStartDate(),
                            list.getCheckStartTime());
                    LocalDateTime ed = LocalDateTime.of(list.getDeadline(),
                            list.getDeadTime());
                    if(currStart.isAfter(st) && currEnd.isBefore(ed)){
                        return false;
                    }
                }
            }
        }
        // 모든 리스트에서 문제가 없으면 true
        return true;
    }
    // 한달차이 확인
    private boolean checkMonthGap(LocalDateTime start,LocalDateTime end){
        LocalDateTime afterMonth = start.plusMonths(1);
        // 마감이 한달뒤보다 같거나, 이전일때 false 반환
        return end.isAfter(afterMonth);
    }
    // 일주일 확인
    private boolean checkWeekGap(LocalDateTime start,LocalDateTime end){
        LocalDateTime afterWeek = start.plusDays(7);
        // 마감이 일주일뒤보다 같거나, 이전일때 false 반환
        return end.isAfter(afterWeek);
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
            System.out.println("---------      " + dix + "      ---------");
            System.out.println("제목 : " + list.getListName() +", 반복주기 : " + list.getCycleType() + ", 반복 종료일 : " + list.getCycleEnd());
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
            System.out.println("---------------------------");
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
    // 시작일 입력받기
    private LocalDate getStartDate(String type,LocalDate dateNow) {
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
            if (inputManager.checkDateIsBefore(deadline,dateNow)) {
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
            if (inputManager.checkTimeIsAfter(timeNow,time,today,dateNow)) {
                return time;
            }
        }
    }
    // 시작 시간 입력받기 today가 현재 입력
    private LocalTime getStartTime(LocalDate today,LocalDate dateNow,
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
            if (inputManager.checkTimeIsBefore(timeNow,time,today,dateNow)) {
                return time;
            }
        }
    }
    // 바쁨 할일 생성시  중복시간 겹치는지 검증하는 함수
    // 기준날짜 ~ 마감일
    private boolean canCreateBusyJob(LocalDate endDate,LocalTime endTime,
                                     LocalDate startDate,LocalTime startTime
    ){
        // START와 END는 바쁨할일
        LocalDateTime start = LocalDateTime.of(endDate,endTime);
        LocalDateTime end = LocalDateTime.of(startDate,startTime);
        for(TodoList list : todoList){
            // 바쁨 할일과 비교
            if (list.getBusy().equals(BusyType.BUSY_Y)) {
                LocalDateTime todoStart = LocalDateTime.of(list.getCheckStartDate()
                        ,list.getCheckStartTime());
                LocalDateTime todoEnd = LocalDateTime.of(list.getDeadline(),
                        list.getDeadTime());
                // 기존 할일의 시작일과 마감일 사이에 있으면 안됨
                if(start.isAfter(todoStart) && start.isBefore(todoEnd))
                    return false;
                if(end.isAfter(todoStart) && end.isBefore(todoEnd))
                    return false;
            }
            // 안바쁨 할일과 비교, 마감일이나, 시작일이 없으면, 무한대임으로 검증필요 x
            else{
                if(list.isHasDeadline() && list.isCanCheckAfterCheckStartDate()){
                    LocalDateTime todoStart = LocalDateTime.of(list.getCheckStartDate()
                            ,list.getCheckStartTime());
                    LocalDateTime todoEnd = LocalDateTime.of(list.getDeadline(),
                            list.getDeadTime());
                    // 생성하려는 바쁨할일이 다 먹고있으면 안됨
                    if(start.isBefore(todoStart) && end.isAfter(todoEnd))
                        return false;
                }
            }
        }
        for(RegularList lists : regulerList){
            for(TodoList list : lists.getTodoList()){
                // 바쁨 할일과 비교
                if (list.getBusy().equals(BusyType.BUSY_Y)) {
                    LocalDateTime todoStart = LocalDateTime.of(list.getCheckStartDate()
                            ,list.getCheckStartTime());
                    LocalDateTime todoEnd = LocalDateTime.of(list.getDeadline(),
                            list.getDeadTime());
                    // 기존 할일의 시작일과 마감일 사이에 있으면 안됨
                    if(start.isAfter(todoStart) && start.isBefore(todoEnd))
                        return false;
                    if(end.isAfter(todoStart) && end.isBefore(todoEnd))
                        return false;
                }
                // 안바쁨 할일과 비교, 마감일이나, 시작일이 없으면, 무한대임으로 검증필요 x
                else{
                    if(list.isHasDeadline() && list.isCanCheckAfterCheckStartDate()){
                        LocalDateTime todoStart = LocalDateTime.of(list.getCheckStartDate()
                                ,list.getCheckStartTime());
                        LocalDateTime todoEnd = LocalDateTime.of(list.getDeadline(),
                                list.getDeadTime());
                        // 생성하려는 바쁨할일이 다 먹고있으면 안됨
                        if(start.isBefore(todoStart) && end.isAfter(todoEnd))
                            return false;
                    }
                }
            }
        }
        return true;
    }
    // 일반 할일 생성시 생성 가능한지 확인하는 함수
    private boolean canCreateNormalJob(LocalDate date, LocalTime time, LocalDate startDate, LocalTime startTime){
        // 일반 할일의 시작과 끝
        LocalDateTime end = LocalDateTime.of(date,time);
        LocalDateTime start = LocalDateTime.of(startDate,startTime);
        for(TodoList list : todoList){
            // 바쁨 할일만 고려하면됨
            if (list.getBusy().equals(BusyType.BUSY_Y)) {
                LocalDateTime todoStart = LocalDateTime.of(list.getCheckStartDate(),
                        list.getCheckStartTime());
                LocalDateTime todoEnd = LocalDateTime.of(list.getDeadline(),
                        list.getDeadTime());
                // 존재하는 바쁨 할일 사이에 구간이있으면 안됨
                if(start.isAfter(todoStart) && end.isBefore(todoEnd))
                    return false;
            }
        }
        for(RegularList lists : regulerList){
            for(TodoList list : lists.getTodoList()){
                // 바쁨 할일만 고려하면됨
                if (list.getBusy().equals(BusyType.BUSY_Y)) {
                    LocalDateTime todoStart = LocalDateTime.of(list.getCheckStartDate(),
                            list.getCheckStartTime());
                    LocalDateTime todoEnd = LocalDateTime.of(list.getDeadline(),
                            list.getDeadTime());
                    // 존재하는 바쁨 할일 사이에 구간이있으면 안됨
                    if(start.isAfter(todoStart) && end.isBefore(todoEnd))
                        return false;
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
        System.out.println(count + "회차" + todo.showRegularListDetail() + isCheck);
    }
}
