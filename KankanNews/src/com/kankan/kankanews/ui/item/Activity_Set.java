package com.kankan.kankanews.ui.item;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.volley.VolleyError;
import com.kankan.kankanews.base.BaseActivity;
import com.kankan.kankanews.base.download.MyRequestCallBack;
import com.kankan.kankanews.bean.User_Collect_Offline;
import com.kankan.kankanews.config.AndroidConfig;
import com.kankan.kankanews.dialog.InfoMsgHint;
import com.kankan.kankanews.ui.view.MyTextView;
import com.kankan.kankanews.utils.CommonUtils;
import com.kankan.kankanews.utils.ToastUtils;
import com.kankanews.kankanxinwen.R;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.SqlInfo;
import com.lidroid.xutils.db.table.DbModel;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.http.HttpHandler;
import com.umeng.fb.FeedbackAgent;
import com.umeng.fb.fragment.FeedbackFragment;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;

public class Activity_Set extends BaseActivity implements OnClickListener {

	private ImageView set_tui;
	private ImageView set_download;

	private boolean isTui;

	private MyTextView set_detele_now;

	private MyTextView set_version;

	private LinearLayout set_about;
	private LinearLayout set_bodyProcotol;
	private LinearLayout set_public;
	private LinearLayout set_fankui;
	private LinearLayout set_updata;
	private LinearLayout set_delete;

	private Button set_quit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set);

		// tongji
		initAnalytics(AndroidConfig.set_page);

		initTitle_Left_bar("设置", "", "#000000", R.drawable.icon_black_big);
		setOnLeftClickLinester(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AnimFinsh();
			}
		});

	}

	@Override
	protected void initView() {
		set_tui = (ImageView) findViewById(R.id.set_tui);
		set_download = (ImageView) findViewById(R.id.set_download);

		set_detele_now = (MyTextView) findViewById(R.id.set_detele_now);

		set_version = (MyTextView) findViewById(R.id.set_version);

		set_about = (LinearLayout) findViewById(R.id.set_about);
		set_bodyProcotol = (LinearLayout) findViewById(R.id.set_bodyProcotol);
		set_public = (LinearLayout) findViewById(R.id.set_public);
		set_fankui = (LinearLayout) findViewById(R.id.set_fankui);
		set_updata = (LinearLayout) findViewById(R.id.set_updata);
		set_delete = (LinearLayout) findViewById(R.id.set_delete);

		set_quit = (Button) findViewById(R.id.set_quit);
	}

	@Override
	protected void initData() {
		// 初始化缓存大小
		float filelength = getFolderSize(CommonUtils
				.getImageCachePath(getApplicationContext())) / 1024 / 1024;
		float filele_rusult = (float) (Math.round(filelength * 10)) / 10;// 这里的100就是2位小数点,如果要其它位,如4位,这里两个100改成10000
		set_detele_now.setText("当前缓存" + Float.toString(filele_rusult) + "M");

		if (isTui == true) {
			set_tui.setImageResource(R.drawable.icon_set_on);
		} else {
			set_tui.setImageResource(R.drawable.icon_set_off);
		}
		init_flow();

		set_version.setText("当前版本  " + spUtil.getVersion());
	}

	// 初始化按钮 flow流量
	private void init_flow() {
		if (spUtil.isFlow() == true) {
			set_download.setImageResource(R.drawable.icon_set_on);
		} else {
			set_download.setImageResource(R.drawable.icon_set_off);
		}
	}

	@Override
	protected void setListener() {
		set_tui.setOnClickListener(this);
		set_download.setOnClickListener(this);

		set_about.setOnClickListener(this);
		set_public.setOnClickListener(this);
		set_bodyProcotol.setOnClickListener(this);
		set_fankui.setOnClickListener(this);
		set_updata.setOnClickListener(this);
		set_delete.setOnClickListener(this);
		set_quit.setOnClickListener(this);

		UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
			@Override
			public void onUpdateReturned(int updateStatus,
					UpdateResponse updateInfo) {
				switch (updateStatus) {
				case UpdateStatus.Yes: // has update
					UmengUpdateAgent.showUpdateDialog(mContext, updateInfo);
					break;
				case UpdateStatus.No: // has no update
					ToastUtils.Infotoast(mContext, "已是最新版本~");
					// Toast.makeText(mContext, "没有更新",
					// Toast.LENGTH_SHORT).show();
					break;
				// case UpdateStatus.NoneWifi: // none wifi
				// Toast.makeText(mContext, "没有wifi连接， 只在wifi下更新",
				// Toast.LENGTH_SHORT).show();
				// break;
				case UpdateStatus.Timeout: // time out
					ToastUtils.Errortoast(mContext, "连接超时~");
					// Toast.makeText(mContext, "超时",
					// Toast.LENGTH_SHORT).show();
					break;
				}
			}
		});

		if (!mApplication.isLogin) {
			set_quit.setVisibility(View.GONE);
		}
	}

	@Override
	protected void onSuccess(JSONObject jsonObject) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onFailure(VolleyError error) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View v) {
		if (!isFinsh) {
			switch (v.getId()) {
			// case R.id.set_tui:
			// if(isTui == true){
			// isTui = false;
			// }else{
			// isTui = true;
			// }
			// initData();
			// break;

			case R.id.set_download:
				if (spUtil.isFlow() == true) {
					spUtil.setFlow(false);
				} else {
					spUtil.setFlow(true);
				}
				initData();
				break;

			case R.id.set_fankui:
				Intent intent = new Intent();
				intent.setClass(mContext, New_Activity_My_FanKui.class);
				String id = new FeedbackAgent(mContext)
						.getDefaultConversation().getId();
				intent.putExtra(FeedbackFragment.BUNDLE_KEY_CONVERSATION_ID, id);
				startActivity(intent);
				break;

			case R.id.set_about:
				startAnimActivity(New_Activity_My_About.class);
				break;
			case R.id.set_bodyProcotol:
				startAnimActivity2Obj(ActivityBodyProcotol.class, "TYPE", 0);
				break;
			case R.id.set_public:
				startAnimActivity2Obj(ActivityBodyProcotol.class, "TYPE", 1);
				break;
			case R.id.set_updata:

				if (CommonUtils.isNetworkAvailable(mContext)) {
					UmengUpdateAgent.forceUpdate(mContext);
				} else {
					ToastUtils.ErrorToastNoNet(mContext);
				}
				// UmengUpdateAgent.setUpdateListener(new UmengUpdateListener()
				// {
				// @Override
				// public void onUpdateReturned(int updateStatus,UpdateResponse
				// updateInfo) {
				// switch (updateStatus) {
				// case UpdateStatus.Yes: // has update
				// UmengUpdateAgent.showUpdateDialog(mContext, updateInfo);
				// break;
				// case UpdateStatus.No: // has no update
				// ToastUtils.Infotoast(mContext, "已是最新版本~");
				// // Toast.makeText(mContext, "没有更新",
				// Toast.LENGTH_SHORT).show();
				// break;
				// // case UpdateStatus.NoneWifi: // none wifi
				// // Toast.makeText(mContext, "没有wifi连接， 只在wifi下更新",
				// Toast.LENGTH_SHORT).show();
				// // break;
				// case UpdateStatus.Timeout: // time out
				// ToastUtils.Infotoast(mContext, "连接超时~");
				// // Toast.makeText(mContext, "超时", Toast.LENGTH_SHORT).show();
				// break;
				// }
				// }
				// });

				break;

			case R.id.set_delete:
				delete();
				break;

			case R.id.set_quit:
				quit();
				break;

			default:
				break;
			}
		}

	}

	@Override
	public void onBackPressed() {
		AnimFinsh();
	}

	private void quit() {
		final InfoMsgHint dialog = new InfoMsgHint(mContext, R.style.MyDialog1);

		dialog.setContent("退出登录", "是否要退出当前登录？", "退出登录", "取消");

		dialog.setCancleListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		dialog.setOKListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 清空本地用户信息
				spUtil.setUserId("");
				spUtil.setUserName("");
				spUtil.setUserPost("");
				mApplication.setUser(null);

				// 清空本地收藏
				try {
					List<User_Collect_Offline> uco = new ArrayList<User_Collect_Offline>();
					uco = dbUtils.findAll(Selector.from(
							User_Collect_Offline.class).where("isCollect",
							"==", true));
					if (uco != null && uco.size() > 0) {
						for (int i = 0; i < uco.size(); i++) {
							uco.get(i).setCollect(false);
							if (mApplication.mUser_Collect_Offlines != null
									&& mApplication.mUser_Collect_Offlines
											.size() > 0
									&& mApplication.mUser_Collect_Offlines
											.get(uco.get(i).getId()) != null) {
								mApplication.mUser_Collect_Offlines.get(
										uco.get(i).getId()).setCollect(false);
							}
						}
					}
					dbUtils.saveOrUpdateAll(uco);
				} catch (DbException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				ToastUtils.Infotoast(mContext, "退出成功！");

				set_quit.setVisibility(View.GONE);

				Intent data = new Intent();
				data.putExtra("User", false);
				setResult(AndroidConfig.Set_resultCode, data);
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	private void delete() {
		final InfoMsgHint dialog = new InfoMsgHint(mContext, R.style.MyDialog1);

		dialog.setContent("清空缓存", "是否要清空当前缓存", "清空", "放弃");

		dialog.setCancleListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		dialog.setOKListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				delAllFile(CommonUtils.getImageCachePath(
						getApplicationContext()).toString());
				set_detele_now.setText("当前缓存0.0M");
				ToastUtils.Infotoast(mContext, "清除缓存成功!");
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	/**
	 * 获取文件夹大小
	 * 
	 * @param file
	 *            File实例
	 * @return long
	 */
	public static float getFolderSize(java.io.File file) {
		float size = 0;
		try {
			java.io.File[] fileList = file.listFiles();
			for (int i = 0; i < fileList.length; i++) {
				if (fileList[i].isDirectory()) {
					size = size + getFolderSize(fileList[i]);
				} else {
					size = size + fileList[i].length();
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return size;
	}

	/**
	 * 删除文件夹里面的所有文件
	 * 
	 * @param path
	 *            String 文件夹路径 如 c:/fqf
	 */
	public void delAllFile(String path) {
		File file = new File(path);
		if (!file.exists()) {
			return;
		}
		if (!file.isDirectory()) {
			return;
		}
		String[] tempList = file.list();
		ArrayList<String> temp_List = new ArrayList<String>();

		for (String string : tempList) {
			temp_List.add(string);
		}

		// 记录要删除的图片名称
		List<String> img = new ArrayList<String>();
		// 记录要修改的user_collect_offline 表
		List<User_Collect_Offline> uco_list = new ArrayList<User_Collect_Offline>();
		try {
			// 清理缓存的时候 不清除用户收藏和已下载的新闻图片
			List<DbModel> reuslt = dbUtils
					.findDbModelAll(new SqlInfo(
							"select a.* from com_xunao_JiangHhVideo_bean_User_Collect_Offline b "
									+ "left join com_xunao_JiangHhVideo_bean_Content_News a on a.mid = b.id "
									+ "where isCollect = 1"));
			if (reuslt != null) {
				for (DbModel dbModel : reuslt) {
					img.add(String.valueOf(dbModel.getString("titlepic")
							.hashCode()));
					img.add(String.valueOf(dbModel.getString(
							"profile_image_url").hashCode()));
				}
			}

			// 修改本地离线下载数据
			uco_list = dbUtils.findAll(Selector
					.from(User_Collect_Offline.class).where("isOffline", "==",
							true));
			if (uco_list != null && uco_list.size() > 0) {
				for (int i = 0; i < uco_list.size(); i++) {
					uco_list.get(i).setOffline(false);
					// mApplication
					if (mApplication.mUser_Collect_Offlines != null
							&& mApplication.mUser_Collect_Offlines.size() > 0
							&& mApplication.mUser_Collect_Offlines.get(uco_list
									.get(i).getId()) != null) {
						mApplication.mUser_Collect_Offlines.get(
								uco_list.get(i).getId()).setOffline(false);
					}
				}
			}
			dbUtils.saveOrUpdateAll(uco_list);
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 清理缓存的时候 不清除用户头像
		if (mApplication.getUser() != null) {
			img.add(String.valueOf(mApplication.getUser().getUser_poster()
					.hashCode()));
		}

		File movefile = CommonUtils.getVideoCachePath(mContext);
		if (movefile.exists()) {
			// 暂停下载
			Set<Entry<String, HttpHandler>> entrySetHandler = mApplication.mHttpHandlereds
					.entrySet();

			for (Entry<String, HttpHandler> e : entrySetHandler) {
				mApplication.mUser_Collect_Offlines.get(e.getKey()).setType(
						User_Collect_Offline.DOWNLOADSTOP);

				e.getValue().cancel();
			}

			Set<Entry<String, MyRequestCallBack>> entrySetCallBack = mApplication.mRequestCallBackPauses
					.entrySet();

			for (Entry<String, MyRequestCallBack> e : entrySetCallBack) {
				mApplication.mUser_Collect_Offlines.get(e.getKey()).setType(
						User_Collect_Offline.DOWNLOADSTOP);
			}
			mApplication.mRequestCallBackPauses.clear();
			// 删除所有的video文件
			CommonUtils.deleteDir(movefile);
		}

		temp_List.removeAll(img);
		File temp = null;
		for (int i = 0; i < temp_List.size(); i++) {
			if (path.endsWith(File.separator)) {
				temp = new File(path + temp_List.get(i));
			} else {
				temp = new File(path + File.separator + temp_List.get(i));
			}
			if (temp.isFile()) {
				temp.delete();
			}
			if (temp.isDirectory()) {
				delAllFile(path + "/" + temp_List.get(i));// 先删除文件夹里面的文件
				delFolder(path + "/" + temp_List.get(i));// 再删除空文件夹
			}
		}
	}

	public void delFolder(String folderPath) {
		try {
			delAllFile(folderPath); // 删除完里面所有内容
			String filePath = folderPath;
			filePath = filePath.toString();
			java.io.File myFilePath = new java.io.File(filePath);
			myFilePath.delete(); // 删除空文件夹
		} catch (Exception e) {
			System.out.println("删除文件夹操作出错");
			e.printStackTrace();

		}
	}

}
