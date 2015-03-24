package com.kankan.kankanews.bean;

import org.json.JSONObject;

import com.kankan.kankanews.base.BaseBean;
import com.kankan.kankanews.exception.NetRequestException;

public class SuccessMsg extends BaseBean<SuccessMsg> {

	@Override
	public JSONObject toJSON() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SuccessMsg parseJSON(JSONObject jsonObj) throws NetRequestException {
		// TODO Auto-generated method stub
		checkJson(jsonObj);
		return this;
	}

}
