package com.kankan.kankanews.bean;

import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.kankan.kankanews.base.BaseBean;
import com.kankan.kankanews.exception.NetRequestException;

public class Reporter_NewsList extends BaseBean<Reporter_NewsList> {

	private LinkedList<Reporter_News> reporter_News_List;
	private Reporter_News reporter_News;

	@Override
	public JSONObject toJSON() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Reporter_NewsList parseJSON(JSONObject jsonObj)
			throws NetRequestException {

		checkJson(jsonObj);

		JSONArray result = jsonObj.optJSONArray("return_result");
		if (result != null && result.length() > 0) {
			reporter_News_List = new LinkedList<Reporter_News>();
			reporter_News = null;
			JSONObject optJsonObject;
			for (int i = 0; i < result.length(); i++) {
				optJsonObject = result.optJSONObject(i);
				reporter_News = new Reporter_News();
				reporter_News.parseJSON(optJsonObject);
				reporter_News_List.add(reporter_News);
			}
			return this;
		}else{
			return null;
		}
	}

	public LinkedList<Reporter_News> getReporter_News_List() {
		return reporter_News_List;
	}

	public void setReporter_News_List(LinkedList<Reporter_News> reporter_News_List) {
		this.reporter_News_List = reporter_News_List;
	}

	public Reporter_News getReporter_News() {
		return reporter_News;
	}

	public void setReporter_News(Reporter_News reporter_News) {
		this.reporter_News = reporter_News;
	}
	
}
