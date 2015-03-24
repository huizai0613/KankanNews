package com.kankan.kankanews.bean;

import org.json.JSONObject;

import com.kankan.kankanews.base.BaseBean;
import com.kankan.kankanews.exception.NetRequestException;

public class Content_Count extends BaseBean <Content_Count>{
	
	private String id;//用于数据库的
	private String share_qq;
	private String share_weixin;
	private String share_weibo;
	private String share_mail;
	private String forward;
	private String favorite_num;
	private String comment;
	private String onclick;

	@Override
	public JSONObject toJSON() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Content_Count parseJSON(JSONObject jsonObj) throws NetRequestException {
		
		checkJson(jsonObj);
		
		JSONObject jsonObject = jsonObj.optJSONObject("return_result");
		
		share_qq = jsonObject.optString("share_qq")==""?"0": jsonObject.optString("share_qq");
		share_weixin = jsonObject.optString("share_weixin")==""?"0": jsonObject.optString("share_weixin");
		share_weibo = jsonObject.optString("share_weibo")==""?"0": jsonObject.optString("share_weibo");
		share_mail = jsonObject.optString("share_mail")==""?"0": jsonObject.optString("share_mail");
		forward = jsonObject.optString("forward");
		favorite_num = jsonObject.optString("favorite_num")==""?"0":jsonObject.optString("favorite_num");
		comment = jsonObject.optString("comment")==""?"0": jsonObject.optString("comment");
		onclick = jsonObject.optString("onclick");
		
		return this;
	}

	public String getShare_qq() {
		return share_qq;
	}

	public void setShare_qq(String share_qq) {
		this.share_qq = share_qq;
	}

	public String getShare_weixin() {
		return share_weixin;
	}

	public void setShare_weixin(String share_weixin) {
		this.share_weixin = share_weixin;
	}

	public String getShare_weibo() {
		return share_weibo;
	}

	public void setShare_weibo(String share_weibo) {
		this.share_weibo = share_weibo;
	}

	public String getShare_mail() {
		return share_mail;
	}

	public void setShare_mail(String share_mail) {
		this.share_mail = share_mail;
	}

	public String getForward() {
		return forward;
	}

	public void setForward(String forward) {
		this.forward = forward;
	}

	public String getFavorite_num() {
		return favorite_num;
	}

	public void setFavorite_num(String favorite_num) {
		this.favorite_num = favorite_num;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getOnclick() {
		return onclick;
	}

	public void setOnclick(String onclick) {
		this.onclick = onclick;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
