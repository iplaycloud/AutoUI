package com.tchip.autoui;

import com.tchip.autoui.util.MyUncaughtExceptionHandler;

import android.app.Application;

public class MyApp extends Application {

	/** ACC是否连接 */
	public static boolean isAccOn = true;

	@Override
	public void onCreate() {
		super.onCreate();

		MyUncaughtExceptionHandler myUncaughtExceptionHandler = MyUncaughtExceptionHandler
				.getInstance();
		myUncaughtExceptionHandler.init(getApplicationContext());

	}

}
