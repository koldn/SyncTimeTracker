package ru.dkolmogortsev.messages;

/**
 * Created by dkolmogortsev on 2/25/17.
 */
public class TaskCreated {
    public String getTaskName() {
        return taskName;
    }

    private String taskName;

    public TaskCreated(String taskName, String taskDescription) {
        this.taskName = taskName;
        this.taskDescription = taskDescription;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    private String taskDescription;

}
