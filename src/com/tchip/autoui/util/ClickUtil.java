package com.tchip.autoui.util;

public class ClickUtil {

	private static long lastClickTime;

	/**
	 * @param clickMinSpan
	 *            两次点击至少间隔时间,单位:ms
	 * @return
	 */
	public static boolean isQuickClick(int clickMinSpan) {
		long time = System.currentTimeMillis();
		long timeD = time - lastClickTime;
		if (0 < timeD && timeD < clickMinSpan) {
			MyLog.v("ClickUtil.isQuickClick:Click Too Quickly!");
			return true;
		}
		lastClickTime = time;
		return false;
	}

	private static long lastSaveLogTime;

	public static boolean isSaveLogTooQuick(int runMinSpan) {
		long time = System.currentTimeMillis();
		long timeD = time - lastSaveLogTime;
		if (0 < timeD && timeD < runMinSpan) {
			MyLog.v("ClickUtil.isSaveLogTooQuick,Run Too Quickly!");
			return true;
		}
		lastSaveLogTime = time;
		return false;
	}

	private static long lastUpdateWeatherTime;

	public static boolean isUpdateWeatherQuick(int runMinSpan) {
		long time = System.currentTimeMillis();
		long timeD = time - lastUpdateWeatherTime;
		if (0 < timeD && timeD < runMinSpan) {
			MyLog.v("[ClickUtil]isUpdateWeatherQuick,Run Too Quickly!");
			return true;
		}
		lastUpdateWeatherTime = time;
		return false;
	}
	
	private static long lastSendKeyTime;

	public static boolean isSendKeyTooQuick(int runMinSpan) {
		long time = System.currentTimeMillis();
		long timeD = time - lastSendKeyTime;
		if (0 < timeD && timeD < runMinSpan) {
			MyLog.v("ClickUtil.isSendKeyTooQuick,Run Too Quickly!");
			return true;
		}
		lastSendKeyTime = time;
		return false;
	}

}
