import java.time.LocalDate;
import java.time.LocalTime;

public class TodoList {
    private String title;
    private boolean isCheck;
    private DateResult deadline;
    // 요구사항 1A, 시각 설정 추가
    private LocalTime deadTime;
    // 요구사항 1C 체크 기한 제한
    private DateResult checkStartDate;
    private LocalTime checkStartTime;
    // 요구사항 1B 마감 기한 공백 가능
    private boolean hasDeadline;
    // 요구사항 1C 체크 기한 제한
    private boolean canCheckAfterDeadline;
    // 요구사항 1D
    private boolean canCheckAfterCheckStartDate;
    // 요구사항 2A
    private BusyType busy;
    
    public BusyType getBusy() {
        return busy;
    }

    public void setBusy(BusyType busy) {
        this.busy = busy;
    }

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
            DateResult date,
            LocalTime time,
            DateResult startDate,
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
            DateResult date,
            LocalTime time,
            DateResult startDate,
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
        String output = ". title: " + this.title + ", 마감일 : 미설정, 체크 가능 시작일 : " + this.checkStartDate.getFormatDate() + "일 " + this.checkStartTime + "분";
        return output;
    }
    public String showDeadline(){
        String output =
                ". title: " + this.title + ", 마감일 :" + this.deadline.getFormatDate() + "일" +
                        " " + this.deadTime + "분, 체크 가능 시작일 : 미설정";
        return output;
    }
    public String showDeadlineAndStartDate(){
        String output =
                ". title: " + this.title + ", 마감일 :" + this.deadline.getFormatDate() + "일" +
                        " " + this.deadTime + "분, 체크 가능 시작일 : " + this.checkStartDate.getFormatDate() + "일 " + this.checkStartTime + "분";
        return output;
    }
    public String showRegularListDetail(){
        String output = ". ";
        if(this.hasDeadline){
            output += "마감일 : " + this.deadline.getFormatDate() + "일 " + this.deadTime +
                    "분 ";
        }else{
            output += "마감일 : 미설정 ";
        }
        if(this.canCheckAfterCheckStartDate){
            output += "시작일 : " + this.checkStartDate.getFormatDate() +"일 " + this.checkStartTime + "분 ";
        }else{
            output += "시작일 : 미설정 ";
        }
        return output;
    }

    public static TodoList titleAndCheckStartDate(String title, DateResult startDate, LocalTime startTime) {
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

    public static TodoList titleAndDeadlineCANT(String title, DateResult deadlineDate, LocalTime deadlineTime) {
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

    public static TodoList createRegularList(
            String title,
            boolean hasDLine,
            DateResult deadline,
            LocalTime deadTime,
            BusyType bType,
            boolean hasSLine,
            DateResult startline,
            LocalTime startTime,
            boolean canCheckAfterD
            ){
        TodoList newTodo = new TodoList();
        newTodo.setTitle(title);
        newTodo.isCheck(false);
        newTodo.setHasDeadline(false);
        if(hasDLine){
            newTodo.setHasDeadline(true);
            newTodo.setDeadline(deadline);
            newTodo.setDeadTime(deadTime);
        }
        newTodo.setBusy(bType);
        newTodo.setCanCheckAfterCheckStartDate(false);
        if(hasSLine){
            newTodo.setCanCheckAfterCheckStartDate(true);
            newTodo.setCheckStartDate(startline);
            newTodo.setCheckStartTime(startTime);
        }
        newTodo.setCanCheckAfterDeadline(false);
        if(canCheckAfterD){
            newTodo.setCanCheckAfterDeadline(true);
        }
        return newTodo;
    }

    public static TodoList titleAndDeadlineCAN(String title, DateResult deadlineDate, LocalTime deadlineTime) {
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

    public DateResult getCheckStartDate() {
        return checkStartDate;
    }

    public void setCheckStartDate(DateResult checkStartDate) {
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

    public DateResult getDeadline() {
        return deadline;
    }

    public void setDeadline(DateResult deadline) {
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

    @Override
    public String toString() {
        return "TodoList{" +
                "title='" + title + '\'' +
                ", isCheck=" + isCheck +
                ", deadline=" + deadline +
                ", deadTime=" + deadTime +
                ", checkStartDate=" + checkStartDate +
                ", checkStartTime=" + checkStartTime +
                ", hasDeadline=" + hasDeadline +
                ", canCheckAfterDeadline=" + canCheckAfterDeadline +
                ", canCheckAfterCheckStartDate=" + canCheckAfterCheckStartDate +
                ", busy=" + busy +
                '}';
    }
}
