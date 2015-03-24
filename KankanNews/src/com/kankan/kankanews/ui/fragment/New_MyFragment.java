package com.kankan.kankanews.ui.fragment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.LinearGradient;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.kankanews.kankanxinwen.R;
import com.kankan.kankanews.base.BaseFragment;
import com.kankan.kankanews.bean.New_News;
import com.kankan.kankanews.dialog.InfoMsgHint;
import com.kankan.kankanews.ui.item.New_Activity_My_About;
import com.kankan.kankanews.ui.item.New_Activity_My_FanKui;
import com.kankan.kankanews.ui.item.New_Activity_MyFoot;
import com.kankan.kankanews.utils.CommonUtils;
import com.kankan.kankanews.utils.PixelUtil;
import com.kankan.kankanews.utils.ToastUtils;
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

public class New_MyFragment extends BaseFragment implements OnClickListener {

	private View inflate;

	private LinearLayout layout_my_foot;
	private ImageView layout_download;
	private LinearLayout layout_about;
	private LinearLayout layout_point;
	private LinearLayout layout_fankui;
	private LinearLayout layout_updata;
	private TextView layout_version;
	private LinearLayout layout_delete;
	private TextView layout_detele_now;

	private View scroll_view;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreateView(inflater, container, savedInstanceState);

		inflate = inflater.inflate(R.layout.new_fragment_my, null);

		initview();
		initLister();
		initData();
		return inflate;
	}

	public void initview() {
		scroll_view = inflate.findViewById(R.id.scroll_view);
		View view = inflate.findViewById(R.id.scroll_child);
		view.setLayoutParams(new ScrollView.LayoutParams(
				ScrollView.LayoutParams.MATCH_PARENT, mActivity.mScreenHeight
						+ PixelUtil.dp2px(500)));
		scroll_view
				.setLayoutParams(new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.MATCH_PARENT,
						mActivity.mScreenHeight));

		layout_my_foot = (LinearLayout) inflate
				.findViewById(R.id.layout_my_foot);

		layout_download = (ImageView) inflate
				.findViewById(R.id.layout_download);
		layout_about = (LinearLayout) inflate.findViewById(R.id.layout_about);
		layout_fankui = (LinearLayout) inflate.findViewById(R.id.layout_fankui);
		layout_updata = (LinearLayout) inflate.findViewById(R.id.layout_updata);
		layout_version = (TextView) inflate.findViewById(R.id.layout_version);
		layout_delete = (LinearLayout) inflate.findViewById(R.id.layout_delete);
		layout_detele_now = (TextView) inflate
				.findViewById(R.id.layout_detele_now);

		initTitle_Right_Left_bar(inflate, "我", "", "", "#ffffff", 0, 0,
				"#000000", "#000000");
	}

	private void initLister() {
		layout_my_foot.setOnClickListener(this);
		layout_about.setOnClickListener(this);
		layout_download.setOnClickListener(this);
		layout_updata.setOnClickListener(this);
		layout_fankui.setOnClickListener(this);

		layout_delete.setOnClickListener(this);

		UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
			@Override
			public void onUpdateReturned(int updateStatus,
					UpdateResponse updateInfo) {
				switch (updateStatus) {
				case UpdateStatus.Yes: // has update
					UmengUpdateAgent.showUpdateDialog(mActivity, updateInfo);
					break;
				case UpdateStatus.No: // has no update
					ToastUtils.Infotoast(mActivity, "已是最新版本~");
					// Toast.makeText(mContext, "没有更新",
					// Toast.LENGTH_SHORT).show();
					break;
				// case UpdateStatus.NoneWifi: // none wifi
				// Toast.makeText(mContext, "没有wifi连接， 只在wifi下更新",
				// Toast.LENGTH_SHORT).show();
				// break;
				case UpdateStatus.Timeout: // time out
					ToastUtils.Errortoast(mActivity, "连接超时~");
					// Toast.makeText(mContext, "超时",
					// Toast.LENGTH_SHORT).show();
					break;
				}
			}
		});
	}

	@Override
	public void onResume() {
		super.onResume();
		// // 初始化缓存大小
		float filelength = getFolderSize(CommonUtils
				.getImageCachePath(mActivity.getApplicationContext()))
				/ 1024
				/ 1024
				+ getFolderSize(CommonUtils.getVideoCachePath(mActivity
						.getApplicationContext())) / 1024 / 1024;
		float filele_rusult = (float) (Math.round(filelength * 10)) / 10;//
		// 这里的100就是2位小数点,如果要其它位,如4位,这里两个100改成10000
		layout_detele_now.setText("当前缓存" + Float.toString(filele_rusult) + "M");
	}

	private void initData() {

		init_flow();

		layout_version.setText("当前版本  " + CommonUtils.getVersion(mActivity));

	}

	// 初始化按钮 flow流量
	private void init_flow() {
		if (mActivity.spUtil.isFlow() == true) {
			layout_download.setImageResource(R.drawable.icon_set_on);
		} else {
			layout_download.setImageResource(R.drawable.icon_set_off);
		}
	}

	@Override
	protected boolean initLocalDate() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	protected void saveLocalDate() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void refreshNetDate() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void loadMoreNetDate() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onSuccessObject(JSONObject jsonObject) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onSuccessArray(JSONArray jsonObject) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onFailure(VolleyError error) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.layout_my_foot:
			mActivity.startAnimActivity(New_Activity_MyFoot.class);
			break;

		case R.id.layout_about:
			mActivity.startAnimActivity(New_Activity_My_About.class);
			break;

		case R.id.layout_delete:
			delete();
			break;

		case R.id.layout_download:
			if (mActivity.spUtil.isFlow() == true) {
				mActivity.spUtil.setFlow(false);
			} else {
				mActivity.spUtil.setFlow(true);
			}
			initData();
			break;

		case R.id.layout_fankui:
			Intent intent = new Intent();
			intent.setClass(mActivity, New_Activity_My_FanKui.class);
			String id = new FeedbackAgent(mActivity).getDefaultConversation()
					.getId();
			intent.putExtra(FeedbackFragment.BUNDLE_KEY_CONVERSATION_ID, id);
			startActivity(intent);
			break;

		case R.id.layout_updata:
			if (CommonUtils.isNetworkAvailable(mActivity)) {
				UmengUpdateAgent.forceUpdate(mActivity);
			} else {
				ToastUtils.ErrorToastNoNet(mActivity);
			}
			break;

		default:
			break;
		}

	}

	private void delete() {
		final InfoMsgHint dialog = new InfoMsgHint(mActivity, R.style.MyDialog1);

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
						mActivity.getApplicationContext()).toString());

				try {
					String sql = "Update com_kankan_kankanews_bean_New_News set looktime = '0'";
					mActivity.dbUtils.execNonQuery(sql);
				} catch (DbException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				layout_detele_now.setText("当前缓存0.0M");
				ToastUtils.Infotoast(mActivity, "清除缓存成功!");
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
