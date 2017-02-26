package ru.dkolmogortsev.controls;

import javafx.scene.control.Button;
import ru.dkolmogortsev.task.Task;

/**
 * Created by dkolmogortsev on 2/26/17.
 */
public class TimeEntryStartButton extends Button
{
    private String taskId;

    public TimeEntryStartButton(Task task)
    {
        super("Start");
        this.taskId = task.getUUID();
    }
}
