package com.kankan.kankanews.ui.item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.kankan.kankanews.base.BaseActivity;
import com.kankan.kankanews.bean.Content_New_List;
import com.kankan.kankanews.bean.Content_News;
import com.kankan.kankanews.bean.SuccessMsg;
import com.kankan.kankanews.bean.User;
import com.kankan.kankanews.bean.User_Collect_Offline;
import com.kankan.kankanews.config.AndroidConfig;
import com.kankan.kankanews.exception.NetRequestException;
import com.kankan.kankanews.net.ItnetUtils;
import com.kankan.kankanews.sina.AccessTokenKeeper;
import com.kankan.kankanews.sina.Constants;
import com.kankan.kankanews.utils.CommonUtils;
import com.kankan.kankanews.utils.ToastUtils;
import com.kankanews.kankanxinwen.R;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.AsyncWeiboRunner;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.net.WeiboParameters;
import com.sina.weibo.sdk.openapi.AbsOpenAPI;
import com.sina.weibo.sdk.openapi.models.ErrorInfo;
import com.sina.weibo.sdk.utils.LogUtil;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.UMAuthListener;
import com.umeng.socialize.controller.listener.SocializeListeners.UMDataListener;
import com.umeng.socialize.exception.SocializeException;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.UMSsoHandler;

public class Activity_Login extends BaseActivity implements OnClickListener {

	private AuthInfo mAuthInfo;
	/** 注意：SsoHandler 仅当 SDK 支持 SSO 时有效 */
	private SsoHandler mSsoHandler;
	/** 封装了 "access_token"，"expires_in"，"refresh_token"，并提供了他们的管理功能 */
	private Oauth2AccessToken mAccessToken;

	/** 访问微博服务接口的地址 */
	protected static final String API_SERVER = "https://api.weibo.com/2";
	private static final int READ_USER = 0;
	private static final String API_BASE_URL = API_SERVER + "/users";
	private static final SparseArray<String> sAPIList = new SparseArray<String>();
	static {
		sAPIList.put(READ_USER, API_BASE_URL + "/show.json");
	}
	/** GET 请求方式 */
	protected static final String HTTPMETHOD_GET = "GET";
	private static final String TAG = AbsOpenAPI.class.getName();
	/** HTTP 参数 */
	protected static final String KEY_ACCESS_TOKEN = "access_token";

	private String type;

	private ImageView login_sina;
	private ImageView login_qq;

	private ItnetUtils instance;

	private User user;

	private boolean canquit = true;

	// 本地数据的数组 用hashmap比较方便
	private HashMap<String, User_Collect_Offline> uco_hashmap = new HashMap<String, User_Collect_Offline>();

	// 获取我的收藏的数组
	private ArrayList<Content_News> mContent_News;

	// 整个平台的Controller, 负责管理整个SDK的配置、操作等处理
	private UMSocialService mController = UMServiceFactory
			.getUMSocialService("com.umeng.login");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_login);

		// tongji
		initAnalytics(AndroidConfig.login_page);

		initTitle_Left_bar("登录", "", "#000000", R.drawable.icon_black_big);

		setOnLeftClickLinester(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (canquit) {
					AnimFinsh();
				}
			}
		});

		QZoneSsoHandler qqSsoHandler = new QZoneSsoHandler(mContext,
				"1103461267", "3FVq3JjOmzUFb1jE");
		qqSsoHandler.addToSocialSDK();

		// sina sso
		// 快速授权时，请不要传入 SCOPE，否则可能会授权不成功
		mAuthInfo = new AuthInfo(mContext, Constants.APP_KEY,
				Constants.REDIRECT_URL, Constants.SCOPE);
		mSsoHandler = new SsoHandler(mContext, mAuthInfo);

	}

	@Override
	protected void initView() {
		login_sina = (ImageView) findViewById(R.id.login_sina);
		login_qq = (ImageView) findViewById(R.id.login_qq);

	}

	@Override
	protected void initData() {
		instance = ItnetUtils.getInstance(this);

		// mController.getConfig().setSsoHandler(new SinaSsoHandler());
		// 初始化本地的离线收藏数据
		initLoaclData();
	}

	private void initLoaclData() {
		try {
			ArrayList<User_Collect_Offline> uco = (ArrayList<User_Collect_Offline>) dbUtils
					.findAll(User_Collect_Offline.class);
			if (uco != null && uco.size() > 0) {
				for (User_Collect_Offline user_collect_offline : uco) {
					String id = user_collect_offline.getId();
					uco_hashmap.put(id, user_collect_offline);
				}
			}
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected void setListener() {
		login_sina.setOnClickListener(this);
		login_qq.setOnClickListener(this);

	}

	@Override
	protected void onSuccess(JSONObject jsonObject) {
		// TODO Auto-generated method stub
		try {
			user = new User();
			user = user.parseJSON(jsonObject);
			if (user != null) {
				mApplication.setUser(user);
				Intent data = new Intent();
				data.putExtra("User", true);
				setResult(AndroidConfig.Drawer_login_resultCode, data);

				spUtil.setUserId(user.getUser_id());
				spUtil.setUserName(user.getUser_name());
				spUtil.setUserPost(user.getUser_poster());

				if (type.equals("QQ")) {
					CommonUtils.clickevent(mContext, "type", "腾讯qq",
							AndroidConfig.login_event);
				} else if (type.equals("SINA")) {
					CommonUtils.clickevent(mContext, "type", "新浪微博",
							AndroidConfig.login_event);
				}

				commitLocatData();

			}
		} catch (NetRequestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected void onFailure(VolleyError error) {
		ToastUtils.Errortoast(mContext, "出错啦！请重试~");
		System.out.println(error);
		loading_dialog.dismiss();
		canquit = true;
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if (canquit) {
			AnimFinsh();
		}
	}

	@Override
	public void onClick(View v) {

		loading_dialog.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					loading_dialog.dismiss();
					canquit = true;
					ToastUtils.Infotoast(mContext, "取消登录");
				}
				return false;
			}
		});

		switch (v.getId()) {
		case R.id.login_sina:
			type = "SINA";
			// login(SHARE_MEDIA.SINA);
			loading_dialog.setCanceledOnTouchOutside(false);
			canquit = false;
			loading_dialog.show();
			mSsoHandler.authorize(new AuthListener());
			break;
		case R.id.login_qq:
			type = "QQ";
			login(SHARE_MEDIA.QZONE);
			break;

		default:
			break;
		}

	}

	/**
	 * 授权。如果授权成功，则获取用户信息</br>
	 */
	private void login(final SHARE_MEDIA platform) {
		mController.doOauthVerify(mContext, platform, new UMAuthListener() {

			@Override
			public void onStart(SHARE_MEDIA platform) {
				loading_dialog.setCanceledOnTouchOutside(false);
				canquit = false;
				loading_dialog.show();
			}

			@Override
			public void onError(SocializeException e, SHARE_MEDIA platform) {
				loading_dialog.dismiss();
				canquit = true;
			}

			@Override
			public void onComplete(Bundle value, SHARE_MEDIA platform) {
				String uid = value.getString("uid");
				if (!TextUtils.isEmpty(uid)) {
					getUserInfo(platform);
				} else {
					canquit = true;
					loading_dialog.dismiss();
					ToastUtils.Errortoast(mContext, "登录出现异常，请重试");
				}
			}

			@Override
			public void onCancel(SHARE_MEDIA platform) {
				loading_dialog.dismiss();
				canquit = true;
			}
		});

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (loading_dialog != null && loading_dialog.isShowing()) {
			loading_dialog.dismiss();
		}

	}

	/**
	 * 获取授权平台的用户信息</br>
	 */
	private void getUserInfo(SHARE_MEDIA platform) {
		mController.getPlatformInfo(mContext, platform, new UMDataListener() {

			@Override
			public void onStart() {
				// ToastUtil.showToast(mContext, "please waiting ...");

			}

			@Override
			public void onComplete(int status, Map<String, Object> info) {

				if (info != null && info.size() > 0 && info.get("uid") != null
						&& info.get("screen_name") != null
						&& info.get("profile_image_url") != null) {

					String uid = info.get("uid").toString();
					String screen_name = info.get("screen_name").toString();
					String profile_image_url = info.get("profile_image_url")
							.toString();

					getKKUserInfo(uid, screen_name, profile_image_url);

				} else {
					ToastUtils.Errortoast(mContext, "登录出现异常，请重试");
					loading_dialog.dismiss();
					canquit = true;
				}

			}
		});
	}

	/**
	 * 提交看看服务器数据
	 * 
	 * @param uid
	 * @param screen_name
	 * @param profile_image_url
	 */
	protected void getKKUserInfo(String uid, String screen_name,
			String profile_image_url) {
		JSONObject jsonObject = new JSONObject();
		try {
			JSONObject userJson = new JSONObject();
			userJson.put("avatar", profile_image_url);
			userJson.put("username", screen_name);
			userJson.put("openId", uid);
			jsonObject.put("openID", uid);
			jsonObject.put("userJson", userJson.toString());
			jsonObject.put("type", type);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String check = CommonUtils.md5(CommonUtils.md5(jsonObject.toString())
				+ "a4323##@0D#@");
		instance.getUserId("1001", jsonObject.toString(), check, mListener,
				mErrorListener);
	}

	/**
	 * sso授权
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		/** 使用SSO授权必须添加如下代码 */
		UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(
				requestCode);
		if (ssoHandler != null) {
			ssoHandler.authorizeCallBack(requestCode, resultCode, data);
		}
		// SSO 授权回调 sina
		// 重要：发起 SSO 登陆的 Activity 必须重写 onActivityResult
		if (mSsoHandler != null) {
			mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
		}
	}

	/**
	 * 提交本地数据 并获取服务器数据（收藏）
	 */
	private void commitLocatData() {
		new Thread() {
			@Override
			public void run() {
				try {
					// 提交本地数据
					List<User_Collect_Offline> user_Collect_Offlines = dbUtils
							.findAll(Selector.from(User_Collect_Offline.class)
									.where("isCollect", "==", true));
					if (user_Collect_Offlines != null
							&& user_Collect_Offlines.size() > 0) {
						String newid_time = "";
						// String nowtime = Long.toString(TimeUtil.now());
						for (User_Collect_Offline user_Collect_Offline : user_Collect_Offlines) {
							newid_time = newid_time
									+ user_Collect_Offline.getId() + ":"
									+ user_Collect_Offline.getCollectTime()
									+ ",";
						}
						newid_time = newid_time.substring(0,
								newid_time.length() - 1);
						ItnetUtils.getInstance(mContext).AddCollect(
								mApplication.getUser().getUser_id(),
								mApplication.getUser().getUser_name(),
								newid_time, AddCollectListener,
								AddCollectErrorListener);
					} else {

						// 从服务器拉取数据
						ItnetUtils.getInstance(mContext)
								.getMyCollect(
										mApplication.getUser().getUser_id(),
										getMyCollectListener,
										getMyCollectErrorListener);
					}

				} catch (DbException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	/*
	 * 提交收藏
	 */
	// 处理网络出错
	protected ErrorListener AddCollectErrorListener = new ErrorListener() {
		@Override
		public void onErrorResponse(VolleyError error) {
			error.printStackTrace();
			// ToastUtils.Infotoast(mContext, "收藏失败");
		}
	};
	// 处理网络成功
	protected Listener<JSONObject> AddCollectListener = new Listener<JSONObject>() {
		@Override
		public void onResponse(JSONObject jsonObject) {
			try {
				SuccessMsg successMsg = new SuccessMsg();
				successMsg.parseJSON(jsonObject);

				// 提交成功后 把本地所有的收藏false
				try {
					ArrayList<User_Collect_Offline> user_Collect_Offliness = (ArrayList<User_Collect_Offline>) dbUtils
							.findAll(User_Collect_Offline.class);
					for (int i = 0; i < user_Collect_Offliness.size(); i++) {
						user_Collect_Offliness.get(i).setCollect(false);
					}
					dbUtils.updateAll(user_Collect_Offliness);
				} catch (DbException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				// 从服务器拉取数据
				ItnetUtils.getInstance(mContext).getMyCollect(
						mApplication.getUser().getUser_id(),
						getMyCollectListener, getMyCollectErrorListener);

			} catch (NetRequestException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};

	/*
	 * 获取我的收藏
	 */
	// 处理网络出错
	protected ErrorListener getMyCollectErrorListener = new ErrorListener() {
		@Override
		public void onErrorResponse(VolleyError error) {
			error.printStackTrace();
			// ToastUtils.Infotoast(mContext, "收藏失败");
		}
	};
	// 处理网络成功
	protected Listener<JSONObject> getMyCollectListener = new Listener<JSONObject>() {
		@Override
		public void onResponse(final JSONObject jsonObject) {

			Thread thread = new Thread() {
				public void run() {
					try {
						if (mContent_News == null) {
							mContent_News = new ArrayList<Content_News>();
						}
						Content_New_List myCollect_List = new Content_New_List();
						myCollect_List = myCollect_List.parseJSON(jsonObject);
						if (myCollect_List != null) {
							mContent_News = myCollect_List.getmContent_Newss();
							ArrayList<User_Collect_Offline> ddd = new ArrayList<User_Collect_Offline>();
							User_Collect_Offline uco;
							for (Content_News mc : mContent_News) {
								if (uco_hashmap != null
										&& uco_hashmap.size() > 0
										&& uco_hashmap.get(mc.getMid()) != null) {
									uco = uco_hashmap.get(mc.getMid());
									uco.setCollectTime(Long.parseLong(mc
											.getDateline()));
									uco.setCollect(true);
								} else {
									uco = new User_Collect_Offline();
									uco.setCollect(true);
									uco.setCollectTime(Long.parseLong(mc
											.getDateline()));
									uco.setId(mc.getMid());
								}
								ddd.add(uco);
							}

							dbUtils.saveOrUpdateAll(ddd);
							dbUtils.saveOrUpdateAll(mContent_News);
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					AnimFinsh();
				};
			};
			thread.start();
		}
	};

	/**
	 * 微博认证授权回调类。 1. SSO 授权时，需要在 {@link #onActivityResult} 中调用
	 * {@link SsoHandler#authorizeCallBack} 后， 该回调才会被执行。 2. 非 SSO
	 * 授权时，当授权结束后，该回调就会被执行。 当授权成功后，请保存该 access_token、expires_in、uid 等信息到
	 * SharedPreferences 中。
	 */
	class AuthListener implements WeiboAuthListener {

		@Override
		public void onComplete(Bundle values) {
			// 从 Bundle 中解析 Token
			mAccessToken = Oauth2AccessToken.parseAccessToken(values);
			if (mAccessToken.isSessionValid()) {
				// 保存 Token 到 SharedPreferences
				AccessTokenKeeper.writeAccessToken(mContext, mAccessToken);
				// Toast.makeText(mContext, "授权成功", Toast.LENGTH_SHORT).show();
				// 获取用户信息
				long uid = Long.parseLong(mAccessToken.getUid());
				show(uid, sinalistener);
			} else {
				// 以下几种情况，您会收到 Code：
				// 1. 当您未在平台上注册的应用程序的包名与签名时；
				// 2. 当您注册的应用程序包名与签名不正确时；
				// 3. 当您在平台上注册的包名和签名与您当前测试的应用的包名和签名不匹配时。
				// String code = values.getString("code");
				// String message = "授权失败";
				// if (!TextUtils.isEmpty(code)) {
				// message = message + "\nObtained the code: " + code;
				canquit = true;
				loading_dialog.dismiss();
				ToastUtils.Errortoast(mContext, "登录出现异常，请重试");
				// }
				// Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
			}
		}

		@Override
		public void onCancel() {
			canquit = true;
			loading_dialog.dismiss();
			ToastUtils.Errortoast(mContext, "取消登录");
		}

		@Override
		public void onWeiboException(WeiboException e) {
			Toast.makeText(mContext, "Auth exception : " + e.getMessage(),
					Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * 微博 OpenAPI 回调接口。
	 */
	private RequestListener sinalistener = new RequestListener() {
		@Override
		public void onComplete(String response) {
			if (!TextUtils.isEmpty(response)) {
				LogUtil.i(TAG, response);
				// 调用 User#parse 将JSON串解析成User对象
				if (response != null) {
					try {
						JSONObject jsonObject = new JSONObject(response);
						String uid = jsonObject.optString("id", "");
						String screen_name = jsonObject.optString(
								"screen_name", "");
						String profile_image_url = jsonObject.optString(
								"avatar_hd", "");
						getKKUserInfo(uid, screen_name, profile_image_url);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else {
					ToastUtils.Errortoast(mContext, "登录出现异常，请重试");
					loading_dialog.dismiss();
					canquit = true;
				}
			}
		}

		@Override
		public void onWeiboException(WeiboException e) {
			LogUtil.e(TAG, e.getMessage());
			ErrorInfo info = ErrorInfo.parse(e.getMessage());
			Toast.makeText(mContext, info.toString(), Toast.LENGTH_LONG).show();
		}
	};

	/**
	 * 根据用户ID获取用户信息。
	 * 
	 * @param uid
	 *            需要查询的用户ID
	 * @param listener
	 *            异步请求回调接口
	 */
	public void show(long uid, RequestListener listener) {
		WeiboParameters params = new WeiboParameters(Constants.APP_KEY);
		params.put("uid", uid);
		requestAsync(sAPIList.get(READ_USER), params, HTTPMETHOD_GET, listener);
	}

	/**
	 * HTTP 异步请求。
	 * 
	 * @param url
	 *            请求的地址
	 * @param params
	 *            请求的参数
	 * @param httpMethod
	 *            请求方法
	 * @param listener
	 *            请求后的回调接口
	 */
	protected void requestAsync(String url, WeiboParameters params,
			String httpMethod, RequestListener listener) {
		if (null == mAccessToken || TextUtils.isEmpty(url) || null == params
				|| TextUtils.isEmpty(httpMethod) || null == listener) {
			LogUtil.e(TAG, "Argument error!");
			return;
		}
		params.put(KEY_ACCESS_TOKEN, mAccessToken.getToken());
		new AsyncWeiboRunner(mContext).requestAsync(url, params, httpMethod,
				listener);
	}

}
