package com.kankan.kankanews.bean;

import org.json.JSONObject;

import com.kankan.kankanews.base.BaseBean;
import com.kankan.kankanews.exception.NetRequestException;
import com.lidroid.xutils.db.annotation.Id;

public class New_News_Click extends BaseBean<New_News_Click> {

	@Id
	private String id;// 其实是mid 新闻id
	
	private String type;
	private String clickTime;
	private String classid;
	
	private String ztid;//专题id   主要用在专题里的点击量

	public String getClassid() {
		return classid;
	}

	public void setClassid(String classid) {
		this.classid = classid;
	}

	@Override
	public JSONObject toJSON() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public New_News_Click parseJSON(JSONObject jsonObj)
			throws NetRequestException {
		return this;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getClickTime() {
		return clickTime;
	}

	public void setClickTime(String clickTime) {
		this.clickTime = clickTime;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getZtid() {
		return ztid;
	}

	public void setZtid(String ztid) {
		this.ztid = ztid;
	}

}
