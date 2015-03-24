package com.kankan.kankanews.ui.fragment;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
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
import com.kankan.kankanews.ui.fragment.item.New_HomeItemFragment;
import com.kankan.kankanews.ui.view.ColumnHorizontalScrollView;
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
	// private ImageView shade_right;

	private ViewPager mViewpager;

	private ItnetUtils instance;
	private int[] itemWidth;

	private ArrayList<New_HomeCate> homeCates;

	private RelativeLayout rl_column;
	private View main_bg;

	// private int endPosition;
	private int beginPosition;
	private int currentFragmentIndex;
	private boolean isEnd;

	public ArrayList<New_HomeItemFragment> fragments;

	// 不同像素比文字大小
	// int[] textTouchSize = { 12, 10, 7 };
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
		// scrollBlock = (ImageView) inflate.findViewById(R.id.scrollBlock);
		// scrollBlock.getLayoutParams().width = itemWidth;
		mRadioGroupContentWidth = mActivity.mScreenWidth - PixelUtil.dp2px(69);
		columnWidth = mRadioGroupContentWidth / 5 + PixelUtil.dp2px(10);

		params = new LinearLayout.LayoutParams(
				android.widget.LinearLayout.LayoutParams.WRAP_CONTENT,
				android.widget.LinearLayout.LayoutParams.MATCH_PARENT);
		Linparams = new LinearLayout.LayoutParams(
				android.widget.LinearLayout.LayoutParams.WRAP_CONTENT,
				android.widget.LinearLayout.LayoutParams.MATCH_PARENT);

		rl_column = (RelativeLayout) inflate.findViewById(R.id.rl_column);
		main_bg = inflate.findViewById(R.id.main_bg);
		main_bg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				refreshNetDate();
			}
		});

		// shade_right = (ImageView) inflate.findViewById(R.id.shade_right);

		mViewpager = (ViewPager) inflate.findViewById(R.id.viewpager);
		mViewpager.setOffscreenPageLimit(1);
		initLocaDate = initLocalDate();

		if (CommonUtils.isNetworkAvailable(mActivity)) {
			refreshNetDate();
		} else {
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

	public void showData() {
		mRadioGroup_content.removeAllViews();
		if (homeCates != null) {
			final int count = homeCates.size();

			for (int i = 0; i < count; i++) {

				// TextView localTextView = (TextView)
				// mInflater.inflate(R.layout.column_radio_item, null);
				LinearLayout layout = new LinearLayout(mActivity);
				layout.setGravity(Gravity.CENTER);
				final MyTextView columnTextView = new MyTextView(mActivity);
				columnTextView.setTextAppearance(mActivity,
						R.style.top_category_scroll_view_item_text);
				// localTextView.setBackground(getResources().getDrawable(R.drawable.top_category_scroll_text_view_bg));
				// columnTextView
				// .setBackgroundResource(R.drawable.radio_buttong_bg);
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
				params.rightMargin = PixelUtil.dp2px(5);
				params.leftMargin = PixelUtil.dp2px(5);

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
			// int k = checkView.getMeasuredWidth();
			// int l = checkView.getLeft();
			// int i2 = l + k / 2 - mActivity.mScreenWidth / 2;
			// rg_nav_content.getParent()).smoothScrollTo(i2, 0);
			// mColumnHorizontalScrollView.smoothScrollTo(i2, 0);
			// mColumnHorizontalScrollView.smoothScrollTo((position - 2) *
			// mItemWidth , 0);
			//
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

		// mColumnHorizontalScrollView.smoothScrollBy(scrollWidth, 0);

		// }
		// currentFragmentIndex = position;
		// int count = 0;
		// int flat = 1;
		// count = position + 1;
		// if (position > lastPosition) {
		// flat = 1;
		// } else {
		// flat = -1;
		// }
		//
		// lastPosition = position;
		// Animation animation = new TranslateAnimation(endPosition, position
		// * itemWidth, 0, 0);
		//
		// beginPosition = position * itemWidth;
		//
		// currentFragmentIndex = position;
		// if (animation != null) {
		// animation.setFillAfter(true);
		// animation.setDuration(0);
		// scrollBlock.startAnimation(animation);
		// int s = (count - 4);
		// int scrollWidth = 0;
		// // for (int i = 0; i < count; i++) {
		// if (s > 0) {
		// View childAt = mRadioGroup_content.getChildAt(count);
		// if (childAt != null) {
		// scrollWidth += (childAt.getWidth() + PixelUtil.dp2px(20))
		// * flat;
		//
		// mColumnHorizontalScrollView.smoothScrollBy(scrollWidth, 0);
		// } else {
		// childAt = mRadioGroup_content.getChildAt(count - 1);
		// scrollWidth += childAt.getWidth() + PixelUtil.dp2px(40);
		// mColumnHorizontalScrollView.smoothScrollBy(
		// mColumnHorizontalScrollView.getWidth(), 0);
		//
		// }
		// }
		// }

		// }
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
				if (mColumnHorizontalScrollViewWidth - PixelUtil.dp2px(30) <= itemWidth[j]) {
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
				// mColumnHorizontalScrollView.smoothScrollTo(
				// (currentFragmentIndex - 2) * columnWidth, 0);
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
			// if (currentFragmentIndex == position) {
			// endPosition = itemWidth * currentFragmentIndex
			// + (int) (itemWidth * positionOffset);
			// }
			// if (currentFragmentIndex == position + 1) {
			// endPosition = itemWidth * currentFragmentIndex
			// - (int) (itemWidth * (1 - positionOffset));
			// }

			// Animation mAnimation = new TranslateAnimation(beginPosition,
			// endPosition, 0, 0);
			//
			// mColumnHorizontalScrollView.smoothScrollTo(endPosition, 0);
			//
			// mAnimation.setFillAfter(true);
			// mAnimation.setDuration(0);
			// scrollBlock.startAnimation(mAnimation);
			// mColumnHorizontalScrollView.invalidate();
			// beginPosition = endPosition;
		}
	}

	@Override
	public void onPageScrollStateChanged(int state) {
		// if (state == ViewPager.SCROLL_STATE_DRAGGING) {
		// isEnd = false;
		// } else if (state == ViewPager.SCROLL_STATE_SETTLING) {
		// isEnd = true;
		// beginPosition = currentFragmentIndex * itemWidth;
		// if (mViewpager.getCurrentItem() == currentFragmentIndex) {
		// scrollBlock.clearAnimation();
		// Animation animation = null;
		// animation = new TranslateAnimation(endPosition,
		// currentFragmentIndex * itemWidth, 0, 0);
		// animation.setFillAfter(true);
		// animation.setDuration(1);
		// scrollBlock.startAnimation(animation);
		// mColumnHorizontalScrollView.invalidate();
		// endPosition = currentFragmentIndex * itemWidth;
		// }
		// }
	}

	boolean initLocaDate;

	private LinearLayout.LayoutParams params;
	private LinearLayout.LayoutParams Linparams;

	// 当前选中的tab
	public int columnSelectIndex = 0;

	private FragmentStatePagerAdapter adapter;

	// private ImageView scrollBlock;

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
