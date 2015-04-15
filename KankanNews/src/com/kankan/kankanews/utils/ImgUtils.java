package com.kankan.kankanews.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
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

import android.util.Log;

import com.kankan.kankanews.config.AndroidConfig;
import com.umeng.message.proguard.T;

public class ImgUtils {
	
	public static Map<String, String> sendImage(String fileUrl) {
		Map<String, String> result = new HashMap<String, String>();
		HttpURLConnection conn = null;
		DataOutputStream outStream = null;
		FileInputStream tarFile = null;
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

			long contentLength = srcFile.length()
					+ sb1.toString().getBytes().length
					+ sb2.toString().getBytes().length;

			conn.setRequestProperty("Content-Length", contentLength + "");
			conn.setRequestProperty("Charsert", "UTF-8");
			conn.setRequestProperty("Content-Type", MULTIPART_FROM_DATA
					+ ";boundary=" + BOUNDARY);

			// String topStr =
			// "{\"phonenum\":\"11111111111\",\"newstext\":\"asdfasdfzchfadjf\",\"imagenum\":1,\"imagegroup\":{\"0\":{\"filename\":\"test.jpg\",\"base64file\":\"";
			// String bottomStr = "\"}}}";

			outStream = new DataOutputStream(
					conn.getOutputStream());
			outStream.write(sb1.toString().getBytes());
			// outStream.write(sb1.toString().getBytes());
			// outStream.write(topStr.toString().getBytes());
			// InputStream is =
			// act.getResources().openRawResource(R.drawable.test);
			tarFile = new FileInputStream(srcFile);
			// InputStream is = new FileInputStream(file.getValue());

			byte[] buffer = new byte[8192];
			int len = 0;
			while ((len = tarFile.read(buffer)) != -1) {
				outStream.write(buffer, 0, len);
			}
			outStream.write(sb2.toString().getBytes());
			// outStream.write(bottomStr.toString().getBytes());
			// outStream.write(LINEND.getBytes());
			// InputStream inpu = conn.getInputStream();
			// StringBuffer buf = new StringBuffer();
			// byte[] bufferR = new byte[1024];
			// while ((len = is.read(buffer)) != -1) {
			// buf.append(new String(buffer));
			// }
//			Log.i("IMAGE_BASE", conn.getResponseCode() + "");
//			Log.i("IMAGE_BASE", conn.getResponseMessage());

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
			Log.e("IMG_UTILS", e.getLocalizedMessage());
			result.put("ResponseCode", "ERROR");
			result.put("ResponseContent", "ERROR");
		} finally {
			try {
				tarFile.close();
				outStream.close();
				conn.getInputStream().close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Log.e("IMG_UTILS", e.getLocalizedMessage());
			}
		}
		return result;
	}
	
	public static Map<String, String> sendRevelationsContent(String tel, String content, String imgUrls){
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
            //System.out.println(httpResponse.getStatusLine().getStatusCode()); 
//            if (httpResponse.getStatusLine().getStatusCode() == 200) { 
//                // 第三步，使用getEntity方法活得返回结果 
//                String result = EntityUtils.toString(httpResponse.getEntity()); 
//            } 
			result.put("ResponseCode", httpResponse.getStatusLine().getStatusCode() + "");
			result.put("ResponseContent", EntityUtils.toString(httpResponse.getEntity()));
			return result;
        } catch (ClientProtocolException e) { 
			Log.e("IMG_UTILS", e.getLocalizedMessage());
        } catch (IOException e) { 
			Log.e("IMG_UTILS", e.getLocalizedMessage());
        }
        result.put("ResponseCode", "ERROR");
		result.put("ResponseContent", "ERROR");
		return result; 
	}
}
