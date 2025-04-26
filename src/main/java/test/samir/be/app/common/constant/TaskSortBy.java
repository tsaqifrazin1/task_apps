package test.samir.be.app.common.constant;

public enum TaskSortBy {
    DueDate("dueDate"),
    Priority("priority");

    private final String value;

    TaskSortBy(String value) {
        this.value = value;
    }
}
