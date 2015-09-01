package com.kankan.kankanews.ui.view;

import com.kankan.kankanews.base.IA.CrashApplication;
import com.kankan.kankanews.utils.CommonUtils;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class BorderTextView extends TextView {
	private String namespace = "http://shadow.com";
	private int color;

	public BorderTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initFontStyle();
	}

	public BorderTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		color = Color.parseColor(attrs.getAttributeValue(namespace,
				"BorderColor"));
		initFontStyle();
	}

	public BorderTextView(Context context) {
		super(context);
		initFontStyle();
	}

	public BorderTextView(Context context, String color) {
		super(context);
		this.color = Color.parseColor(color);
		initFontStyle();
	}

	private void initFontStyle() {
		setTypeface(CrashApplication.getInstance().getTf());// 设置字体
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub

		super.onDraw(canvas);
		// 画边框
		Rect rec = canvas.getClipBounds(); 
		Paint paint = new Paint();
		paint.setColor(color);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(4);
		canvas.drawRect(rec, paint);
	}
}
