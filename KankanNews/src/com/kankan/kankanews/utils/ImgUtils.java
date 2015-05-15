package com.kankan.kankanews.utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;
import android.content.Context;

import com.kankan.kankanews.config.AndroidConfig;
import com.kankanews.kankanxinwen.R;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

public class ImgUtils {
	public static ImageLoader imageLoader = ImageLoader.getInstance();
	public static DisplayImageOptions homeImageOptions;
	public static ImageLoaderConfiguration picSetImageOptions;
	
	static {
		homeImageOptions = new DisplayImageOptions.Builder().cacheInMemory(true)// 设置下载的图片是否缓存在内存中
				.cacheOnDisc(true)// 设置下载的图片是否缓存在SD卡中
				 .showImageOnLoading(R.drawable.default_news_display) //设置图片在下载期间显示的图片  
				 .showImageForEmptyUri(R.drawable.default_news_display)//设置图片Uri为空或是错误的时候显示的图片  
				.showImageOnFail(R.drawable.default_news_display)  //设置图片加载/解码过程中错误时候显示的图片
				.imageScaleType(ImageScaleType.IN_SAMPLE_INT)// 设置图片以如何的编码方式显示
				.bitmapConfig(Bitmap.Config.RGB_565)// 设置图片的解码类型//  EXACTLY_STRETCHED
				.decodingOptions(new BitmapFactory.Options())// 设置图片的解码配置
				// .delayBeforeLoading(int delayInMillis)//int
				// delayInMillis为你设置的下载前的延迟时间
				// 设置图片加入缓存前，对bitmap进行设置
				// .preProcessor(BitmapProcessor preProcessor)
				.resetViewBeforeLoading(true)// 设置图片在下载前是否重置，复位
				.build();// 构建完成
	}
	
	public static Map<String, String> sendImage(String fileUrl) {
		Map<String, String> result = new HashMap<String, String>();
		HttpURLConnection conn = null;
		DataOutputStream outStream = null;
		ByteArrayInputStream tarFile = null;
		BufferedReader responseReader = null;
		try {
			File srcFile = new File(fileUrl);
			String uriAPI = AndroidConfig.REVELATIONS_IMAGE_POST;
			String BOUNDARY = java.util.UUID.randomUUID().toString();
			String PREFIX = "--", LINEND = "\r\n";
			String MULTIPART_FROM_DATA = "multipart/form-data";
			String CHARSET = "UTF-8";

			URL uri = new URL(uriAPI);

			StringBuilder sb1 = new StringBuilder();
			sb1.append(PREFIX);
			sb1.append(BOUNDARY);
			sb1.append(LINEND);
			sb1.append("Content-Disposition: form-data; name=\"file\"; filename=\""
					+ "test.jpg" + "\"" + LINEND);
			sb1.append("Content-Type: multipart/form-data; charset=" + CHARSET
					+ LINEND);
			sb1.append(LINEND);

			StringBuilder sb2 = new StringBuilder();
			sb2.append(LINEND);
			sb2.append(PREFIX);
			sb2.append(BOUNDARY);
			sb2.append(PREFIX);

			conn = (HttpURLConnection) uri.openConnection();
			conn.setReadTimeout(5 * 1000);
			conn.setDoInput(true);// 允许输入
			conn.setDoOutput(true);// 允许输出
			conn.setUseCaches(false);
			conn.setRequestMethod("POST"); // Post方式
			conn.setRequestProperty("connection", "keep-alive");

			ByteArrayOutputStream fileOut = getSmallBitmap(fileUrl);
			
			long fileLength = fileOut.toByteArray().length;

			Log.e("UPLOAD_FILE_LENGTH", fileLength + "");
			tarFile = new ByteArrayInputStream(fileOut.toByteArray());

			long contentLength = fileLength + sb1.toString().getBytes().length
					+ sb2.toString().getBytes().length;

			conn.setRequestProperty("Content-Length", contentLength + "");
			conn.setRequestProperty("Charsert", "UTF-8");
			conn.setRequestProperty("Content-Type", MULTIPART_FROM_DATA
					+ ";boundary=" + BOUNDARY);

			outStream = new DataOutputStream(conn.getOutputStream());
			outStream.write(sb1.toString().getBytes());

			byte[] buffer = new byte[8192];
			int len = 0;
			while ((len = tarFile.read(buffer)) != -1) {
				outStream.write(buffer, 0, len);
			}
			outStream.write(sb2.toString().getBytes());

			responseReader = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));
			StringBuffer responseContent = new StringBuffer();
			while (responseReader.ready()) {
				responseContent.append(responseReader.readLine());
			}
			result.put("ResponseCode", conn.getResponseCode() + "");
			result.put("ResponseContent", responseContent.toString());

		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.e("IMG_UTILS", e.getLocalizedMessage(), e);
			result.put("ResponseCode", "ERROR");
			result.put("ResponseContent", "ERROR");
		} finally {
			try {
				tarFile.close();
				outStream.close();
				conn.getInputStream().close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Log.e("IMG_UTILS", e.getLocalizedMessage(), e);
			}
		}
		return result;
	}

	public static Map<String, String> sendRevelationsContent(String tel,
			String content, String imgUrls) {
		Map<String, String> result = new HashMap<String, String>();
		HttpPost httpPost = new HttpPost(AndroidConfig.REVELATIONS_CONTENT_POST);
		// 设置HTTP POST请求参数必须用NameValuePair对象
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("phonenum", tel));
		params.add(new BasicNameValuePair("newstext", content));
		params.add(new BasicNameValuePair("imagegroup", imgUrls));
		HttpResponse httpResponse = null;
		try {
			// 设置httpPost请求参数
			httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			httpResponse = new DefaultHttpClient().execute(httpPost);

			result.put("ResponseCode", httpResponse.getStatusLine()
					.getStatusCode() + "");
			result.put("ResponseContent",
					EntityUtils.toString(httpResponse.getEntity()));
			return result;
		} catch (ClientProtocolException e) {
			Log.e("IMG_UTILS", e.getLocalizedMessage(), e);
		} catch (IOException e) {
			Log.e("IMG_UTILS", e.getLocalizedMessage(), e);
		}
		result.put("ResponseCode", "ERROR");
		result.put("ResponseContent", "ERROR");
		return result;
	}
	
	public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
    // Raw height and width of image
    final int height = options.outHeight;
    final int width = options.outWidth;
    int inSampleSize = 1;

    if (height > reqHeight || width > reqWidth) {
        if (width > height) {
            inSampleSize = Math.round((float)height / (float)reqHeight);
        } else {
            inSampleSize = Math.round((float)width / (float)reqWidth);
        }
        return inSampleSize + 1;
    }
    return inSampleSize;
}
	
	public static Bitmap decodeImage(String path, int showWidth, int showHeight) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);

		options.inJustDecodeBounds = false;
		if (showWidth != 0 && showHeight != 0)
			options.inSampleSize = calculateInSampleSize(options, showWidth,
					showHeight);
		options.inPreferredConfig = Bitmap.Config.RGB_565;
		return BitmapFactory.decodeFile(path, options);
	}

	public static ByteArrayOutputStream getSmallBitmap(String filePath) {
		Bitmap bm = decodeImage(filePath, 600, 800);
		long fileLength = new File(filePath).length();
		int scale = 60;
		if (fileLength > 2097152)
			scale = 20;
		if (fileLength <= 2097152 && fileLength > 1572864)
			scale = 40;
		if (fileLength <= 1572864 && fileLength > 1048576)
			scale = 60;
		if (fileLength <= 1048576 && fileLength > 524288)
			scale = 70;
		if (fileLength <= 524288 && fileLength > 262144)
			scale = 80;
		if (fileLength <= 262144)
			scale = 100;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        if (filePath.endsWith(".jpg") || filePath.endsWith(".JPG"))
        	bm.compress(Bitmap.CompressFormat.JPEG, scale, baos);
		if (filePath.endsWith(".jpeg") || filePath.endsWith(".JPEG"))
			bm.compress(Bitmap.CompressFormat.JPEG, scale, baos);
		if (filePath.endsWith(".png") || filePath.endsWith(".PNG"))
			bm.compress(Bitmap.CompressFormat.JPEG, scale, baos);
		return baos;
	}

	public static Bitmap getNetImage(String url) {
		try {
			// 建立网络连接
			URL imageURl = new URL(url);
			URLConnection con = imageURl.openConnection();
			con.connect();
			InputStream in = con.getInputStream();
			return BitmapFactory.decodeStream(in);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.e("getNetImage", e.getLocalizedMessage(), e);
			return null;
		}
	}
	
}
