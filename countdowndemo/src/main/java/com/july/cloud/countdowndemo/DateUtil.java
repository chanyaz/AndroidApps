package com.july.cloud.countdowndemo;

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
    public static String[] months = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
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


            } else if (present.after(evening)) {
                section = SECTION_EVENING_TODAY;

            } else {
                section = SECTION_MORNING_TODAY;

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
     * 判断当前日期是星期几
     *
     * @param pTime 要判断的时间
     * @return dayForWeek 判断结果
     * @Exception 发生异常
     */
    public static String DayforWeek(String pTime) throws Exception{
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.setTime(format.parse(pTime));
        int dayForWeek = 0;
        String week = "";
        dayForWeek = c.get(Calendar.DAY_OF_WEEK);
        switch (dayForWeek) {
            case 1:
                week = "Sunday";
                break;
            case 2:
                week = "Monday";
                break;
            case 3:
                week = "Tuesday";
                break;
            case 4:
                week = "Wednesday";
                break;
            case 5:
                week = "Thurday";
                break;
            case 6:
                week = "Friday";
                break;
            case 7:
                week = "Saturday";
                break;
        }
        return week;
    }
}
