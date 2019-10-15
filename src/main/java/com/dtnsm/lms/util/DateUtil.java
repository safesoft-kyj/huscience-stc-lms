package com.dtnsm.lms.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public  class DateUtil {


    // 문자열 날짜에 하루 더하기
    public static String getStringDateAddDay(String stringDate, int addDay) {
        String retrunValue = "";

        Date date = getStringToDate(stringDate);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, addDay);
        retrunValue = getDateToString(cal.getTime());

        return retrunValue;
    }

    public static Date getStringToDate(String stringDate) {
        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd").parse(stringDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static String getDateToString(Date date) {
        String stringDate = new SimpleDateFormat("yyyy-MM-dd").format(date);

        return stringDate;
    }

    public static Date getToday() {
        return getStringToDate(getTodayString());
    }

    public static String getTodayString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c1 = Calendar.getInstance();
        return sdf.format(c1.getTime());
    }


    public static List<String> getYearList() {
        int year, yearMin, yearMax;
        int yearResuult, yearCount;
        int month;
        int date;

        List<String> yearList = new ArrayList<>();
        Calendar now = Calendar.getInstance();

        year = now.get(Calendar.YEAR);
        month = now.get(Calendar.MONTH) + 1;
        date = now.get(Calendar.DATE);

        yearMin = 2010;
        yearMax = now.get(Calendar.YEAR)+1;
        yearCount = yearMax - yearMin;

        yearResuult = yearMin;

        for(int i=0; yearResuult<=yearMax; i++) {
            yearList.add(String.valueOf(yearResuult++));
        }

        return yearList;
    }

}
