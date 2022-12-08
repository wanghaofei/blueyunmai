package com.yucheng.ycbtsdk.Utils;

import java.util.Calendar;

public class TimeUtil {

    public static byte[] makeBleTime(){

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        if (dayOfWeek == 1) {
            dayOfWeek = 6;
        } else {
            dayOfWeek -= 2;
        }

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);
        int sec = calendar.get(Calendar.SECOND);

        byte[] tDataBytes = new byte[8];
        int tReduce2000Year = year;
        tDataBytes[0] = (byte) (tReduce2000Year & 0xff);
        tDataBytes[1] = (byte)((tReduce2000Year >> 8) & 0xff);
        tDataBytes[2] = (byte)month;
        tDataBytes[3] = (byte)dayOfMonth;
        tDataBytes[4] = (byte)hour;
        tDataBytes[5] = (byte)min;
        tDataBytes[6] = (byte)sec;
        tDataBytes[7] = (byte)dayOfWeek;

        return tDataBytes;
    }
}

    
