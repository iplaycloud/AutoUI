package com.tchip.autoui.util;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.widget.Toast;

public class HintUtil {

	/**
	 * 播放音乐
	 * 
	 * @param context
	 * @param uri
	 *            eg：R.raw.camera_image
	 */
	public static void playAudio(Context context, Uri uri) {
		AudioManager audioManager = (AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE);
		MediaPlayer mediaPlayer;

		if (audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION) > 0) {
			mediaPlayer = MediaPlayer.create(context, uri);
			mediaPlayer.start();
		}
	}

	public static void showToast(Context context, String content) {
		Toast.makeText(context, content, Toast.LENGTH_SHORT).show();
	}

}
