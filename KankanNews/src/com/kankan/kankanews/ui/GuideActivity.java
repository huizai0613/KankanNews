package com.kankan.kankanews.ui;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import com.android.volley.VolleyError;
import com.kankan.kankanews.base.BaseActivity;
import com.kankan.kankanews.base.IA.CrashApplication;
import com.kankan.kankanews.utils.CommonUtils;
import com.kankan.kankanews.utils.SharePreferenceUtil;
import com.kankanews.kankanxinwen.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * class desc: 引导界面
 */
public class GuideActivity extends BaseActivity implements OnPageChangeListener {

	private ViewPager vp;
	private ViewPagerAdapter vpAdapter;
	private List<View> views;

	// 应用版本号
	private String version;

	// 底部小点图片
	private ImageView[] dots;

	// 记录当前选中位置
	private int currentIndex;

	// private SharePreferenceUtil spUtil = (SharePreferenceUtil)
	// CrashApplication.getInstance().getSpUtil();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guide);

		initAnalytics("");
		setRightFinsh(false);

		// 初始化页面
		initViews();

		// 初始化底部小点
		initDots();

		// 初始化应用的版本号
		version = CommonUtils.getVersionName(this);
	}

	private void initViews() {
		LayoutInflater inflater = LayoutInflater.from(this);

		views = new ArrayList<View>();
		// 初始化引导图片列表
		views.add(inflater.inflate(R.layout.guide_one, null));
		views.add(inflater.inflate(R.layout.guide_two, null));
		views.add(inflater.inflate(R.layout.guide_three, null));
		views.add(inflater.inflate(R.layout.guide_four, null));
		views.add(inflater.inflate(R.layout.guide_five, null));

		// 初始化Adapter
		vpAdapter = new ViewPagerAdapter(views, this);

		vp = (ViewPager) findViewById(R.id.viewpager);
		vp.setAdapter(vpAdapter);
		// 绑定回调
		vp.setOnPageChangeListener(this);
	}

	private void initDots() {
		LinearLayout ll = (LinearLayout) findViewById(R.id.ll);

		dots = new ImageView[views.size()];

		// 循环取得小点图片
		for (int i = 0; i < views.size(); i++) {
			dots[i] = (ImageView) ll.getChildAt(i);
			// dots[i].setEnabled(true);// 都设为灰色
			dots[i].setBackgroundResource(R.drawable.page_gray);
		}

		currentIndex = 0;
		// dots[currentIndex].setEnabled(false);// 设置为白色，即选中状态
		dots[currentIndex].setBackgroundResource(R.drawable.page_black);
	}

	private void setCurrentDot(int position) {
		if (position < 0 || position > views.size() - 1
				|| currentIndex == position) {
			return;
		}
		// dots[position].setEnabled(false);
		// dots[currentIndex].setEnabled(true);

		dots[position].setBackgroundResource(R.drawable.page_black);
		dots[currentIndex].setBackgroundResource(R.drawable.page_gray);

		currentIndex = position;
	}

	// 当滑动状态改变时调用
	@Override
	public void onPageScrollStateChanged(int arg0) {
	}

	// 当当前页面被滑动时调用
	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}

	// 当新的页面被选中时调用
	@Override
	public void onPageSelected(int arg0) {
		// 设置底部小点选中状态
		setCurrentDot(arg0);
	}

	/**
	 * class desc: 引导页面适配器
	 */
	public class ViewPagerAdapter extends PagerAdapter {

		// 界面列表
		private List<View> views;
		private Activity activity;

		public ViewPagerAdapter(List<View> views, Activity activity) {
			this.views = views;
			this.activity = activity;
		}

		// 销毁arg1位置的界面
		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPager) arg0).removeView(views.get(arg1));
		}

		@Override
		public void finishUpdate(View arg0) {
		}

		// 获得当前界面数
		@Override
		public int getCount() {
			if (views != null) {
				return views.size();
			}
			return 0;
		}

		// 初始化arg1位置的界面
		@Override
		public Object instantiateItem(View arg0, int arg1) {
			((ViewPager) arg0).addView(views.get(arg1), 0);
			if (arg1 == views.size() - 1) {
				ImageView mStartWeiboImageButton = (ImageView) arg0
						.findViewById(R.id.guide_start);
				mStartWeiboImageButton
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								// 设置已经引导
								setGuided();
								goHome();

							}

						});
			}
			return views.get(arg1);
		}

		private void goHome() {
			// 跳转
			Intent intent = new Intent(activity, MainActivity.class);
			activity.startActivity(intent);
			activity.finish();
		}

		/**
		 * 
		 * method desc：设置已经引导过了，下次启动不用再次引导
		 */
		private void setGuided() {
			spUtil.setVersion(version);
			spUtil.setFristComing(false);
		}

		// 判断是否由对象生成界面
		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return (arg0 == arg1);
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {
		}

	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub

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

	}

	@Override
	protected void onFailure(VolleyError error) {
		// TODO Auto-generated method stub

	}

}
