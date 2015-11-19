package com.kankan.kankanews.bean;

import java.io.Serializable;
import java.util.List;

import com.kankan.kankanews.bean.interfaz.CanBePlay;
import com.kankan.kankanews.bean.interfaz.CanSharedObject;

public class LiveChannelObj implements CanBePlay, CanSharedObject, Serializable {
	private String id;
	private String classid;
	private String title;
	private String titlepic;
	private String appbgpic;
	private String streamurl;
	private String intro;
	private String streamtype;
	private String catename;
	private String trailer;
	private String trailer_stime;
	private String trailer_etime;
	private String sharepic;
	private String titleurl;
	private String datetime;
	private String type;
	private String time;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getClassid() {
		return classid;
	}

	public void setClassid(String classid) {
		this.classid = classid;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitlepic() {
		return titlepic;
	}

	public void setTitlepic(String titlepic) {
		this.titlepic = titlepic;
	}

	public String getAppbgpic() {
		return appbgpic;
	}

	public void setAppbgpic(String appbgpic) {
		this.appbgpic = appbgpic;
	}

	public String getStreamurl() {
		return streamurl;
	}

	public void setStreamurl(String streamurl) {
		this.streamurl = streamurl;
	}

	public String getIntro() {
		return intro;
	}

	public void setIntro(String intro) {
		this.intro = intro;
	}

	public String getStreamtype() {
		return streamtype;
	}

	public void setStreamtype(String streamtype) {
		this.streamtype = streamtype;
	}

	public String getCatename() {
		return catename;
	}

	public void setCatename(String catename) {
		this.catename = catename;
	}

	public String getTrailer() {
		return trailer;
	}

	public void setTrailer(String trailer) {
		this.trailer = trailer;
	}

	public String getTrailer_stime() {
		return trailer_stime;
	}

	public void setTrailer_stime(String trailer_stime) {
		this.trailer_stime = trailer_stime;
	}

	public String getTrailer_etime() {
		return trailer_etime;
	}

	public void setTrailer_etime(String trailer_etime) {
		this.trailer_etime = trailer_etime;
	}

	public void setSharedPic(String sharepic) {
		this.sharepic = sharepic;
	}

	public String getTitleurl() {
		return titleurl;
	}

	public void setTitleurl(String titleurl) {
		this.titleurl = titleurl;
	}

	public String getDatetime() {
		return datetime;
	}

	public void setDatetime(String datetime) {
		this.datetime = datetime;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	@Override
	public String getShareTitle() {
		// TODO Auto-generated method stub
		return title;
	}

	@Override
	public String getSharedPic() {
		// TODO Auto-generated method stub
		return sharepic;
	}

}
