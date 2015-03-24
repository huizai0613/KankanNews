package com.kankan.kankanews.bean;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.kankan.kankanews.base.BaseBean;
import com.kankan.kankanews.exception.NetRequestException;

public class Content_New_List extends BaseBean<Content_New_List> {

	private Content_News mContent_News;
	private ArrayList<Content_News> mContent_Newss;

	@Override
	public JSONObject toJSON() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Content_New_List parseJSON(JSONObject jsonObj)
			throws NetRequestException {

		checkJson(jsonObj);

		JSONArray jsonArray = jsonObj.optJSONArray("return_result");

		if (jsonArray != null && jsonArray.length() > 0) {
			mContent_Newss = new ArrayList<Content_News>();
			mContent_News = null;
			JSONObject jsonObject;
			for (int i = 0; i < jsonArray.length(); i++) {
				jsonObject = jsonArray.optJSONObject(i);
				mContent_News = new Content_News();
				mContent_News.parseJSONAll(jsonObject);
				mContent_Newss.add(mContent_News);
			}
			return this;
		}
		return null;
	}

	public Content_News getmContent_News() {
		return mContent_News;
	}

	public void setmContent_News(Content_News mContent_News) {
		this.mContent_News = mContent_News;
	}

	public ArrayList<Content_News> getmContent_Newss() {
		return mContent_Newss;
	}

	public void setmContent_Newss(ArrayList<Content_News> mContent_Newss) {
		this.mContent_Newss = mContent_Newss;
	}

}
