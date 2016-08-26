package com.tchip.autoui.service;

import com.tchip.autoui.util.SettingUtil;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class TempMonitorService extends Service {

	private final boolean DEBUG = false;
	private final String TAG = "TEMP";

	private final int TEMP_HIGH = DEBUG ? 65 * 1000 : 115 * 1000;
	private final int TEMP_LOW = DEBUG ? 60 * 1000 : 105 * 1000;

	/** 高温时执行脚本路径 */
	private final String PATH_HIGH = "/system/xbin/gaowen";
	/** 正常时执行脚本路径 */
	private final String PATH_NORMAL = "/system/xbin/zhengchang";
	
	private final String PATH_ZONE = "/system/xbin/zone";

	private int tempFlag = 0;
	private int zoneFlag = 0;
	private boolean iszoneFlag = false;

	/** 读取cpu温度的间隔时间 */
	private int cpuReadSpan = 1000 * 5;

	/** 是否处于高温状态 */
	private boolean isHighing = false;

	Handler mHandler = new Handler();

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (DEBUG)
			Log.v(TAG, "TempMonitorService.onStartCommand");

		try {
			mHandler.removeCallbacks(readCpuTemp);
		} catch (Exception e) {
		}
		mHandler.postDelayed(readCpuTemp, cpuReadSpan);

		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		if (DEBUG)
			Log.v(TAG, "TempMonitorService.onDestroy");
	}

	Runnable readCpuTemp = new Runnable() {

		@Override
		public void run() {
			int cpuTemp = SettingUtil.getCpuTemp();
			if (cpuTemp < TEMP_LOW) {
				if (isHighing) {
					tempFlag--;
				} else if (iszoneFlag) {
					zoneFlag--;
				}else {
					zoneFlag = 0;
					tempFlag = 0;
				}
			} else if (cpuTemp > TEMP_HIGH) {
				zoneFlag = 0;
				if (!isHighing) {
					tempFlag++;
				} else {
					tempFlag = 0;
				}
			} else {					//105000 ~ 115000
				tempFlag = 0;
				zoneFlag++;
				if(zoneFlag >= 3 && !isHighing && !iszoneFlag){
					zoneFlag = 0;
					SettingUtil.executeCmd(PATH_ZONE);
					iszoneFlag = true;
				}else if(zoneFlag >= 3 || iszoneFlag){
					zoneFlag = 0;
				}
			}
			Log.v(TAG, "TEMP:" + cpuTemp + " -FALG:" + tempFlag + " -HIGH:"
					+ isHighing + " -iszoneFlag:" + iszoneFlag + " -zoneFlag:" + zoneFlag);
			if (tempFlag >= 3) {
				tempFlag = 0;
				iszoneFlag = false;
				isHighing = true;
				SettingUtil.executeCmd(PATH_HIGH);
			} else if (tempFlag <= -3) {
				tempFlag = 0;
				iszoneFlag = false;
				isHighing = false;
				SettingUtil.executeCmd(PATH_NORMAL);
			}
			if(zoneFlag <= -3){		//正常到3*1235 在变成正常的case
				zoneFlag = 0;
				iszoneFlag = false;
				isHighing = false;
				SettingUtil.executeCmd(PATH_HIGH);
				SettingUtil.executeCmd(PATH_NORMAL);
			}
			mHandler.postDelayed(readCpuTemp, cpuReadSpan);
		}

	};

}
