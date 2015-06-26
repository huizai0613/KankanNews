package com.kankan.kankanews.ui.fragment;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.kankan.kankanews.base.BaseFragment;
import com.kankan.kankanews.bean.New_HomeCate;
import com.kankan.kankanews.exception.NetRequestException;
import com.kankan.kankanews.net.ItnetUtils;
import com.kankan.kankanews.search.SearchMainActivity;
import com.kankan.kankanews.ui.fragment.item.New_HomeItemFragment;
import com.kankan.kankanews.ui.view.MyTextView;
import com.kankan.kankanews.utils.CommonUtils;
import com.kankan.kankanews.utils.PixelUtil;
import com.kankanews.kankanxinwen.R;
import com.lidroid.xutils.exception.DbException;

public class New_HomeFragment extends BaseFragment implements
		OnPageChangeListener {

	private View inflate;
	private int mRadioGroupContentWidth;
	private HorizontalScrollView mColumnHorizontalScrollView;
	private LinearLayout mRadioGroup_content;
	private ImageView shade_left;
	private ImageView searchBut;
	// private ImageView shade_right;

	private ViewPager mViewpager;

	private ItnetUtils instance;
	private int[] itemWidth;

	private ArrayList<New_HomeCate> homeCates;

	private View main_bg;

	private int currentFragmentIndex;
	private boolean isEnd;

	public ArrayList<New_HomeItemFragment> fragments;

	public static String PUSH_NEWS_ID = null;

	int[] textNomalSize = { R.dimen.textsize_1, R.dimen.textsize_2,
			R.dimen.textsize_3, R.dimen.textsize_4 };

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		instance = ItnetUtils.getInstance(mActivity);
		inflate = inflater.inflate(R.layout.new_fragment_home, null);
		mColumnHorizontalScrollView = (HorizontalScrollView) inflate
				.findViewById(R.id.mColumnHorizontalScrollView);
		mRadioGroup_content = (LinearLayout) inflate
				.findViewById(R.id.mRadioGroup_content);

		mRadioGroupContentWidth = mActivity.mScreenWidth - PixelUtil.dp2px(69);
		columnWidth = mRadioGroupContentWidth / 5 + PixelUtil.dp2px(10);

		params = new LinearLayout.LayoutParams(
				android.widget.LinearLayout.LayoutParams.WRAP_CONTENT,
				android.widget.LinearLayout.LayoutParams.MATCH_PARENT);
		Linparams = new LinearLayout.LayoutParams(
				android.widget.LinearLayout.LayoutParams.WRAP_CONTENT,
				android.widget.LinearLayout.LayoutParams.MATCH_PARENT);

		main_bg = inflate.findViewById(R.id.main_bg);

		searchBut = (ImageView) inflate.findViewById(R.id.home_search_but);
		mViewpager = (ViewPager) inflate.findViewById(R.id.viewpager);
		mViewpager.setOffscreenPageLimit(1);

		setListener();

		if (CommonUtils.isNetworkAvailable(mActivity)) {
			refreshNetDate();
		} else {
			initLocaDate = initLocalDate();
			if (!initLocaDate) {
				main_bg.setVisibility(View.VISIBLE);
			}
		}

		return inflate;
	}

	public Resources getResourcesSelf() {
		Resources res = super.getResources();
		Configuration config = new Configuration();
		config.setToDefaults();
		res.updateConfiguration(config, res.getDisplayMetrics());
		return res;
	}

	private void setListener() {
		// TODO Auto-generated method stub
		searchBut.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				New_HomeFragment.this.startActivity(new Intent(
						New_HomeFragment.this.mActivity,
						SearchMainActivity.class));
				New_HomeFragment.this.mActivity.overridePendingTransition(
						R.anim.in_from_right, R.anim.alpha_op );//R.anim.out_to_top
			}
		});
		main_bg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				refreshNetDate();
			}
		});
	}

	public void showData() {
		Log.e("PixelUtil.getScale()", PixelUtil.getScale() + "");
		mRadioGroup_content.removeAllViews();
		if (homeCates != null) {
			final int count = homeCates.size();

			for (int i = 0; i < count; i++) {

				LinearLayout layout = new LinearLayout(mActivity);
				layout.setGravity(Gravity.CENTER);
				final MyTextView columnTextView = new MyTextView(mActivity);
				columnTextView.setTextAppearance(mActivity,
						R.style.top_category_scroll_view_item_text);

				columnTextView.setGravity(Gravity.CENTER);
				columnTextView.setId(i);
				columnTextView.setText(homeCates.get(i).getTitle());
				columnTextView.setTextColor(getResources().getColorStateList(
						R.color.top_category_scroll_text_color_day));
				columnTextView
						.setBackgroundResource(R.drawable.select_columnitem);

				if (columnSelectIndex == i) {
					columnTextView.setSelected(true);
					columnTextView.setTextSize(getResourcesSelf()
							.getDimensionPixelSize(
									textNomalSize[PixelUtil.getScale()]));
				} else {
					columnTextView.setTextSize(getResourcesSelf().getDimension(
							textNomalSize[PixelUtil.getScale()]));
				}
				columnTextView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						for (int i = 0; i < mRadioGroup_content.getChildCount(); i++) {

							LinearLayout layout = (LinearLayout) mRadioGroup_content
									.getChildAt(i);
							TextView localView = (TextView) layout
									.getChildAt(0);

							if (localView != v) {
								localView.setSelected(false);
								localView.setTextSize(getResourcesSelf()
										.getDimensionPixelSize(
												textNomalSize[PixelUtil
														.getScale()]));
							} else {
								localView.setTextSize(getResourcesSelf()
										.getDimensionPixelSize(
												textNomalSize[PixelUtil
														.getScale()]));
								localView.setSelected(true);
								mViewpager.setCurrentItem(i, false);
							}
						}
					}
				});
				params.rightMargin = PixelUtil.dp2px(0);
				params.leftMargin = PixelUtil.dp2px(18);

				layout.addView(columnTextView, params);
				mRadioGroup_content.addView(layout, Linparams);

			}
			totalChildCount = mRadioGroup_content.getChildCount();

			initViewPager();

		}
	}

	private int[] columeWidth;

	// 初始化ViewPager
	private void initViewPager() {

		fragments = new ArrayList<New_HomeItemFragment>();
		New_HomeItemFragment fragment = null;
		if (homeCates != null) {
			for (New_HomeCate hc : homeCates) {
				fragment = new New_HomeItemFragment();
				fragment.setAppclassidAndSP(hc.getAppclassid(), hc.getSp());
				fragments.add(fragment);
			}

			adapter = new FragmentStatePagerAdapter(
					mActivity.getSupportFragmentManager()) {

				@Override
				public int getCount() {
					// TODO Auto-generated method stub
					return fragments.size();
				}

				@Override
				public Fragment getItem(int arg0) {
					return fragments.get(arg0);
				}

				@Override
				public int getItemPosition(Object object) {
					// TODO Auto-generated method stub
					return POSITION_NONE;
				}

				@Override
				public Object instantiateItem(ViewGroup arg0, int arg1) {
					Object obj = super.instantiateItem(arg0, arg1);
					return obj;
				}

				@Override
				public void destroyItem(ViewGroup container, int position,
						Object object) {
					super.destroyItem(container, position, object);
					fragments.get(position).recycle();

				}

			};

			mViewpager.setDrawingCacheEnabled(false);
			mViewpager.setOnPageChangeListener(this);
			mViewpager.setAdapter(adapter);
		}

	}

	/**
	 * 选择的Column里面的Tab
	 * */
	@SuppressLint("ResourceAsColor")
	private void selectTab(int tab_postion) {
		columnSelectIndex = tab_postion;
		for (int i = 0; i < mRadioGroup_content.getChildCount(); i++) {
			LinearLayout layout = (LinearLayout) mRadioGroup_content
					.getChildAt(tab_postion);
			TextView checkView = (TextView) layout.getChildAt(0);
		}
		// 判断是否选中
		for (int j = 0; j < mRadioGroup_content.getChildCount(); j++) {
			LinearLayout layout = (LinearLayout) mRadioGroup_content
					.getChildAt(j);
			TextView checkView1 = (TextView) layout.getChildAt(0);
			boolean ischeck;
			if (j == tab_postion) {
				ischeck = true;
				checkView1.setTextSize(getResourcesSelf()
						.getDimensionPixelSize(
								textNomalSize[PixelUtil.getScale()]));
			} else {
				ischeck = false;
				checkView1.setTextSize(getResourcesSelf()
						.getDimensionPixelSize(
								textNomalSize[PixelUtil.getScale()]));
			}
			checkView1.setSelected(ischeck);
		}
	}

	@Override
	public void onPageSelected(final int position) {

		if (itemWidth == null) {
			childCount = mRadioGroup_content.getChildCount();
			itemWidth = new int[childCount];
			for (int j = 0; j < childCount; j++) {
				for (int k = 0; k <= j; k++) {
					itemWidth[j] += mRadioGroup_content.getChildAt(k)
							.getWidth();
				}
			}
		}
		if (mColumnHorizontalScrollViewWidth == 0) {
			mColumnHorizontalScrollViewWidth = mColumnHorizontalScrollView
					.getWidth();
			for (int j = 0; j < childCount; j++) {
				if (mColumnHorizontalScrollViewWidth - PixelUtil.dp2px(60) <= itemWidth[j]) {
					bottomPosition = j - 1;
					break;
				}

			}

		}

		if (currentFragmentIndex > position) {
			currentFragmentIndex = position;

			// mColumnHorizontalScrollView.smoothScrollTo(totalChildCount
			// - (-currentFragmentIndex + 1) * columnWidth, 0);

			if (totalChildCount - (-currentFragmentIndex + totalChildCount) - 2 >= 0) {
				mColumnHorizontalScrollView
						.smoothScrollTo(
								itemWidth[totalChildCount
										- (-currentFragmentIndex + totalChildCount)
										- 2], 0);
			} else {
				mColumnHorizontalScrollView.smoothScrollTo(0, 0);
			}

		} else {
			currentFragmentIndex = position;
			if (currentFragmentIndex - bottomPosition - 1 >= 0) {

				mColumnHorizontalScrollView.smoothScrollTo(
						itemWidth[(currentFragmentIndex - bottomPosition - 1)],
						0);
			}
		}

		selectTab(position);
	}

	@Override
	public void onPageScrolled(int position, float positionOffset,
			int positionOffsetPixels) {
		if (!isEnd) {

		}
	}

	@Override
	public void onPageScrollStateChanged(int state) {

	}

	boolean initLocaDate;

	private LinearLayout.LayoutParams params;
	private LinearLayout.LayoutParams Linparams;

	// 当前选中的tab
	public int columnSelectIndex = 0;

	private FragmentStatePagerAdapter adapter;

	private int lastPosition;
	private int columnWidth;
	private int totalChildCount;
	private int mColumnHorizontalScrollViewWidth;
	private int childCount;
	private int bottomPosition;

	@Override
	protected boolean initLocalDate() {
		try {
			homeCates = (ArrayList<New_HomeCate>) mActivity.dbUtils
					.findAll(New_HomeCate.class);
			if (homeCates != null) {
				showData();
				return true;
			} else {
				return false;
			}
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	@Override
	protected void saveLocalDate() {
		try {
			mActivity.dbUtils.deleteAll(New_HomeCate.class);
			mActivity.dbUtils.saveAll(homeCates);
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected void refreshNetDate() {
		instance.getNewHomeCateData(mListenerArray, mErrorListener);
	}

	@Override
	protected void loadMoreNetDate() {
	}

	@Override
	protected void onSuccessObject(JSONObject jsonObject) {

	}

	@Override
	protected void onSuccessArray(JSONArray jsonObject) {

		if (jsonObject != null) {
			main_bg.setVisibility(View.GONE);
			int length = jsonObject.length();
			homeCates = new ArrayList<New_HomeCate>();
			New_HomeCate mNew_HomeCate = null;
			for (int i = 0; i < length; i++) {
				mNew_HomeCate = new New_HomeCate();
				try {
					mNew_HomeCate.parseJSON(jsonObject.optJSONObject(i));
					homeCates.add(mNew_HomeCate);
				} catch (NetRequestException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			saveLocalDate();
			showData();
		} else {
			if (!initLocaDate) {
				main_bg.setVisibility(View.VISIBLE);
			}
		}
	}

	@Override
	protected void onFailure(VolleyError error) {
		if (!initLocaDate) {
			main_bg.setVisibility(View.VISIBLE);
		}
	}

}
