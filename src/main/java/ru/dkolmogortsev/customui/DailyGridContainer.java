package ru.dkolmogortsev.customui;

import javafx.scene.layout.FlowPane;

/**
 * Created by dkolmogortsev on 3/11/17.
 */
public class DailyGridContainer extends FlowPane
{
    public DailyGridContainer()
    {
        setVgap(10);
    }

    public int getGridIndex(long date)
    {
        for (int i = 0; i < getChildren().size(); i++)
        {
            DayGridPane dayGrid = (DayGridPane)getChildren().get(i);
            if (dayGrid.getDate() == date)
            {
                return i;
            }
        }
        return -1;
    }

}
