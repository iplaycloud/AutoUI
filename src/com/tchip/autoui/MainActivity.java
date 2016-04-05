package com.tchip.autoui;

import com.tchip.autoui.util.HintUtil;
import com.tchip.autoui.util.OpenUtil;
import com.tchip.autoui.util.OpenUtil.MODULE_TYPE;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_main);
		initialLayout();
	}

	/** 初始化布局 */
	private void initialLayout() {
		MyOnClickListener myOnClickListener = new MyOnClickListener();
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
}
