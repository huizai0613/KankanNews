package com.kankan.kankanews.bean;

import org.json.JSONObject;

import com.kankan.kankanews.base.BaseBean;
import com.kankan.kankanews.exception.NetRequestException;

public class MyCollect extends BaseBean<MyCollect> {
	
	private String id;
	private String mid;
	private String title;
	private String titlepic;
	private String dateline;

	@Override
	public JSONObject toJSON() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MyCollect parseJSON(JSONObject jsonObj) throws NetRequestException {
		
		id = jsonObj.optString("id");
		mid = jsonObj.optString("mid");
		title = jsonObj.optString("title");
		titlepic = jsonObj.optString("titlepic");
		dateline = jsonObj.optString("dateline");
		
		return this;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMid() {
		return mid;
	}

	public void setMid(String mid) {
		this.mid = mid;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitlepic() {
		return titlepic;
	}

	public void setTitlepic(String titlepic) {
		this.titlepic = titlepic;
	}

	public String getDateline() {
		return dateline;
	}

	public void setDateline(String dateline) {
		this.dateline = dateline;
	}
	
}
