package ru.dkolmogortsev.customui;

import javafx.scene.layout.FlowPane;

/**
 * Created by dkolmogortsev on 3/11/17.
 */
public class DailyGridContainer extends FlowPane
{
    public int getGridIndex(String date)
    {
        for (int i = 0; i < getChildren().size(); i++)
        {
            DayGridPane dayGrid = (DayGridPane)getChildren().get(i);
            if (dayGrid.getDate().equalsIgnoreCase(date))
            {
                return i;
            }
        }
        return -1;
    }
}
