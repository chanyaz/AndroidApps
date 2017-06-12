package com.july.cloud.countdowndemo;

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

    public static String getGlobalTime(String country) {
        String mTimeZone = getTimeZone(country);
        Calendar calendar = Calendar.getInstance();
        Calendar ukTime = new GregorianCalendar(TimeZone.getTimeZone(mTimeZone));
        ukTime.setTimeInMillis(calendar.getTimeInMillis());
        int year = ukTime.get(Calendar.YEAR);
        int month = ukTime.get(Calendar.MONTH);
        int date = ukTime.get(Calendar.DATE);
        int hour = ukTime.get(Calendar.HOUR_OF_DAY);
        int mini = ukTime.get(Calendar.MINUTE);
        int second = ukTime.get(Calendar.SECOND);
        String mMonth;
        if ((month + 1) < 10) {
            mMonth = "0" + String.valueOf(month + 1);
        } else {
            mMonth = String.valueOf(month + 1);
        }
        String mDate;
        if (date < 10) {
            mDate = "0" + String.valueOf(date);
        } else {
            mDate = String.valueOf(date);
        }
        String mHour;
        if (hour < 10) {
            mHour = "0" + String.valueOf(hour);
        } else {
            mHour = String.valueOf(hour);
        }
        String mMini;
        if (mini < 10) {
            mMini = "0" + String.valueOf(mini);
        } else {
            mMini = String.valueOf(mini);
        }
        String mSecond;
        if (second < 10) {
            mSecond = "0" + String.valueOf(second);
        } else {
            mSecond = String.valueOf(second);
        }
        String nowtime = year + "-" + mMonth + "-" + mDate + " " + mHour + ":" + mMini + ":" + mSecond;
        System.out.println(year + "-" + mMonth + "-" + mDate + " " + mHour + ":" + mMini + ":" + mSecond);
        return nowtime;
    }

    public static String getTimeZone(String country) {
        String mTimeZone = "GMT";
        if (country == "en-GB") {
            mTimeZone = "GMT+1";
        } else if (country == "en-US") {
            mTimeZone = "GMT-4";
        } else if (country == "en-CA") {
            mTimeZone = "GMT-8";
        } else if (country == "en-AA") {
            mTimeZone = "GMT+8";
        }
        return mTimeZone;
    }

    /**
     * 缓存保存时长
     *
     * @param country
     * @param digest_edition
     * @param set_mornning_time
     * @param set_evening_time
     * @return
     */
    public static int getCacheSaveTime(String country, int digest_edition, String set_mornning_time, String set_evening_time) {
        String mTimeZone = getTimeZone(country);
        Calendar calendar = Calendar.getInstance();
        Calendar ukTime = new GregorianCalendar(TimeZone.getTimeZone(mTimeZone));
        ukTime.setTimeInMillis(calendar.getTimeInMillis());
        int hour = ukTime.get(Calendar.HOUR_OF_DAY);
        int mini = ukTime.get(Calendar.MINUTE);
        int second = ukTime.get(Calendar.SECOND);
        int cache_time = 0;
        if (set_mornning_time == "") {
            set_mornning_time = "08:00:00";//6-10
        }
        if (set_evening_time == "") {
            set_evening_time = "18:00:00";//18-22
        }
        if (digest_edition == 0) {
            //evening - nowtime
            int set_mini = Integer.valueOf(set_evening_time.substring(3, 5).trim());
            int set_hour = Integer.valueOf(set_evening_time.substring(0, 2));
            cache_time = (set_hour * 3600 + set_mini * 60) - (hour * 3600 + mini * 60 + second);
        } else if (digest_edition == 1) {
            //morning - mowtime
            int mor_set_hou = 0;
            int mor_set_min = 0;
            if (set_mornning_time.substring(0, 1) == "0") {
                mor_set_min = Integer.valueOf(set_mornning_time.substring(3, 5).trim());
                mor_set_hou = Integer.valueOf(set_mornning_time.substring(1, 2).trim());
            } else {
                mor_set_hou = Integer.valueOf(set_mornning_time.substring(0, 2).trim());
            }
            int now_mor = mor_set_min * 60 + mor_set_hou * 3600;
            if (17 < hour && hour <= 23) {
                cache_time = now_mor + 24 * 3600 - (hour * 3600 + mini * 60 + second);
            } else {
                cache_time = now_mor - (hour * 3600 + mini * 60 + second);
            }
        }
        return cache_time;
    }
}
