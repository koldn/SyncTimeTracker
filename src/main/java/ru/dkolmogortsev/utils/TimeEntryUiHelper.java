package ru.dkolmogortsev.utils;

import com.google.common.collect.Lists;
import java.util.List;
import javafx.scene.layout.ColumnConstraints;

/**
 * Created by dkolmogortsev on 2/26/17.
 */
public class TimeEntryUiHelper
{
    public static List<ColumnConstraints> getConstraints()
    {
        List<ColumnConstraints> result = Lists.newArrayList();
        Lists.newArrayList(35d, 15d, 15d, 10d, 10d, 7.5d, 7.5d).forEach(p ->
        {
            ColumnConstraints c = new ColumnConstraints();
            c.setPercentWidth(p);
            result.add(c);
        });
        return result;
    }
}
