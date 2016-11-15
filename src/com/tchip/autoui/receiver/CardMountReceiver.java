package com.tchip.autoui.receiver;

import com.tchip.autoui.Constant;
import com.tchip.autoui.util.ClickUtil;
import com.tchip.autoui.util.MyLog;
import com.tchip.autoui.util.ProviderUtil;
import com.tchip.autoui.util.ProviderUtil.Name;
import com.tchip.autoui.util.SettingUtil;
import com.tchip.autoui.view.FormatDialog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.WindowManager;

public class CardMountReceiver extends BroadcastReceiver {

	private Context context;
	private FormatDialog.Builder builder;
	private FormatDialog alertDialog;

	@Override
	public void onReceive(final Context context, Intent intent) {
		this.context = context;
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
							dialog.dismiss();
							new Thread(new FormatCardThread()).start();
						}
					});
					builder.setNegativeButton("取消", new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
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

	class FormatCardThread implements Runnable {

		@Override
		public void run() {
			while ((!"0".equals(ProviderUtil.getValue(context,
					Name.REC_BACK_STATE, "0")) || !"0".equals(ProviderUtil
					.getValue(context, Name.REC_FRONT_STATE, "0")))) {
				try {
					context.sendBroadcast(new Intent(
							"tchip.intent.action.MEDIA_FORMAT").putExtra(
							"path", "/storage/sdcard1"));
					Thread.sleep(500);
					Message messageWait = new Message();
					messageWait.what = 0;
					formatCardHandler.sendMessage(messageWait);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			Message messageFormat = new Message();
			messageFormat.what = 1;
			formatCardHandler.sendMessage(messageFormat);
		}
	}

	Handler formatCardHandler = new Handler() {

		public void handleMessage(Message message) {
			switch (message.what) {
			case 0:
				MyLog.w("FormatCard wait for stopping record");
				break;

			case 1:
				context.sendBroadcast(new Intent(
						"tchip.intent.action.FORMAT_CARD"));
				break;
			}
		}
	};
}
