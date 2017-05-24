package com.shenke.digest.util;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Bg设置、View、时间帮助类
 */
public class Helper {
    public static void setBackground(View view, Drawable d) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackground(d);
        } else {
            view.setBackgroundDrawable(d);
        }
    }

    /**
     * 非UI线程刷新View
     *
     * @param view
     */
    public static void postInvalidateOnAnimation(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.postInvalidateOnAnimation();
        } else {
            view.invalidate();
        }
    }


    public static String format(Date date) {
        SimpleDateFormat myFmt1 = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss EEE");
        String[] days = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        String[] daysFull = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        int month = calendar.get(Calendar.MONTH);
        return String
                .valueOf(days[day - 1] + " " + myFmt1.format(date) + " " + months[month] + " " + daysFull[day - 1]);

    }
    public static String getGlobalTime(String country){
        String mTimeZone = "GMT";
        if(country == "en-UK"){
            mTimeZone ="GMT+1";
        }else if(country == "en-US"){
            mTimeZone ="GMT-1";
        } else if(country == "en-CA"){
            mTimeZone ="GMT-8";
        }
        else if(country == "en-AA"){
            mTimeZone ="GMT+8";
        }
        Calendar calendar = Calendar.getInstance();
        Calendar ukTime = new GregorianCalendar(TimeZone.getTimeZone(mTimeZone));
        ukTime.setTimeInMillis(calendar.getTimeInMillis());
        int year = ukTime.get(Calendar.YEAR);
        int month = ukTime.get(Calendar.MONTH);
        int date = ukTime.get(Calendar.DATE);
        int hour = ukTime.get(Calendar.HOUR_OF_DAY);
        int mini = ukTime.get(Calendar.MINUTE);
        int second = ukTime.get(Calendar.SECOND);
        Date date1 = new GregorianCalendar(TimeZone.getTimeZone(mTimeZone)).getTime();
        String nowtime = year+"-"+(month+1)+"-"+date+" "+hour+":"+mini+":"+second;
        System.out.println(year+"-"+(month+1)+"-"+date+" "+hour+":"+mini+":"+second);
        return nowtime;
    }
}
