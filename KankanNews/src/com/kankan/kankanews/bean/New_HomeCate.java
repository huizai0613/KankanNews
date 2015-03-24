package com.kankan.kankanews.bean;

import org.json.JSONObject;

import com.kankan.kankanews.base.BaseBean;
import com.kankan.kankanews.exception.NetRequestException;
import com.lidroid.xutils.db.annotation.Id;

public class New_HomeCate extends BaseBean {

	@Id
	private String appclassid;
	private String title;
	private String sp;

	public String getSp() {
		return sp;
	}

	public void setSp(String sp) {
		this.sp = sp;
	}

	public String getAppclassid() {
		return appclassid;
	}

	public void setAppclassid(String appclassid) {
		this.appclassid = appclassid;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public JSONObject toJSON() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object parseJSON(JSONObject jsonObj) throws NetRequestException {
		appclassid = jsonObj.optString("appclassid");
		title = jsonObj.optString("title");
		sp = jsonObj.optString("sp");

		return this;
	}

}
