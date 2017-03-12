package ru.dkolmogortsev.utils;

import com.google.common.collect.Lists;
import java.text.SimpleDateFormat;
import java.util.Date;
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
        //Task description, task name, start-stop, duration, start, delete
        Lists.newArrayList(35d, 15d, 15d, 18d, 7.5d, 7.5d).forEach(p ->
        {
            ColumnConstraints c = new ColumnConstraints();
            c.setPercentWidth(p);
            result.add(c);
        });
        return result;
    }

    public static String getTimeFromMills(long mills)
    {
        return new SimpleDateFormat("HH:mm").format(new Date(mills));
    }

    public static String formatDate(long mills)
    {
        return new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date(mills));
    }
}
