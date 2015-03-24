package com.kankan.kankanews.ui.view;

import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.OnClosedListener;
import com.kankan.kankanews.base.IA.CrashApplication;
import com.kankan.kankanews.config.AndroidConfig;
import com.kankan.kankanews.ui.MainActivity;
import com.kankan.kankanews.ui.item.Activity_Login;
import com.kankan.kankanews.ui.item.Activity_My_Attention;
import com.kankan.kankanews.ui.item.Activity_My_Collect;
import com.kankan.kankanews.ui.item.Activity_My_Comment;
import com.kankan.kankanews.ui.item.Activity_My_Foot;
import com.kankan.kankanews.ui.item.Activity_OffLine;
import com.kankan.kankanews.ui.item.Activity_Set;
import com.kankanews.kankanxinwen.R;

/**
 * 自定义SlidingMenu 测拉菜单类
 * */
public class DrawerView extends RelativeLayout implements OnClickListener {

	private MainActivity activity;
	SlidingMenu localSlidingMenu;

	private LinearLayout menu_login;
	private LinearLayout menu_set;

	private LinearLayout menu_new;
	private LinearLayout menu_hot;
	private LinearLayout menu_mycollect;
	private LinearLayout menu_myfoot;
	private LinearLayout menu_myAttention;
	private LinearLayout menu_mycomment;
	private LinearLayout menu_item_offline;
	private LinearLayout menu_item_set;

	public DrawerView(MainActivity activity) {
		super(activity);
		this.activity = activity;
	}

	public void setOnClosedListener(OnClosedListener onClosedListener) {
		if (localSlidingMenu != null) {
			localSlidingMenu.setOnClosedListener(onClosedListener);
		}
	}

	public SlidingMenu initSlidingMenu() {
		localSlidingMenu = new SlidingMenu(activity);
		localSlidingMenu.setMenu(R.layout.left_drawer_fragment);// 设置menu的布局文件
		localSlidingMenu.setMode(SlidingMenu.LEFT);// 设置左滑菜单
		localSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);// 设置要使菜单滑动，触碰屏幕的范围
		// localSlidingMenu.setTouchModeBehind(SlidingMenu.RIGHT);
		localSlidingMenu.setShadowWidthRes(R.dimen.shadow_width);// 设置阴影图片的宽度
		localSlidingMenu.setShadowDrawable(R.drawable.shadow);// 设置阴影图片
		localSlidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);// SlidingMenu划出时主页面显示的剩余宽度
		localSlidingMenu.setFadeDegree(0.35F);// SlidingMenu滑动时的渐变程度
		localSlidingMenu.attachToActivity(activity, SlidingMenu.LEFT);// 使SlidingMenu附加在Activity左边
		// localSlidingMenu.setBehindWidthRes(R.dimen.left_drawer_avatar_size);//设置SlidingMenu菜单的宽度
		// localSlidingMenu.toggle();//动态判断自动关闭或开启SlidingMenu
		// localSlidingMenu.setSecondaryMenu(R.layout.profile_drawer_right);
		localSlidingMenu.setSecondaryShadowDrawable(R.drawable.shadowright);
		localSlidingMenu
				.setOnOpenedListener(new SlidingMenu.OnOpenedListener() {

					@Override
					public void onOpened() {
						// TODO Auto-generated method stub
					}
				});

		initView();
		return localSlidingMenu;
	}

	private void initView() {

		menu_set = (LinearLayout) activity.findViewById(R.id.menu_set);
		menu_login = (LinearLayout) activity.findViewById(R.id.menu_login);

		menu_new = (LinearLayout) activity.findViewById(R.id.menu_new);
		menu_hot = (LinearLayout) activity.findViewById(R.id.menu_hot);
		menu_myfoot = (LinearLayout) activity.findViewById(R.id.menu_myfoot);
		menu_myAttention = (LinearLayout) activity
				.findViewById(R.id.menu_myAttention);
		menu_mycollect = (LinearLayout) activity
				.findViewById(R.id.menu_mycollect);
		menu_mycomment = (LinearLayout) activity
				.findViewById(R.id.menu_mycomment);
		menu_item_offline = (LinearLayout) activity
				.findViewById(R.id.menu_item_offline);

		menu_new.setBackgroundColor(Color.parseColor("#22ffffff"));
		menu_hot.setBackgroundResource(0);

		menu_item_set = (LinearLayout) activity
				.findViewById(R.id.menu_item_set);

		menu_set.setOnClickListener(this);
		menu_login.setOnClickListener(this);

		menu_new.setOnClickListener(this);
		menu_hot.setOnClickListener(this);
		menu_myAttention.setOnClickListener(this);
		menu_mycollect.setOnClickListener(this);
		menu_myfoot.setOnClickListener(this);
		menu_mycomment.setOnClickListener(this);
		menu_item_set.setOnClickListener(this);
		menu_item_offline.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.menu_login:
			if (CrashApplication.getInstance().getUser() == null) {
				Intent intent_login = new Intent(activity, Activity_Login.class);
				activity.startActivityForResult(intent_login,
						AndroidConfig.Drawer_login_requestCode);
				activity.overridePendingTransition(R.anim.in_from_right,
						R.anim.out_to_left);
			} else {
				// TODO
			}
			break;
		case R.id.menu_new:
//			activity.changeFragment(0);
			localSlidingMenu.showContent();
			menu_new.setBackgroundColor(Color.parseColor("#22ffffff"));
			menu_hot.setBackgroundResource(0);
			break;

		case R.id.menu_hot:
//			activity.changeFragment(1);
			localSlidingMenu.showContent();
			menu_hot.setBackgroundColor(Color.parseColor("#22ffffff"));
			menu_new.setBackgroundResource(0);
			break;

		case R.id.menu_mycollect://收藏
			// activity.checkUser(Activity_My_Collect.class);
			activity.startAnimActivity(Activity_My_Collect.class);
			break;

		case R.id.menu_myfoot://看过
			activity.startAnimActivity(Activity_My_Foot.class);
			break;
		case R.id.menu_myAttention: //关注
			activity.startAnimActivity(Activity_My_Attention.class);
			break;
		case R.id.menu_mycomment:
			// Intent intent = new Intent(activity, Activity_Set.class);
			activity.checkUser(Activity_My_Comment.class);
			break;
		case R.id.menu_item_offline:// 离线
			activity.startAnimActivity(Activity_OffLine.class);
			break;

		case R.id.menu_set:
		case R.id.menu_item_set://设置
			Intent intent_set = new Intent(activity, Activity_Set.class);
			activity.startActivityForResult(intent_set,
					AndroidConfig.Set_requestCode);
			activity.overridePendingTransition(R.anim.in_from_right,
					R.anim.out_to_left);
			// activity.startAnimActivity(Activity_Set.class);
			break;

		default:
			break;
		}
	}

}
