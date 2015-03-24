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
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.RelativeLayout.LayoutParams;

import com.android.volley.VolleyError;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.kankan.kankanews.base.BaseActivity;
import com.kankan.kankanews.base.download.MyRequestCallBack;
import com.kankan.kankanews.bean.MyCollect;
import com.kankan.kankanews.bean.New_LivePlay;
import com.kankan.kankanews.bean.User_Collect_Offline;
import com.kankan.kankanews.config.AndroidConfig;
import com.kankan.kankanews.ui.fragment.New_ColumsFragment;
import com.kankan.kankanews.ui.fragment.New_HomeFragment;
import com.kankan.kankanews.ui.fragment.New_LivePlayFragment;
import com.kankan.kankanews.ui.fragment.New_MyFragment;
import com.kankan.kankanews.ui.view.MyTextView;
import com.kankan.kankanews.utils.Options;
import com.kankan.kankanews.utils.PixelUtil;
import com.kankan.kankanews.utils.TimeUtil;
import com.kankan.kankanews.utils.ToastUtils;
import com.kankanews.kankanxinwen.R;
import com.lidroid.xutils.http.HttpHandler;
import com.umeng.message.PushAgent;
import com.umeng.update.UmengUpdateAgent;

public class MainActivity extends BaseActivity {
	public WindowManager wm;
	private int curTab;
	private int lastTab;
	public RelativeLayout curTouchTab;

	private ImageView menu_user_img;
	private MyTextView menu_user_name;

	public SlidingMenu side_drawer;
	private LinearLayout main_fragment_content;
	public int newsW;
	public int topNewW;

	public FragmentManager fragmentManager;
	// 记录用户是否登录
	private boolean isloaduser = false;

	// 获取我的收藏的数组
	private ArrayList<MyCollect> myCollects;

	// 定义Fragment数组
	public ArrayList<Fragment> fragments = new ArrayList<Fragment>();
	private List<Fragment> addFragments;

	private int[] nomalImg = { R.drawable.tab_one_nomal,
			R.drawable.tab_two_nomal, R.drawable.tab_three_nomal,
			R.drawable.tab_four_nomal };
	private int[] touchImg = { R.drawable.tab_one_touch,
			R.drawable.tab_two_touch, R.drawable.tab_three_touch,
			R.drawable.tab_four_touch };

	// public DrawerView drawerView;

	@Override
	protected void onSaveInstanceState(Bundle outState) {

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
		wm = (WindowManager) getApplicationContext().getSystemService(
				Context.WINDOW_SERVICE);
		setContentView(R.layout.activity_main);
		// 自动更新提示
		UmengUpdateAgent.setUpdateOnlyWifi(false);
		UmengUpdateAgent.update(this);
		// tongji
		// initAnalytics("");

		newsW = (mScreenWidth - PixelUtil.dp2px(15)) / 2;
		topNewW = mScreenWidth;
		// 注册广播
		// registerReceiver(mHomeKeyEventReceiver, new IntentFilter(
		// Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
	}

	long lastTime;

	@Override
	protected void onResume() {
		super.onResume();
		mApplication.checkLogin();
		// if (!isloaduser && mApplication.isLogin) {
		// imageLoader.displayImage(mApplication.getUser().getUser_poster(),
		// menu_user_img, Options.getBigImageOptions(null));
		// menu_user_name.setText(mApplication.getUser().getUser_name());
		// isloaduser = true;
		// findViewById(R.id.menu_set).setVisibility(View.GONE);
		// findViewById(R.id.menu_offline).setVisibility(View.GONE);
		// }

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
		menu_user_img = (ImageView) findViewById(R.id.menu_user_img);
		menu_user_name = (MyTextView) findViewById(R.id.menu_user_name);

		main_fragment_content = (LinearLayout) findViewById(R.id.main_fragment_content);
		menu_bottom_bar = (RelativeLayout) findViewById(R.id.menu_bottom_bar);
		tab_one = (RelativeLayout) findViewById(R.id.tab_one);
		tab_two = (RelativeLayout) findViewById(R.id.tab_two);
		tab_three = (RelativeLayout) findViewById(R.id.tab_three);
		tab_four = (RelativeLayout) findViewById(R.id.tab_four);

		// 初始化fragments
		New_HomeFragment mainFragment = new New_HomeFragment();
		New_LivePlayFragment liveFragment = new New_LivePlayFragment();
		New_ColumsFragment columFragment = new New_ColumsFragment();
		New_MyFragment setFragment = new New_MyFragment();
		fragments.add(mainFragment);
		fragments.add(liveFragment);
		fragments.add(columFragment);
		fragments.add(setFragment);

		Intent intent = getIntent();
		Serializable serializableExtra = intent.getSerializableExtra("LIVE");
		if (serializableExtra != null ) {
			New_LivePlay mlive = (New_LivePlay) serializableExtra;
			New_LivePlayFragment fragment = (New_LivePlayFragment) fragments
					.get(1);
			fragment.setSelectPlay(true);
			fragment.setSelectPlayID(Integer.parseInt(mlive.getZid()));
			touchTab(tab_two);
		} else {
			touchTab(tab_one);
		}
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

	// protected void initSlidingMenu() {
	// drawerView = new DrawerView(this);
	// side_drawer = new DrawerView(this).initSlidingMenu();
	//
	// }
	//
	int i;
	long front;
	long later;

	public void touchDoubleDown() {
		// i++;
		// if (i < 2) {
		// front = System.currentTimeMillis();
		// return;
		// }
		// if (i >= 2) {
		// later = System.currentTimeMillis();
		// if (later - front > 500) {
		// front = System.currentTimeMillis();
		// i = 1;
		// } else {
		refreshMianItem();
		// i = 0;
		// }
		// }
	}

	public void touchTab(View v) {
		int id = v.getId();
		if (id == R.id.tab_one && curTouchTab == tab_one) {
			touchDoubleDown();
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

		switch (id) {
		case R.id.tab_one:
			// error_bg.setVisibility(View.GONE);
			curTouchTab = tab_one;
			setTabStyle(tab_one, 0, true);
			break;
		case R.id.tab_two:
			// error_bg.setVisibility(View.GONE);
			curTouchTab = tab_two;
			setTabStyle(tab_two, 1, true);
			break;
		case R.id.tab_three:
			// if (mApplication.getUser().getType() == 0) {
			// msg_tv.setText("请与办公室联系，加入办公室即可跟踪运单状态.");
			// error_bg.setVisibility(View.VISIBLE);
			// } else {
			// error_bg.setVisibility(View.GONE);
			// }
			curTouchTab = tab_three;
			setTabStyle(tab_three, 2, true);
			break;
		case R.id.tab_four:
			// error_bg.setVisibility(View.GONE);
			curTouchTab = tab_four;
			setTabStyle(tab_four, 3, true);
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
		}
		New_LivePlayFragment fragment = (New_LivePlayFragment) fragments.get(1);
		if (curTab != 1) {
			if (fragment.getVideoView() != null) {
				fragment.getVideoView().release(true);
			}
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
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
			boolean contains = addFragments.contains(fragments.get(curTab));
			if (contains) {
				beginTransaction.show(fragments.get(curTab));
				if (fragments.get(curTab).isResumed()) {
					fragments.get(curTab).onResume();
				}
			} else {
				beginTransaction.add(R.id.main_fragment_content,
						fragments.get(curTab));
				beginTransaction.show(fragments.get(curTab));
				if (fragments.get(curTab).isResumed()) {
					fragments.get(curTab).onResume();
				}
			}
		} else {
			beginTransaction.add(R.id.main_fragment_content,
					fragments.get(curTab));
			beginTransaction.show(fragments.get(curTab));
			if (fragments.get(curTab).isResumed()) {
				fragments.get(curTab).onResume();
			}
		}

		beginTransaction.commit();

	}

	private void setTabStyle(RelativeLayout view, int positon, boolean isTouch) {

		// TextView tab_num = (TextView) view.findViewById(R.id.tab_num);
		ImageView tab_img = (ImageView) view.findViewById(R.id.tab_img);

		if (isTouch) {
			// tab_num.setVisibility(View.GONE);
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
				return;
			}
		}

		mApplication.shutDown();
	}

	// @Override
	// protected void onResume() {
	// super.onResume();
	// if(mApplication.getUser()!=null){
	// side_drawer.showMenu();
	// imageLoader.displayImage(mApplication.getUser().getProfile_image_url(),menu_user_img,Options.getListOptions());
	// menu_user_name.setText(mApplication.getUser().getScreen_name());
	// }
	// }

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == AndroidConfig.Drawer_login_resultCode) {
			boolean isuser = (Boolean) data.getSerializableExtra("User");
			if (isuser) {
				side_drawer.showMenu();
				imageLoader.displayImage(mApplication.getUser()
						.getUser_poster(), menu_user_img, Options
						.getBigImageOptions(null));
				menu_user_name.setText(mApplication.getUser().getUser_name());
				isloaduser = true;
			}
		}

		if (resultCode == AndroidConfig.Set_resultCode) {
			boolean isuser = (Boolean) data.getSerializableExtra("User");
			if (!isuser) {
				menu_user_img.setImageResource(R.drawable.icon_menu_login);
				menu_user_name.setText("登录");
				isloaduser = false;
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

	public void bottomBarVisible(int visibility) {
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		if (visibility == View.VISIBLE) {
			params.bottomMargin = PixelUtil.dp2px(54);
		} else {
			params.bottomMargin = 0;

		}
		main_fragment_content.setLayoutParams(params);
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

}
