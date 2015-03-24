package com.kankan.kankanews.bean;

import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.kankan.kankanews.base.BaseBean;
import com.kankan.kankanews.exception.NetRequestException;

public class Hot_NewsList extends BaseBean<Hot_NewsList> {

	private Hot_News hot_News;
	private LinkedList<Hot_News> hot_News_list;

	@Override
	public JSONObject toJSON() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Hot_NewsList parseJSON(JSONObject jsonObj)
			throws NetRequestException {
		
		checkJson(jsonObj);

		JSONObject jsonObject = jsonObj.optJSONObject("return_result");
		JSONArray list = jsonObject.optJSONArray("list");

		if (list != null && list.length() > 0) {
			hot_News_list = new LinkedList<Hot_News>();
			hot_News = null;
			JSONObject optJsonObject;
			for (int i = 0; i < list.length(); i++) {
				optJsonObject = list.optJSONObject(i);
				hot_News = new Hot_News();
				hot_News.parseJSON(optJsonObject);
				hot_News_list.add(hot_News);
			}
		}

		return this;
	}

	public Hot_News getHot_News() {
		return hot_News;
	}

	public void setHot_News(Hot_News hot_News) {
		this.hot_News = hot_News;
	}

	public LinkedList<Hot_News> getHot_Newss() {
		return hot_News_list;
	}

	public void setHot_Newss(LinkedList<Hot_News> hot_News_list) {
		this.hot_News_list = hot_News_list;
	}

}
