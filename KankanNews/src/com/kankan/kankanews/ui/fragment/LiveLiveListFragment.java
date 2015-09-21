package com.kankan.kankanews.ui.fragment;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.volley.VolleyError;
import com.iss.view.pulltorefresh.PullToRefreshPinnedSectionListView;
import com.kankan.kankanews.base.BaseFragment;
import com.kankan.kankanews.bean.LiveLiveList;
import com.kankan.kankanews.bean.LiveLiveObj;
import com.kankan.kankanews.ui.view.MyTextView;
import com.kankan.kankanews.ui.view.PinnedSectionListView;
import com.kankan.kankanews.ui.view.PinnedSectionListView.PinnedSectionListAdapter;
import com.kankan.kankanews.utils.DebugLog;
import com.kankan.kankanews.utils.ImgUtils;
import com.kankan.kankanews.utils.JsonUtils;
import com.kankanews.kankanxinwen.R;

public class LiveLiveListFragment extends BaseFragment {
	private View inflate;
	private PullToRefreshPinnedSectionListView mLiveListView;
	private LiveListViewAdapter mLiveListViewAdapter;
	private LiveLiveList mLiveLiveList;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		initView();
		initDate();
		initLinsenter();
		return inflate;
	}

	private void initLinsenter() {
		mLiveListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

			}
		});
	}

	private void initView() {
		inflate = inflater.inflate(R.layout.fragment_live_live_list, null);
		mLiveListView = (PullToRefreshPinnedSectionListView) inflate
				.findViewById(R.id.live_list_view);
	}

	private void initDate() {
		refreshNetDate();
	}

	@Override
	protected boolean initLocalDate() {
		return false;
	}

	@Override
	protected void saveLocalDate() {

	}

	@Override
	protected void refreshNetDate() {
		netUtils.getLiveList(this.mListenerObject, this.mErrorListener);
	}

	@Override
	protected void loadMoreNetDate() {
	}

	@Override
	protected void onSuccessObject(JSONObject jsonObject) {
		mLiveLiveList = (LiveLiveList) JsonUtils.toObject(
				jsonObject.toString(), LiveLiveList.class);
		mLiveListViewAdapter = new LiveListViewAdapter(this.mActivity);
		((PinnedSectionListView) mLiveListView.getRefreshableView())
				.setShadowVisible(true);
		mLiveListView.setAdapter(mLiveListViewAdapter);

	}

	@Override
	protected void onSuccessArray(JSONArray jsonObject) {
	}

	private class Item {

		public static final int ITEM = 0;
		public static final int SECTION = 1;

		public final int type;
		public final String liveType;
		public final LiveLiveObj liveObj;

		public int sectionPosition;
		public int listPosition;

		public Item(int type, LiveLiveObj liveObj, String liveType) {
			this.type = type;
			this.liveObj = liveObj;
			this.liveType = liveType;
		}

	}

	private class LiveListViewAdapter extends ArrayAdapter<Item> implements
			PinnedSectionListAdapter {

		public LiveListViewAdapter(Context context) {
			super(context, R.layout.section_live_fragment_list_view,
					android.R.id.text1);
			boolean flagLive = false;
			boolean flagPre = false;
			for (char i = 0; i < mLiveLiveList.getLive().size(); i++) {
				if (!flagLive) {
					Item section = new Item(Item.SECTION, mLiveLiveList
							.getLive().get(i), mLiveLiveList.getLive().get(i)
							.getType());
					add(section);
					flagLive = true;
				}
				Item item = new Item(Item.ITEM, mLiveLiveList.getLive().get(i),
						mLiveLiveList.getLive().get(i).getType());
				add(item);
			}
			for (char i = 0; i < mLiveLiveList.getTrailer().size(); i++) {
				if (!flagPre) {
					Item section = new Item(Item.SECTION, mLiveLiveList
							.getTrailer().get(i), mLiveLiveList.getTrailer()
							.get(i).getType());
					add(section);
					flagPre = true;
				}
				Item item = new Item(Item.ITEM, mLiveLiveList.getTrailer().get(
						i), mLiveLiveList.getTrailer().get(i).getType());
				add(item);
			}
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// LinearLayout layout = (LinearLayout) super.getView(position,
			// convertView,
			// parent);
			Item item = getItem(position);
			ViewGroup layout = null;
			if (item.type == Item.SECTION) {
				layout = (LinearLayout) inflate.inflate(mActivity,
						R.layout.section_live_fragment_list_view, null);
				ImageView imageView = (ImageView) layout
						.findViewById(R.id.section_list_view_image);
				if ("正在直播".equals(item.liveType)) {
					imageView.setBackgroundResource(R.drawable.live_living);
				}
				if ("直播预告".equals(item.liveType)) {
					imageView.setBackgroundResource(R.drawable.live_preview);
				}
			} else {
				LiveLiveObj liveObj = item.liveObj;
				layout = (LinearLayout) inflate.inflate(mActivity,
						R.layout.item_live_fragment_list_view, null);
				ImageView titlePic = (ImageView) layout
						.findViewById(R.id.live_live_list_titlepic);
				ImgUtils.imageLoader.displayImage(liveObj.getTitlepic(),
						titlePic, ImgUtils.homeImageOptions);
				ImageView liveType = (ImageView) layout
						.findViewById(R.id.live_live_list_livetype);
				if ("正在直播".equals(liveObj.getType())) {
					liveType.setBackgroundResource(R.drawable.ic_live);
				}
				if ("直播预告".equals(liveObj.getType())) {
					liveType.setBackgroundResource(R.drawable.ic_next);
				}
				MyTextView title = (MyTextView) layout
						.findViewById(R.id.live_live_list_livetitle);
				title.setText(liveObj.getTitle());
				MyTextView time = (MyTextView) layout
						.findViewById(R.id.live_live_list_livetime);
				time.setText(liveObj.getTime());
			}
			layout.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

				}
			});
			// layout.setLayoutParams(new ListView.LayoutParams(
			// LayoutParams.MATCH_PARENT,
			// ListView.LayoutParams.WRAP_CONTENT));
			// TextView view = new
			// TextView(LivePlayFragment.this.mActivity);
			// layout.addView(view);
			// view.setTextColor(Color.DKGRAY);
			// view.setTag("" + position);
			// Item item = getItem(position);
			// if (item.type == Item.SECTION) {
			// // view.setOnClickListener(PinnedSectionListActivity.this);
			// // view.setBackgroundColor(parent.getResources().getColor(
			// // COLORS[item.sectionPosition % COLORS.length]));
			// }
			return layout;
		}

		@Override
		public int getViewTypeCount() {
			return 2;
		}

		@Override
		public int getItemViewType(int position) {
			return getItem(position).type;
		}

		@Override
		public boolean isItemViewTypePinned(int viewType) {
			return viewType == Item.SECTION;
		}

	}

	@Override
	protected void onFailure(VolleyError error) {
		DebugLog.e(error.getLocalizedMessage());
	}
}
