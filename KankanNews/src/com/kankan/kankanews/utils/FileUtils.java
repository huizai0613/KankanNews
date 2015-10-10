package com.kankan.kankanews.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

public class FileUtils {
	private static int bufferd = 1024;

	/*
	 * <!-- 在SDCard中创建与删除文件权限 --> <uses-permission
	 * android:name="android.permission.MOUNT_UNMOUNT_FILESYSTEMS"/> <!--
	 * 往SDCard写入数据权限 --> <uses-permission
	 * android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
	 */

	// =================get SDCard information===================
	public static boolean isSdcardAvailable() {
		String status = Environment.getExternalStorageState();
		if (status.equals(Environment.MEDIA_MOUNTED)) {
			return true;
		}
		return false;
	}

	public static long getSDAllSizeKB() {
		// get path of sdcard
		File path = Environment.getExternalStorageDirectory();
		StatFs sf = new StatFs(path.getPath());
		// get single block size(Byte)
		long blockSize = sf.getBlockSize();
		// 获取所有数据块数
		long allBlocks = sf.getBlockCount();
		// 返回SD卡大小
		return (allBlocks * blockSize) / 1024; // KB
	}

	/**
	 * free size for normal application
	 * 
	 * @return
	 */
	public static long getSDAvalibleSizeKB() {
		File path = Environment.getExternalStorageDirectory();
		StatFs sf = new StatFs(path.getPath());
		long blockSize = sf.getBlockSize();
		long avaliableSize = sf.getAvailableBlocks();
		return (avaliableSize * blockSize) / 1024;// KB
	}

	// =====================File Operation==========================
	public static boolean isFileExist(String director) {
		File file = new File(Environment.getExternalStorageDirectory()
				+ File.separator + director);
		return file.exists();
	}

	/**
	 * create multiple director
	 * 
	 * @param path
	 * @return
	 */
	public static boolean createFile(String director) {
		if (isFileExist(director)) {
			return true;
		} else {
			File file = new File(Environment.getExternalStorageDirectory()
					+ File.separator + director);
			if (!file.mkdirs()) {
				return false;
			}
			return true;
		}
	}

	public static File writeToSDCardFile(String directory, String fileName,
			String content, boolean isAppend) {
		return writeToSDCardFile(directory, fileName, content, "", isAppend);
	}

	/**
	 * 
	 * @param director
	 *            (you don't need to begin with
	 *            Environment.getExternalStorageDirectory()+File.separator)
	 * @param fileName
	 * @param content
	 * @param encoding
	 *            (UTF-8...)
	 * @param isAppend
	 *            : Context.MODE_APPEND
	 * @return
	 */
	public static File writeToSDCardFile(String directory, String fileName,
			String content, String encoding, boolean isAppend) {
		// mobile SD card path +path
		File file = null;
		OutputStream os = null;
		try {
			if (!createFile(directory)) {
				return file;
			}
			file = new File(Environment.getExternalStorageDirectory()
					+ File.separator + directory + File.separator + fileName);
			os = new FileOutputStream(file, isAppend);
			if (encoding.equals("")) {
				os.write(content.getBytes());
			} else {
				os.write(content.getBytes(encoding));
			}
			os.flush();
		} catch (IOException e) {
			Log.e("FileUtil", "writeToSDCardFile:" + e.getMessage());
		} finally {
			try {
				if (os != null) {
					os.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return file;
	}

	/**
	 * write data from inputstream to SDCard
	 */
	public File writeToSDCardFromInput(String directory, String fileName,
			InputStream input) {
		File file = null;
		OutputStream os = null;
		try {
			if (createFile(directory)) {
				return file;
			}
			file = new File(Environment.getExternalStorageDirectory()
					+ File.separator + directory + fileName);
			os = new FileOutputStream(file);
			byte[] data = new byte[bufferd];
			int length = -1;
			while ((length = input.read(data)) != -1) {
				os.write(data, 0, length);
			}
			// clear cache
			os.flush();
		} catch (Exception e) {
			Log.e("FileUtil", "" + e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				os.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return file;
	}

	/**
	 * this url point to image(jpg)
	 * 
	 * @param url
	 * @return image name
	 */
	public static String getUrlLastString(String url) {
		String[] str = url.split("/");
		int size = str.length;
		return str[size - 1];
	}

	public static String getExtensionName(String filename) {
		if ((filename != null) && (filename.length() > 0)) {
			int dot = filename.lastIndexOf('.');
			if ((dot > -1) && (dot < (filename.length() - 1))) {
				return filename.substring(dot + 1);
			}
		}
		return filename;
	}

	public static boolean isVideo(String filePath) {
		File file = new File(filePath);
		if (file.exists()) {
			if (getExtensionName(file.getName()).equalsIgnoreCase("mp4")
					|| getExtensionName(file.getName()).equalsIgnoreCase("3gp")
					|| getExtensionName(file.getName()).equalsIgnoreCase("avi")
					|| getExtensionName(file.getName()).equalsIgnoreCase("mkv")
					|| getExtensionName(file.getName())
							.equalsIgnoreCase("rmvb")
					|| getExtensionName(file.getName()).equalsIgnoreCase("flv")
					|| getExtensionName(file.getName()).equalsIgnoreCase("mov")
					|| getExtensionName(file.getName()).equalsIgnoreCase("wmv")
					|| getExtensionName(file.getName()).equalsIgnoreCase("ram")
					|| getExtensionName(file.getName()).equalsIgnoreCase("ra")
					|| getExtensionName(file.getName()).equalsIgnoreCase("mpg"))
				return true;
		}
		return false;
	}
}
