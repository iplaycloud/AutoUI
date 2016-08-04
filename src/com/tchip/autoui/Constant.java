package com.tchip.autoui;

public interface Constant {
	/** Debug：打印Log */
	public static final boolean isDebug = true;

	/** 日志Tag */
	public static final String TAG = "AZ";

	/** SharedPreferences */
	public static final class MySP {
		/** 名称 */
		public static final String NAME = "AutoUI";

	}

	/** 广播 */
	public static final class Broadcast {
		/** 隐藏状态栏 */
		public static final String STATUS_HIDE = "tchip.intent.action.STATUS_HIDE";
		/** 显示状态栏 */
		public static final String STATUS_SHOW = "tchip.intent.action.STATUS_SHOW";
		/** ACC上电 */
		public static final String ACC_ON = "com.tchip.ACC_ON";
		/** ACC下电 */
		public static final String ACC_OFF = "com.tchip.ACC_OFF";

		/** 停车守卫:发生碰撞 */
		public static final String GSENSOR_CRASH = "com.tchip.GSENSOR_CRASH";

		/** 倒车开始 */
		public static final String BACK_CAR_ON = "com.tchip.KEY_BACK_CAR_ON";
		/** 倒车结束 */
		public static final String BACK_CAR_OFF = "com.tchip.KEY_BACK_CAR_OFF";

		/** 隐藏格式化对话框 */
		public static final String HIDE_FORMAT_DIALOG = "com.tchip.HIDE_FORMAT_DIALOG";

		/** TTS播报,Extra:content(String) */
		public static final String TTS_SPEAK = "tchip.intent.action.TTS_SPEAK";

		public static final String POWER_OFF = "tchip.intent.action.POWER_OFF";

		public static final String SYSTEMUI_USB = "tchip.intent.action.SYSTEMUI_USB";

		public static final String KILL_APP = "tchip.intent.action.KILL_APP";

		/** 系统设置进入格式化界面 */
		public static final String MEDIA_FORMAT = "tchip.intent.action.MEDIA_FORMAT";

		/** 关掉录像：释放预览 */
		public static final String RELEASE_RECORD = "tchip.intent.action.RELEASE_RECORD";

		/** FM发射开启,通知侧边栏更新 */
		public static final String FM_ON = "tchip.intent.action.FM_ON";

		/** FM发射关闭,通知侧边栏更新 */
		public static final String FM_OFF = "tchip.intent.action.FM_OFF";

		/** 重启设备 */
		public static final String DEVICE_REBOOT = "tchip.intent.action.DEVICE_REBOOT";

		/** 熄屏 */
		public static final String CLOSE_SCREEN = "tchip.intent.action.CLOSE_SCREEN";

		/**
		 * 语音命令,Extra:command
		 * 
		 * 1.语音拍照：take_photo
		 * 
		 * 2.语音开始录像：open_dvr
		 * 
		 * 3.语音停止录像：close_dvr
		 * 
		 * 4.停车拍照：take_park_photo
		 */
		public static final String SPEECH_COMMAND = "com.tchip.SPEECH_COMMAND";

		/***** Below is OLD *****/

		/**
		 * 行车记录仪抓拍到图片之后发送以下广播,DSA接收
		 * 
		 * String[] picPaths = new String[2]; //第一张保存前置的图片路径 ；第二张保存后置的，如无可以为空
		 * 
		 * Intent intent = new Intent("com.action.http.post.picture");
		 * 
		 * intent.putExtra("picture", picPaths);
		 * 
		 * sendBroadcast(intent);
		 */
		public static final String SEND_PIC_PATH = "com.action.http.post.picture";

		/**
		 * DSA接收到广播之后进行图片的上传成功之后返回广播：
		 * 
		 * Intent intent = new Intent("dsa.action.http.picture.result");
		 * 
		 * intent.putExtra("result",1); // 0失败 1成功
		 * 
		 * sendBroadcast(intent);
		 */
		public static final String GET_PIC_RESULT = "dsa.action.http.picture.result";

		/**
		 * 照片保存广播
		 * 
		 * Extra:path
		 */
		public static final String ACTION_IMAGE_SAVE = "tchip.intent.action.ACTION_IMAGE_SAVE";

	}

	public static final class Module {
		/** 是否是公版软件 */
		public static final boolean isPublic = true;

		public static final boolean hasYouku = false;

		/** ACC不在时凌晨3点是否重启机器 */
		public static final boolean rebootAt3 = false;
	}

	public static final class UI {
		/** 公版6.86 */
		public static final String TQ6 = "TQ";
		/** 善领6.86 */
		public static final String SL6 = "SL";
		/** 公版9.76 */
		public static final String TQ9 = "TQ9";
		/** 善领9.76 */
		public static final String SL9 = "SL9";
	}

	public static final class Path {

		/** 字体目录 **/
		public static final String FONT = "fonts/";

		/** 音频通道：0-系统 1-蓝牙 */
		public static final String NODE_SWITCH_AUDIO = "/sys/bus/i2c/devices/0-007f/Spk_Choose_Num";

		/** FM开关:0-下电 1-上电 */
		public static final String NODE_FM_ENABLE = "/sys/bus/i2c/devices/2-002c/enable_qn8027";

		/** FM频率 */
		public static final String NODE_FM_FREQUENCY = "/sys/bus/i2c/devices/2-002c/setch_qn8027";

		/** 电子狗 */
		public static final String NODE_EDOG_ENABLE = "/sys/bus/i2c/devices/0-007f/EDog_enable";

		/** LED指示灯:0-全灭 1X-红灯 2X-蓝灯 100-都亮 */
		public static final String NODE_LED_CONFIG = "/sys/bus/i2c/devices/0-007f/LED_ON_OFF";

		/** ACC状态 */
		public static final String NODE_ACC_STATUS = "/sys/bus/i2c/devices/0-007f/ACC_status";

		/** SD卡插入标志:0-未插入 1-插入 */
		public static final String NODE_SD_STATUS = "/sys/bus/i2c/devices/0-007f/tf1_status";

		/**
		 * Read: 0-未倒车 1-倒车
		 * 
		 * Write：1：自动调节亮度节点开 0：关;默认打开 ；2：停车侦测开关节点打开 3：关闭（默认）
		 */
		public static final String NODE_BACK_STATUS = "/sys/bus/i2c/devices/0-007f/back_car_status";

		public static String RECORD_DIRECTORY = "/storage/sdcard1/DrivingRecord/";
		/** 前录存储路径 */
		public static String RECORD_FRONT = "/storage/sdcard1/DrivingRecord/VideoFront/";
		public static String IMAGE = "/storage/sdcard1/DrivingRecord/Image/";

		/** 后录存储路径 */
		public static String RECORD_BACK = "/storage/sdcard1/DrivingRecord/VideoBack/";
	}

}
