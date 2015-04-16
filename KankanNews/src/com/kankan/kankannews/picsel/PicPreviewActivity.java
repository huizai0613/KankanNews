package com.kankan.kankannews.picsel;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.kankan.kankanews.base.BaseActivity;
import com.kankan.kankanews.config.AndroidConfig;
import com.kankanews.kankanxinwen.R;

public class PicPreviewActivity extends BaseActivity implements
		OnPageChangeListener, OnClickListener {

	private ViewPager imgViewPage;
	private Button okBut;
	private Button cancelBut;
	private Button delBut;
	private TextView sumPic;
	private TextView numPic;
//	private LinearLayout showLayout;
	private List<String> imagesSelected = new LinkedList<String>();
	private List<ImageView> imageViews = new LinkedList<ImageView>();
	private int sumImg = 0;
 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.pic_preview_activity);
		this.setRightFinsh(false);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		imgViewPage = (ViewPager) this.findViewById(R.id.pic_view_page);
		okBut = (Button) this.findViewById(R.id.pic_preview_ok);
		cancelBut = (Button) this.findViewById(R.id.pic_preview_cancel);
		delBut = (Button) this.findViewById(R.id.pic_preview_delete);
		sumPic = (TextView) this.findViewById(R.id.pic_preview_sum);
		numPic = (TextView) this.findViewById(R.id.pic_preview_num);
//		showLayout = (LinearLayout) this.findViewById(R.id.pic_preview_show);

	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub
		List<String> mainSeleted = (List<String>) this.getIntent()
				.getSerializableExtra("IMAGE_SELECTED_LIST");
		imagesSelected.addAll(mainSeleted);

		LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.MATCH_PARENT);
		for (int i = 0; i < imagesSelected.size(); i++) {
			ImageView image = new ImageView(this);

			image.setLayoutParams(layoutParams);
//			ImageLoader.getInstance(1, Type.LIFO).loadImage(
//					imagesSelected.get(i), image);
			Bitmap bit = BitmapFactory.decodeFile(imagesSelected.get(i));
			image.setImageBitmap(bit);
			imageViews.add(image);
			imgViewPage.addView(image);
		}
		
		imgViewPage.setAdapter(new ViewPagerAdapter());
		
		sumImg = imagesSelected.size();
		
		sumPic.setText("/" + sumImg + "张");
		numPic.setText("1");
	}

	/**
	 * class desc: 引导页面适配器
	 */
	public class ViewPagerAdapter extends PagerAdapter {

		// 销毁arg1位置的界面
		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
//			((BitmapDrawable) (imageViews.get(arg1)).getDrawable()).getBitmap().recycle();
//			((ViewPager) arg0).removeView(imageViews.get(arg1));
//			imageViews.get(arg1).setImageBitmap(null);
			return;
		}

		@Override
		public void finishUpdate(View arg0) {
		}

		// 获得当前界面数
		@Override
		public int getCount() {
			if (imageViews != null) {
				return imageViews.size();
			}
			return 0;
		}

		// 判断是否由对象生成界面
		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return (arg0 == arg1);
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			ImageView image = imageViews.get(position);
			Bitmap bit = BitmapFactory.decodeFile(imagesSelected.get(position));
			image.setImageBitmap(bit);
			if(image.getParent() == null){
				container.addView(image);
			} else{
				 ((ViewGroup)image.getParent()).removeView(image);
				 container.addView(image); 
			}
			return image;
		}

		@Override
		public int getItemPosition(Object object) {
		    return POSITION_NONE;
		}
		
	}

	@Override
	protected void setListener() {
		// TODO Auto-generated method stub
		imgViewPage.setOnPageChangeListener(this);
		okBut.setOnClickListener(this);
		cancelBut.setOnClickListener(this);
		delBut.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		int id = v.getId();
		switch(id){
		case R.id.pic_preview_ok:
			getIntent().putExtra("NEW_IMAGE_SELECTED_LIST", (Serializable)imagesSelected);
	        setResult(AndroidConfig.REVELATIONS_FRAGMENT_RESULT_OK, getIntent());
	        finish();
			break;
		case R.id.pic_preview_cancel:
	        setResult(AndroidConfig.REVELATIONS_FRAGMENT_RESULT_CANCEL);
	        finish();
	        break;
			
		case R.id.pic_preview_delete:
			int curNo = imgViewPage.getCurrentItem();
			if(curNo == 0){
				if(sumImg ==0){
					
				}else {
					imgViewPage.setCurrentItem(1);
				}
			}else{
				imgViewPage.setCurrentItem(curNo - 1);
			}
			imagesSelected.remove(curNo);
			ImageView image = imageViews.get(curNo);
			imageViews.remove(image);
			imgViewPage.removeView(image);
			((BitmapDrawable) (image).getDrawable()).getBitmap().recycle();
			imgViewPage.getAdapter().notifyDataSetChanged();
			numPic.setText((curNo) + "");
			sumImg--;
			sumPic.setText("/" + sumImg + "张");
			break;
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
		numPic.setText((arg0 + 1) + "");
	}
	
	@Override
    public void onBackPressed() {
        setResult(AndroidConfig.REVELATIONS_FRAGMENT_RESULT_CANCEL);
        super.onBackPressed();
    }
	
	@Override
    public void finish() {
        for (ImageView image : imageViews) {
			((BitmapDrawable) (image).getDrawable()).getBitmap().recycle();
		}
        super.finish();
    }
}
