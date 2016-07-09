package com.tchip.autoui;

import com.tchip.autoui.util.MyUncaughtExceptionHandler;

import android.app.Application;

public class MyApp extends Application {

	/** ACC是否连接 */
	public static boolean isAccOn = true;
	
	/** 是否处于低功耗待机状态 */
	public static boolean isSleeping = false;
	
	/** 正在执行休眠确认 */
	public static boolean isSleepConfirm = false;

	@Override
	public void onCreate() {
		super.onCreate();

		MyUncaughtExceptionHandler myUncaughtExceptionHandler = MyUncaughtExceptionHandler
				.getInstance();
		myUncaughtExceptionHandler.init(getApplicationContext());

	}

}
