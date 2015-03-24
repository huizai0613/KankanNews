package com.kankan.kankanews.ui.view;

import com.kankanews.kankanxinwen.R;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.webkit.WebView;
import android.widget.ProgressBar;

/**
 * 带进度条的WebView
 * 
 * @author 农民伯伯
 * @see http://www.cnblogs.com/over140/archive/2013/03/07/2947721.html
 * 
 */
@SuppressWarnings("deprecation")
public class ProgressWebView extends WebView {

	private ProgressBar progressbar;
	

	public ProgressBar getProgressbar() {
		return progressbar;
	}


	public ProgressWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
		progressbar = new ProgressBar(context, null,
				android.R.attr.progressBarStyleHorizontal);
		progressbar.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
				5, 0, 0));
		progressbar.setProgressDrawable(getResources().getDrawable(
				R.drawable.web_seekbar));

		addView(progressbar);
		// setWebViewClient(new WebViewClient(){});
	}


	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		LayoutParams lp = (LayoutParams) progressbar.getLayoutParams();
		lp.x = l;
		lp.y = t;
		progressbar.setLayoutParams(lp);
		super.onScrollChanged(l, t, oldl, oldt);
	}
}