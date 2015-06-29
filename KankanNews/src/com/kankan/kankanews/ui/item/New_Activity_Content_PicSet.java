package com.kankan.kankanews.ui.item;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.kankan.kankanews.base.BaseActivity;
import com.kankan.kankanews.base.BaseVideoActivity;
import com.kankan.kankanews.bean.New_News;
import com.kankan.kankanews.bean.New_NewsPic;
import com.kankan.kankanews.config.AndroidConfig;
import com.kankan.kankanews.exception.NetRequestException;
import com.kankan.kankanews.net.ItnetUtils;
import com.kankan.kankanews.sina.AccessTokenKeeper;
import com.kankan.kankanews.sina.Constants;
import com.kankan.kankanews.ui.view.CustomShareBoard;
import com.kankan.kankanews.utils.CommonUtils;
import com.kankan.kankanews.utils.ImgUtils;
import com.kankan.kankanews.utils.Options;
import com.kankan.kankanews.utils.PixelUtil;
import com.kankan.kankanews.utils.ShareUtil;
import com.kankan.kankanews.utils.TimeUtil;
import com.kankan.kankanews.utils.ToastUtils;
import com.kankanews.kankanxinwen.R;
import com.lidroid.xutils.exception.DbException;
import com.umeng.socialize.sso.UMSsoHandler;
//import com.sina.weibo.sdk.api.ImageObject;
//import com.sina.weibo.sdk.api.TextObject;
//import com.sina.weibo.sdk.api.WeiboMultiMessage;
//import com.sina.weibo.sdk.api.share.BaseResponse;
//import com.sina.weibo.sdk.api.share.IWeiboHandler;
//import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
//import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
//import com.sina.weibo.sdk.api.share.WeiboShareSDK;
//import com.sina.weibo.sdk.auth.AuthInfo;
//import com.sina.weibo.sdk.auth.Oauth2AccessToken;
//import com.sina.weibo.sdk.auth.WeiboAuthListener;
//import com.sina.weibo.sdk.constant.WBConstants;
//import com.sina.weibo.sdk.exception.WeiboException;
import com.xunao.view.photoview.PhotoView;
import com.xunao.view.photoview.PhotoViewAttacher.OnPhotoTapListener;

public class New_Activity_Content_PicSet extends BaseVideoActivity implements
		OnClickListener, OnPageChangeListener {

	/** 微博微博分享接口实例 */
	// private IWeiboShareAPI mWeiboShareAPI = null;

	private String mid;
	private String type;
	private String titleurl;
	private String newstime;
	private String titlePic;
	private String sharedPic;
	private String titlelist;

	private boolean isHide = false;
	private ViewPager vp;
	private TextView vp_content;

	private MyVpAdapter myVpAdapter;
	private New_NewsPic new_NewsPic;

	private String[][] parseImagegroups;

	private View rLayout_bottom;

	private New_News new_news;

	// 分享类
	private ShareUtil shareUtil;

	private View main_bg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_activity_pic_set);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		/** 使用SSO授权必须添加如下代码 */

		UMSsoHandler ssoHandler = shareUtil.getmController().getConfig()
				.getSsoHandler(requestCode);
		if (ssoHandler != null) {
			ssoHandler.authorizeCallBack(requestCode, resultCode, data);
		}
	}

	@Override
	protected void initView() {
		initTitle_Right_Left_bar("看看新闻", "", "", "#ffffff",
				R.drawable.new_ic_more, R.drawable.new_ic_back, "#000000",
				"#000000");

		vp = (ViewPager) findViewById(R.id.vp);
		vp_content = (TextView) findViewById(R.id.vp_content);
		rLayout_bottom = findViewById(R.id.rLayout_bottom);
		main_bg = findViewById(R.id.main_bg);

		main_bg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				initNetData();
			}
		});

	}

	@Override
	protected void initData() {
		// 获取上个页面传来的数据
		Intent intent = getIntent();
		mid = intent.getStringExtra("mid");
		type = intent.getStringExtra("type");
		titleurl = intent.getStringExtra("titleurl");
		newstime = intent.getStringExtra("newstime");
		titlePic = intent.getStringExtra("titlePic");
		sharedPic = intent.getStringExtra("sharedPic");
		titlelist = intent.getStringExtra("titlelist");
		// 存储数据
		new_news = new New_News();
		new_news.setId(mid);
		new_news.setType(type);
		new_news.setTitleurl(titleurl);
		new_news.setNewstime(newstime);
		new_news.setTitlepic(titlePic);
		new_news.setSharedPic(sharedPic);
		new_news.setTitlelist(titlelist);

		// 提交点击
		ItnetUtils.getInstance(mContext).addNewNewsClickData("tjid=" + mid);
		
		ItnetUtils.getInstance(mContext).getAnalyse(this, "album",
				new_news.getTitlelist(), new_news.getTitleurl());

		myVpAdapter = new MyVpAdapter();
		initLocalData();
		initNetData();
	}

	boolean isLocalData;

	private void initLocalData() {
		try {
			new_NewsPic = dbUtils.findById(New_NewsPic.class, new_news.getId());
			if (new_NewsPic != null) {
				parseImagegroups = new_NewsPic.parseImagegroup();
				vp.setOnPageChangeListener(this);
				vp.setAdapter(myVpAdapter);
				setbootmText(0);
				isLocalData = true;
				return;
			}
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		isLocalData = false;
	}

	private void initNetData() {
		ItnetUtils.getInstance(this).getNewNewsContent(mid, type, mListener,
				mErrorListener);
	}

	@Override
	protected void setListener() {
		vp.setOnPageChangeListener(this);
		setOnLeftClickLinester(this);
		setOnRightClickLinester(this);

	}

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	int curPage;

	@Override
	public void onPageSelected(int arg0) {
		if (arg0 == 0) {
			setRightFinsh(true);
		} else {
			setRightFinsh(false);
		}
		curPage = arg0;

		setbootmText(arg0);
	}

	@Override
	protected void onSuccess(JSONObject jsonObject) {
		new_NewsPic = new New_NewsPic();

		try {
			main_bg.setVisibility(View.GONE);
			new_NewsPic.parseJSON(jsonObject);
			new_NewsPic.setid(new_news.getId());
			parseImagegroups = new_NewsPic.parseImagegroup();
			// myVpAdapter.notifyDataSetChanged();
			vp.setOnPageChangeListener(this);
			vp.setAdapter(myVpAdapter);
			vp.setCurrentItem(curPage, false);
			setbootmText(curPage);

		} catch (NetRequestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// 保存数据到本地
		try {
			dbUtils.saveOrUpdate(new_NewsPic);
			new_news.setTitle(new_NewsPic.getTitle());
			new_news.setTitlepic(new_NewsPic.getTitlepic());
			new_news.setLooktime(Long.toString(TimeUtil.now()));
			dbUtils.saveOrUpdate(new_news);
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// 初始化shareutil类
		shareUtil = new ShareUtil(new_news, mContext);

	}

	@Override
	protected void onFailure(VolleyError error) {
		if (!isLocalData) {
			main_bg.setVisibility(View.VISIBLE);
		}
		ToastUtils.ErrorToastNoNet(mContext);
	}

	/**
	 * 设置底部文字
	 * 
	 * @param position
	 */
	private void setbootmText(int position) {
		if (parseImagegroups != null) {
			String str = (position + 1) + "/" + parseImagegroups.length + "　"
					+ parseImagegroups[position][0];
			SpannableStringBuilder style = new SpannableStringBuilder(str);
			// SpannableStringBuilder实现CharSequence接口
			style.setSpan(new AbsoluteSizeSpan(PixelUtil.dp2px(20)), 0,
					((position + 1) + "/" + parseImagegroups.length).length(),
					Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

			if (!TextUtils.isEmpty(parseImagegroups[position][0]))
				style.setSpan(new AbsoluteSizeSpan(PixelUtil.dp2px(14)),
						((position + 1) + "/" + parseImagegroups.length)
								.length() + 1, str.length() - 1,
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			vp_content.setText(style);
		}
	}

	private class MyVpAdapter extends PagerAdapter {
		@Override
		public int getCount() {
			if (parseImagegroups != null) {
				return parseImagegroups.length;
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
			String picUrl = CommonUtils
					.doWebpUrl(parseImagegroups[position][1]);
			View view = getLayoutInflater().inflate(
					R.layout.new_item_activity_picset, null);
			final PhotoView photoView = (PhotoView) view
					.findViewById(R.id.img_photo_view);
			/*
			 * 点击事件:隐藏头部和底部布局，在PhotoViewAttacher中的onDoubleTap()已经注释掉双击缩放的逻辑
			 */
			photoView.setOnPhotoTapListener(new OnPhotoTapListener() {
				@Override
				public void onPhotoTap(View view, float x, float y) {
					Animation top_in = AnimationUtils.loadAnimation(
							New_Activity_Content_PicSet.this, R.anim.top_in);
					top_in.setAnimationListener(new AnimationListener() {
						@Override
						public void onAnimationStart(Animation animation) {
							com_title_bar_bg.setVisibility(View.VISIBLE);
							rLayout_bottom.setVisibility(View.VISIBLE);
							vp_content.setVisibility(View.VISIBLE);

						}

						@Override
						public void onAnimationRepeat(Animation animation) {
						}

						@Override
						public void onAnimationEnd(Animation animation) {

						}
					});
					Animation top_out = AnimationUtils.loadAnimation(
							New_Activity_Content_PicSet.this, R.anim.top_out);
					top_out.setAnimationListener(new AnimationListener() {
						@Override
						public void onAnimationStart(Animation animation) {

						}

						@Override
						public void onAnimationRepeat(Animation animation) {
						}

						@Override
						public void onAnimationEnd(Animation animation) {
							com_title_bar_bg.setVisibility(View.GONE);
							rLayout_bottom.setVisibility(View.GONE);
							vp_content.setVisibility(View.GONE);
						}
					});
					Animation bottom_in = AnimationUtils.loadAnimation(
							New_Activity_Content_PicSet.this, R.anim.bottom_in);
					Animation bottom_out = AnimationUtils
							.loadAnimation(New_Activity_Content_PicSet.this,
									R.anim.bottom_out);
					isHide = !isHide;
					if (isHide) {
						com_title_bar_bg.startAnimation(top_out);
						rLayout_bottom.startAnimation(bottom_out);
					} else {
						com_title_bar_bg.startAnimation(top_in);
						rLayout_bottom.startAnimation(bottom_in);
					}
				}
			});

			// 加载图片
			// ImgUtils.imageLoader.displayImage(picUrl, photoView,
			// Options.getSmallImageOptions(false));
			ImgUtils.imageLoader.displayImage(picUrl, photoView,
					ImgUtils.homeImageOptions);
			view.setTag(photoView);
			container.addView(view);
			return view;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {

			View v = (View) object;
			PhotoView tag2 = (PhotoView) v.getTag();

			// if (tag2 != null) {
			// Drawable drawable2 = tag2.getDrawable();
			//
			// if (drawable2 instanceof BitmapDrawable) {
			// BitmapDrawable drawable = (BitmapDrawable) tag2
			// .getDrawable();
			// if (drawable != null) {
			// Bitmap bitmap = drawable.getBitmap();
			// if (bitmap != null) {
			// bitmap.recycle();
			// bitmap = null;
			// }
			// drawable = null;
			// }
			// }
			// }
			container.removeView((View) object);
		}
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.com_title_bar_left_bt:
			onBackPressed();
			break;
		case R.id.com_title_bar_right_bt:
		case R.id.com_title_bar_right_tv:
			if (shareUtil == null) {
				shareUtil = new ShareUtil(new_news, mContext);
			}
			// 一键分享
			CustomShareBoard shareBoard = new CustomShareBoard(this, shareUtil,
					this);
			shareBoard.setAnimationStyle(R.style.popwin_anim_style);
			shareBoard.showAtLocation(mContext.getWindow().getDecorView(),
					Gravity.BOTTOM, 0, 0);

			break;

		}
	}

	@Override
	public void onBackPressed() {
		AnimFinsh();
	}

	@Override
	public void refresh() {
		if (CommonUtils.isNetworkAvailable(mContext))
			initNetData();
	}

	@Override
	public void finish() {
		if (vp != null)
			vp.setAdapter(null);
		System.gc();
		super.finish();
	}
}
