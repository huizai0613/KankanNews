package com.kankan.kankanews.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;

@SuppressLint("SimpleDateFormat")
public class TimeUtil {

	public final static String FORMAT_YEAR = "yyyy";
	public final static String FORMAT_MONTH_DAY = "MM月dd日";

	public final static String FORMAT_DATE = "yyyy-MM-dd";
	public final static String FORMAT_TIME = "HH:mm";
	public final static String FORMAT_MONTH_DAY_TIME = "MM月dd日  hh:mm";

	public final static String FORMAT_DATE_TIME = "yyyy-MM-dd HH:mm:ss";
	public final static String FORMAT_DATE1_TIME = "yyyy/MM/dd HH:mm";
	public final static String FORMAT_DATE_TIME_SECOND = "yyyy/MM/dd HH:mm:ss";

	private static SimpleDateFormat sdf = new SimpleDateFormat();
	private static final int YEAR = 365 * 24 * 60 * 60;// 年
	private static final int MONTH = 30 * 24 * 60 * 60;// 月
	private static final int DAY = 24 * 60 * 60;// 天
	private static final int HOUR = 60 * 60;// 小时
	private static final int MINUTE = 60;// 分钟

	// 将传入时间与当前时间进行对比，是否今天昨天
	public static String getTime(Date date) {
		String todySDF = "今天 HH:mm";
		String yesterDaySDF = "昨天 HH:mm";
		String otherSDF = "M月d日 HH:mm";
		SimpleDateFormat sfd = null;
		String time = "";
		Calendar dateCalendar = Calendar.getInstance();
		dateCalendar.setTime(date);
		Date now = new Date();
		Calendar targetCalendar = Calendar.getInstance();
		targetCalendar.setTime(now);
		targetCalendar.set(Calendar.HOUR_OF_DAY, 0);
		targetCalendar.set(Calendar.MINUTE, 0);
		if (dateCalendar.after(targetCalendar)) {
			sfd = new SimpleDateFormat(todySDF);
			time = sfd.format(date);
			return time;
		} else {
			targetCalendar.add(Calendar.DATE, -1);
			if (dateCalendar.after(targetCalendar)) {
				sfd = new SimpleDateFormat(yesterDaySDF);
				time = sfd.format(date);
				return time;
			}
		}
		sfd = new SimpleDateFormat(otherSDF);
		time = sfd.format(date);
		return time;
	}

	/**
	 * 根据时间戳获取描述性时间，如3分钟前，1天前
	 * 
	 * @param timestamp
	 *            时间戳 单位为毫秒
	 * @return 时间字符串
	 */
	public static String getDescriptionTimeFromTimestamp(long timestamp) {
		long currentTime = System.currentTimeMillis();
		long timeGap = (currentTime - timestamp) / 1000;// 与现在时间相差秒数
		System.out.println("timeGap: " + timeGap);
		String timeStr = null;
		if (timeGap > YEAR) {
			timeStr = timeGap / YEAR + "年前";
		} else if (timeGap > MONTH) {
			timeStr = timeGap / MONTH + "个月前";
		} else if (timeGap > DAY) {// 1天以上
			timeStr = timeGap / DAY + "天前";
		} else if (timeGap > HOUR) {// 1小时-24小时
			timeStr = timeGap / HOUR + "小时前";
		} else if (timeGap > MINUTE) {// 1分钟-59分钟
			timeStr = timeGap / MINUTE + "分钟前";
		} else {// 1秒钟-59秒钟
			timeStr = "刚刚";
		}
		return timeStr;
	}

	/**
	 * 获取当前日期的指定格式的字符串
	 * 
	 * @param format
	 *            指定的日期时间格式，若为null或""则使用指定的格式"yyyy-MM-dd HH:MM"
	 * @return
	 */
	public static String getCurrentTime(String format) {
		if (format == null || format.trim().equals("")) {
			sdf.applyPattern(FORMAT_DATE_TIME);
		} else {
			sdf.applyPattern(format);
		}
		return sdf.format(new Date());
	}

	// date类型转换为String类型
	// formatType格式为yyyy-MM-dd HH:mm:ss//yyyy年MM月dd日 HH时mm分ss秒
	// data Date类型的时间
	public static String dateToString(Date data, String formatType) {
		return new SimpleDateFormat(formatType).format(data);
	}

	// long类型转换为String类型
	// currentTime要转换的long类型的时间
	// formatType要转换的string类型的时间格式
	public static String longToString(long currentTime, String formatType) {
		String strTime = "";
		Date date = longToDate(currentTime, formatType);// long类型转成Date类型
		strTime = dateToString(date, formatType); // date类型转成String
		return strTime;
	}

	public static String timeStrToString(String time, String formatType) {
		SimpleDateFormat format = new SimpleDateFormat(formatType);
		long newsTime = Long.parseLong(time) * 1000;
		return format.format(new Date(newsTime));
	}

	// string类型转换为date类型
	// strTime要转换的string类型的时间，formatType要转换的格式yyyy-MM-dd HH:mm:ss//yyyy年MM月dd日
	// HH时mm分ss秒，
	// strTime的时间格式必须要与formatType的时间格式相同
	public static Date stringToDate(String strTime, String formatType) {
		SimpleDateFormat formatter = new SimpleDateFormat(formatType);
		Date date = null;
		try {
			date = formatter.parse(strTime);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return date;
	}

	// long转换为Date类型
	// currentTime要转换的long类型的时间
	// formatType要转换的时间格式yyyy-MM-dd HH:mm:ss//yyyy年MM月dd日 HH时mm分ss秒
	public static Date longToDate(long currentTime, String formatType) {
		Date dateOld = new Date(currentTime); // 根据long类型的毫秒数生命一个date类型的时间
		String sDateTime = dateToString(dateOld, formatType); // 把date类型的时间转换为string
		Date date = stringToDate(sDateTime, formatType); // 把String类型转换为Date类型
		return date;
	}

	// string类型转换为long类型
	// strTime要转换的String类型的时间
	// formatType时间格式
	// strTime的时间格式和formatType的时间格式必须相同
	public static long stringToLong(String strTime, String formatType) {
		Date date = stringToDate(strTime, formatType); // String类型转成date类型
		if (date == null) {
			return 0;
		} else {
			long currentTime = dateToLong(date); // date类型转成long类型
			return currentTime;
		}
	}

	// date类型转换为long类型
	// date要转换的date类型的时间
	public static long dateToLong(Date date) {
		return date.getTime();
	}

	public static String getTime(long time) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		return format.format(new Date(time));
	}

	public static String getHourAndMin(long time) {
		SimpleDateFormat format = new SimpleDateFormat("HH:mm");
		return format.format(new Date(time));
	}

	public static String getChatTime(long timesamp) {
		String result = "";
		SimpleDateFormat sdf = new SimpleDateFormat("dd");
		Date today = new Date(System.currentTimeMillis());
		Date otherDay = new Date(timesamp);
		int temp = Integer.parseInt(sdf.format(today))
				- Integer.parseInt(sdf.format(otherDay));

		switch (temp) {
		case 0:
			result = "今天 " + getHourAndMin(timesamp);
			break;
		case 1:
			result = "昨天 " + getHourAndMin(timesamp);
			break;
		case 2:
			result = "前天 " + getHourAndMin(timesamp);
			break;

		default:
			result = getTime(timesamp);
			break;
		}

		return result;
	}

	public static boolean checkCurDate(long timesamp) {
		String result = "";
		SimpleDateFormat sdfD = new SimpleDateFormat("dd");
		SimpleDateFormat sdfM = new SimpleDateFormat("MM");
		Date today = new Date(System.currentTimeMillis());
		Date otherDay = new Date(timesamp);
		int temp_D = Integer.parseInt(sdfD.format(today))
				- Integer.parseInt(sdfD.format(otherDay));
		int temp_M = Integer.parseInt(sdfM.format(today))
				- Integer.parseInt(sdfM.format(otherDay));

		if (temp_M == 0) {
			if (temp_D == 0) {
				return false;
			} else {
				return true;
			}
		} else {
			return true;
		}

	}

	/** 时间戳转日期 日期类型,如"yyyy/MM/dd HH:mm:ss" */
	public static String unix2date(long timestamp, String format) {
		return new java.text.SimpleDateFormat(format)
				.format(new java.util.Date(timestamp * 1000));
	}

	/** 日期转时间戳 日期,格式"yyyy-MM-dd HH:mm:ss" 转换失败返回-1 */
	public static long date2unix(String date) {
		try {
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date)
					.getTime()
					- new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(
							"1970-01-01 00:00:00").getTime();
		} catch (ParseException e) {
			XunaoLog.yLog().i("时间\"" + date + "\"转时间戳失败");
			return -1;
		}
	}

	/** 日期转时间戳 日期,格式"yyyy-MM-dd HH:mm" 转换失败返回-1 */
	public static long date2unix2(String date) {
		try {
			return new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(date)
					.getTime()
					- new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(
							"1970-01-01 00:00").getTime();
		} catch (ParseException e) {
			XunaoLog.yLog().i("时间\"" + date + "\"转时间戳失败");
			return -1;
		}
	}

	/** 获取当前时间戳 (单位秒) */
	public static long now() {
		return System.currentTimeMillis() / 1000;
	}

	/**
	 * 获取给定时间戳到现在的时间差,根据数量级不同返回不同的单位
	 * 时间差加相应单位,如刚刚、w分钟前、x个小时前、y天前、z个月前、很久前,如果传入的时间大于现在,返回未知
	 */
	public static String lag(long timestamp) {
		long diff = now() - timestamp;
		if (diff < 0) {
			return "未知";
		} else if (diff <= 60) {
			return "刚刚";
		} else if (diff <= (60 * 60)) {
			return diff / 60 + "分钟前";
		} else if (diff <= (60 * 60 * 24)) {
			return diff / (60 * 60) + "个小时前";
		} else if (diff <= (60 * 60 * 24 * 30)) {
			return diff / (60 * 60 * 24) + "天前";
		} else if (diff <= (60 * 60 * 24 * 30 * 12)) {
			return diff / (60 * 60 * 24 * 30) + "个月前";
		} else {
			return "很久前";
		}
	}

	public static String getWeekOfDate(long timestamp) {
		String[] dayofweek = new String[] { "星期日", "星期一", "星期二", "星期三", "星期四",
				"星期五", "星期六" };
		Calendar c = Calendar.getInstance();
		try {
			c.setTime((new SimpleDateFormat("yyyy/MM/dd")).parse(unix2date(
					timestamp, "yyyy/MM/dd")));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return dayofweek[c.get(Calendar.DAY_OF_WEEK) - 1];
	}

	public static String getWeekOfDate(String date, String format) {
		String[] dayofweek = new String[] { "星期日", "星期一", "星期二", "星期三", "星期四",
				"星期五", "星期六" };
		Calendar c = Calendar.getInstance();
		try {
			c.setTime((new SimpleDateFormat(format)).parse(date));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return dayofweek[c.get(Calendar.DAY_OF_WEEK) - 1];
	}

	/**
	 * 一天内的时间显示为“刚刚”，“X分钟前”，“X小时前”，超过24小时后则显示为具体的时间点
	 */
	public static String getMyTime(long timestamp) {
		long diff = now() - timestamp;
		if (diff < 0) {
			return "未知";
		} else if (diff <= 60) {
			return "刚刚";
		} else if (diff <= (60 * 60)) {
			return diff / 60 + "分钟前";
		} else if (diff <= (60 * 60 * 24)) {
			return diff / (60 * 60) + "个小时前";
		} else {
			return unix2date(timestamp, "yyyy-MM-dd HH:mm:ss");
		}
	}

}