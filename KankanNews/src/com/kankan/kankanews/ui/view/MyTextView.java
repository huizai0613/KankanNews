package com.kankan.kankanews.ui.view;

import com.kankan.kankanews.base.IA.CrashApplication;
import com.kankan.kankanews.utils.CommonUtils;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

public class MyTextView extends TextView {

	public MyTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initFontStyle();
	}

	public MyTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initFontStyle();
	}

	public MyTextView(Context context) {
		super(context);
		initFontStyle();
	}

	private void initFontStyle() {
		setTypeface(CrashApplication.getInstance().getTf());// 设置字体
	}

	public int getAvailableWidth() {
		return getWidth() - getPaddingLeft() - getPaddingRight();
	}

	public boolean isOverFlowed() {
		Paint paint = getPaint();
		if (getAvailableWidth() == 0 || getLineCount() == 0)
			return false;
		float width = paint.measureText(getText().toString())
				/ this.getLineCount(); 
		if (width > getAvailableWidth())
			return true;
		return false;
	}
}
