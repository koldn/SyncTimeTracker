package ru.dkolmogortsev.customui;

import javafx.scene.layout.GridPane;

/**
 * Created by dkolmogortsev on 3/11/17.
 */
public class DayGridPane extends GridPane
{
    private String date;

    public DayGridPane(String date)
    {
        this.date = date;
    }

    public String getDate()
    {
        return date;
    }
}
