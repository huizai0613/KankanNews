package com.kankan.kankanews.bean;

import org.json.JSONObject;

import com.kankan.kankanews.base.BaseBean;
import com.kankan.kankanews.exception.NetRequestException;

public class Comment extends BaseBean<Comment> {

	private String id;
	private String mid;
	private String uid;
	private String name;
	private String cTime;
	private String intro;
	private String newtitle;
	private String userpic;
	private String memo;

	private int type;// 1为获取的 0为自己添加的

	@Override
	public JSONObject toJSON() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Comment parseJSON(JSONObject jsonObj) throws NetRequestException {

		id = jsonObj.optString("id");
		mid = jsonObj.optString("mid");
		uid = jsonObj.optString("uid");
		name = jsonObj.optString("name");
		cTime = jsonObj.optString("cTime");
		intro = jsonObj.optString("intro");
		newtitle = jsonObj.optString("newtitle");
		userpic = jsonObj.optString("userpic");
		memo = jsonObj.optString("memo");

		type = 1;

		return this;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMid() {
		return mid;
	}

	public void setMid(String mid) {
		this.mid = mid;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getcTime() {
		return cTime;
	}

	public void setcTime(String cTime) {
		this.cTime = cTime;
	}

	public String getIntro() {
		return intro;
	}

	public void setIntro(String intro) {
		this.intro = intro;
	}

	public String getNewtitle() {
		return newtitle;
	}

	public void setNewtitle(String newtitle) {
		this.newtitle = newtitle;
	}

	public String getUserpic() {
		return userpic;
	}

	public void setUserpic(String userpic) {
		this.userpic = userpic;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

}
