package ru.dkolmogortsev.controls;

import javafx.geometry.Pos;
import javafx.scene.control.Button;

import ru.dkolmogortsev.task.Task;

/**
 * Created by dkolmogortsev on 2/26/17.
 */
public class TimeEntryButton extends Button
{
    private String taskId;

    public TimeEntryButton(Task task)
    {
        super();
        setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        getStyleClass().clear();
        setAlignment(Pos.CENTER);
        this.taskId = task.getUUID();
    }
}
