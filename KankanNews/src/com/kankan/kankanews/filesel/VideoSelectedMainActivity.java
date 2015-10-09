package com.kankan.kankanews.filesel;

import java.io.File;
import java.io.FilenameFilter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kankan.kankanews.bean.ImageFloder;
import com.kankan.kankanews.bean.Video;
import com.kankan.kankanews.config.AndroidConfig;
import com.kankan.kankanews.filesel.ListVideoDirPopupWindow.OnImageDirSelected;
import com.kankan.kankanews.utils.DebugLog;
import com.kankanews.kankanxinwen.R;

public class VideoSelectedMainActivity extends Activity implements
		OnImageDirSelected {
	private ProgressDialog mProgressDialog;

	/**
	 * 图片数量最多的文件夹
	 */
	private File mVideoDir;
	/**
	 * 所有的图片
	 */
	private Map<String, List<Video>> mVideos = new HashMap<String, List<Video>>();

	private List<Video> allVideos = new ArrayList<Video>();

	private Video mSelectedVideo;

	private GridView mGirdView;
	private VideoSelectedGridAdapter mAdapter;

	/**
	 * 扫描拿到所有的图片文件夹
	 */
	private Map<String, ImageFloder> mVideoFloders = new HashMap<String, ImageFloder>();

	private RelativeLayout mBottomLy;

	private TextView mChooseDir;
	private TextView mVideoCount;
	private TextView selectedNum;
	private Button selectedOk;
	int totalCount = 0;

	private int mScreenHeight;

	private ListVideoDirPopupWindow mListVideoDirPopupWindow;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			mProgressDialog.dismiss();
			// 为View绑定数据
			data2View();
			// 初始化展示文件夹的popupWindw
			initListDirPopupWindw();
		}
	};

	/**
	 * 为View绑定数据
	 */
	private void data2View() {
		if (mVideoDir == null) {
			Toast.makeText(getApplicationContext(), "不好意思，一段视频没扫描到",
					Toast.LENGTH_SHORT).show();
			return;
		}
		mAdapter = new VideoSelectedGridAdapter(getApplicationContext(),
				allVideos, R.layout.pic_selected_grid_item, "", this);
		mGirdView.setAdapter(mAdapter);
		mVideoCount.setText(totalCount + "段");
	};

	/**
	 * 初始化展示文件夹的popupWindw
	 */
	private void initListDirPopupWindw() {
		mListVideoDirPopupWindow = new ListVideoDirPopupWindow(
				LayoutParams.MATCH_PARENT, (int) (mScreenHeight * 0.7),
				new ArrayList<ImageFloder>(mVideoFloders.values()),
				LayoutInflater.from(getApplicationContext()).inflate(
						R.layout.pic_selected_list_dir, null));

		mListVideoDirPopupWindow.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				// 设置背景颜色变暗
				WindowManager.LayoutParams lp = getWindow().getAttributes();
				lp.alpha = 1.0f;
				getWindow().setAttributes(lp);
			}
		});
		// 设置选择文件夹的回调
		mListVideoDirPopupWindow.setOnImageDirSelected(this);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pic_selected_activity_main);

		DisplayMetrics outMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
		mScreenHeight = outMetrics.heightPixels;

		initView();
		getImages();
		initEvent();

	}

	/**
	 * 利用ContentProvider扫描手机中的图片，此方法在运行在子线程中 完成图片的扫描，最终获得jpg最多的那个文件夹
	 */
	private void getImages() {
		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			Toast.makeText(this, "暂无外部存储", Toast.LENGTH_SHORT).show();
			return;
		}
		// 显示进度条
		mProgressDialog = ProgressDialog.show(this, null, "正在加载...");

		new Thread(new Runnable() {
			@Override
			public void run() {

				ImageFloder allImageFloder = new ImageFloder();
				mVideoFloders.put("", allImageFloder);

				Uri mImageUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
				ContentResolver mContentResolver = VideoSelectedMainActivity.this
						.getContentResolver();

				// 只查询jpeg和png的图片
				Cursor mCursor = mContentResolver.query(mImageUri, null, null,
						null, MediaStore.Video.Media.DATE_MODIFIED + " DESC");

				Log.e("Cursor", mCursor.getColumnCount() + "");
				while (mCursor.moveToNext()) {
					// 获取视频编号
					Video video = new Video();
					video.setId(mCursor.getString(mCursor
							.getColumnIndex(MediaStore.Video.Media._ID)));
					video.setMimeType(mCursor.getString(mCursor
							.getColumnIndex(MediaStore.Video.Media.MIME_TYPE)));
					video.setName(mCursor.getString(mCursor
							.getColumnIndex(MediaStore.Video.Media.TITLE)));
					video.setPath(mCursor.getString(mCursor
							.getColumnIndex(MediaStore.Video.Media.DATA)));

					String selection = MediaStore.Video.Thumbnails.VIDEO_ID
							+ "=?";
					String[] selectionArgs = new String[] { video.getId() };
					Cursor thumbCursor = mContentResolver.query(
							MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI,
							new String[] { MediaStore.Video.Thumbnails.DATA,
									MediaStore.Video.Thumbnails.VIDEO_ID },
							null, null, null);
					while (thumbCursor.moveToNext()) {
						DebugLog.e(thumbCursor.getString(thumbCursor
								.getColumnIndexOrThrow(MediaStore.Video.Thumbnails.VIDEO_ID)));
					}
					// if (thumbCursor.moveToFirst()) {
					// video.setThumbPath(thumbCursor.getString(thumbCursor
					// .getColumnIndexOrThrow(MediaStore.Video.Thumbnails.DATA)));
					//
					// }
					// 获取该图片的父路径名
					File parentFile = new File(video.getPath()).getParentFile();
					if (parentFile == null)
						continue;
					String dirPath = parentFile.getAbsolutePath();
					ImageFloder imageFloder = null;
					// 利用一个HashSet防止多次扫描同一个文件夹（不加这个判断，图片多起来还是相当恐怖的~~）
					if (mVideoFloders.containsKey(dirPath)) {
						imageFloder = mVideoFloders.get(dirPath);
						imageFloder.setCount(imageFloder.getCount() + 1);
					} else {
						// 初始化imageFloder
						imageFloder = new ImageFloder();
						imageFloder.setDir(dirPath);
						imageFloder.setFirstImagePath(video.getPath());
						imageFloder.setCount(1);
						mVideoFloders.put(imageFloder.getDir(), imageFloder);
					}

					if (parentFile == null)
						continue;
					allVideos.add(video);
					totalCount += 1;

					mVideoDir = parentFile;

					if (mVideos.containsKey(dirPath)) {
						mVideos.get(dirPath).add(video);
					} else {
						List<Video> videos = new ArrayList<Video>();
						videos.add(video);
						mVideos.put(dirPath, videos);
					}

				}
				allImageFloder.setCount(totalCount);
				allImageFloder.setDir("");
				allImageFloder.setName("所有图片");

				// Log.e("allImgs", allImgs.get(0));
				// Log.e("allImgs", allImgs.get(10));
				Collections.sort(allVideos, new Comparator<Video>() {
					public int compare(Video arg0, Video arg1) {
						File file1 = new File(arg0.getPath());
						File file2 = new File(arg1.getPath());
						if (file1.lastModified() > file2.lastModified())
							return -1;
						if (file1.lastModified() == file2.lastModified())
							return 0;
						return 1;
					}
				});
				allImageFloder.setFirstImagePath(allVideos.get(0).getPath());
				mVideos.put("", allVideos);
				mCursor.close();

				// 通知Handler扫描图片完成
				mHandler.sendEmptyMessage(0x110);

			}
		}).start();

	}

	/**
	 * 初始化View
	 */
	private void initView() {
		mGirdView = (GridView) findViewById(R.id.id_gridView);
		mChooseDir = (TextView) findViewById(R.id.id_choose_dir);
		mVideoCount = (TextView) findViewById(R.id.id_total_count);
		selectedNum = (TextView) findViewById(R.id.id_pic_selected_num);
		selectedOk = (Button) findViewById(R.id.id_pic_selected_ok);

		mBottomLy = (RelativeLayout) findViewById(R.id.id_bottom_ly);

	}

	private void initEvent() {
		/**
		 * 为底部的布局设置点击事件，弹出popupWindow
		 */
		mBottomLy.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mListVideoDirPopupWindow
						.setAnimationStyle(R.style.anim_popup_dir);
				mListVideoDirPopupWindow.showAsDropDown(mBottomLy, 0, 0);

				// 设置背景颜色变暗
				WindowManager.LayoutParams lp = getWindow().getAttributes();
				lp.alpha = .3f;
				getWindow().setAttributes(lp);
			}
		});

		selectedOk.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO
				getIntent().putExtra("_SELECTED_VIDEO_",
						(Serializable) mSelectedVideo);
				setResult(AndroidConfig.REVELATIONS_FRAGMENT_RESULT_OK,
						getIntent());
				finish();
			}
		});
	}

	@Override
	public void selected(ImageFloder floder) {
		if (!"".equals(floder.getDir())) {
			mVideoDir = new File(floder.getDir());
			List<Video> videos = mVideos.get(floder.getDir());
			Collections.sort(videos, new Comparator<Video>() {
				public int compare(Video arg0, Video arg1) {
					File file1 = new File(arg0.getPath());
					File file2 = new File(arg1.getPath());
					if (file1.lastModified() > file2.lastModified())
						return -1;
					return 1;
				}
			});
			mAdapter = new VideoSelectedGridAdapter(getApplicationContext(),
					videos, R.layout.pic_selected_grid_item,
					mVideoDir.getAbsolutePath(), this);
		} else {
			mAdapter = new VideoSelectedGridAdapter(getApplicationContext(),
					allVideos, R.layout.pic_selected_grid_item, "", this);
		}
		/**
		 * 可以看到文件夹的路径和图片的路径分开保存，极大的减少了内存的消耗；
		 */

		mGirdView.setAdapter(mAdapter);
		// mAdapter.notifyDataSetChanged();
		mVideoCount.setText(floder.getCount() + "张");
		mChooseDir.setText(floder.getName());
		mListVideoDirPopupWindow.dismiss();

	}

	public TextView getSelectedNum() {
		return selectedNum;
	}

	@Override
	public void onBackPressed() {
		setResult(AndroidConfig.REVELATIONS_FRAGMENT_RESULT_CANCEL);
		super.onBackPressed();
	}

	public class VideoSelectedGridAdapter extends CommonAdapter<Video> {

		/**
		 * 文件夹路径
		 */
		private String mDirPath;
		private TextView selectedNum;
		private VideoSelectedMainActivity selectedMainActivity;

		public VideoSelectedGridAdapter(Context context, List<Video> mDatas,
				int itemLayoutId, String dirPath,
				VideoSelectedMainActivity selectedMainActivity) {
			super(context, mDatas, itemLayoutId);
			this.mDirPath = dirPath;
			this.selectedMainActivity = selectedMainActivity;
			this.selectedNum = selectedMainActivity.getSelectedNum();
		}

		@Override
		public void convert(final ViewHolder helper, final Video item) {
			// 设置no_pic
			helper.setImageResource(R.id.id_item_image, R.drawable.pictures_no);
			// 设置no_selected
			helper.setImageResource(R.id.id_item_select,
					R.drawable.picture_unselected);
			// 设置图片
			helper.setVideoImage(R.id.id_item_image, item.getPath());

			final ImageView mImageView = helper.getView(R.id.id_item_image);
			final ImageView mSelect = helper.getView(R.id.id_item_select);

			mImageView.setColorFilter(null);
			// 设置ImageView的点击事件
			mImageView.setOnClickListener(new OnClickListener() {
				// 选择，则将图片变暗，反之则反之
				@Override
				public void onClick(View v) {

					// 已经选择过该图片
					if (mSelectedVideo == item) {
						mSelectedVideo = null;
						mSelect.setImageResource(R.drawable.picture_unselected);
						mImageView.setColorFilter(null);
					} else
					// 未选择该图片
					{
						if (mSelectedVideo == null) {
							mSelectedVideo = item;
							mSelect.setImageResource(R.drawable.pictures_selected);
							mImageView.setColorFilter(Color
									.parseColor("#77000000"));
						}
					}
					selectedNum.setText(mSelectedVideo == null ? "0" : "1"
							+ "/1段");
				}
			});

			/**
			 * 已经选择过的图片，显示出选择过的效果
			 */
			if (mSelectedVideo == item) {
				mSelect.setImageResource(R.drawable.pictures_selected);
				mImageView.setColorFilter(Color.parseColor("#77000000"));
			}
			selectedNum.setText(mSelectedVideo == null ? "0" : "1" + "/1段");

		}
	}
}
