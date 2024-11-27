import java.time.LocalDateTime;
import java.util.List;

public class RegularList {
    private List<TodoList> todoList;
    private CycleType cycleType;
    private String listName;
    private LocalDateTime cycleEnd;

    public RegularList(List<TodoList> todoList, CycleType cycleType,
                       String name,LocalDateTime cycleEnd) {
        this.todoList = todoList;
        this.cycleType = cycleType;
        this.listName = name;
        this.cycleEnd = cycleEnd;
    }

    public List<TodoList> getTodoList() {
        return todoList;
    }

    public void setTodoList(List<TodoList> todoList) {
        this.todoList = todoList;
    }

    public CycleType getCycleType() {
        return cycleType;
    }

    public void setCycleType(CycleType cycleType) {
        this.cycleType = cycleType;
    }

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }

    public LocalDateTime getCycleEnd() {
        return cycleEnd;
    }

    public void setCycleEnd(LocalDateTime cycleEnd) {
        this.cycleEnd = cycleEnd;
    }
}
