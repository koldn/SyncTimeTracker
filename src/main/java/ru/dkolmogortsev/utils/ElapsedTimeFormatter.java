package ru.dkolmogortsev.utils;

/**
 * Created by dkolmogortsev on 2/11/17.
 */
public class ElapsedTimeFormatter
{
    private static final int SECONDS_IN_MINUTE = 60;
    private static final int MINUTES_IN_HOUR = 60;
    private static final int SECONDS_IN_HOUR = SECONDS_IN_MINUTE * MINUTES_IN_HOUR;

    public static String formatElapsed(long elapsed)
    {
        long hours = elapsed / SECONDS_IN_HOUR;
        long minutes = (elapsed % SECONDS_IN_HOUR) / SECONDS_IN_MINUTE;
        long secs = elapsed % SECONDS_IN_MINUTE;
        return String.format("%d:%02d:%02d", hours, minutes, secs);
    }
}
