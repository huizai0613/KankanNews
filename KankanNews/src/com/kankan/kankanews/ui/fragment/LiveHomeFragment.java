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
import tv.danmaku.ijk.media.widget.VideoView;
import android.content.pm.ActivityInfo;
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
import com.kankan.kankanews.bean.LiveLiveObj;
import com.kankan.kankanews.bean.interfaz.CanBePlay;
import com.kankan.kankanews.utils.DebugLog;
import com.kankanews.kankanxinwen.R;

public class LiveHomeFragment extends BaseFragment implements OnInfoListener,
		OnCompletionListener, OnErrorListener, OnClickListener,
		OnPreparedListener, OnPageChangeListener {
	private View inflate;
	private ViewPager mLiveHomeViewPager;
	private FragmentStatePagerAdapter mLiveHomeViewPagerAdapter;
	private List<BaseFragment> fragments;
	private View videoRootView;
	private VideoView liveVideoView;
	private boolean isPlayStat;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreateView(inflater, container, savedInstanceState);
		initView();
		initData();
		initLinsenter();
		return inflate;
	}

	private void initLinsenter() {
		liveVideoView.setOnPreparedListener(this);
		liveVideoView.setOnInfoListener(this);
		liveVideoView.setOnErrorListener(this);
	}

	private void initView() {
		inflate = inflater.inflate(R.layout.fragment_live_home, null);
		mLiveHomeViewPager = (ViewPager) inflate
				.findViewById(R.id.live_home_view_pager);
		videoRootView = inflate.findViewById(R.id.video_root_view);
		liveVideoView = (VideoView) inflate.findViewById(R.id.live_video_view);
		initViewPager();
	}

	private void initViewPager() {
		mLiveHomeViewPager.setOffscreenPageLimit(0);
		fragments = new ArrayList<BaseFragment>();
		LiveLiveListFragment live = new LiveLiveListFragment();
		LiveChannelListFragment channel = new LiveChannelListFragment();
		live.setHomeFragment(this);
		channel.setHomeFragment(this);
		fragments.add(live);
		fragments.add(channel);

		mLiveHomeViewPagerAdapter = new FragmentStatePagerAdapter(
				mActivity.getSupportFragmentManager()) {

			@Override
			public int getCount() {
				return 2;
			}

			@Override
			public Fragment getItem(int arg0) {
				return fragments.get(arg0);
			}

			@Override
			public int getItemPosition(Object object) {
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

	public void playLive(CanBePlay playTarget) {
		this.mActivity
				.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		videoRootView.setVisibility(View.VISIBLE);
		liveVideoView.stopPlayback();
		liveVideoView.setVideoPath(playTarget.getStreamurl());
		this.setPlayStat(true);
	}

	public void closePlay() {
		this.mActivity
				.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		liveVideoView.stopPlayback();
		videoRootView.setVisibility(View.GONE);
	}

	private void initData() {
		liveVideoView.setUserAgent("KKApp");
	}

	@Override
	protected boolean initLocalDate() {
		return false;
	}

	@Override
	protected void saveLocalDate() {

	}

	@Override
	protected void refreshNetDate() {
	}

	@Override
	protected void loadMoreNetDate() {

	}

	@Override
	protected void onSuccessObject(JSONObject jsonObject) {

	}

	@Override
	protected void onSuccessArray(JSONArray jsonObject) {

	}

	@Override
	public void onPrepared(IMediaPlayer mp) {
		DebugLog.e("卧槽准备好了");
	}

	@Override
	public void onClick(View v) {

	}

	@Override
	public boolean onError(IMediaPlayer mp, int what, int extra) {
		DebugLog.e("卧槽有消息" + extra);
		return false;
	}

	@Override
	public void onCompletion(IMediaPlayer mp) {

	}

	@Override
	public boolean onInfo(IMediaPlayer mp, int what, int extra) {
		DebugLog.e("卧槽有消息" + extra);
		return false;
	}

	@Override
	protected void onFailure(VolleyError error) {
		DebugLog.e(error.getLocalizedMessage());

	}

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	@Override
	public void onPageSelected(int arg0) {

	}

	public boolean isPlayStat() {
		return isPlayStat;
	}

	public void setPlayStat(boolean isPlayStat) {
		this.isPlayStat = isPlayStat;
	}

}
