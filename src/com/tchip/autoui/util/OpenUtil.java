package com.tchip.autoui.util;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;

public class OpenUtil {

	public enum MODULE_TYPE {
		/** 语音助手 */
		CHAT,

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

		/** 文件管理 */
		FILE_EXPLORER,

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

		/** 导航:百度SDK */
		NAVI_BAIDU_SDK,

		/** 导航:高德地图 */
		NAVI_GAODE,

		/** 导航:高德地图车机版 */
		NAVI_GAODE_CAR,

		/** 导航：图吧 */
		NAVI_TUBA,

		/** 行车记录 */
		RECORD,

		RECORD_BACK,

		/** 轨迹 */
		ROUTE,

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
		XIMALAYA
	}

	public static void openModule(Activity activity, MODULE_TYPE moduleTye) {
		if (!ClickUtil.isQuickClick(1000)) {
			try {
				switch (moduleTye) {
				case CHAT:
					break;

				case CLOUD_CENTER:
					activity.sendBroadcast(new Intent(
							"tchip.intent.action.ACTION_GPS_ON")); // 打开GPS
					Intent intentCloudCenter = new Intent(Intent.ACTION_VIEW);
					intentCloudCenter
							.setClassName(
									"com.hdsc.monitor.heart.monitorvoice",
									"com.hdsc.monitor.heart.monitorvoice.CloudCenterActivity");
					intentCloudCenter.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
							| Intent.FLAG_ACTIVITY_TASK_ON_HOME);
					activity.startActivity(intentCloudCenter);
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

				case FILE_EXPLORER:
					ComponentName componentFileExplorer = new ComponentName(
							"com.tchip.filemanager",
							"com.tchip.filemanager.ui.activity.MainActivity");
					Intent intentFileExplorer = new Intent();
					intentFileExplorer.setComponent(componentFileExplorer);
					activity.startActivity(intentFileExplorer);
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

				// case MULTIMEDIA:
				// Intent intentMultimedia = new Intent(activity,
				// MultimediaActivity.class);
				// activity.startActivity(intentMultimedia);
				// break;

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

				case NAVI_BAIDU_SDK:
					if (TelephonyUtil.isNetworkConnected(activity)) {
						// 打开GPS
						activity.sendBroadcast(new Intent(
								"tchip.intent.action.ACTION_GPS_ON"));

						ComponentName componentBaiduNavi;
						componentBaiduNavi = new ComponentName(
								"com.tchip.baidunavi",
								"com.tchip.baidunavi.ui.activity.MainActivity");
						Intent intentBaiduNavi = new Intent();
						intentBaiduNavi.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
								| Intent.FLAG_ACTIVITY_TASK_ON_HOME);
						intentBaiduNavi.setComponent(componentBaiduNavi);
						activity.startActivity(intentBaiduNavi);
					} else {
						// 无网络
					}
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

				case NAVI_TUBA:
					activity.sendBroadcast(new Intent(
							"tchip.intent.action.ACTION_GPS_ON")); // 打开GPS
					ComponentName componentTuba;
					componentTuba = new ComponentName(
							"com.mapbar.android.carnavi",
							"com.mapbar.android.carnavi.activity.LoadingActivity");
					Intent intentTubaNavi = new Intent();
					intentTubaNavi.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
							| Intent.FLAG_ACTIVITY_TASK_ON_HOME);
					intentTubaNavi.setComponent(componentTuba);
					activity.startActivity(intentTubaNavi);
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

				case RECORD_BACK:
					ComponentName componentRecord = new ComponentName(
							"com.tchip.autorecordback",
							"com.tchip.autorecordback.ui.MainActivity");
					Intent intentRecord = new Intent();
					intentRecord.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
							| Intent.FLAG_ACTIVITY_TASK_ON_HOME);
					intentRecord.setComponent(componentRecord);
					activity.startActivity(intentRecord);
					break;

				case ROUTE:
					ComponentName componentRoute = new ComponentName(
							"com.tchip.route",
							"com.tchip.route.ui.activity.MainActivity");
					Intent intentRoute = new Intent();
					intentRoute.setComponent(componentRoute);
					intentRoute.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
							| Intent.FLAG_ACTIVITY_TASK_ON_HOME);
					activity.startActivity(intentRoute);
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
					Intent intentXimaLaya = new Intent();
					// ComponentName comp = new ComponentName(
					// "com.ximalaya.ting.android",
					// "com.ximalaya.ting.android.activity.login.WelcomeActivity");
					ComponentName componentXimalaya = new ComponentName(
							"com.ximalaya.ting.android.car",
							"com.ximalaya.ting.android.car.activity.WelcomeActivity");
					intentXimaLaya.setComponent(componentXimalaya);
					intentXimaLaya.setAction("android.intent.action.VIEW");
					intentXimaLaya.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
							| Intent.FLAG_ACTIVITY_TASK_ON_HOME);
					activity.startActivity(intentXimaLaya);
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
}
