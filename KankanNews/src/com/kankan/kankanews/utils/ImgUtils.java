package com.kankan.kankanews.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.util.Log;

public class ImgUtils {
	public static void send(String fileUrl) {
		try {
			File srcFile = new File(fileUrl);
			String uriAPI = "http://api.app.kankanews.com/kankan/v5/test/pic/1";
			String BOUNDARY = java.util.UUID.randomUUID().toString();
			String PREFIX = "--", LINEND = "\r\n";
			String MULTIPART_FROM_DATA = "multipart/form-data";
			String CHARSET = "UTF-8";
			URL uri;
			uri = new URL(uriAPI);
			
			StringBuilder sb1 = new StringBuilder();    
            sb1.append(PREFIX);    
            sb1.append(BOUNDARY);    
            sb1.append(LINEND);    
            sb1.append("Content-Disposition: form-data; name=\"file\"; filename=\""    
                    + "test.jpg" + "\"" + LINEND);    
            sb1.append("Content-Type: multipart/form-data; charset="    
                    + CHARSET + LINEND);    
            sb1.append(LINEND);  
			
            
            StringBuilder sb2 = new StringBuilder();  
            sb2.append(LINEND); 
            sb2.append(PREFIX);    
            sb2.append(BOUNDARY);
            sb2.append(PREFIX); 
            
			HttpURLConnection conn = (HttpURLConnection) uri.openConnection();
			conn.setReadTimeout(5 * 1000);
			conn.setDoInput(true);// 允许输入
			conn.setDoOutput(true);// 允许输出
			conn.setUseCaches(false);
			conn.setRequestMethod("POST"); // Post方式
			conn.setRequestProperty("connection", "keep-alive");
			
			long contentLength = srcFile.length() + sb1.toString().getBytes().length + sb2.toString().getBytes().length;
			
			conn.setRequestProperty("Content-Length", contentLength + "");
			conn.setRequestProperty("Charsert", "UTF-8");
			conn.setRequestProperty("Content-Type", MULTIPART_FROM_DATA
					+ ";boundary=" + BOUNDARY);
			
            
//            String topStr =
//    		 "{\"phonenum\":\"11111111111\",\"newstext\":\"asdfasdfzchfadjf\",\"imagenum\":1,\"imagegroup\":{\"0\":{\"filename\":\"test.jpg\",\"base64file\":\"";
//    		String bottomStr = "\"}}}";
            
            
            DataOutputStream outStream = new DataOutputStream(    
                    conn.getOutputStream());    
            outStream.write(sb1.toString().getBytes()); 
//            outStream.write(sb1.toString().getBytes());  
//            outStream.write(topStr.toString().getBytes());    
//            InputStream is = act.getResources().openRawResource(R.drawable.test); 
            FileInputStream is = new FileInputStream(srcFile);
//            InputStream is = new FileInputStream(file.getValue());  
            
            byte[] buffer = new byte[8192];    
            int len = 0;    
            while ((len = is.read(buffer)) != -1) {    
                outStream.write(buffer, 0, len);    
            }    
            is.close();  
            outStream.write(sb2.toString().getBytes()); 
            outStream.close();
//            outStream.write(bottomStr.toString().getBytes());   
//            outStream.write(LINEND.getBytes());   
//            InputStream inpu = conn.getInputStream();
//            StringBuffer buf = new StringBuffer();
//            byte[] bufferR = new byte[1024];    
//            while ((len = is.read(buffer)) != -1) {    
//            	buf.append(new String(buffer));    
//            }    
            Log.i("IMAGE_BASE", conn.getResponseCode() + "");
            Log.i("IMAGE_BASE", conn.getResponseMessage());
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            
            while(reader.ready()){
                Log.i("IMAGE_BASE", reader.readLine());
            }
            conn.getInputStream().close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e("IMG_UTILS", e.getLocalizedMessage());
		}
	}
}
