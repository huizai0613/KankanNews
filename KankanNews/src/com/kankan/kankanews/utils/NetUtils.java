package com.kankan.kankanews.utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.Volley;
import com.kankan.kankanews.base.IA.CrashApplication;
import com.kankan.kankanews.bean.VideoUploadResult;
import com.kankan.kankanews.config.AndroidConfig;
import com.kankan.kankanews.net.CustomRequest;
import com.kankan.kankanews.net.CustomRequestArray;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.callback.RequestCallBack;

public class NetUtils {

	private static NetUtils netUtils;
	private RequestQueue mRequestQueue;
	private CustomRequest mCustomRequest;
	private CustomRequestArray mCustomRequestArray;
	private Context mContext;

	private String separator = "__";

	private NetUtils(Context mContext) {
		this.mContext = mContext;
		mRequestQueue = Volley.newRequestQueue(mContext);
	};

	public static NetUtils getInstance(Context mContext) {
		if (netUtils == null) {
			netUtils = new NetUtils(mContext);
		}
		return netUtils;
	}

	/**
	 * 获取首页数据
	 */
	public void getMainData(String id, Listener<JSONObject> reponseListener,
			ErrorListener errorListener) {
		mCustomRequest = new CustomRequest(Request.Method.POST,
				AndroidConfig.NETHOST + AndroidConfig.MAINDATA + id, null,
				reponseListener, errorListener);
		mRequestQueue.add(mCustomRequest);
	}

	/**
	 * 获取最热新闻 数据
	 */
	public void getHotData(String id, Listener<JSONObject> reponseListener,
			ErrorListener errorListener) {
		mCustomRequest = new CustomRequest(Request.Method.POST,
				AndroidConfig.NETHOST + AndroidConfig.HOTDATA + id, null,
				reponseListener, errorListener);
		mRequestQueue.add(mCustomRequest);

	}

	/**
	 * 获取用户id
	 */
	public void getUserId(String appID, String message, String check,
			Listener<JSONObject> reponseListener, ErrorListener errorListener) {

		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("appID", appID);
		hashMap.put("message", message);
		hashMap.put("check", check);

		String url = AndroidConfig.GETUSERID + "?appID=" + appID + "&message="
				+ message + "&check=" + check;
		System.out.println(url);

		mCustomRequest = new CustomRequest(Request.Method.POST,
				AndroidConfig.GETUSERID, hashMap, reponseListener,
				errorListener);
		mRequestQueue.add(mCustomRequest);
	}

	/**
	 * 获取新闻详细 数据
	 */
	public void getNewsContentData(String id,
			Listener<JSONObject> reponseListener, ErrorListener errorListener) {
		mCustomRequest = new CustomRequest(Request.Method.POST,
				AndroidConfig.NETHOST + AndroidConfig.NewContent + id, null,
				reponseListener, errorListener);
		mRequestQueue.add(mCustomRequest);
	}

	/**
	 * 获取新闻详细 数据
	 */
	public void getNewsContentDataPush(String news_id,
			Listener<JSONObject> reponseListener, ErrorListener errorListener) {
		mCustomRequest = new CustomRequest(Request.Method.POST,
				AndroidConfig.KANKAN_HOST + AndroidConfig.NewContentPush
						+ news_id, null, reponseListener, errorListener);
		mRequestQueue.add(mCustomRequest);
	}

	/**
	 * 获取评论 新闻内容页
	 */
	public void getNewsContentCommentData(String id, String lastid,
			Listener<JSONObject> reponseListener, ErrorListener errorListener) {
		mCustomRequest = new CustomRequest(Request.Method.POST,
				AndroidConfig.NETHOST + AndroidConfig.NewContentComment + id
						+ "/max/" + lastid, null, reponseListener,
				errorListener);
		mRequestQueue.add(mCustomRequest);
	}

	/**
	 * 获取评论 我的评论
	 */
	public void getMyComment(String uid, String lastid,
			Listener<JSONObject> reponseListener, ErrorListener errorListener) {
		mCustomRequest = new CustomRequest(Request.Method.POST,
				AndroidConfig.NETHOST + AndroidConfig.GetMyComment + uid
						+ "/max/" + lastid, null, reponseListener,
				errorListener);
		mRequestQueue.add(mCustomRequest);
	}

	/**
	 * 获取分享数量等 新闻内容页
	 */
	public void getNewsContentCountsData(String id,
			Listener<JSONObject> reponseListener, ErrorListener errorListener) {
		mCustomRequest = new CustomRequest(Request.Method.POST,
				AndroidConfig.NETHOST + AndroidConfig.NewContentCounts + id,
				null, reponseListener, errorListener);
		mRequestQueue.add(mCustomRequest);
	}

	/**
	 * 获取新闻首页列表
	 */
	public void getNewsHomeList(Listener<JSONObject> reponseListener,
			ErrorListener errorListener) {
		mCustomRequest = new CustomRequest(Request.Method.GET,
				AndroidConfig.KANKAN_HOST + AndroidConfig.NEWS_HOME_DATA, null,
				reponseListener, errorListener);
		mRequestQueue.add(mCustomRequest);
	}

	/**
	 * 提交分享数据
	 */
	public void CommitShare(String mid, String type,
			Listener<JSONObject> reponseListener, ErrorListener errorListener) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("id", mid);
		hashMap.put("type", type);
		mCustomRequest = new CustomRequest(Request.Method.POST,
				AndroidConfig.NETHOST + AndroidConfig.CommitShare, hashMap,
				reponseListener, errorListener);
		mRequestQueue.add(mCustomRequest);
	}

	/**
	 * 发表评论
	 */
	public void CommitComment(String mid, String memo, String name, String uid,
			String userpic, Listener<JSONObject> reponseListener,
			ErrorListener errorListener) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("memo", memo);
		hashMap.put("name", name);
		hashMap.put("uid", uid);
		hashMap.put("userpic", userpic);
		mCustomRequest = new CustomRequest(Request.Method.POST,
				AndroidConfig.NETHOST + AndroidConfig.CommitComment + mid,
				hashMap, reponseListener, errorListener);
		mRequestQueue.add(mCustomRequest);
	}

	/**
	 * 点击收藏 mid为mid：time 多个用逗号隔开
	 */
	public void AddCollect(String uid, String name, String mid,
			Listener<JSONObject> reponseListener, ErrorListener errorListener) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("uid", uid);
		hashMap.put("name", name);
		mCustomRequest = new CustomRequest(Request.Method.POST,
				AndroidConfig.NETHOST + AndroidConfig.AddCollect + mid,
				hashMap, reponseListener, errorListener);
		mRequestQueue.add(mCustomRequest);
	}

	/**
	 * 取消收藏 ids 为新闻id的数组id,id,id
	 */
	public void CancelCollect(String uid, String ids,
			Listener<JSONObject> reponseListener, ErrorListener errorListener) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("uid", uid);
		hashMap.put("ids", ids);
		mCustomRequest = new CustomRequest(Request.Method.POST,
				AndroidConfig.NETHOST + AndroidConfig.CancelCollect, hashMap,
				reponseListener, errorListener);
		mRequestQueue.add(mCustomRequest);
	}

	/**
	 * 获取我的收藏数据
	 */
	public void getMyCollect(String uid, Listener<JSONObject> reponseListener,
			ErrorListener errorListener) {
		mCustomRequest = new CustomRequest(Request.Method.POST,
				AndroidConfig.NETHOST + AndroidConfig.GetMyCollect + uid, null,
				reponseListener, errorListener);
		mRequestQueue.add(mCustomRequest);
	}

	/**
	 * 记者主页
	 */
	public void getReporter(String reproterId, String lastId,
			Listener<JSONObject> reponseListener, ErrorListener errorListener) {
		mCustomRequest = new CustomRequest(Request.Method.POST,
				AndroidConfig.NETHOST + AndroidConfig.GetReporter + reproterId
						+ "/max/" + lastId, null, reponseListener,
				errorListener);
		mRequestQueue.add(mCustomRequest);
	}

	/**
	 * 下载视频
	 * 
	 * @param downUrl
	 *            下载地址
	 * @param fileName
	 *            文件名
	 * @param context
	 *            上下文
	 * @param callBack
	 *            回调函数
	 * @return
	 */
	public HttpHandler downloadVideo(String downUrl, String fileName,
			RequestCallBack<File> callBack) {

		HttpUtils httpUtils = new HttpUtils();
		File file = new File(CommonUtils.getVideoCachePath(mContext), fileName);
		HttpHandler handler = httpUtils.download(downUrl,
				file.getAbsolutePath(), true, false, callBack);

		return handler;
	}

	// ------------------------------------------------------------------看看新闻------------------------------------------------------------------------
	/**
	 * 获取主页条目
	 */
	public void getNewHomeCateData(Listener<JSONArray> reponseListener,
			ErrorListener errorListener) {
		mCustomRequestArray = new CustomRequestArray(Request.Method.POST,
				AndroidConfig.KANKAN_HOST + AndroidConfig.New_HomeCateData,
				null, reponseListener, errorListener);
		mRequestQueue.add(mCustomRequestArray);

	}

	/**
	 * 获取首页数据
	 */
	public void getNewHomeData(String lastnewstime, String chassid, String sp,
			Listener<JSONObject> reponseListener, ErrorListener errorListener) {
		mCustomRequest = new CustomRequest(Request.Method.POST,
				AndroidConfig.KANKAN_HOST + AndroidConfig.New_HomeData
						+ chassid + "/sp/" + sp + "/timestamp/" + lastnewstime,
				null, reponseListener, errorListener);

		mRequestQueue.add(mCustomRequest);
	}

	/**
	 * 获取新闻点击量
	 */
	public void getNewNewsClickData(String midtype,
			Listener<JSONArray> reponseListener, ErrorListener errorListener) {
		mCustomRequestArray = new CustomRequestArray(Request.Method.POST,
				AndroidConfig.New_NewsClick + midtype, null, reponseListener,
				errorListener);
		mRequestQueue.add(mCustomRequestArray);
	}

	/**
	 * 添加新闻点击量 mid id tjid
	 */
	public void addNewNewsClickData(String id) {
		mCustomRequestArray = new CustomRequestArray(Request.Method.POST,
				AndroidConfig.New_NewsAddClick + id, null, null, null);
		mRequestQueue.add(mCustomRequestArray);
	}

	/**
	 * 获取新闻详情
	 */
	public void getNewNewsContent(String mid, String type,
			Listener<JSONObject> reponseListener, ErrorListener errorListener) {
		mCustomRequest = new CustomRequest(Request.Method.POST,
				AndroidConfig.KANKAN_HOST + AndroidConfig.New_NewsContent + mid
						+ "/mtype/" + (Integer.valueOf(type) % 10), null,
				reponseListener, errorListener);
		mRequestQueue.add(mCustomRequest);
	}

	/**
	 * 获取新闻内容页数据
	 */
	public void getNewsContent(String id, String type,
			Listener<JSONObject> reponseListener, ErrorListener errorListener) {
		mCustomRequest = new CustomRequest(Request.Method.POST,
				AndroidConfig.KANKAN_HOST + AndroidConfig.NewsContent + type
						+ "_" + id, null, reponseListener, errorListener);
		mRequestQueue.add(mCustomRequest);
	}

	/**
	 * 获取直播数据
	 */
	public void getNewLivePlayData(Listener<JSONArray> reponseListener,
			ErrorListener errorListener) {
		mCustomRequestArray = new CustomRequestArray(Request.Method.POST,
				AndroidConfig.KANKAN_HOST + AndroidConfig.New_LivePlay, null,
				reponseListener, errorListener);
		mRequestQueue.add(mCustomRequestArray);
	}

	/**
	 * 获取直播数据
	 */
	public void getLiveList(Listener<JSONObject> reponseListener,
			ErrorListener errorListener) {
		mCustomRequest = new CustomRequest(Request.Method.POST,
				AndroidConfig.KANKAN_HOST + AndroidConfig.LIVE_LIST_URL + "?r="
						+ UUIDUtils.getUUID(8), null, reponseListener,
				errorListener);
		mRequestQueue.add(mCustomRequest);
	}

	/**
	 * 获取直播频道数据
	 */
	public void getChannelList(Listener<JSONObject> reponseListener,
			ErrorListener errorListener) {
		mCustomRequest = new CustomRequest(Request.Method.POST,
				AndroidConfig.KANKAN_HOST + AndroidConfig.LIVE_CHANNEL_URL
						+ "?r=" + UUIDUtils.getUUID(8), null, reponseListener,
				errorListener);
		mRequestQueue.add(mCustomRequest);
	}

	/**
	 * 获取栏目列表
	 */
	public void getNewColumsData(Listener<JSONArray> reponseListener,
			ErrorListener errorListener) {
		mCustomRequestArray = new CustomRequestArray(Request.Method.POST,
		// AndroidConfig.New_NETHOST +
				AndroidConfig.New_Colums, null, reponseListener, errorListener);
		mRequestQueue.add(mCustomRequestArray);
	}

	/**
	 * 获取栏目列表带二级菜单
	 */
	public void getNewColumsSecondData(Listener<JSONArray> reponseListener,
			ErrorListener errorListener) {
		mCustomRequestArray = new CustomRequestArray(Request.Method.POST,
				AndroidConfig.New_Colums_Second_Level, null, reponseListener,
				errorListener);
		mRequestQueue.add(mCustomRequestArray);
	}

	/**
	 * 获取栏目节目列表
	 * 
	 * @param programName
	 *            栏目名称
	 * @param datestamp
	 *            选择日期的时间戳,可以为空，默认为当天
	 * @param newstime
	 *            用于分页，可以为空，默认为第一页
	 */
	public void getNewColumsInfoData(String classid, String datestamp,
			String newstime, Listener<JSONArray> reponseListener,
			ErrorListener errorListener) {

		// try {
		// programName = URLEncoder.encode(programName, "utf-8");
		// } catch (UnsupportedEncodingException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

		datestamp = TextUtils.isEmpty(datestamp) ? "" : "/day/" + datestamp;
		newstime = TextUtils.isEmpty(newstime) ? "" : "/timestamp/" + newstime;
		mCustomRequestArray = new CustomRequestArray(Request.Method.GET,
				AndroidConfig.KANKAN_HOST + AndroidConfig.New_Colums_Info
						+ classid + datestamp + newstime, null,
				reponseListener, errorListener);
		mRequestQueue.add(mCustomRequestArray);
	}

	/**
	 * 获取专题详情
	 */
	public void getSubjectData(String ztid,
			Listener<JSONObject> reponseListener, ErrorListener errorListener) {
		mCustomRequest = new CustomRequest(Request.Method.POST,
				AndroidConfig.KANKAN_HOST + AndroidConfig.New_Subject + ztid,
				null, reponseListener, errorListener);
		mRequestQueue.add(mCustomRequest);
	}

	/**
	 * 获得热门推荐接口
	 */
	public void getRecommendData(Listener<JSONArray> reponseListener,
			ErrorListener errorListener) {
		mCustomRequestArray = new CustomRequestArray(Request.Method.POST,
				AndroidConfig.New_Recommend, null, reponseListener,
				errorListener);
		mRequestQueue.add(mCustomRequestArray);
	}

	/**
	 * 获得热门推荐接口
	 */
	public void getAdert(Map<String, String> params,
			Listener<JSONObject> reponseListener, ErrorListener errorListener) {
		mCustomRequest = new CustomRequest(Request.Method.POST,
				AndroidConfig.ADVERT_GET, params, reponseListener,
				errorListener);

		mRequestQueue.add(mCustomRequest);
	}

	/**
	 * 获取搜索条目
	 */
	public void getSearchData(String searchContent, int pageNum,
			Listener<JSONArray> reponseListener, ErrorListener errorListener) {
		try {
			searchContent = URLEncoder.encode(searchContent, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mCustomRequestArray = new CustomRequestArray(Request.Method.GET,
				AndroidConfig.KANKAN_HOST + AndroidConfig.SEARCH_GET + "?w="
						+ searchContent + "&p=" + pageNum, null,
				reponseListener, errorListener);
		mRequestQueue.add(mCustomRequestArray);

	}

	/**
	 * 获取搜索条目
	 */
	public void getSearchHotWord(Listener<JSONArray> reponseListener,
			ErrorListener errorListener) {
		mCustomRequestArray = new CustomRequestArray(Request.Method.GET,
				AndroidConfig.KANKAN_HOST + AndroidConfig.SEARCH_HOT_WORD,
				null, reponseListener, errorListener);
		mRequestQueue.add(mCustomRequestArray);

	}

	/**
	 * 获取报料首页条目
	 */
	public void getRevelationsHomeList(Listener<JSONObject> reponseListener,
			ErrorListener errorListener) {
		mCustomRequest = new CustomRequest(
				Request.Method.GET,
				AndroidConfig.KANKAN_HOST + AndroidConfig.REVELATIONS_HOME_DATA,
				null, reponseListener, errorListener);
		mRequestQueue.add(mCustomRequest);
	}

	/**
	 * 获取报料活动详情
	 */
	public void getRevelationsActivityList(String aid, String timestamp,
			Listener<JSONObject> reponseListener, ErrorListener errorListener) {
		mCustomRequest = new CustomRequest(Request.Method.GET,
				AndroidConfig.KANKAN_HOST
						+ AndroidConfig.REVELATIONS_ACTIVITY_DATA + "/aid/"
						+ aid + "/timestamp/" + timestamp, null,
				reponseListener, errorListener);
		mRequestQueue.add(mCustomRequest);
	}

	/**
	 * 获取报料更多
	 */
	public void getRevelationsBreaknewsMore(String timestamp,
			Listener<JSONObject> reponseListener, ErrorListener errorListener) {
		mCustomRequest = new CustomRequest(Request.Method.GET,
				AndroidConfig.KANKAN_HOST
						+ AndroidConfig.REVELATIONS_BREAKNEWS_MORE_DATA
						+ timestamp, null, reponseListener, errorListener);
		mRequestQueue.add(mCustomRequest);
	}

	/**
	 * 提交报料内容
	 */
	public void postRevelationContent(String tel, String content,
			String imageUrls, Listener<JSONObject> reponseListener,
			ErrorListener errorListener) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("phonenum", tel);
		params.put("newstext", content);
		params.put("imagegroup", imageUrls);
		mCustomRequest = new CustomRequest(Request.Method.POST,
				AndroidConfig.REVELATIONS_CONTENT_POST, params,
				reponseListener, errorListener);
		mRequestQueue.add(mCustomRequest);
	}

	public void getAnalyse(Context context, String type, String title,
			String titleUrl) {
		String url;
		try {
			url = AndroidConfig.New_NewsAnalyse + "?itemType=" + type
					+ "&pageTitle=" + URLEncoder.encode(title, "utf-8")
					+ "&pageURL=" + titleUrl;
			new AnalyseGetThread(url, context).start();
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			Log.e("New_Activity_Content_Web.initData", e1.getLocalizedMessage());
		}
	}

	private class AnalyseGetThread extends Thread {
		private String url;
		private Context context;

		AnalyseGetThread(String url, Context context) {
			this.url = url;
			this.context = context;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub

			try {
				TelephonyManager telephonyManager = (TelephonyManager) context
						.getSystemService(Context.TELEPHONY_SERVICE);
				ConnectivityManager connectivityManager = (ConnectivityManager) context
						.getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo activeNetInfo = connectivityManager
						.getActiveNetworkInfo();
				HttpGet httpRequest = new HttpGet(url);
				// httpRequest.setHeader("MOBILE_DEVICE_INFO",
				// android.os.Build.MODEL);
				String operatorName = telephonyManager.getNetworkOperatorName()
						.trim().equals("") ? "null" : telephonyManager
						.getNetworkOperatorName();
				httpRequest.setHeader(
						"User-Agent",
						"kankanapp(" + android.os.Build.MODEL + separator
								+ "kankanapp" + separator
								+ CommonUtils.getVersion(context) + separator
								+ "Android" + separator + "Android"
								+ android.os.Build.VERSION.RELEASE + separator
								+ operatorName + separator
								+ activeNetInfo.getTypeName() + ")");
				HttpResponse httpResponse;
				httpResponse = new DefaultHttpClient().execute(httpRequest);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				Log.e("ItnetUtils.addNewNewsAnalyse", e.getLocalizedMessage()
						+ "");
			}
		}
	}

	public static VideoUploadResult getTokenUploadVideo(File video,
			String deviceId) {
		// BufferedReader responseReader = null;
		HttpURLConnection conn = null;
		StringBuffer responseContent = new StringBuffer();
		HttpGet httpGet = null;
		HttpResponse httpResponse = null;
		try {
			String path = AndroidConfig.REVELATIONS_GET_VIDEO_UPLOAD_TOKEN
					+ "?name=" + video.getName() + "_" + deviceId + "&size="
					+ video.length();
			httpGet = new HttpGet(path);
			httpResponse = new DefaultHttpClient().execute(httpGet);
			// DebugLog.e(path);
			// URL url = new URL(path);
			// conn = (HttpURLConnection) url.openConnection();
			// conn.setConnectTimeout(5 * 1000);
			// conn.setRequestMethod("GET");
			// responseReader = new BufferedReader(new InputStreamReader(
			// conn.getInputStream()));
			// while (responseReader.ready()) {
			// responseContent.append(responseReader.readLine());
			// }
			responseContent.append(EntityUtils.toString(httpResponse
					.getEntity()));
		} catch (Exception e) {
			DebugLog.e(e.getLocalizedMessage());
			VideoUploadResult result = new VideoUploadResult();
			result.setSuccess(false);
			return result;
		} finally {
		}
		return JsonUtils.toObject(responseContent.toString(),
				VideoUploadResult.class);
	}

	public static VideoUploadResult valiedateUploadVideo(String token,
			File video, String deviceId) {
		HttpGet httpGet = null;
		HttpResponse httpResponse = null;
		BufferedReader responseReader = null;
		// HttpURLConnection conn = null;
		StringBuffer responseContent = new StringBuffer();
		try {
			String path = AndroidConfig.REVELATIONS_VIDEO_UPLOAD + "?name="
					+ video.getName() + "_" + deviceId + "&size="
					+ video.length() + "&token=" + token;
			// URL url = new URL(path);
			httpGet = new HttpGet(path);
			httpResponse = new DefaultHttpClient().execute(httpGet);
			// conn = (HttpURLConnection) url.openConnection();
			// conn.setConnectTimeout(5 * 1000);
			// conn.setRequestMethod("GET");
			// InputStream inStream = conn.getInputStream();
			// responseReader = new BufferedReader(new InputStreamReader(
			// conn.getInputStream()));
			// while (responseReader.ready()) {
			// responseContent.append(responseReader.readLine());
			// }
			responseContent.append(EntityUtils.toString(httpResponse
					.getEntity()));
		} catch (Exception e) {
			DebugLog.e(e.getLocalizedMessage());
			VideoUploadResult result = new VideoUploadResult();
			result.setSuccess(false);
			return result;
		} finally {
		}
		return JsonUtils.toObject(responseContent.toString(),
				VideoUploadResult.class);
	}

	public static VideoUploadResult postVideo(File video, String token,
			long from, long to) {
		HttpURLConnection conn = null;
		DataOutputStream outStream = null;
		StringBuffer responseContent = null;
		BufferedReader responseReader = null;
		FileChannel channel = null;
		try {
			String uriAPI = "http://i.kankanews.com:8080/upload.do?token="
					+ token + "&name=" + video.getName();
			String BOUNDARY = java.util.UUID.randomUUID().toString();
			String PREFIX = "--", LINEND = "\r\n";
			String MULTIPART_FROM_DATA = "multipart/form-data";
			String CHARSET = "UTF-8";

			URL uri = new URL(uriAPI);

			conn = (HttpURLConnection) uri.openConnection();
			conn.setReadTimeout(5000 * 1000);
			conn.setDoInput(true);// 允许输入
			conn.setDoOutput(true);// 允许输出
			conn.setUseCaches(false);
			conn.setRequestMethod("POST"); // Post方式
			conn.setRequestProperty("connection", "keep-alive");

			channel = new FileInputStream(video).getChannel();
			long contentLength = to - from;

			conn.setRequestProperty("Content-Length", contentLength + "");
			conn.setRequestProperty("Charsert", "UTF-8");
			conn.setRequestProperty("Content-Type", MULTIPART_FROM_DATA
					+ ";boundary=" + BOUNDARY);
			conn.setRequestProperty("content-range", "bytes " + from + "-" + to
					+ "/" + video.length());

			outStream = new DataOutputStream(conn.getOutputStream());
			channel.transferTo(from, to - from, Channels.newChannel(outStream));

			responseReader = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));
			responseContent = new StringBuffer();
			// while (responseReader.ready()) {
			// responseContent.append(responseReader.readLine());
			// }
			String str;
			while ((str = responseReader.readLine()) != null) {
				responseContent.append(str);
			}
		} catch (Exception e) {
			DebugLog.e(e.getLocalizedMessage());
			VideoUploadResult result = new VideoUploadResult();
			result.setSuccess(false);
			return result;
		} finally {
			try {
				outStream.close();
				channel.close();
				conn.getInputStream().close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return JsonUtils.toObject(responseContent.toString(),
				VideoUploadResult.class);
	}
}
