package com.kankan.kankanews.bean;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.text.TextUtils;
import android.util.Log;

import com.kankan.kankanews.base.BaseBean;
import com.kankan.kankanews.exception.NetRequestException;

public class New_Colums extends BaseBean<New_Colums> {

	private String id;// classId
	private String classId;
	private String title;
	private String titlePic;
	private String type;
	private int secondNum;
	private List<New_Colums_Second> secondList;

	@Override
	public JSONObject toJSON() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public New_Colums parseJSON(JSONObject jsonObj) throws NetRequestException {

		if (jsonObj != null) {
			id = jsonObj.optString("id");
			classId = jsonObj.optString("classids");
			title = jsonObj.optString("title");
			titlePic = jsonObj.optString("titlepic");
			type = jsonObj.optString("type");
			secondNum = jsonObj.optInt("num");

			secondList = new ArrayList<New_Colums_Second>();
			JSONArray secondColums = jsonObj.optJSONArray("classes");
			if (secondColums != null && secondColums.length() > 0) {
				for (int i = 0; i < secondColums.length(); i++) {
					JSONObject tmpObject = secondColums.optJSONObject(i);
					New_Colums_Second secondColum = new New_Colums_Second();
					secondColum = secondColum.parseJSON(tmpObject);
					secondList.add(secondColum);
				}
			}

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

	public String getClassId() {
		return classId;
	}

	public void setClassId(String classId) {
		this.classId = classId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitlePic() {
		return titlePic;
	}

	public void setTitlePic(String titlePic) {
		this.titlePic = titlePic;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getSecondNum() {
		return secondNum;
	}

	public void setSecondNum(int secondNum) {
		this.secondNum = secondNum;
	}

	public List<New_Colums_Second> getSecondList() {
		return secondList;
	}

	public void setSecondList(List<New_Colums_Second> secondList) {
		this.secondList = secondList;
	}

}
