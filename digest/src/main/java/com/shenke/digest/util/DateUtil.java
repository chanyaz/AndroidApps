package com.shenke.digest.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateUtil {
    private static final String TAG = "DateUtil";
    public static final int SECTION_MORNING_TODAY = 1;
    public static final int SECTION_EVENING_TODAY = 2;
    public static final int SECTION_EVENING_YESTERDAY = 3;
    //public static final int  SECTION_MORNING_YESTERDAY = 3;
    public static String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
    public static String[] month = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};

    public static Date getPreDay(Date presentDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(presentDate);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        presentDate = calendar.getTime();
        return presentDate;
    }


    public static Date getPreNDay(Date presentDate, int n) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(presentDate);
        calendar.add(Calendar.DAY_OF_MONTH, -n);
        presentDate = calendar.getTime();
        return presentDate;
    }

    public static Date getNextDay(Date presentDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(presentDate);
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        presentDate = calendar.getTime();
        return presentDate;
    }

    public static String format(Date date, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(date);
    }

    public static Date getPresentDate() {
        GregorianCalendar g = new GregorianCalendar();
        return g.getTime();
    }

    public static int getTimeSection() {

        int section = SECTION_MORNING_TODAY;
        try {
            GregorianCalendar g = new GregorianCalendar();
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
            String ymd = sdf1.format(g.getTime());
            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date morning = sdf2.parse(ymd + " 08:00:00");
            Date evening = sdf2.parse(ymd + " 18:00:00");
            Date present = g.getTime();
            if (present.before(morning)) {
                section = SECTION_EVENING_YESTERDAY;
                LogUtil.d(TAG, " yesterday's evening digest");

            } else if (present.after(evening)) {
                section = SECTION_EVENING_TODAY;
                LogUtil.d(TAG, "today's evening digest ");
            } else {
                section = SECTION_MORNING_TODAY;
                LogUtil.d(TAG, "today's morning digest ");
            }

        } catch (ParseException e) {

            e.printStackTrace();
        }
        return section;
    }

    public static String MonthFormat(String string) {
        for (int i = 0; i < months.length; i++) {
            if (months[i].equals(string)) {
                string = month[i];
            }
        }
        return string;
    }
    /**
     *  if (string.equals("Jan")) {
     string = "January";
     }else if(string.equals("Feb")){
     string = "February";
     }else if(string.equals("Mar")){
     string = "March";
     }else if(string.equals("Apr")){
     string = "April";
     }else if(string.equals("May")){
     string = "May";
     }else if(string.equals("Jun")){
     string = "June";
     }else if(string.equals("Jul")){
     string = "July";
     }else if(string.equals("Aug")){
     string = "August";
     }else if(string.equals("Sep")){
     string = "September";
     }else if(string.equals("Oct")){
     string = "October";
     }else if(string.equals("Nov")){
     string = "November";
     }else if(string.equals("Dec")){
     string = "December";
     }
     */
}
