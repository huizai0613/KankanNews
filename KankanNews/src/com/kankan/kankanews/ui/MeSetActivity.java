package com.kankan.kankanews.ui;

import java.io.File;
import java.util.ArrayList;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.kankan.kankanews.base.BaseActivity;
import com.kankan.kankanews.dialog.InfoMsgHint;
import com.kankan.kankanews.ui.item.New_Activity_MyFoot;
import com.kankan.kankanews.ui.item.New_Activity_My_About;
import com.kankan.kankanews.ui.item.New_Activity_My_FanKui;
import com.kankan.kankanews.ui.item.NewsBrowseRecordActivity;
import com.kankan.kankanews.utils.CommonUtils;
import com.kankan.kankanews.utils.FontUtils;
import com.kankan.kankanews.utils.PixelUtil;
import com.kankan.kankanews.utils.ToastUtils;
import com.kankanews.kankanxinwen.R;
import com.lidroid.xutils.exception.DbException;
import com.umeng.fb.FeedbackAgent;
import com.umeng.fb.fragment.FeedbackFragment;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;
import com.zcw.togglebutton.ToggleButton;

public class MeSetActivity extends BaseActivity implements OnClickListener {

	private View inflate;

	private LinearLayout layout_my_foot;
//	private ToggleButton layout_download;
	private LinearLayout layout_about;
	private LinearLayout layout_point;
	private LinearLayout layout_fankui;
	private LinearLayout layout_updata;
	private LinearLayout font_size_set;
	private TextView layout_version;
	private LinearLayout layout_delete;
	private TextView layout_detele_now;
	private ToggleButton isDayMode;

	private View scroll_view;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_me_set);

	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		scroll_view = this.findViewById(R.id.scroll_view);
		// View view = this.findViewById(R.id.scroll_child);
		// view.setLayoutParams(new ScrollView.LayoutParams(
		// ScrollView.LayoutParams.MATCH_PARENT, this.mScreenHeight
		// + PixelUtil.dp2px(500)));
		// scroll_view.setLayoutParams(new FrameLayout.LayoutParams(
		// FrameLayout.LayoutParams.MATCH_PARENT, this.mScreenHeight));

		layout_my_foot = (LinearLayout) this.findViewById(R.id.layout_my_foot);
		nightView = findViewById(R.id.night_view);

//		layout_download = (ToggleButton) this
//				.findViewById(R.id.layout_download);
		layout_about = (LinearLayout) this.findViewById(R.id.layout_about);
		font_size_set = (LinearLayout) this.findViewById(R.id.font_size_set);
		layout_fankui = (LinearLayout) this.findViewById(R.id.layout_fankui);
		layout_updata = (LinearLayout) this.findViewById(R.id.layout_updata);
		layout_version = (TextView) this.findViewById(R.id.layout_version);
		layout_delete = (LinearLayout) this.findViewById(R.id.layout_delete);
		layout_detele_now = (TextView) this
				.findViewById(R.id.layout_detele_now);
		isDayMode = (ToggleButton) this.findViewById(R.id.is_day_mode);

		initTitleLeftBar("我", R.drawable.new_ic_back);
	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub

		initToggle();
		layout_version.setText("当前版本  " + CommonUtils.getVersion(this));
	}

	@Override
	protected void setListener() {
		// TODO Auto-generated method stub
		layout_my_foot.setOnClickListener(this);
		layout_about.setOnClickListener(this);
		layout_updata.setOnClickListener(this);
		layout_fankui.setOnClickListener(this);
		font_size_set.setOnClickListener(this);
		layout_delete.setOnClickListener(this);
		setOnLeftClickLinester(this);

		isDayMode.setOnToggleChanged(new ToggleButton.OnToggleChanged() {

			@Override
			public void onToggle(boolean on) {
				// TODO Auto-generated method stub
				spUtil.saveIsDayMode(!on);
				if (on) {
					chage2Night();
				} else {
					chage2Day();
				}
			}
		});
//		layout_download.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
//
//			@Override
//			public void onToggle(boolean on) {
//				// TODO Auto-generated method stub
//				MeSetActivity.this.spUtil.setFlow(on);
//			}
//		});

		UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
			@Override
			public void onUpdateReturned(int updateStatus,
					UpdateResponse updateInfo) {
				switch (updateStatus) {
				case UpdateStatus.Yes: // has update
					UmengUpdateAgent.showUpdateDialog(MeSetActivity.this,
							updateInfo);
					break;
				case UpdateStatus.No: // has no update
					ToastUtils.Infotoast(MeSetActivity.this, "已是最新版本~");
					// Toast.makeText(mContext, "没有更新",
					// Toast.LENGTH_SHORT).show();
					break;
				// case UpdateStatus.NoneWifi: // none wifi
				// Toast.makeText(mContext, "没有wifi连接， 只在wifi下更新",
				// Toast.LENGTH_SHORT).show();
				// break;
				case UpdateStatus.Timeout: // time out
					ToastUtils.Errortoast(MeSetActivity.this, "连接超时~");
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
		initToggle();
		initNightView(false);
		float filelength = getFolderSize(CommonUtils.getImageCachePath(this))
				/ 1024 / 1024
				+ getFolderSize(CommonUtils.getVideoCachePath(this)) / 1024
				/ 1024;
		float filele_rusult = (float) (Math.round(filelength * 10)) / 10;//
		// 这里的100就是2位小数点,如果要其它位,如4位,这里两个100改成10000
		layout_detele_now.setText("当前缓存" + Float.toString(filele_rusult) + "M");
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
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.layout_my_foot:
			this.startAnimActivity(NewsBrowseRecordActivity.class);
			break;
		case R.id.font_size_set:
			// mActivity.startAnimActivity(New_Activity_My_About.class);
			float radix = spUtil.getFontSizeRadix();
			int index = 1;
			for (int i = 0; i < FontUtils.fontSize.length; i++) {
				if (FontUtils.fontSize[i] == radix) {
					index = i;
					break;
				}
			}
			AlertDialog ad = new AlertDialog.Builder(this)
					.setTitle("选择区域")
					.setSingleChoiceItems(FontUtils.fontSizeShow, index,
							new RadioOnClick(index)).create();
			// areaRadioListView = ad.getListView();
			ad.show();
			break;
		case R.id.layout_about:
			this.startAnimActivity(New_Activity_My_About.class);
			break;

		case R.id.layout_delete:
			delete();
			break;

		case R.id.layout_fankui:
			Intent intent = new Intent();
			intent.setClass(this, New_Activity_My_FanKui.class);
			String id = new FeedbackAgent(this).getDefaultConversation()
					.getId();
			intent.putExtra(FeedbackFragment.BUNDLE_KEY_CONVERSATION_ID, id);
			startActivity(intent);
			break;

		case R.id.layout_updata:
			if (CommonUtils.isNetworkAvailable(this)) {
				UmengUpdateAgent.forceUpdate(this);
			} else {
				ToastUtils.ErrorToastNoNet(this);
			}
			break;
		case R.id.title_bar_left_img:
			onBackPressed();
			break;

		default:
			break;
		}
	}

	private void delete() {
		final InfoMsgHint dialog = new InfoMsgHint(this, R.style.MyDialog1);

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
				delAllFile(CommonUtils.getImageCachePath(MeSetActivity.this)
						.toString());

				try {
					String sql = "Update com_kankan_kankanews_bean_New_News set looktime = '0'";
					MeSetActivity.this.dbUtils.execNonQuery(sql);
				} catch (DbException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				layout_detele_now.setText("当前缓存0.0M");
				ToastUtils.Infotoast(MeSetActivity.this, "清除缓存成功!");
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

	class RadioOnClick implements DialogInterface.OnClickListener {
		private int index;

		public RadioOnClick(int index) {
			this.index = index;
		}

		public void setIndex(int index) {
			this.index = index;
		}

		public int getIndex() {
			return index;
		}

		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			setIndex(which);
			Toast.makeText(MeSetActivity.this,
					"您已经选择了： " + FontUtils.fontSizeShow[index],
					Toast.LENGTH_LONG).show();
			spUtil.saveFontSizeRadix(FontUtils.fontSize[index]);
			FontUtils.chagneFontSizeGlobal();
			dialog.dismiss();
		}
	}

	private void initToggle() {
		if (this.spUtil.getIsDayMode()) {
			isDayMode.setToggleOff();
		} else {
			isDayMode.setToggleOn();
		}
//		if (this.spUtil.isFlow()) {
//			layout_download.setToggleOn();
//		} else {
//			layout_download.setToggleOff();
//		}
	}
}
