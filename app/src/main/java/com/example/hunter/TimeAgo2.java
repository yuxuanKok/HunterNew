package com.example.hunter;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class TimeAgo2 {

    public String covertTimeToText(Date dataDate) {

        String convTime = null;

        String prefix = "";
        String suffix = "ago";

        try {
            String formattedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(dataDate);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date pasTime = dateFormat.parse(formattedDate);
//            Date pasTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z").parse(dataDate);



            Date nowTime = new Date();
            Log.e("ConvTimeE", nowTime.toString());

            long dateDiff = nowTime.getTime() - pasTime.getTime();

            long second = TimeUnit.MILLISECONDS.toSeconds(dateDiff);
            long minute = TimeUnit.MILLISECONDS.toMinutes(dateDiff);
            long hour   = TimeUnit.MILLISECONDS.toHours(dateDiff);
            long day  = TimeUnit.MILLISECONDS.toDays(dateDiff);

            if (second < 60) {
                convTime = second+" s "+suffix;
            } else if (minute < 60) {
                convTime = minute+" m "+suffix;
            } else if (hour < 24) {
                convTime = hour+" h "+suffix;
            } else if (day >= 7) {
                if (day > 360) {
                    convTime = (day / 30) + " y " + suffix;
                } else if (day > 30) {
                    convTime = (day / 360) + " M " + suffix;
                } else {
                    convTime = (day / 7) + " w " + suffix;
                }
            } else if (day < 7) {
                convTime = day+" d "+suffix;
            }

        } catch (ParseException e) {
            e.printStackTrace();
            Log.e("ConvTimeE", e.getMessage());

        }
        Log.d("TimeAgo2", "working");
        return convTime;
    }

}
