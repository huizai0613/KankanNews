package com.kankan.kankanews.ui.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.kankan.kankanews.utils.CommonUtils;
import com.kankan.kankanews.utils.Options;
import com.kankanews.kankanxinwen.R;
import com.nostra13.universalimageloader.core.ImageLoader;

public class AutoImageVIew extends ImageView {

	private AutoImageTag mAutoImageTag;
	private Bitmap bitmap;
	private ImageLoader imageLoader;

	public AutoImageVIew(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		imageLoader = ImageLoader.getInstance();
	}

	public AutoImageVIew(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		imageLoader = ImageLoader.getInstance();
	}

	public AutoImageVIew(Context context) {
		super(context);
		imageLoader = ImageLoader.getInstance();
	}

	public AutoImageTag getmAutoImageTag() {
		return mAutoImageTag;
	}

	public void setmAutoImageTag(AutoImageTag mAutoImageTag) {
		this.mAutoImageTag = mAutoImageTag;
	}

	public void loadImage() {

//		setTag(R.string.graphic, "graphic");

//		CommonUtils.zoomImage(imageLoader, mAutoImageTag.getUrlPath(), this,
//				getContext());
	}

	public void reciverImage() {

		if (bitmap != null && !bitmap.isRecycled()) {
			bitmap.recycle();
			bitmap = null;
		}

	}

}
