package com.kankan.kankanews.bean;

import java.io.Serializable;
import java.util.List;

import com.kankan.kankanews.bean.interfaz.CanBePlay;
import com.kankan.kankanews.bean.interfaz.CanSharedObject;

public class LiveLiveObj implements CanBePlay, Serializable, CanSharedObject {
	private String id;
	private String title;
	private String titlepic;
	private String streamurl;
	private String intro;
	private String isgood;
	private String sharepic;
	private String titleurl;
	private List<Keyboard> keyboard;
	private String timestamp;
	private String catename;
	private String datetime;
	private String type;
	private String time;
	private String widepic;
	private boolean isOrder = false;
	private String sharetitle;

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

	public String getTitlepic() {
		return titlepic;
	}

	public void setTitlepic(String titlepic) {
		this.titlepic = titlepic;
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

	public String getIsgood() {
		return isgood;
	}

	public void setIsgood(String isgood) {
		this.isgood = isgood;
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

	public List<Keyboard> getKeyboard() {
		return keyboard;
	}

	public void setKeyboard(List<Keyboard> keyboard) {
		this.keyboard = keyboard;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getCatename() {
		return catename;
	}

	public void setCatename(String catename) {
		this.catename = catename;
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

	public boolean isOrder() {
		return isOrder;
	}

	public void setOrder(boolean isOrder) {
		this.isOrder = isOrder;
	}

	public String getWidepic() {
		return widepic;
	}

	public void setWidepic(String widepic) {
		this.widepic = widepic;
	}

	@Override
	public String getShareTitle() {
		// TODO Auto-generated method stub
		if (this.sharetitle == null)
			return title;
		return this.sharetitle;
	}

	@Override
	public void setSharedTitle(String shareTitle) {
		// TODO Auto-generated method stub
		this.sharetitle = shareTitle;
	}

	@Override
	public String getSharedPic() {
		return sharepic;
	}

	@Override
	public String getShareIntro() {
		return this.getIntro();
	}

	/**
	 * "id": "326", "title": "《琅琊榜》点映+发布会—上海站", "titlepic":
	 * "http://p3.img.kksmg.com/image/2015/08/20/5d8c1101f7089cb1fea998b9537a9721.jpg"
	 * , "streamurl": "http://hls.kksmg.com/iphone/downloads/ch1/index.m3u8",
	 * "intro":
	 * "由著名演员胡歌、刘涛、王凯、黄维德、高鑫、陈龙等联袂主演的大型古装传奇电视剧《琅琊榜》将于9月15日登陆上海。导演孔笙，制片人侯鸿亮，主演胡歌、刘涛、王凯、陈龙、刘敏涛、靳东、刘奕君、吴磊、高鑫等人将出席点映及发布会。"
	 * , "isgood": "7", "sharepic":
	 * "http://p3.img.kksmg.com/image/2015/08/20/5d8c1101f7089cb1fea998b9537a9721.jpg"
	 * , "titleurl": "http://live.kankanews.com/zhibo/326.html", "keyboard": [],
	 * "timestamp": 1442296800, "catename": "看看直播", "datetime":
	 * "2015-09-15 14:00:::2015-09-15 15:00", "type": "直播预告", "time":
	 * "09月15日 14:00"
	 */
}
