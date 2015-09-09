package com.kankan.kankanews.ui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.android.volley.VolleyError;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.kankan.kankanews.base.BaseActivity;
import com.kankan.kankanews.base.BaseFragment;
import com.kankan.kankanews.base.BaseVideoActivity;
import com.kankan.kankanews.base.download.MyRequestCallBack;
import com.kankan.kankanews.bean.MyCollect;
import com.kankan.kankanews.bean.New_LivePlay;
import com.kankan.kankanews.config.AndroidConfig;
import com.kankan.kankanews.ui.fragment.New_ColumsFragment;
import com.kankan.kankanews.ui.fragment.New_HomeFragment;
import com.kankan.kankanews.ui.fragment.New_LivePlayFragment;
import com.kankan.kankanews.ui.fragment.New_MyFragment;
import com.kankan.kankanews.ui.fragment.New_RevelationsFragment;
import com.kankan.kankanews.ui.view.MyTextView;
import com.kankan.kankanews.utils.CommonUtils;
import com.kankan.kankanews.utils.DebugLog;
import com.kankan.kankanews.utils.Options;
import com.kankan.kankanews.utils.PixelUtil;
import com.kankan.kankanews.utils.ShareUtil;
import com.kankan.kankanews.utils.TimeUtil;
import com.kankanews.kankanxinwen.R;
import com.lidroid.xutils.http.HttpHandler;
import com.networkbench.agent.impl.NBSAppAgent;
import com.umeng.message.PushAgent;
import com.umeng.socialize.sso.UMSsoHandler;
import com.umeng.update.UmengUpdateAgent;

public class MainActivity extends BaseVideoActivity implements OnClickListener {
	public WindowManager wm;

	private long lastTime;

	private LinearLayout menu_bottom_bar;
	private RelativeLayout tabHome;
	public RelativeLayout tabLive;
	private RelativeLayout tabRevelate;
	public RelativeLayout curTouchTab;

	public RelativeLayout lastAddFrament;

	private ImageView screenGuide;

	public ShareUtil shareUtil;

	public SlidingMenu side_drawer;
	private LinearLayout main_fragment_content;
	public int newsW;

	public FragmentManager fragmentManager;

	// 定义Fragment数组
	public Map<RelativeLayout, BaseFragment> fragments = new HashMap<RelativeLayout, BaseFragment>();
	private List<Fragment> addFragments;

	private List<RelativeLayout> tabArray = new ArrayList<RelativeLayout>();;
	private int[] nomalImg = { R.drawable.tab_home_nomal,
			R.drawable.tab_revelate_nomal, R.drawable.tab_live_nomal };
	private int[] touchImg = { R.drawable.tab_home_touch,
			R.drawable.tab_revelate_touch, R.drawable.tab_live_touch };

	@Override
	protected void onSaveInstanceState(Bundle outState) {

	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		if (spUtil.getIsDayMode())
			chage2Day();
		else
			chage2Night();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		Serializable serializableExtra = intent.getSerializableExtra("LIVE");
		if (serializableExtra != null) {
			New_LivePlay mlive = (New_LivePlay) serializableExtra;
			New_LivePlayFragment fragment = (New_LivePlayFragment) fragments
					.get(tabLive);
			fragment.setSelectPlay(true);
			fragment.setSelectPlayID(Integer.parseInt(mlive.getZid()));
			if (curTouchTab == tabLive) {
				refreshLive();
			} else {
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						touchTab(tabLive);
					}
				}, 500);

			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		spUtil.setFristComing(false);
		this.mApplication.setMainActivity(this);

		wm = (WindowManager) getApplicationContext().getSystemService(
				Context.WINDOW_SERVICE);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		// 自动更新提示
		UmengUpdateAgent.setUpdateOnlyWifi(false);
		UmengUpdateAgent.update(this);
		Log.e("mScreenWidth", mScreenWidth + "");
		Log.e("mScreenHeight", mScreenHeight + "");

		newsW = (mScreenWidth - PixelUtil.dp2px(15)) / 2;

		Intent intent = getIntent();
		Serializable serializableExtra = intent.getSerializableExtra("LIVE");
		if (serializableExtra != null) {
			New_LivePlay mlive = (New_LivePlay) serializableExtra;
			New_LivePlayFragment fragment = (New_LivePlayFragment) fragments
					.get(tabLive);
			fragment.setSelectPlay(true);
			fragment.setSelectPlayID(Integer.parseInt(mlive.getZid()));
			touchTab(tabLive);
		} else {
			Bundle bun = intent.getExtras();
			if (bun != null) {
				if (bun.containsKey("LIVE_ID")) {
					// 直播分享
					New_LivePlayFragment fragment = (New_LivePlayFragment) fragments
							.get(tabLive);
					fragment.setSelectPlay(true);
					fragment.setSelectPlayID(Integer.parseInt(bun
							.getString("LIVE_ID")));
					touchTab(tabLive);
				} else {
					touchTab(tabHome); // 正常启动
				}
			} else {
				touchTab(tabHome); // 正常启动
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (lastTime != 0) {
			if ((TimeUtil.now() - lastTime) / 60 >= 10) {
				if (curTouchTab == tabHome) {
					refreshMianItem();
				} else if (curTouchTab == tabLive) {
					refreshLive();
				}
			}
			lastTime = TimeUtil.now();
		} else {
			lastTime = TimeUtil.now();
		}

	}

	@Override
	protected void initView() {
		setRightFinsh(false);
		// initSlidingMenu();
		fragmentManager = getSupportFragmentManager();

		main_fragment_content = (LinearLayout) findViewById(R.id.main_fragment_content);
		menu_bottom_bar = (LinearLayout) findViewById(R.id.menu_bottom_bar);
		tabHome = (RelativeLayout) findViewById(R.id.tab_home);
		tabLive = (RelativeLayout) findViewById(R.id.tab_live);
		tabRevelate = (RelativeLayout) findViewById(R.id.tab_revelate);
		tabArray.add(tabHome);
		tabArray.add(tabRevelate);
		tabArray.add(tabLive);

		screenGuide = (ImageView) findViewById(R.id.full_screen_guide);
		nightView = findViewById(R.id.night_view);

		// 初始化fragments
		New_HomeFragment mainFragment = new New_HomeFragment();
		New_LivePlayFragment liveFragment = new New_LivePlayFragment();
		// New_ColumsFragment columFragment = new New_ColumsFragment();
		// New_MyFragment setFragment = new New_MyFragment();
		New_RevelationsFragment reveFragment = new New_RevelationsFragment();
		fragments.put(tabHome, mainFragment);
		fragments.put(tabRevelate, reveFragment);
		fragments.put(tabLive, liveFragment);
		// fragments.add(columFragment);
		// fragments.add(setFragment);
	}

	@Override
	protected void initData() {
	}

	@Override
	protected void setListener() {
		// TODO Auto-generated method stub
		screenGuide.setOnClickListener(this);
	}

	@Override
	protected void onSuccess(JSONObject jsonObject) {
		// TODO Auto-generated method stub
	}

	@Override
	protected void onFailure(VolleyError error) {
		// TODO Auto-generated method stub
	}

	public void touchTab(View v) {
		int id = v.getId();
		if (id == R.id.tab_home && curTouchTab == tabHome) {
			refreshMianItem();
		} else if (id == R.id.tab_live && curTouchTab == tabLive) {
			refreshLive();
		} else if (id == R.id.tab_revelate && curTouchTab == tabRevelate) {
			Intent intent = new Intent(this, RevelationsActivity.class);
			this.startActivity(intent);
		}
		if (id == R.id.tab_home) {
			if (spUtil.getFirstGetColumns()) {
				screenGuide
						.setBackgroundResource(R.drawable.screen_guide_columns);
				screenGuide.setVisibility(View.VISIBLE);
				screenGuide.setTag("COLUMNS");
			}
		}
		if (id == R.id.tab_revelate) {
			if (spUtil.getFirstGetRevalations()) {
				screenGuide
						.setBackgroundResource(R.drawable.screen_guide_revelations);
				screenGuide.setVisibility(View.VISIBLE);
				screenGuide.setTag("REVALATIONS");
			}
		}
		changeTab(id);
		changeFragment(id);
	}

	public void changeTab(int id) {
		switch (id) {
		case R.id.tab_home:
			curTouchTab = tabHome;
			break;
		case R.id.tab_live:
			curTouchTab = tabLive;
			break;
		case R.id.tab_revelate:
			curTouchTab = tabRevelate;
			break;
		}
		setTabStyle();
	}

	private void changeFragment(int id) {
		New_LivePlayFragment fragment = (New_LivePlayFragment) fragments
				.get(tabLive);
		if (curTouchTab != tabLive) {
			if (fragment.getVideoView() != null) {
				fragment.getVideoView().stopPlayback();
			}
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			fragment.isFirst = false;
		} else {
			if (fragment.isResumed()) {
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
			}
		}

		FragmentTransaction beginTransaction = fragmentManager
				.beginTransaction();
		int size = tabArray.size();

		if (addFragments == null) {
			addFragments = fragmentManager.getFragments();
		}

		for (RelativeLayout tab : tabArray) {
			if (tab == curTouchTab) {
				continue;
			}
			if (fragments.get(tab).isResumed()) {
				beginTransaction.hide(fragments.get(tab));
				fragments.get(tab).onPause();
			}
		}
		if (addFragments != null) {
			// boolean contains = fragments.get(curTab).isAdded();
			boolean contains = addFragments
					.contains(fragments.get(curTouchTab));
			if (contains) {
				beginTransaction.show(fragments.get(curTouchTab));
				if (fragments.get(curTouchTab).isResumed()) {
					fragments.get(curTouchTab).onResume();
				}
			} else {
				if (curTouchTab != lastAddFrament) {
					beginTransaction.add(R.id.main_fragment_content,
							fragments.get(curTouchTab));
					lastAddFrament = curTouchTab;
				}
				beginTransaction.show(fragments.get(curTouchTab));
				if (fragments.get(curTouchTab).isResumed()) {
					fragments.get(curTouchTab).onResume();
				}
			}
		} else {
			if (curTouchTab != lastAddFrament) {
				beginTransaction.add(R.id.main_fragment_content,
						fragments.get(curTouchTab));
				lastAddFrament = curTouchTab;
			}
			beginTransaction.show(fragments.get(curTouchTab));
			if (fragments.get(curTouchTab).isResumed()) {
				fragments.get(curTouchTab).onResume();
			}
		}
		beginTransaction.commit();
	}

	private void setTabStyle() {
		for (int i = 0; i < tabArray.size(); i++) {
			RelativeLayout tabView = tabArray.get(i);
			ImageView tab_img = (ImageView) tabView.findViewById(R.id.tab_img);
			if (tabView == curTouchTab) {
				tab_img.setImageResource(touchImg[i]);
				if (tabView == tabRevelate) {
					RelativeLayout.LayoutParams imgParams = new RelativeLayout.LayoutParams(
							RelativeLayout.LayoutParams.MATCH_PARENT,
							RelativeLayout.LayoutParams.MATCH_PARENT);
					imgParams.setMargins(5, 5, 5, 5);
					tab_img.setLayoutParams(imgParams);
					float realHeight = getResources().getDimension(
							R.dimen.bottom_bar_real_height);
					ViewGroup.LayoutParams params = tabView.getLayoutParams();
					params.height = (int) realHeight;
					tabView.setLayoutParams(params);
					tabView.setBackgroundResource(R.drawable.tab_home_middle_item_border_selected);
					// final TranslateAnimation anim = new TranslateAnimation(
					// tabView.getX(), tabView.getX() + 10,
					// tabView.getY(), tabView.getY());
					// anim.setInterpolator(new CycleInterpolator(2f));
					// anim.setDuration( 300 );
					// tabView.setAnimation(anim);
					// TranslateAnimation shakeAnim = (TranslateAnimation)
					// AnimationUtils
					// .loadAnimation(this, R.anim.shake_y);
					// shakeAnim.setRepeatCount(Integer.MAX_VALUE);
					// tabView.startAnimation(shakeAnim);
				} else {
					tabView.setBackgroundResource(R.drawable.tab_home_left_right_item_border_selected);
				}
			} else {
				tab_img.setImageResource(nomalImg[i]);
				if (tabView == tabRevelate) {
					RelativeLayout.LayoutParams imgParams = new RelativeLayout.LayoutParams(
							RelativeLayout.LayoutParams.MATCH_PARENT,
							RelativeLayout.LayoutParams.MATCH_PARENT);
					imgParams.setMargins(PixelUtil.dp2px(10),
							PixelUtil.dp2px(10), PixelUtil.dp2px(10),
							PixelUtil.dp2px(10));
					tab_img.setLayoutParams(imgParams);
					float realHeight = getResources().getDimension(
							R.dimen.base_action_bar_height);
					ViewGroup.LayoutParams params = tabView.getLayoutParams();
					params.height = (int) realHeight;
					tabView.setLayoutParams(params);
					tabView.setBackgroundResource(R.drawable.tab_home_left_right_item_border);
				} else {
					tabView.setBackgroundResource(R.drawable.tab_home_left_right_item_border);
				}
			}
		}
	}

	public SlidingMenu getSide_drawer() {
		return side_drawer;
	}

	@Override
	public void onBackPressed() {
		if (curTouchTab == tabLive) {
			if (((New_LivePlayFragment) fragments.get(tabLive)).isFullstate()) {
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
				((New_LivePlayFragment) fragments.get(tabLive)).orientationHandler
						.sendEmptyMessageDelayed(0, 1000);
				return;
			}
		}

		mApplication.shutDown();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == AndroidConfig.REVELATIONS_FRAGMENT_RESULT_CANCEL
				|| resultCode == AndroidConfig.REVELATIONS_FRAGMENT_RESULT_OK) {
			New_RevelationsFragment fragment = (New_RevelationsFragment) fragments
					.get(tabRevelate);
			fragment.onActivityResult(requestCode, resultCode, data);
		}
		if (this.shareUtil != null) {
			UMSsoHandler ssoHandler = this.shareUtil.getmController()
					.getConfig().getSsoHandler(requestCode);
			if (ssoHandler != null) {
				ssoHandler.authorizeCallBack(requestCode, resultCode, data);
			}
		}
	}

	private BroadcastReceiver mHomeKeyEventReceiver = new BroadcastReceiver() {
		String SYSTEM_REASON = "reason";
		String SYSTEM_HOME_KEY = "homekey";
		String SYSTEM_HOME_KEY_LONG = "recentapps";

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
				String reason = intent.getStringExtra(SYSTEM_REASON);
				if (TextUtils.equals(reason, SYSTEM_HOME_KEY)) {
					// 表示按了home键,程序到了后台

				}
			}
		}
	};

	public void bottomBarVisible(int visibility) {
		// RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
		// LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		// if (visibility == View.VISIBLE) {
		// params.bottomMargin = PixelUtil.dp2px(this.getResources()
		// .getDimension(R.dimen.base_action_bar_height));
		// } else {
		// params.bottomMargin = 0;
		//
		// }
		// main_fragment_content.setLayoutParams(params);
		menu_bottom_bar.setVisibility(visibility);

	}

	// 刷新首页当前子页面
	private void refreshMianItem() {
		New_HomeFragment fragment = (New_HomeFragment) fragments.get(tabHome);
		if (fragment.fragments != null
				&& fragment.fragments.get(fragment.columnSelectIndex) != null) {
			fragment.fragments.get(fragment.columnSelectIndex).refresh();
		}
	}

	// 刷新直播
	private void refreshLive() {
		New_LivePlayFragment fragment = (New_LivePlayFragment) fragments
				.get(tabLive);
		fragment.refresh();
	}

	@Override
	public void shareReBack() {
		// TODO Auto-generated method stub
		super.shareReBack();
		New_LivePlayFragment fragment = (New_LivePlayFragment) fragments
				.get(tabLive);
		fragment.isFirst = false;
		if (this.curTouchTab == tabLive)
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
	}

	public void closeClick() {
		// TODO Auto-generated method stub
		tabHome.setClickable(false);
		tabLive.setClickable(false);
		tabRevelate.setClickable(false);
	}

	public void openClick() {
		// TODO Auto-generated method stub
		tabHome.setClickable(true);
		tabLive.setClickable(true);
		tabRevelate.setClickable(true);
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		super.finishNoRemove();
	}

	@Override
	public void netChanged() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.full_screen_guide:
			screenGuide.setVisibility(View.GONE);
			String tag = (String) screenGuide.getTag();
			if (tag.equals("COLUMNS"))
				spUtil.setFirstGetColumns(false);
			else
				spUtil.setFirstGetRevalations(false);
			break;

		default:
			break;
		}
	}

	public New_LivePlayFragment getLiveFragment() {
		return (New_LivePlayFragment) this.fragments.get(tabLive);
	}
}
