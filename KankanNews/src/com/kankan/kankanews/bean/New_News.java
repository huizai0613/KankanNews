package com.kankan.kankanews.bean;

import java.util.ArrayList;

import org.json.JSONObject;

import com.kankan.kankanews.base.BaseBean;
import com.kankan.kankanews.bean.interfaz.CanSharedObject;
import com.kankan.kankanews.exception.NetRequestException;

@SuppressWarnings("serial")
public class New_News extends BaseBean<New_News> implements CanSharedObject {

	private String id;// 用mid 作为主键
	private String title;
	private String titleurl;
	private String titlepic;
	private String type;
	private String sourceid;
	private String newstime;
	private String spclassid;
	private String ztid;
	// private String mid;

	private String titlepiclist;// 列表页的图片，用于作为分享的图片
	private String titlelist;//列表页的title

	// 新闻详情
	private String omsid;
	private String videourl;
	private String intro;
	private String newstext;

	// 自定义字段用于区别 home_top_news home_news
	private String my_type;
	// 记录观看的时间
	private String looktime = "0";

	@Override
	public JSONObject toJSON() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public New_News parseJSON(JSONObject jsonObj) throws NetRequestException {

		id = jsonObj.optString("mid");
		title = jsonObj.optString("title");
		titleurl = jsonObj.optString("titleurl");
		titlepic = jsonObj.optString("titlepic");
		type = jsonObj.optString("type");
		sourceid = jsonObj.optString("sourceid");
		newstime = jsonObj.optString("newstime");
		spclassid = jsonObj.optString("spclassid");
		ztid = jsonObj.optString("ztid");

		omsid = jsonObj.optString("omsid");
		videourl = jsonObj.optString("videourl");
		intro = jsonObj.optString("intro");
		newstext = jsonObj.optString("newstext");

		// try {
		// JSONArray tempParagraph = new JSONArray(newstext);
		// JSONObject jsonObject = (JSONObject) tempParagraph.opt(0);
		// int length = tempParagraph.length();
		// for(int i=0;i<length;i++){
		// String tempString = tempParagraph.getString("" + i);
		// paragraph.add(tempString);
		// }
		// } catch (Exception e) {
		// e.printStackTrace();
		// }

		return this;
	}

	public ArrayList<String> getNewsText() {
		ArrayList<String> paragraph = new ArrayList<String>();
		String nt = newstext.toString().substring(1,
				newstext.toString().length() - 1);
		String[] split = nt.split(",");
		int length = split.length;
		if (length > 0) {
			for (String string : split) {
				string = string.substring(1, string.length() - 1);
				// String[] str = string.split(":::");
				paragraph.add(string);

			}
		}
		return paragraph;
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

	// public String getMid() {
	// return mid;
	// }
	//
	// public void setMid(String mid) {
	// this.mid = mid;
	// }

	public String getMy_type() {
		return my_type;
	}

	public void setMy_type(String my_type) {
		this.my_type = my_type;
	}

	public String getOmsid() {
		return omsid;
	}

	public void setOmsid(String omsid) {
		this.omsid = omsid;
	}

	public String getVideourl() {
		return videourl;
	}

	public void setVideourl(String videourl) {
		this.videourl = videourl;
	}

	public String getIntro() {
		return intro;
	}

	public void setIntro(String intro) {
		this.intro = intro;
	}

	public String getLooktime() {
		return looktime;
	}

	public void setLooktime(String looktime) {
		this.looktime = looktime;
	}

	public String getNewstext() {
		return newstext;
	}

	public void setNewstext(String newstext) {
		this.newstext = newstext;
	}

	public String getTitlepiclist() {
		return titlepiclist;
	}

	public void setTitlepiclist(String titlepiclist) {
		this.titlepiclist = titlepiclist;
	}

	public String getTitlelist() {
		return titlelist;
	}

	public void setTitlelist(String titlelist) {
		this.titlelist = titlelist;
	}

	// public ArrayList<String> getParagraph() {
	// return paragraph;
	// }
	//
	// public void setParagraph(String paragraph) {
	// try {
	// JSONObject tempParagraph = new JSONObject(paragraph);
	// int length = tempParagraph.length();
	// for(int i=0;i<length;i++){
	// String tempString = tempParagraph.getString("" + i);
	// this.paragraph.add(tempString);
	// }
	// } catch (JSONException e) {
	// e.printStackTrace();
	// }
	// }

}
