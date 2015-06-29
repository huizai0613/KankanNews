package com.kankan.kankanews.base;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.iss.view.pulltorefresh.PullToRefreshBase.Mode;
import com.iss.view.pulltorefresh.PullToRefreshListView;
import com.kankan.kankanews.base.IA.CrashApplication;
import com.kankan.kankanews.ui.MainActivity;
import com.kankan.kankanews.ui.view.MyTextView;
import com.kankanews.kankanxinwen.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

public abstract class BaseFragment extends Fragment {

	protected CrashApplication crashApplication;
	public MainActivity mActivity;
	
	protected DisplayImageOptions options;
	protected PullToRefreshListView listview;
	protected LayoutInflater inflater;
	protected boolean isLoadMore;
	protected long lastThreadTimeMillis;

	private RelativeLayout com_title_bar_bg;
	private View com_title_bar_bottom_line;
	private ImageView com_title_bar_left_bt;
	private MyTextView com_title_bar_left_tv;
	private ImageView com_title_bar_content_img;
	private MyTextView com_title_bar_content;
	private ImageView com_title_bar_right_bt;
	private MyTextView com_title_bar_right_tv;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mActivity = (MainActivity) getActivity();
		crashApplication = CrashApplication.getInstance();
		handler = new Handler();
		// options = new DisplayImageOptions.Builder()
		// .showStubImage(R.drawable.empty_photo)
		// .showImageForEmptyUri(R.drawable.empty_photo)
		// .showImageOnFail(R.drawable.empty_photo).cacheInMemory(true)
		// .cacheOnDisc(true).bitmapConfig(Bitmap.Config.RGB_565) // 设置图片的解码类型
		// .build();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		this.inflater = inflater;

		return null;
	}

	protected void initListView(Mode mMode) {
		// 设置PullToRefreshListView的模式
		listview.setMode(mMode);
		// 设置PullRefreshListView上提加载时的加载提示
		listview.getLoadingLayoutProxy(false, true).setPullLabel("上拉加载更多");
		listview.getLoadingLayoutProxy(false, true).setRefreshingLabel(
				"刷新中…");
		listview.getLoadingLayoutProxy(false, true)
				.setReleaseLabel("松开立即加载");

		// 设置PullRefreshListView下拉加载时的加载提示
		listview.getLoadingLayoutProxy(true, false).setPullLabel("下拉可以刷新");
//		listview.getLoadingLayoutProxy(true, false).setRefreshingLabel(
//				"正在刷新请稍后...");
		listview.getLoadingLayoutProxy(true, false).setReleaseLabel("释放后刷新");
	}

	/**
	 * 加载本地数据
	 */
	protected abstract boolean initLocalDate();

	/**
	 * 保存数据到本地
	 */
	protected abstract void saveLocalDate();

	/**
	 * 刷新网络数据
	 */
	protected abstract void refreshNetDate();

	/**
	 * 加载更多网络数据
	 */
	protected abstract void loadMoreNetDate();

	/**
	 * http连接成功
	 */
	protected abstract void onSuccessObject(JSONObject jsonObject);

	/**
	 * http连接成功
	 */
	protected abstract void onSuccessArray(JSONArray jsonObject);

	/**
	 * http连接失败
	 */
	protected abstract void onFailure(VolleyError error);

	// 处理网络出错
	protected ErrorListener mErrorListener = new ErrorListener() {

		@Override
		public void onErrorResponse(VolleyError error) {
			onFailure(error);
		}
	};
	// 处理网络成功(JsonObject)
	protected Listener<JSONObject> mListenerObject = new Listener<JSONObject>() {
		@Override
		public void onResponse(JSONObject jsonObject) {
			onSuccessObject(jsonObject);
		}
	};

	// 处理网络成功(JsonArray)
	protected Listener<JSONArray> mListenerArray = new Listener<JSONArray>() {
		@Override
		public void onResponse(JSONArray jsonObject) {
			onSuccessArray(jsonObject);
		}
	};
	protected Handler handler;

	/**
	 * 设置头部
	 * 
	 * @param view
	 *            容器
	 * @param content_img_id
	 *            中间图标
	 * @param left_img_id
	 *            左侧图标
	 */
	protected void initTitle_Left_bar(View view, int left_img_id,
			int content_img_id, int right_img_id) {

		initTitleView(view);

		com_title_bar_content.setVisibility(View.GONE);
		com_title_bar_content_img.setVisibility(View.VISIBLE);
		com_title_bar_content_img.setImageResource(content_img_id);
		com_title_bar_left_bt.setImageResource(left_img_id);
		// com_title_bar_right_bt.setImageResource(right_img_id);

	}

	// 初始化头部组件
	private void initTitleView(View view) {
		com_title_bar_left_bt = (ImageView) view
				.findViewById(R.id.com_title_bar_left_bt);
		com_title_bar_content = (MyTextView) view
				.findViewById(R.id.com_title_bar_content);
		com_title_bar_left_tv = (MyTextView) view
				.findViewById(R.id.com_title_bar_left_tv);
		com_title_bar_right_tv = (MyTextView) view
				.findViewById(R.id.com_title_bar_right_tv);
		com_title_bar_right_bt = (ImageView) view
				.findViewById(R.id.com_title_bar_right_bt);
		com_title_bar_content_img = (ImageView) view
				.findViewById(R.id.com_title_bai_content_img);
	}
	
	public void initTitle_Right_Left_bar(View view,String content, String leftContent,
			String rightContent, String contentColor, int right_img_id,
			int left_img_id, String bgColor,String lineColor) {
		com_title_bar_bg = (RelativeLayout) view.findViewById(R.id.com_title_bar_bg);
		com_title_bar_bottom_line =  view.findViewById(R.id.com_title_bar_bottom_line);
		com_title_bar_left_bt = (ImageView) view.findViewById(R.id.com_title_bar_left_bt);
		com_title_bar_content = (MyTextView) view.findViewById(R.id.com_title_bar_content);
		com_title_bar_right_bt = (ImageView) view.findViewById(R.id.com_title_bar_right_bt);

		com_title_bar_left_tv = (MyTextView) view.findViewById(R.id.com_title_bar_left_tv);
		com_title_bar_right_tv = (MyTextView) view.findViewById(R.id.com_title_bar_right_tv);

		com_title_bar_left_tv.setText(leftContent);
		com_title_bar_right_tv.setText(rightContent);
		com_title_bar_content.setText(content);
		com_title_bar_content.setTextColor(Color.parseColor(contentColor));
		if (!TextUtils.isEmpty(bgColor))
			com_title_bar_bg.setBackgroundColor(Color.parseColor(bgColor));
		if (!TextUtils.isEmpty(lineColor))
			com_title_bar_bottom_line.setBackgroundColor(Color.parseColor(lineColor));
		com_title_bar_right_bt.setImageResource(right_img_id);
		com_title_bar_left_bt.setImageResource(left_img_id);

	}

	// 左边按钮的点击事件
	protected void setOnLeftClickLinester(OnClickListener clickListener) {
		com_title_bar_left_tv.setOnClickListener(clickListener);
		com_title_bar_left_bt.setOnClickListener(clickListener);
	}

	// 右边按钮的点击事件
	protected void setOnRightClickLinester(OnClickListener clickListener) {
		com_title_bar_right_tv.setOnClickListener(clickListener);
		com_title_bar_right_bt.setOnClickListener(clickListener);
	}

	// 只带id和bean跳转界面 若只有单个新闻，
	public void startAnimActivityById(Class<?> cla, int position, String key,
			int[] bean) {
		Intent intent = new Intent(mActivity, cla);
		intent.putExtra("position", position);
		intent.putExtra(key, bean);
		mActivity.startActivity(intent);
		mActivity.overridePendingTransition(R.anim.in_from_right,
				R.anim.out_to_left);
	}

	public void startAnimActivityByBean(Class<?> cla, String key, BaseBean bean) {
		Intent intent = new Intent(mActivity, cla);
		intent.putExtra(key, bean);
		mActivity.startActivity(intent);
		mActivity.overridePendingTransition(R.anim.in_from_right,
				R.anim.out_to_left);
	}

//	public void startAnimActivityByParameter(Class<?> cla, String mid,
//			String type, String titleurl, String newstime, String titlepiclist,String titlelist) {
//		Intent intent = new Intent(mActivity, cla);
//		intent.setAction("com.sina.weibo.sdk.action.ACTION_SDK_REQ_ACTIVITY");
//		intent.addCategory(Intent.CATEGORY_DEFAULT);
//		intent.putExtra("mid", mid);
//		intent.putExtra("type", type);
//		intent.putExtra("titleurl", titleurl);
//		intent.putExtra("newstime", newstime);
//		intent.putExtra("titlepiclist", titlepiclist);
//		intent.putExtra("titlelist", titlelist);
//		
//		// intent.putExtra(key, bean);
//		mActivity.startActivity(intent);
//		mActivity.overridePendingTransition(R.anim.in_from_right,
//				R.anim.out_to_left);
//	}

//	public void startSubjectActivityByParameter(Class<?> cla, String ztid,
//			String title) {
//		Intent intent = new Intent(mActivity, cla);
//		intent.putExtra("ztid", ztid);
//		intent.putExtra("title", title);
//		mActivity.startActivity(intent);
//		mActivity.overridePendingTransition(R.anim.in_from_right,
//				R.anim.out_to_left);
//	}

	@Override
	public void onResume() {
		super.onResume();
		// 统计页面id
		if (!analyticsId.equals("")) {
			MobclickAgent.onPageStart(analyticsId);
		}
	}

	/**
	 * 统计
	 */
	protected String analyticsId = "";
	protected long currentThreadTimeMillis;

	/**
	 * 页面统计 fragment
	 * 
	 * @param analyticsId
	 *            页面id 不统计页面的 直接写""
	 */
	protected void initAnalytics(String analyticsId) {
		this.analyticsId = analyticsId;
	}

	@Override
	public void onPause() {
		if (!analyticsId.equals("")) {
			MobclickAgent.onPageEnd(analyticsId);
		}
		super.onPause();
	}
}
