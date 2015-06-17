package com.kankan.kankanews.bean;

import org.json.JSONObject;

import android.text.TextUtils;

import com.kankan.kankanews.base.BaseBean;
import com.kankan.kankanews.exception.NetRequestException;

public class New_Colums_Second extends BaseBean<New_Colums_Second> {

	private String id;// classId
	private String name;
	private String tvLogo;
	private String timeslotS;
	private String timeslotE;

	@Override
	public JSONObject toJSON() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public New_Colums_Second parseJSON(JSONObject jsonObj)
			throws NetRequestException {

		if (jsonObj != null) {
			id = jsonObj.optString("classid");
			name = jsonObj.optString("classname");
			tvLogo = jsonObj.optString("tvlogo");
			timeslotS = jsonObj.optString("timeslot_s");
			timeslotE = jsonObj.optString("timeslot_e");
			return this;
		} else {
			return null;
		}
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTvLogo() {
		return tvLogo;
	}

	public void setTvLogo(String tvLogo) {
		this.tvLogo = tvLogo;
	}

	public String getTimeslotS() {
		return timeslotS;
	}

	public void setTimeslotS(String timeslotS) {
		this.timeslotS = timeslotS;
	}

	public String getTimeslotE() {
		return timeslotE;
	}

	public void setTimeslotE(String timeslotE) {
		this.timeslotE = timeslotE;
	}

}
