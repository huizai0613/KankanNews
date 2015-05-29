/*
 * Copyright 2014 trinea.cn All right reserved. This software is the confidential and proprietary information of
 * trinea.cn ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with trinea.cn.
 */
package com.kankan.kankanews.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.ant.liao.GifView;
import com.ant.liao.GifView.GifImageType;
import com.kankan.kankanews.base.BaseFragment;
import com.kankan.kankanews.bean.New_News_Top;
import com.kankan.kankanews.ui.fragment.New_LivePlayFragment;
import com.kankan.kankanews.ui.item.New_Activity_Content_PicSet;
import com.kankan.kankanews.ui.item.New_Activity_Content_Video;
import com.kankan.kankanews.ui.item.New_Activity_Content_Web;
import com.kankan.kankanews.ui.view.AutoImageTag;
import com.kankan.kankanews.utils.CommonUtils;
import com.kankan.kankanews.utils.ImgUtils;
import com.kankanews.kankanxinwen.R;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * ImagePagerAdapter
 * 
 * @author <a href="http://www.trinea.cn" target="_blank">Trinea</a> 2014-2-23
 */
public class ImagePagerAdapter extends RecyclingPagerAdapter {

	private Context context;
	private List<AutoImageTag> imageIdList;

	private int size;
	private boolean isInfiniteLoop;
	private ImageLoader imageLoader;
	private BaseFragment fragment;
	private ArrayList<New_News_Top> getmTopNewsList;
	LayoutInflater inflater;

	public ImagePagerAdapter(Context context, BaseFragment fragment,
			List<AutoImageTag> imageIdList,
			ArrayList<New_News_Top> getmTopNewsList) {
		this.context = context;
		inflater = LayoutInflater.from(context);
		this.getmTopNewsList = getmTopNewsList;
		this.fragment = fragment;
		this.imageIdList = imageIdList;
		this.size = imageIdList.size();
		isInfiniteLoop = false;
		imageLoader = ImageLoader.getInstance();
	}

	@Override
	public int getCount() {
		// Infinite loop
		return isInfiniteLoop ? Integer.MAX_VALUE : imageIdList.size();
	}

	/**
	 * get really position
	 * 
	 * @param position
	 * @return
	 */
	private int getPosition(int position) {
		return isInfiniteLoop ? position % size : position;
	}

	@Override
	public int getItemViewType(int position) {
		return CommonUtils.UrlToFileFormat(
				imageIdList.get(getPosition(position)).getUrlPath())
				.equalsIgnoreCase("gif") ? 1 : 0;
	}

	@Override
	public int getViewTypeCount() {
		// TODO Auto-generated method stub
		return 2;
	}

	@Override
	public View getView(final int position, View view, ViewGroup container) {
		if (view == null) {
			if (getItemViewType(position) == 1) {// gif图片
				GifView gifView = new GifView(context);
				view = gifView;
			} else {
				ImageView imageView = new ImageView(context);
				imageView.setScaleType(ScaleType.CENTER_CROP);
				view = imageView;
				// }
			}
		}

		if (getItemViewType(position) == 1) {// gif图片
			GifView gifView = (GifView) view;
			gifView.setGifImageType(GifImageType.COVER);
			gifView.setShowDimension(fragment.mActivity.topNewW,
					(int) (fragment.mActivity.topNewW / 1.7));
			gifView.setTag(R.string.viewwidth,
					(int) (fragment.mActivity.topNewW / 1.7));

			CommonUtils.zoomImageGIF(imageLoader,
					imageIdList.get(getPosition(position)).getUrlPath(),
					gifView, context);

			gifView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					New_News_Top news = getmTopNewsList
							.get(getPosition(position));
					switch (Integer.valueOf(news.getType()) % 10) {
					// 视频
					case 1:
						fragment.mActivity.startAnimActivityByParameter(
								New_Activity_Content_Video.class,
								news.getMid(), news.getType(),
								news.getTitleurl(), news.getNewstime(),
								news.getTitle(), news.getTitlepic(),
								news.getSharedPic());
						break;
					// 图集
					case 2:
						fragment.mActivity.startAnimActivityByParameter(
								New_Activity_Content_PicSet.class,
								news.getMid(), news.getType(),
								news.getTitleurl(), news.getNewstime(),
								news.getTitle(), news.getTitlepic(),
								news.getSharedPic());
						break;
					case 6:
						New_LivePlayFragment f = (New_LivePlayFragment) fragment.mActivity.fragments
								.get(1);
						f.setSelectPlay(true);
						f.setSelectPlayID(Integer.parseInt(news.getZtid()));
						fragment.mActivity.touchTab(fragment.mActivity.tab_two);
						break;
					// 其他
					default:
						fragment.mActivity.startAnimActivityByParameter(
								New_Activity_Content_Web.class, news.getMid(),
								news.getType(), news.getTitleurl(),
								news.getNewstime(), news.getTitle(),
								news.getTitlepic(), news.getSharedPic());
						break;
					}
				}

			});

		} else {
			ImageView v = (ImageView) view;

			v.setTag(R.string.viewwidth,
					(int) (fragment.mActivity.topNewW / 1.7));
			v.setTag(R.string.isTop, true);
			ImgUtils.imageLoader.displayImage(
					imageIdList.get(getPosition(position)).getUrlPath()
							.split("::::::")[0], v, ImgUtils.homeImageOptions);
//			CommonUtils.zoomImage(
//					imageLoader,
//					imageIdList.get(getPosition(position)).getUrlPath()
//							.split("::::::")[0], v, context);

			v.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					New_News_Top news = getmTopNewsList
							.get(getPosition(position));
					switch (Integer.valueOf(news.getType()) % 10) {
					// 视频
					case 1:
						fragment.mActivity.startAnimActivityByParameter(
								New_Activity_Content_Video.class,
								news.getMid(), news.getType(),
								news.getTitleurl(), news.getNewstime(),
								news.getTitle(), news.getTitlepic(),
								news.getSharedPic());
						break;
					// 图集
					case 2:
						fragment.mActivity.startAnimActivityByParameter(
								New_Activity_Content_PicSet.class,
								news.getMid(), news.getType(),
								news.getTitleurl(), news.getNewstime(),
								news.getTitle(), news.getTitlepic(),
								news.getSharedPic());
						break;
					case 6:
						New_LivePlayFragment f = (New_LivePlayFragment) fragment.mActivity.fragments
								.get(1);
						f.setSelectPlay(true);
						f.setSelectPlayID(Integer.parseInt(news.getZtid()));
						fragment.mActivity.touchTab(fragment.mActivity.tab_two);
						break;
					// 其他
					default:
						fragment.mActivity.startAnimActivityByParameter(
								New_Activity_Content_Web.class, news.getMid(),
								news.getType(), news.getTitleurl(),
								news.getNewstime(), news.getTitle(),
								news.getTitlepic(), news.getSharedPic());
						break;
					}
				}

			});

		}

		return view;
	}

	/**
	 * @return the isInfiniteLoop
	 */
	public boolean isInfiniteLoop() {
		return isInfiniteLoop;
	}

	/**
	 * @param isInfiniteLoop
	 *            the isInfiniteLoop to set
	 */
	public ImagePagerAdapter setInfiniteLoop(boolean isInfiniteLoop) {
		this.isInfiniteLoop = isInfiniteLoop;
		return this;
	}
}
