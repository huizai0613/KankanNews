package com.kankan.kankanews.utils;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import com.kankanews.kankanxinwen.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.DisplayImageOptions.Builder;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

public class Options {
	/**
	 * 新闻列表中用到的图片加载配置
	 */
	public static DisplayImageOptions getBigImageOptions(Drawable drawable) {
		Builder displayer = new DisplayImageOptions.Builder()
		// 设置图片在下载期间显示的图片
		// 设置下载的图片是否缓存在内存中
				.cacheInMemory(false)
				// 设置下载的图片是否缓存在SD卡中
				.cacheOnDisc(true)
				// 保留Exif信息
				.considerExifParams(false)
				// 设置图片以如何的编码方式显示
				.imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
				// 设置图片的解码类型
				.bitmapConfig(Bitmap.Config.RGB_565)
				// .decodingOptions(android.graphics.BitmapFactory.Options
				// decodingOptions)//设置图片的解码配置
				.considerExifParams(true)
				// 设置图片下载前的延迟
				.delayBeforeLoading(0)// int
				// delayInMillis为你设置的延迟时间
				// 设置图片加入缓存前，对bitmap进行设置
				// .preProcessor(BitmapProcessor preProcessor)
				.resetViewBeforeLoading(false)// 设置图片在下载前是否重置，复位
				// .displayer(new RoundedBitmapDisplayer(20))//是否设置为圆角，弧度为多少
				.displayer(new FadeInBitmapDisplayer(0));// 淡入

		if (drawable != null) {
			displayer.showImageOnLoading(drawable)
			// 设置图片Uri为空或是错误的时候显示的图片
					.showImageForEmptyUri(drawable)
					// 设置图片加载/解码过程中错误时候显示的图片
					.showImageOnFail(drawable);
		} else {
			displayer.showImageOnLoading(R.drawable.default_news_display)
			// 设置图片Uri为空或是错误的时候显示的图片
					.showImageForEmptyUri(R.drawable.default_news_display)
					// 设置图片加载/解码过程中错误时候显示的图片
					.showImageOnFail(R.drawable.default_news_display);
		}

		DisplayImageOptions options = displayer.build();

		return options;
	}

	public static DisplayImageOptions getSmallImageOptions(boolean isMemory) {
		DisplayImageOptions options = new DisplayImageOptions.Builder()
		// 设置图片在下载期间显示的图片
				.showImageOnLoading(R.drawable.default_news_display)
				// 设置图片Uri为空或是错误的时候显示的图片
				.showImageForEmptyUri(R.drawable.default_news_display)
				// 设置图片加载/解码过程中错误时候显示的图片
				.showImageOnFail(R.drawable.default_news_display)
				// 设置下载的图片是否缓存在内存中
				.cacheInMemory(isMemory)
				// 设置下载的图片是否缓存在SD卡中
				.cacheOnDisc(true)
				// 保留Exif信息
				.considerExifParams(false)
				// 设置图片以如何的编码方式显示
				.imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
				// 设置图片的解码类型
				.bitmapConfig(Bitmap.Config.RGB_565)
				// .decodingOptions(android.graphics.BitmapFactory.Options
				// decodingOptions)//设置图片的解码配置
				.considerExifParams(true)
				// 设置图片下载前的延迟
				.delayBeforeLoading(0)// int
				// delayInMillis为你设置的延迟时间
				// 设置图片加入缓存前，对bitmap进行设置
				// .preProcessor(BitmapProcessor preProcessor)
				.resetViewBeforeLoading(false)// 设置图片在下载前是否重置，复位
				// .displayer(new RoundedBitmapDisplayer(20))//是否设置为圆角，弧度为多少
				.displayer(new FadeInBitmapDisplayer(0))// 淡入
				.build();
		return options;
	}

}
