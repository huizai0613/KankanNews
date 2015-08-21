package com.kankan.kankanews.ui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
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
import android.view.Window;
import android.view.WindowManager;
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
import com.kankan.kankanews.bean.User_Collect_Offline;
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

public class MainActivity extends BaseVideoActivity {
	public WindowManager wm;
	private int curTab;
	private int lastTab;
	public RelativeLayout curTouchTab;

	public ShareUtil shareUtil;

	private int lastAddFrament = -1;

	public SlidingMenu side_drawer;
	private LinearLayout main_fragment_content;
	public int newsW;

	public FragmentManager fragmentManager;

	// 定义Fragment数组
	public ArrayList<BaseFragment> fragments = new ArrayList<BaseFragment>();
	private List<Fragment> addFragments;

	private int[] nomalImg = { R.drawable.tab_one_nomal,
			R.drawable.tab_two_nomal, R.drawable.tab_three_nomal,
			R.drawable.tab_four_nomal, R.drawable.tab_five_nomal };
	private int[] touchImg = { R.drawable.tab_one_touch,
			R.drawable.tab_two_touch, R.drawable.tab_three_touch,
			R.drawable.tab_four_touch, R.drawable.tab_five_touch };

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
					.get(1);
			fragment.setSelectPlay(true);
			fragment.setSelectPlayID(Integer.parseInt(mlive.getZid()));
			if (curTouchTab == tab_two) {
				refreshLive();
			} else {
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						touchTab(tab_two);
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
		Log.e("px2sp", PixelUtil.px2sp(28, this) + "");

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
					.get(1);
			fragment.setSelectPlay(true);
			fragment.setSelectPlayID(Integer.parseInt(mlive.getZid()));
			touchTab(tab_two);
		} else {
			Bundle bun = intent.getExtras();
			if (bun != null) {
				if (bun.containsKey("LIVE_ID")) {
					// 直播分享
					New_LivePlayFragment fragment = (New_LivePlayFragment) fragments
							.get(1);
					fragment.setSelectPlay(true);
					fragment.setSelectPlayID(Integer.parseInt(bun
							.getString("LIVE_ID")));
					touchTab(tab_two);
				} else {
					touchTab(tab_one); // 正常启动
				}
			} else {
				touchTab(tab_one); // 正常启动
			}
		}
	}

	long lastTime;

	@Override
	protected void onResume() {
		super.onResume();
		if (lastTime != 0) {
			if ((TimeUtil.now() - lastTime) / 60 >= 10) {
				if (curTouchTab == tab_one) {
					refreshMianItem();
				} else if (curTouchTab == tab_two) {
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
		menu_bottom_bar = (RelativeLayout) findViewById(R.id.menu_bottom_bar);
		tab_one = (RelativeLayout) findViewById(R.id.tab_one);
		tab_two = (RelativeLayout) findViewById(R.id.tab_two);
		tab_three = (RelativeLayout) findViewById(R.id.tab_three);
		tab_four = (RelativeLayout) findViewById(R.id.tab_four);
		tab_five = (RelativeLayout) findViewById(R.id.tab_five);

		nightView = findViewById(R.id.night_view);

		// 初始化fragments
		New_HomeFragment mainFragment = new New_HomeFragment();
		New_LivePlayFragment liveFragment = new New_LivePlayFragment();
		New_ColumsFragment columFragment = new New_ColumsFragment();
		New_MyFragment setFragment = new New_MyFragment();
		New_RevelationsFragment reveFragment = new New_RevelationsFragment();
		fragments.add(mainFragment);
		fragments.add(liveFragment);
		fragments.add(columFragment);
		fragments.add(setFragment);
		fragments.add(reveFragment);
	}

	@Override
	protected void initData() {
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

	int i;
	long front;
	long later;

	public void touchTab(View v) {
		int id = v.getId();
		if (id == R.id.tab_one && curTouchTab == tab_one) {
			refreshMianItem();
		} else if (id == R.id.tab_two && curTouchTab == tab_two) {
			refreshLive();
		} else if (id == R.id.tab_three && curTouchTab == tab_three) {
			refreshColum();
		}
		changeTab(id);
		changeFragment(id);
	}

	public void changeTab(int id) {

		setTabStyle(tab_one, 0, false);
		setTabStyle(tab_two, 1, false);
		setTabStyle(tab_three, 2, false);
		setTabStyle(tab_four, 3, false);
		setTabStyle(tab_five, 4, false);

		switch (id) {
		case R.id.tab_one:
			curTouchTab = tab_one;
			setTabStyle(tab_one, 0, true);
			break;
		case R.id.tab_two:
			curTouchTab = tab_two;
			setTabStyle(tab_two, 1, true);
			break;
		case R.id.tab_three:
			curTouchTab = tab_three;
			setTabStyle(tab_three, 2, true);
			break;
		case R.id.tab_four:
			curTouchTab = tab_four;
			setTabStyle(tab_four, 3, true);
			break;
		case R.id.tab_five:
			curTouchTab = tab_five;
			setTabStyle(tab_five, 4, true);
			break;
		}
	}

	private void changeFragment(int id) {
		lastTab = curTab;
		switch (id) {
		case R.id.tab_one:
			curTab = 0;
			break;
		case R.id.tab_two:
			curTab = 1;
			break;
		case R.id.tab_three:
			curTab = 2;
			break;
		case R.id.tab_four:
			curTab = 3;
			break;
		case R.id.tab_five:
			curTab = 4;
			break;
		}

		New_LivePlayFragment fragment = (New_LivePlayFragment) fragments.get(1);
		if (curTab != 1) {
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
		int size = fragments.size();

		if (addFragments == null) {
			addFragments = fragmentManager.getFragments();
		}

		for (int i = 0; i < size; i++) {
			if (i == curTab) {
				continue;
			}
			if (fragments.get(i).isResumed()) {
				beginTransaction.hide(fragments.get(i));
				fragments.get(i).onPause();
			}
		}
		if (addFragments != null) {
			// boolean contains = fragments.get(curTab).isAdded();
			boolean contains = addFragments.contains(fragments.get(curTab));
			if (contains) {
				beginTransaction.show(fragments.get(curTab));
				if (fragments.get(curTab).isResumed()) {
					fragments.get(curTab).onResume();
				}
			} else {
				if (curTab != lastAddFrament) {
					beginTransaction.add(R.id.main_fragment_content,
							fragments.get(curTab));
					lastAddFrament = curTab;
				}
				beginTransaction.show(fragments.get(curTab));
				if (fragments.get(curTab).isResumed()) {
					fragments.get(curTab).onResume();
				}
			}
		} else {
			if (curTab != lastAddFrament) {
				beginTransaction.add(R.id.main_fragment_content,
						fragments.get(curTab));
				lastAddFrament = curTab;
			}
			beginTransaction.show(fragments.get(curTab));
			if (fragments.get(curTab).isResumed()) {
				fragments.get(curTab).onResume();
			}
		}
		beginTransaction.commit();
	}

	private void setTabStyle(RelativeLayout view, int positon, boolean isTouch) {

		ImageView tab_img = (ImageView) view.findViewById(R.id.tab_img);

		if (isTouch) {
			tab_img.setImageResource(touchImg[positon]);
		} else {
			tab_img.setImageResource(nomalImg[positon]);
		}
	}

	public SlidingMenu getSide_drawer() {
		return side_drawer;
	}

	@Override
	public void onBackPressed() {
		if (curTab == 1) {
			if (((New_LivePlayFragment) fragments.get(1)).isFullstate()) {
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
				((New_LivePlayFragment) fragments.get(1)).orientationHandler
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
					.get(4);
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
					Set<Entry<String, HttpHandler>> entrySetHandler = mApplication.mHttpHandlereds
							.entrySet();

					for (Entry<String, HttpHandler> e : entrySetHandler) {
						mApplication.mUser_Collect_Offlines.get(e.getKey())
								.setType(User_Collect_Offline.DOWNLOADSTOP);

						e.getValue().cancel();
					}

					Set<Entry<String, MyRequestCallBack>> entrySetCallBack = mApplication.mRequestCallBackPauses
							.entrySet();

					for (Entry<String, MyRequestCallBack> e : entrySetCallBack) {
						mApplication.mUser_Collect_Offlines.get(e.getKey())
								.setType(User_Collect_Offline.DOWNLOADSTOP);
					}
					mApplication.mRequestCallBackPauses.clear();

				}
			}
		}
	};
	private PushAgent mPushAgent;
	private RelativeLayout menu_bottom_bar;
	private RelativeLayout tab_one;
	public RelativeLayout tab_two;
	private RelativeLayout tab_three;
	private RelativeLayout tab_four;
	private RelativeLayout tab_five;

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

		New_HomeFragment fragment = (New_HomeFragment) fragments.get(0);
		if (fragment.fragments != null
				&& fragment.fragments.get(fragment.columnSelectIndex) != null) {

			fragment.fragments.get(fragment.columnSelectIndex).refresh();
		}
	}

	// 刷新直播
	private void refreshLive() {
		New_LivePlayFragment fragment = (New_LivePlayFragment) fragments.get(1);
		fragment.refresh();
	}

	// 刷新栏目
	private void refreshColum() {
		New_ColumsFragment fragment = (New_ColumsFragment) fragments.get(2);
		fragment.refresh();
	}

	@Override
	public void shareReBack() {
		// TODO Auto-generated method stub
		super.shareReBack();
		New_LivePlayFragment fragment = (New_LivePlayFragment) fragments.get(1);
		fragment.isFirst = false;
		if (this.curTab == 1)
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
	}

	public void closeClick() {
		// TODO Auto-generated method stub
		tab_one.setClickable(false);
		tab_two.setClickable(false);
		tab_three.setClickable(false);
		tab_four.setClickable(false);
		tab_five.setClickable(false);
	}

	public void openClick() {
		// TODO Auto-generated method stub
		tab_one.setClickable(true);
		tab_two.setClickable(true);
		tab_three.setClickable(true);
		tab_four.setClickable(true);
		tab_five.setClickable(true);
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
}
