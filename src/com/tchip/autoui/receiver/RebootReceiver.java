package com.tchip.autoui.receiver;

import com.tchip.autoui.MyApp;
import com.tchip.autoui.util.MyLog;
import com.tchip.autoui.util.SettingUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

public class RebootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		MyLog.i("RebootReceiver:>>>>>>>>>>>>>>>>MyApp.isAccOn:" + MyApp.isAccOn);
		if (!MyApp.isAccOn) { // 凌晨3点重启机器
			PowerManager powerManager = (PowerManager) context
					.getSystemService(Context.POWER_SERVICE);
			WakeLock partialWakeLock = powerManager.newWakeLock(
					PowerManager.PARTIAL_WAKE_LOCK, this.getClass()
							.getCanonicalName());
			partialWakeLock.acquire(10 * 1000);
			SettingUtil.normalReboot(context);
		}

	}

}
