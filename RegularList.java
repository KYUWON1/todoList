import java.util.List;

public class RegularList {
    private List<TodoList> todoList;
    private CycleType cycleType;

    public RegularList(List<TodoList> todoList, CycleType cycleType) {
        this.todoList = todoList;
        this.cycleType = cycleType;
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
}
