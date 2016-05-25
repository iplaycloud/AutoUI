package com.tchip.autoui.ui;

import java.util.Calendar;
import java.util.Locale;

import com.tchip.autoui.Constant;
import com.tchip.autoui.MyApp;
import com.tchip.autoui.R;
import com.tchip.autoui.util.HintUtil;
import com.tchip.autoui.util.MyLog;
import com.tchip.autoui.util.OpenUtil;
import com.tchip.autoui.util.OpenUtil.MODULE_TYPE;
import com.tchip.autoui.util.ProviderUtil;
import com.tchip.autoui.util.ProviderUtil.Name;
import com.tchip.autoui.util.SettingUtil;
import com.tchip.autoui.util.StorageUtil;
import com.tchip.autoui.util.TypefaceUtil;
import com.tchip.autoui.util.WeatherUtil;
import com.tchip.autoui.view.MyScrollView;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextClock;
import android.widget.TextView;

public class MainActivity extends Activity {
	private Context context;
	private TextToSpeech textToSpeech;

	private MyScrollView scrollView;
	private ImageView imageWeatherInfo;
	private TextView textWeatherInfo, textWeatherTmpRange, textWeatherCity;
	private ImageView imageRecordState;
	private TextView textRecStateFront, textRecStateBack;
	private TextView textFrequency; // FM发射频率
	private ImageView imageFMState; // FM发射状态

	/** 剩余空间 */
	private TextView textLeftStorage;
	/** 总空间 */
	private TextView textTotalStorage;

	/** UI主线程Handler */
	private Handler mainHandler;

	/** 非UI任务线程 */
	private static final HandlerThread taskHandlerThread = new HandlerThread(
			"ui-task-thread");
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
		textToSpeech = new TextToSpeech(context, new MyTTSOnInitListener());
		mainHandler = new Handler(this.getMainLooper());

		initialLayout();

		getContentResolver()
				.registerContentObserver(
						Uri.parse("content://com.tchip.provider.AutoProvider/state/name/"),
						true, new AutoContentObserver(new Handler()));

		mainReceiver = new MainReceiver();
		IntentFilter mainFilter = new IntentFilter();
		mainFilter.addAction(Constant.Broadcast.ACC_ON);
		mainFilter.addAction(Constant.Broadcast.ACC_OFF);
		mainFilter.addAction(Constant.Broadcast.TTS_SPEAK);
		mainFilter.addAction(Intent.ACTION_TIME_TICK);
		registerReceiver(mainReceiver, mainFilter);

		// SettingUtil.initialNodeState(MainActivity.this); // FIXME

		// TODO:Start AutoRecord
		startAutoRecord("autoui_oncreate");
	}

	@Override
	protected void onResume() {
		sendBroadcast(new Intent(Constant.Broadcast.STATUS_SHOW)); // 显示状态栏
		// scrollView.smoothScrollTo(0, 0); // 返回第一个图标
		updateFileInfo();
		updateWeatherInfo();
		updateRecordInfo();
		updateFMTransmitInfo();
		super.onResume();
	}

	@Override
	protected void onPause() {
		sendBroadcast(new Intent(Constant.Broadcast.STATUS_HIDE)); // 隐藏状态栏
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		if (mainReceiver != null) {
			unregisterReceiver(mainReceiver);
		}

		if (textToSpeech != null) { // 关闭TTS引擎
			textToSpeech.shutdown();
		}
		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return true;
		} else
			return super.onKeyDown(keyCode, event);
	}

	private void startAutoRecord(String reason) {
		ComponentName componentRecord = new ComponentName(
				"com.tchip.autorecord", "com.tchip.autorecord.ui.MainActivity");
		Intent intentRecord = new Intent();
		intentRecord.putExtra("time", System.currentTimeMillis());
		intentRecord.putExtra("reason", reason);
		intentRecord.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intentRecord.setComponent(componentRecord);
		startActivity(intentRecord);
	}

	class MyTTSOnInitListener implements OnInitListener {

		@Override
		public void onInit(int status) {
			// tts.setEngineByPackageName("com.iflytek.vflynote");
			textToSpeech.setLanguage(Locale.CHINESE);
		}

	}

	private void speakVoice(String content) {
		MyLog.v("[AutoUI]speakVoice:" + content);
		textToSpeech.speak(content, TextToSpeech.QUEUE_FLUSH, null, content);
	}

	private MainReceiver mainReceiver;

	private class MainReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			MyLog.v("[AutoUI.Main.MainReceiver]action:" + action);
			if (Constant.Broadcast.ACC_ON.equals(action)) {
				ProviderUtil.setValue(context, Name.ACC_STATE, "1");
				SettingUtil.setGpsState(MainActivity.this, true); // 打开GPS //
																	// FIXME
				SettingUtil.setAirplaneMode(MainActivity.this, false); // 飞行模式

			} else if (Constant.Broadcast.ACC_OFF.equals(action)) {
				ProviderUtil.setValue(context, Name.ACC_STATE, "0");
				SettingUtil.setGpsState(MainActivity.this, false); // 关闭GPS //
																	// FIXME
				SettingUtil.setAirplaneMode(MainActivity.this, true); // 飞行模式

			} else if (Constant.Broadcast.TTS_SPEAK.equals(action)) {
				String content = intent.getExtras().getString("content");
				if (null != content && content.trim().length() > 0) {
					speakVoice(content);
				}
			} else if (Intent.ACTION_TIME_TICK.equals(action)) {
				Calendar calendar = Calendar.getInstance(); // 获取时间
				int minute = calendar.get(Calendar.MINUTE);
				if (minute == 0) {
					int year = calendar.get(Calendar.YEAR);
					MyLog.v("[TimeTickReceiver]Year:" + year);

					int hour = calendar.get(Calendar.HOUR_OF_DAY);
					if (MyApp.isAccOn) { // ACC_ON
						if (year >= 2016) {
							if (1 == SettingUtil.getAccStatus()) { // 再次确认
								speakVoice("整点报时:" + hour + "点整");
							}
						}
					} else { // ACC_OFF
						if (hour == 3) { // 凌晨3点重启机器
							context.sendBroadcast(new Intent(
									"tchip.intent.action.ACTION_REBOOT"));
						}
					}
				}
			}
		}

	}

	/** 初始化布局 */
	private void initialLayout() {
		MyOnClickListener myOnClickListener = new MyOnClickListener();
		scrollView = (MyScrollView) findViewById(R.id.scrollView);
		// 时钟
		TextClock textClockDate = (TextClock) findViewById(R.id.textClockDate);
		textClockDate.setTypeface(TypefaceUtil.get(this, Constant.Path.FONT
				+ "Font-Helvetica-Neue-LT-Pro.otf"));
		// 行车记录
		RelativeLayout layoutRecord = (RelativeLayout) findViewById(R.id.layoutRecord);
		layoutRecord.setOnClickListener(myOnClickListener);
		imageRecordState = (ImageView) findViewById(R.id.imageRecordState);
		imageRecordState.setOnClickListener(myOnClickListener);
		textRecStateFront = (TextView) findViewById(R.id.textRecStateFront);
		textRecStateBack = (TextView) findViewById(R.id.textRecStateBack);
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
		textFrequency = (TextView) findViewById(R.id.textFrequency);
		imageFMState = (ImageView) findViewById(R.id.imageFMState);
		imageFMState.setOnClickListener(myOnClickListener);
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

			case R.id.imageFMState:
				changeFMState();
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

	/** 更改FM发射状态 */
	private void changeFMState() {
		Message msgChangeFmState = new Message();
		msgChangeFmState.what = 9;
		taskHandler.sendMessage(msgChangeFmState);
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
				final String recStateFront = ProviderUtil.getValue(context,
						Name.REC_FRONT_STATE);
				final String recStateBack = ProviderUtil.getValue(context,
						Name.REC_BACK_STATE);
				mainHandler.post(new Runnable() {

					@Override
					public void run() {
						if (recStateFront != null
								&& recStateFront.trim().length() > 0) {
							if ("1".equals(recStateFront)) {
								textRecStateFront
										.setText(getResources().getString(
												R.string.rec_state_front_on));
							} else {
								textRecStateFront
										.setText(getResources().getString(
												R.string.rec_state_front_off));
							}
						}
						if (recStateBack != null
								&& recStateBack.trim().length() > 0) {
							if ("1".equals(recStateBack)) {
								textRecStateBack.setText(getResources()
										.getString(R.string.rec_state_back_on));
							} else {
								textRecStateBack
										.setText(getResources().getString(
												R.string.rec_state_back_off));
							}
						}

					}
				});
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
				// String fmStateConfig = ProviderUtil.getValue(context,
				// Name.FM_TRANSMIT_STATE);
				boolean isFmOnNode = SettingUtil.isFmTransmitOnNode();
				String fmFrequencyConfig = ProviderUtil.getValue(context,
						Name.FM_TRANSMIT_FREQ);
				int fmFreqencyNode = SettingUtil.getFmFrequcenyNode(context);

				final String textFrequencyContent; // 发射频率
				if (null != fmFrequencyConfig
						&& fmFrequencyConfig.trim().length() > 0) {
					int intFreqConfig = Integer.parseInt(fmFrequencyConfig);
					if (intFreqConfig > 8750 && intFreqConfig <= 10800) {
						textFrequencyContent = "" + intFreqConfig / 100.0f;
					} else {
						textFrequencyContent = "" + fmFreqencyNode / 100.0f;
					}
				} else {
					textFrequencyContent = "" + fmFreqencyNode / 100.0f;
					if (9600 != fmFreqencyNode) {
						ProviderUtil.setValue(context, Name.FM_TRANSMIT_FREQ,
								"" + fmFreqencyNode);
					}
				}

				final boolean isImageFMStateOn; // 发射状态
				isImageFMStateOn = isFmOnNode;
				mainHandler.post(new Runnable() {

					@Override
					public void run() {
						textFrequency.setText(textFrequencyContent);
						imageFMState
								.setImageResource(isImageFMStateOn ? R.drawable.main_item_state_stop
										: R.drawable.main_item_state_play);
					}
				});
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

			case 9: // 点击按钮：更改FM发射状态
				this.removeMessages(9);
				final boolean isFmOnNow = SettingUtil.isFmTransmitOnNode();
				if (isFmOnNow) {
					SettingUtil.setFmTransmitPowerOn(context, false);
					SettingUtil.setFmTransmitConfigOn(context, false);
				} else {
					SettingUtil.setFmTransmitPowerOn(context, true);
					SettingUtil.setFmTransmitConfigOn(context, true);
				}
				mainHandler.post(new Runnable() {
					@Override
					public void run() {
						imageFMState
								.setImageResource(!isFmOnNow ? R.drawable.main_item_state_stop
										: R.drawable.main_item_state_play);

					}
				});
				this.removeMessages(9);
				break;

			}
		}
	}
}
