package com.example.umfeed.utils;

import java.util.Calendar;

public class TimeUtils {
    public static String getGreeting() {
        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);

        if(timeOfDay < 12) {
            return "Good morning";
        } else if (timeOfDay <= 16) {
            return "Good afternoon";
        } else {
            return "Good evening";
        }
    }
}
