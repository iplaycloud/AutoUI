package com.tchip.autoui.util;

import com.tchip.autoui.Constant;

import android.util.Log;

public class MyLog {
	private static String MODULE = "[AutoUI]";

	public static void e(String log) {
		if (Constant.isDebug)
			Log.e(Constant.TAG, DateUtil.getTimeStr("HH:mm:ss") + MODULE + log);
	}

	public static void v(String log) {
		if (Constant.isDebug)
			Log.v(Constant.TAG, DateUtil.getTimeStr("HH:mm:ss") + MODULE + log);
	}

	public static void d(String log) {
		if (Constant.isDebug)
			Log.d(Constant.TAG, DateUtil.getTimeStr("HH:mm:ss") + MODULE + log);
	}

	public static void i(String log) {
		if (Constant.isDebug)
			Log.i(Constant.TAG, DateUtil.getTimeStr("HH:mm:ss") + MODULE + log);
	}

	public static void w(String log) {
		if (Constant.isDebug)
			Log.w(Constant.TAG, DateUtil.getTimeStr("HH:mm:ss") + MODULE + log);
	}

}
