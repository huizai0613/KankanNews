package com.kankan.kankanews.base;

import java.io.Serializable;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.json.JSONObject;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.BroadcastReceiver;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.iss.view.pulltorefresh.PullToRefreshBase.Mode;
import com.iss.view.pulltorefresh.PullToRefreshListView;
import com.kankan.kankanews.base.IA.CrashApplication;
import com.kankan.kankanews.bean.New_News_Home;
import com.kankan.kankanews.bean.interfaz.CanBeShared;
import com.kankan.kankanews.dialog.Loading_Dialog;
import com.kankan.kankanews.dialog.TishiMsgHint;
import com.kankan.kankanews.ui.view.MyTextView;
import com.kankan.kankanews.utils.NetUtils;
import com.kankan.kankanews.utils.SharePreferenceUtil;
import com.kankan.kankanews.utils.XunaoLog;
import com.kankanews.kankanxinwen.R;
import com.lidroid.xutils.DbUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.bean.SHARE_MEDIA;

public abstract class BaseActivity extends FragmentActivity implements
		CanBeShared {

	public DbUtils dbUtils;
	// protected Loading_Dialog loading_dialog;
	public CrashApplication mApplication;
	public SharePreferenceUtil spUtil;
	public int mScreenWidth;
	public int mScreenHeight;
	private XunaoLog yeLog;
	protected BaseActivity mContext;
	protected NetUtils netUtils;
	// protected ImageLoader imageLoader = ImageLoader.getInstance();
	protected PullToRefreshListView listview;
	protected boolean isLoadMore;

	protected RelativeLayout titleBarView;
	protected ImageView titleBarLeftImg;
	protected ImageView titleBarLeftImgSecond;
	protected ImageView titleBarContentImg;
	protected MyTextView titleBarContent;
	protected MyTextView titleBarRightText;
	protected ImageView titleBarRightImg;
	protected ImageView titleBarRightImgSecond;
	private GestureDetector gestureDetector;
	protected boolean isRightFinsh = true;
	protected HashMap<String, SoftReference<Bitmap>> imageCache;

	public View nightView;

	public boolean isFinsh;
	protected boolean isNeedNightView = true;

	public void setRightFinsh(boolean isRightFinsh) {
		this.isRightFinsh = isRightFinsh;
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {

		try {
			gestureDetector.onTouchEvent(ev);
			return super.dispatchTouchEvent(ev);
		} catch (IllegalArgumentException e) {
			return true;
		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Set<Entry<String, SoftReference<Bitmap>>> entrySet = imageCache
				.entrySet();
		for (Entry<String, SoftReference<Bitmap>> e : entrySet) {
			Bitmap bitmap = e.getValue().get();
			if (bitmap != null) {
				bitmap.recycle();
			}
			imageCache.put(e.getKey(), new SoftReference<Bitmap>(null));
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		mApplication = (CrashApplication) CrashApplication.getInstance();
		spUtil = mApplication.getSpUtil();
		super.onCreate(savedInstanceState);
		// registerReceiver(mDialogReceiver, new IntentFilter("dialog"));
		imageCache = new HashMap<String, SoftReference<Bitmap>>();
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		mScreenWidth = metric.widthPixels;
		mScreenHeight = metric.heightPixels;
		yeLog = XunaoLog.yLog();
		mContext = this;
		mApplication.addActivity(this);
		netUtils = NetUtils.getInstance(this);
		// loading_dialog = new Loading_Dialog(mContext, R.style.MyDialogStyle);

		dbUtils = mApplication.getDbUtils();

		gestureDetector = new GestureDetector(this,
				new GestureDetector.OnGestureListener() {

					@Override
					public boolean onSingleTapUp(MotionEvent e) {
						// TODO Auto-generated method stub
						return false;
					}

					@Override
					public void onShowPress(MotionEvent e) {
						// TODO Auto-generated method stub

					}

					@Override
					public boolean onScroll(MotionEvent e1, MotionEvent e2,
							float distanceX, float distanceY) {

						return false;
					}

					@Override
					public void onLongPress(MotionEvent e) {
						// TODO Auto-generated method stub

					}

					// 右滑手势
					@Override
					public boolean onFling(MotionEvent e1, MotionEvent e2,
							float velocityX, float velocityY) {
						if (isRightFinsh) {
							// 右滑动
							if (e2.getX() - e1.getX() > 200
									&& Math.abs(e2.getY() - e1.getY()) < Math
											.abs(e2.getX() - e1.getX())) {
								onBackPressed();
								isFinsh = true;
								return true;
							}
						}
						return false;
					}

					@Override
					public boolean onDown(MotionEvent e) {
						// TODO Auto-generated method stub
						return false;
					}
				});

	}

	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);
		initView();
		initData();
		setListener();
		if (isNeedNightView)
			initNightView(false);
	}

	protected void initListView() {
		// 设置PullToRefreshListView的模式
		listview.setMode(Mode.BOTH);

		// 设置PullRefreshListView上提加载时的加载提示
		listview.getLoadingLayoutProxy(false, true).setPullLabel("上拉加载更多");
		listview.getLoadingLayoutProxy(false, true).setRefreshingLabel("刷新中…");
		listview.getLoadingLayoutProxy(false, true).setReleaseLabel("松开立即加载");

		// 设置PullRefreshListView下拉加载时的加载提示
		listview.getLoadingLayoutProxy(true, false).setPullLabel("下拉可以刷新");
		// listview.getLoadingLayoutProxy(true, false)
		// .setRefreshingLabel("正在淘江湖~");
		listview.getLoadingLayoutProxy(true, false).setReleaseLabel("释放后刷新");
	}

	// 处理网络出错
	protected ErrorListener mErrorListener = new ErrorListener() {

		@Override
		public void onErrorResponse(VolleyError error) {
			onFailure(error);
		}
	};
	// 处理网络成功
	protected Listener<JSONObject> mListener = new Listener<JSONObject>() {

		@Override
		public void onResponse(JSONObject jsonObject) {
			onSuccess(jsonObject);
		}
	};

	/**
	 * 打Log ShowLog
	 * 
	 * @return void
	 * @throws
	 */
	public void ShowLog(String msg) {
		yeLog.i(msg);
	}

	public void startAnimActivity2Obj(Class<?> cla, String key, BaseBean bean) {

		Intent intent = new Intent(this, cla);
		intent.putExtra(key, bean);
		this.startActivity(intent);
		this.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
	}

	public void startAnimActivity2Obj(Class<?> cla, String key, int bean) {

		Intent intent = new Intent(this, cla);
		intent.putExtra(key, bean);
		this.startActivity(intent);
		this.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
	}

	public void startAnimActivity2Obj(Class<?> cla, String key, String num,
			BaseBean bean) {
		Intent intent = new Intent(this, cla);
		intent.putExtra(key, bean);
		intent.putExtra("NUM", num);
		this.startActivity(intent);
		this.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
	}

	public void startAnimActivity2ObjForResult(Class<?> cla, String key,
			int code, String num, BaseBean bean) {
		Intent intent = new Intent(this, cla);
		intent.putExtra(key, bean);
		intent.putExtra("NUM", num);
		this.startActivityForResult(intent, code);
		this.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
	}

	public void startAnimActivity2ObjForResult(Class<?> cla, String key,
			int code, BaseBean bean) {
		Intent intent = new Intent(this, cla);
		intent.putExtra(key, bean);
		this.startActivityForResult(intent, code);
		this.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
	}

	public void startAnimActivityByParameter(Class<?> cla, String mid,
			String type, String titleurl, String newstime, String titlelist,
			String titlePic, String sharedPic, String intro) {
		Intent intent = new Intent(this, cla);
		intent.setAction("com.sina.weibo.sdk.action.ACTION_SDK_REQ_ACTIVITY");
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		intent.putExtra("mid", mid);
		intent.putExtra("type", type);
		intent.putExtra("titleurl", titleurl);
		intent.putExtra("newstime", newstime);
		intent.putExtra("titlelist", titlelist);
		intent.putExtra("titlePic", titlePic);
		intent.putExtra("sharedPic", sharedPic);
		intent.putExtra("intro", intro);
		this.startActivity(intent);
		this.overridePendingTransition(R.anim.in_from_right, R.anim.alpha_out);
	}

	public void startAnimActivityByParameterAlpha(Class<?> cla, String mid,
			String type, String titleurl, String newstime, String titlelist,
			String titlePic, String sharedPic) {
		Intent intent = new Intent(this, cla);
		intent.setAction("com.sina.weibo.sdk.action.ACTION_SDK_REQ_ACTIVITY");
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		intent.putExtra("mid", mid);
		intent.putExtra("type", type);
		intent.putExtra("titleurl", titleurl);
		intent.putExtra("newstime", newstime);
		intent.putExtra("titlelist", titlelist);
		intent.putExtra("titlePic", titlePic);
		intent.putExtra("sharedPic", sharedPic);
		this.startActivity(intent);
		this.overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
	}

	public void startAnimActivityByParameter(Class<?> cla, New_News_Home mews) {
		Intent intent = new Intent(this, cla);
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		intent.putExtra("HOME_NEWS", (Serializable) mews);
		this.startActivity(intent);
		this.overridePendingTransition(R.anim.in_from_right, R.anim.alpha_out);
	}

	public void startSubjectActivityByParameter(Class<?> cla, String ztid,
			String title, String titlepic, String titleurl, String titlePic,
			String sharedPic, String intro) {
		Intent intent = new Intent(this, cla);
		intent.putExtra("ztid", ztid);
		intent.putExtra("title", title);
		intent.putExtra("titlepic", titlepic);
		intent.putExtra("titleurl", titleurl);
		intent.putExtra("titlePic", titlePic);
		intent.putExtra("sharedPic", sharedPic);
		intent.putExtra("intro", intro);
		this.startActivity(intent);
		this.overridePendingTransition(R.anim.in_from_right, R.anim.alpha_out);
	}

	public void startSubjectActivityByParameterAlpha(Class<?> cla, String ztid,
			String title, String titlepic, String titleurl, String titlePic,
			String sharedPic) {
		Intent intent = new Intent(this, cla);
		intent.putExtra("ztid", ztid);
		intent.putExtra("title", title);
		intent.putExtra("titlepic", titlepic);
		intent.putExtra("titleurl", titleurl);
		intent.putExtra("titlePic", titlePic);
		intent.putExtra("sharedPic", sharedPic);
		this.startActivity(intent);
		this.overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
	}

	public void startAnimActivity(Class<?> cla) {
		this.startActivity(new Intent(this, cla));
		this.overridePendingTransition(R.anim.in_from_right, R.anim.alpha_out);
	}

	public void startAnimActivityBack(Class<?> cla) {
		this.startActivity(new Intent(this, cla));
		this.overridePendingTransition(R.anim.in_from_left, R.anim.alpha_out);
	}

	public void startAnim_back_Activity(Class<?> cla) {
		this.startActivity(new Intent(this, cla));
		this.overridePendingTransition(R.anim.in_from_left, R.anim.alpha_out);
	}

	public void startAnimActivityAndFinsh(Class<?> cla) {
		this.startActivity(new Intent(this, cla));
		this.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
		finish();
	}

	// 只带id和bean跳转界面
	public void startAnimActivityById(Class<?> cla, int position, String key,
			int[] bean) {
		Intent intent = new Intent(this, cla);
		intent.putExtra("position", position);
		intent.putExtra(key, bean);
		this.startActivity(intent);
		this.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
	}

	public void AnimFinsh() {
		this.finish();
		this.overridePendingTransition(R.anim.alpha_in, R.anim.out_to_right);
	}

	// init头部view
	protected void initTitleBarView() {
		titleBarView = (RelativeLayout) findViewById(R.id.title_bar_view);
		// 左
		titleBarLeftImg = (ImageView) findViewById(R.id.title_bar_left_img);
		titleBarLeftImgSecond = (ImageView) findViewById(R.id.title_bar_left_img_second);
		// 中
		titleBarContentImg = (ImageView) findViewById(R.id.title_bar_content_img);
		titleBarContent = (MyTextView) findViewById(R.id.title_bar_content);
		// 右
		titleBarRightImgSecond = (ImageView) findViewById(R.id.title_bar_right_second_img);
		titleBarRightImg = (ImageView) findViewById(R.id.title_bar_right_img);
		titleBarRightText = (MyTextView) findViewById(R.id.title_bar_right_text);

		titleBarLeftImg.setVisibility(View.GONE);
		titleBarLeftImgSecond.setVisibility(View.GONE);
		titleBarContentImg.setVisibility(View.GONE);
		titleBarContent.setVisibility(View.GONE);
		titleBarRightImgSecond.setVisibility(View.GONE);
		titleBarRightImg.setVisibility(View.GONE);
		titleBarRightText.setVisibility(View.GONE);
	}

	// 左图标 中文字 右图标
	protected void initTitle_Left_bar(int left_img, String mid_text,
			int right_img) {
		initTitleBarView();
		titleBarLeftImg.setVisibility(View.VISIBLE);
		titleBarContent.setVisibility(View.VISIBLE);
		titleBarRightImg.setVisibility(View.VISIBLE);
		titleBarLeftImg.setImageResource(left_img);
		titleBarContent.setText(mid_text);
		titleBarRightImg.setImageResource(right_img);
	}

	// 只有左侧和中间的 图标
	protected void initTitleLeftBar(int left_img_id, int mid_img_id) {
		initTitleBarView();

		titleBarContentImg.setVisibility(View.VISIBLE);
		titleBarLeftImg.setVisibility(View.VISIBLE);
		titleBarContentImg.setImageResource(mid_img_id);
		titleBarLeftImg.setImageResource(left_img_id);
	}

	protected void initTitleLeftBar(String content, int leftImgId) {
		initTitleBarView();
		titleBarContent.setVisibility(View.VISIBLE);
		titleBarLeftImg.setVisibility(View.VISIBLE);

		titleBarContent.setText(content);
		titleBarLeftImg.setImageResource(leftImgId);

	}

	protected void initTitleBar(String content) {
		initTitleBarView();
		titleBarContent.setVisibility(View.VISIBLE);

		titleBarContent.setText(content);
	}

	protected void initTitleRightBar(String content, String rightContent,
			int right_img_id) {
		initTitleBarView();
		titleBarRightText.setVisibility(View.VISIBLE);
		titleBarContent.setVisibility(View.VISIBLE);
		titleBarRightImg.setVisibility(View.VISIBLE);

		titleBarRightText.setText(rightContent);
		titleBarContent.setText(content);
		titleBarRightImg.setImageResource(right_img_id);
	}

	public void initTitleBarContent(String content, int leftImgSecondId,
			String rightContent, int right_img_id, int left_img_id) {
		initTitleBarView();
		titleBarLeftImgSecond.setVisibility(View.VISIBLE);
		titleBarRightText.setVisibility(View.VISIBLE);
		titleBarContent.setVisibility(View.VISIBLE);
		titleBarRightImg.setVisibility(View.VISIBLE);
		titleBarLeftImg.setVisibility(View.VISIBLE);

		titleBarLeftImgSecond.setImageResource(leftImgSecondId);
		titleBarRightText.setText(rightContent);
		titleBarContent.setText(content);
		titleBarRightImg.setImageResource(right_img_id);
		titleBarLeftImg.setImageResource(left_img_id);
	}

	public void initTitleBarIcon(int contentId, int leftImgId,
			int leftImgSecondId, int rightImgId, int rightImgSecondId) {
		initTitleBarView();
		titleBarContentImg.setVisibility(View.VISIBLE);
		titleBarRightImgSecond.setVisibility(View.VISIBLE);
		titleBarLeftImg.setVisibility(View.VISIBLE);
		titleBarRightImg.setVisibility(View.VISIBLE);

		titleBarRightImg.setImageResource(rightImgId);
		titleBarRightImgSecond.setImageResource(rightImgSecondId);
		titleBarLeftImg.setImageResource(leftImgId);
		titleBarContentImg.setImageResource(contentId);
		titleBarLeftImgSecond.setImageResource(leftImgSecondId);
	}

	// 左边按钮的点击事件
	protected void setOnLeftClickLinester(OnClickListener clickListener) {
		titleBarLeftImgSecond.setOnClickListener(clickListener);
		titleBarLeftImg.setOnClickListener(clickListener);
	}

	// 右边按钮的点击事件
	protected void setOnRightClickLinester(OnClickListener clickListener) {
		titleBarRightImg.setOnClickListener(clickListener);
		titleBarRightImgSecond.setOnClickListener(clickListener);
		titleBarRightText.setOnClickListener(clickListener);
	}

	// 右边按钮的点击事件
	protected void setOnContentClickLinester(OnClickListener clickListener) {
		titleBarContent.setOnClickListener(clickListener);
		titleBarContentImg.setOnClickListener(clickListener);
	}

	protected void showLeftBarTv() {
		titleBarLeftImgSecond.setVisibility(View.VISIBLE);
	}

	/**
	 * 初始化视图
	 */
	protected abstract void initView();

	/**
	 * 初始化数据
	 */
	protected abstract void initData();

	/**
	 * 设置监听事件
	 */
	protected abstract void setListener();

	/**
	 * http连接成功
	 */
	protected abstract void onSuccess(JSONObject jsonObject);

	/**
	 * http连接失败
	 */
	protected abstract void onFailure(VolleyError error);

	@Override
	public void onBackPressed() {
		AnimFinsh();
	}

	/**
	 * 统计
	 */
	protected String analyticsId = "";

	/**
	 * 页面统计 activity
	 * 
	 * @param analyticsId
	 *            页面id 不统计页面的 直接写""
	 */
	protected void initAnalytics(String analyticsId) {
		this.analyticsId = analyticsId;
	}

	public boolean isTaskTop;

	@Override
	protected void onResume() {
		super.onResume();
		isTaskTop = true;
		if (!analyticsId.equals("")) {
			MobclickAgent.onPageStart(analyticsId);
		}
		MobclickAgent.onResume(mContext);
	}

	@Override
	protected void onPause() {
		super.onPause();
		isTaskTop = false;
		if (!analyticsId.equals("")) {
			MobclickAgent.onPageEnd(analyticsId);
		}
		MobclickAgent.onPause(mContext);
	}

	private BroadcastReceiver mDialogReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (mContext.getComponentName().toString()
					.equals(getTopActivity(mContext))
					&& mContext.isTaskTop) {
				if (action.equals("dialog")) {
					String stringExtra = intent.getStringExtra("value");
					final TishiMsgHint dialog = new TishiMsgHint(mContext,
							R.style.MyDialog1);
					dialog.setContent(stringExtra, "我知道了");
					dialog.setCancleListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							dialog.dismiss();
						}
					});
					dialog.show();
				}
			}
			// xuda注销广播
			context.unregisterReceiver(this);
		}
	};

	String getTopActivity(Activity context) {
		ActivityManager manager = (ActivityManager) context
				.getSystemService(ACTIVITY_SERVICE);
		List<RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1);
		if (runningTaskInfos != null)
			return runningTaskInfos.get(0).topActivity.toString();
		else
			return null;
	}

	public void refresh() {

	}

	public void Commit_Share(SHARE_MEDIA platform) {

	}

	public void shareReBack() {

	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		this.mApplication.removeActivity(this);
		super.finish();
	}

	public void finishNoRemove() {
		// TODO Auto-generated method stub
		super.finish();
	}

	public void initNightView(boolean isFullScreen) {
		if (!spUtil.getIsDayMode())
			chage2Night();
	}

	public void chage2Day() {
		// mNightView.setBackgroundResource(android.R.color.transparent);
		if (nightView != null)
			nightView.setVisibility(View.GONE);
	}

	public void chage2Night() {
		// mNightView.setBackgroundResource(R.color.night_mask);
		if (nightView != null)
			nightView.setVisibility(View.VISIBLE);
	}

	public void changeFontSize() {

	}

	public void changeDayMode(boolean isDay) {
		if (isDay)
			chage2Day();
		else
			chage2Night();
	}

	public void copy2Clip() {

	}

	protected boolean initLocalDate() {
		// TODO Auto-generated method stub
		return true;
	}

	protected void saveLocalDate() {
		// TODO Auto-generated method stub

	}
}
