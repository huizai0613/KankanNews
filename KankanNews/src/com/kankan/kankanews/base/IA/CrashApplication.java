package com.kankan.kankanews.base.IA;

//import io.vov.vitamio.Vitamio;

import java.io.File;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;

import com.kankan.kankanews.base.BaseActivity;
import com.kankan.kankanews.base.download.MyRequestCallBack;
import com.kankan.kankanews.bean.Advert;
import com.kankan.kankanews.bean.Content_News;
import com.kankan.kankanews.bean.New_Colums;
import com.kankan.kankanews.bean.New_Colums_Info;
import com.kankan.kankanews.bean.New_Colums_Second;
import com.kankan.kankanews.bean.New_LivePlay;
import com.kankan.kankanews.bean.New_News;
import com.kankan.kankanews.bean.New_News_Home;
import com.kankan.kankanews.bean.New_News_Top;
import com.kankan.kankanews.bean.New_Recommend;
import com.kankan.kankanews.bean.User;
import com.kankan.kankanews.bean.User_Collect_Offline;
import com.kankan.kankanews.config.AndroidConfig;
import com.kankan.kankanews.utils.CommonUtils;
import com.kankan.kankanews.utils.SharePreferenceUtil;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.DbUtils.DbUpgradeListener;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.http.HttpHandler;
import com.nostra13.universalimageloader.cache.disc.impl.BaseDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.L;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.UmengRegistrar;
import com.umeng.message.entity.UMessage;

public class CrashApplication extends Application {

	private static CrashApplication mInstance;
	private static SharePreferenceUtil shareUtils;

	private static ImageLoaderConfiguration config;

	private static boolean isStart;

	public TreeMap<String, MyRequestCallBack> mRequestCallBackPauses = new TreeMap<String, MyRequestCallBack>();

	public TreeMap<String, HttpHandler> mHttpHandlereds = new TreeMap<String, HttpHandler>();

	public TreeMap<String, MyRequestCallBack> mRequestCallBackeds = new TreeMap<String, MyRequestCallBack>();

	public TreeMap<String, User_Collect_Offline> mUser_Collect_Offlines = new TreeMap<String, User_Collect_Offline>();

	public LinkedList<BaseActivity> mBaseActivityList = new LinkedList<BaseActivity>();
	public boolean isAattch = false;
	public boolean isLogin = false;

	private User user;

	private BaseActivity mainActivity;

	public static boolean isStart() {
		return isStart;
	}

	public static void setStart(boolean isStart) {
		CrashApplication.isStart = isStart;
	}

	public Typeface getTf() {
		return tf;
	}

	public void setTf(Typeface tf) {
		this.tf = tf;
	}

	static UmengMessageHandler messageHandler = new UmengMessageHandler() {
		public void dealWithNotificationMessage(final Context arg0,
				UMessage arg1) {
			if (isStart) {
				Intent intent = new Intent("dialog");

				intent.putExtra("value", arg1.text);

				mInstance.sendBroadcast(intent);

			} else {
				super.dealWithNotificationMessage(arg0, arg1);
			}
		};

	};

	@Override
	public void onCreate() {
		super.onCreate();

		mPushAgent = PushAgent.getInstance(this);
		mPushAgent.setDebugMode(true);
		mInstance = this;
		mPushAgent.setMessageHandler(messageHandler);
		ActivityManager activityManager = (ActivityManager) this
				.getSystemService(Context.ACTIVITY_SERVICE);
		int memoryClass = activityManager.getMemoryClass();

		WindowManager manager = (WindowManager) this
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = manager.getDefaultDisplay();
		width = display.getWidth();
		int height = display.getHeight();
		AssetManager mgr = getAssets();
		tf = Typeface.createFromAsset(mgr, "nomal.TTF");

		dbUtils = DbUtils.create(this, "kankan", 5, new DbUpgradeListener() {
			@Override
			public void onUpgrade(DbUtils arg0, int arg1, int arg2) {
				// TODO Auto-generated method stub
				try {
					arg0.dropTable(New_News_Home.class);
					arg0.dropTable(New_News_Top.class);
					arg0.dropTable(New_News.class);
					arg0.dropTable(New_Recommend.class);
					arg0.dropTable(New_LivePlay.class);
					arg0.dropTable(New_Colums.class);
					arg0.dropTable(New_Colums_Second.class);
					arg0.dropTable(New_Colums_Info.class);
					arg0.dropTable(Content_News.class);
					arg0.dropTable(Advert.class);
					arg0.createTableIfNotExist(New_News_Home.class);
					arg0.createTableIfNotExist(New_News_Top.class);
					arg0.createTableIfNotExist(New_News.class);
					arg0.createTableIfNotExist(New_Recommend.class);
					arg0.createTableIfNotExist(New_LivePlay.class);
					arg0.createTableIfNotExist(New_Colums.class);
					arg0.createTableIfNotExist(New_Colums_Second.class);
					arg0.createTableIfNotExist(New_Colums_Info.class);
					arg0.createTableIfNotExist(Content_News.class);
					arg0.createTableIfNotExist(Advert.class);
				} catch (DbException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		dbUtils.configAllowTransaction(true);

		// Vitamio.initialize(this,
		// getResources().getIdentifier("libarm", "raw", getPackageName()));
		checkLogin();
		File cacheDir = CommonUtils.getImageCachePath(getApplicationContext());
		initImageLoader(this, cacheDir);

		CrashHandler crashHandler = CrashHandler.getInstance();
		crashHandler.init(this);
		String device_token = UmengRegistrar.getRegistrationId(this);
	}

	public void initImageLoader(Context context, File cacheDir) {
		// This configuration tuning is custom. You can tune every option, you
		// may tune some of them,
		// or you can create default configuration by
		// ImageLoaderConfiguration.createDefault(this);
		// method.
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				context).threadPriority(Thread.NORM_PRIORITY - 2)
				.diskCache(new MyUnlimitedDiscCache(cacheDir))
				.denyCacheImageMultipleSizesInMemory()
				.diskCacheSize(50 * 1024 * 1024)
				.memoryCache(new WeakMemoryCache())
				// .memoryCacheSize(20*1024*1024)
				// .diskCacheExtraOptions(480, 320, null)
				// 50 Mb
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				// .writeDebugLogs() // Remove for release app
				.build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);
	}

	class MyUnlimitedDiscCache extends BaseDiscCache {

		public MyUnlimitedDiscCache(File cacheDir) {
			this(cacheDir, new MyFileNameGenerator());
		}

		public MyUnlimitedDiscCache(File cacheDir,
				FileNameGenerator fileNameGenerator) {
			super(cacheDir);
		}

	}

	class MyFileNameGenerator implements FileNameGenerator {

		@Override
		public String generate(String imageUri) {
			byte[] md5 = getMD5(CommonUtils.UrlToFileName(imageUri).getBytes());
			BigInteger bi = new BigInteger(md5).abs();
			return bi.toString(RADIX);
		}

		private static final String HASH_ALGORITHM = "MD5";
		private static final int RADIX = 10 + 26; // 10 digits + 26 letters

		private byte[] getMD5(byte[] data) {
			byte[] hash = null;
			try {
				MessageDigest digest = MessageDigest
						.getInstance(HASH_ALGORITHM);
				digest.update(data);
				hash = digest.digest();
			} catch (NoSuchAlgorithmException e) {
				L.e(e);
			}
			return hash;
		}

	}

	public void checkLogin() {
		getSpUtil();
		String user_id = shareUtils.getUserId();
		String user_name = shareUtils.getUserName();
		String user_poster = shareUtils.getUserPost();
		if (user_id == "" || user_name == "" || user_poster == "") {
			isLogin = false;
		} else {
			isLogin = true;
			user = new User();
			user.setUser_id(user_id);
			user.setUser_name(user_name);
			user.setUser_poster(user_poster);
		}
	}

	public static CrashApplication getInstance() {
		// TODO Auto-generated method stub
		return mInstance;
	}

	public SharePreferenceUtil getSpUtil() {

		if (shareUtils == null) {
			shareUtils = new SharePreferenceUtil(mInstance,
					AndroidConfig.SHARENAME);
		}

		return shareUtils;
	}

	public void addActivity(BaseActivity activity) {
		mBaseActivityList.add(activity);
	}

	public void removeActivity(BaseActivity activity) {
		mBaseActivityList.remove(activity);
	}

	/*
	 * 閻庣懓鑻崣蹇涙焻閿熶粙宕欓悜妯虹亯闁汇劌瀚花鏌ユ偨閿燂拷 缂備焦鎸诲顐⑿掕箛搴ｎ伇濞戞搩浜為弲顐︽閿燂拷
	 * 闁硅埖绋戠槐鎾舵暜缁嬪灝绻侀柛鎺戠埣閿熸枻鎷烽柛鎴嫹
	 */
	public void exit() {

		// 閻炴稏鍔庨妵姘跺箰婢跺鍟奾ome闂佸尅鎷�,缂佸顑呯花顓㈠礆妫颁胶鍟婇柛姘瑜帮拷
		Set<Entry<String, HttpHandler>> entrySetHandler = mHttpHandlereds
				.entrySet();

		for (Entry<String, HttpHandler> e : entrySetHandler) {
			mUser_Collect_Offlines.get(e.getKey()).setType(
					User_Collect_Offline.DOWNLOADSTOP);

			e.getValue().cancel();
		}

		Set<Entry<String, MyRequestCallBack>> entrySetCallBack = mRequestCallBackPauses
				.entrySet();

		for (Entry<String, MyRequestCallBack> e : entrySetCallBack) {
			mUser_Collect_Offlines.get(e.getKey()).setType(
					User_Collect_Offline.DOWNLOADSTOP);
		}
		mRequestCallBackPauses.clear();

		for (Activity activity : mBaseActivityList) {
			if (activity != null) {
				activity.finish();
			}
		}
		isStart = false;

		System.exit(0);
	}

	/*
	 * 婵炲鍔戦弨銏ゆ儌鐠囪尙绉�
	 */
	public void logout() {

		for (Activity activity : mBaseActivityList) {
			if (activity != null) {
				activity.finish();
			}
		}
	}

	/*
	 * 闂佸尅鎷锋慨锝勭劍婢у秹寮垫繅顪﹖ivity
	 */
	public void allFinish() {
		for (Activity activity : mBaseActivityList) {
			if (activity != null) {
				activity.finish();
			}
		}
	}

	// 閺夆晝鍋熼悽濠氭倷閻熸澘姣婇弶鈺傛煥濞叉牗绋夐妶鍡╁仹 闂侇偓鎷烽柛鎴ｆ閳诲吋鎯旈敓锟�
	int i;
	long front;
	long later;
	private Typeface tf;
	private DbUtils dbUtils;
	private PushAgent mPushAgent;
	private int width;

	public DbUtils getDbUtils() {
		return dbUtils;
	}

	public void shutDown() {
		i++;
		if (i < 2) {
			Toast.makeText(this, "再点一次退出程序", 0).show();
			front = System.currentTimeMillis();
			return;
		}
		if (i >= 2) {
			later = System.currentTimeMillis();
			if (later - front > 2000) {
				Toast.makeText(this, "再点一次退出程序", 0).show();
				front = System.currentTimeMillis();
				i = 1;
			} else {

				// File videoCachePath =
				// CommonUtils.getVideoCachePath(mInstance);
				// File videoCachePath

				exit();
				i = 0;
			}
		}
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	private int[] arrayid;
	private int position;
	public int Coupontime;

	public int[] getArrayid() {
		return arrayid;
	}

	public void setArrayid(int[] arrayid) {
		this.arrayid = arrayid;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public BaseActivity getMainActivity() {
		return mainActivity;
	}

	public void setMainActivity(BaseActivity mainActivity) {
		this.mainActivity = mainActivity;
	}

	public void changeMainActivityDayMode() {
		if (this.mainActivity.spUtil.getIsDayMode())
			this.mainActivity.chage2Day();
		else
			this.mainActivity.chage2Night();
	}
}
