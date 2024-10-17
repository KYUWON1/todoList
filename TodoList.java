import java.time.LocalDate;
import java.time.LocalTime;

public class TodoList {
    private String title;
    private boolean isCheck;
    private LocalDate deadline;
    // 요구사항 1A, 시각 설정 추가
    private LocalTime deadTime;
    // 요구사항 1C 체크 기한 제한
    private LocalDate checkStartDate;
    private LocalTime checkStartTime;
    // 요구사항 1B 마감 기한 공백 가능
    private boolean hasDeadline;
    // 요구사항 1C 체크 기한 제한
    private boolean canCheckAfterDeadline;
    // 요구사항 1D
    private boolean canCheckAfterCheckStartDate;

    public TodoList(){
    }

    public static TodoList onlyTitle(String title){
        TodoList newTodo = new TodoList();
        newTodo.setTitle(title);
        newTodo.setCheck(false);
        newTodo.setHasDeadline(false);
        newTodo.setCanCheckAfterDeadline(false);
        newTodo.setCanCheckAfterCheckStartDate(false);
        return newTodo;
    }

    public static TodoList titleAndDeadlineAndStartDayCAN(
            String title,
            LocalDate date,
            LocalTime time,
            LocalDate startDate,
            LocalTime startTime
    ){
        TodoList newTodo = new TodoList();
        newTodo.setTitle(title);
        newTodo.setDeadline(date);
        newTodo.setDeadTime(time);
        newTodo.setCheckStartDate(startDate);
        newTodo.setCheckStartTime(startTime);
        newTodo.setCheck(false);
        newTodo.setHasDeadline(true);
        newTodo.setCanCheckAfterDeadline(true);
        newTodo.setCanCheckAfterCheckStartDate(true);
        return newTodo;
    }

    public static TodoList titleAndDeadlineAndStartDayCANT(
            String title,
            LocalDate date,
            LocalTime time,
            LocalDate startDate,
            LocalTime startTime
    ){
        TodoList newTodo = new TodoList();
        newTodo.setTitle(title);
        newTodo.setDeadline(date);
        newTodo.setDeadTime(time);
        newTodo.setCheckStartDate(startDate);
        newTodo.setCheckStartTime(startTime);
        newTodo.setCheck(false);
        newTodo.setHasDeadline(true);
        newTodo.setCanCheckAfterDeadline(false);
        newTodo.setCanCheckAfterCheckStartDate(true);
        return newTodo;
    }
    public String showOnlyTitle(){
        String output = ". title: " + this.title + ", 마감일 : 미설정, 체크 가능 시작일 : " +
                "미설정";
        return output;
    }
    public String showStartDate(){
        String output = ". title: " + this.title + ", 마감일 : 미설정, 체크 가능 시작일 : " + this.checkStartDate + "일 " + this.checkStartTime + "분";
        return output;
    }
    public String showDeadline(){
        String output =
                ". title: " + this.title + ", 마감일 :" + this.deadline + "일 " + this.deadTime + "분, 체크 가능 시작일 : 미설정";
        return output;
    }
    public String showDeadlineAndStartDate(){
        String output =
                ". title: " + this.title + ", 마감일 :" + this.deadline + "일 " + this.deadTime + "분, 체크 가능 시작일 : " + this.checkStartDate + "일 " + this.checkStartTime + "분";
        return output;
    }

    public static TodoList titleAndCheckStartDate(String title, LocalDate startDate, LocalTime startTime) {
        TodoList newTodo = new TodoList();
        newTodo.setTitle(title);
        newTodo.setCheck(false);
        newTodo.setHasDeadline(false);
        newTodo.setCanCheckAfterDeadline(true);
        newTodo.setCheckStartDate(startDate);
        newTodo.setCheckStartTime(startTime);
        newTodo.setCanCheckAfterCheckStartDate(true);
        return newTodo;
    }

    public static TodoList titleAndDeadlineCANT(String title, LocalDate deadlineDate, LocalTime deadlineTime) {
        TodoList newTodo = new TodoList();
        newTodo.setTitle(title);
        newTodo.setDeadline(deadlineDate);
        newTodo.setDeadTime(deadlineTime);
        newTodo.setCheck(false);
        newTodo.setHasDeadline(true);
        newTodo.setCanCheckAfterDeadline(false);
        newTodo.setCanCheckAfterCheckStartDate(false);
        return newTodo;
    }

    public static TodoList titleAndDeadlineCAN(String title, LocalDate deadlineDate, LocalTime deadlineTime) {
        TodoList newTodo = new TodoList();
        newTodo.setTitle(title);
        newTodo.setDeadline(deadlineDate);
        newTodo.setDeadTime(deadlineTime);
        newTodo.setCheck(false);
        newTodo.setHasDeadline(true);
        newTodo.setCanCheckAfterDeadline(true);
        newTodo.setCanCheckAfterCheckStartDate(false);
        return newTodo;
    }

    public LocalTime getDeadTime() {
        return deadTime;
    }

    public void setDeadTime(LocalTime deadTime) {
        this.deadTime = deadTime;
    }

    public LocalDate getCheckStartDate() {
        return checkStartDate;
    }

    public void setCheckStartDate(LocalDate checkStartDate) {
        this.checkStartDate = checkStartDate;
    }

    public LocalTime getCheckStartTime() {
        return checkStartTime;
    }

    public void setCheckStartTime(LocalTime checkStartTime) {
        this.checkStartTime = checkStartTime;
    }

    public boolean isHasDeadline() {
        return hasDeadline;
    }

    public void setHasDeadline(boolean hasDeadline) {
        this.hasDeadline = hasDeadline;
    }

    public boolean isCanCheckAfterDeadline() {
        return canCheckAfterDeadline;
    }

    public void setCanCheckAfterDeadline(boolean canCheckAfterDeadline) {
        this.canCheckAfterDeadline = canCheckAfterDeadline;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void isCheck(boolean check) {
        isCheck = check;
    }

    public boolean isCanCheckAfterCheckStartDate() {
        return canCheckAfterCheckStartDate;
    }

    public void setCanCheckAfterCheckStartDate(boolean canCheckAfterCheckStartDate) {
        this.canCheckAfterCheckStartDate = canCheckAfterCheckStartDate;
    }
}
