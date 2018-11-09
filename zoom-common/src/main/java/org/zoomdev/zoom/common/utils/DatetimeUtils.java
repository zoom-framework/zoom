package org.zoomdev.zoom.common.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DatetimeUtils {
	// 日期格式
	public static final String DATE_FORMAT = "yyyy-MM-dd";
	// 时间格式
	public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
	// 时间格式
	public static final String DATE_TIME_FORMAT_MINUTE = "yyyy-MM-dd HH:mm";

	public static final String SHORT_DATE_FORMAT = "yyyyMMdd";
	public static final String SHORT_DATE_TIME_FORMAT = "yyyyMMddHHmmss";
	// 时间格式
	public static final String TIME_FORMAT = "HH:mm:ss";

	public static final long ONE_DAY_SECONDS = 24 * 3600L;

	public static final long ONE_DAY_MILI_SECONDS = ONE_DAY_SECONDS * 1000;
	
	public static final String SHORT_TIME_FORMAT = "HHmmss";
	public static final String SHORT_MONTH = "yyyyMM";
	/**
	 * 是否是闰年
	 * 
	 * @param year
	 * @return
	 */
	public static boolean isLeapYear(int year) {
		return (year % 4 == 0 && year % 100 != 0 || year % 400 == 0);
	}

	static final int[] DATE_OF_MONTH = new int[] { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
	public static final int A_DAY = 86400;

	public static String shortDate() {
		return new SimpleDateFormat(DatetimeUtils.SHORT_DATE_FORMAT).format(new Date());
	}

	public static String shortDate(long time) {
		return new SimpleDateFormat(DatetimeUtils.SHORT_DATE_FORMAT).format(new Date(time));
	}

	public static String shortTime() {
		return new SimpleDateFormat(DatetimeUtils.SHORT_TIME_FORMAT).format(new Date());
	}

	public static String shortDateTime(long time) {
		return new SimpleDateFormat(DatetimeUtils.SHORT_DATE_TIME_FORMAT).format(new Date(time));
	}

	public static String shortDateTime() {
		return new SimpleDateFormat(DatetimeUtils.SHORT_DATE_TIME_FORMAT).format(new Date());
	}

	/**
	 * 一个月有几天
	 * 
	 * @param month 0-11
	 * @param year  年份
	 * @return
	 */
	public static int getDaysOfMonth(int year, int month) {
		if (month == 2) {
			return isLeapYear(year) ? 29 : 20;
		}
		return DATE_OF_MONTH[month - 1];
	}

	public static String date() {
		return new SimpleDateFormat(DatetimeUtils.DATE_FORMAT).format(new Date());
	}

	public static String dateTime() {
		return new SimpleDateFormat(DatetimeUtils.DATE_TIME_FORMAT).format(new Date());
	}

	public static String date(int unixTimeStamp) {
		return new SimpleDateFormat(DatetimeUtils.DATE_FORMAT).format(new Date((long) unixTimeStamp * 1000));
	}

	public static String dateTime(int unixTimeStamp) {
		return new SimpleDateFormat(DatetimeUtils.DATE_TIME_FORMAT).format(new Date((long) unixTimeStamp * 1000));
	}

	public static String time() {
		return new SimpleDateFormat(DatetimeUtils.TIME_FORMAT).format(new Date());
	}

	public static String time(int unixTimeStamp) {
		return new SimpleDateFormat(DatetimeUtils.TIME_FORMAT).format(new Date((long) unixTimeStamp * 1000));
	}

	public static int getTime() {
		return (int) (System.currentTimeMillis() / 1000);
	}

	public static int parseDate(String date) throws ParseException {
		return (int) (new SimpleDateFormat(DatetimeUtils.DATE_FORMAT).parse(date).getTime() / 1000);
	}

	public static Date parseShortDate(String date) throws ParseException {
		return new SimpleDateFormat(DatetimeUtils.SHORT_DATE_FORMAT).parse(date);
	}

	public static Date toDate(String time, String format) {
		SimpleDateFormat df = new SimpleDateFormat(format);
		try {
			return df.parse(time);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	public static String shortYestoday() {
		return new SimpleDateFormat(DatetimeUtils.SHORT_DATE_FORMAT)
				.format(new Date(System.currentTimeMillis() - 86400L * 1000));
	}

	/**
	 * 下一个月
	 *
	 * @param month yyyyMM
	 */
	public static String nextMonth(String month) {
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMM");
			Date date = dateFormat.parse(month);

			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			calendar.set(Calendar.MONDAY, calendar.get(Calendar.MONTH) + 1);

			return dateFormat.format(calendar.getTime());

		} catch (ParseException e) {
			throw new RuntimeException(e);
		}

	}

	/**
	 * 上一个月
	 *
	 * @param month yyyyMM
	 */
	public static String prevMonth(String month) {
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMM");
			Date date = dateFormat.parse(month);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			calendar.set(Calendar.MONDAY, calendar.get(Calendar.MONTH) - 1);
			return dateFormat.format(calendar.getTime());
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	public static String shortMonth(long unixTime) {

		return new SimpleDateFormat(DatetimeUtils.SHORT_MONTH).format(new Date(unixTime * 1000));
	}

	/**
	 * 获取今天的剩余秒数
	 *
	 * @return
	 */
	public static int getDayLeftSeconds() {
		long time = System.currentTimeMillis() / 1000;
		long t1 = time % 86400L;
		return (int) (ONE_DAY_SECONDS - t1);
	}

	public static long parseShortDateTimeSilence(String time) {
		try {
			return new SimpleDateFormat(DatetimeUtils.SHORT_DATE_TIME_FORMAT).parse(time).getTime();
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	public static Date parseShortDateTime(String time) throws ParseException {
		return new SimpleDateFormat(DatetimeUtils.SHORT_DATE_TIME_FORMAT).parse(time);
	}

	public static String shortDate(Date date) {
		return new SimpleDateFormat(DatetimeUtils.SHORT_DATE_FORMAT).format(date);
	}

	public static String shortDateTime(Date date) {
		return new SimpleDateFormat(DatetimeUtils.SHORT_DATE_TIME_FORMAT).format(date);
	}

	public static String shortTime(Date date) {
		return new SimpleDateFormat(DatetimeUtils.SHORT_TIME_FORMAT).format(date);
	}

	public static Date parseDateTime(String dateTime) throws ParseException {

		return new SimpleDateFormat(DatetimeUtils.DATE_TIME_FORMAT).parse(dateTime);
	}

	public static String format(Date date, String format) {
		return new SimpleDateFormat(format).format(date);

	}

	/** 时间格式转换 */
	public static String dateFormat(String dateStr, String oldFormat, String newFormat) {
		SimpleDateFormat sdf1 = new SimpleDateFormat(oldFormat);
		SimpleDateFormat sdf2 = new SimpleDateFormat(newFormat);
		Date date = null;
		try {
			date = sdf1.parse(dateStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sdf2.format(date);
	}

	/** 获取规定时间前几天的时间 */
	public static String getDateBefore(int bDay, String DateFormat) {
		Calendar now = Calendar.getInstance();
		now.setTime(new Date());
		now.set(Calendar.DATE, now.get(Calendar.DATE) - bDay);
		return new SimpleDateFormat(DateFormat).format(now.getTime());
	}

	public static interface IDateVisiter {
		void onVisit(Date date);
	}

	public static void durationDay(Date startDate, Date endDate, IDateVisiter visiter) {
		long start = startDate.getTime();
		long end = endDate.getTime();

		for (long i = start; i <= end; i += ONE_DAY_MILI_SECONDS) {
			visiter.onVisit(new Date(i));
		}

	}

	public static Date parse(String date, String format) {
		try {
			return new SimpleDateFormat(format).parse(date);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	public static String chargeFormat(String date, String current, String dest) throws ParseException {
		Date date2 = new SimpleDateFormat(current).parse(date);
		return new SimpleDateFormat(dest).format(date2);
	}

	/**
	 * 增加
	 *
	 * @param date
	 * @param value
	 * @param format
	 * @return
	 */
	public static Date add(Date date, long value, int format) {
		long t = date.getTime();
		switch (format) {
		case Calendar.DATE:
			t += ONE_DAY_MILI_SECONDS;
			return new Date(t);
		default:
			throw new RuntimeException("不支持的格式，必须是Calendar的常量");
		}

	}

	public static enum AddDateType {

		DAY(Calendar.DATE), MONTH(Calendar.MONTH), YEAR(Calendar.YEAR);

		private int field;

		private AddDateType(int field) {
			this.field = field;
		}

		Date v(Date date, int v) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			calendar.set(field, calendar.get(field) + v);
			return calendar.getTime();
		}

	}

	/**
	 * 日期增加
	 *
	 * @param date
	 * @param type
	 * @param value
	 * @return
	 */
	public static Date dateAdd(Date date, AddDateType type, int value) {
		return type.v(date, value);
	}

	/**
	 * 去掉小时分钟秒
	 *
	 * @param date
	 * @return
	 */
	public static Date dateClean(Date date) {
		long time = date.getTime();
		long timeOfDate = time % 86400000L;
		time -= timeOfDate;
		return new Date(time);
	}

}
