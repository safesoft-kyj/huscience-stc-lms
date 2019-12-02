package com.dtnsm.lms.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

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

    // 달의 마지막 일자 스트링으로 받기
    public static String getStringMonthStartDate() {

        int nYear, nMonth, nDay;
        Calendar cal = Calendar.getInstance();

        nYear = cal.get(Calendar.YEAR);
        nMonth = cal.get(Calendar.MONTH);
        nDay = 1;

        cal.set(nYear, nMonth, nDay);

        return getDateToString(cal.getTime());
    }

    public static String getStringMonthEndDate() {

        int nYear, nMonth, nDay;

        Calendar cal = Calendar.getInstance();
        cal.setTime(getToday());

        nYear = cal.get(Calendar.YEAR);
        nMonth = cal.get(Calendar.MONTH);
        nDay = cal.getActualMaximum  (Calendar.DAY_OF_MONTH);

        cal.set(nYear, nMonth, nDay);

        return getDateToString(cal.getTime());
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

//    public static Date getStringToDate2(String stringDate, String format) {
//        Date date = null;
//        try {
//            date = new SimpleDateFormat(format, Locale.ENGLISH).parse(stringDate);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        return date;
//    }

    public static String getEnglishDate(String stringDate) {
        String outDateString = "";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat outSdf = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);

            Date tmpDate = sdf.parse(stringDate);
            outDateString = outSdf.format(tmpDate);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return outDateString;
    }

    public static Date getStringToDate(String stringDate, String format) {
        Date date = null;
        try {
            date = new SimpleDateFormat(format, Locale.ENGLISH).parse(stringDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static String getDateToString(Date date) {
        String stringDate = new SimpleDateFormat("yyyy-MM-dd").format(date);

        return stringDate;
    }

    public static String getDateToString(Date date, String format) {
        String stringDate = new SimpleDateFormat(format, Locale.ENGLISH).format(date);

        return stringDate;
    }

    public static Date getToday() {
        return getStringToDate(getTodayString());
    }

    public static Date getTodayDate() {
        Calendar c1 = Calendar.getInstance();
        return c1.getTime();
    }

    public static String getTodayString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c1 = Calendar.getInstance();
        return sdf.format(c1.getTime());
    }

    public static String getTodayDisplayString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MMM-dd");
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
