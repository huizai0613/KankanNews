package com.kankan.kankanews.ui;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.volley.VolleyError;
import com.kankan.kankanews.base.BaseActivity;
import com.kankan.kankanews.bean.Advert;
import com.kankan.kankanews.bean.New_News_Click;
import com.kankan.kankanews.bean.New_News_Home;
import com.kankan.kankanews.bean.New_News_Top;
import com.kankan.kankanews.config.AndroidConfig;
import com.kankan.kankanews.net.ItnetUtils;
import com.kankan.kankanews.utils.CommonUtils;
import com.kankan.kankanews.utils.ImgUtils;
import com.kankan.kankanews.utils.PixelUtil;
import com.kankanews.kankanxinwen.R;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.networkbench.agent.impl.NBSAppAgent;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;

/**
 * class desc: 启动画面 (1)判断是否是首次加载应用--采取读取SharedPreferences的方法
 * (2)是，则进入GuideActivity；否，则进入MainActivity (3)3s后执行(2)操作
 */
public class SplashActivity extends BaseActivity {
	private ItnetUtils instance;
	boolean isFirstIn = false;
	// 应用版本号
	private String version;

	private static final int GO_HOME = 1000;
	private static final int GO_GUIDE = 1001;
	// 延迟3秒
	private static final long SPLASH_DELAY_MILLIS = 3000;

	private ImageView welcome_img;
	private ImageView welcome_text_img;
	private LinearLayout rootView;
	private ImageView adPic;
	private Advert advert;
	// private Animation animation;

	/**
	 * Handler:跳转到不同界面
	 */
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case GO_HOME:
				goHome();
				break;
			case GO_GUIDE:
				goGuide();
				break;
			}
			super.handleMessage(msg);
		}
	};
	private PushAgent mPushAgent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mPushAgent = PushAgent.getInstance(this);
		mPushAgent.onAppStart();
		mPushAgent.enable();
		instance = ItnetUtils.getInstance(this);
		// Log.e("UmengRegistrar", UmengRegistrar.getRegistrationId(this));

//		NBSAppAgent.setLicenseKey("90d48bf7c56d4d5d9071ce32a39644d3")
//				.withLocationServiceEnabled(true).start(this);
		mApplication.setStart(true);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_splash);
		Log.e("mScreenWidth", mScreenWidth + "");
		// 关闭默认的统计方式
		MobclickAgent.openActivityDurationTrack(false);
		// 发送策略
		MobclickAgent.updateOnlineConfig(mContext);
		Log.e("PixelUtil.dp2px(18)", PixelUtil.px2dp(96) + "");
		initAnalytics("");
		setRightFinsh(false);

		init();
		// createShortcut();
		// String device_token = UmengRegistrar.getRegistrationId(this);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

	}

	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		int adPicWidth = adPic.getWidth();
		int adPicHeight = adPic.getHeight();

		if (CommonUtils.isNetworkAvailableNoToast(this)) {
			Map<String, String> params = new HashMap<String, String>();
			params.put("aid", "107167");
			params.put("ts", new Date().getTime() + "");
			params.put("fmt", "json");
			params.put("ver", "1");
			params.put("aw", adPicWidth + "");
			params.put("ah", adPicHeight + "");
			params.put("m", "1");
			// Log.e("Date", params.get("ts"));AndroidConfig.ADVERT_GET
			// String params = "?aid=107167&ts=" + new Date().getTime() +
			// "&fmt=json&ver=1";
			Log.e("ADVERT_GET", AndroidConfig.ADVERT_GET + params);
			instance.getAdert(params, this.mListener, this.mErrorListener);
		} else {
			getLocalAdvert();
			showAdvert();
		}
	}

	private void init() {

		version = CommonUtils.getVersionName(this);
		isFirstIn = spUtil.isFristComing();

		if (isFirstIn) {
			CommonUtils.deleteDir(CommonUtils.getVideoCachePath(mContext));
			CommonUtils.deleteDir(CommonUtils.getImageCachePath(mContext));
		}
		// 判断程序与第几次运行，如果是第一次运行则跳转到引导界面，否则跳转到主界面
		if (isFirstIn || !version.equals(spUtil.getVersion())) {
			// 使用Handler的postDelayed方法，2秒后执行跳转到MainActivity
			mHandler.sendEmptyMessageDelayed(GO_GUIDE, SPLASH_DELAY_MILLIS);
		} else {
			mHandler.sendEmptyMessageDelayed(GO_HOME, SPLASH_DELAY_MILLIS);
		}
	}

	private void goHome() {
		Intent intent = getIntent();
		String scheme = intent.getScheme();
		if ("kkl".equals(scheme)) {
			Uri uridata = this.getIntent().getData();
			String liveId = uridata.getQueryParameter("LIVE_ID");
			intent.putExtra("LIVE_ID", liveId);
		}
		intent.setClass(SplashActivity.this, MainActivity.class);
		SplashActivity.this.startActivity(intent);
		// overridePendingTransition(R.anim.alpha, R.anim.alpha_op);
		SplashActivity.this.finish();
	}

	private void goGuide() {
		Intent intent = new Intent(SplashActivity.this, GuideActivity.class);
		SplashActivity.this.startActivity(intent);
		SplashActivity.this.finish();
	}

	/**
	 * 创建桌面快捷方式
	 */
	private void createShortcut() {
		SharedPreferences setting = getSharedPreferences("silent.preferences",
				0);
		// 判断是否第一次启动应用程序（默认为true）
		boolean firstStart = setting.getBoolean("FIRST_START", true);
		// 第一次启动时创建桌面快捷方式
		if (firstStart) {
			Intent shortcut = new Intent(
					"com.android.launcher.action.INSTALL_SHORTCUT");
			// 快捷方式的名称
			shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME,
					getString(R.string.app_name));
			// 不允许重复创建
			shortcut.putExtra("duplicate", false);
			// 指定快捷方式的启动对象
			// ComponentName comp = new ComponentName(this.getPackageName(), "."
			// + this.getLocalClassName());
			// shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, new Intent(
			// Intent.ACTION_MAIN).setComponent(comp));

			Intent shortcutIntent = new Intent(Intent.ACTION_MAIN);
			shortcutIntent.setClassName(this, this.getClass().getName());
			shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);

			// 快捷方式的图标
			ShortcutIconResource iconRes = Intent.ShortcutIconResource
					.fromContext(this, R.drawable.icon_launch);
			shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconRes);
			// 发出广播
			sendBroadcast(shortcut);
			// 将第一次启动的标识设置为false
			Editor editor = setting.edit();
			editor.putBoolean("FIRST_START", false);
			// 提交设置
			editor.commit();
		}
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		adPic = (ImageView) this.findViewById(R.id.ad_pic);

		rootView = (LinearLayout) this.findViewById(R.id.welcome_root_view);
		welcome_img = (ImageView) this.findViewById(R.id.welcome_logo_img);
		welcome_text_img = (ImageView) this
				.findViewById(R.id.welcome_logo_text_img);

		int width = this.mScreenWidth;
		int height = this.mScreenHeight;
		Log.e("adPicWidth", adPic.getWidth() + "");
		Log.e("adPicHeight", adPic.getHeight() + "");
		// welcome_img.post(new Runnable() {
		// @Override
		// public void run() {
		// AnimationSet animationSet = new AnimationSet(true);
		// final int top = welcome_img.getTop();
		// final int left = welcome_img.getLeft();
		// View v = getWindow().findViewById(Window.ID_ANDROID_CONTENT);
		//
		// Class<?> c = null;
		// Object obj = null;
		// Field field = null;
		// int x = 0, sbar = 0;
		// try {
		// c = Class.forName("com.android.internal.R$dimen");
		// obj = c.newInstance();
		// field = c.getField("status_bar_height");
		// x = Integer.parseInt(field.get(obj).toString());
		// sbar = getResources().getDimensionPixelSize(x);
		// } catch (Exception e) {
		// Log.e("get status bar height fail",
		// e.getLocalizedMessage(), e);
		// }
		//
		// Rect frame = new Rect();
		// getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		//
		// final float statusBarHeight = frame.top;
		// final float displayHeight = getWindowManager()
		// .getDefaultDisplay().getHeight();
		//
		// final float toY = (top - statusBarHeight) / displayHeight;
		//
		// // TranslateAnimation translateAnimation = new
		// // TranslateAnimation(
		// // Animation.RELATIVE_TO_SELF, 0f,
		// // Animation.RELATIVE_TO_SELF, -5 / 6f,
		// // Animation.RELATIVE_TO_SELF, 0f,
		// // Animation.RELATIVE_TO_PARENT, - toY);
		// // translateAnimation.setDuration(2000);
		// // translateAnimation.setFillAfter(true);
		// // translateAnimation.setFillEnabled(true);
		// // animationSet.addAnimation(translateAnimation);
		// // welcome_img.startAnimation(animationSet);
		//
		// AnimationSet alphaSet = new AnimationSet(true);
		// AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
		// alphaAnimation.setDuration(1000);
		// alphaSet.addAnimation(alphaAnimation);
		// welcome_text_img.startAnimation(alphaSet);
		// welcome_img.startAnimation(alphaSet);
		// alphaAnimation.setAnimationListener(new AnimationListener() {
		//
		// @Override
		// public void onAnimationStart(Animation animation) {
		// // TODO Auto-generated method stub
		// // welcome_text_img.setVisibility(View.GONE);
		// }
		//
		// @Override
		// public void onAnimationEnd(Animation animation) {
		//
		// }
		//
		// @Override
		// public void onAnimationRepeat(Animation animation) {
		// }
		//
		// });
		//
		// // translateAnimation.setAnimationListener(new
		// // AnimationListener(){
		// //
		// // @Override
		// // public void onAnimationStart(Animation animation) {
		// // // TODO Auto-generated method stub
		// // welcome_img.setVisibility(View.GONE);
		// // }
		// //
		// // @Override
		// // public void onAnimationEnd(Animation animation) {
		// // // TODO Auto-generated method stub
		// // // welcome_img.setX(welcome_img.getWidth() / 6);
		// // // welcome_img.setY(top - statusBarHeight);
		// // // welcome_img.setLayoutParams(new LayoutParams(10, (int)
		// // (top - statusBarHeight)));
		// // // welcome_img.setVisibility(View.VISIBLE);
		// //
		// // }
		// //
		// // @Override
		// // public void onAnimationRepeat(Animation animation) {
		// // // TODO Auto-generated method stub
		// //
		// // }
		// //
		// // });
		// }
		// });

	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void setListener() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onSuccess(JSONObject jsonObject) {
		// TODO Auto-generated method stub
		Log.e("jsonObject", jsonObject.toString());
		if (advert == null)
			advert = new Advert();
		advert.parseJSON(jsonObject);
		if (advert != null) {
			showAdvert();
			saveAdvertLocal();
		} else {
			getLocalAdvert();
			showAdvert();
		}
	}

	@Override
	protected void onFailure(VolleyError error) {
		// TODO Auto-generated method stub
		getLocalAdvert();
		showAdvert();
	}

	private void saveAdvertLocal() {
		try {
			if (this.dbUtils.tableIsExist(Advert.class)) {
				this.dbUtils.deleteAll(Advert.class);
			}
			if (advert != null)
				this.dbUtils.save(advert);
		} catch (DbException e) {
			Log.e("SplashActivity saveAdvertLocal", e.getLocalizedMessage());
		}
	}

	private void getLocalAdvert() {
		try {
			advert = this.dbUtils.findFirst(Advert.class);
		} catch (DbException e) {
			Log.e("SplashActivity saveAdvertLocal", e.getLocalizedMessage());
		}
	}

	private void showAdvert() {
		if (advert != null)
			ImgUtils.imageLoader.displayImage(advert.getUrl(), adPic,
					ImgUtils.liveImageOptions);
	}
}
