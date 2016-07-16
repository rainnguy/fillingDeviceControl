package com.banxian.util;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 获取当前时间
 * 
 * @author xk
 *
 */
public class CurrentTime {

	private CurrentTime() {
	}

	private static final CurrentTime single = new CurrentTime();

	// 静态工厂方法
	public static CurrentTime getInstance() {
		return single;
	}

	/** 北京时间网 */
	private static final String WEBURL = "http://www.beijing-time.org/";

	/**
	 * 获取当前北京时间，格式：yyyyMMddHHmmss
	 * 
	 * @return
	 */
	public String getCurrentTime() {
		URL url;
		URLConnection uc = null;
		try {
			// 取得资源对象
			url = new URL(WEBURL);

			// 生成连接对象
			uc = url.openConnection();
			// 发出连接
			uc.connect();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
		long ld = uc.getDate(); // 取得网站日期时间
		Date date = new Date(ld); // 转换为标准时间对象
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		String dateString = formatter.format(date);
		return dateString;
	}

	/**
	 * 获取当前日期,格式：yyyyMMdd
	 * 
	 * @return 当前日期
	 */
	public String getNowDate() {
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		String dateString = dateFormat.format(date);
		return dateString;
	}

	/**
	 * 获取当前年月,格式：yyyyMM
	 * 
	 * @return 当前年月
	 */
	public String getNowMonth() {
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMM");
		String dateString = dateFormat.format(date);
		return dateString;
	}

	/**
	 * 获取上个月的年月,格式：yyyyMM
	 * 
	 * @return 上个月的年月
	 */
	public String getLastMonth() {

		Date date = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.MONTH, -1);
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMM");
		String dateString = dateFormat.format(cal.getTime());
		return dateString;
	}

	/**
	 * 获取当天的凌晨时间
	 * 
	 * @return
	 */
	public String getstartTime() {

		Date date = new Date();

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		int minute = cal.get(Calendar.MINUTE);
		int second = cal.get(Calendar.SECOND);
		// 时分秒（毫秒数）
		long millisecond = hour * 60 * 60 * 1000 + minute * 60 * 1000 + second * 1000;
		// 凌晨00:00:00
		cal.setTimeInMillis(cal.getTimeInMillis() - millisecond);

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateString = dateFormat.format(cal.getTime());
		return dateString;
	}
	
	/**
	 * 获取前一天的日期，格式：yyyyMMdd
	 * @return
	 */
	public String getLastDay(){
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DAY_OF_YEAR, - 1);
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		String dateString = dateFormat.format(calendar.getTime());
		return dateString;
	}
}
