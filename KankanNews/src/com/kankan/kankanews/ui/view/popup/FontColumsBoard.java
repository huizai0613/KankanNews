/**
 * 
 */

package com.kankan.kankanews.ui.view.popup;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.kankan.kankanews.base.BaseActivity;
import com.kankan.kankanews.bean.New_Colums;
import com.kankan.kankanews.bean.New_Colums_Second;
import com.kankan.kankanews.ui.item.New_Activity_Colums_Info;
import com.kankan.kankanews.utils.DebugLog;
import com.kankan.kankanews.utils.FontUtils;
import com.kankan.kankanews.utils.ImgUtils;
import com.kankan.kankanews.utils.SharePreferenceUtil;
import com.kankanews.kankanxinwen.R;

/**
 * 
 */
public class FontColumsBoard extends PopupWindow implements OnClickListener {
	private LayoutInflater inflater;
	private BaseActivity activity;
	private View cancelBut;
	private TextView smallSize;
	private TextView normalSize;
	private TextView bigSize;
	private TextView mostSize;
	private ImageView dayNight;
	public SharePreferenceUtil spUtil;
	private View backView;

	public class ColumItem {
		ImageView logo;
		TextView title;
	}

	ColumItem item;

	public FontColumsBoard(BaseActivity activity) {
		super(activity);
		this.activity = activity;
		spUtil = activity.spUtil;
		initView(activity);
		initData();
	}

	@SuppressWarnings("deprecation")
	private void initView(Context context) {
		inflater = LayoutInflater.from(context);
		View rootView = inflater.inflate(R.layout.font_colum_board, null);
		cancelBut = rootView.findViewById(R.id.font_colums_cancel_but);
		smallSize = (TextView) rootView.findViewById(R.id.font_small_size);
		normalSize = (TextView) rootView.findViewById(R.id.font_normal_size);
		bigSize = (TextView) rootView.findViewById(R.id.font_big_size);
		mostSize = (TextView) rootView.findViewById(R.id.font_most_size);
		dayNight = (ImageView) rootView.findViewById(R.id.day_night_change);
		backView = rootView.findViewById(R.id.font_back_view);
		changeNightImg(spUtil.getIsDayMode());

		initFontSizeLayout();
		setCurFontSizeBg();
		setContentView(rootView);
		setWidth(LayoutParams.MATCH_PARENT);
		setHeight(LayoutParams.WRAP_CONTENT);
		setFocusable(true);
		setBackgroundDrawable(new BitmapDrawable());
		setTouchable(true);
	}

	public void initData() {
		cancelBut.setOnClickListener(this);
		smallSize.setOnClickListener(this);
		normalSize.setOnClickListener(this);
		bigSize.setOnClickListener(this);
		mostSize.setOnClickListener(this);
		dayNight.setOnClickListener(this);
		backView.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.font_small_size:
			initFontSizeLayout();
			changeBg(smallSize, R.drawable.font_size_set_left_item_border_red,
					Color.WHITE);
			spUtil.saveFontSizeRadix(FontUtils.fontSize[0]);
			activity.changeFontSize();
			break;
		case R.id.font_normal_size:
			initFontSizeLayout();
			changeBg(normalSize, R.drawable.font_size_set_item_border_red,
					Color.WHITE);
			spUtil.saveFontSizeRadix(FontUtils.fontSize[1]);
			activity.changeFontSize();
			break;
		case R.id.font_big_size:
			initFontSizeLayout();
			changeBg(bigSize, R.drawable.font_size_set_item_border_red,
					Color.WHITE);
			spUtil.saveFontSizeRadix(FontUtils.fontSize[2]);
			activity.changeFontSize();
			break;
		case R.id.font_most_size:
			initFontSizeLayout();
			changeBg(mostSize, R.drawable.font_size_set_right_item_border_red,
					Color.WHITE);
			spUtil.saveFontSizeRadix(FontUtils.fontSize[3]);
			activity.changeFontSize();
			break;
		case R.id.day_night_change:
			boolean isDay = !spUtil.getIsDayMode();
			spUtil.saveIsDayMode(isDay);
			changeNightImg(isDay);
			activity.changeDayMode(isDay);
			break;
		case R.id.font_back_view:
		default:
			dismiss();
			break;
		}
	}

	public void initFontSizeLayout() {
		changeBg(smallSize, R.drawable.font_size_set_left_item_border,
				Color.BLACK);
		changeBg(normalSize, R.drawable.font_size_set_item_border, Color.BLACK);
		changeBg(bigSize, R.drawable.font_size_set_item_border, Color.BLACK);
		changeBg(mostSize, R.drawable.font_size_set_right_item_border,
				Color.BLACK);
	}

	public void setCurFontSizeBg() {
		float radix = spUtil.getFontSizeRadix();
		if (radix == FontUtils.fontSize[0])
			changeBg(smallSize, R.drawable.font_size_set_left_item_border_red,
					Color.WHITE);
		if (radix == FontUtils.fontSize[1])
			changeBg(normalSize, R.drawable.font_size_set_item_border_red,
					Color.WHITE);
		if (radix == FontUtils.fontSize[2])
			changeBg(bigSize, R.drawable.font_size_set_item_border_red,
					Color.WHITE);
		if (radix == FontUtils.fontSize[3])
			changeBg(mostSize, R.drawable.font_size_set_right_item_border_red,
					Color.WHITE);
	}

	public void changeBg(TextView view, int resId, int textColor) {
		view.setBackgroundResource(resId);
		view.setTextColor(textColor);
	}

	public void changeNightImg(boolean isDay) {
		if (isDay)
			dayNight.setImageResource(R.drawable.change_night);
		else
			dayNight.setImageResource(R.drawable.change_day);
	}
}