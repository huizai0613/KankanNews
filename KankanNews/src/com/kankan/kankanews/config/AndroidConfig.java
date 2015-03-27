package com.kankan.kankanews.config;

public class AndroidConfig {

	public static final int FRAGMENTREFRESHTIME = 1000 * 60 * 10;// fragment刷新间隔时间

	public static final String SHARENAME = "transportationAndroid";

	public static final String DOWLOADAPK = "http://www.uw52.com/apk/test.apk";

	public static final String NETHOST = "http://api.app.kankanews.com/jianghu/v1";
	// public static final String NETHOST = "http://192.168.1.106:85/jyt/v1/";

	// 主页数据
	public static final String MAINDATA = "/info/public_timeline/";

	// 登录后请求获取用户id
	public static final String GETUSERID = "http://sso.kankanews.com/app/getKanKanID.do";

	// 最热数据
	public static final String HOTDATA = "/info/public_hotline/";

	// 新闻详细数据
	public static final String NewContent = "/info/show/";

	// 评论数据——新闻内容页
	public static final String NewContentComment = "/comments/show/";

	// 分享数据——新闻内容页
	public static final String NewContentCounts = "/info/counts/";

	// 提交分享数据到服务器
	public static final String CommitShare = "/info/share/";

	// 发表评论
	public static final String CommitComment = "/comments/create/";

	// 点击收藏
	public static final String AddCollect = "/favorites/create/";

	// 取消收藏
	public static final String CancelCollect = "/favorites/destroy";

	// 获取我的收藏数据
	public static final String GetMyCollect = "/favorites/timeline/";

	// 获取用户评论
	public static final String GetMyComment = "/comments/by_me/";

	// 记者主页
	public static final String GetReporter = "/info/reporter_timeline/";

	// 登录回调
	public static final int Drawer_login_requestCode = 1001;
	public static final int Drawer_login_resultCode = 1002;

	// 评论回调
	public static final int Comment_requestCode = 2001;
	public static final int Comment_resultCode = 2002;

	// 设置回调
	public static final int Set_requestCode = 3001;
	public static final int Set_resultCode = 3002;

	// 页面统计-ID
	// 首页-最新
	public static final String new_news_page = "PageView_timeline"; // "最新"
	// 首页-最热
	public static final String new_hot_page = "PageView_hotline"; // "最热"
	// 我的收藏
	public static final String my_collect_page = "PageView_favorites"; // "我的收藏"
	// 我的评论
	public static final String my_comment_page = "PageView_myComments"; // "我的评论"
	// 离线
	public static final String my_offline_page = "PageView_download"; // "我的离线"
	// 设置
	public static final String set_page = "PageView_setting"; // "设置"
	// 用户登陆
	public static final String login_page = "PageView_login"; // "用户登录"
	// 意见反馈
	public static final String set_fankui_page = "PageView_feedback"; // "意见反馈"
	// 关于页面
	public static final String set_about_page = "PageView_about"; // "关于页面"
	// 看过的新闻
	public static final String history_page = "PageView_history";

	// 事件统计-ID
	// 视频播放
	public static final String video_play_event = "Event_play"; // "play"
	// 收藏
	public static final String video_collect_event = "Event_collec"; // "collect"
	// 离线缓存
	public static final String video_offline_event = "Event_download"; // "download"
	// 发送评论
	public static final String video_comment_event = "Event_comment"; // "comment"
	// 登陆
	public static final String login_event = "Event_login"; // "login"
	// 离线播放
	public static final String video_offline_play_event = "Event_offline_play"; // "offline_play"
	// 全屏
	public static final String video_fullscreen_event = "Event_fullscreen"; // "fullscreen"

	// 参数：
	// 事件 参数名 参数值
	// fullscreen action 放大 或者 缩小
	// offline_play title 视频标题
	// login type 新浪微博 或者 腾讯qq
	// comment title 视频标题
	// download title 视频标题
	// collect title 视频标题
	// play title 视频标题

	// ------------------------------------------------------------------看看新闻------------------------------------------------------------------------
	public static final String New_NETHOST = "http://api.app.kankanews.com/kankan";

	// 首页条目数据
	public static final String New_HomeCateData = "/v5/info/item_timeline";
	// 首页数据
	public static final String New_HomeData = "/v5/info/public_timeline/";

	// 新闻点击量
	public static final String New_NewsClick = "http://m.kankanews.com/web/clickList?list=";
	// 添加新闻点击量
	public static final String New_NewsAddClick = "http://m.kankanews.com/web/clickCollector?";
	// 新闻详情
	public static final String New_NewsContent = "/v5/content/video_detail/";

	// 直播数据
	public static final String New_LivePlay = "/v5/live/timeline/";

	// 栏目列表
	public static final String New_Colums = "http://api.app.kankanews.com/index.php/kankan/v5/channel/timeline";

	// 栏目节目列表
	public static final String New_Colums_Info = "/v5/channel/info_timeline/";

	// 栏目时间回调
	public static final int Colums_Time_requestCode = 10001;
	public static final int Colums_Time_resultCode = 10002;
	// 专题接口
	public static final String New_Subject = "/v5/info/focus_timeline/";

	// 获得热门推荐接口
	public static final String New_Recommend = "http://api.app.kankanews.com/index.php/kankan/v5/content/recommend_timeline";
	
}