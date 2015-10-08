/**
 * 
 */

package com.kankan.kankanews.ui.view.popup;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kankan.kankanews.animation.RotateAndTranslateAnimation;
import com.kankan.kankanews.base.BaseActivity;
import com.kankan.kankanews.bean.New_Colums;
import com.kankan.kankanews.bean.New_Colums_Second;
import com.kankan.kankanews.ui.item.New_Activity_Colums_Info;
import com.kankan.kankanews.utils.DebugLog;
import com.kankan.kankanews.utils.ImgUtils;
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
	}

	public void initData() {
	}

	@Override
	public void onClick(View v) {
		dismiss();
	}

	public void doAnim() {
		AnimationSet animationSet = new AnimationSet(false);
		// 参数1～2：x轴的开始位置
		// 参数3～4：y轴的开始位置
		// 参数5～6：x轴的结束位置
		// 参数7～8：x轴的结束位置
		MarginLayoutParams margin = new MarginLayoutParams(
				goPhotoRevelationsImg.getLayoutParams());
		DebugLog.e(goPhotoRevelationsImg.getWidth() + "");
		margin.setMargins(this.activity.mScreenWidth / 3
				- goPhotoRevelationsImg.getWidth() / 2, margin.topMargin,
				margin.width, margin.bottomMargin);
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
				margin);
		goPhotoRevelationsImg.setLayoutParams(layoutParams);
		// goPhotoRevelationsImg.setX(10);
		// goPhotoRevelationsImg.offsetTopAndBottom(-10);
		// goPhotoRevelationsImg.offsetLeftAndRight(-200);
		Animation translateAnimation = new RotateAndTranslateAnimation(200, 0,
				200, 0, 3600, 7200);
		// translateAnimation.setStartOffset(startOffset + preDuration);
		translateAnimation.setDuration(500);
		translateAnimation
				.setInterpolator(new AccelerateDecelerateInterpolator());
		animationSet.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				goPhotoRevelationsImg.postDelayed(new Runnable() {

					@Override
					public void run() {
						goPhotoRevelationsImg.clearAnimation();
					}
				}, 0);
			}
		});
		animationSet.addAnimation(translateAnimation);
		animationSet.setFillAfter(true);
		goPhotoRevelationsImg.startAnimation(animationSet);
	}
}
