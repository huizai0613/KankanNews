package com.kankan.kankanews.ui.item;

import java.util.Calendar;
import java.util.Date;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;

import com.android.volley.VolleyError;
import com.kankan.kankanews.base.BaseActivity;
import com.kankan.kankanews.base.view.SildingFinishLayout;
import com.kankan.kankanews.bean.New_Colums;
import com.kankan.kankanews.config.AndroidConfig;
import com.kankan.kankanews.utils.TimeUtil;
import com.kankanews.kankanxinwen.R;
import com.squareup.timessquare.CalendarPickerView;
import com.squareup.timessquare.CalendarPickerView.SelectionMode;
import com.squareup.timessquare.MyCallInterface;

public class New_Activity_Colums_Info_Time extends BaseActivity implements
		OnClickListener, MyCallInterface {

	private CalendarPickerView calendar;
	private New_Colums colums;

	private SildingFinishLayout mSildingFinishLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_activity_colums_info_time);
		mSildingFinishLayout = (SildingFinishLayout) findViewById(R.id.sildingFinishLayout);
		mSildingFinishLayout
				.setOnSildingFinishListener(new SildingFinishLayout.OnSildingFinishListener() {

					@Override
					public void onSildingFinish() {
						finish();
					}
				});
		mSildingFinishLayout.setTouchView(mSildingFinishLayout);
	}

	@Override
	protected void initView() {
		nightView = findViewById(R.id.night_view);

		colums = (New_Colums) getIntent().getSerializableExtra("colums");

		// 初始化头部
		initTitleLeftBar("选择日期", R.drawable.new_ic_back);
		// 头部的左右点击事件
		setOnLeftClickLinester(this);
		setOnRightClickLinester(this);

		final Calendar nextYear = Calendar.getInstance();
		// nextYear.add(Calendar.DATE,
		// Integer.valueOf(colums.getProgramStart()));
		// nextYear.add(Calendar.YEAR, 0);
		nextYear.add(Calendar.DAY_OF_MONTH, 1);

		final Calendar lastYear = Calendar.getInstance();
		lastYear.add(Calendar.YEAR, -1);

		calendar = (CalendarPickerView) findViewById(R.id.calendar_view);
		calendar.init(lastYear.getTime(), nextYear.getTime()) //
				.inMode(SelectionMode.SINGLE) //
				.withSelectedDate(new Date());
		calendar.setMyListen(this);
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.title_bar_left_img:
			onBackPressed();
			break;
		case R.id.title_bar_right_img:
			break;
		}

	}

	@Override
	public void method() {

		Intent intent = new Intent();
		long time = calendar.getSelectedDate().getTime();
		// if(time<System.currentTimeMillis())
		// {
		intent.putExtra("time", TimeUtil.unix2date(time / 1000, "yyyy-MM-dd"));
		setResult(AndroidConfig.Colums_Time_resultCode, intent);
		AnimFinsh();
		// }
		// ToastUtils.Infotoast(mContext, TimeUtil.unix2date(calendar
		// .getSelectedDate().getTime()/1000, "yyyy-MM-dd"));
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		boolean flag = mSildingFinishLayout.onTouch(ev);
		if (flag)
			return flag;
		return super.dispatchTouchEvent(ev);
	}
}
