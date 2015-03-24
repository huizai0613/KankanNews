package com.kankan.kankanews.base;

import java.io.Serializable;

import org.json.JSONObject;

import com.kankan.kankanews.exception.NetRequestException;

public abstract class BaseBean<T> implements Serializable {

	public boolean checkJson(JSONObject jo) throws NetRequestException {

		String optString = jo.optString("return_num");

		if (optString != null) {
			if (optString.equals("1")) {
				return true;
			} else {
				String errorMsg = jo.optString("return_msg");
				throw new NetRequestException(new Error(optString, errorMsg));
			}
		} else {
			throw new NetRequestException(new Error("10000", "网络阻塞请稍候"));
		}
	}

	/**
	 * 将Bean实例转化为json对象
	 * 
	 * @return
	 */
	public abstract JSONObject toJSON();

	/**
	 * 将json对象转化为Bean实例
	 * 
	 * @param jsonObj
	 * @return
	 */
	public abstract T parseJSON(JSONObject jsonObj) throws NetRequestException;
	
}
