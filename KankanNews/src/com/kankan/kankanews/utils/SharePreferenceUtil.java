package com.kankan.kankanews.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashSet;
import java.util.Set;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

/**
 * 首选项管理
 * 
 * @ClassName: SharePreferenceUtil
 * @Description: TODO
 * @author smile
 * @date 2014-6-10 下午4:20:14
 */
@SuppressLint("CommitPrefEdits")
public class SharePreferenceUtil {
	private SharedPreferences mSharedPreferences;
	private static SharedPreferences.Editor editor;

	public SharePreferenceUtil(Context context, String name) {
		mSharedPreferences = context.getSharedPreferences(name,
				Context.MODE_PRIVATE);
		editor = mSharedPreferences.edit();
	}

	private String SHARED_KEY_NOTIFY = "shared_key_notify";
	private String SHARED_KEY_VOICE = "shared_key_sound";
	private String SHARED_KEY_VIBRATE = "shared_key_vibrate";
	private String SHARED_KEY_ACCOUNT = "shared_key_account";
	private String SHARED_KEY_PASSWORD = "shared_key_password";
	private String SHARED_KEY_LASRTIME = "shared_key_lasttime";
	private String SHARED_KEY_CONTENT = "shared_key_content";
	private String SHARED_KEY_FULL = "shared_key_full";

	private String SHARED_KEY_FRIST = "shared_key_frist";
	// 应用版本号
	private String SHARE_KEY_VERSION = "shared_key_version";
	// 用户信息
	private String SHARED_KEY_USER_ID = "shared_key_user_id";
	private String SHARED_KEY_USER_NAME = "shared_key_user_name";
	private String SHARED_KEY_USER_POST = "shared_key_user_post";
	// 是否用手机流量看/下载视频 默认为否
	private String SHARED_KEY_FLOW = "shared_key_flow";

	private String SEARCH_HIS_LIST = "search_his_list";

	private String FONT_SIZE_RADIX = "font_size_radix";

	private String IS_DAY_MODE = "is_day_mode";

	private String USER_TELEPHONE = "user_telephone";

	private String FIRST_GET_COLUMNS = "first_get_columns";

	private String FIRST_GET_REVELATIONS = "first_get_revelations";

	private String NEWS_HOME_VOTE = "NEWS_HOME_VOTE";

	// 应用版本号
	public void setVersion(String version) {
		editor.putString(SHARE_KEY_VERSION, version);
		editor.commit();
	}

	public String getVersion() {
		return mSharedPreferences.getString(SHARE_KEY_VERSION, "");
	}

	// 是否第一次进入程序
	public boolean isFristComing() {
		return mSharedPreferences.getBoolean(SHARED_KEY_FRIST, true);
	}

	public void setFristComing(boolean isFrist) {
		editor.putBoolean(SHARED_KEY_FRIST, isFrist);
		editor.commit();
	}

	// 是否用流量看视频
	public boolean isFlow() {
		return mSharedPreferences.getBoolean(SHARED_KEY_FLOW, false);
	}

	public void setFlow(boolean isFlow) {
		editor.putBoolean(SHARED_KEY_FLOW, isFlow);
		editor.commit();
	}

	// 用户id
	public void setUserId(String userid) {
		editor.putString(SHARED_KEY_USER_ID, userid);
		editor.commit();
	}

	public String getUserId() {
		return mSharedPreferences.getString(SHARED_KEY_USER_ID, "");
	}

	// 用户name
	public void setUserName(String usernmae) {
		editor.putString(SHARED_KEY_USER_NAME, usernmae);
		editor.commit();
	}

	public String getUserName() {
		return mSharedPreferences.getString(SHARED_KEY_USER_NAME, "");
	}

	// 用户post
	public void setUserPost(String userpost) {
		editor.putString(SHARED_KEY_USER_POST, userpost);
		editor.commit();
	}

	public String getUserPost() {
		return mSharedPreferences.getString(SHARED_KEY_USER_POST, "");
	}

	// 是否允许推送通知
	public boolean isAllowPushNotify() {
		return mSharedPreferences.getBoolean(SHARED_KEY_NOTIFY, true);
	}

	public void setPushNotifyEnable(boolean isChecked) {
		editor.putBoolean(SHARED_KEY_NOTIFY, isChecked);
		editor.commit();
	}

	// 允许声音
	public boolean isAllowVoice() {
		return mSharedPreferences.getBoolean(SHARED_KEY_VOICE, true);
	}

	public void setAllowVoiceEnable(boolean isChecked) {
		editor.putBoolean(SHARED_KEY_VOICE, isChecked);
		editor.commit();
	}

	// 允许震动
	public boolean isAllowVibrate() {
		return mSharedPreferences.getBoolean(SHARED_KEY_VIBRATE, true);
	}

	public void setAllowVibrateEnable(boolean isChecked) {
		editor.putBoolean(SHARED_KEY_VIBRATE, isChecked);
		editor.commit();
	}

	// 登陆账号
	public void setAccount(String phone) {
		editor.putString(SHARED_KEY_ACCOUNT, phone);
		editor.commit();
	}

	public String getAccount() {
		return mSharedPreferences.getString(SHARED_KEY_ACCOUNT, "");
	}

	// 登陆密码
	public void setPwd(String pwd) {
		editor.putString(SHARED_KEY_PASSWORD, pwd);
		editor.commit();
	}

	public String getPwd() {
		return mSharedPreferences.getString(SHARED_KEY_PASSWORD, "");
	}

	// 最后请求时间
	public void setLastTime(long lastTime) {
		editor.putLong(SHARED_KEY_LASRTIME, lastTime);
		editor.commit();
	}

	public long getLastTime() {
		return mSharedPreferences.getLong(SHARED_KEY_LASRTIME, 0);
	}

	public void setObject(Object object, String key) {

		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();

			ObjectOutputStream oos = new ObjectOutputStream(bos);

			oos.writeObject(object);

			String objStr = new String(Base64.encode(bos.toByteArray(),
					Base64.DEFAULT));
			editor.putString(key, objStr);
			editor.commit();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public <T> T getObject(String key, Class<T> t) {

		try {

			String string = mSharedPreferences.getString(key, "");

			if (!string.equals("")) {

				byte[] decode = Base64
						.decode(string.getBytes(), Base64.DEFAULT);

				ByteArrayInputStream bos = new ByteArrayInputStream(decode);

				ObjectInputStream oos = new ObjectInputStream(bos);

				T readObject = (T) oos.readObject();
				return readObject;
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	/** 是否第一次进入新闻详细界面 */
	public void setFirstContent(boolean pwd) {
		editor.putBoolean(SHARED_KEY_CONTENT, pwd);
		editor.commit();
	}

	public boolean getFirstContent() {
		return mSharedPreferences.getBoolean(SHARED_KEY_CONTENT, true);
	}

	/** 是否第一次全屏播放 */
	public void setFirstFull(boolean pwd) {
		editor.putBoolean(SHARED_KEY_FULL, pwd);
		editor.commit();
	}

	public boolean getFirstFull() {
		return mSharedPreferences.getBoolean(SHARED_KEY_FULL, true);
	}

	/** 搜索历史 */
	public void saveSearchHisList(String hisString) {
		editor.putString(SEARCH_HIS_LIST, hisString);
		editor.commit();
	}

	/** 搜索历史 */
	public String getSearchHisList() {
		return mSharedPreferences.getString(SEARCH_HIS_LIST, "");
	}

	/** 字体大小调整基数 */
	public void saveFontSizeRadix(float redix) {
		editor.putFloat(FONT_SIZE_RADIX, redix);
		editor.commit();
	}

	/** 字体大小调整基数 */
	public float getFontSizeRadix() {
		return mSharedPreferences.getFloat(FONT_SIZE_RADIX, 1);
	}

	/** 是否为日间模式 */
	public void saveIsDayMode(boolean isDay) {
		editor.putBoolean(IS_DAY_MODE, isDay);
		editor.commit();
	}

	/** 是否为日间模式 */
	public boolean getIsDayMode() {
		return mSharedPreferences.getBoolean(IS_DAY_MODE, true);
	}

	/** 是否为日间模式 */
	public void saveUserTelephone(String userTelephone) {
		editor.putString(USER_TELEPHONE, userTelephone);
		editor.commit();
	}

	/** 是否为日间模式 */
	public String getUserTelephone() {
		return mSharedPreferences.getString(USER_TELEPHONE, null);
	}

	/** 是否第一次进入首页看到栏目 */
	public void setFirstGetColumns(boolean get) {
		editor.putBoolean(FIRST_GET_COLUMNS, get);
		editor.commit();
	}

	public boolean getFirstGetColumns() {
		return mSharedPreferences.getBoolean(FIRST_GET_COLUMNS, true);
	}

	/** 是否第一次进入报料 */
	public void setFirstGetRevalations(boolean get) {
		editor.putBoolean(FIRST_GET_REVELATIONS, get);
		editor.commit();
	}

	public boolean getFirstGetRevalations() {
		return mSharedPreferences.getBoolean(FIRST_GET_REVELATIONS, true);
	}

	/** 是否第一次进入报料 */
	public void addVoteId(String voteId) {
		Set<String> voteSet = mSharedPreferences.getStringSet(NEWS_HOME_VOTE,
				new HashSet<String>());
		voteSet.add(voteId);
		editor.putStringSet(NEWS_HOME_VOTE, voteSet);
		editor.commit();
	}

	public boolean judgeVoteId(String voteId) {
		Set<String> voteSet = mSharedPreferences.getStringSet(NEWS_HOME_VOTE,
				new HashSet<String>());
		return voteSet.contains(voteId);
	}
}
