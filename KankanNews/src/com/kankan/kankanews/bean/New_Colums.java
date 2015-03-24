package com.kankan.kankanews.bean;

import org.json.JSONObject;

import android.text.TextUtils;

import com.kankan.kankanews.base.BaseBean;
import com.kankan.kankanews.exception.NetRequestException;

public class New_Colums extends BaseBean<New_Colums> {

	private String id;//classId
	private String programName;
	private String programPic;
	private String programType;
	private String programStart;
	private String programEnd;

	@Override
	public JSONObject toJSON() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public New_Colums parseJSON(JSONObject jsonObj) throws NetRequestException {

		if (jsonObj != null) {
			id = jsonObj.optString("classId");
			programName = jsonObj.optString("programName");
			programStart = jsonObj.optString("programStart");
			programEnd = jsonObj.optString("programEnd");
			
			programPic = jsonObj.optString("programPic");
			programType = jsonObj.optString("programType");

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

	public String getProgramName() {
		return programName;
	}

	public void setProgramName(String programName) {
		this.programName = programName;
	}

	public String getProgramPic() {
		return programPic;
	}

	public void setProgramPic(String programPic) {
		this.programPic = programPic;
	}

	public String getProgramType() {
		return programType;
	}

	public void setProgramType(String programType) {
		this.programType = programType;
	}

	public String getProgramStart() {
		return programStart;
	}

	public void setProgramStart(String programStart) {
		this.programStart = programStart;
	}

	public String getProgramEnd() {
		return programEnd;
	}

	public void setProgramEnd(String programEnd) {
		this.programEnd = programEnd;
	}

}
