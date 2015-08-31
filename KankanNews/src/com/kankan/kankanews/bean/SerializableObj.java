package com.kankan.kankanews.bean;

import java.io.Serializable;

import org.json.JSONObject;

import com.kankan.kankanews.base.BaseBean;
import com.kankan.kankanews.bean.interfaz.CanSharedObject;
import com.kankan.kankanews.exception.NetRequestException;
import com.lidroid.xutils.db.annotation.Id;

public class SerializableObj implements Serializable {
	private String id;
	private String jsonStr;
	private String classType;

	public SerializableObj() {

	}

	public SerializableObj(String id, String jsonStr, String classType) {
		super();
		this.id = id;
		this.jsonStr = jsonStr;
		this.classType = classType;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getJsonStr() {
		return jsonStr;
	}

	public void setJsonStr(String jsonStr) {
		this.jsonStr = jsonStr;
	}

	public String getClassType() {
		return classType;
	}

	public void setClassType(String classType) {
		this.classType = classType;
	}

}
