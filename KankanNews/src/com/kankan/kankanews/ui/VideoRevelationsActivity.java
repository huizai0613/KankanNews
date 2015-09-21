package com.kankan.kankanews.ui;

import java.io.File;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.AlignmentSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.kankan.kankanews.base.BaseActivity;
import com.kankan.kankanews.base.BaseFragment;
import com.kankan.kankanews.bean.ResultInfo;
import com.kankan.kankanews.config.AndroidConfig;
import com.kankan.kankanews.picsel.PicPreviewActivity;
import com.kankan.kankanews.picsel.PicSelectedMainActivity;
import com.kankan.kankanews.ui.view.popup.ModifyAvatarDialog;
import com.kankan.kankanews.utils.CommonUtils;
import com.kankan.kankanews.utils.ImageLoader;
import com.kankan.kankanews.utils.ImageLoader.Type;
import com.kankan.kankanews.utils.ImgUtils;
import com.kankan.kankanews.utils.JsonUtils;
import com.kankan.kankanews.utils.ToastUtils;
import com.kankanews.kankanxinwen.R;

public class VideoRevelationsActivity extends BaseActivity implements
		OnClickListener {
	private String aid;
	private LayoutInflater inflate;

	private EditText contentText;
	private TextView contentNumText;
	private EditText telText;
	private TextView postBut;
	private TextView postValidateBut;
	private TextView telBindingCancelBut;
	private TextView validateTextView;
	private TextView telBindingView;
	private TextView telBindingNameView;
	private GridView imageGridView;
	private ScrollView inputContentView;
	private ScrollView postView;
	private ImageGroupGridAdapter gridAdapter;
	private List<String> imagesSelected = new LinkedList<String>();
	private List<String> imagesSelectedUrl = new LinkedList<String>();

	public static final String IMAGE_PATH = "My_weixin";
	public static final File FILE_SDCARD = Environment
			.getExternalStorageDirectory();
	public static final File FILE_LOCAL = new File(FILE_SDCARD, IMAGE_PATH);
	public static final File FILE_PIC_SCREENSHOT = new File(FILE_LOCAL,
			"images/screenshots");

	private static int _STATUS_INPUT_CONTENT_ = 8001;
	private static int _STATUS_POST_ = 8002;

	private int status = _STATUS_INPUT_CONTENT_;

	private TimeCount timeCount = new TimeCount(60000, 1000);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_revelations);

	}

	@Override
	protected void initView() {
		inflate = LayoutInflater.from(this);
		contentText = (EditText) this
				.findViewById(R.id.revelations_post_content);
		contentNumText = (TextView) this
				.findViewById(R.id.revelations_post_content_num);
		telText = (EditText) this.findViewById(R.id.revelations_post_tel);
		postBut = (TextView) this.findViewById(R.id.revelations_post_button);
		postValidateBut = (TextView) this
				.findViewById(R.id.revelations_post_validate_button);
		inputContentView = (ScrollView) this
				.findViewById(R.id.imput_content_scroll_view);
		postView = (ScrollView) this.findViewById(R.id.post_scroll_view);
		imageGridView = (GridView) this
				.findViewById(R.id.revelations_post_image_grid);
		validateTextView = (TextView) this
				.findViewById(R.id.revelations_validate_message_text_view);
		telBindingView = (TextView) this
				.findViewById(R.id.revelations_binding_telephone_text_view);
		telBindingNameView = (TextView) this
				.findViewById(R.id.revelations_binding_telephone_name_text_view);
		telBindingCancelBut = (TextView) this
				.findViewById(R.id.revelations_binding_cancel_button);
		gridAdapter = new ImageGroupGridAdapter();
		imageGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		imageGridView.setAdapter(gridAdapter);
		initTitleLeftBar("我要报料", R.drawable.new_ic_back);
	}

	@Override
	protected void setListener() {
		postBut.setOnClickListener(this);
		postValidateBut.setOnClickListener(this);
		telBindingCancelBut.setOnClickListener(this);
		contentText.addTextChangedListener(new MaxLengthWatcher(300,
				contentText));
		setOnLeftClickLinester(this);
	}

	class MaxLengthWatcher implements TextWatcher {

		private int maxLen = 0;
		private EditText editText = null;

		public MaxLengthWatcher(int maxLen, EditText editText) {
			this.maxLen = maxLen;
			this.editText = editText;
		}

		public void afterTextChanged(Editable arg0) {
			// TODO Auto-generated method stub

		}

		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {
			// TODO Auto-generated method stub

		}

		public void onTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {
			// TODO Auto-generated method stub
			Editable editable = editText.getText();
			int len = editable.length();

			if (len > maxLen) {
				int selEndIndex = Selection.getSelectionEnd(editable);
				String str = editable.toString();
				// 截取新字符串
				String newStr = str.substring(0, maxLen);
				editText.setText(newStr);
				editable = editText.getText();

				// 新字符串的长度
				int newLen = editable.length();
				// 旧光标位置超过字符串长度
				if (selEndIndex > newLen) {
					selEndIndex = editable.length();
				}
				// 设置新光标所在的位置
				Selection.setSelection(editable, selEndIndex);
			}
			contentNumText.setText(editable.length() + "/300字");
		}

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int id = v.getId();
		switch (id) {
		case R.id.title_bar_left_img:
			onBackPressed();
			break;
		case R.id.revelations_binding_cancel_button:
			spUtil.saveUserTelephone(null);
			telBindingCancelBut.setVisibility(View.GONE);
			telBindingView.setVisibility(View.GONE);
			telBindingNameView.setVisibility(View.GONE);
			postBut.setText("下一步");
			break;
		case R.id.revelations_post_validate_button:
			if (telText.getText().length() == 0
					|| !isPhoneNum(telText.getText().toString())) {
				telText.requestFocus();
				ToastUtils.Errortoast(this, "请填写正确的电话号码");
				break;
			}
			if (CommonUtils.isNetworkAvailable(this)) {
				postValidateBut.setClickable(false);
				postValidateBut.setText("等待");
				new PostValidateMessageTask().execute("");
			}
			break;
		case R.id.revelations_post_button:
			if (this.status == _STATUS_INPUT_CONTENT_) {
				if (contentText.getText().length() == 0) {
					contentText.requestFocus();
					ToastUtils.Errortoast(this, "报料内容不得为空");
					break;
				}
				if (spUtil.getUserTelephone() == null
						|| spUtil.getUserTelephone().equals("")) {
					this.status = _STATUS_POST_;
					inputContentView.setVisibility(View.GONE);
					postView.setVisibility(View.VISIBLE);
					postBut.setText("提交");
				} else {
					if (CommonUtils.isNetworkAvailable(this)) {
						new PostTask().execute("");
						postBut.setText("正在提交");
						postBut.setEnabled(false);
						postBut.setBackgroundColor(Color.parseColor("#BEBEBE"));
					}
				}

				break;
			} else if (this.status == _STATUS_POST_) {
				if (validateTextView.getText().length() == 0) {
					validateTextView.requestFocus();
					ToastUtils.Errortoast(this, "验证码不得为空");
					break;
				}
				if (telText.getText().length() == 0
						|| !isPhoneNum(telText.getText().toString())) {
					telText.requestFocus();
					ToastUtils.Errortoast(this, "请填写正确的电话号码");
					break;
				}
				if (CommonUtils.isNetworkAvailable(this)) {
					new PostTask().execute("");
					postBut.setText("正在提交");
					postBut.setEnabled(false);
					postBut.setBackgroundColor(Color.parseColor("#BEBEBE"));
				}
			}
		}
	}

	private boolean isPhoneNum(String phoneNum) {
		// TODO Auto-generated method stub
		Pattern pattern = Pattern
				.compile("(\\d{11})|^((\\d{7,8})|(\\d{4}|\\d{3})-(\\d{7,8})|(\\d{4}|\\d{3})-(\\d{7,8})-(\\d{4}|\\d{3}|\\d{2}|\\d{1})|(\\d{7,8})-(\\d{4}|\\d{3}|\\d{2}|\\d{1}))");
		Matcher matcher = pattern.matcher(phoneNum);
		return matcher.matches();
	}

	private void goSelect() {
		// TODO Auto-generated method stub
		ModifyAvatarDialog modifyAvatarDialog = new ModifyAvatarDialog(this) {
			// 选择本地相册
			@Override
			public void doGoToImg() {
				this.dismiss();
				Intent intent = new Intent(VideoRevelationsActivity.this,
						PicSelectedMainActivity.class);
				intent.putExtra("IMAGE_SELECTED_LIST",
						(Serializable) imagesSelected);
				startActivityForResult(intent,
						AndroidConfig.REVELATIONS_FRAGMENT_REQUEST_NO);
			}

			// 选择相机拍照
			@Override
			public void doGoToPhone() {
				this.dismiss();
				String status = Environment.getExternalStorageState();
				if (status.equals(Environment.MEDIA_MOUNTED)) {
					try {
						// String localTempImageFileName = "";
						// localTempImageFileName = String.valueOf((new Date())
						// .getTime()) + ".png";
						// File filePath = FILE_PIC_SCREENSHOT;
						// if (!filePath.exists()) {
						// filePath.mkdirs();
						// }
						Intent intent = new Intent(
								android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
						// RevelationsActivity.this.mApplication.getCacheDir()
						// File f = new File(
						// CommonUtils
						// .getImageCachePath(getApplicationContext()),
						// localTempImageFileName);
						// File f = new File(filePath, localTempImageFileName);
						// localTempImgDir和localTempImageFileName是自己定义的名字
						// Uri u = Uri.fromFile(f);
						intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
						// intent.putExtra(MediaStore.EXTRA_OUTPUT, u);
						// intent.setDataAndType(u, "image/*");
						startActivityForResult(
								intent,
								AndroidConfig.REVELATIONS_FRAGMENT_PHOTO_REQUEST_NO);
					} catch (ActivityNotFoundException e) {
						//
						Log.e("ActivityNotFoundException",
								e.getLocalizedMessage());
					}
				}
			}
		};
		AlignmentSpan span = new AlignmentSpan.Standard(
				Layout.Alignment.ALIGN_CENTER);
		AbsoluteSizeSpan span_size = new AbsoluteSizeSpan(25, true);
		SpannableStringBuilder spannable = new SpannableStringBuilder();
		String dTitle = "请选择图片";
		spannable.append(dTitle);
		spannable.setSpan(span, 0, dTitle.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		spannable.setSpan(span_size, 0, dTitle.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		modifyAvatarDialog.setTitle(spannable);
		modifyAvatarDialog.show();
	}

	private void foPreview() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(this, PicPreviewActivity.class);
		intent.putExtra("IMAGE_SELECTED_LIST", (Serializable) imagesSelected);
		this.startActivityForResult(intent,
				AndroidConfig.REVELATIONS_FRAGMENT_REQUEST_NO);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) {
		case AndroidConfig.REVELATIONS_FRAGMENT_RESULT_OK:
			imagesSelected.clear();
			imagesSelectedUrl.clear();
			List<String> mainSeleted = (List<String>) data
					.getSerializableExtra("NEW_IMAGE_SELECTED_LIST");
			imagesSelected.addAll(mainSeleted);
			refreshImages();
			break;
		}
		switch (requestCode) {
		case AndroidConfig.REVELATIONS_FRAGMENT_PHOTO_REQUEST_NO:
			Uri uri = data.getData();
			if (uri == null) {
				// use bundle to get data
				Bundle bundle = data.getExtras();
				if (bundle != null) {
					Bitmap photo = (Bitmap) bundle.get("data"); // get bitmap
					Log.e("URI", photo.toString());
					// spath :生成图片取个名字和路径包含类型
					String localTempImageFileName = "";
					localTempImageFileName = String.valueOf((new Date())
							.getTime()) + ".png";
					String path = CommonUtils.getCameraImageCachePath(
							getApplicationContext()).getPath()
							+ localTempImageFileName;
					boolean flag = ImgUtils.saveImage(photo, path);
					if (flag) {
						imagesSelected.add(path);
						gridAdapter.notifyDataSetChanged();
					} else {
						ToastUtils.Errortoast(this, "保存图片失败请重试");
					}
				} else {
					ToastUtils.Errortoast(this, "保存图片失败请重试");
					return;
				}
			} else {
				ToastUtils.Errortoast(this, "保存图片失败请重试");
				// to do find the path of pic by uri
			}
			break;
		}

	}

	private void refreshImages() {
		// TODO Auto-generated method stub
		// for (int i = 0; i < imageViews.length; i++) {
		// if (i < imagesSelected.size()) {
		// ImageLoader.getInstance(3, Type.LIFO).loadImage(
		// imagesSelected.get(i), imageViews[i]);
		// imageViews[i].setVisibility(View.VISIBLE);
		// } else if (i == imagesSelected.size()) {
		// imageViews[i].setImageResource(R.drawable.revelations_add_pic);
		// imageViews[i].setVisibility(View.VISIBLE);
		// } else {
		// imageViews[i].setVisibility(View.INVISIBLE);
		// }
		// }
		// imageGridView.setAdapter(new ImageGroupGridAdapter());
		gridAdapter.notifyDataSetChanged();
		// imageGridView.postInvalidate();
	}

	private class PostTask extends AsyncTask<String, Void, Map<String, String>> {

		// Map<String, String> ResultCode ResultText
		@Override
		protected Map<String, String> doInBackground(String... params) {
			// TODO Auto-generated method stub
			Map<String, String> taskResult = new HashMap<String, String>();
			String tel;
			if (spUtil.getUserTelephone() == null
					|| spUtil.getUserTelephone().equals("")) {
				tel = telText.getText().toString();

				String validateCode = validateTextView.getText().toString();
				Map<String, String> valiResponse = ImgUtils
						.validateRevelationsValidateMessage(tel, validateCode);
				if (valiResponse.get("ResponseCode").equals(
						AndroidConfig.RESPONSE_CODE_OK)) {
					ResultInfo info = JsonUtils.toObject(
							valiResponse.get("ResponseContent"),
							ResultInfo.class);
					if (info.getResultCode() == 0) {
						taskResult.put("ResultCode", "ERROR");
						taskResult.put("ResultText", info.getMsg());
						return taskResult;
					} else {
						spUtil.saveUserTelephone(tel);
					}
				} else {
					taskResult.put("ResultCode", "ERROR");
					taskResult.put("ResultText", "验证码验证失败");
					return taskResult;
				}
			} else {
				tel = spUtil.getUserTelephone();
			}

			imagesSelectedUrl.clear();
			for (int i = 0; i < imagesSelected.size(); i++) {
				if (i < imagesSelectedUrl.size())
					continue;
				Map<String, String> response = ImgUtils
						.sendImage(imagesSelected.get(i));
				if (response.get("ResponseCode").equals(
						AndroidConfig.RESPONSE_CODE_OK)) {
					imagesSelectedUrl.add(response.get("ResponseContent"));
				} else {
					taskResult.put("ResultCode", "ERROR");
					taskResult.put("ResultText", "上传图片失败请重新上传");
					return taskResult;
				}
			}

			String content = contentText.getText().toString();
			StringBuffer imageUrls = new StringBuffer();
			for (int i = 0; i < imagesSelectedUrl.size(); i++) {
				imageUrls.append(imagesSelectedUrl.get(i));
				if (i != imagesSelectedUrl.size() - 1)
					imageUrls.append("|");
			}
			// instance.postRevelationContent(tel, content,
			// imageUrls.toString(), this.mListenerObject, mErrorListener);
			Map<String, String> result = ImgUtils.sendRevelationsContent(tel,
					content, imageUrls.toString(), aid);
			taskResult.put("ResultCode", result.get("ResponseCode"));
			taskResult.put("ResultText", result.get("ResponseContent"));
			return taskResult;
		}

		@Override
		protected void onPostExecute(Map<String, String> result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (result.get("ResultCode").equals(AndroidConfig.RESPONSE_CODE_OK)) {
				ToastUtils.Infotoast(VideoRevelationsActivity.this, "上传成功");
				cleanRevelation();
				VideoRevelationsActivity.this.AnimFinsh();
			} else {
				ToastUtils.Errortoast(VideoRevelationsActivity.this,
						result.get("ResultText"));
				postBut.setText("提交");
				postBut.setEnabled(true);
				postBut.setBackgroundColor(Color.parseColor("#FF0000"));
			}
		}
	}

	private class PostValidateMessageTask extends
			AsyncTask<String, Void, Map<String, String>> {

		// Map<String, String> ResultCode ResultText
		@Override
		protected Map<String, String> doInBackground(String... params) {
			// TODO Auto-generated method stub
			Map<String, String> taskResult = new HashMap<String, String>();
			String tel = telText.getText().toString();
			Map<String, String> result = ImgUtils
					.sendRevelationsValidateMessage(VideoRevelationsActivity.this,
							tel);
			taskResult.put("ResultCode", result.get("ResponseCode"));
			taskResult.put("ResultText", result.get("ResponseContent"));
			return taskResult;
		}

		@Override
		protected void onPostExecute(Map<String, String> result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (result.get("ResultCode").equals(AndroidConfig.RESPONSE_CODE_OK)) {
				ResultInfo info = JsonUtils.toObject(result.get("ResultText"),
						ResultInfo.class);
				if (info.getResultCode() == 1) {
					ToastUtils.Infotoast(VideoRevelationsActivity.this, "发送成功");
					// 成功 倒计时
					timeCount.start();
				} else {
					postValidateBut.setClickable(true);
					postValidateBut.setText("重新验证");
					ToastUtils.Infotoast(VideoRevelationsActivity.this,
							info.getMsg());
				}
			} else {
				postValidateBut.setClickable(true);
				postValidateBut.setText("重新验证");
				ToastUtils.Errortoast(VideoRevelationsActivity.this, "获取验证码失败请重新尝试");
			}
		}

	}

	private void cleanRevelation() {
		// TODO Auto-generated method stub
		telText.setText("");
		contentText.setText("");
		imagesSelected.clear();
		imagesSelectedUrl.clear();
		// imageOne.setImageResource(R.drawable.revelations_add_pic);
		// imageOne.setVisibility(View.VISIBLE);
		// imageTwo.setVisibility(View.INVISIBLE);
		// imageThree.setVisibility(View.INVISIBLE);
		// imageFour.setVisibility(View.INVISIBLE);
		postBut.setText("提交");
		postBut.setEnabled(true);
		postBut.setBackgroundColor(Color.parseColor("#FF0000"));
	}

	private void sendRevelationContent() {

	}

	private void sendImagesError(String msg) {
		ToastUtils.Errortoast(this, msg);
	}

	@Override
	protected void onSuccess(JSONObject jsonObject) {
		ToastUtils.Infotoast(this, jsonObject.toString());
	}

	@Override
	protected void onFailure(VolleyError error) {
		// TODO Auto-generated method stub
		ToastUtils.Errortoast(this, "内容上传失败请重新上传");
	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub
		this.aid = this.getIntent().getStringExtra("_AID_");
		if (spUtil.getUserTelephone() == null
				|| spUtil.getUserTelephone().equals("")) {
			telBindingView.setVisibility(View.GONE);
			telBindingNameView.setVisibility(View.GONE);
			telBindingCancelBut.setVisibility(View.GONE);
		} else {
			telBindingView.setText(spUtil.getUserTelephone());
			telBindingView.setVisibility(View.VISIBLE);
			telBindingNameView.setVisibility(View.VISIBLE);
			telBindingCancelBut.setVisibility(View.VISIBLE);
			postBut.setText("提交");
		}
	}

	private class ImageGroupGridAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			if (imagesSelected.size() == 0)
				return 1;
			if (imagesSelected.size() == 9)
				return 9;
			return imagesSelected.size() + 1;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			// if (imageGroup.length == 4)
			return null;
			// return imageGroup[position];
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			// TODO Auto-generated method stub
			ImageView imageView = null;
			// if (convertView == null) {
			convertView = inflate.inflate(
					R.layout.item_revelations_post_image_grid_item, null);
			imageView = (ImageView) convertView
					.findViewById(R.id.post_image_item);
			// RelativeLayout.LayoutParams params =
			// (RelativeLayout.LayoutParams) imageView
			// .getLayoutParams();
			// params.width = RelativeLayout.LayoutParams.MATCH_PARENT;
			// params.height = (int) (parent.getWidth() / 3 * 0.75);
			// imageView.setLayoutParams(params);
			// convertView.setTag(imageView);
			// } else {
			// imageView = (ImageView) convertView.getTag();
			// }
			imageView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (imagesSelected.size() == position) {
						goSelect();
					} else {
						foPreview();
					}
				}
			});
			if (imagesSelected.size() == 0) {
				imageView.setImageResource(R.drawable.revelations_add_pic);

			} else if (imagesSelected.size() == 9)
				// for (String image : imagesSelected) {
				// imageView.setImageResource(image);
				ImageLoader.getInstance(3, Type.LIFO).loadImage(
						imagesSelected.get(position), imageView);
			// }
			else {
				// for (String image : imagesSelected) {
				// imageView.setImageResource(image);
				if (position == imagesSelected.size())
					imageView.setImageResource(R.drawable.revelations_add_pic);
				else
					ImageLoader.getInstance(3, Type.LIFO).loadImage(
							imagesSelected.get(position), imageView);
				// }
			}
			return convertView;
		}
	}

	class TimeCount extends CountDownTimer {
		public TimeCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
		}

		@Override
		public void onFinish() {// 计时完毕时触发
			postValidateBut.setText("重新验证");
			postValidateBut.setClickable(true);
		}

		@Override
		public void onTick(long millisUntilFinished) {// 计时过程显示
			postValidateBut.setText(millisUntilFinished / 1000 + "秒");
		}
	}

	@Override
	public void onBackPressed() {
		if (this.status == _STATUS_INPUT_CONTENT_)
			AnimFinsh();
		else if (this.status == _STATUS_POST_) {
			this.status = _STATUS_INPUT_CONTENT_;
			inputContentView.setVisibility(View.VISIBLE);
			postView.setVisibility(View.GONE);
			postBut.setText("下一步");
		}

	}
}
