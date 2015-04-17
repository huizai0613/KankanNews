package com.kankan.kankanews.ui.item;

import java.io.ByteArrayOutputStream;
import java.io.File;

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
import com.kankan.kankanews.exception.NetRequestException;
import com.kankan.kankanews.net.ItnetUtils;
import com.kankan.kankanews.sina.AccessTokenKeeper;
import com.kankan.kankanews.sina.Constants;
import com.kankan.kankanews.ui.view.CustomShareBoard;
import com.kankan.kankanews.utils.CommonUtils;
import com.kankan.kankanews.utils.Options;
import com.kankan.kankanews.utils.PixelUtil;
import com.kankan.kankanews.utils.ShareUtil;
import com.kankan.kankanews.utils.TimeUtil;
import com.kankan.kankanews.utils.ToastUtils;
import com.kankanews.kankanxinwen.R;
import com.lidroid.xutils.exception.DbException;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.constant.WBConstants;
import com.sina.weibo.sdk.exception.WeiboException;
import com.xunao.view.photoview.PhotoView;
import com.xunao.view.photoview.PhotoViewAttacher.OnPhotoTapListener;

public class New_Activity_Content_PicSet extends BaseVideoActivity implements
		OnClickListener, OnPageChangeListener, IWeiboHandler.Response {

	/** 微博微博分享接口实例 */
	private IWeiboShareAPI mWeiboShareAPI = null;

	private String mid;
	private String type;
	private String titleurl;
	private String newstime;
	private String titlepiclist;
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

		// mShareType = getIntent().getIntExtra(KEY_SHARE_TYPE, SHARE_CLIENT);
		// 创建微博分享接口实例
		mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(this, Constants.APP_KEY);
		// 注册第三方应用到微博客户端中，注册成功后该应用将显示在微博的应用列表中。
		// 但该附件栏集成分享权限需要合作申请，详情请查看 Demo 提示
		// NOTE：请务必提前注册，即界面初始化的时候或是应用程序初始化时，进行注册
		mWeiboShareAPI.registerApp();
		// 当 Activity 被重新初始化时（该 Activity 处于后台时，可能会由于内存不足被杀掉了），
		// 需要调用 {@link IWeiboShareAPI#handleWeiboResponse} 来接收微博客户端返回的数据。
		// 执行成功，返回 true，并调用 {@link IWeiboHandler.Response#onResponse}；
		// 失败返回 false，不调用上述回调
		if (savedInstanceState != null) {
			mWeiboShareAPI.handleWeiboResponse(getIntent(), this);
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
		titlepiclist = intent.getStringExtra("titlepiclist");
		titlelist = intent.getStringExtra("titlelist");
		// 存储数据
		new_news = new New_News();
		new_news.setId(mid);
		new_news.setType(type);
		new_news.setTitleurl(titleurl);
		new_news.setNewstime(newstime);
		new_news.setTitlepiclist(titlepiclist);
		new_news.setTitlelist(titlelist);

		// 提交点击
		ItnetUtils.getInstance(mContext).addNewNewsClickData("tjid=" + mid);

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
			String picUrl = parseImagegroups[position][1];
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
			imageLoader.displayImage(picUrl, photoView,
					Options.getSmallImageOptions(false));
			view.setTag(photoView);
			container.addView(view);
			return view;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {

			View v = (View) object;
			PhotoView tag2 = (PhotoView) v.getTag();

			if (tag2 != null) {
				Drawable drawable2 = tag2.getDrawable();

				if (drawable2 instanceof BitmapDrawable) {
					BitmapDrawable drawable = (BitmapDrawable) tag2
							.getDrawable();
					if (drawable != null) {
						Bitmap bitmap = drawable.getBitmap();
						if (bitmap != null) {
							bitmap.recycle();
							bitmap = null;
						}
						drawable = null;
					}
				}
			}
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
			CustomShareBoard shareBoard = new CustomShareBoard(this, shareUtil, this);
			shareBoard.setAnimationStyle(R.style.popwin_anim_style);
			shareBoard.showAtLocation(mContext.getWindow().getDecorView(),
					Gravity.BOTTOM, 0, 0);

			break;

		}
	}

	public void sendSingleMessage() {
		// 1. 初始化微博的分享消息
		WeiboMultiMessage weiboMultiMessage = new WeiboMultiMessage();
		// 创建媒体消息
		// weiboMultiMessage.mediaObject = getVideoObj();
		TextObject textObject = new TextObject();
		textObject.text = new_news.getTitlelist() + "-看看新闻 "
				+ new_news.getTitleurl() + " （分享自@看看新闻网） ";
		ImageObject imageObject = new ImageObject();
		imageObject.setImageObject(getThumbBitmap());
		weiboMultiMessage.textObject = textObject;
		weiboMultiMessage.imageObject = imageObject;
		// 2. 初始化从第三方到微博的消息请求
		SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
		// 用transaction唯一标识一个请求
		request.transaction = String.valueOf(System.currentTimeMillis());
		request.multiMessage = weiboMultiMessage;

		AuthInfo authInfo = new AuthInfo(this, Constants.APP_KEY,
				Constants.REDIRECT_URL, Constants.SCOPE);
		Oauth2AccessToken accessToken = AccessTokenKeeper
				.readAccessToken(getApplicationContext());
		String token = "";
		if (accessToken != null) {
			token = accessToken.getToken();
		}
		mWeiboShareAPI.sendRequest(this, request, authInfo, token,
				new WeiboAuthListener() {

					@Override
					public void onWeiboException(WeiboException arg0) {
					}

					@Override
					public void onComplete(Bundle bundle) {
						Oauth2AccessToken newToken = Oauth2AccessToken
								.parseAccessToken(bundle);
						AccessTokenKeeper.writeAccessToken(
								getApplicationContext(), newToken);
						ToastUtils.Infotoast(New_Activity_Content_PicSet.this, "分享成功");
					}

					@Override
					public void onCancel() {
						ToastUtils.Infotoast(New_Activity_Content_PicSet.this, "分享取消");
					}
				});
	}

	/**
	 * 获取当前新闻的缩略图对应的 Bitmap。
	 */
	public Bitmap getThumbBitmap() {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		options.inPreferredConfig = Bitmap.Config.RGB_565;
		// Bitmap decodeFile = BitmapFactory
		// .decodeFile(
		// CommonUtils.getImageCachePath(mContext)
		// .getAbsolutePath()
		// + "/"
		// + String.valueOf(new_news.getTitlepic()
		// .hashCode()), options);
		// int byteCount = decodeFile.getByteCount();
		// int height = decodeFile.getHeight();
		// long memeory=byteCount*height;
		// int width = options.outWidth;
		// int height = options.outHeight;
		//
		// if (width > height) {
		// options.inSampleSize = width / 400;
		// } else {
		// options.inSampleSize = height / 400;
		// }
		// options.inJustDecodeBounds = false;

		File file = new File(
				CommonUtils.getImageCachePath(mContext),
				CommonUtils
						.generate(new_news.getTitlepiclist().split("::::::")[0]));
		Bitmap decodeFile = BitmapFactory.decodeFile(file.getAbsolutePath());

		if (decodeFile == null) {
			decodeFile = BitmapFactory.decodeFile(CommonUtils
					.getImageCachePath(mContext)
					+ "/"
					+ "big_"
					+ CommonUtils.generate(new_news.getTitlepiclist().split(
							"::::::")[0]));
		}

		// Bitmap decodeFile = BitmapFactory.decodeFile(CommonUtils
		// .getImageCachePath(mContext)
		// + "/"
		// + CommonUtils.UrlToFileName(new_news.getTitlepiclist()));

		int byteCount = decodeFile.getRowBytes();

		int height2 = decodeFile.getHeight();

		long mem = height2 * byteCount;

		ByteArrayOutputStream bao = new ByteArrayOutputStream();

		if (mem > 100 * 1024 * 8) {
			decodeFile.compress(CompressFormat.JPEG, 80, bao);
		} else if (mem < 100 * 1024 * 8 && mem > 80 * 1024 * 8) {
			decodeFile.compress(CompressFormat.JPEG, 90, bao);
		} else {
			decodeFile.compress(CompressFormat.JPEG, 100, bao);
		}
		if (decodeFile != null && !decodeFile.isRecycled()) {
			decodeFile.recycle();
		}
		byte[] byteArray = bao.toByteArray();
		Bitmap decodeByteArray = BitmapFactory.decodeByteArray(byteArray, 0,
				byteArray.length);

		return decodeByteArray;
	}

	@Override
	public void onBackPressed() {
		AnimFinsh();
	}

	@Override
	public void onResponse(BaseResponse arg0) {
		switch (arg0.errCode) {
		case WBConstants.ErrorCode.ERR_OK:
			ToastUtils.Infotoast(mContext, "分享成功");
			break;
		case WBConstants.ErrorCode.ERR_CANCEL:
//			ToastUtils.Infotoast(mContext, "分享取消");
			break;
		case WBConstants.ErrorCode.ERR_FAIL:
			ToastUtils.Infotoast(mContext, "分享失败");
			break;
		}
	}

	@Override
	public void refresh() {
		if (CommonUtils.isNetworkAvailable(mContext))
			initNetData();
	}

}
