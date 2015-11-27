package com.kankan.kankanews.ui;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import com.android.volley.VolleyError;
import com.kankan.kankanews.base.BaseActivity;
import com.kankan.kankanews.photoview.PhotoView;
import com.kankan.kankanews.photoview.PhotoViewAttacher;
import com.kankan.kankanews.photoview.PhotoViewAttacher.OnPhotoTapListener;
import com.kankan.kankanews.photoview.PhotoViewAttacher.OnViewTapListener;
import com.kankan.kankanews.utils.CommonUtils;
import com.kankan.kankanews.utils.DebugLog;
import com.kankan.kankanews.utils.ImgUtils;
import com.kankan.kankanews.utils.PixelUtil;
import com.kankanews.kankanxinwen.R;

public class PhotoViewActivity extends BaseActivity implements OnClickListener,
		OnPageChangeListener {
	private String[] imageGroup;
	private List<View> imagePointViews;
	private int curPageNum;
	private ViewPager photoViewPager;
	private LinearLayout pointsContent;
	private MyVpAdapter viewPagerAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_photo_view);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		photoViewPager = (ViewPager) this
				.findViewById(R.id.photo_view_view_pager);
		pointsContent = (LinearLayout) this
				.findViewById(R.id.photo_view_points_vontent);
	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub
		curPageNum = this.getIntent().getIntExtra("_PHOTO_CUR_NUM_", 0);
		imageGroup = this.getIntent().getStringArrayExtra("_IMAGE_GROUP_");
		imagePointViews = new ArrayList<View>(imageGroup.length);
		android.widget.LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
				PixelUtil.dp2px(6), PixelUtil.dp2px(6));
		for (int i = 0; i < imageGroup.length; i++) {
			View point = new View(this);
			layoutParams.rightMargin = PixelUtil.dp2px(6);
			point.setLayoutParams(layoutParams);
			point.setBackgroundResource(R.drawable.point_gray);
			imagePointViews.add(point);
		}
		if (imagePointViews.size() > 1) {
			for (View v : imagePointViews) {
				if (v.getParent() != null)
					((LinearLayout) v.getParent()).removeView(v);
				pointsContent.addView(v);
				v.setBackgroundResource(R.drawable.point_gray);
			}
			imagePointViews.get(curPageNum).setBackgroundResource(
					R.drawable.point_red);
			pointsContent.setVisibility(View.VISIBLE);
		} else {
			pointsContent.setVisibility(View.GONE);
		}
		viewPagerAdapter = new MyVpAdapter();
		photoViewPager.setAdapter(viewPagerAdapter);
		if (curPageNum != 0)
			setRightFinsh(false);
		else
			setRightFinsh(true);
		photoViewPager.setCurrentItem(curPageNum);
	}

	@Override
	protected void setListener() {
		// TODO Auto-generated method stub
		photoViewPager.setOnPageChangeListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}

	private class MyVpAdapter extends PagerAdapter {
		@Override
		public int getCount() {
			if (imageGroup != null) {
				return imageGroup.length;
			} else {
				return 0;
			}
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			String picUrl = CommonUtils.doWebpUrl(imageGroup[position]);
			View view = getLayoutInflater().inflate(
					R.layout.new_item_activity_picset, null);
			final PhotoView photoView = (PhotoView) view
					.findViewById(R.id.img_photo_view);
			ImgUtils.imageLoader.displayImage(picUrl, photoView,
					ImgUtils.homeImageOptions);
			// PhotoViewAttacher mAttacher = new PhotoViewAttacher(photoView);
			// mAttacher.setOnViewTapListener(new OnViewTapListener() {
			// @Override
			// public void onViewTap(View view, float x, float y) {
			// DebugLog.e("单击");
			// }
			// });
			photoView.setOnPhotoTapListener(new OnPhotoTapListener() {

				@Override
				public void onPhotoTap(View view, float x, float y) {
					// TODO Auto-generated method stub
					DebugLog.e("单击");
					finish();
				}
			});
			view.setTag(photoView);
			container.addView(view);
			return view;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {

			View v = (View) object;
			PhotoView tag2 = (PhotoView) v.getTag();
			container.removeView((View) object);
		}
	}

	@Override
	protected void onSuccess(JSONObject jsonObject) {

	}

	@Override
	protected void onFailure(VolleyError error) {

	}

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	@Override
	public void onPageSelected(int arg0) {
		if (arg0 == 0) {
			setRightFinsh(true);
		} else {
			setRightFinsh(false);
		}
		for (View v : imagePointViews) {
			v.setBackgroundResource(R.drawable.point_gray);
		}
		imagePointViews.get(arg0 % imagePointViews.size())
				.setBackgroundResource(R.drawable.point_red);

	}

}
