package com.kankan.kankanews.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IMediaPlayer.OnCompletionListener;
import tv.danmaku.ijk.media.player.IMediaPlayer.OnErrorListener;
import tv.danmaku.ijk.media.player.IMediaPlayer.OnInfoListener;
import tv.danmaku.ijk.media.player.IMediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.android.volley.VolleyError;
import com.kankan.kankanews.base.BaseFragment;
import com.kankanews.kankanxinwen.R;

public class LiveHomeFragment extends BaseFragment implements OnInfoListener,
		OnCompletionListener, OnErrorListener, OnClickListener,
		OnPreparedListener, OnPageChangeListener {
	private View inflate;
	private ViewPager mLiveHomeViewPager;
	private FragmentStatePagerAdapter mLiveHomeViewPagerAdapter;
	private List<BaseFragment> fragments;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreateView(inflater, container, savedInstanceState);
		initView();
		initDate();
		initLinsenter();
		return inflate;
	}

	private void initLinsenter() {
	}

	private void initView() {
		inflate = inflater.inflate(R.layout.fragment_live_home, null);
		mLiveHomeViewPager = (ViewPager) inflate
				.findViewById(R.id.live_home_view_pager);
		mLiveHomeViewPager.setOffscreenPageLimit(0);
		initViewPager();
	}

	private void initViewPager() {
		fragments = new ArrayList<BaseFragment>();
		fragments.add(new LiveLiveListFragment());
		fragments.add(new LiveLiveListFragment());

		mLiveHomeViewPagerAdapter = new FragmentStatePagerAdapter(
				mActivity.getSupportFragmentManager()) {

			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				return 2;
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

		mLiveHomeViewPager.setDrawingCacheEnabled(false);
		mLiveHomeViewPager.setOnPageChangeListener(this);
		mLiveHomeViewPager.setAdapter(mLiveHomeViewPagerAdapter);
		mLiveHomeViewPager.setCurrentItem(0, false);

	}

	private void initDate() {
	}

	@Override
	protected boolean initLocalDate() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void saveLocalDate() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void refreshNetDate() {
		// TODO Auto-generated method stub
	}

	@Override
	protected void loadMoreNetDate() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onSuccessObject(JSONObject jsonObject) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onSuccessArray(JSONArray jsonObject) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPrepared(IMediaPlayer mp) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onError(IMediaPlayer mp, int what, int extra) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onCompletion(IMediaPlayer mp) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onInfo(IMediaPlayer mp, int what, int extra) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void onFailure(VolleyError error) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageSelected(int arg0) {
		// TODO Auto-generated method stub

	}

}
