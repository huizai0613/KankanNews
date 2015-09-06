package com.kankan.kankanews.picsel;

import java.io.File;
import java.io.FilenameFilter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.database.Cursor;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kankan.kankanews.bean.ImageFloder;
import com.kankan.kankanews.config.AndroidConfig;
import com.kankan.kankanews.picsel.ListImageDirPopupWindow.OnImageDirSelected;
import com.kankanews.kankanxinwen.R;

public class PicSelectedMainActivity extends Activity implements
		OnImageDirSelected {
	private ProgressDialog mProgressDialog;

	/**
	 * 存储文件夹中的图片数量
	 */
	private int mPicsSize;
	/**
	 * 图片数量最多的文件夹
	 */
	private File mImgDir;
	/**
	 * 所有的图片
	 */
	private List<String> mImgs = new ArrayList<String>();

	private List<String> allImgs = new ArrayList<String>();

	private GridView mGirdView;
	private PicSelectedGridAdapter mAdapter;
	/**
	 * 临时的辅助类，用于防止同一个文件夹的多次扫描
	 */
	private HashSet<String> mDirPaths = new HashSet<String>();

	/**
	 * 扫描拿到所有的图片文件夹
	 */
	private List<ImageFloder> mImageFloders = new ArrayList<ImageFloder>();

	private RelativeLayout mBottomLy;

	private TextView mChooseDir;
	private TextView mImageCount;
	private TextView selectedNum;
	private Button selectedOk;
	int totalCount = 0;

	private int mScreenHeight;

	private ListImageDirPopupWindow mListImageDirPopupWindow;

	private List<String> mSelectedImage = new LinkedList<String>();;

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
		if (mImgDir == null) {
			Toast.makeText(getApplicationContext(), "不好意思，一张图片没扫描到",
					Toast.LENGTH_SHORT).show();
			return;
		}

		// for (ImageFloder floder : mImageFloders) {
		// // mImgs.addAll(Arrays.asList(new File(floder.getDir()).list()));
		// mImgDir = new File(floder.getDir());
		// mImgs.addAll(Arrays.asList(mImgDir.list(new FilenameFilter()
		// {
		// @Override
		// public boolean accept(File dir, String filename)
		// {
		// if (filename.endsWith(".jpg") || filename.endsWith(".png")
		// || filename.endsWith(".jpeg") || filename.endsWith(".JPG") ||
		// filename.endsWith(".PNG")
		// || filename.endsWith(".JPEG") && new File(dir+ "/"
		// +filename).length() < AndroidConfig.MAX_SEL_IMG_LENGTH)
		// return true;
		// return false;
		// }
		// })));
		// }

		// mImgs = Arrays.asList(mImgDir.list());

		/**
		 * 可以看到文件夹的路径和图片的路径分开保存，极大的减少了内存的消耗；
		 */
		// mImgs = Arrays.asList(mImgDir.list());
		// String[] array = (String[])allImgs.toArray();
		// List<String> tmpList = Arrays.asList(array);

		// tmpList.addAll(allImgs);
		// Collections.sort(allImgs, new Comparator<String>() {
		// public int compare(String arg0, String arg1) {
		// File file1 = new File(arg0);
		// File file2 = new File(arg1);
		// if(file1.lastModified() > file2.lastModified())
		// return 1;
		// return -1;
		// }
		// });

		// Log.e("allImgs", allImgs.get(0));
		// Log.e("allImgs", allImgs.get(10));
		mAdapter = new PicSelectedGridAdapter(getApplicationContext(), allImgs,
				R.layout.pic_selected_grid_item, "", this);
		mGirdView.setAdapter(mAdapter);
		mImageCount.setText(totalCount + "张");
	};

	/**
	 * 初始化展示文件夹的popupWindw
	 */
	private void initListDirPopupWindw() {
		mListImageDirPopupWindow = new ListImageDirPopupWindow(
				LayoutParams.MATCH_PARENT, (int) (mScreenHeight * 0.7),
				mImageFloders, LayoutInflater.from(getApplicationContext())
						.inflate(R.layout.pic_selected_list_dir, null));

		mListImageDirPopupWindow.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				// 设置背景颜色变暗
				WindowManager.LayoutParams lp = getWindow().getAttributes();
				lp.alpha = 1.0f;
				getWindow().setAttributes(lp);
			}
		});
		// 设置选择文件夹的回调
		mListImageDirPopupWindow.setOnImageDirSelected(this);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pic_selected_activity_main);

		DisplayMetrics outMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
		mScreenHeight = outMetrics.heightPixels;

		List<String> mainSeleted = (List<String>) this.getIntent()
				.getSerializableExtra("IMAGE_SELECTED_LIST");
		mSelectedImage.addAll(mainSeleted);

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
				mImageFloders.add(allImageFloder);
				String firstImage = null;

				Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				ContentResolver mContentResolver = PicSelectedMainActivity.this
						.getContentResolver();

				// 只查询jpeg和png的图片
				Cursor mCursor = mContentResolver.query(mImageUri, null, "("
						+ MediaStore.Images.Media.MIME_TYPE + "=? or "
						+ MediaStore.Images.Media.MIME_TYPE + "=? or "
						+ MediaStore.Images.Media.MIME_TYPE + "=? ) and "
						+ MediaStore.Images.Media.SIZE + "<"
						+ AndroidConfig.MAX_SEL_IMG_LENGTH, new String[] {
						"image/jpeg", "image/png", "image/jpg" },
						MediaStore.Images.Media.DATE_MODIFIED + " DESC");

				Log.e("Cursor", mCursor.getColumnCount() + "");
				while (mCursor.moveToNext()) {
					// 获取图片的路径
					String path = mCursor.getString(mCursor
							.getColumnIndex(MediaStore.Images.Media.DATA));

					// 拿到第一张图片的路径
					if (firstImage == null)
						firstImage = path;
					// 获取该图片的父路径名
					File parentFile = new File(path).getParentFile();
					if (parentFile == null)
						continue;
					String dirPath = parentFile.getAbsolutePath();
					ImageFloder imageFloder = null;
					// 利用一个HashSet防止多次扫描同一个文件夹（不加这个判断，图片多起来还是相当恐怖的~~）
					if (mDirPaths.contains(dirPath)) {
						continue;
					} else {
						mDirPaths.add(dirPath);
						// 初始化imageFloder
						imageFloder = new ImageFloder();
						imageFloder.setDir(dirPath);
						imageFloder.setFirstImagePath(path);
					}

					if (parentFile == null)
						continue;
					int picSize = 0;
					try {

						String[] parentFileList = parentFile
								.list(new FilenameFilter() {
									@Override
									public boolean accept(File dir,
											String filename) {
										long length = new File(dir + "/"
												+ filename).length();
										if ((filename.endsWith(".jpg")
												|| filename.endsWith(".JPG")
												|| filename.endsWith(".png")
												|| filename.endsWith(".PNG")
												|| filename.endsWith(".jpeg") || filename
													.endsWith(".JPEG"))
												&& length < AndroidConfig.MAX_SEL_IMG_LENGTH)
											return true;
										return false;
									}
								});
						for (String imgPath : parentFileList) {
							allImgs.add(parentFile + "/" + imgPath);
						}
						// imageFloder.setFirstImagePath(path);
						// allImgs.addAll(Arrays.asList(parentFileList));
						picSize = parentFileList.length;
					} catch (NullPointerException e) {
						Log.e("NULLPOINT", e.getLocalizedMessage(), e);
						continue;
					}
					totalCount += picSize;

					imageFloder.setCount(picSize);
					mImageFloders.add(imageFloder);

					if (picSize > mPicsSize) {
						mPicsSize = picSize;
						mImgDir = parentFile;
					}
				}
				allImageFloder.setCount(totalCount);
				allImageFloder.setDir("");
				allImageFloder.setName("所有图片");

				// Log.e("allImgs", allImgs.get(0));
				// Log.e("allImgs", allImgs.get(10));
				Collections.sort(allImgs, new Comparator<String>() {
					public int compare(String arg0, String arg1) {
						File file1 = new File(arg0);
						File file2 = new File(arg1);
						if (file1.lastModified() > file2.lastModified())
							return -1;
						if (file1.lastModified() == file2.lastModified())
							return 0;
						return 1;
					}
				});
				allImageFloder.setFirstImagePath(allImgs.get(0));

				// Log.e("allImgs", allImgs.get(0));
				// Log.e("allImgs", allImgs.get(10));
				mCursor.close();

				// 扫描完成，辅助的HashSet也就可以释放内存了
				mDirPaths = null;

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
		mImageCount = (TextView) findViewById(R.id.id_total_count);
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
				mListImageDirPopupWindow
						.setAnimationStyle(R.style.anim_popup_dir);
				mListImageDirPopupWindow.showAsDropDown(mBottomLy, 0, 0);

				// 设置背景颜色变暗
				WindowManager.LayoutParams lp = getWindow().getAttributes();
				lp.alpha = .3f;
				getWindow().setAttributes(lp);
			}
		});

		selectedOk.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				getIntent().putExtra("NEW_IMAGE_SELECTED_LIST",
						(Serializable) mSelectedImage);
				setResult(AndroidConfig.REVELATIONS_FRAGMENT_RESULT_OK,
						getIntent());
				finish();
			}
		});
	}

	@Override
	public void selected(ImageFloder floder) {
		if (!"".equals(floder.getDir())) {
			mImgDir = new File(floder.getDir());
			mImgs = Arrays.asList(mImgDir.list(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String filename) {
					if ((filename.endsWith(".jpg") || filename.endsWith(".png")
							|| filename.endsWith(".jpeg")
							|| filename.endsWith(".JPG")
							|| filename.endsWith(".PNG") || filename
								.endsWith(".JPEG"))
							&& new File(dir + "/" + filename).length() < AndroidConfig.MAX_SEL_IMG_LENGTH)
						return true;
					return false;
				}
			}));
			Collections.sort(mImgs, new Comparator<String>() {
				public int compare(String arg0, String arg1) {
					File file1 = new File(mImgDir.getAbsolutePath() + "/"
							+ arg0);
					File file2 = new File(mImgDir.getAbsolutePath() + "/"
							+ arg1);
					if (file1.lastModified() > file2.lastModified())
						return -1;
					return 1;
				}
			});
			mAdapter = new PicSelectedGridAdapter(getApplicationContext(),
					mImgs, R.layout.pic_selected_grid_item,
					mImgDir.getAbsolutePath(), this);
		} else {
			mAdapter = new PicSelectedGridAdapter(getApplicationContext(),
					allImgs, R.layout.pic_selected_grid_item, "", this);
		}
		/**
		 * 可以看到文件夹的路径和图片的路径分开保存，极大的减少了内存的消耗；
		 */

		mGirdView.setAdapter(mAdapter);
		// mAdapter.notifyDataSetChanged();
		mImageCount.setText(floder.getCount() + "张");
		mChooseDir.setText(floder.getName());
		mListImageDirPopupWindow.dismiss();

	}

	public TextView getSelectedNum() {
		return selectedNum;
	}

	public List<String> getSelectedImage() {
		// TODO Auto-generated method stub
		return mSelectedImage;
	}

	@Override
	public void onBackPressed() {
		setResult(AndroidConfig.REVELATIONS_FRAGMENT_RESULT_CANCEL);
		super.onBackPressed();
	}
}
