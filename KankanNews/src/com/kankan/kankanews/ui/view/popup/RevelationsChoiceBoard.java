/**
 * 
 */

package com.kankan.kankanews.ui.view.popup;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.kankan.kankanews.animation.RotateAndTranslateAnimation;
import com.kankan.kankanews.base.BaseActivity;
import com.kankan.kankanews.ui.RevelationsActivity;
import com.kankan.kankanews.ui.SharedJumpActivity;
import com.kankan.kankanews.utils.PixelUtil;
import com.kankanews.kankanxinwen.R;

/**
 * 
 */
public class RevelationsChoiceBoard extends PopupWindow implements
		OnClickListener {
	private LayoutInflater inflater;
	private BaseActivity activity;
	private View backView;
	private ImageView goPhotoRevelationsImg;
	private ImageView goVideoRevelationsImg;

	public RevelationsChoiceBoard(BaseActivity activity) {
		super(activity);
		this.activity = activity;
		initView(activity);
		initData();
	}

	@SuppressWarnings("deprecation")
	private void initView(Context context) {
		inflater = LayoutInflater.from(context);
		View rootView = inflater.inflate(R.layout.popup_revelations_choice,
				null);
		backView = rootView.findViewById(R.id.choice_back_view);
		goPhotoRevelationsImg = (ImageView) rootView
				.findViewById(R.id.go_photo_revelations_img);
		goVideoRevelationsImg = (ImageView) rootView
				.findViewById(R.id.go_video_revelations_img);
		backView.setOnClickListener(this);
		setContentView(rootView);
		setFocusable(true);
		setWidth(LayoutParams.MATCH_PARENT);
		setHeight(LayoutParams.WRAP_CONTENT);
		setBackgroundDrawable(new BitmapDrawable());
		setTouchable(true);
		goPhotoRevelationsImg.setOnClickListener(this);
		goVideoRevelationsImg.setOnClickListener(this);
	}

	public void initData() {
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.choice_back_view) {
			dismiss();
			return;
		}
		 Intent intent = new Intent(this.activity, RevelationsActivity.class);
		switch (v.getId()) {
		case R.id.go_photo_revelations_img:
			intent.putExtra("_REVELATIONS_TYPE_",
					RevelationsActivity._REVELATIONS_PHOTO_);
			break;
		case R.id.go_video_revelations_img:
			intent.putExtra("_REVELATIONS_TYPE_",
					RevelationsActivity._REVELATIONS_VIDEO_);
			break;
		}
		this.activity.startActivity(intent);
		dismiss();
	}

	public void doAnim() {
		setImgMargin(true, goPhotoRevelationsImg);
		setImgMargin(false, goVideoRevelationsImg);
		int leftFromXDelta = this.activity.mScreenWidth / 3 / 2
				- PixelUtil.dp2px(10);
		int rightFromXDelta = -this.activity.mScreenWidth / 3 / 2
				+ PixelUtil.dp2px(10);
		int fromYDelta = PixelUtil.dp2px(140 - 30 - 30);
		AnimationSet photoImgAnimationSet = generateAnimation(leftFromXDelta,
				fromYDelta, true);
		cleanAnimation(photoImgAnimationSet, goPhotoRevelationsImg);
		goPhotoRevelationsImg.startAnimation(photoImgAnimationSet);
		AnimationSet videoImgAnimationSet = generateAnimation(rightFromXDelta,
				fromYDelta, false);
		cleanAnimation(videoImgAnimationSet, goVideoRevelationsImg);
		goVideoRevelationsImg.startAnimation(videoImgAnimationSet);
	}

	private void setImgMargin(boolean isLeft, View view) {
		MarginLayoutParams margin = new MarginLayoutParams(
				view.getLayoutParams());
		if (isLeft) {
			margin.setMargins(
					this.activity.mScreenWidth / 3 - PixelUtil.dp2px(20),
					margin.topMargin, margin.width, margin.bottomMargin);
		} else {
			margin.setMargins(
					this.activity.mScreenWidth / 3 * 2
							- PixelUtil.dp2px(60 - 20), margin.topMargin,
					margin.width, margin.bottomMargin);
		}
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
				margin);
		view.setLayoutParams(layoutParams);
	}

	private AnimationSet generateAnimation(int fromXDelta, int fromYDelta,
			boolean isLeft) {
		AnimationSet animationSet = new AnimationSet(false);
		Animation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
		alphaAnimation.setDuration(300);
		Animation translateAnimation = new RotateAndTranslateAnimation(
				fromXDelta, 0, fromYDelta, 0, isLeft ? 0 : 720, isLeft ? 720
						: 0);
		translateAnimation.setDuration(300);
		translateAnimation
				.setInterpolator(new AccelerateDecelerateInterpolator());
		animationSet.addAnimation(translateAnimation);
		animationSet.addAnimation(alphaAnimation);
		animationSet.setFillAfter(true);
		return animationSet;
	}

	private void cleanAnimation(AnimationSet animationSet, final View view) {
		animationSet.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				view.postDelayed(new Runnable() {
					@Override
					public void run() {
						view.clearAnimation();
					}
				}, 0);
			}
		});
	}
}
