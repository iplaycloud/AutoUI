package com.tchip.autoui.util;

import java.io.File;

public class FileUtil {

	/**
	 * 遍历目录大小
	 * 
	 * @return 剩余空间，单位：字节B
	 */
	public static long getTotalSizeOfFilesInDir(File file) {
		if (file.isFile())
			return file.length();
		final File[] children = file.listFiles();
		long total = 0;
		if (children != null)
			for (final File child : children) {
				total += getTotalSizeOfFilesInDir(child);
			}
		return total;
	}

}