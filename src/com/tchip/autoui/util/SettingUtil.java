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

import com.tchip.autoui.Constant;
import com.tchip.autoui.util.ProviderUtil.Name;

import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
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
		context.sendBroadcast(new Intent(isOn ? Constant.Broadcast.FM_ON
				: Constant.Broadcast.FM_OFF));
		MyLog.v("setFmTransmitPowerOn:" + isOn);
	}

	public static File nodeEdogPower = new File(Constant.Path.NODE_EDOG_ENABLE);

	public static void setEdogPowerOn(boolean isOn) {
		SettingUtil.SaveFileToNode(nodeEdogPower, isOn ? "1" : "0");
		MyLog.v("setEdogPowerOn:" + isOn);
	}

	/**
	 * 获取设置中存取的频率
	 * 
	 * @return 8750-10800
	 */
	public static int getFmFrequcenyNode(Context context) {
		int fmFreqency = 9600; // 8800; // Default
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
				MyLog.e("SettingUtil.getLCDValue: FileNotFoundException");
			} catch (IOException e) {
				e.printStackTrace();
				MyLog.e("SettingUtil.getLCDValue: IOException");
			}
		}
		// ProviderUtil.setValue(context, Name.FM_TRANSMIT_FREQ,
		// strNodeFmChannel);
		fmFreqency = Integer.parseInt(strNodeFmChannel);

		MyLog.v("SettingUtil.getFmFrequcenyNode,fmFreqency:" + fmFreqency);
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
			MyLog.v("SettingUtil.setFmFrequencyNode success:" + frequency
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
						| PowerManager.SCREEN_DIM_WAKE_LOCK, "autoui");

		wl.acquire(); // 点亮屏幕
		wl.release(); // 释放

		KeyguardManager km = (KeyguardManager) context
				.getSystemService(Context.KEYGUARD_SERVICE); // 得到键盘锁管理器对象
		KeyguardLock kl = km.newKeyguardLock("AutoUI"); // 参数是LogCat里用的Tag
		kl.disableKeyguard();
	}

	/** ACC状态节点 */
	public static File fileAccStatus = new File(Constant.Path.NODE_ACC_STATUS);

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
				if ((ch = inputStreamReader.read()) != -1) {
					inputStreamReader.close();
					return Integer.parseInt(String.valueOf((char) ch));
				} else {
					inputStreamReader.close();
				}
			} catch (FileNotFoundException e) {
				MyLog.e("[AutoUI.SettintUtil.getFileInt] FileNotFoundException:"
						+ e.toString());
				e.printStackTrace();
			} catch (IOException e) {
				MyLog.e("[AutoUI.SettintUtil.getFileInt] IOException:"
						+ e.toString());
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
	public static File fileEDogPower = new File(Constant.Path.NODE_EDOG_ENABLE);

	/**
	 * 设置电子狗电源开关
	 * 
	 * @param isEDogOn
	 */
	public static void setEDogEnable(boolean isEDogOn) {
		MyLog.v("[SettingUtil]setEDogEnable:" + isEDogOn);
		SaveFileToNode(fileEDogPower, isEDogOn ? "1" : "0");
	}

	public static File FileLedPower = new File(Constant.Path.NODE_LED_CONFIG);

	/**
	 * LED指示灯:
	 * 
	 * 0-全灭
	 * 
	 * 10-红灯灭
	 * 
	 * 11-红灯亮
	 * 
	 * 20-蓝灯灭
	 * 
	 * 21-蓝灯亮
	 * 
	 * 100-都亮
	 * 
	 * @param ledConfig
	 */
	public static void setLedConfig(int ledConfig) {
		MyLog.v("[SettingUtil]setLedConfig:" + ledConfig);
		SaveFileToNode(FileLedPower, "" + ledConfig);
	}
	
	

	/** GPS开关 */
	public static boolean isGpsOn(Context context) {
		ContentResolver resolver = context.getContentResolver();
		boolean gpsState = Settings.Secure.isLocationProviderEnabled(resolver,
				LocationManager.GPS_PROVIDER);
		MyLog.v("[GPS]Now State:" + gpsState);
		return gpsState;
	}

	/** 设置GPS开关 */
	public static void setGpsOn(final Context context, final boolean isGpsOn) {
		ContentResolver resolver = context.getContentResolver();
		boolean nowState = isGpsOn(context);
		if (isGpsOn != nowState) {
			MyLog.v("[GPS]Set State:" + isGpsOn);
			// Settings.Secure.setLocationProviderEnabled(resolver,
			// LocationManager.GPS_PROVIDER, isGpsOn);
			int mCurrentMode = (!isGpsOn) ? Settings.Secure.LOCATION_MODE_HIGH_ACCURACY
					: Settings.Secure.LOCATION_MODE_OFF;
			int mode = isGpsOn ? Settings.Secure.LOCATION_MODE_HIGH_ACCURACY
					: Settings.Secure.LOCATION_MODE_OFF;
			Intent intent = new Intent(
					"com.android.settings.location.MODE_CHANGING");
			intent.putExtra("CURRENT_MODE", mCurrentMode);
			intent.putExtra("NEW_MODE", mode);
			context.sendBroadcast(intent,
					android.Manifest.permission.WRITE_SECURE_SETTINGS);
			Settings.Secure.putInt(resolver, Settings.Secure.LOCATION_MODE,
					mode);
		}
	}


}
