package ru.dkolmogortsev.utils;

/**
 * Created by dkolmogortsev on 2/11/17.
 */
public class ElapsedTimeFormatter {
    private static final int SECONDS_IN_MINUTE = 60;
    private static final int MINUTES_IN_HOUR = 60;
    private static final int SECONDS_IN_HOUR = SECONDS_IN_MINUTE * MINUTES_IN_HOUR;

    public static String formatElapsed(int elapsed){
        int hours = elapsed / SECONDS_IN_HOUR;
        int minutes = (elapsed % SECONDS_IN_HOUR)/SECONDS_IN_MINUTE;
        int secs = elapsed % SECONDS_IN_MINUTE;
        return String.format("%d:%02d:%02d", hours, minutes,secs);
    }
}
