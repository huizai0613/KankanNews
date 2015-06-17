package com.kankan.kankanews.search;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.iss.view.pulltorefresh.PullToRefreshBase;
import com.iss.view.pulltorefresh.PullToRefreshListView;
import com.iss.view.pulltorefresh.PullToRefreshBase.Mode;
import com.kankan.kankanews.base.BaseActivity;
import com.kankan.kankanews.bean.New_News_Search;
import com.kankan.kankanews.config.AndroidConfig;
import com.kankan.kankanews.exception.NetRequestException;
import com.kankan.kankanews.net.ItnetUtils;
import com.kankan.kankanews.ui.item.New_Activity_Content_Video;
import com.kankan.kankanews.ui.view.MyTextView;
import com.kankan.kankanews.utils.CommonUtils;
import com.kankan.kankanews.utils.ImgUtils;
import com.kankan.kankanews.utils.PixelUtil;
import com.kankan.kankanews.utils.ToastUtils;
import com.kankanews.kankanxinwen.R;

public class SearchMainActivity extends BaseActivity implements OnClickListener {
	private int searchContentHintKey = 10086;

	private ItnetUtils instance;
	private EditText searchContent;
	private ImageView searchIcon;
	private TextView cancelBut;
	private ImageView closeBut;
	private TextView searchBut;
	private PullToRefreshListView searchListView;
	private ListView searchHisListView;
	private SearchListAdapter searchAdapt;
	private SearchHisListAdapter searchHisAdapt;

	private int curPageNum = 1;
	private int maxSearchNum = 10;

	private List<New_News_Search> searchList = new LinkedList<New_News_Search>();

	private LayoutInflater inflate;
	private SeachListViewHolder holder;
	private SeachHisListViewHolder hisHolder;

	private boolean isLoadMore = false;

	private List<String> searchHisList = new LinkedList<String>();

	private class SeachListViewHolder {
		ImageView titlePic;
		MyTextView title;
		MyTextView click;
		MyTextView newsTime;
	}

	private class SeachHisListViewHolder {
		ImageView removeHis;
		MyTextView hisText;
		MyTextView cleanHis;
	}

	protected ErrorListener mErrorListener = new ErrorListener() {

		@Override
		public void onErrorResponse(VolleyError error) {
			onFailure(error);
		}
	};

	// 处理网络成功(JsonArray)
	protected Listener<JSONArray> mListenerArray = new Listener<JSONArray>() {
		@Override
		public void onResponse(JSONArray jsonObject) {
			onSuccessArray(jsonObject);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.search_main_activity);
		// clearHisList();
	}

	@Override
	protected void initListView() {
		// TODO Auto-generated method stub
		searchListView.setMode(Mode.PULL_FROM_END);

		// 设置PullRefreshListView上提加载时的加载提示
		searchListView.getLoadingLayoutProxy(false, true).setPullLabel(
				"上拉加载...");
		searchListView.getLoadingLayoutProxy(false, true).setRefreshingLabel(
				"正在加载请稍后…");
		searchListView.getLoadingLayoutProxy(false, true).setReleaseLabel(
				"松开加载更多...");
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		inflate = LayoutInflater.from(this);
		searchContent = (EditText) this.findViewById(R.id.search_content);
		cancelBut = (TextView) this.findViewById(R.id.search_cancel_but);
		closeBut = (ImageView) this.findViewById(R.id.search_close_but);
		searchIcon = (ImageView) this.findViewById(R.id.search_icon);
		searchBut = (TextView) this.findViewById(R.id.search_but);
		searchListView = (PullToRefreshListView) this
				.findViewById(R.id.search_list_view);
		searchHisListView = (ListView) this
				.findViewById(R.id.search_his_list_view);
	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub
		instance = ItnetUtils.getInstance(this);
		searchContent.setTag(searchContent.getHint().toString());
		initListView();
		searchAdapt = new SearchListAdapter();
		searchListView.setAdapter(searchAdapt);
		getHisList();
		searchHisAdapt = new SearchHisListAdapter();
		searchHisListView.setAdapter(searchHisAdapt);
	}

	@Override
	protected void setListener() {
		// TODO Auto-generated method stub
		searchBut.setOnClickListener(this);
		cancelBut.setOnClickListener(this);
		closeBut.setOnClickListener(this);

		searchListView
				.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2() {

					@Override
					public void onPullDownToRefresh(
							PullToRefreshBase refreshView) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onPullUpToRefresh(PullToRefreshBase refreshView) {
						// TODO Auto-generated method stub
						loadMoreNetDate();
					}
				});
		searchContent.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				String searchText = searchContent.getText().toString();
				if (hasFocus) {
					showHisList();
					if (searchText == null || searchText.equals("")) {
						searchContent.setHint("");
						closeBut.setVisibility(View.GONE);
						cancelBut.setVisibility(View.VISIBLE);
					}
				} else {
					if (searchContent.getText() == null
							|| searchText.equals("")) {
						searchContent
								.setHint(searchContent.getTag().toString());
						closeBut.setVisibility(View.VISIBLE);
						cancelBut.setVisibility(View.GONE);
					}
				}
			}
		});
		searchContent.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				Editable editable = searchContent.getText();
				int len = editable.length();

				if (len == 0) {
					searchBut.setVisibility(View.GONE);
					cancelBut.setVisibility(View.VISIBLE);
				} else {
					searchBut.setVisibility(View.VISIBLE);
					cancelBut.setVisibility(View.GONE);
				}

				if (len > maxSearchNum) {
					int selEndIndex = Selection.getSelectionEnd(editable);
					String str = editable.toString();
					// 截取新字符串
					String newStr = str.substring(0, maxSearchNum);
					searchContent.setText(newStr);
					editable = searchContent.getText();

					// 新字符串的长度
					int newLen = editable.length();
					// 旧光标位置超过字符串长度
					if (selEndIndex > newLen) {
						selEndIndex = editable.length();
					}
					// 设置新光标所在的位置
					Selection.setSelection(editable, selEndIndex);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});

		searchContent.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				// TODO Auto-generated method stub
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
					// 登陆
					goSearch();
					return true;
				} else {
					return false;
				}
			}
		});
	}

	private void loadMoreNetDate() {
		// TODO Auto-generated method stub
		isLoadMore = true;
		String searchText = searchContent.getText().toString();
		instance.getSearchData(searchText, curPageNum + 1, mListenerArray,
				mErrorListener);
	}

	private void goSearch() {
		// TODO Auto-generated method stub
		ToastUtils.Infotoast(SearchMainActivity.this, "搜索");
		String searchText = searchContent.getText().toString();
		cancelSearchContentFocus();
		curPageNum = 1;
		isLoadMore = false;
		instance.getSearchData(searchText, curPageNum, mListenerArray,
				mErrorListener);
		addHis(searchText);
		hideHisList();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int id = v.getId();
		switch (id) {
		case R.id.search_cancel_but:
			cancelSearchContentFocus();
			break;

		case R.id.search_but:
			goSearch();
			break;
		default:
			break;
		}
	}

	private void cancelSearchContentFocus() {
		searchContent.clearFocus();
		InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

		imm.hideSoftInputFromWindow(searchContent.getWindowToken(), 0);
	}

	private class SearchListAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return searchList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return searchList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			if (convertView == null) {
				convertView = inflate.inflate(R.layout.search_news_item, null);
				holder = new SeachListViewHolder();
				holder.titlePic = (ImageView) convertView
						.findViewById(R.id.home_news_titlepic);

				holder.title = (MyTextView) convertView
						.findViewById(R.id.home_news_title);
				// holder.title.setTextSize(New_HomeItemFragment.this
				// .getResources().getDimension(
				// textNomalSize[PixelUtil.getScale()]));
				holder.newsTime = (MyTextView) convertView
						.findViewById(R.id.search_news_newstime);
				holder.click = (MyTextView) convertView
						.findViewById(R.id.search_news_click_num);
				// holder.home_news_play = (ImageView) convertView
				// .findViewById(R.id.home_news_play);
				convertView.setTag(holder);
			} else {
				holder = (SeachListViewHolder) convertView.getTag();
			}

			final New_News_Search news = searchList.get(position);
			news.setTitlePic(CommonUtils.doWebpUrl(news.getTitlePic()));
			ImgUtils.imageLoader.displayImage(news.getTitlePic(),
					holder.titlePic, ImgUtils.homeImageOptions);
			holder.title.setText(news.getTitle());
			// holder.newstime.setText(news.getClickNum());

			// SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日");
			// long newsTime = Long.parseLong(news.getNewsTime()) * 1000;
			// holder.newsTime.setText(format.format(new Date(newsTime)));
			holder.click.setText(news.getClickNum() + "次");
			holder.newsTime.setText(dealNewsTime(news.getNewsTime()));
			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					SearchMainActivity.this.startAnimActivityByParameter(
							New_Activity_Content_Video.class, news.getMId(),
							news.getType(), news.getTitleUrl(),
							news.getClickNum() + "", news.getTitle(),
							news.getTitlePic(), news.getSharedPic());
				}
			});
			return convertView;
		}
	}

	private class SearchHisListAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			if(searchHisList.size() == 0)
				return 0;
			return searchHisList.size() + 1;
		}

		@Override
		public int getViewTypeCount() {
			// TODO Auto-generated method stub
			return 2;
		}

		@Override
		public int getItemViewType(int position) {
			// TODO Auto-generated method stub
			if (position < searchHisList.size())
				return 0;
			return 1;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			if (position < searchHisList.size())
				return searchHisList.get(position);
			return "取消";
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			final int itemType = getItemViewType(position);
			if (convertView == null) {
				convertView = inflate.inflate(R.layout.search_his_list_item,
						null);
				hisHolder = new SeachHisListViewHolder();
				hisHolder.removeHis = (ImageView) convertView
						.findViewById(R.id.search_his_remove);
				hisHolder.hisText = (MyTextView) convertView
						.findViewById(R.id.search_his_text);
				hisHolder.cleanHis = (MyTextView) convertView
						.findViewById(R.id.search_his_clean);
				if (itemType == 0) {
					hisHolder.removeHis.setVisibility(View.VISIBLE);
					hisHolder.hisText.setVisibility(View.VISIBLE);
					hisHolder.cleanHis.setVisibility(View.GONE);
				} else if (itemType == 1) {
					hisHolder.removeHis.setVisibility(View.GONE);
					hisHolder.hisText.setVisibility(View.GONE);
					hisHolder.cleanHis.setVisibility(View.VISIBLE);
				}
				convertView.setTag(hisHolder);
			} else {
				hisHolder = (SeachHisListViewHolder) convertView.getTag();
			}

			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (itemType == 1)
						clearHisList();
				}
			});
			if (itemType == 0)
				hisHolder.hisText.setText(searchHisList.get(position));
			return convertView;
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

	protected void onSuccessArray(JSONArray jsonArray) {
		// TODO Auto-generated method stub seachList
		if (!isLoadMore)
			searchList.clear();
		else
			curPageNum = curPageNum++;
		for (int i = 0; i < jsonArray.length(); i++) {
			New_News_Search news = new New_News_Search();
			try {
				news.parseJSON((JSONObject) jsonArray.get(i));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				Log.e("SearchMainActivity", e.getLocalizedMessage());
			}
			searchList.add(news);
		}
		searchAdapt.notifyDataSetChanged();
		searchListView.onRefreshComplete();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if (searchContent.hasFocus()
				|| searchBut.getVisibility() == View.VISIBLE) {
			searchContent.setText("");
			cancelSearchContentFocus();
			return;
		}
		this.finish();
		this.overridePendingTransition(R.anim.alpha, R.anim.out_to_top);
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		super.finish();
	}

	private String dealNewsTime(String newsTime) {
		long now = new Date().getTime();
		long newsTimeL = Long.parseLong(newsTime) * 1000;
		long differ = (now - newsTimeL) / 1000;
		if (differ < 60) {
			return differ + "秒前";
		} else if (differ / 60 < 60) {
			return (differ / 60) + "分前";
		} else if (differ / 60 / 60 < 24) {
			return (differ / 60 / 60) + "小时前";
		} else if (differ / 60 / 60 / 24 < 31) {
			return (differ / 60 / 60 / 24) + "天前";
		} else if (differ / 60 / 60 / 24 / 30 < 12) {
			return (differ / 60 / 60 / 24 / 30) + "月前";
		} else {
			return (differ / 60 / 60 / 24 / 365) + "年前";
		}
	}

	private void addHis(String searchText) {
		// TODO Auto-generated method stub
		if (searchHisList.size() == AndroidConfig.MAX_SEARCH_HIS_NUM)
			searchHisList.remove(0);
		searchHisList.add(searchText);
		saveHisList();
		searchHisAdapt.notifyDataSetChanged();
	}

	private void saveHisList() {
		StringBuffer buf = new StringBuffer();
		if (searchHisList.size() > 0) {
			for (String ele : searchHisList) {
				buf.append(ele).append("||");
			}
			buf.deleteCharAt(buf.length() - 1);
			buf.deleteCharAt(buf.length() - 1);
		}
		spUtil.saveSearchHisList(buf.toString());
	}

	private void getHisList() {
		String buf = spUtil.getSearchHisList();
		String[] bufArray = buf.split("\\|\\|");
		searchHisList.clear();
		for (String ele : bufArray) {
			if(ele !=null && !ele.equals(""))
			searchHisList.add(ele);
		}
	}

	private void showHisList() {
		if (searchHisList.size() > 0)
			searchHisListView.setVisibility(View.VISIBLE);
	}

	private void hideHisList() {
		searchHisListView.setVisibility(View.GONE);
	}

	private void clearHisList() {
		searchHisList.clear();
		saveHisList();
		searchHisAdapt.notifyDataSetChanged();
	}
}
