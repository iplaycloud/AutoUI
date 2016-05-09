package com.tchip.autoui.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.List;

import com.tchip.autoui.Constant;
import com.tchip.autoui.util.ProviderUtil.Name;

import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;

public class SettingUtil {

	/** ACC 是否在 */
	public static boolean isAccOn(Context context) {
		String accState = ProviderUtil.getValue(context, Name.ACC_STATE);
		if (null != accState && accState.trim().length() > 0
				&& "1".equals(accState)) {
			return true;
		} else {
			return false;
		}
	}

	/** 设置飞行模式 */
	public static void setAirplaneMode(Context context, boolean setAirPlane) {
		MyLog.v("[SettingUtil]setAirplaneMode:" + setAirPlane);
		Settings.Global.putInt(context.getContentResolver(),
				Settings.Global.AIRPLANE_MODE_ON, setAirPlane ? 1 : 0);
		// 广播飞行模式的改变，让相应的程序可以处理。
		Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
		intent.putExtra("state", setAirPlane);
		context.sendBroadcast(intent);
	}

	public static void setGpsState(Context context, boolean isGpsOn) {
		context.sendBroadcast(new Intent(isGpsOn ? Constant.Broadcast.GPS_ON
				: Constant.Broadcast.GPS_OFF));
	}

	public static boolean getGpsState(Context context) {
		ContentResolver resolver = context.getContentResolver();
		boolean gpsState = Settings.Secure.isLocationProviderEnabled(resolver,
				LocationManager.GPS_PROVIDER);
		MyLog.v("[GPS]Now State:" + gpsState);
		return gpsState;
	}

	/**
	 * FM发射开关节点
	 * 
	 * 1：开 0：关
	 */
	public static File nodeFmEnable = new File(Constant.Path.NODE_FM_ENABLE);

	/**
	 * FM发射频率节点
	 * 
	 * 频率范围：支持7600~10800:使用8750-10800
	 */
	public static File nodeFmChannel = new File(Constant.Path.NODE_FM_FREQUENCY);

	/**
	 * FM发射是否打开:Config
	 * 
	 * @return
	 * @deprecated Using {@link #isFmTransmitPowerOn()} instead.
	 */
	public static boolean isFmTransmitConfigOn(Context context) {
		String strFmEnable = ProviderUtil.getValue(context,
				Name.FM_TRANSMIT_STATE);
		if (null != strFmEnable && strFmEnable.trim().length() > 0
				&& "1".equals(strFmEnable)) {
			return true;
		} else {
			return false;
		}
	}

	public static void setFmTransmitConfigOn(Context context, boolean isOn) {
		ProviderUtil
				.setValue(context, Name.FM_TRANSMIT_STATE, isOn ? "1" : "0");
	}

	/**
	 * FM发射是否打开:Power
	 * 
	 * @return
	 */
	public static boolean isFmTransmitOnNode() {
		return getFileInt(nodeFmEnable) == 1;
	}

	public static void setFmTransmitPowerOn(Context context, boolean isOn) {
		SettingUtil
				.SaveFileToNode(SettingUtil.nodeFmEnable, (isOn ? "1" : "0"));
		// context.sendBroadcast(new Intent(isOn ?
		// "com.tchip.FM_OPEN_CARLAUNCHER"
		// : "com.tchip.FM_CLOSE_CARLAUNCHER"));
	}

	/**
	 * 获取设置中存取的频率
	 * 
	 * @return 8750-10800
	 */
	public static int getFmFrequcenyNode(Context context) {
		int fmFreqency = 8800; // Default
		String strNodeFmChannel = "";
		if (nodeFmChannel.exists()) {
			try {
				InputStreamReader read = new InputStreamReader(
						new FileInputStream(nodeFmChannel), "utf-8");
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = null;
				while ((lineTxt = bufferedReader.readLine()) != null) {
					strNodeFmChannel += lineTxt.toString();
				}
				read.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				MyLog.e("[SettingUtil]getLCDValue: FileNotFoundException");
			} catch (IOException e) {
				e.printStackTrace();
				MyLog.e("[SettingUtil]getLCDValue: IOException");
			}
		}
		ProviderUtil.setValue(context, Name.FM_TRANSMIT_FREQ, strNodeFmChannel);
		fmFreqency = Integer.parseInt(strNodeFmChannel);

		MyLog.v("[SettingUtil]getFmFrequcenyNode,fmFreqency:" + fmFreqency);
		return fmFreqency;
	}

	/**
	 * 设置FM发射频率:8750-10800
	 * 
	 * @param frequency
	 */
	public static void setFmFrequencyNode(Context context, int frequency) {
		if (frequency >= 8750 && frequency <= 10800) {
			SaveFileToNode(nodeFmChannel, String.valueOf(frequency));
			MyLog.v("[SettingUtil]setFmFrequencyNode success:" + frequency
					/ 100.0f + "MHz");
		}
	}

	/**
	 * @return 8750-10800
	 * 
	 * @deprecated
	 */
	public static int getFmFrequencyConfig(Context context) {
		String strFrequency = ProviderUtil.getValue(context,
				Name.FM_TRANSMIT_FREQ);
		if (null != strFrequency && strFrequency.trim().length() > 0) {
			return Integer.parseInt(strFrequency);
		} else
			return 8800;
	}

	/**
	 * @param context
	 * @param frequency
	 */
	public static void setFmFrequencyConfig(Context context, int frequency) {
		if (frequency >= 8750 && frequency <= 10800) {
			ProviderUtil.setValue(context, Name.FM_TRANSMIT_FREQ, ""
					+ frequency);
			MyLog.v("[SettingUtil]setFmFrequencyConfig success:" + frequency
					/ 100.0f + "MHz");
		}
	}

	public static void SaveFileToNode(File file, String value) {
		if (file.exists()) {
			try {
				StringBuffer strbuf = new StringBuffer("");
				strbuf.append(value);
				OutputStream output = null;
				OutputStreamWriter outputWrite = null;
				PrintWriter print = null;
				try {
					output = new FileOutputStream(file);
					outputWrite = new OutputStreamWriter(output);
					print = new PrintWriter(outputWrite);
					print.print(strbuf.toString());
					print.flush();
					output.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
					Log.e(Constant.TAG, "SaveFileToNode:output error");
				}
			} catch (IOException e) {
				Log.e(Constant.TAG, "SaveFileToNode:IO Exception");
			}
		} else {
			Log.e(Constant.TAG, "SaveFileToNode:File:" + file + "not exists");
		}
	}

	/** 点亮屏幕 */
	public static void lightScreen(Context context) {
		// 获取电源管理器对象
		PowerManager pm = (PowerManager) context
				.getSystemService(Context.POWER_SERVICE);

		// 获取PowerManager.WakeLock对象,后面的参数|表示同时传入两个值,最后的是LogCat里用的Tag
		PowerManager.WakeLock wl = pm.newWakeLock(
				PowerManager.ACQUIRE_CAUSES_WAKEUP
						| PowerManager.SCREEN_DIM_WAKE_LOCK, "bright");

		wl.acquire(); // 点亮屏幕
		wl.release(); // 释放

		KeyguardManager km = (KeyguardManager) context
				.getSystemService(Context.KEYGUARD_SERVICE); // 得到键盘锁管理器对象
		KeyguardLock kl = km.newKeyguardLock("ZMS"); // 参数是LogCat里用的Tag
		kl.disableKeyguard();
	}

	/** Camera自动调节亮度节点，1：开 0：关;默认打开 */
	public static File fileAutoLightSwitch = new File(
			"/sys/devices/platform/mt-i2c.1/i2c-1/1-007f/back_car_status");

	/** 设置Camera自动调节亮度开关 */
	public static void setAutoLight(Context context, boolean isAutoLightOn) {
		SaveFileToNode(fileAutoLightSwitch, isAutoLightOn ? "1" : "0");
		MyLog.v("[SettingUtil]setAutoLight:" + isAutoLightOn);
	}

	/** 停车侦测开关节点，2：打开 3：关闭（默认） */
	public static File fileParkingMonitor = new File(
			"/sys/devices/platform/mt-i2c.1/i2c-1/1-007f/back_car_status");

	public static void setParkingMonitor(Context context, boolean isParkingOn) {
		MyLog.v("[SettingUtil]setParkingMonitor:" + isParkingOn);
		SaveFileToNode(fileParkingMonitor, isParkingOn ? "2" : "3");

		SharedPreferences sharedPreferences = context.getSharedPreferences(
				Constant.MySP.NAME, Context.MODE_PRIVATE);
		// Editor editor = sharedPreferences.edit();
		// editor.putBoolean(Constant.MySP.STR_PARKING_ON, isParkingOn);
		// editor.commit();
	}

	/** ACC状态节点 */
	public static File fileAccStatus = new File(
			"/sys/devices/platform/mt-i2c.1/i2c-1/1-007f/acc_car_status");

	/**
	 * 获取ACC状态
	 * 
	 * @return 0:ACC下电,1:ACC上电
	 */
	public static int getAccStatus() {
		return getFileInt(fileAccStatus);
	}

	public static int getFileInt(File file) {
		if (file.exists()) {
			try {
				InputStream inputStream = new FileInputStream(file);
				InputStreamReader inputStreamReader = new InputStreamReader(
						inputStream);
				int ch = 0;
				if ((ch = inputStreamReader.read()) != -1)
					return Integer.parseInt(String.valueOf((char) ch));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return 0;
	}

	/** 获取背光亮度值 */
	public static int getLCDValue() {
		File fileLCDValue = new File("/sys/class/leds/lcd-backlight/brightness"); // 背光值节点

		String strValue = "";
		if (fileLCDValue.exists()) {
			try {
				InputStreamReader read = new InputStreamReader(
						new FileInputStream(fileLCDValue), "utf-8");
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = null;
				while ((lineTxt = bufferedReader.readLine()) != null) {
					strValue += lineTxt.toString();
				}
				read.close();
				return Integer.parseInt(strValue);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				MyLog.e("[SettingUtil]getLCDValue: FileNotFoundException");
			} catch (IOException e) {
				e.printStackTrace();
				MyLog.e("[SettingUtil]getLCDValue: IOException");
			}
		}
		return -5;
	}

	/** 电子狗电源开关节点，1-打开 0-关闭 */
	public static File fileEDogPower = new File(
			"/sys/devices/platform/mt-i2c.1/i2c-1/1-007f/edog_car_status");

	/**
	 * 设置电子狗电源开关
	 * 
	 * @param isEDogOn
	 */
	public static void setEDogEnable(boolean isEDogOn) {
		MyLog.v("[SettingUtil]setEDogEnable:" + isEDogOn);
		SaveFileToNode(fileEDogPower, isEDogOn ? "1" : "0");
	}

	/** 初始化节点状态 */
	public static void initialNodeState(Context context) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				Constant.MySP.NAME, Context.MODE_PRIVATE);

		// 1.启动时初始化FM发射频率节点,频率范围：7600~10800:8750-10800

		// 2.初始化自动亮度节点

		// 3.初始化停车侦测开关

	}

	public static void killApp(Context context, String app) {
		ActivityManager myActivityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningAppProcessInfo> mRunningPros = myActivityManager
				.getRunningAppProcesses();
		for (ActivityManager.RunningAppProcessInfo amPro : mRunningPros) {
			if (amPro.processName.contains(app)) {
				try {
					Method forceStopPackage = myActivityManager
							.getClass()
							.getDeclaredMethod("forceStopPackage", String.class);
					forceStopPackage.setAccessible(true);
					forceStopPackage.invoke(myActivityManager,
							amPro.processName);
					MyLog.d("kill kuwo music ok...............");
				} catch (Exception e) {
					MyLog.d("kill kuwo music failed..............."
							+ e.toString());
				}
			}
		}
	}

	public static void killApp(Context context, String[] app) {
		ActivityManager myActivityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningAppProcessInfo> mRunningPros = myActivityManager
				.getRunningAppProcesses();
		for (ActivityManager.RunningAppProcessInfo amPro : mRunningPros) {
			for (String strApp : app) {
				if (amPro.processName.contains(strApp)) {
					try {
						Method forceStopPackage = myActivityManager.getClass()
								.getDeclaredMethod("forceStopPackage",
										String.class);
						forceStopPackage.setAccessible(true);
						forceStopPackage.invoke(myActivityManager,
								amPro.processName);
					} catch (Exception e) {
					}
				}
			}
		}
	}
}
