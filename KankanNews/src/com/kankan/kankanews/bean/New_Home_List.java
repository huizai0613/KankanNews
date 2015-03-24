package com.kankan.kankanews.bean;

import java.util.ArrayList;
import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.kankan.kankanews.base.BaseBean;
import com.kankan.kankanews.exception.NetRequestException;

public class New_Home_List extends BaseBean<New_Home_List> {

	private String classID;
	private ArrayList<New_News_Top> top_list;
	private LinkedList<New_News_Home> home_list;
	private New_News_Top new_news_top;
	private New_News_Home new_news_home;

	public String getClassID() {
		return classID;
	}

	public void setClassID(String classID) {
		this.classID = classID;
	}

	@Override
	public JSONObject toJSON() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public New_Home_List parseJSON(JSONObject jsonObj)
			throws NetRequestException {

		// checkJson(jsonObj);

		JSONArray top_json = jsonObj.optJSONArray("top");
		JSONArray home_json = jsonObj.optJSONArray("list");

		top_list = new ArrayList<New_News_Top>();
		if (top_json != null && top_json.length() > 0) {
			new_news_top = null;
			JSONObject jsonObject;
			for (int i = 0; i < top_json.length(); i++) {
				jsonObject = top_json.optJSONObject(i);
				new_news_top = new New_News_Top();
				new_news_top.parseJSON(jsonObject);
				new_news_top.setClassid(classID);
				top_list.add(new_news_top);
			}
		}

		home_list = new LinkedList<New_News_Home>();
		if (home_json != null && home_json.length() > 0) {
			new_news_home = null;
			JSONObject jsonObject;
			for (int i = 0; i < home_json.length(); i++) {
				jsonObject = home_json.optJSONObject(i);
				new_news_home = new New_News_Home();
				new_news_home.parseJSON(jsonObject);
				new_news_home.setClassid(classID);
				home_list.add(new_news_home);
			}
		}

		return this;
	}

	public ArrayList<New_News_Top> getTop_list() {
		return top_list;
	}

	public void setTop_list(ArrayList<New_News_Top> top_list) {
		this.top_list = top_list;
	}

	public LinkedList<New_News_Home> getHome_list() {
		return home_list;
	}

	public void setHome_list(LinkedList<New_News_Home> home_list) {
		this.home_list = home_list;
	}

}
