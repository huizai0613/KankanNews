package com.kankan.kankanews.bean;

import org.json.JSONObject;

import com.kankan.kankanews.base.BaseBean;
import com.kankan.kankanews.exception.NetRequestException;

public class New_News_Home extends BaseBean<New_News_Home> {

	private String id;
	private String title;
	private String titleurl;
	private String titlepic;
	private String sharedPic;
	private String type;
	private String sourceid;
	private String newstime;
	private String spclassid;
	private String ztid;
	private String mid;
	private String classid;
	private String labels;

	private String ztype = "";
	private String intro;

	private String stime;
	private String etime;

	public String getClassid() {
		return classid;
	}

	public void setClassid(String classid) {
		this.classid = classid;
	}

	@Override
	public JSONObject toJSON() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public New_News_Home parseJSON(JSONObject jsonObj)
			throws NetRequestException {

		id = jsonObj.optString("id");
		title = jsonObj.optString("title");
		titleurl = jsonObj.optString("titleurl");
		titlepic = jsonObj.optString("titlepic");
		sharedPic = jsonObj.optString("sharepic");
		type = jsonObj.optString("type");

		sourceid = jsonObj.optString("sourceid");
		newstime = jsonObj.optString("newstime");
		spclassid = jsonObj.optString("spclassid");
		ztid = jsonObj.optString("ztid");
		mid = jsonObj.optString("mid");

		ztype = jsonObj.optString("ztype");
		intro = jsonObj.optString("intro");
		labels = jsonObj.optString("labels");

		stime = jsonObj.optString("stime");
		etime = jsonObj.optString("etime");

		return this;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitleurl() {
		return titleurl;
	}

	public void setTitleurl(String titleurl) {
		this.titleurl = titleurl;
	}

	public String getTitlepic() {
		return titlepic;
	}

	public void setTitlepic(String titlepic) {
		this.titlepic = titlepic;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSourceid() {
		return sourceid;
	}

	public void setSourceid(String sourceid) {
		this.sourceid = sourceid;
	}

	public String getNewstime() {
		return newstime;
	}

	public void setNewstime(String newstime) {
		this.newstime = newstime;
	}

	public String getSpclassid() {
		return spclassid;
	}

	public void setSpclassid(String spclassid) {
		this.spclassid = spclassid;
	}

	public String getZtid() {
		return ztid;
	}

	public void setZtid(String ztid) {
		this.ztid = ztid;
	}

	public String getMid() {
		return mid;
	}

	public void setMid(String mid) {
		this.mid = mid;
	}

	public String getZtype() {
		return ztype;
	}

	public void setZtype(String ztype) {
		this.ztype = ztype;
	}

	public String getIntro() {
		return intro;
	}

	public void setIntro(String intro) {
		this.intro = intro;
	}

	public String getStime() {
		return stime;
	}

	public void setStime(String stime) {
		this.stime = stime;
	}

	public String getEtime() {
		return etime;
	}

	public void setEtime(String etime) {
		this.etime = etime;
	}

	public String getSharedPic() {
		return sharedPic;
	}

	public void setSharedPic(String sharedPic) {
		this.sharedPic = sharedPic;
	}

	public String getLabels() {
		return labels;
	}

	public void setLabels(String labels) {
		this.labels = labels;
	}

}
