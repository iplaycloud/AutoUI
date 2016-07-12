package com.tchip.autoui.ui;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import cn.kuwo.autosdk.api.KWAPI;
import cn.kuwo.autosdk.api.PlayState;

import com.tchip.autoui.Constant;
import com.tchip.autoui.MyApp;
import com.tchip.autoui.R;
import com.tchip.autoui.util.HintUtil;
import com.tchip.autoui.util.MyLog;
import com.tchip.autoui.util.OpenUtil;
import com.tchip.autoui.util.ProviderUtil;
import com.tchip.autoui.util.SettingUtil;
import com.tchip.autoui.util.StorageUtil;
import com.tchip.autoui.util.TelephonyUtil;
import com.tchip.autoui.util.TypefaceUtil;
import com.tchip.autoui.util.WeatherUtil;
import com.tchip.autoui.util.OpenUtil.MODULE_TYPE;
import com.tchip.autoui.util.ProviderUtil.Name;
import com.tchip.autoui.view.TransitionViewPager;
import com.tchip.autoui.view.TransitionViewPager.TransitionEffect;
import com.tchip.autoui.view.TransitionViewPagerContainer;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Instrumentation;
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
import android.os.PowerManager;
import android.os.SystemClock;
import android.os.PowerManager.WakeLock;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.support.v4.view.PagerAdapter;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends Activity {
	private Context context;

	private View viewMain, viewVice;
	private List<View> viewList;
	private TransitionViewPager viewPager;

	private ImageView imageWeatherInfo;
	private TextView textWeatherInfo, textWeatherTmpRange, textWeatherCity;
	private ImageView imageRecordState;
	private TextView textRecStateFront, textRecStateBack;
	/** 酷我API */
	private KWAPI kuwoAPI;

	private TextToSpeech textToSpeech;
	private PowerManager powerManager;

	/** UI主线程Handler */
	private Handler mainHandler;

	private boolean isPagerOneShowed = false;
	private boolean isPagerTwoShowed = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		context = getApplicationContext();

		powerManager = (PowerManager) context
				.getSystemService(Context.POWER_SERVICE);
		textToSpeech = new TextToSpeech(context, new MyTTSOnInitListener());
		mainHandler = new Handler(this.getMainLooper());
		kuwoAPI = KWAPI.createKWAPI(this, "auto");

		setContentView(R.layout.activity_pager);

		LayoutInflater inflater = LayoutInflater.from(this);
		viewMain = inflater.inflate(R.layout.activity_pager_one, null);
		viewVice = inflater.inflate(R.layout.activity_pager_two, null);

		viewList = new ArrayList<View>();// 将要分页显示的View装入数组中
		viewList.add(viewMain);
		viewList.add(viewVice);

		viewPager = (TransitionViewPager) findViewById(R.id.viewpager);
		viewPager.setTransitionEffect(TransitionEffect.Standard);
		viewPager.setPageMargin(0); // 10
		viewPager.setAdapter(pagerAdapter);

		mainReceiver = new MainReceiver();
		IntentFilter mainFilter = new IntentFilter();
		mainFilter.addAction(Constant.Broadcast.ACC_ON);
		mainFilter.addAction(Constant.Broadcast.ACC_OFF);
		mainFilter.addAction(Constant.Broadcast.BACK_CAR_ON);
		mainFilter.addAction(Constant.Broadcast.BACK_CAR_OFF);
		mainFilter.addAction(Constant.Broadcast.TTS_SPEAK);
		mainFilter.addAction(Intent.ACTION_TIME_TICK);
		mainFilter.addAction(Constant.Broadcast.GSENSOR_CRASH);
		mainFilter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
		registerReceiver(mainReceiver, mainFilter);

		getContentResolver()
				.registerContentObserver(
						Uri.parse("content://com.tchip.provider.AutoProvider/state/name/"),
						true, new AutoContentObserver(new Handler()));

		// Reset Record State
		ProviderUtil.setValue(context, Name.REC_FRONT_STATE, "0");
		ProviderUtil.setValue(context, Name.REC_BACK_STATE, "0");

		// 设置时区
		sendBroadcast(new Intent(Intent.ACTION_TIMEZONE_CHANGED).putExtra(
				"time-zone", "Asia/Shanghai"));

		initialNodeState();

		// 首次启动是否需要自动录像
		if (1 == SettingUtil.getAccStatus()) {
			doAccOnWork();
			new Thread(new StartRecordThread()).start();
		} else {
			MyApp.isAccOn = false; // 同步ACC状态
			ProviderUtil.setValue(context, Name.ACC_STATE, "0");
			sendBroadcast(new Intent("tchip.intent.action.CLOSE_SCREEN"));
			doAccOffWork();
			doSleepWork();
		}
	}

	@Override
	protected void onResume() {
		MyLog.i("onResume");
		super.onResume();
		sendBroadcast(new Intent(Constant.Broadcast.STATUS_SHOW)); // 显示状态栏
		updateAllInfo();
		syncBackCarStatus();
	}

	@Override
	protected void onPause() {
		super.onPause();
		sendBroadcast(new Intent(Constant.Broadcast.STATUS_HIDE)); // 隐藏状态栏
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mainReceiver != null) {
			unregisterReceiver(mainReceiver);
		}
		if (textToSpeech != null) { // 关闭TTS引擎
			textToSpeech.shutdown();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			viewPager.setCurrentItem(0); // 回到第一页
			return true;
		} else
			return super.onKeyDown(keyCode, event);
	}

	/** 启动前录 */
	private void startAutoRecord(long sendTime) {
		if (MyApp.isAccOn) {
			try {
				ActivityManager am = (ActivityManager) context
						.getSystemService(Context.ACTIVITY_SERVICE);
				ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
				String currentPackageName = cn.getPackageName();
				MyLog.v("currentPackageName:" + currentPackageName);
				ProviderUtil.setValue(context, Name.PKG_WHEN_BACK,
						currentPackageName);

				ComponentName componentRecord = new ComponentName(
						"com.tchip.autorecord",
						"com.tchip.autorecord.ui.MainActivity");
				Intent intentRecord = new Intent();
				intentRecord.putExtra("time", sendTime);
				intentRecord.putExtra("reason", "acc_on");
				intentRecord.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
						| Intent.FLAG_ACTIVITY_NEW_TASK);
				intentRecord.setComponent(componentRecord);
				startActivity(intentRecord);
				MyLog.v("startAutoRecord");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void startParkRecord() {
		try {
			ComponentName componentRecord = new ComponentName(
					"com.tchip.autorecord",
					"com.tchip.autorecord.ui.MainActivity");
			Intent intentRecord = new Intent();
			intentRecord.putExtra("reason", "acc_on");
			intentRecord.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
					| Intent.FLAG_ACTIVITY_NEW_TASK);
			intentRecord.setComponent(componentRecord);
			startActivity(intentRecord);
			MyLog.v("startParkRecord");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	class StartRecordThread implements Runnable {

		@Override
		public void run() {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			startAutoRecord(System.currentTimeMillis());
		}

	}

	class CloseRecordThread implements Runnable {

		@Override
		public void run() {
			try {
				Thread.sleep(1500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (!MyApp.isAccOn) {
				sendBroadcast(new Intent(Constant.Broadcast.RELEASE_RECORD));
				sendBroadcast(new Intent(Constant.Broadcast.KILL_APP).putExtra(
						"name", "com.tchip.autorecord"));
			}
		}

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
				if (name.startsWith("weather")) { // 天气
					updateWeatherInfo();
				} else if (name.startsWith("rec")) { // 录像
					updateRecordInfo();
				} else if (name.startsWith("music")) { // 音乐
					updateMusicInfo();
				} else if (Name.PARK_REC_STATE.equals(name)) {
					if (!MyApp.isAccOn) {
						String strParkMonitor = ProviderUtil.getValue(context,
								Name.PARK_REC_STATE);
						if (null != strParkMonitor
								&& strParkMonitor.trim().length() > 0
								&& "0".equals(strParkMonitor)) {
							KWAPI.createKWAPI(MainActivity.this, "auto")
									.exitAPP(MainActivity.this);
							// Reset Record State
							ProviderUtil.setValue(context,
									Name.REC_FRONT_STATE, "0");
							ProviderUtil.setValue(context, Name.REC_BACK_STATE,
									"0");
							new Thread(new CloseRecordThread()).start();
							doAccOffWork();
							doSleepWork();
							sendBroadcast(new Intent(
									"tchip.intent.action.CLOSE_SCREEN"));
						}
					}

				}
			}
			super.onChange(selfChange, uri);
		}

		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
		}

	}

	private void updateAllInfo() {
		// Page 1
		updateRecordInfo();
		updateMusicInfo();
		// Page 2
		updateWeatherInfo();
	}

	PagerAdapter pagerAdapter = new PagerAdapter() {

		@Override
		public boolean isViewFromObject(View view, Object obj) {
			if (view instanceof TransitionViewPagerContainer) {
				return ((TransitionViewPagerContainer) view).getChildAt(0) == obj;
			} else {
				return view == obj;
			}
		}

		@Override
		public int getCount() {
			return viewList.size();
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			MyLog.v("destroyItem position" + position);
			container.removeView(viewList.get(position));
		}

		@Override
		public int getItemPosition(Object object) {
			return super.getItemPosition(object);
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			container.addView(viewList.get(position));
			viewPager.setObjectForPosition(viewList.get(position), position); // 动画需要

			MyLog.v("position:" + position);
			if (position == 0)
				updateLayoutOne();
			else
				updateLayoutTwo();
			return viewList.get(position);
		}

	};

	private void updateLayoutOne() {
		// 行车记录
		RelativeLayout layoutRecord = (RelativeLayout) findViewById(R.id.layoutRecord);
		layoutRecord.setOnClickListener(new MyOnClickListener());
		imageRecordState = (ImageView) findViewById(R.id.imageRecordState);
		imageRecordState.setOnClickListener(new MyOnClickListener());
		textRecStateFront = (TextView) findViewById(R.id.textRecStateFront);
		textRecStateBack = (TextView) findViewById(R.id.textRecStateBack);
		updateRecordInfo();
		// 导航
		RelativeLayout layoutNavigation = (RelativeLayout) findViewById(R.id.layoutNavigation);
		layoutNavigation.setOnClickListener(new MyOnClickListener());
		// 音乐
		RelativeLayout layoutMusic = (RelativeLayout) findViewById(R.id.layoutMusic);
		layoutMusic.setOnClickListener(new MyOnClickListener());
		ImageView imageMusicState = (ImageView) findViewById(R.id.imageMusicState);
		imageMusicState.setOnClickListener(new MyOnClickListener());

		// 蓝牙通话
		RelativeLayout layoutPhone = (RelativeLayout) findViewById(R.id.layoutPhone);
		layoutPhone.setOnClickListener(new MyOnClickListener());
		// 电子狗
		RelativeLayout layoutEDog = (RelativeLayout) findViewById(R.id.layoutEDog);
		layoutEDog.setOnClickListener(new MyOnClickListener());
		// FM发射
		RelativeLayout layoutFMTransmit = (RelativeLayout) findViewById(R.id.layoutFMTransmit);
		layoutFMTransmit.setOnClickListener(new MyOnClickListener());
		// 文件管理
		RelativeLayout layoutFileManager = (RelativeLayout) findViewById(R.id.layoutFileManager);
		layoutFileManager.setOnClickListener(new MyOnClickListener());

		isPagerOneShowed = true;
		updateAllInfo();
	}

	private void updateLayoutTwo() {
		// 网络电台-喜马拉雅
		RelativeLayout layoutXimalaya = (RelativeLayout) findViewById(R.id.layoutXimalaya);
		layoutXimalaya.setOnClickListener(new MyOnClickListener());
		ImageView imageXimalayaState = (ImageView) findViewById(R.id.imageXimalayaState);
		imageXimalayaState.setOnClickListener(new MyOnClickListener());
		// 微信助手
		RelativeLayout layoutWechat = (RelativeLayout) findViewById(R.id.layoutWechat);
		layoutWechat.setOnClickListener(new MyOnClickListener());
		// 翼卡
		RelativeLayout layoutYiKa = (RelativeLayout) findViewById(R.id.layoutYiKa);
		layoutYiKa.setOnClickListener(new MyOnClickListener());
		TextView textTitleYika = (TextView) findViewById(R.id.textTitleYika);
		textTitleYika.setText(getResources().getString(
				Constant.Module.hasYouku ? R.string.title_youku
						: R.string.title_weme));
		ImageView imageYika = (ImageView) findViewById(R.id.imageYika);
		imageYika.setImageDrawable(getResources().getDrawable(
				Constant.Module.hasYouku ? R.drawable.main_item_multimedia
						: R.drawable.main_item_weme, null));
		// 天气
		RelativeLayout layoutWeather = (RelativeLayout) findViewById(R.id.layoutWeather);
		layoutWeather.setOnClickListener(new MyOnClickListener());
		imageWeatherInfo = (ImageView) findViewById(R.id.imageWeatherInfo);
		textWeatherInfo = (TextView) findViewById(R.id.textWeatherInfo);
		textWeatherTmpRange = (TextView) findViewById(R.id.textWeatherTmpRange);
		textWeatherTmpRange.setTypeface(TypefaceUtil.get(this,
				Constant.Path.FONT + "Font-Helvetica-Neue-LT-Pro.otf"));
		textWeatherCity = (TextView) findViewById(R.id.textWeatherCity);
		updateWeatherInfo();
		// 设置
		RelativeLayout layoutSetting = (RelativeLayout) findViewById(R.id.layoutSetting);
		layoutSetting.setOnClickListener(new MyOnClickListener());

		isPagerTwoShowed = true;
		updateAllInfo();
	}

	class MyOnClickListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.layoutRecord:
				if (MyApp.isAccOn) {
					OpenUtil.openModule(MainActivity.this, MODULE_TYPE.RECORD);
				} else {
					HintUtil.showToast(MainActivity.this, getResources()
							.getString(R.string.sleeping_now));
				}
				break;

			case R.id.imageRecordState:
				if (MyApp.isAccOn) {
					String strRecordState = ProviderUtil.getValue(context,
							Name.REC_FRONT_STATE);
					if (null != strRecordState
							&& strRecordState.trim().length() > 0) {
						if ("1".equals(strRecordState)) {
							sendBroadcast(new Intent(
									Constant.Broadcast.SPEECH_COMMAND)
									.putExtra("command", "close_dvr"));
						} else {
							sendBroadcast(new Intent(
									Constant.Broadcast.SPEECH_COMMAND)
									.putExtra("command", "open_dvr"));
						}
					}

				} else {
					HintUtil.showToast(MainActivity.this, getResources()
							.getString(R.string.sleeping_now));
				}
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
				kuwoAPI.setPlayState(MainActivity.this, PlayState.STATE_PLAY);
				break;

			case R.id.layoutXimalaya:
				OpenUtil.openModule(MainActivity.this, MODULE_TYPE.XIMALAYA);
				break;

			case R.id.imageXimalayaState:
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

			case R.id.layoutFileManager:
				OpenUtil.openModule(MainActivity.this,
						MODULE_TYPE.FILE_MANAGER_MTK);
				break;

			case R.id.layoutWechat:
				OpenUtil.openModule(MainActivity.this, MODULE_TYPE.WECHAT);
				break;

			case R.id.layoutYiKa:
				if (Constant.Module.hasYouku) {
					OpenUtil.openModule(MainActivity.this, MODULE_TYPE.YOUKU);
				} else
					OpenUtil.openModule(MainActivity.this, MODULE_TYPE.YIKA);
				break;

			case R.id.layoutSetting:
				OpenUtil.openModule(MainActivity.this, MODULE_TYPE.SETTING);
				break;

			default:
				break;
			}
		}

	}

	class MyTTSOnInitListener implements OnInitListener {

		@Override
		public void onInit(int status) {
			// tts.setEngineByPackageName("com.iflytek.vflynote");
			textToSpeech.setLanguage(Locale.CHINESE);
		}

	}

	private void speakVoice(String content) {
		MyLog.v("speakVoice:" + content);
		textToSpeech.speak(content, TextToSpeech.QUEUE_FLUSH, null, content);
	}

	/** 更新录制信息 */
	private void updateRecordInfo() {
		if (isPagerOneShowed) {
			Message msgUpdateRecord = new Message();
			msgUpdateRecord.what = 1;
			taskHandler.sendMessage(msgUpdateRecord);
		}
	}

	/** 更新天气信息 */
	private void updateWeatherInfo() {
		if (isPagerTwoShowed) {
			Message msgUpdateWeather = new Message();
			msgUpdateWeather.what = 2;
			taskHandler.sendMessage(msgUpdateWeather);
		}
	}

	/** 更新音乐信息 */
	private void updateMusicInfo() {
		if (isPagerOneShowed) {
			Message msgUpdateMusic = new Message();
			msgUpdateMusic.what = 3;
			taskHandler.sendMessage(msgUpdateMusic);
		}
	}

	/** 同步倒车状态 */
	private void syncBackCarStatus() {
		Message msgSyncBackCar = new Message();
		msgSyncBackCar.what = 6;
		taskHandler.sendMessage(msgSyncBackCar);
	}

	/**
	 * 初始化节点,调用：
	 * 
	 * 1.onCreate
	 * 
	 * 2.ACC_ON
	 */
	private void initialNodeState() {
		Message msgInitialNodeState = new Message();
		msgInitialNodeState.what = 7;
		taskHandler.sendMessage(msgInitialNodeState);
	}

	private void doAccOnWork() {
		MyApp.isAccOn = true; // 同步ACC状态
		MyApp.isSleeping = false; // 取消低功耗待机
		MyApp.isSleepConfirm = false;
		accOffCount = 0;
		preSleepCount = 0;
		ProviderUtil.setValue(context, Name.ACC_STATE, "1");
		ProviderUtil.setValue(context, Name.PARK_REC_STATE, "0");
		TelephonyUtil.setAirplaneMode(context, false); // 关闭飞行模式
		SettingUtil.setGpsOn(context, true); // 打开GPS
		SettingUtil.setEdogPowerOn(true); // 打开电子狗电源
		SettingUtil.setLedConfig(21); // 蓝灯亮
	}

	private void doAccOffWork() {
		sendKeyCode(KeyEvent.KEYCODE_HOME);
		SettingUtil.setGpsOn(context, false); // 关闭GPS
		SettingUtil.setEdogPowerOn(false); // 关闭电子狗电源
		SettingUtil.setLedConfig(0); // 关闭LED灯
		OpenUtil.killAppWhenAccOff(context);
	}

	private void doSleepWork() {
		MyApp.isSleeping = true;
		SettingUtil.setFmTransmitPowerOn(context, false); // 关闭FM发射
		TelephonyUtil.setAirplaneMode(context, true); // 打开飞行模式
	}

	private void sendKeyCode(final int keyCode) {
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

	private MainReceiver mainReceiver;

	private class MainReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			MyLog.v("MainReceiver.action:" + action);
			if (Constant.Broadcast.ACC_ON.equals(action)) {
				doAccOnWork();
				initialNodeState();
				new Thread(new StartRecordThread()).start();

			} else if (Constant.Broadcast.ACC_OFF.equals(action)) {
				MyApp.isAccOn = false;
				ProviderUtil.setValue(context, Name.ACC_STATE, "0");
				KWAPI.createKWAPI(MainActivity.this, "auto").exitAPP(
						MainActivity.this);

				// Reset Record State
				sendBroadcast(new Intent(Constant.Broadcast.SPEECH_COMMAND)
						.putExtra("command", "take_park_photo"));
				ProviderUtil.setValue(context, Name.REC_FRONT_STATE, "0");
				ProviderUtil.setValue(context, Name.REC_BACK_STATE, "0");
				new Thread(new CloseRecordThread()).start();

				String strParkMonitorState = ProviderUtil.getValue(context,
						Name.SET_PARK_MONITOR_STATE);
				if (null != strParkMonitorState
						&& strParkMonitorState.trim().length() > 0
						&& "1".equals(strParkMonitorState)) {
					String strStartPark90s = getResources().getString(
							R.string.hint_start_park_monitor_after_90);
					HintUtil.showToast(context, strStartPark90s);
					speakVoice(strStartPark90s);
				}

				preSleepCount = 0;
				MyApp.isSleepConfirm = true;
				new Thread(new PreSleepThread()).start();
			} else if (Constant.Broadcast.GSENSOR_CRASH.equals(action)) { // 停车守卫
				if (MyApp.isSleeping && !MyApp.isAccOn
						&& StorageUtil.isFrontCardExist()) {
					String strParkRecord = ProviderUtil.getValue(context,
							Name.PARK_REC_STATE);
					String strFrontRecord = ProviderUtil.getValue(context,
							Name.REC_FRONT_STATE);
					if (null != strParkRecord
							&& strParkRecord.trim().length() > 0
							&& "0".equals(strParkRecord)
							|| null != strFrontRecord
							&& strFrontRecord.trim().length() > 0
							&& "0".equals(strFrontRecord)) {
						ProviderUtil
								.setValue(context, Name.PARK_REC_STATE, "1");
						startParkRecord();
					} else {
						MyLog.v("PARK_REC_STATE Already 1");
					}
				}
			} else if (Constant.Broadcast.BACK_CAR_ON.equals(action)) {
				ProviderUtil.setValue(context, Name.BACK_CAR_STATE, "1");
				startAutoRecord(SystemClock.currentThreadTimeMillis());
				speakVoice(getResources().getString(R.string.hint_back_car_now));
			} else if (Constant.Broadcast.BACK_CAR_OFF.equals(action)) {
				ProviderUtil.setValue(context, Name.BACK_CAR_STATE, "0");
				OpenUtil.returnWhenBackOver(MainActivity.this);
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
					MyLog.v("TimeTickReceiver.Year:" + year);

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
									Constant.Broadcast.DEVICE_REBOOT));
						}
					}
				}
			} else if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
				String reason = intent.getStringExtra("reason");
				if ("homekey".equals(reason)) {
					viewPager.setCurrentItem(0); // 回到第一页
				} else if ("recentapps".equals(reason)) {
				}
			}
		}
	}

	private WakeLock partialWakeLock;

	/**
	 * 获取休眠锁
	 * 
	 * PARTIAL_WAKE_LOCK
	 * 
	 * SCREEN_DIM_WAKE_LOCK
	 * 
	 * FULL_WAKE_LOCK
	 * 
	 * ON_AFTER_RELEASE
	 */
	private void acquirePartialWakeLock(long timeout) {
		partialWakeLock = powerManager.newWakeLock(
				PowerManager.PARTIAL_WAKE_LOCK, this.getClass()
						.getCanonicalName());
		partialWakeLock.acquire(timeout);
	}

	/** ACC断开进入预备模式的时间:秒 **/
	private int preSleepCount = 0;

	/** 预备睡眠模式的时间:秒 **/
	private final int TIME_SLEEP_CONFIRM = 2;

	/** 预备唤醒模式的时间:秒 **/
	private final int TIME_WAKE_CONFIRM = 1;

	/** ACC断开的时间:秒 **/
	private int accOffCount = 0;

	/** ACC断开进入深度休眠之前的时间:秒 **/
	private final int TIME_SLEEP_GOING = 85;

	/**
	 * 90s后进入停车侦测守卫模式，期间如果ACC上电则取消
	 */
	public class GoingParkMonitorThread implements Runnable {

		@Override
		public void run() {
			synchronized (goingParkMonitorHandler) {
				/** 激发条件:1.ACC下电 2.未进入休眠 **/
				while (!MyApp.isAccOn && !MyApp.isSleeping) {
					try {
						Thread.sleep(1000);
						Message message = new Message();
						message.what = 1;
						goingParkMonitorHandler.sendMessage(message);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	final Handler goingParkMonitorHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				if (!MyApp.isAccOn) {
					accOffCount++;
					acquirePartialWakeLock(2 * 1000);
				} else {
					accOffCount = 0;
				}

				MyLog.v("[ParkingMonitor]accOffCount:" + accOffCount);

				if (accOffCount >= TIME_SLEEP_GOING && !MyApp.isAccOn
						&& !MyApp.isSleeping) {
					doSleepWork();
				}
				break;

			default:
				break;
			}
		}
	};

	/** 预备休眠线程 **/
	public class PreSleepThread implements Runnable {

		@Override
		public void run() {
			synchronized (preSleepHandler) {
				/** 激发条件:1.ACC下电 2.未进入休眠 **/
				while (MyApp.isSleepConfirm && !MyApp.isAccOn
						&& !MyApp.isSleeping) {
					try {
						Thread.sleep(1000);
						Message message = new Message();
						message.what = 1;
						preSleepHandler.sendMessage(message);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}

	}

	final Handler preSleepHandler = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				if (!MyApp.isAccOn) {
					preSleepCount++;
				} else {
					preSleepCount = 0;
				}
				MyLog.v("[ParkingMonitor]preSleepCount:" + preSleepCount);

				if (preSleepCount == TIME_SLEEP_CONFIRM && !MyApp.isAccOn
						&& !MyApp.isSleeping) {
					MyApp.isSleepConfirm = false;
					preSleepCount = 0;
					doAccOffWork();
					new Thread(new GoingParkMonitorThread()).start();
				}
				break;

			default:
				break;
			}
		}

	};

	/** 非UI任务线程 */
	private static final HandlerThread taskHandlerThread = new HandlerThread(
			"ui-task-thread");
	static {
		taskHandlerThread.start();
	}
	/**
	 * @param 1 更新录制信息
	 * @param 2 更新天气信息
	 * @param 3 更新音乐信息 ToDo
	 * @param 6 同步倒车状态
	 * @param 7 初始化节点
	 */
	private final Handler taskHandler = new TaskHandler(
			taskHandlerThread.getLooper());

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
								imageRecordState
										.setImageResource(R.drawable.main_item_state_record_stop);
							} else {
								textRecStateFront
										.setText(getResources().getString(
												R.string.rec_state_front_off));
								imageRecordState
										.setImageResource(R.drawable.main_item_state_record_start);
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

			case 6: // 同步倒车状态
				this.removeMessages(6);
				ProviderUtil.setValue(context, Name.BACK_CAR_STATE, ""
						+ SettingUtil.getBackCarStatus());
				this.removeMessages(6);
				break;

			case 7: // 初始化节点
				this.removeMessages(7);
				// FM发射开关/频率
				String fmFrequencyConfig = ProviderUtil.getValue(context,
						Name.FM_TRANSMIT_FREQ);
				if (null != fmFrequencyConfig
						&& fmFrequencyConfig.trim().length() > 0) {
					SettingUtil.setFmFrequencyNode(context,
							Integer.parseInt(fmFrequencyConfig));
				}
				String fmStateConfig = ProviderUtil.getValue(context,
						Name.FM_TRANSMIT_STATE);
				if (null != fmStateConfig && fmStateConfig.trim().length() > 0
						&& "1".equals(fmStateConfig)) {
					SettingUtil.setFmTransmitPowerOn(context, true);
				} else {
					SettingUtil.setFmTransmitPowerOn(context, false);
				}
				// 停车守卫开关
				String strParkMonitor = ProviderUtil.getValue(context,
						Name.SET_PARK_MONITOR_STATE);
				if (null != strParkMonitor
						&& strParkMonitor.trim().length() > 0
						&& "0".equals(strParkMonitor)) {
					SettingUtil.setParkMonitorNode(false);
				} else {
					SettingUtil.setParkMonitorNode(true);
				}
				// 自动亮度调节
				String strAutoLight = ProviderUtil.getValue(context,
						Name.SET_AUTO_LIGHT_STATE);
				if (null != strAutoLight && strAutoLight.trim().length() > 0
						&& "1".equals(strAutoLight)) {
					SettingUtil.setAutoLight(true);
				} else {
					SettingUtil.setAutoLight(false);
				}
				// ACC下电唤醒
				String strAccOffWake = ProviderUtil.getValue(context,
						Name.DEBUG_ACCOFF_WAKE);
				if (null != strAccOffWake && strAccOffWake.trim().length() > 0
						&& "1".equals(strAccOffWake)) {
					SettingUtil.setAccOffWake(true);
				} else {
					SettingUtil.setAccOffWake(false);
				}
				this.removeMessages(7);
				break;

			}
		}
	}

}
