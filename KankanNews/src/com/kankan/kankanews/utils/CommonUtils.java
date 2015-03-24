package com.kankan.kankanews.utils;

import static android.os.Environment.MEDIA_MOUNTED;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;

import com.ant.liao.GifView;
import com.kankan.kankanews.bean.Content_News;
import com.kankanews.kankanxinwen.R;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.utils.L;
import com.umeng.analytics.MobclickAgent;

@SuppressLint("NewApi")
public class CommonUtils {

	/** 检查是否有网络 */
	public static boolean isNetworkAvailable(Context context) {
		NetworkInfo info = getNetworkInfo(context);
		if (info != null) {
			boolean available = info.isAvailable();
			if (!available) {
				ToastUtils.ErrorToastNoNet(context);
			}
			return available;
		}
		ToastUtils.ErrorToastNoNet(context);
		return false;
	}

	/** 检查是否是WIFI */
	public static boolean isWifi(Context context) {
		NetworkInfo info = getNetworkInfo(context);
		if (info != null) {
			if (info.getType() == ConnectivityManager.TYPE_WIFI)
				return true;
		}
		return false;
	}

	/** 检查是否是移动网络 */
	public static boolean isMobile(Context context) {
		NetworkInfo info = getNetworkInfo(context);
		if (info != null) {
			if (info.getType() == ConnectivityManager.TYPE_MOBILE)
				return true;
		}
		return false;
	}

	public static NetworkInfo getNetworkInfo(Context context) {

		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		return cm.getActiveNetworkInfo();
	}

	/** 检查SD卡是否存在 */
	public static boolean checkSdCard() {
		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED))
			return true;
		else
			return false;
	}

	/**
	 * 去除特殊字符或将所有中文标号替换为英文标号
	 * 
	 * @param str
	 * @return
	 */
	public static String stringFilter(String str) {
		str = str.replaceAll("【", "[ ").replaceAll("】", "] ")
				.replaceAll("！", "! ").replaceAll("？", "? ")
				.replaceAll("，", ", ").replaceAll("。", ". ")
				.replaceAll(":", ": ");
		;// 替换中文标号
			// String regEx = "[『』]"; // 清除掉特殊字符
		// Pattern p = Pattern.compile(regEx);
		// Matcher m = p.matcher(str);
		return str;
	}

	// public static void showUpdateDialog(final Context context,
	// final String url, String content, final Handler handler) {
	//
	// UpdateVersionDialog dialog = new UpdateVersionDialog(context);
	// dialog.setContentView(R.layout.dialog__updateversion);
	// ProgressDialog pBar = new ProgressDialog(context);
	// pBar.setTitle("正在下载");
	// pBar.setMessage("请稍候...");
	// pBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	// // pBar.setCanceledOnTouchOutside(false);
	// dialog.init(context, url, pBar, handler);
	// dialog.setContent(content);
	// dialog.show();
	// AlertDialog.Builder builder = new AlertDialog.Builder(context);
	//
	// builder.
	// builder.setTitle("检测到新版本");
	// builder.setMessage(content);
	// builder.setPositiveButton("下载", new DialogInterface.OnClickListener()
	// {
	//
	// @Override
	// public void onClick(DialogInterface dialog, int which) {
	// // TODO Auto-generated method stub
	// ProgressDialog pBar = new ProgressDialog(context);
	// pBar.setTitle("正在下载");
	// pBar.setMessage("请稍候...");
	// pBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	// // pBar.setCanceledOnTouchOutside(false);
	// startUpdateAPK(context, url, pBar, handler);
	// }
	// }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
	//
	// @Override
	// public void onClick(DialogInterface dialog, int which) {
	// dialog.dismiss();
	// handler.sendEmptyMessage(Splash_Activity.GO);
	// }
	// });
	//
	// AlertDialog create = builder.create();
	// create.setCanceledOnTouchOutside(false);
	// create.show();
	// }

	// 开始下载更新
	public static void startUpdateAPK(final Context context, final String url,
			final ProgressDialog pBar, final Handler handler) {
		pBar.show();
		final File file = getAPKPath(context);
		if (file.exists()) {
			file.delete();
		}

		new Thread() {
			@Override
			public void run() {
				HttpClient client = new DefaultHttpClient();
				HttpGet get = new HttpGet(url);
				HttpResponse response;
				try {
					response = client.execute(get);
					HttpEntity entity = response.getEntity();
					long length = entity.getContentLength();
					InputStream is = entity.getContent();
					FileOutputStream fileOutputStream = null;
					if (is != null) {
						fileOutputStream = new FileOutputStream(file);
						byte[] buf = new byte[1024];
						int ch = -1;
						while ((ch = is.read(buf)) != -1) {
							fileOutputStream.write(buf, 0, ch);
							if (length > 0) {
							}
						}
					}
					fileOutputStream.flush();
					if (fileOutputStream != null) {
						fileOutputStream.close();
					}
					down(context, handler, pBar);
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	private static void down(final Context context, Handler handler,
			final ProgressDialog pBar) {

		handler.post(new Runnable() {
			@Override
			public void run() {
				pBar.cancel();
				update(context);
			}
		});
	}

	private static void update(Context context) {

		String apkPath = getAPKPath(context).getAbsolutePath();
		root(apkPath);
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.parse("file://" + apkPath),
				"application/vnd.android.package-archive");
		context.startActivity(intent);
	}

	private static void root(String path) {
		try {
			Runtime.getRuntime().exec("chmod 777 " + path);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static File getAPKPath(Context context) {

		File externalStorageDirectory = null;

		if (checkSdCard()) {
			externalStorageDirectory = new File(
					android.os.Environment.getExternalStorageDirectory(),
					"transportApk");

		} else {
			externalStorageDirectory = new File(context.getCacheDir(),
					"transportApk");
		}
		if (externalStorageDirectory.exists()) {
			externalStorageDirectory.mkdirs();

		}
		return new File(externalStorageDirectory, "update.apk");
	}

	public static File getImagePath(Context context, String imaName) {

		File externalStorageDirectory = null;

		externalStorageDirectory = new File(
				Environment.getExternalStorageDirectory(),
				"/DCIM/Camera/transport");
		if (!externalStorageDirectory.exists()) {
			externalStorageDirectory.mkdirs();
		}

		return new File(externalStorageDirectory, imaName);
	}

	public static boolean versionCompare(Context context, String version) {

		String versionName = getVersionName(context);

		if (versionName.equals(version)) {// 一样不需要更新
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 得到版本号
	 * 
	 * @param context
	 * @return
	 */
	public static String getVersionName(Context context) {
		String verName = "";
		try {
			verName = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return verName;
	}

	/*
	 * MD5加密
	 */
	private static final char HEX_DIGITS[] = { '0', '1', '2', '3', '4', '5',
			'6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	public static String toHexString(byte[] b) { // String to byte
		StringBuilder sb = new StringBuilder(b.length * 2);
		for (int i = 0; i < b.length; i++) {
			sb.append(HEX_DIGITS[(b[i] & 0xf0) >>> 4]);
			sb.append(HEX_DIGITS[b[i] & 0x0f]);
		}
		return sb.toString();
	}

	public static String md5(String s) {
		try {
			// Create MD5 Hash
			MessageDigest digest = java.security.MessageDigest
					.getInstance("MD5");
			digest.update(s.getBytes());
			byte messageDigest[] = digest.digest();

			return toHexString(messageDigest);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		return "";
	}

	public static String generate(String imageUri) {
		byte[] md5 = getMD5(CommonUtils.UrlToFileName(imageUri).getBytes());
		BigInteger bi = new BigInteger(md5).abs();
		return bi.toString(RADIX);
	}

	private static final String HASH_ALGORITHM = "MD5";
	private static final int RADIX = 10 + 26; // 10 digits + 26 letters

	private static byte[] getMD5(byte[] data) {
		byte[] hash = null;
		try {
			MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
			digest.update(data);
			hash = digest.digest();
		} catch (NoSuchAlgorithmException e) {
			L.e(e);
		}
		return hash;
	}

	/**
	 * 获取系统可用内存
	 * 
	 */
	public static long getSystemMemoryInfo(Context context) {
		ActivityManager myActivityManager = (ActivityManager) context
				.getSystemService(Activity.ACTIVITY_SERVICE);
		// 然后获得MemoryInfo类型对象
		ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
		// 然后，使用getMemoryInfo(memoryInfo)方法获得系统可用内存，此方法将内存大小保存在memoryInfo对象上
		myActivityManager.getMemoryInfo(memoryInfo);
		// 然后，memoryInfo对象上的availmem值即为所求
		return memoryInfo.availMem;
	}

	private static final String EXTERNAL_STORAGE_PERMISSION = "android.permission.WRITE_EXTERNAL_STORAGE";

	private static boolean hasExternalStoragePermission(Context context) {
		int perm = context
				.checkCallingOrSelfPermission(EXTERNAL_STORAGE_PERMISSION);
		return perm == PackageManager.PERMISSION_GRANTED;
	}

	/**
	 * 获取图片缓存地址
	 * 
	 */
	public static File getImageCachePath(Context context) {
		return getOwnCacheDirectory(context, "kankanxinwen/cache/image");

	}

	/**
	 * 获取视频缓存地址
	 * 
	 */
	public static File getVideoCachePath(Context context) {
		return getOwnCacheDirectory(context, "kankanxinwen/Cache/video");
	}

	/**
	 * Returns specified application cache directory. Cache directory will be
	 * created on SD card by defined path if card is mounted and app has
	 * appropriate permission. Else - Android defines cache directory on
	 * device's file system.
	 *
	 * @param context
	 *            Application context
	 * @param cacheDir
	 *            Cache directory path (e.g.: "AppCacheDir",
	 *            "AppDir/cache/images")
	 * @return Cache {@link File directory}
	 */
	public static File getOwnCacheDirectory(Context context, String cacheDir) {
		File appCacheDir = null;
		if (MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
				&& hasExternalStoragePermission(context)) {
			appCacheDir = new File(Environment.getExternalStorageDirectory(),
					cacheDir);
		}
		if (appCacheDir == null
				|| (!appCacheDir.exists() && !appCacheDir.mkdirs())) {
			appCacheDir = context.getCacheDir();
		}
		return appCacheDir;
	}

	/**
	 * 将URL中文件名分析出来 为防止冲突 取倒数第二个斜杠
	 * 
	 * @param url
	 * @return
	 */
	public static String UrlToFileName(String url) {
		if (url.length() > 0) {
			String[] strs = url.split("/");
			return strs[strs.length - 2] + strs[strs.length - 1];
		} else {
			return "";
		}
	}

	/**
	 * 将URL中各式取出
	 * 
	 * @param url
	 * @return
	 */
	public static String UrlToFileFormat(String url) {
		if (url.length() > 0) {
			int lastIndexOf = url.lastIndexOf('.');
			return url.substring(lastIndexOf + 1, url.length());
		} else {
			return "";
		}
	}

	/**
	 * 取得视频大小
	 * 
	 * @return
	 */
	public static File getVideoFile(Context context, Content_News content_News) {

		File videoFile = new File(getVideoCachePath(context),
				UrlToFileName(content_News.getMp4url()));
		if (videoFile.exists()) {
			return videoFile;
		} else {
			return null;
		}
	}

	/**
	 * 获取版本号
	 * 
	 * @return 当前应用的版本号
	 */
	public static String getVersion(Context context) {
		try {
			PackageManager manager = context.getPackageManager();
			PackageInfo info = manager.getPackageInfo(context.getPackageName(),
					0);
			String version = info.versionName;
			return version;
		} catch (Exception e) {
			e.printStackTrace();
			return "no_version";
		}
	}

	/**
	 * 将数字转化为固定格式
	 * 
	 * @param str
	 * @return
	 */
	public static String NumFormat(String str) {
		NumberFormat nf = new DecimalFormat("###,###,###");
		String str_result = nf.format(Integer.valueOf(str));
		return str_result;
	}

	/**
	 * 统计点击事件
	 */
	public static void clickevent(Context context, String key, String value,
			String id) {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(key, value);
		MobclickAgent.onEvent(context, id, map);
	}

	/**
	 * 递归删除目录下的所有文件及子目录下所有文件
	 * 
	 * @param dir
	 *            将要删除的文件目录
	 * @return boolean Returns "true" if all deletions were successful. If a
	 *         deletion fails, the method stops attempting to delete and returns
	 *         "false".
	 */
	public static boolean deleteDir(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			// 递归删除目录中的子目录下
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}
		// 目录此时为空，可以删除
		return dir.delete();
	}

	public static Bitmap addBitmapToCache(String path, Bitmap bitmap,
			Map<String, SoftReference<Bitmap>> imageCache) {
		// 强引用的Bitmap对象
		// 软引用的Bitmap对象
		SoftReference<Bitmap> softBitmap = new SoftReference<Bitmap>(bitmap);
		// 添加该对象到Map中使其缓存
		imageCache.put(path, softBitmap);
		return softBitmap.get();
	}

	public static Bitmap getBitmapByPath(String path,
			Map<String, SoftReference<Bitmap>> imageCache) {
		// 从缓存中取软引用的Bitmap对象
		SoftReference<Bitmap> softBitmap = imageCache.get(path);
		// 判断是否存在软引用
		if (softBitmap == null) {
			return null;
		}
		// 取出Bitmap对象，如果由于内存不足Bitmap被回收，将取得空
		Bitmap bitmap = softBitmap.get();
		return bitmap;
	}

	// 压缩图片后再显示
	public static void zoomImage(final ImageLoader imageLoader,
			final String titlepic, final ImageView titlepic2,
			final Context context,
			final Map<String, SoftReference<Bitmap>> imageCache) {

		String tag = (String) titlepic2.getTag();

		titlepic2.setTag(titlepic);
		Boolean isLoad = (Boolean) titlepic2.getTag(R.string.image_isloadcom);
		if (tag == null || !tag.equalsIgnoreCase(titlepic)
				|| (isLoad == null || !isLoad)) {

			Bitmap bitmapByPath = getBitmapByPath(titlepic, imageCache);

			if (bitmapByPath == null) {
				titlepic2.setTag(R.string.image_isloadcom, false);
				final Integer viewwidth = (Integer) titlepic2
						.getTag(R.string.viewwidth);
				final int viewWidth = titlepic2.getWidth();
				final int viewHeight = titlepic2.getHeight();
				final File file = new File(
						CommonUtils.getImageCachePath(context),
						CommonUtils.generate(titlepic));
				final BitmapFactory.Options options = new BitmapFactory.Options();

				if (!file.exists()) {
					titlepic2.setTag(titlepic);
					imageLoader.loadImage(titlepic, new ImageLoadingListener() {
						@Override
						public void onLoadingStarted(String arg0, View arg1) {
							titlepic2
									.setImageResource(R.drawable.default_news_display);
						}

						@Override
						public void onLoadingFailed(String arg0, View arg1,
								FailReason arg2) {
							titlepic2
									.setImageResource(R.drawable.default_news_display);
						}

						@Override
						public void onLoadingComplete(String arg0, View arg1,
								final Bitmap arg2) {
							AsyncTask asyncTask = new AsyncTask<Object, Object, Bitmap>() {

								private int whb;

								protected Bitmap doInBackground(Object[] params) {

									// int byteCount = arg2.getHeight();
									// int rowBytes = arg2.getRowBytes();
									// int mem = byteCount * rowBytes;
									// int size = 90;
									// if (mem > 1024 * 8 && mem < 30 * 1024 *
									// 8) {
									// size = 80;
									// } else if (mem > 30 * 1024 * 8
									// && mem < 40 * 1024 * 8) {
									// size = 70;
									// } else if (mem >= 40 * 1024 * 8
									// && mem < 60 * 1024 * 8) {
									// size = 60;
									// } else if (mem >= 60 * 1024 * 8
									// && mem < 90 * 1024 * 8) {
									// size = 50;
									// } else if (mem >= 90 * 1024 * 8) {
									// size = 40;
									// }

									int width = arg2.getWidth();
									int height = arg2.getHeight();

									// whb=Math.round((float) width / (float)
									// height);

									int insampSize = 0;

									if (viewWidth == 0 && viewHeight == 0) {
										insampSize += Math.round((float) width
												/ (float) viewwidth);
									} else if (width > viewWidth
											|| height > viewHeight) {
										int wround = Math.round((float) width
												/ (float) viewWidth);
										int hround = Math.round((float) height
												/ (float) viewHeight);
										insampSize += (wround > hround ? wround
												: hround) + 2;
									}
									try {
										CompressFormat format = null;
										if ("png"
												.equalsIgnoreCase(UrlToFileFormat(titlepic))) {
											format = CompressFormat.PNG;
										} else {
											format = CompressFormat.JPEG;
										}

										boolean compress = arg2.compress(
												format, 90,
												new FileOutputStream(file));
										options.inPreferredConfig = Bitmap.Config.RGB_565;
										options.inSampleSize = insampSize >= 6 ? insampSize - 2
												: insampSize;
										Bitmap decodeFile = BitmapFactory
												.decodeFile(
														file.getAbsolutePath(),
														options);

										if (arg2 != null) {
											arg2.recycle();
										}
										return decodeFile;
									} catch (FileNotFoundException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									} catch (OutOfMemoryError e) {
										System.gc();
										imageLoader.clearMemoryCache();
									}
									return null;

								};

								protected void onPostExecute(Bitmap result) {
									String tag = (String) titlepic2.getTag();
									if (titlepic.equalsIgnoreCase(tag)) {
										// if (whb != 0
										// && titlepic2.getTag(R.string.graphic)
										// !=
										// null
										// && "graphic"
										// .equalsIgnoreCase((String) titlepic2
										// .getTag(R.string.graphic))) {
										// titlepic2.getLayoutParams().height =
										// viewwidth
										// / whb;
										// }
										if (result != null) {
											titlepic2.setTag(
													R.string.image_isloadcom,
													true);
											titlepic2
													.setImageBitmap(addBitmapToCache(
															titlepic, result,
															imageCache));
										}
									}

								};

							};
							asyncTask.execute();
						}

						@Override
						public void onLoadingCancelled(String arg0, View arg1) {
							titlepic2
									.setImageResource(R.drawable.default_news_display);
						}
					});
				} else {
					titlepic2.setImageResource(R.drawable.default_news_display);

					AsyncTask asyncTask = new AsyncTask<Object, Object, Bitmap>() {

						private int whb;

						@Override
						protected Bitmap doInBackground(Object... params) {

							try {
								options.inPreferredConfig = Bitmap.Config.RGB_565;
								options.inJustDecodeBounds = true;

								Bitmap decodeFile = BitmapFactory.decodeFile(
										file.getAbsolutePath(), options);

								int height = options.outHeight;
								int width = options.outWidth;
								// whb=Math.round((float) width / (float)
								// height);
								int insampSize = 0;
								if (viewWidth == 0 && viewHeight == 0) {
									if (viewwidth != null) {
										insampSize += Math.round((float) width
												/ (float) viewwidth);
									} else {
										insampSize = 4;
									}
								} else if (width > viewWidth
										|| height > viewHeight) {

									int wround = Math.round((float) width
											/ (float) viewWidth);
									int hround = Math.round((float) height
											/ (float) viewHeight);
									insampSize += (wround > hround ? wround
											: hround) + 2;
								}

								options.inPreferredConfig = Bitmap.Config.RGB_565;
								options.inSampleSize = insampSize >= 6 ? insampSize - 2
										: insampSize;
								options.inJustDecodeBounds = false;
								Bitmap decodeFilelock = BitmapFactory
										.decodeFile(file.getAbsolutePath(),
												options);

								return decodeFilelock;
							} catch (Exception e) {
								e.printStackTrace();
							} catch (OutOfMemoryError e) {
								System.gc();
								imageLoader.clearMemoryCache();
							}
							return null;
						}

						@Override
						protected void onPostExecute(Bitmap result) {
							String tag = (String) titlepic2.getTag();
							if (titlepic.equalsIgnoreCase(tag)) {
								if (result != null) {
									titlepic2.setImageBitmap(addBitmapToCache(
											titlepic, result, imageCache));
								}
							}
						}
					};
					asyncTask.execute();
				}
			} else {
				String tag1 = (String) titlepic2.getTag();
				if (titlepic.equalsIgnoreCase(tag1)) {
					if (bitmapByPath != null) {
						titlepic2.setTag(R.string.image_isloadcom, true);
						titlepic2.setImageBitmap(bitmapByPath);
					}
				}
			}
		}

	}

	// 压缩图片后再显示
	public static void zoomImage(ImageLoader imageLoader,
			final String titlepic, final ImageView titlepic2,
			final Context context) {

		final Boolean isTop = (Boolean) titlepic2.getTag(R.string.isTop);
		String tag = (String) titlepic2.getTag();
		titlepic2.setTag(titlepic);
		Boolean isLoad = (Boolean) titlepic2.getTag(R.string.image_isloadcom);
		if (tag == null || !tag.equalsIgnoreCase(titlepic)
				|| (isLoad == null || !isLoad)) {

			titlepic2.setTag(R.string.image_isloadcom, false);
			final Integer viewwidth = (Integer) titlepic2
					.getTag(R.string.viewwidth);
			final int viewWidth = titlepic2.getWidth();
			final int viewHeight = titlepic2.getHeight();
			File file = null;
			if (isTop == null || !isTop) {
				file = new File(CommonUtils.getImageCachePath(context),
						CommonUtils.generate(titlepic));
			} else {
				file = new File(CommonUtils.getImageCachePath(context), "big_"
						+ CommonUtils.generate(titlepic));
			}

			final BitmapFactory.Options options = new BitmapFactory.Options();

			if (!file.exists()) {
				titlepic2.setTag(titlepic);
				imageLoader.loadImage(titlepic, new ImageLoadingListener() {
					@Override
					public void onLoadingStarted(String arg0, View arg1) {
						titlepic2
								.setImageResource(R.drawable.default_news_display);
					}

					@Override
					public void onLoadingFailed(String arg0, View arg1,
							FailReason arg2) {
						titlepic2
								.setImageResource(R.drawable.default_news_display);
					}

					@Override
					public void onLoadingComplete(String arg0, View arg1,
							final Bitmap arg2) {
						AsyncTask asyncTask = new AsyncTask<Object, Object, Bitmap>() {

							private int whb;

							protected Bitmap doInBackground(Object[] params) {

								// int byteCount = arg2.getHeight();
								// int rowBytes = arg2.getRowBytes();
								// int mem = byteCount * rowBytes;
								// int size = 90;
								// if (mem > 1024 * 8 && mem < 30 * 1024 *
								// 8) {
								// size = 80;
								// } else if (mem > 30 * 1024 * 8
								// && mem < 40 * 1024 * 8) {
								// size = 70;
								// } else if (mem >= 40 * 1024 * 8
								// && mem < 60 * 1024 * 8) {
								// size = 60;
								// } else if (mem >= 60 * 1024 * 8
								// && mem < 90 * 1024 * 8) {
								// size = 50;
								// } else if (mem >= 90 * 1024 * 8) {
								// size = 40;
								// }

								int width = arg2.getWidth();
								int height = arg2.getHeight();

								// whb=Math.round((float) width / (float)
								// height);

								int insampSize = 0;

								try {
									CompressFormat format = null;
									if ("png"
											.equalsIgnoreCase(UrlToFileFormat(titlepic))) {
										format = CompressFormat.PNG;
									} else {
										format = CompressFormat.JPEG;
									}
									File file = null;
									if (isTop == null || !isTop) {
										file = new File(CommonUtils
												.getImageCachePath(context),
												CommonUtils.generate(titlepic));
										boolean compress = arg2.compress(
												format, 75,
												new FileOutputStream(file));

										if (viewWidth == 0 && viewHeight == 0) {
											insampSize += Math
													.round((float) width
															/ (float) viewwidth);
										} else if (width > viewWidth
												|| height > viewHeight) {
											int wround = Math
													.round((float) width
															/ (float) viewWidth);
											int hround = Math
													.round((float) height
															/ (float) viewHeight);
											insampSize += (wround > hround ? wround
													: hround) + 2;
										}

									} else {
										file = new File(
												CommonUtils
														.getImageCachePath(context),
												"big_"
														+ CommonUtils
																.generate(titlepic));

										boolean compress = arg2.compress(
												format, 100,
												new FileOutputStream(file));

										if (viewWidth == 0 && viewHeight == 0) {
											insampSize += Math
													.round((float) width
															/ (float) viewwidth);
										}

										if (insampSize > 4) {
											insampSize = insampSize - 2;
										}

									}

									options.inPreferredConfig = Bitmap.Config.RGB_565;
									options.inSampleSize = insampSize;
									Bitmap decodeFile = BitmapFactory
											.decodeFile(file.getAbsolutePath(),
													options);

									if (arg2 != null) {
										arg2.recycle();
									}

									return new SoftReference<Bitmap>(decodeFile)
											.get();
								} catch (FileNotFoundException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								return null;

							};

							protected void onPostExecute(Bitmap result) {
								String tag = (String) titlepic2.getTag();
								if (titlepic.equalsIgnoreCase(tag)) {
									// if (whb != 0
									// && titlepic2.getTag(R.string.graphic)
									// !=
									// null
									// && "graphic"
									// .equalsIgnoreCase((String) titlepic2
									// .getTag(R.string.graphic))) {
									// titlepic2.getLayoutParams().height =
									// viewwidth
									// / whb;
									// }
									if (result != null) {
										titlepic2.setTag(
												R.string.image_isloadcom, true);
										titlepic2.setImageBitmap(result);

									}
								}

							};

						};
						asyncTask.execute();
					}

					@Override
					public void onLoadingCancelled(String arg0, View arg1) {
						titlepic2
								.setImageResource(R.drawable.default_news_display);
					}
				});
			} else {
				titlepic2.setImageResource(R.drawable.default_news_display);

				AsyncTask asyncTask = new AsyncTask<Object, Object, Bitmap>() {

					private int whb;

					@Override
					protected Bitmap doInBackground(Object... params) {

						try {
							options.inPreferredConfig = Bitmap.Config.RGB_565;
							options.inJustDecodeBounds = true;
							int insampSize = 0;
							File file = null;

							if (isTop == null || !isTop) {

								file = new File(
										CommonUtils.getImageCachePath(context),
										CommonUtils.generate(titlepic));

								Bitmap decodeFile = BitmapFactory.decodeFile(
										file.getAbsolutePath(), options);

								int height = options.outHeight;
								int width = options.outWidth;

								if (viewWidth == 0 && viewHeight == 0) {
									if (viewwidth != null) {
										insampSize += Math.round((float) width
												/ (float) viewwidth);
									} else {
										insampSize = 4;
									}
								} else if (width > viewWidth
										|| height > viewHeight) {

									int wround = Math.round((float) width
											/ (float) viewWidth);
									int hround = Math.round((float) height
											/ (float) viewHeight);
									insampSize += (wround > hround ? wround
											: hround) + 2;
								}

							} else {
								file = new File(
										CommonUtils.getImageCachePath(context),
										"big_" + CommonUtils.generate(titlepic));

								Bitmap decodeFile = BitmapFactory.decodeFile(
										file.getAbsolutePath(), options);

								int height = options.outHeight;
								int width = options.outWidth;

								if (viewWidth == 0 && viewHeight == 0) {
									if (viewwidth != null) {
										insampSize += Math.round((float) width
												/ (float) viewwidth);
									}
								}

								if (insampSize > 4) {
									insampSize = insampSize - 2;
								}

							}

							// whb=Math.round((float) width / (float)
							// height);

							options.inPreferredConfig = Bitmap.Config.RGB_565;
							options.inSampleSize = insampSize;
							options.inJustDecodeBounds = false;
							Bitmap decodeFilelock = BitmapFactory.decodeFile(
									file.getAbsolutePath(), options);

							return new SoftReference<Bitmap>(decodeFilelock)
									.get();
						} catch (Exception e) {
							e.printStackTrace();
						}
						return null;
					}

					@Override
					protected void onPostExecute(Bitmap result) {
						String tag = (String) titlepic2.getTag();
						if (titlepic.equalsIgnoreCase(tag)) {
							if (result != null) {
								titlepic2.setImageBitmap(result);
							}
						}
					}
				};
				asyncTask.execute();
			}
		}

	}

	// // 压缩图片后再显示
	// public static void zoomImageTX(ImageLoader imageLoader,
	// final String titlepic, final ImageView titlepic2,
	// final Context context) {
	// titlepic2.setTag(titlepic);
	//
	// final Integer viewwidth = (Integer) titlepic2
	// .getTag(R.string.viewwidth);
	// final int viewWidth = titlepic2.getWidth();
	// final int viewHeight = titlepic2.getHeight();
	// final File file = new File(CommonUtils.getImageCachePath(context),
	// CommonUtils.UrlToFileName(titlepic));
	// final BitmapFactory.Options options = new BitmapFactory.Options();
	// options.inPreferredConfig = Bitmap.Config.RGB_565;
	//
	// if (!file.exists()) {
	// imageLoader.loadImage(titlepic, new ImageLoadingListener() {
	// @Override
	// public void onLoadingStarted(String arg0, View arg1) {
	// titlepic2
	// .setImageResource(R.drawable.default_news_report_pic);
	// }
	//
	// @Override
	// public void onLoadingFailed(String arg0, View arg1,
	// FailReason arg2) {
	// titlepic2
	// .setImageResource(R.drawable.default_news_report_pic);
	// }
	//
	// @Override
	// public void onLoadingComplete(String arg0, View arg1,
	// Bitmap arg2) {
	// int width = arg2.getWidth();
	// int height = arg2.getHeight();
	//
	// int byteCount = arg2.getHeight();
	// int rowBytes = arg2.getRowBytes();
	// int mem = byteCount * rowBytes;
	// int size = 100;
	// if (mem > 1024 * 8 && mem < 30 * 1024 * 8) {
	// size = 90;
	// } else if (mem > 30 * 1024 * 8 && mem < 40 * 1024 * 8) {
	// size = 75;
	// } else if (mem >= 40 * 1024 * 8 && mem < 60 * 1024 * 8) {
	// size = 60;
	// } else if (mem >= 60 * 1024 * 8 && mem < 90 * 1024 * 8) {
	// size = 30;
	// } else if (mem >= 90 * 1024 * 8) {
	// size = 20;
	// }
	//
	// int insampSize = 0;
	//
	// if (viewWidth == 0 && viewHeight == 0) {
	//
	// if (viewwidth == null) {
	// insampSize += Math.round((float) width
	// / (float) viewwidth);
	// } else {
	// insampSize = 4;
	// }
	// } else if (width > viewWidth || height > viewHeight) {
	// int wround = Math.round((float) width
	// / (float) viewWidth);
	// int hround = Math.round((float) height
	// / (float) viewHeight);
	// insampSize += (wround > hround ? wround : hround) + 2;
	// }
	// try {
	// boolean compress = arg2.compress(CompressFormat.JPEG,
	// size, new FileOutputStream(file));
	// options.inPreferredConfig = Bitmap.Config.RGB_565;
	// options.inSampleSize = insampSize;
	// Bitmap decodeFile = BitmapFactory.decodeFile(
	// file.getAbsolutePath(), options);
	// if (arg2 != null) {
	// arg2.recycle();
	// arg2 = null;
	// }
	// SoftReference<Bitmap> softBitmap = new SoftReference<Bitmap>(
	// decodeFile);
	// String tag = (String) titlepic2.getTag();
	// if (titlepic.equalsIgnoreCase(tag)) {
	// titlepic2.setImageBitmap(softBitmap.get());
	// }
	// } catch (FileNotFoundException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	//
	// }
	//
	// @Override
	// public void onLoadingCancelled(String arg0, View arg1) {
	// titlepic2
	// .setImageResource(R.drawable.default_news_report_pic);
	// }
	// });
	// } else {
	// titlepic2.setImageResource(R.drawable.default_news_report_pic);
	// options.inPreferredConfig = Bitmap.Config.RGB_565;
	// options.inJustDecodeBounds = true;
	//
	// Bitmap decodeFile = BitmapFactory.decodeFile(
	// file.getAbsolutePath(), options);
	//
	// int height = options.outHeight;
	// int width = options.outWidth;
	// int insampSize = 0;
	// if (width > viewWidth || height > viewHeight) {
	// int wround = Math.round((float) width / (float) viewWidth);
	// int hround = Math.round((float) height / (float) viewHeight);
	// insampSize += (wround > hround ? wround : hround) + 2;
	// }
	//
	// options.inPreferredConfig = Bitmap.Config.RGB_565;
	// options.inSampleSize = insampSize;
	// options.inJustDecodeBounds = false;
	// Bitmap decodeFilelock = BitmapFactory.decodeFile(
	// file.getAbsolutePath(), options);
	// SoftReference<Bitmap> softBitmap = new SoftReference<Bitmap>(
	// decodeFilelock);
	// String tag = (String) titlepic2.getTag();
	// if (titlepic.equalsIgnoreCase(tag)) {
	// titlepic2.setImageBitmap(softBitmap.get());
	// }
	// }
	//
	// }

	/**
	 * 加载本地大图片
	 */
	public static void loadLockBigBitmap(Context context, View v, int res,
			int inSampleSize) {
		try {
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inSampleSize = inSampleSize;
			Bitmap decodeResource = BitmapFactory.decodeResource(
					context.getResources(), res, opts);
			v.setBackground(new BitmapDrawable(decodeResource));
			v.setVisibility(View.VISIBLE);
		} catch (OutOfMemoryError e) {
			loadLockBigBitmap(context, v, res, inSampleSize + 2);
		}
	}

	public static void zoomImageGIF(final ImageLoader imageLoader,
			final String urlPath, final GifView gifView, final Context context) {

		final File file = new File(CommonUtils.getImageCachePath(context),
				CommonUtils.generate(urlPath));
		gifView.setBackgroundResource(R.drawable.default_news_display);
		if (!file.exists()) {

			HttpUtils httpUtils = new HttpUtils();

			httpUtils.download(urlPath, file.getAbsolutePath(), false,
					new RequestCallBack<File>() {

						@Override
						public void onSuccess(ResponseInfo<File> arg0) {
							zoomImageGIF(imageLoader, urlPath, gifView, context);
						}

						@Override
						public void onFailure(HttpException arg0, String arg1) {
						}
					});
		} else {
			gifView.setBackgroundColor(android.R.color.transparent);
			InputStream rawFile = null;
			try {
				rawFile = new FileInputStream(file);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			gifView.setGifImage(rawFile);
		}
	}
}
