package com.tchip.autoui.receiver;

import com.tchip.autoui.Constant;
import com.tchip.autoui.util.ClickUtil;
import com.tchip.autoui.util.MyLog;
import com.tchip.autoui.util.SettingUtil;
import com.tchip.autoui.view.FormatDialog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.view.WindowManager;

public class CardMountReceiver extends BroadcastReceiver {

	private FormatDialog.Builder builder;
	private FormatDialog alertDialog;

	@Override
	public void onReceive(final Context context, Intent intent) {
		String action = intent.getAction();
		MyLog.i("CardMountReceiver.action:" + action);
		if (action.equals(Intent.ACTION_MEDIA_MOUNTED)) {
			if ("/storage/sdcard1".equals(intent.getData().getPath())) { // 是否需要格式化录像卡？
				if (SettingUtil.isSDInsert()) {
					SettingUtil.clearSDStatus();
					builder = new FormatDialog.Builder(
							context.getApplicationContext());
					builder.setMessage("是否需要格式化录像卡?");
					builder.setTitle("提示");
					builder.setPositiveButton("确认", new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							ClickUtil.lastFromatTime = System
									.currentTimeMillis();
							dialog.dismiss();
							context.sendBroadcast(new Intent(
									"tchip.intent.action.FORMAT_CARD"));
						}
					});
					builder.setNegativeButton("取消", new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							ClickUtil.lastFromatTime = System
									.currentTimeMillis();
							dialog.dismiss();
						}
					});
					alertDialog = builder.create();
					alertDialog.getWindow().setType(
							WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
					alertDialog.setCanceledOnTouchOutside(true);
					if (!alertDialog.isShowing()) {
						alertDialog.show();
					}
				} else {
					MyLog.e("This mount action is not insert.");
				}
			}
		} else if (action.equals(Intent.ACTION_MEDIA_EJECT)
				|| action.equals(Intent.ACTION_MEDIA_BAD_REMOVAL)
				|| action.equals(Intent.ACTION_MEDIA_UNMOUNTED)) {
			if ("/storage/sdcard1".equals(intent.getData().getPath())) {
				context.sendBroadcast(new Intent(
						Constant.Broadcast.HIDE_FORMAT_DIALOG));
			}
		}
	}
}
