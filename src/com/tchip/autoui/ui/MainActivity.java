package com.tchip.autoui.ui;

import com.tchip.autoui.Constant;
import com.tchip.autoui.R;
import com.tchip.autoui.util.HintUtil;
import com.tchip.autoui.util.MyLog;
import com.tchip.autoui.util.OpenUtil;
import com.tchip.autoui.util.OpenUtil.MODULE_TYPE;
import com.tchip.autoui.util.ProviderUtil;
import com.tchip.autoui.util.ProviderUtil.Name;
import com.tchip.autoui.util.StorageUtil;
import com.tchip.autoui.util.TypefaceUtil;
import com.tchip.autoui.util.WeatherUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private Context context;

	private ImageView imageWeatherInfo;
	private TextView textWeatherInfo, textWeatherTmpRange, textWeatherCity;

	/** 剩余空间 */
	private TextView textLeftStorage;
	/** 总空间 */
	private TextView textTotalStorage;

	/** UI主线程Handler */
	private Handler mainHandler;

	/** 非UI任务线程 */
	private static final HandlerThread taskHandlerThread = new HandlerThread(
			"task-thread");
	static {
		taskHandlerThread.start();
	}
	private final Handler taskHandler = new TaskHandler(
			taskHandlerThread.getLooper());

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

		context = getApplicationContext();
		mainHandler = new Handler(this.getMainLooper());

		initialLayout();

		getContentResolver()
				.registerContentObserver(
						Uri.parse("content://com.tchip.provider.AutoProvider/state/name/"),
						true, new AutoContentObserver(new Handler()));
	}

	@Override
	protected void onResume() {
		sendBroadcast(new Intent(Constant.Broadcast.STATUS_SHOW)); // 显示状态栏
		updateFileInfo();
		super.onResume();
	}

	@Override
	protected void onPause() {
		sendBroadcast(new Intent(Constant.Broadcast.STATUS_HIDE)); // 隐藏状态栏
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	/** 初始化布局 */
	private void initialLayout() {
		MyOnClickListener myOnClickListener = new MyOnClickListener();
		// 时钟
		TextClock textClockDate = (TextClock) findViewById(R.id.textClockDate);
		textClockDate.setTypeface(TypefaceUtil.get(this, Constant.Path.FONT
				+ "Font-Helvetica-Neue-LT-Pro.otf"));
		// 行车记录
		RelativeLayout layoutRecord = (RelativeLayout) findViewById(R.id.layoutRecord);
		layoutRecord.setOnClickListener(myOnClickListener);
		ImageView imageRecordState = (ImageView) findViewById(R.id.imageRecordState);
		imageRecordState.setOnClickListener(myOnClickListener);
		// 导航
		RelativeLayout layoutNavigation = (RelativeLayout) findViewById(R.id.layoutNavigation);
		layoutNavigation.setOnClickListener(myOnClickListener);
		// 天气
		RelativeLayout layoutWeather = (RelativeLayout) findViewById(R.id.layoutWeather);
		layoutWeather.setOnClickListener(myOnClickListener);
		imageWeatherInfo = (ImageView) findViewById(R.id.imageWeatherInfo);
		textWeatherInfo = (TextView) findViewById(R.id.textWeatherInfo);
		textWeatherTmpRange = (TextView) findViewById(R.id.textWeatherTmpRange);
		textWeatherTmpRange.setTypeface(TypefaceUtil.get(this,
				Constant.Path.FONT + "Font-Helvetica-Neue-LT-Pro.otf"));
		textWeatherCity = (TextView) findViewById(R.id.textWeatherCity);
		// 音乐
		RelativeLayout layoutMusic = (RelativeLayout) findViewById(R.id.layoutMusic);
		layoutMusic.setOnClickListener(myOnClickListener);
		ImageView imageMusicState = (ImageView) findViewById(R.id.imageMusicState);
		imageMusicState.setOnClickListener(myOnClickListener);
		// 喜马拉雅
		RelativeLayout layoutXimalaya = (RelativeLayout) findViewById(R.id.layoutXimalaya);
		layoutXimalaya.setOnClickListener(myOnClickListener);
		ImageView imageXimalayaState = (ImageView) findViewById(R.id.imageXimalayaState);
		imageXimalayaState.setOnClickListener(myOnClickListener);
		// 电子狗
		RelativeLayout layoutEDog = (RelativeLayout) findViewById(R.id.layoutEDog);
		layoutEDog.setOnClickListener(myOnClickListener);
		// 蓝牙通话
		RelativeLayout layoutPhone = (RelativeLayout) findViewById(R.id.layoutPhone);
		layoutPhone.setOnClickListener(myOnClickListener);
		// FM发射
		RelativeLayout layoutFMTransmit = (RelativeLayout) findViewById(R.id.layoutFMTransmit);
		layoutFMTransmit.setOnClickListener(myOnClickListener);
		// 多媒体
		RelativeLayout layoutMultimedia = (RelativeLayout) findViewById(R.id.layoutMultimedia);
		layoutMultimedia.setOnClickListener(myOnClickListener);
		// 文件管理
		RelativeLayout layoutFileManager = (RelativeLayout) findViewById(R.id.layoutFileManager);
		layoutFileManager.setOnClickListener(myOnClickListener);
		textTotalStorage = (TextView) findViewById(R.id.textTotalStorage);
		textLeftStorage = (TextView) findViewById(R.id.textLeftStorage);
		// 微信助手
		RelativeLayout layoutWechat = (RelativeLayout) findViewById(R.id.layoutWechat);
		layoutWechat.setOnClickListener(myOnClickListener);
		// 微密
		RelativeLayout layoutWeme = (RelativeLayout) findViewById(R.id.layoutWeme);
		layoutWeme.setOnClickListener(myOnClickListener);
		// 设置
		RelativeLayout layoutSetting = (RelativeLayout) findViewById(R.id.layoutSetting);
		layoutSetting.setOnClickListener(myOnClickListener);
	}

	/** ContentProvder监听 */
	public class AutoContentObserver extends ContentObserver {

		public AutoContentObserver(Handler handler) {
			super(handler);
		}

		@Override
		public void onChange(boolean selfChange, Uri uri) {
			String name = uri.getLastPathSegment(); // getPathSegments().get(2);
			if (name.equals("state")) { // insert

			} else { // update
				MyLog.v("[ContentObserver]onChange,selfChange:" + selfChange
						+ ",Name:" + name);

				if (name.startsWith("weather")) { // 天气
					updateWeatherInfo();
				} else if (name.startsWith("rec")) { // 录像
					updateRecordInfo();
				} else if (name.startsWith("music")) { // 音乐
					updateMusicInfo();
				} else if (name.startsWith("bt")) { // 蓝牙
					updateBTDialerInfo();
				} else if (name.startsWith("fm")) { // FM
					updateFMTransmitInfo();
				} else if (name.startsWith("set")) { // 设置

				} else if (name.startsWith("edog")) { // EDog
					updateEDogInfo();
				}
			}
			super.onChange(selfChange, uri);
		}

		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
		}

	}

	class MyOnClickListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.layoutRecord:
				OpenUtil.openModule(MainActivity.this, MODULE_TYPE.RECORD);
				break;

			case R.id.imageRecordState:
				HintUtil.showToast(MainActivity.this, "Change Record State"); // FIXME
				break;

			case R.id.layoutNavigation:
				OpenUtil.openModule(MainActivity.this,
						MODULE_TYPE.NAVI_GAODE_CAR);
				break;

			case R.id.layoutWeather:
				OpenUtil.openModule(MainActivity.this, MODULE_TYPE.WEATHER);
				break;

			case R.id.layoutMusic:
				OpenUtil.openModule(MainActivity.this, MODULE_TYPE.MUSIC);
				break;

			case R.id.imageMusicState:
				HintUtil.showToast(MainActivity.this, "Change Music State"); // FIXME
				break;

			case R.id.layoutXimalaya:
				OpenUtil.openModule(MainActivity.this, MODULE_TYPE.XIMALAYA);
				break;

			case R.id.imageXimalayaState:
				HintUtil.showToast(MainActivity.this, "Change Ximalaya State"); // FIXME
				break;

			case R.id.layoutEDog:
				OpenUtil.openModule(MainActivity.this, MODULE_TYPE.EDOG);
				break;

			case R.id.layoutPhone:
				OpenUtil.openModule(MainActivity.this, MODULE_TYPE.DIALER);
				break;

			case R.id.layoutFMTransmit:
				OpenUtil.openModule(MainActivity.this, MODULE_TYPE.FMTRANSMIT);
				break;

			case R.id.layoutMultimedia:
				OpenUtil.openModule(MainActivity.this, MODULE_TYPE.MULTIMEDIA);
				break;

			case R.id.layoutFileManager:
				OpenUtil.openModule(MainActivity.this,
						MODULE_TYPE.FILE_MANAGER_MTK);
				break;

			case R.id.layoutWechat:
				OpenUtil.openModule(MainActivity.this, MODULE_TYPE.WECHAT);
				break;

			case R.id.layoutWeme:
				OpenUtil.openModule(MainActivity.this, MODULE_TYPE.WEME);
				break;

			case R.id.layoutSetting:
				OpenUtil.openModule(MainActivity.this, MODULE_TYPE.SETTING);
				break;

			default:
				break;
			}
		}

	}

	/** 更新录制信息 */
	private void updateRecordInfo() {
		Message msgUpdateRecord = new Message();
		msgUpdateRecord.what = 1;
		taskHandler.sendMessage(msgUpdateRecord);
	}

	/** 更新天气信息 */
	private void updateWeatherInfo() {
		Message msgUpdateWeather = new Message();
		msgUpdateWeather.what = 2;
		taskHandler.sendMessage(msgUpdateWeather);
	}

	/** 更新音乐信息 */
	private void updateMusicInfo() {
		Message msgUpdateMusic = new Message();
		msgUpdateMusic.what = 3;
		taskHandler.sendMessage(msgUpdateMusic);
	}

	/** 更新FMOL信息 */
	private void updateFMOLInfo() {
		Message msgUpdateFMOL = new Message();
		msgUpdateFMOL.what = 4;
		taskHandler.sendMessage(msgUpdateFMOL);
	}

	/** 更新电子狗信息 */
	private void updateEDogInfo() {
		Message msgUpdateEDog = new Message();
		msgUpdateEDog.what = 5;
		taskHandler.sendMessage(msgUpdateEDog);
	}

	/** 更新蓝牙电话信息 */
	private void updateBTDialerInfo() {
		Message msgUpdateBTDialer = new Message();
		msgUpdateBTDialer.what = 6;
		taskHandler.sendMessage(msgUpdateBTDialer);
	}

	/** 更新FM发射信息 */
	private void updateFMTransmitInfo() {
		Message msgUpdateFMTransmit = new Message();
		msgUpdateFMTransmit.what = 7;
		taskHandler.sendMessage(msgUpdateFMTransmit);
	}

	/** 更新文件信息 */
	private void updateFileInfo() {
		Message msgUpdateFile = new Message();
		msgUpdateFile.what = 8;
		taskHandler.sendMessage(msgUpdateFile);
	}

	class TaskHandler extends Handler {

		public TaskHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1: // 更新录制信息
				this.removeMessages(1);
				
				this.removeMessages(1);
				break;

			case 2: // 更新天气信息
				this.removeMessages(2);
				final String weatherInfo = ProviderUtil.getValue(context,
						Name.WEATHER_INFO);
				final String weatherTempLow = ProviderUtil.getValue(context,
						Name.WEATHER_TEMP_LOW);
				final String weatherTempHigh = ProviderUtil.getValue(context,
						Name.WEATHER_TEMP_HIGH);
				final String weatherCity = ProviderUtil.getValue(context,
						Name.WEATHER_LOC_CITY);
				mainHandler.post(new Runnable() {

					@Override
					public void run() {
						if (weatherInfo != null
								&& weatherInfo.trim().length() > 0) {
							imageWeatherInfo.setImageResource(WeatherUtil
									.getWeatherDrawable(WeatherUtil
											.getTypeByStr(weatherInfo)));
							textWeatherInfo.setText(weatherInfo);
						} else {
							textWeatherInfo.setText(getResources().getString(
									R.string.weather_unknown));
						}
						if (weatherTempLow != null
								&& weatherTempLow.trim().length() > 0
								&& weatherTempHigh != null
								&& weatherTempHigh.trim().length() > 0) {
							textWeatherTmpRange.setText(weatherTempLow + "~"
									+ weatherTempHigh + "℃");
						} else {
							textWeatherTmpRange.setText(getResources()
									.getString(R.string.weather_no_temp));
						}
						if (weatherCity != null
								&& weatherCity.trim().length() > 0) {
							textWeatherCity.setText(weatherCity);
						} else {
							textWeatherCity.setText(getResources().getString(
									R.string.weather_not_record));
						}

					}
				});
				this.removeMessages(2);
				break;

			case 3: // 更新音乐信息
				this.removeMessages(3);
				break;

			case 4: // 更新FMOL信息
				this.removeMessages(4);
				break;

			case 5: // 更新电子狗信息
				this.removeMessages(5);
				break;

			case 6: // 更新蓝牙电话信息
				this.removeMessages(6);
				break;

			case 7: // 更新FM发射信息
				this.removeMessages(7);
				break;

			case 8: // 更新文件信息
				this.removeMessages(8);
				final String fileTotalSize = StorageUtil.getFileTotalSizeStr();
				final String fileLeftSize = StorageUtil.getFileLeftSizeStr();
				mainHandler.post(new Runnable() {

					@Override
					public void run() {
						textTotalStorage.setText(getResources().getString(
								R.string.file_total_hint)
								+ fileTotalSize
								+ getResources().getString(R.string.file_gb));
						textLeftStorage.setText(fileLeftSize);
					}
				});
				this.removeMessages(8);
				break;

			}
		}

	}
}
