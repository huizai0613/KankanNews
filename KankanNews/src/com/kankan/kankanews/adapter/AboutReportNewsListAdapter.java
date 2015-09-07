package com.kankan.kankanews.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.kankan.kankanews.bean.RevelationsNew;
import com.kankan.kankanews.ui.RevelationsBreakNewsMoreActivity;
import com.kankan.kankanews.ui.view.MyTextView;
import com.kankan.kankanews.utils.CommonUtils;
import com.kankan.kankanews.utils.FontUtils;
import com.kankan.kankanews.utils.ImgUtils;
import com.kankanews.kankanxinwen.R;

public class AboutReportNewsListAdapter extends BaseAdapter {

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		return null;
	}
//	private List<RevelationsNew> revelationsNew;
//	private Context context;
//	private LayoutInflater inflater;
//
//	public AboutReportNewsListAdapter(Context context,
//			List<RevelationsNew> revelationsNew) {
//		this.revelationsNew = revelationsNew;
//		this.context = context;
//		inflater = LayoutInflater.from(context);
//	}
//
//	@Override
//	public int getCount() {
//		// TODO Auto-generated method stub
//		return revelationsNew.size();
//	}
//
//	@Override
//	public Object getItem(int position) {
//		// TODO Auto-generated method stub
//		return revelationsNew.get(position);
//	}
//
//	@Override
//	public long getItemId(int position) {
//		// TODO Auto-generated method stub
//		return position;
//	}
//
//	@Override
//	public View getView(final int position, View convertView, ViewGroup parent) {
//		// TODO Auto-generated method stub
//		if (convertView == null) {
//			convertView = inflater.inflate(
//					R.layout.item_revelations_breaknews_about_report, null);
//			aboutReportHolder = new BreaknewsAboutReportHolder();
//			aboutReportHolder.newsTitilePic = (ImageView) convertView
//					.findViewById(R.id.about_report_news_titlepic);
//			aboutReportHolder.newsTitile = (MyTextView) convertView
//					.findViewById(R.id.about_report_news_title);
//			convertView.setTag(aboutReportHolder);
//		} else {
//			aboutReportHolder = (BreaknewsAboutReportHolder) convertView
//					.getTag();
//		}
//		ImgUtils.imageLoader.displayImage(CommonUtils.doWebpUrl(revelationsNew
//				.get(position).getTitlepic()), aboutReportHolder.newsTitilePic,
//				ImgUtils.homeImageOptions);
//		aboutReportHolder.newsTitile.setText(revelationsNew.get(position)
//				.getTitle());
//
//		FontUtils.setTextViewFontSize(context,
//				aboutReportHolder.newsTitile,
//				R.string.home_news_title_text_size, spUtil.getFontSizeRadix());
//		convertView.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				openNews(revelationsNew.get(position));
//			}
//		});
//		return convertView;
//	}
}
