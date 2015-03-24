package com.kankan.kankanews.base;

import org.json.JSONObject;

import com.kankan.kankanews.utils.ToastUtils;

import android.content.Context;

public class Error extends BaseBean<Error> {

	public static final String COMERRORINFO = "网络未知错误!请稍候!";

	private String errorId;
	private String errorInfo;

	public Error() {
		super();
	}

	public Error(String errorInfo) {
		super();
		this.errorInfo = errorInfo;
	}

	public Error(String errorId, String errorInfo) {
		super();
		this.errorId = errorId;
		this.errorInfo = errorInfo;
	}

	public void print(Context context) {
		ToastUtils.Errortoast(context, errorInfo);
	}

	@Override
	public Error parseJSON(JSONObject jsonObj) {

		errorId = jsonObj.optString("errorId");
		errorInfo = jsonObj.optString("errorInfo");

		return this;
	}

	@Override
	public JSONObject toJSON() {
		// TODO Auto-generated method stub
		return null;
	}

}
