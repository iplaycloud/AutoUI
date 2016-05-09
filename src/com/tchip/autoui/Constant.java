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
		/** 关闭GPS */
		public static final String GPS_OFF = "tchip.intent.action.GPS_OFF";
		/** 打开GPS */
		public static final String GPS_ON = "tchip.intent.action.GPS_ON";
		/** ACC上电 */
		public static final String ACC_ON = "com.tchip.ACC_ON";
		/** ACC下电 */
		public static final String ACC_OFF = "com.tchip.ACC_OFF";
		/** TTS播报,Extra:content(String) */
		public static final String TTS_SPEAK = "tchip.intent.action.TTS_SPEAK";

		public static final String POWER_OFF = "tchip.intent.action.POWER_OFF";

		public static final String SYSTEMUI_USB = "tchip.intent.action.SYSTEMUI_USB";

		public static final String KILL_APP = "tchip.intent.action.KILL_APP";

		/** 系统设置进入格式化界面 */
		public static final String MEDIA_FORMAT = "tchip.intent.action.MEDIA_FORMAT";

		/***** Below is OLD *****/

		/** 进入休眠 */
		public static final String SLEEP_ON = "com.tchip.SLEEP_ON";

		/** 取消休眠 */
		public static final String SLEEP_OFF = "com.tchip.SLEEP_OFF";

		/** 停车守卫:发生碰撞 */
		public static final String GSENSOR_CRASH = "com.tchip.GSENSOR_CRASH";

		/** 外置蓝牙连接 */
		public static final String BT_CONNECTED = "com.tchip.BT_CONNECTED";

		/** 外置蓝牙断开 */
		public static final String BT_DISCONNECTED = "com.tchip.BT_DISCONNECTED";

		/** 蓝牙音乐播放 */
		public static final String BT_MUSIC_PLAYING = "com.tchip.BT_MUSIC_PLAYING";

		/** 蓝牙音乐停止 */
		public static final String BT_MUSIC_STOPED = "com.tchip.BT_MUSIC_STOPED";

		/**
		 * 设置同步广播,Extra:content
		 * 
		 * 1.停车守卫开关：parkOn,parkOff
		 * 
		 * 2.碰撞侦测，开关：crashOn,crashOff;灵敏度:crashLow,crashMiddle,crashHigh
		 */
		public static final String SETTING_SYNC = "com.tchip.SETTING_SYNC";

		/**
		 * 语音命令,Extra:command
		 * 
		 * 1.语音拍照：take_photo
		 * 
		 * 2.语音开始录像：open_dvr
		 * 
		 * 3.语音停止录像：close_dvr
		 */
		public static final String SPEECH_COMMAND = "com.tchip.SPEECH_COMMAND";

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

		public static boolean isIconAtom = false;

	}

	public static final class Path {

		/** 字体目录 **/
		public static final String FONT = "fonts/";

		/**
		 * 音频通道：0-系统到喇叭 1-蓝牙到喇叭 2-系统到FM 3-BT到FM
		 */
		public static final String NODE_SWITCH_AUDIO = "/sys/bus/i2c/devices/0-007f/Spk_Choose_Num";

		/** FM开关:0-下电 1-上电 */
		public static final String NODE_FM_ENABLE = "/sys/bus/i2c/devices/2-0011/enable_si4712";

		/** FM频率 */
		public static final String NODE_FM_FREQUENCY = "/sys/bus/i2c/devices/2-0011/setch_si4712";
	}

}
