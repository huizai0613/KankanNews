package com.kankan.kankanews.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.kankan.kankanews.base.BaseActivity;
import com.kankan.kankanews.bean.interfaz.CanSharedObject;
import com.kankan.kankanews.config.AndroidConfig;
import com.kankanews.kankanxinwen.R;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.bean.StatusCode;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;
import com.umeng.socialize.media.MailShareContent;
import com.umeng.socialize.media.QQShareContent;
import com.umeng.socialize.media.SinaShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMVideo;
import com.umeng.socialize.sso.EmailHandler;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;

public class ShareUtil {

	private final UMSocialService mController = UMServiceFactory
			.getUMSocialService("com.umeng.share");
	public Activity activity;
	private CanSharedObject shareObj;

	public ShareUtil(CanSharedObject shareObj, Activity activity) {
		this.shareObj = shareObj;
		this.activity = activity;

		// 关掉默认的提示
		mController.getConfig().closeToast();

		configPlatforms();
		setShareContent();
	}

	public UMSocialService getmController() {
		return mController;
	}

	private Drawable drawable;

	public void setDrawable(Drawable drawable) {
		this.drawable = drawable;
	}

	/**
	 * 直接分享，底层可编辑分享接口。如果分享的平台是新浪、腾讯微博、豆瓣、人人，则直接分享，无任何界面弹出； 其它平台分别启动客户端分享</br>
	 */
	public void directShare(SHARE_MEDIA mPlatform) {

		mController.getConfig().setPlatforms(SHARE_MEDIA.WEIXIN,
				SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE,
				SHARE_MEDIA.SINA, SHARE_MEDIA.TENCENT, SHARE_MEDIA.DOUBAN,
				SHARE_MEDIA.RENREN);

		mController.postShare(activity, mPlatform, new SnsPostListener() {

			@Override
			public void onStart() {
			}

			@Override
			public void onComplete(SHARE_MEDIA platform, int eCode,
					SocializeEntity entity) {
				// String showText = "分享成功";
				DebugLog.e("分享成功");
				((BaseActivity) activity).Commit_Share(platform);

				if (platform.equals(SHARE_MEDIA.EMAIL)) {
					return;
				}

				if (eCode == StatusCode.ST_CODE_SUCCESSED) {
					ToastUtils.Infotoast(activity, "分享成功");
				} else if (eCode == StatusCode.ST_CODE_ERROR_CANCEL) {
					// ToastUtils.Infotoast(activity, "分享取消");
				} else {
					ToastUtils.Infotoast(activity, "分享失败");
				}
				// Toast.makeText(activity, showText,
				// Toast.LENGTH_SHORT).show();
			}
		});
	}

	/**
	 * 根据不同的平台设置不同的分享内容</br>
	 */
	private void setShareContent() {

		// 配置SSO
		mController.getConfig().setSsoHandler(new SinaSsoHandler());
		// mController.getConfig().setSsoHandler(new TencentWBSsoHandler());

		// QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(activity,
		// "1103461267", "3FVq3JjOmzUFb1jE");
		// qZoneSsoHandler.addToSocialSDK();
		// mController.setShareContent("友盟社会化组件（SDK）让移动应用快速整合社交分享功能");

		// UMImage localImage = new UMImage(activity,(
		// (BitmapDrawable)drawable).getBitmap());
		// UMImage SharePic = new UMImage(activity, content_News.getTitlepic());
		UMImage titlePic = null;
		if (null == shareObj.getSharedPic()
				|| "".equals(shareObj.getSharedPic().trim())) {
			titlePic = new UMImage(activity, AndroidConfig.Shared_Icon);
		} else {
			titlePic = new UMImage(activity, shareObj.getSharedPic());
		}
		// UMImage resImage = new UMImage(activity, R.drawable.icon);

		String sharedUrl = handleUrl(shareObj.getTitleurl());

		String sharedIntro = (shareObj.getShareIntro() == null || shareObj
				.getShareIntro().trim().equals("")) ? shareObj.getShareTitle()
				: shareObj.getShareIntro();

		// 视频分享
		UMVideo video = new UMVideo(shareObj.getTitleurl());
		video.setMediaUrl(shareObj.getTitleurl());
		video.setThumb(shareObj.getTitleurl());
		video.setTargetUrl(shareObj.getTitleurl());
		video.setTitle(shareObj.getTitle());
		video.setThumb(titlePic);

		// 微信
		WeiXinShareContent weixinContent = new WeiXinShareContent();
		// weixinContent.setShareContent(shareObj.getTitle() == null ? shareObj
		// .getTitlelist() : shareObj.getTitle());
		weixinContent.setShareContent(sharedIntro == null ? shareObj.getTitle()
				: sharedIntro);
		weixinContent.setTitle(shareObj.getShareTitle());
		weixinContent.setTargetUrl(sharedUrl);
		weixinContent.setShareMedia(titlePic);
		mController.setShareMedia(weixinContent);

		// 设置朋友圈分享的内容
		CircleShareContent circleMedia = new CircleShareContent();
		circleMedia.setShareContent(sharedIntro);
		circleMedia.setTitle(shareObj.getShareTitle());
		circleMedia.setShareImage(titlePic);
		// circleMedia.setShareMedia(uMusic);
		// circleMedia.setShareMedia(video);
		circleMedia.setTargetUrl(sharedUrl);
		mController.setShareMedia(circleMedia);

		// 设置QQ空间分享内容
		// QZoneShareContent qzone = new QZoneShareContent();
		// qzone.setShareContent(content_News.getM_url());
		// // qzone.setTargetUrl("http://www.umeng.com/social");
		// qzone.setTitle(content_News.getTitle());
		// qzone.setShareImage(SharePic);
		// mController.setShareMedia(qzone);

		// video.setThumb(new UMImage(activity, BitmapFactory.decodeResource(
		// activity.getResources(), R.drawable.ic_launcher)));

		// 设置qq分享内容
		QQShareContent qqShareContent = new QQShareContent();
		qqShareContent.setShareContent(sharedIntro);
		qqShareContent.setTitle(shareObj.getShareTitle());
		qqShareContent.setShareImage(titlePic);
		// qqShareContent.setShareMusic(uMusic);
		// qqShareContent.setShareVideo(video);
		qqShareContent.setTargetUrl(sharedUrl);
		mController.setShareMedia(qqShareContent);

		// 视频分享
		// UMVideo umVideo = new UMVideo(
		// "http://v.youku.com/v_show/id_XNTc0ODM4OTM2.html");
		// umVideo.setThumb("http://www.umeng.com/images/pic/home/social/img-1.png");
		// umVideo.setTitle("友盟社会化组件视频");

		// 腾讯微博
		// TencentWbShareContent tencent = new TencentWbShareContent();
		// tencent.setShareContent("来自友盟社会化组件（SDK）让移动应用快速整合社交分享功能，腾讯微博");
		// // 设置tencent分享内容
		// mController.setShareMedia(tencent);

		// 设置邮件分享内容， 如果需要分享图片则只支持本地图片
		MailShareContent mail = new MailShareContent();
		if (drawable != null) {
			UMImage maillocalImage = new UMImage(activity,
					((BitmapDrawable) drawable).getBitmap());
			mail.setShareImage(maillocalImage);
		}
		// UMImage maillocalImage = new
		// UMImage(activity,R.drawable.ic_launcher);
		mail.setTitle(shareObj.getShareTitle());
		mail.setShareContent(sharedUrl);
		// 设置tencent分享内容
		mController.setShareMedia(mail);

		// sina
		SinaShareContent sinaContent = new SinaShareContent(titlePic);
		// sinaContent.setShareMedia(video);
		sinaContent.setShareImage(titlePic);
		// sinaContent.setShareImage(new UMImage(activity,
		// "http://static.statickksmg.com/image/2015/05/07/ad7b6608ed69b909356b605db8924891.jpg"));
		// TODO
		sinaContent.setShareContent(sharedIntro + "-看看新闻 " + sharedUrl
				+ " （分享自@看看新闻网） ");
		mController.setShareMedia(sinaContent);
	}

	/**
	 * 添加所有的平台</br>
	 */
	// private void addCustomPlatforms() {
	// // 添加微信平台
	// addWXPlatform();
	// // 添加QQ平台
	// addQQQZonePlatform();
	// // 添加email平台
	// addEmail();
	//
	// mController.getConfig().setPlatforms(SHARE_MEDIA.WEIXIN,
	// SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE,
	// SHARE_MEDIA.SINA, SHARE_MEDIA.TENCENT, SHARE_MEDIA.DOUBAN,
	// SHARE_MEDIA.RENREN, SHARE_MEDIA.EMAIL, SHARE_MEDIA.EVERNOTE,
	// SHARE_MEDIA.FACEBOOK, SHARE_MEDIA.GOOGLEPLUS,
	// SHARE_MEDIA.INSTAGRAM, SHARE_MEDIA.LAIWANG,
	// SHARE_MEDIA.LAIWANG_DYNAMIC, SHARE_MEDIA.LINKEDIN,
	// SHARE_MEDIA.PINTEREST, SHARE_MEDIA.POCKET, SHARE_MEDIA.SMS,
	// SHARE_MEDIA.TWITTER, SHARE_MEDIA.YIXIN,
	// SHARE_MEDIA.YIXIN_CIRCLE, SHARE_MEDIA.YNOTE);
	// mController.openShare(activity, false);
	// }

	private String handleUrl(String srcUrl) {
		if (srcUrl.contains("?")) {
			return srcUrl + "&utm_source=kankanapp";
		}
		return srcUrl + "?utm_source=kankanapp";
	}

	/**
	 * 配置分享平台参数</br>
	 */
	private void configPlatforms() {
		// 添加新浪SSO授权
		mController.getConfig().setSsoHandler(new SinaSsoHandler());
		// 添加腾讯微博SSO授权
		// mController.getConfig().setSsoHandler(new TencentWBSsoHandler());
		// 添加人人网SSO授权
		// RenrenSsoHandler renrenSsoHandler = new RenrenSsoHandler(activity,
		// "201874", "28401c0964f04a72a14c812d6132fcef",
		// "3bf66e42db1e4fa9829b955cc300b737");
		// mController.getConfig().setSsoHandler(renrenSsoHandler);

		// 添加QQ、QZone平台
		addQQQZonePlatform();

		// 添加微信、微信朋友圈平台
		addWXPlatform();

		addEmail();
	}

	/**
	 * @功能描述 : 添加QQ平台支持 QQ分享的内容， 包含四种类型， 即单纯的文字、图片、音乐、视频. 参数说明 : title, summary,
	 *       image url中必须至少设置一个, targetUrl必须设置,网页地址必须以"http://"开头 . title :
	 *       要分享标题 summary : 要分享的文字概述 image url : 图片地址 [以上三个参数至少填写一个] targetUrl
	 *       : 用户点击该分享时跳转到的目标地址 [必填] ( 若不填写则默认设置为友盟主页 )
	 * @return
	 */
	private void addQQQZonePlatform() {
		String appId = "1103880827";
		String appKey = "y99xCuBUAIJ0IB8x";
		// 添加QQ支持, 并且设置QQ分享内容的target url
		UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(activity, appId,
				appKey);
		// qqSsoHandler.setTargetUrl("http://www.umeng.com");
		qqSsoHandler.addToSocialSDK();

		// 添加QZone平台
		QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(activity, appId,
				appKey);
		qZoneSsoHandler.addToSocialSDK();

		// 添加email
	}

	/**
	 * @功能描述 : 添加微信平台分享
	 * @return
	 */
	private void addWXPlatform() {
		// 注意：在微信授权的时候，必须传递appSecret
		// wx967daebe835fbeac是你在微信开发平台注册应用的AppID, 这里需要替换成你注册的AppID
		String appId = "wx3f0cb92ed4db29b6";
		String appSecret = "4ead2f0961bd6952570453d03562200e";
		// 添加微信平台
		UMWXHandler wxHandler = new UMWXHandler(activity, appId, appSecret);
		wxHandler.addToSocialSDK();

		// 支持微信朋友圈
		UMWXHandler wxCircleHandler = new UMWXHandler(activity, appId,
				appSecret);
		wxCircleHandler.setToCircle(true);
		wxCircleHandler.addToSocialSDK();
	}

	/**
	 * 添加Email平台</br>
	 */
	private void addEmail() {
		// 添加email
		EmailHandler emailHandler = new EmailHandler();
		emailHandler.addToSocialSDK();
	}

	/*
	 * 一键分享
	 */
	public void shareOnKey() {
		mController.getConfig().setPlatforms(SHARE_MEDIA.WEIXIN,
				SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.QQ, SHARE_MEDIA.SINA,
				SHARE_MEDIA.EMAIL);
		// mController.openShare(activity, false);

		mController.openShare(activity, new SnsPostListener() {
			@Override
			public void onStart() {

			}

			@Override
			public void onComplete(SHARE_MEDIA platform, int eCode,
					SocializeEntity entity) {
				// String name = platform.name();
				// String showText = "分享成功";
				if (eCode != StatusCode.ST_CODE_SUCCESSED) {
					// showText = "分享失败 [" + eCode + "]";
				}
				// Toast.makeText(activity, showText,
				// Toast.LENGTH_SHORT).show();
			}
		});
	}

}
