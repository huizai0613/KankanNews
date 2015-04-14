package com.kankan.kankanews.ui.fragment;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.kankan.kankanews.base.BaseFragment;
import com.kankan.kankanews.config.AndroidConfig;
import com.kankan.kankanews.utils.ImageLoader;
import com.kankan.kankanews.utils.ImageLoader.Type;
import com.kankan.kankanews.utils.ImgUtils;
import com.kankan.kankannews.picsel.PicSelectedMainActivity;
import com.kankanews.kankanxinwen.R;

public class New_RevelationsFragment extends BaseFragment implements
		OnClickListener {

	private View inflate;

	private EditText contentText;
	private TextView contentNumText;
	private EditText telText;
	private Button postBut;
	private ImageView imageOne;
	private ImageView imageTwo;
	private ImageView imageThree;
	private ImageView imageFour;
	private ImageView[] imageViews = null;
	private List<String> imagesSelected = new LinkedList<String>();


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreateView(inflater, container, savedInstanceState);
		inflate = inflater.inflate(R.layout.new_fragment_revelations, null);

		initview();
		initLister();
		initData();
		return inflate;

	}

	public void initview() {
		contentText = (EditText) inflate
				.findViewById(R.id.revelations_post_content);
		contentNumText = (TextView) inflate
				.findViewById(R.id.revelations_post_content_num);
		telText = (EditText) inflate.findViewById(R.id.revelations_post_tel);
		postBut = (Button) inflate.findViewById(R.id.revelations_post_button);
		imageOne = (ImageView) inflate.findViewById(R.id.revelations_image_one);
		imageTwo = (ImageView) inflate.findViewById(R.id.revelations_image_two);
		imageThree = (ImageView) inflate
				.findViewById(R.id.revelations_image_three);
		imageFour = (ImageView) inflate
				.findViewById(R.id.revelations_image_four);
		imageViews = new ImageView[]{ imageOne, imageTwo, imageThree,
					imageFour };
		initTitle_Right_Left_bar(inflate, "看看爆料", "", "", "#ffffff", 0, 0,
				"#000000", "#000000");
	}

	private void initLister() {
		postBut.setOnClickListener(this);
		imageOne.setOnClickListener(this);
		imageTwo.setOnClickListener(this);
		imageThree.setOnClickListener(this);
		imageFour.setOnClickListener(this);
		contentText.addTextChangedListener(new MaxLengthWatcher(300,
				contentText));

	}

	private void initData() {

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
		case R.id.revelations_image_one:
		case R.id.revelations_image_two:
		case R.id.revelations_image_three:
		case R.id.revelations_image_four:
			Intent intent = new Intent(this.getActivity(),
					PicSelectedMainActivity.class);
			intent.putExtra("IMAGE_SELECTED_LIST",
					(Serializable) imagesSelected);
			this.startActivityForResult(intent,
					AndroidConfig.REVELATIONS_FRAGMENT_REQUEST_NO);
			break;
		case R.id.revelations_post_button:
			new Thread(new Runnable(){

				@Override
				public void run() {
					// TODO Auto-generated method stub
					ImgUtils.send(imagesSelected.get(0));
				}
				
			}).start();
			break;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) {
		case AndroidConfig.REVELATIONS_FRAGMENT_RESULT_OK:
			imagesSelected.clear();
			List<String> mainSeleted = (List<String>)data.getSerializableExtra("NEW_IMAGE_SELECTED_LIST");
			imagesSelected.addAll(mainSeleted);
			refreshImages();
			break;
		}
	}

	private void refreshImages() {
		// TODO Auto-generated method stub
		Log.e("IMAGE_SELECTED", imagesSelected.size() + "");
		
		for (int i =0; i < imageViews.length; i++) {
			if(i < imagesSelected.size()){
				ImageLoader.getInstance(3,Type.LIFO).loadImage(imagesSelected.get(i), imageViews[i]);
				imageViews[i].setVisibility(View.VISIBLE);
			}else if(i == imagesSelected.size()){
				imageViews[i].setImageResource(R.drawable.ic_logo);
				imageViews[i].setVisibility(View.VISIBLE);
			}else{
				imageViews[i].setVisibility(View.INVISIBLE);
			}
		}
	}

	@Override
	protected boolean initLocalDate() {
		// TODO Auto-generated method stub
		return false;
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

}
