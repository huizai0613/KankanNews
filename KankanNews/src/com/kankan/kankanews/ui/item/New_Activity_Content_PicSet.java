package com.kankan.kankanews.ui.item;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.json.JSONObject;

import android.content.ClipboardManager;
import android.content.Context;
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
import android.view.MotionEvent;
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
import com.kankan.kankanews.base.IA.CrashApplication;
import com.kankan.kankanews.base.view.SildingFinishLayout;
import com.kankan.kankanews.bean.New_News;
import com.kankan.kankanews.bean.New_NewsPic;
import com.kankan.kankanews.config.AndroidConfig;
import com.kankan.kankanews.exception.NetRequestException;
import com.kankan.kankanews.photoview.PhotoView;
import com.kankan.kankanews.photoview.PhotoViewAttacher.OnPhotoTapListener;
import com.kankan.kankanews.sina.AccessTokenKeeper;
import com.kankan.kankanews.sina.Constants;
import com.kankan.kankanews.ui.MainActivity;
import com.kankan.kankanews.ui.fragment.item.New_HomeItemFragment;
import com.kankan.kankanews.ui.view.board.CustomShareBoard;
import com.kankan.kankanews.ui.view.board.FontColumsBoard;
import com.kankan.kankanews.utils.CommonUtils;
import com.kankan.kankanews.utils.DebugLog;
import com.kankan.kankanews.utils.FontUtils;
import com.kankan.kankanews.utils.ImgUtils;
import com.kankan.kankanews.utils.NetUtils;
import com.kankan.kankanews.utils.Options;
import com.kankan.kankanews.utils.PixelUtil;
import com.kankan.kankanews.utils.ShareUtil;
import com.kankan.kankanews.utils.TimeUtil;
import com.kankan.kankanews.utils.ToastUtils;
import com.kankanews.kankanxinwen.R;
import com.lidroid.xutils.exception.DbException;
import com.umeng.socialize.sso.UMSsoHandler;

public class New_Activity_Content_PicSet extends BaseActivity implements
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
	private View nightView;

	private String[][] parseImagegroups;

	private View rLayout_bottom;

	private New_News new_news;

	// 分享类
	private ShareUtil shareUtil;

	private View main_bg;

	// private SildingFinishLayout mSildingFinishLayout;

	int curPage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_activity_pic_set);
		// mSildingFinishLayout = (SildingFinishLayout)
		// findViewById(R.id.sildingFinishLayout);
		// mSildingFinishLayout
		// .setOnSildingFinishListener(new
		// SildingFinishLayout.OnSildingFinishListener() {
		//
		// @Override
		// public void onSildingFinish() {
		// finish();
		// }
		// });
	}

	// @Override
	// public boolean dispatchTouchEvent(MotionEvent ev) {
	// // TODO Auto-generated method stub
	// if (this.mApplication.getMainActivity() != null) {
	// if (curPage == 0) {
	// boolean flag = mSildingFinishLayout.onTouch(ev);
	// if (flag)
	// return flag;
	// }
	// }
	// return super.dispatchTouchEvent(ev);
	// }

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
		initTitleBarIcon(R.drawable.ic_share, R.drawable.new_ic_back,
				R.drawable.ic_close_white, R.drawable.ic_font,
				R.drawable.ic_refresh);

		vp = (ViewPager) findViewById(R.id.vp);
		vp_content = (TextView) findViewById(R.id.vp_content);
		rLayout_bottom = findViewById(R.id.rLayout_bottom);
		main_bg = findViewById(R.id.main_bg);
		nightView = findViewById(R.id.night_view);
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
		NetUtils.getInstance(mContext).addNewNewsClickData("tjid=" + mid);

		NetUtils.getInstance(mContext).getAnalyse(this, "album",
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
		NetUtils.getInstance(this).getNewNewsContent(mid, type, mListener,
				mErrorListener);
	}

	@Override
	protected void setListener() {
		vp.setOnPageChangeListener(this);
		// 头部的左右点击事件
		setOnLeftClickLinester(this);
		setOnRightClickLinester(this);
		setOnContentClickLinester(this);

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
			// SpannableStringBuilder style = new SpannableStringBuilder(str);
			// // SpannableStringBuilder实现CharSequence接口
			// style.setSpan(new AbsoluteSizeSpan(PixelUtil.dp2px(20)), 0,
			// ((position + 1) + "/" + parseImagegroups.length).length(),
			// Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			//
			// if (!TextUtils.isEmpty(parseImagegroups[position][0]))
			// style.setSpan(new AbsoluteSizeSpan(PixelUtil.dp2px(14)),
			// ((position + 1) + "/" + parseImagegroups.length)
			// .length() + 1, str.length() - 1,
			// Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			FontUtils.setTextViewFontSize(this, vp_content,
					R.string.news_content_text_size, spUtil.getFontSizeRadix());
			vp_content.setText(str);
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
					DebugLog.e("卧槽  点进来了");
//					Animation top_in = AnimationUtils.loadAnimation(
//							New_Activity_Content_PicSet.this, R.anim.top_in);
//					top_in.setAnimationListener(new AnimationListener() {
//						@Override
//						public void onAnimationStart(Animation animation) {
//							titleBarView.setVisibility(View.VISIBLE);
//							rLayout_bottom.setVisibility(View.VISIBLE);
//							vp_content.setVisibility(View.VISIBLE);
//
//						}
//
//						@Override
//						public void onAnimationRepeat(Animation animation) {
//						}
//
//						@Override
//						public void onAnimationEnd(Animation animation) {
//
//						}
//					});
//					Animation top_out = AnimationUtils.loadAnimation(
//							New_Activity_Content_PicSet.this, R.anim.top_out);
//					top_out.setAnimationListener(new AnimationListener() {
//						@Override
//						public void onAnimationStart(Animation animation) {
//
//						}
//
//						@Override
//						public void onAnimationRepeat(Animation animation) {
//						}
//
//						@Override
//						public void onAnimationEnd(Animation animation) {
//							titleBarView.setVisibility(View.GONE);
//							rLayout_bottom.setVisibility(View.GONE);
//							vp_content.setVisibility(View.GONE);
//						}
//					});
//					Animation bottom_in = AnimationUtils.loadAnimation(
//							New_Activity_Content_PicSet.this, R.anim.bottom_in);
//					Animation bottom_out = AnimationUtils
//							.loadAnimation(New_Activity_Content_PicSet.this,
//									R.anim.bottom_out);
					isHide = !isHide;
					if (isHide) {
//						titleBarView.startAnimation(top_out);
//						rLayout_bottom.startAnimation(bottom_out);
						 titleBarView.setVisibility(View.GONE);
						 rLayout_bottom.setVisibility(View.GONE);
						 vp_content.setVisibility(View.GONE);
					} else {
//						titleBarView.startAnimation(top_in);
//						rLayout_bottom.startAnimation(bottom_in);

						 titleBarView.setVisibility(View.VISIBLE);
						 rLayout_bottom.setVisibility(View.VISIBLE);
						 vp_content.setVisibility(View.VISIBLE);
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
		case R.id.title_bar_left_img:
			onBackPressed();
			break;
		case R.id.title_bar_content_img:
			// 一键分享
			CustomShareBoard shareBoard = new CustomShareBoard(this, shareUtil,
					this);
			shareBoard.setAnimationStyle(R.style.popwin_anim_style);
			shareBoard.showAtLocation(mContext.getWindow().getDecorView(),
					Gravity.BOTTOM, 0, 0);
			break;
		case R.id.title_bar_right_second_img:
			this.refresh();
			break;
		case R.id.title_bar_right_img:
			FontColumsBoard fontBoard = new FontColumsBoard(this);
			fontBoard.setAnimationStyle(R.style.popwin_anim_style);
			fontBoard.showAtLocation(mContext.getWindow().getDecorView(),
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
		System.gc();
		if (this.mApplication.getMainActivity() == null) {
			Intent intent = getIntent();
			intent.setClass(this, MainActivity.class);
			this.startActivity(intent);
			overridePendingTransition(R.anim.alpha_in, R.anim.out_to_right);
		}
		super.finish();
	}

	@Override
	public void chage2Day() {
		// TODO Auto-generated method stub
		nightView.setVisibility(View.GONE);
		((CrashApplication) this.getApplication()).changeMainActivityDayMode();
	}

	@Override
	public void chage2Night() {
		// TODO Auto-generated method stub
		nightView.setVisibility(View.VISIBLE);
		((CrashApplication) this.getApplication()).changeMainActivityDayMode();
	}

	@Override
	public void copy2Clip() {
		// TODO Auto-generated method stub
		ClipboardManager clip = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
		clip.setText(titleurl);
		ToastUtils.Infotoast(this, "已将链接复制进黏贴板");
	}

	@Override
	public void changeFontSize() {
		// TODO Auto-generated method stub

		FontUtils.setTextViewFontSize(this, vp_content,
				R.string.news_content_text_size, spUtil.getFontSizeRadix());
		FontUtils.chagneFontSizeGlobal();
	}

	@Override
	public void initNightView(boolean isFullScreen) {
		if (!spUtil.getIsDayMode())
			chage2Night();
	}

}
