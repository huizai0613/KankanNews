package com.kankan.kankanews.ui.item;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.UUID;

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
import com.kankan.kankanews.bean.NewsAlbum;
import com.kankan.kankanews.bean.NewsBrowseRecord;
import com.kankan.kankanews.bean.NewsHome;
import com.kankan.kankanews.bean.NewsHomeModule;
import com.kankan.kankanews.bean.NewsHomeModuleItem;
import com.kankan.kankanews.bean.SerializableObj;
import com.kankan.kankanews.config.AndroidConfig;
import com.kankan.kankanews.exception.NetRequestException;
import com.kankan.kankanews.photoview.PhotoView;
import com.kankan.kankanews.photoview.PhotoViewAttacher.OnPhotoTapListener;
import com.kankan.kankanews.ui.MainActivity;
import com.kankan.kankanews.ui.fragment.item.New_HomeItemFragment;
import com.kankan.kankanews.ui.view.popup.CustomShareBoard;
import com.kankan.kankanews.ui.view.popup.FontColumsBoard;
import com.kankan.kankanews.utils.CommonUtils;
import com.kankan.kankanews.utils.DebugLog;
import com.kankan.kankanews.utils.FontUtils;
import com.kankan.kankanews.utils.ImgUtils;
import com.kankan.kankanews.utils.JsonUtils;
import com.kankan.kankanews.utils.NetUtils;
import com.kankan.kankanews.utils.Options;
import com.kankan.kankanews.utils.PixelUtil;
import com.kankan.kankanews.utils.ShareUtil;
import com.kankan.kankanews.utils.TimeUtil;
import com.kankan.kankanews.utils.ToastUtils;
import com.kankanews.kankanxinwen.R;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.umeng.socialize.sso.UMSsoHandler;

public class NewsAlbumActivity extends BaseActivity implements OnClickListener,
		OnPageChangeListener {

	private NewsHomeModuleItem mHomeModuleItem;
	private NewsAlbum mAlbum;
	private String mAlbumJson;

	private boolean isHide = false;
	private ViewPager mImageViewPager;
	private TextView mTitleView;

	private AlbumImageViewPagerAdapter myVpAdapter;
	private View nightView;

	private View mBottomRootView;

	// 分享类
	private ShareUtil shareUtil;

	private View mRetryView;

	private View mLoadingView;

	private int curPage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_album);
	}

	@Override
	protected void initView() {
		initTitleBarIcon(R.drawable.ic_share, R.drawable.new_ic_back,
				R.drawable.ic_close_white, R.drawable.ic_font,
				R.drawable.ic_refresh);

		mImageViewPager = (ViewPager) findViewById(R.id.album_image_view_pager);
		mTitleView = (TextView) findViewById(R.id.album_title_view);
		mBottomRootView = findViewById(R.id.album_bottom_root_view);
		mRetryView = findViewById(R.id.album_retry_view);
		mLoadingView = findViewById(R.id.album_loading_view);
		nightView = findViewById(R.id.night_view);
		mRetryView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (CommonUtils.isNetworkAvailable(mContext)) {
					initNetData();
				}
			}
		});

	}

	@Override
	protected void initData() {
		mLoadingView.setVisibility(View.VISIBLE);
		// TODO
		Intent intent = getIntent();
		mHomeModuleItem = (NewsHomeModuleItem) intent
				.getSerializableExtra("_NEWS_HOME_MODEULE_ITEM_");
		saveBrowse();
		if (initLocalData()) {
			showData();
		} else {
			if (CommonUtils.isNetworkAvailable(mContext)) {
				initNetData();
			} else {
				this.mLoadingView.setVisibility(View.GONE);
				this.mRetryView.setVisibility(View.VISIBLE);
			}
		}
		// 提交点击
		NetUtils.getInstance(mContext).addNewNewsClickData(
				"tjid=" + mHomeModuleItem.getO_cmsid());

		NetUtils.getInstance(mContext).getAnalyse(this, "album",
				mHomeModuleItem.getTitle(), mHomeModuleItem.getTitleurl());
	}

	private void saveBrowse() {
		final NewsBrowseRecord browse = new NewsBrowseRecord();
		browse.setId(mHomeModuleItem.getO_cmsid());
		browse.setType(mHomeModuleItem.getType());
		browse.setTitle(mHomeModuleItem.getTitle());
		browse.setBrowseTime(new Date().getTime());
		browse.setTitlepic(mHomeModuleItem.getTitlepic());
		new Thread() {
			@Override
			public void run() {
				if (browse != null) {
					try {
						dbUtils.saveOrUpdate(browse);
					} catch (DbException e) {
						e.printStackTrace();
					}
				}

			}
		}.start();
	}

	@Override
	protected void saveLocalDate() {
		try {
			SerializableObj obj = new SerializableObj(UUID.randomUUID()
					.toString(), mAlbumJson, "NewsAlbum" + mAlbum.getId(),
					new Date().getTime());
			this.dbUtils
					.delete(SerializableObj.class,
							WhereBuilder.b("classType", "=",
									"mAlbum" + mAlbum.getId()));
			this.dbUtils.save(obj);
		} catch (DbException e) {
			DebugLog.e(e.getLocalizedMessage());
		}
	}

	protected boolean initLocalData() {
		try {
			SerializableObj object = (SerializableObj) this.dbUtils
					.findFirst(Selector.from(SerializableObj.class).where(
							"classType", "=",
							"NewsAlbum" + mHomeModuleItem.getO_cmsid()));
			if (object != null) {
				if (TimeUtil.isContentSaveTimeOK(object.getSaveTime())) {
					mAlbumJson = object.getJsonStr();
					mAlbum = JsonUtils.toObject(mAlbumJson, NewsAlbum.class);
					return true;
				} else {
					this.dbUtils.delete(
							SerializableObj.class,
							WhereBuilder.b("classType", "=", "mAlbum"
									+ mHomeModuleItem.getO_cmsid()));
					return false;
				}
			} else {
				return false;
			}
		} catch (DbException e) {
			DebugLog.e(e.getLocalizedMessage());
		}
		return false;
	}

	private void initNetData() {
		if (CommonUtils.isNetworkAvailable(this)) {
			netUtils.getNewsContent(mHomeModuleItem.getO_cmsid(),
					mHomeModuleItem.getType(), this.mListener,
					this.mErrorListener);
		}
	}

	@Override
	protected void setListener() {
		mImageViewPager.setOnPageChangeListener(this);
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
		mAlbumJson = jsonObject.toString();
		mAlbum = (NewsAlbum) JsonUtils.toObject(mAlbumJson, NewsAlbum.class);
		saveLocalDate();
		showData();
	}

	private void showData() {
		if (myVpAdapter == null) {
			myVpAdapter = new AlbumImageViewPagerAdapter();
			mImageViewPager.setAdapter(myVpAdapter);
		} else {
			myVpAdapter.notifyDataSetChanged();
		}
		mImageViewPager.setCurrentItem(curPage, false);
		setbootmText(curPage);
		mLoadingView.setVisibility(View.GONE);
		mRetryView.setVisibility(View.GONE);
	}

	@Override
	protected void onFailure(VolleyError error) {
		this.mLoadingView.setVisibility(View.GONE);
		if (mAlbum == null) {
			mRetryView.setVisibility(View.VISIBLE);
		} else {
			this.mRetryView.setVisibility(View.GONE);
		}
		ToastUtils.ErrorToastNoNet(mContext);
	}

	/**
	 * 设置底部文字
	 * 
	 * @param position
	 */
	private void setbootmText(int position) {
		if (mAlbum.getAlbum().size() != 0) {
			String str = (position + 1) + "/" + mAlbum.getAlbum().size() + "　"
					+ mAlbum.getAlbum().get(position).getTitle();
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
			FontUtils.setTextViewFontSize(this, mTitleView,
					R.string.news_content_text_size, spUtil.getFontSizeRadix());
			mTitleView.setText(str);
		}
	}

	private class AlbumImageViewPagerAdapter extends PagerAdapter {
		@Override
		public int getCount() {
			if (mAlbum.getAlbum() != null) {
				return mAlbum.getAlbum().size();
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
			String picUrl = CommonUtils.doWebpUrl(mAlbum.getAlbum()
					.get(position).getImageurl());
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
					// Animation top_in = AnimationUtils.loadAnimation(
					// New_Activity_Content_PicSet.this, R.anim.top_in);
					// top_in.setAnimationListener(new AnimationListener() {
					// @Override
					// public void onAnimationStart(Animation animation) {
					// titleBarView.setVisibility(View.VISIBLE);
					// rLayout_bottom.setVisibility(View.VISIBLE);
					// vp_content.setVisibility(View.VISIBLE);
					//
					// }
					//
					// @Override
					// public void onAnimationRepeat(Animation animation) {
					// }
					//
					// @Override
					// public void onAnimationEnd(Animation animation) {
					//
					// }
					// });
					// Animation top_out = AnimationUtils.loadAnimation(
					// New_Activity_Content_PicSet.this, R.anim.top_out);
					// top_out.setAnimationListener(new AnimationListener() {
					// @Override
					// public void onAnimationStart(Animation animation) {
					//
					// }
					//
					// @Override
					// public void onAnimationRepeat(Animation animation) {
					// }
					//
					// @Override
					// public void onAnimationEnd(Animation animation) {
					// titleBarView.setVisibility(View.GONE);
					// rLayout_bottom.setVisibility(View.GONE);
					// vp_content.setVisibility(View.GONE);
					// }
					// });
					// Animation bottom_in = AnimationUtils.loadAnimation(
					// New_Activity_Content_PicSet.this, R.anim.bottom_in);
					// Animation bottom_out = AnimationUtils
					// .loadAnimation(New_Activity_Content_PicSet.this,
					// R.anim.bottom_out);
					isHide = !isHide;
					if (isHide) {
						// titleBarView.startAnimation(top_out);
						// rLayout_bottom.startAnimation(bottom_out);
						titleBarView.setVisibility(View.GONE);
						mBottomRootView.setVisibility(View.GONE);
						mTitleView.setVisibility(View.GONE);
					} else {
						// titleBarView.startAnimation(top_in);
						// rLayout_bottom.startAnimation(bottom_in);

						titleBarView.setVisibility(View.VISIBLE);
						mBottomRootView.setVisibility(View.VISIBLE);
						mTitleView.setVisibility(View.VISIBLE);
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
			// 初始化shareutil类
			shareUtil = new ShareUtil(mAlbum, mContext);
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
		clip.setText(mAlbum.getUrl());
		ToastUtils.Infotoast(this, "已将链接复制进黏贴板");
	}

	@Override
	public void changeFontSize() {
		// TODO Auto-generated method stub

		FontUtils.setTextViewFontSize(this, mTitleView,
				R.string.news_content_text_size, spUtil.getFontSizeRadix());
		FontUtils.chagneFontSizeGlobal();
	}

	@Override
	public void initNightView(boolean isFullScreen) {
		if (!spUtil.getIsDayMode())
			chage2Night();
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

}
