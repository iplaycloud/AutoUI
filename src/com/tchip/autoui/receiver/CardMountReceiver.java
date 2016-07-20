package com.tchip.autoui.receiver;

import com.tchip.autoui.util.ClickUtil;
import com.tchip.autoui.util.MyLog;
import com.tchip.autoui.view.FormatDialog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.view.WindowManager;

public class CardMountReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(final Context context, Intent intent) {
		String action = intent.getAction();
		MyLog.i("CardEjectReceiver.action:" + action);
		if (action.equals(Intent.ACTION_MEDIA_MOUNTED)) {
			if ("/storage/sdcard1".equals(intent.getData().getPath())) { // 是否需要格式化录像卡？
				if (!ClickUtil.isQuickFromat(30 * 1000)) {
					FormatDialog.Builder builder = new FormatDialog.Builder(
							context);
					builder.setMessage("定期格式化录像卡以保障系统流畅运行，是否格式化?");
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
					FormatDialog alertDialog = builder.create();
					alertDialog.getWindow().setType(
							WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
					alertDialog.setCanceledOnTouchOutside(true);
					alertDialog.show();
				} else {
					MyLog.e("Format too quick!");
				}
			}
		}
	}
}
