package ru.dkolmogortsev.customui;

import javafx.scene.layout.GridPane;

/**
 * Created by dkolmogortsev on 3/11/17.
 */
public class DayGridPane extends GridPane
{
    private long date;

    public DayGridPane(long date)
    {
        this.date = date;
    }

    public long getDate()
    {
        return date;
    }
}
