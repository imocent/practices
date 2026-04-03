package com.fit.util;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * 日期处理工具类
 *
 * @Description: 工具类，可以用作获取系统日期、订单编号等
 */
public class DateUtils {

    // ====================== 定义常量 ======================
    /**
     * 完整时间 yyyy-MM-dd'T'HH:mm:ss
     */
    public static final String STR_DATE_FULL_T = "yyyy-MM-dd'T'HH:mm:ss";
    /**
     * 完整时间 yyyy-MM-dd HH:mm:ss
     */
    public static final String STR_DATE_FULL = "yyyy-MM-dd HH:mm:ss";
    /**
     * 完整时间 yyyy-MM-dd HH:mm:ss.SSS
     */
    public static final String STR_DATE_LONG = "yyyy-MM-dd kk:mm:ss.SSS";
    /**
     * 年月日 yyyy-MM-dd
     */
    public static final String STR_DATE_SMALL = "yyyy-MM-dd";
    /**
     * 年月日时分秒(无横划线) yyMMddHHmmss
     */
    public static final String STR_DATE_KEY = "yyMMddHHmmss";
    /**
     * 年月日时分秒(无横划线) yyyyMMddHHmmss
     */
    public static final String STR_DATE_All_KEY = "yyyyMMddHHmmss";
    /**
     * 年月日(无横划线) yyyyMMdd
     */
    public static final String STR_DATE_SHORT = "yyyyMMdd";
    /**
     * 年月 yyyyMM
     */
    public static final String STR_YEAR_MONTH = "yyyyMM";
    /**
     * 年 yyyy
     */
    public static final String STR_YEAR = "yyyy";

    /**
     * 当前时间对象
     */
    public static Date nowDate() {
        return new Date();
    }

    /**
     * 获取系统当前日期(精确到毫秒)，格式：yyyy-MM-dd
     */
    public static String nowDateStr() {
        return dateFormatToString(STR_DATE_FULL);
    }

    /**
     * 获取当前timestamp
     */
    public static String nowTimeStamp() {
        return System.currentTimeMillis() + "";
    }

    /**
     * 将时间字符串转换成数据库中datetime可存储的类型
     */
    public static Timestamp sqlParse(String date) {
        return new Timestamp(dateParse(date).getTime());
    }

    /**
     * 根据特定格式格式化日期
     *
     * @param date    被格式化的日期
     * @param pattern 时间格式
     * @return 格式化后的字符串
     */
    public static String dateFormatToString(Date date, String pattern) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        Calendar calender = Calendar.getInstance();
        calender.setTime(date);
        return simpleDateFormat.format(calender.getTime());
    }

    /**
     * 获取系统当前时间(指定返回类型)
     *
     * @param pattern 时间格式
     * @return 时间字符串
     */
    public static String dateFormatToString(String pattern) {
        return dateFormatToString(nowDate(), pattern);
    }

    /**
     * 时间转换为yyyy-MM-dd HH:mm:ss格式的字符串
     */
    public static String dateToString(Date date) {
        DateFormat YYYY_MM_DD_MM_HH_SS = new SimpleDateFormat(STR_DATE_FULL);
        return YYYY_MM_DD_MM_HH_SS.format(date);
    }

    /**
     * 获取系统当前日期(精确到毫秒)，格式：yyyy-MM-dd
     */
    public static String getDateNowSMALL() {
        return dateFormatToString(STR_DATE_SMALL);
    }

    /**
     * 获取系统当期年月日(精确到天)，格式：yyyyMMdd
     */
    public static String getDateShort() {
        return dateFormatToString(STR_DATE_SHORT);
    }

    /**
     * 返回系统当前时间(精确到毫秒),作为一个唯一的订单编号，格式：yyyyMMddHHmmss
     */
    public static String getDateOrderNum() {
        return dateFormatToString(STR_DATE_All_KEY);
    }

    /**
     * 两个时间比较
     */
    public static int compareDateWithNow(Date date) {
        return date.compareTo(nowDate());
    }

    /**
     * 两个时间比较(时间戳比较)
     */
    public static int compareDateWithNow(long date) {
        long now = dateToUnixTimestamp();
        if (date > now) {
            return 1;
        } else if (date < now) {
            return -1;
        } else {
            return 0;
        }
    }

    /**
     * 时间字符串转换指定格式时间对象
     *
     * @param dateString 时间字符串
     * @param pattern    时间格式
     */
    public static Date dateParse(String dateString, String pattern) {
        SimpleDateFormat df = new SimpleDateFormat(pattern);
        try {
            return df.parse(dateString);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 时间转换为yyyy-MM-dd HH:mm:ss格式的date类型
     */
    public static Date dateParse(String dateString) {
        return dateParse(dateString, STR_DATE_FULL);
    }

    /**
     * 时间转换为yyyy-MM-dd格式的date类型
     */
    public static Date dateParseYYMMDDDate(String dateString) {
        return dateParse(dateString, STR_DATE_SMALL);
    }

    /**
     * 将指定的日期转换成Unix时间戳
     *
     * @param dateString 时间字符串
     * @param pattern    时间格式
     * @return long 时间戳
     */
    public static long dateToUnixTimestamp(String dateString, String pattern) {
        long timestamp = 0;
        try {
            timestamp = dateParse(dateString, pattern).getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return timestamp;
    }


    /**
     * 将当前日期零点转换成Unix时间戳
     *
     * @return long 时间戳
     */
    public static long dateZeroToUnixTimestamp() {
        String nowDay = getDateNowSMALL() + " 00:00:00";
        return dateToUnixTimestamp(nowDay);
    }

    /**
     * 将指定的日期转换成Unix时间戳，格式：yyyy-MM-dd HH:mm:ss
     */
    public static long dateToUnixTimestamp(String dateString) {
        return dateToUnixTimestamp(dateString, STR_DATE_FULL);
    }

    /**
     * 将当前时间日期转换成Unix时间戳
     *
     * @return long 时间戳
     */
    public static long dateToUnixTimestamp() {
        return nowDate().getTime();
    }

    /**
     * 将Unix时间戳转换成日期
     *
     * @param timestamp 时间戳
     * @return String 日期字符串
     */
    public static String unixTimestampToDate(long timestamp) {
        SimpleDateFormat sd = new SimpleDateFormat(STR_DATE_FULL);
        sd.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        return sd.format(new Date(timestamp));
    }

    /**
     * 将Unix时间戳转换成日期
     *
     * @param timestamp 时间戳
     * @return String 日期字符串
     */
    public static String TimeStamp2Date(long timestamp) {
        return TimeStamp2Date(timestamp, STR_DATE_FULL);
    }

    /**
     * 将Unix时间戳转换成日期
     *
     * @param timestamp 时间戳
     * @return String 日期字符串
     */
    public static String TimeStamp2Date(long timestamp, String pattern) {
        return dateFormatToString(new Date(timestamp), pattern);
    }

    /**
     * 获取当月的第一天
     */
    public static String getMonthFirstDay() {
        Calendar cal = Calendar.getInstance();
        Calendar f = (Calendar) cal.clone();
        f.clear();
        f.set(Calendar.YEAR, cal.get(Calendar.YEAR));
        f.set(Calendar.MONTH, cal.get(Calendar.MONTH));
        return new SimpleDateFormat(STR_DATE_SMALL).format(f.getTime());
    }

    /**
     * 获取当月的第一天，格式：yyyy-MM-dd HH:mm:ss
     */
    public static String getMonthFirstDayFull() {
        return getMonthFirstDay() + " 00:00:00";
    }

    /**
     * 获取当月的最后一天
     */
    public static String getMonthLastDay() {
        Calendar cal = Calendar.getInstance();
        Calendar l = (Calendar) cal.clone();
        l.clear();
        l.set(Calendar.YEAR, cal.get(Calendar.YEAR));
        l.set(Calendar.MONTH, cal.get(Calendar.MONTH) + 1);
        l.set(Calendar.MILLISECOND, -1);
        return new SimpleDateFormat(STR_DATE_SMALL).format(l.getTime());
    }

    /**
     * 获取当月的最后一天，格式：yyyy-MM-dd HH:mm:ss
     */
    public static String getMonthLastDayFull() {
        return getMonthLastDay() + " 23:59:59";
    }

    public static String getLastWeek() {
        return dateFormatToString(getDayByNum(-7), STR_DATE_SMALL);
    }

    /**
     * 获取昨天日期
     */
    public static String getYesterday() {
        return dateFormatToString(getDayByNum(-1), STR_DATE_SMALL);
    }

    public static Date getDayByNum(int num) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(nowDate());
        calendar.add(Calendar.DATE, num);
        return calendar.getTime();
    }

    /**
     * 当前时间加上 年份 所得的 日期
     *
     * @param year 增加的年数
     */
    public static String getYear(int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(nowDate());
        calendar.add(Calendar.YEAR, +year);
        return new SimpleDateFormat(STR_DATE_FULL).format(calendar.getTime());
    }

    /**
     * 给指定的日期加上(减去)月份
     *
     * @param date    时间对象
     * @param pattern 时间格式
     * @param num     增减的月份数
     */
    public static String addMoth(Date date, String pattern, int num) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        Calendar calender = Calendar.getInstance();
        calender.setTime(date);
        calender.add(Calendar.MONTH, num);
        return simpleDateFormat.format(calender.getTime());
    }

    /**
     * 给制定的时间加上(减去)天
     *
     * @param date    时间对象
     * @param pattern 时间格式
     * @param num     增减的天数
     */
    public static String addDay(Date date, String pattern, int num) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        Calendar calender = Calendar.getInstance();
        calender.setTime(date);
        calender.add(Calendar.DATE, num);
        return simpleDateFormat.format(calender.getTime());
    }

    /**
     * 获取昨天零点的时间戳
     */
    public static long getYesterdayZeroUnixTimestamp() {
        String yesterday = getYesterday() + " 00:00:00";
        return dateToUnixTimestamp(yesterday);
    }

    /**
     * 获取昨天23:59:59的时间戳
     */
    public static long getYesterdayUnixTimestamp() {
        String yesterday = getYesterday() + " 23:59:59";
        return dateToUnixTimestamp(yesterday);
    }

    /**
     * 获取指定日期的零点的时间戳
     */
    public static long getEnactZeroDate(Date date) {
        String now = dateFormatToString(date, STR_DATE_SMALL) + " 00:00:00";
        return dateToUnixTimestamp(now);
    }

    /**
     * 获取指定日期的23:59:59的时间戳
     */
    public static long getEnactDate(Date date) {
        String now = dateFormatToString(date, STR_DATE_SMALL) + " 23:59:59";
        return dateToUnixTimestamp(now);
    }
}