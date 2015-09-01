package com.kankan.kankanews.base.download;

import java.io.File;
import java.util.Map.Entry;

import android.os.SystemClock;

import com.kankan.kankanews.base.IA.CrashApplication;
import com.kankan.kankanews.bean.Content_News; 
import com.kankan.kankanews.utils.CommonUtils;
import com.kankan.kankanews.utils.NetUtils;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

public class MyRequestCallBack extends RequestCallBack<File> {

	public enum RequestCallBackType {
		ContentRequestCallBack, OfflineRequestCallBack
	}

	private CrashApplication application = CrashApplication.getInstance();
	private NetUtils instance;
	private Content_News mContent_News;
	private DbUtils dbUtils;

	public Content_News getmContent_News() {
		return mContent_News;
	}

	public MyRequestCallBack(Content_News mContent_News, DbUtils dbUtils) {
		super();
		instance = NetUtils.getInstance(application);
		this.dbUtils = dbUtils;
		this.mContent_News = mContent_News;
	}

	@Override
	public void onStart() {
		super.onStart(); 
	}

	@Override
	public void onLoading(long total, long current, boolean isUploading) {
		super.onLoading(total, current, isUploading); 
	}

	@Override
	public void onFailure(HttpException arg0, String arg1) {
 

	}

	@Override
	public void onSuccess(ResponseInfo<File> arg0) { 
		removeSelfForDown(true);
	}

	@Override
	public void onCancelled() {
		super.onCancelled();  
	}

	public HttpHandler start() {
		String key = mContent_News.getMid();

		if (application.mRequestCallBackeds.size() < 1) {
			application.mRequestCallBackeds.put(key, this);
			HttpHandler downloadVideo = instance.downloadVideo(
					mContent_News.getMp4url(),
					CommonUtils.UrlToFileName(mContent_News.getMp4url()), this);
			application.mHttpHandlereds.put(key, downloadVideo);
			return downloadVideo;
		} else {
			application.mRequestCallBackPauses.put(key, this);
			return null;
		}
	} 

	// 将自己从下载集合中除去
	private void removeSelfForDown(final boolean isDelete) {

		new Thread() {
			@Override
			public void run() {
				if (isDelete) {
					String key = mContent_News.getMid();
					application.mHttpHandlereds.remove(key);
					application.mRequestCallBackeds.remove(key);
				}
				Entry<String, MyRequestCallBack> lastEntry = application.mRequestCallBackPauses
						.lastEntry();
				if (lastEntry != null) {
					application.mRequestCallBackPauses.remove(lastEntry
							.getKey());
					application.mRequestCallBackeds.put(lastEntry.getKey(),
							lastEntry.getValue());
					Content_News getmContent_News = lastEntry.getValue()
							.getmContent_News();

					HttpHandler downloadVideo = instance.downloadVideo(
							getmContent_News.getMp4url(),
							CommonUtils.UrlToFileName(getmContent_News
									.getMp4url()), lastEntry.getValue());
					application.mHttpHandlereds.put(lastEntry.getKey(),
							downloadVideo);
				}
			}
		}.start();

	}
}
