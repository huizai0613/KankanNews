package com.kankan.kankanews.bean;

import java.util.ArrayList;
import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.kankan.kankanews.base.BaseBean;
import com.kankan.kankanews.exception.NetRequestException;

public class MainFragmentBean extends BaseBean<MainFragmentBean> {

	private ArrayList<TopNews> mTopNewsList;
	private LinkedList<News> mNewsList;

	public ArrayList<TopNews> getmTopNewsList() {
		return mTopNewsList;
	}

	public void setmTopNewsList(ArrayList<TopNews> mTopNewsList) {
		this.mTopNewsList = mTopNewsList;
	}

	public LinkedList<News> getmNewsList() {
		return mNewsList;
	}

	public void setmNewsList(LinkedList<News> mNewsList) {
		this.mNewsList = mNewsList;
	}

	@Override
	public JSONObject toJSON() {
		return null;
	}

	@Override
	public MainFragmentBean parseJSON(JSONObject jsonObj) {
		JSONObject return_result = jsonObj.optJSONObject("return_result");

		JSONArray topJSONArray = return_result.optJSONArray("top");
		if (topJSONArray != null) {
			int topL = topJSONArray.length();
			JSONObject optJSONObject = null;
			TopNews topNews = null;
			mTopNewsList = new ArrayList<TopNews>();
			for (int i = 0; i < topL; i++) {
				optJSONObject = topJSONArray.optJSONObject(i);
				try {
					topNews = new TopNews();
					topNews.parseJSON(optJSONObject);
					mTopNewsList.add(topNews);
				} catch (NetRequestException e) {
					e.printStackTrace();
					continue;
				}
			}
		}
		JSONArray listJSONArray = return_result.optJSONArray("list");
		if (listJSONArray != null) {

			int listL = listJSONArray.length();
			JSONObject newsJSONObject = null;
			News mNews = null;
			mNewsList = new LinkedList<News>();
			for (int i = 0; i < listL; i++) {
				newsJSONObject = listJSONArray.optJSONObject(i);
				try {
					mNews = new News();
					mNews.parseJSON(newsJSONObject);
					mNewsList.add(mNews);
				} catch (NetRequestException e) {
					e.printStackTrace();
					continue;
				}
			}
		}
		return this;
	}
}
