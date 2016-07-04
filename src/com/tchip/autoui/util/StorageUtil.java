package com.tchip.autoui.util;

import java.io.File;
import java.text.DecimalFormat;

import com.tchip.autoui.Constant;

import android.os.StatFs;

public class StorageUtil {

	/**
	 * 获取所有存储总容量，包括：内部存储、SD1、SD2
	 * 
	 * @return 单位:GB
	 */
	public static String getFileTotalSizeStr() {
		long totalSize = 0;
		totalSize += getSDTotalSize("/storage/sdcard0");
		totalSize += getSDTotalSize("/storage/sdcard1");
		totalSize += getSDTotalSize("/storage/sdcard2");

		double totalSizeWithDot = 0.00f;
		totalSizeWithDot = (totalSize * 1.00) / (1024 * 1024 * 1024);
		DecimalFormat decimalFormat = new DecimalFormat("##.00");
		return decimalFormat.format(totalSizeWithDot);
	}

	/**
	 * 获取所有存储剩余容量，包括：内部存储、SD1、SD2
	 * 
	 * @return 单位:GB
	 */
	public static String getFileLeftSizeStr() {
		long leftSize = 0;
		leftSize += getSDAvailableSize("/storage/sdcard0");
		leftSize += getSDAvailableSize("/storage/sdcard1");
		leftSize += getSDAvailableSize("/storage/sdcard2");

		double leftSizeSizeWithDot = 0.00f;
		leftSizeSizeWithDot = (leftSize * 1.00) / (1024 * 1024 * 1024);
		DecimalFormat decimalFormat = new DecimalFormat("##.00");
		return decimalFormat.format(leftSizeSizeWithDot);
	}

	/**
	 * 获得SD卡总大小
	 * 
	 * @return 总大小，单位：字节B
	 */
	public static long getSDTotalSize(String SDCardPath) {
		StatFs stat = new StatFs(SDCardPath);
		long blockSize = stat.getBlockSize();
		long totalBlocks = stat.getBlockCount();
		return blockSize * totalBlocks;
	}

	/**
	 * 获得sd卡剩余容量，即可用大小
	 * 
	 * @return 剩余空间，单位：字节B
	 */
	public static long getSDAvailableSize(String SDCardPath) {
		// StatFs stat = new StatFs("/storage/sdcard1");
		StatFs stat = new StatFs(SDCardPath);
		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();
		return blockSize * availableBlocks;
	}
	
	/** 录像SD卡是否存在 */
	public static boolean isFrontCardExist() {
		boolean isVideoCardExist = false;
		try {
			String pathVideo = Constant.Path.RECORD_FRONT;
			File fileVideo = new File(pathVideo);
			boolean isSuccess = fileVideo.mkdirs();
			MyLog.v("StorageUtil.isVideoCardExists,mkdirs isSuccess:"
					+ isSuccess);
			File file = new File(pathVideo);
			if (!file.exists()) {
				isVideoCardExist = false;
			} else {
				isVideoCardExist = true;
			}
		} catch (Exception e) {
			MyLog.e("StorageUtil.isVideoCardExists:Catch Exception!");
			isVideoCardExist = false;
		}
		MyLog.v("StorageUtil.isVideoCardExists:" + isVideoCardExist);
		return isVideoCardExist;
	}

}
