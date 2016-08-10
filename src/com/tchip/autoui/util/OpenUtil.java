package com.tchip.autoui.util;

import java.lang.reflect.Method;
import java.util.List;

import com.tchip.autoui.util.ProviderUtil.Name;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Instrumentation;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.view.KeyEvent;

public class OpenUtil {

	public enum MODULE_TYPE {
		/** 云中心 */
		CLOUD_CENTER,

		/** 云中心-拨号 */
		CLOUD_DIALER,

		/** 云中心-一键接人 */
		CLOUD_PICK,

		/** 设备测试 */
		DEVICE_TEST,

		/** 拨号 */
		DIALER,

		/** 电子狗 */
		EDOG,

		/** 工程模式 */
		ENGINEER_MODE,

		/** 文件管理(MTK) */
		FILE_MANAGER_MTK,

		/** FM发射 */
		FMTRANSMIT,

		/** 图库 */
		GALLERY,

		/** 短信 */
		MMS,

		/** 多媒体 */
		MULTIMEDIA,

		/** 在线音乐 */
		MUSIC,

		/** MTKLogger */
		MTK_LOGGER,

		/** 导航:高德地图 */
		NAVI_GAODE,

		/** 导航:高德地图车机版 */
		NAVI_GAODE_CAR,

		/** 导航:高德地图车镜版 */
		NAVI_GAODE_CAR_MIRROR,

		/** 系统升级 */
		OTA,

		/** 行车记录 */
		RECORD,

		/** 设置 */
		SETTING,

		/** 关于 */
		SETTING_ABOUT,

		/** 应用 */
		SETTING_APP,

		/** 流量使用情况 */
		SETTING_DATA_USAGE,

		/** 日期和时间 */
		SETTING_DATE,

		/** 显示设置 */
		SETTING_DISPLAY,

		/** FM发射设置 */
		SETTING_FM,

		/** 位置 */
		SETTING_LOCATION,

		/** 音量设置 */
		SETTING_VOLUME,

		/** 备份和重置 */
		SETTING_RESET,

		/** 存储设置 */
		SETTING_STORAGE,

		/** 系统设置 */
		SETTING_SYSTEM,

		/** 视频 */
		VIDEO,

		/** 天气 */
		WEATHER,

		/** 微信助手 */
		WECHAT,

		/** 翼卡 */
		YIKA,

		/** Wi-Fi */
		WIFI,

		/** Wi-Fi热点 */
		WIFI_AP,

		/** 喜马拉雅 */
		XIMALAYA,

		MTK_YGPS,

		/** 优酷 */
		YOUKU

	}

	public static void openModule(Activity activity, MODULE_TYPE moduleTye) {
		if (!ClickUtil.isQuickClick(1000)) {
			try {
				switch (moduleTye) {
				case CLOUD_CENTER:
					activity.sendBroadcast(new Intent(
							"tchip.intent.action.ACTION_GPS_ON")); // 打开GPS
					startAppbyPackage(activity,
							"com.hdsc.monitor.heart.monitorvoice");
					break;

				case CLOUD_DIALER:
					activity.sendBroadcast(new Intent(
							"tchip.intent.action.ACTION_GPS_ON")); // 打开GPS
					Intent intentCloudDialer = new Intent(Intent.ACTION_VIEW);
					intentCloudDialer.setClassName(
							"com.hdsc.monitor.heart.monitorvoice",
							"com.hdsc.monitor.heart.monitorvoice.MainActivity");
					intentCloudDialer.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
							| Intent.FLAG_ACTIVITY_TASK_ON_HOME);
					activity.startActivity(intentCloudDialer);
					break;

				case CLOUD_PICK:
					activity.sendBroadcast(new Intent(
							"tchip.intent.action.ACTION_GPS_ON")); // 打开GPS
					Intent intenCloudPick = new Intent(Intent.ACTION_VIEW);
					intenCloudPick.setClassName(
							"com.hdsc.monitor.heart.monitorvoice",
							"com.hdsc.monitor.heart.monitorvoice.JJJRActivity");
					intenCloudPick.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
							| Intent.FLAG_ACTIVITY_TASK_ON_HOME);
					activity.startActivity(intenCloudPick);
					break;

				case DEVICE_TEST:
					Intent intentDeviceTest = new Intent(Intent.ACTION_VIEW);
					intentDeviceTest.setClassName("com.DeviceTest",
							"com.DeviceTest.DeviceTest");
					intentDeviceTest.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
							| Intent.FLAG_ACTIVITY_TASK_ON_HOME);
					activity.startActivity(intentDeviceTest);
					break;

				case DIALER:
					ComponentName componentDialer = new ComponentName(
							"com.goodocom.gocsdk",
							"com.tchip.call.MainActivity");
					Intent intentDialer = new Intent();
					intentDialer.setComponent(componentDialer);
					intentDialer.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
							| Intent.FLAG_ACTIVITY_TASK_ON_HOME);
					activity.startActivity(intentDialer);
					break;

				case ENGINEER_MODE:
					Intent intentEngineerMode = new Intent(Intent.ACTION_VIEW);
					intentEngineerMode.setClassName(
							"com.mediatek.engineermode",
							"com.mediatek.engineermode.EngineerMode");
					intentEngineerMode.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
							| Intent.FLAG_ACTIVITY_TASK_ON_HOME);
					activity.startActivity(intentEngineerMode);
					break;

				case EDOG:
					// SettingUtil.setEDogEnable(true);
					activity.sendBroadcast(new Intent(
							"tchip.intent.action.ACTION_GPS_ON")); // 打开GPS
					// ComponentName componentEDog = new ComponentName(
					// "com.nengzhong.app.activity",
					// "com.nengzhong.app.activity.DogActivity");
					ComponentName componentEDog = new ComponentName(
							"entry.dsa2014", "entry.dsa2014.MainActivity");
					Intent intentEDog = new Intent();
					intentEDog.setComponent(componentEDog);
					intentEDog.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
							| Intent.FLAG_ACTIVITY_TASK_ON_HOME);
					activity.startActivity(intentEDog);
					break;

				case FILE_MANAGER_MTK:
					ComponentName componentFileMtk = new ComponentName(
							"com.mediatek.filemanager",
							"com.mediatek.filemanager.FileManagerOperationActivity");
					Intent intentFileMtk = new Intent();
					intentFileMtk.setComponent(componentFileMtk);
					intentFileMtk.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
							| Intent.FLAG_ACTIVITY_TASK_ON_HOME);
					activity.startActivity(intentFileMtk);
					break;

				case FMTRANSMIT:
					ComponentName componentFM = new ComponentName(
							"com.tchip.autofm",
							"com.tchip.autofm.ui.MainActivity");
					Intent intentFM = new Intent();
					intentFM.setComponent(componentFM);
					intentFM.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
							| Intent.FLAG_ACTIVITY_TASK_ON_HOME);
					activity.startActivity(intentFM);
					break;

				case MULTIMEDIA:
				case GALLERY:
					ComponentName componentImage = new ComponentName(
							"com.android.gallery3d",
							"com.android.gallery3d.app.GalleryActivity");
					Intent intentImage = new Intent();
					intentImage.setComponent(componentImage);
					intentImage.addCategory(Intent.CATEGORY_LAUNCHER);
					intentImage.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
							| Intent.FLAG_ACTIVITY_TASK_ON_HOME);
					activity.startActivity(intentImage);
					break;

				case MMS:
					ComponentName componentMessage = new ComponentName(
							"com.android.mms",
							"com.android.mms.ui.BootActivity");
					Intent intentMessage = new Intent();
					intentMessage.setComponent(componentMessage);
					intentMessage.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
							| Intent.FLAG_ACTIVITY_TASK_ON_HOME);
					activity.startActivity(intentMessage);
					break;

				case MUSIC:
					ComponentName componentMusic;
					// 普通HD版："cn.kuwo.kwmusichd","cn.kuwo.kwmusichd.WelcomeActivity"
					// 车载HD版："cn.kuwo.kwmusiccar","cn.kuwo.kwmusiccar.WelcomeActivity"
					componentMusic = new ComponentName("cn.kuwo.kwmusiccar",
							"cn.kuwo.kwmusiccar.WelcomeActivity");
					Intent intentMusic = new Intent();
					intentMusic.setComponent(componentMusic);
					intentMusic.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
							| Intent.FLAG_ACTIVITY_TASK_ON_HOME);
					activity.startActivity(intentMusic);
					break;

				case MTK_LOGGER:
					Intent intentMtkLogger = new Intent(Intent.ACTION_VIEW);
					intentMtkLogger.setClassName("com.mediatek.mtklogger",
							"com.mediatek.mtklogger.MainActivity");
					intentMtkLogger.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
							| Intent.FLAG_ACTIVITY_TASK_ON_HOME);
					activity.startActivity(intentMtkLogger);
					break;

				case NAVI_GAODE:
					activity.sendBroadcast(new Intent(
							"tchip.intent.action.ACTION_GPS_ON")); // 打开GPS
					ComponentName componentGaodeMobile = new ComponentName(
							"com.autonavi.minimap",
							"com.autonavi.map.activity.SplashActivity");
					Intent intentGaodeMobile = new Intent();
					intentGaodeMobile.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
							| Intent.FLAG_ACTIVITY_TASK_ON_HOME);
					intentGaodeMobile.setComponent(componentGaodeMobile);
					activity.startActivity(intentGaodeMobile);
					break;

				case NAVI_GAODE_CAR:
					activity.sendBroadcast(new Intent(
							"tchip.intent.action.ACTION_GPS_ON")); // 打开GPS
					ComponentName componentGaodeCar;
					componentGaodeCar = new ComponentName(
							"com.autonavi.amapauto",
							"com.autonavi.auto.MainMapActivity");
					Intent intentGaodeCar = new Intent();
					intentGaodeCar.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
							| Intent.FLAG_ACTIVITY_TASK_ON_HOME);
					intentGaodeCar.setComponent(componentGaodeCar);
					activity.startActivity(intentGaodeCar);
					break;

				case NAVI_GAODE_CAR_MIRROR:
					activity.sendBroadcast(new Intent(
							"tchip.intent.action.ACTION_GPS_ON")); // 打开GPS
					ComponentName componentGaodeCarMirror;
					componentGaodeCarMirror = new ComponentName(
							"com.autonavi.amapautolite",
							"com.autonavi.auto.remote.fill.UsbFillActivity");
					Intent intentGaodeCarMirror = new Intent();
					intentGaodeCarMirror.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
							| Intent.FLAG_ACTIVITY_TASK_ON_HOME);
					intentGaodeCarMirror.setComponent(componentGaodeCarMirror);
					activity.startActivity(intentGaodeCarMirror);
					break;

				case OTA:
					Intent intentSettingOTA = new Intent(Intent.ACTION_VIEW);
					intentSettingOTA.setClassName("com.tchipota",
							"com.tchipota.MainActivity");
					intentSettingOTA.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
							| Intent.FLAG_ACTIVITY_TASK_ON_HOME);
					activity.startActivity(intentSettingOTA);
					break;

				case RECORD: {
					ComponentName componentRecord = new ComponentName(
							"com.tchip.autorecord",
							"com.tchip.autorecord.ui.MainActivity");
					Intent intentRecord = new Intent();
					intentRecord.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
							| Intent.FLAG_ACTIVITY_TASK_ON_HOME);
					intentRecord.setComponent(componentRecord);
					activity.startActivity(intentRecord);
				}
					break;

				case SETTING:
					ComponentName componentSetting = new ComponentName(
							"com.tchip.autosetting",
							"com.tchip.autosetting.ui.MainActivity");
					Intent intentSetting = new Intent();
					intentSetting.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
							| Intent.FLAG_ACTIVITY_TASK_ON_HOME);
					intentSetting.setComponent(componentSetting);
					activity.startActivity(intentSetting);
					break;

				case SETTING_ABOUT:
					activity.startActivity(new Intent(
							android.provider.Settings.ACTION_DEVICE_INFO_SETTINGS));
					break;

				case SETTING_APP:
					activity.startActivity(new Intent(
							android.provider.Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS));
					break;

				case SETTING_DATA_USAGE:
					activity.startActivity(new Intent(
							"android.settings.DATA_USAGE_SETTINGS"));
					break;

				case SETTING_DATE:
					activity.startActivity(new Intent(
							android.provider.Settings.ACTION_DATE_SETTINGS));
					break;

				case SETTING_FM:
					activity.startActivity(new Intent(
							"android.settings.FM_SETTINGS"));
					break;

				case SETTING_LOCATION:
					activity.startActivity(new Intent(
							android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
					break;

				case SETTING_RESET:
					activity.startActivity(new Intent(
							"android.settings.BACKUP_AND_RESET_SETTINGS"));
					break;

				case SETTING_STORAGE:
					activity.startActivity(new Intent(
							android.provider.Settings.ACTION_MEMORY_CARD_SETTINGS));
					break;

				case SETTING_SYSTEM:
					ComponentName componentSettingSystem = new ComponentName(
							"com.android.settings",
							"com.android.settings.Settings");
					Intent intentSettingSystem = new Intent();
					intentSettingSystem.setComponent(componentSettingSystem);
					intentSettingSystem.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
							| Intent.FLAG_ACTIVITY_TASK_ON_HOME);
					activity.startActivity(intentSettingSystem);
					break;

				case VIDEO:
					ComponentName componentVideo = new ComponentName(
							"com.mediatek.videoplayer",
							"com.mediatek.videoplayer.MovieListActivity");
					Intent intentVideo = new Intent();
					intentVideo.setComponent(componentVideo);
					intentVideo.addCategory(Intent.CATEGORY_DEFAULT);
					intentVideo.addCategory(Intent.CATEGORY_LAUNCHER);
					intentVideo.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
							| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
					activity.startActivity(intentVideo);
					break;

				case WEATHER:
					ComponentName componentWeather;
					componentWeather = new ComponentName("com.tchip.weather",
							"com.tchip.weather.ui.MainActivity");
					Intent intentWeather = new Intent();
					intentWeather.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
							| Intent.FLAG_ACTIVITY_TASK_ON_HOME);
					intentWeather.setComponent(componentWeather);
					activity.startActivity(intentWeather);
					break;

				case WECHAT:
					ComponentName componentWechat;
					componentWechat = new ComponentName("com.txznet.webchat",
							"com.txznet.webchat.ui.AppStartActivity");
					Intent intentWechat = new Intent();
					intentWechat.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
							| Intent.FLAG_ACTIVITY_TASK_ON_HOME);
					intentWechat.setComponent(componentWechat);
					activity.startActivity(intentWechat);
					break;

				case YIKA:
					ComponentName componentYika;
					componentYika = new ComponentName("com.coagent.ecar",
							"com.coagent.ecarnet.car.activity.WelcomeActivity");
					Intent intentYika = new Intent();
					intentYika.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
							| Intent.FLAG_ACTIVITY_TASK_ON_HOME);
					intentYika.setComponent(componentYika);
					activity.startActivity(intentYika);
					break;

				case WIFI:
					activity.startActivity(new Intent(
							android.provider.Settings.ACTION_WIFI_SETTINGS));
					break;

				case WIFI_AP:
					activity.startActivity(new Intent(
							"android.settings.TETHER_WIFI_SETTINGS"));
					break;

				case XIMALAYA:
					ComponentName componentXimalaya;
					componentXimalaya = new ComponentName(
							"com.ximalaya.ting.android.car",
							"com.ximalaya.ting.android.car.activity.MainActivity");
					Intent intentXimalaya = new Intent();
					intentXimalaya.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
							| Intent.FLAG_ACTIVITY_TASK_ON_HOME);
					intentXimalaya.setComponent(componentXimalaya);
					activity.startActivity(intentXimalaya);
					break;

				case MTK_YGPS:
					ComponentName componentYGPS;
					componentYGPS = new ComponentName("com.mediatek.ygps",
							"com.mediatek.ygps.YgpsActivity");
					Intent intentYGPS = new Intent();
					intentYGPS.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
							| Intent.FLAG_ACTIVITY_TASK_ON_HOME);
					intentYGPS.setComponent(componentYGPS);
					activity.startActivity(intentYGPS);
					break;

				case YOUKU:
					Intent intentYouku = new Intent();
					ComponentName componentYouku = new ComponentName(
							"com.youku.phone",
							"com.youku.phone.ActivityWelcome");
					intentYouku.setComponent(componentYouku);
					intentYouku.setAction("android.intent.action.VIEW");
					intentYouku.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
							| Intent.FLAG_ACTIVITY_TASK_ON_HOME);
					activity.startActivity(intentYouku);
					break;

				default:
					break;
				}
				// activity.overridePendingTransition(
				// R.anim.zms_translate_up_out,
				// R.anim.zms_translate_up_in);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static void killApp(Context context, String app) {
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
					MyLog.v("Kill App Success:" + app);
				} catch (Exception e) {
					MyLog.v("Kill App Fail:" + app);
					e.printStackTrace();
				}
			}
		}
	}

	private static void killApp(Context context, String[] app) {
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

	public static void killAppWhenAccOff(Context context) {
		String[] arrayKillApp = { "cn.kuwo.kwmusiccar", // 酷我音乐
				"com.android.gallery3d", // 图库
				"com.autonavi.amapauto", // 高德地图（车机版）
				"com.autonavi.amapautolite", // 高德地图（车镜版）
				"com.ximalaya.ting.android.car", // 喜马拉雅（车机版）
				// "entry.dsa2014", // 电子狗
				"com.coagent.ecar", // 翼卡
				"com.hdsc.monitor.heart.monitorvoice", // 汇德思创
				"com.youku.phone", // 优酷
				"com.mediatek.filemanager", // 文件管理
				"com.tchip.autofm", // FM发射
				"com.tchip.weather" // 天气
		};
		killApp(context, arrayKillApp);
	}

	/** 返回到车前界面 */
	public static void returnWhenBackOver(Activity activity) {
		String pkgWhenBack = ProviderUtil.getValue(activity,
				Name.PKG_WHEN_BACK, "com.xxx.xxx");
		if ("com.autonavi.amapauto".equals(pkgWhenBack)) {
			openModule(activity, MODULE_TYPE.NAVI_GAODE_CAR);
		} else if ("com.autonavi.amapautolite".equals(pkgWhenBack)) {
			openModule(activity, MODULE_TYPE.NAVI_GAODE_CAR_MIRROR);
		} else if ("com.goodocom.gocsdk".equals(pkgWhenBack)) {
			openModule(activity, MODULE_TYPE.DIALER);
		} else if ("entry.dsa2014".equals(pkgWhenBack)) {
			openModule(activity, MODULE_TYPE.EDOG);
		} else if ("com.mediatek.filemanager".equals(pkgWhenBack)) {
			openModule(activity, MODULE_TYPE.FILE_MANAGER_MTK);
		} else if ("com.tchip.autofm".equals(pkgWhenBack)) {
			openModule(activity, MODULE_TYPE.FMTRANSMIT);
		} else if ("com.android.gallery3d".equals(pkgWhenBack)) {
			// FIXME:视频回放界面倒车回到图库
			// openModule(activity, MODULE_TYPE.GALLERY);
			sendKeyCode(KeyEvent.KEYCODE_HOME);
		} else if ("cn.kuwo.kwmusiccar".equals(pkgWhenBack)) {
			openModule(activity, MODULE_TYPE.MUSIC);
		} else if ("com.tchip.autosetting".equals(pkgWhenBack)
				|| "com.android.settings".equals(pkgWhenBack)) {
			openModule(activity, MODULE_TYPE.SETTING);
		} else if ("com.tchip.weather".equals(pkgWhenBack)) {
			openModule(activity, MODULE_TYPE.WEATHER);
		} else if ("com.txznet.webchat".equals(pkgWhenBack)) {
			openModule(activity, MODULE_TYPE.WECHAT);
		} else if ("com.coagent.ecar".equals(pkgWhenBack)) {
			openModule(activity, MODULE_TYPE.YIKA);
		} else if ("com.hdsc.monitor.heart.monitorvoice".equals(pkgWhenBack)) {
			openModule(activity, MODULE_TYPE.CLOUD_CENTER);
		} else if ("com.ximalaya.ting.android.car".equals(pkgWhenBack)) {
			openModule(activity, MODULE_TYPE.XIMALAYA);
		} else if ("com.youku.phone".equals(pkgWhenBack)) {
			openModule(activity, MODULE_TYPE.YOUKU);
		} else if ("com.tchipota".equals(pkgWhenBack)) {
			openModule(activity, MODULE_TYPE.OTA);
		} else if ("com.tchip.autorecord".equals(pkgWhenBack)) {

		} else if ("com.tchip.autoui".equals(pkgWhenBack)) {
			sendKeyCode(KeyEvent.KEYCODE_HOME);
		} else if ("com.mediatek.engineermode".equals(pkgWhenBack)) {
			openModule(activity, MODULE_TYPE.ENGINEER_MODE);
		} else if ("com.mediatek.mtklogger".endsWith(pkgWhenBack)) {
			openModule(activity, MODULE_TYPE.MTK_LOGGER);
		} else if ("com.mediatek.ygps".endsWith(pkgWhenBack)) {
			openModule(activity, MODULE_TYPE.MTK_YGPS);
		} else {
			startAppbyPackage(activity, pkgWhenBack);
		}
	}

	private static void startAppbyPackage(Context context, String packageName) {
		PackageManager packageManager = context.getPackageManager();
		Intent intent = packageManager.getLaunchIntentForPackage(packageName);
		if (intent != null) {
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
					| Intent.FLAG_ACTIVITY_TASK_ON_HOME);
			context.startActivity(intent);
		} else {
			sendKeyCode(KeyEvent.KEYCODE_HOME);
			MyLog.e("OpenUtil.startAppbyPackage: LaunchIntent is null,pkg:"
					+ packageName);
		}
	}

	private static void sendKeyCode(final int keyCode) {
		new Thread() {
			public void run() {
				try {
					Instrumentation inst = new Instrumentation();
					inst.sendKeyDownUpSync(keyCode);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

}
