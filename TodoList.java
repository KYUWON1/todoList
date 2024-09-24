public class TodoList {
    private String title;
    private String deadline;
    private String createAt;
    private String updatedAt;
    private boolean isCheck;

    public TodoList(String title, String deadline,boolean bool) {
        this.title = title;
        this.deadline = deadline;
        this.isCheck = bool;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void isCheck(boolean check) {
        isCheck = check;
    }

    @Override
    public String toString() {
        return "todoList{" +
                ", title='" + title + '\'' +
                ", deadline='" + deadline + '\'' +
                ", createAt='" + createAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                ", isCheck=" + isCheck +
                '}';
    }
}
