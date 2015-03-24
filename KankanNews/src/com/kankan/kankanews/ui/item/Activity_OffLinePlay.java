package com.kankan.kankanews.ui.item;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.MediaPlayer.OnCompletionListener;
import io.vov.vitamio.MediaPlayer.OnErrorListener;
import io.vov.vitamio.MediaPlayer.OnInfoListener;
import io.vov.vitamio.MediaPlayer.OnPreparedListener;
import io.vov.vitamio.widget.VideoView;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.kankan.kankanews.base.BaseActivity;
import com.kankan.kankanews.bean.Content_News;
import com.kankan.kankanews.ui.view.MultiImageView;
import com.kankan.kankanews.ui.view.VideoViewController;
import com.kankan.kankanews.ui.view.VideoViewController.ControllerType;
import com.kankan.kankanews.utils.CommonUtils;
import com.kankan.kankanews.utils.ToastUtils;
import com.kankanews.kankanxinwen.R;

public class Activity_OffLinePlay extends BaseActivity implements
		OnInfoListener, OnPreparedListener, OnCompletionListener,
		OnErrorListener {

	private VideoView video_view;
	private VideoViewController video_controller;

	private View mVolumeBrightnessLayout;
	private ImageView mOperationPercent;
	/** 最大声音 */
	private int mMaxVolume;
	/** 当前声音 */
	private int mVolume = -1;
	/** 当前进度 */
	private long mSeek;
	/** 最大进度 */
	private long mMaxSeek;
	private AudioManager mAM;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mAM = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		WindowManager.LayoutParams attrs = getWindow().getAttributes();
		attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
		getWindow().setAttributes(attrs);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

		mMaxVolume = mMaxVolume = mAM
				.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		setContentView(R.layout.activity_offlinepaly);
	}

	@Override
	protected void initView() {
		video_view = (VideoView) findViewById(R.id.video_view);
		video_pb = (MultiImageView) findViewById(R.id.video_pb);
		video_controller = (VideoViewController) findViewById(R.id.video_controller);
		video_controller
				.setmControllerType(ControllerType.FullScrennController);
		video_controller.changeView();
		mVolumeBrightnessLayout = findViewById(R.id.operation_volume_brightness);
		mOperationPercent = (ImageView) findViewById(R.id.operation_percent);
		setRightFinsh(false);
	}

	@Override
	protected void initData() {
		Intent intent = getIntent();

		final Content_News mContent_News = (Content_News) intent
				.getSerializableExtra("news");
		position = intent.getStringExtra("NUM");
		mGestureDetector = new GestureDetector(this, new MyGestureListener());
		setRightFinsh(false);

		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				video_controller.setPlayerControl(video_view);
				video_controller
						.setActivity_OffLinePlay(Activity_OffLinePlay.this);
				video_controller.show();
				video_controller.setTitle(mContent_News.getTitle());
				video_view.setVideoPath(CommonUtils.getVideoFile(mContext,
						mContent_News).getAbsolutePath());
				video_view.requestFocus();
				video_view.start();
			}
		}, 2000);

	}

	private boolean hasBeenPaly;

	@Override
	protected void setListener() {

		video_controller.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (video_view.isPlaying() || hasBeenPaly) {
					if (!video_controller.isShow())
						video_controller.show();
				}
			}
		});

		video_view.setOnCompletionListener(this);
		video_view.setOnErrorListener(this);
		video_view.setOnPreparedListener(this);
		video_view.setOnInfoListener(this);
	}

	@Override
	protected void onSuccess(JSONObject jsonObject) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onFailure(VolleyError error) {
		// TODO Auto-generated method stub

	}

	private class MyGestureListener extends SimpleOnGestureListener {

		private float fx;
		private float fy;

		@Override
		public boolean onDown(MotionEvent e) {
			fx = e.getX();
			fy = e.getY();
			return super.onDown(e);
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			float mOldX = e1.getX(), mOldY = e1.getY();
			int y = (int) e2.getRawY();
			int x = (int) e2.getRawX();

			Display disp = getWindowManager().getDefaultDisplay();
			int windowWidth = disp.getWidth();
			int windowHeight = disp.getHeight();

			if (Math.abs((y - fy)) < Math.abs((x - fx)) + 100) {
				video_controller.show();
				onPlayerSeek(x - mOldX);
			}

			return super.onFling(e1, e2, velocityX, velocityY);
		}

		/** 滑动 */
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			float mOldX = e1.getX(), mOldY = e1.getY();
			int y = (int) e2.getRawY();
			int x = (int) e2.getRawX();

			Display disp = getWindowManager().getDefaultDisplay();
			int windowWidth = disp.getWidth();
			int windowHeight = disp.getHeight();

			if (Math.abs((y - fy)) > Math.abs((x - fx)) + 100) {
				onVolumeSlide((mOldY - y) / windowHeight);
			}

			return super.onScroll(e1, e2, distanceX, distanceY);
		}
	}

	/**
	 * 滑动改变声音大小
	 * 
	 * @param percent
	 */
	private void onVolumeSlide(float percent) {
		if (mVolume == -1) {
			mVolume = mAM.getStreamVolume(AudioManager.STREAM_MUSIC);
			if (mVolume < 0)
				mVolume = 0;

			mDismissHandler.removeMessages(0);
			mVolumeBrightnessLayout.setVisibility(View.VISIBLE);
		}

		int index = (int) (percent * mMaxVolume) + mVolume;
		if (index > mMaxVolume)
			index = mMaxVolume;
		else if (index < 0)
			index = 0;

		// 变更声音
		mAM.setStreamVolume(AudioManager.STREAM_MUSIC, index, 0);

		// 变更进度条
		ViewGroup.LayoutParams lp = mOperationPercent.getLayoutParams();
		lp.width = findViewById(R.id.operation_full).getLayoutParams().width
				* index / mMaxVolume;
		mOperationPercent.setLayoutParams(lp);
	}

	/**
	 * 滑动改变播放进度
	 * 
	 * @param percent
	 */
	private void onPlayerSeek(float percent) {
		long msc = 15;
		if (percent < 0) {
			msc *= -1;
		}
		msc *= 1000;
		mSeek = video_view.getCurrentPosition();
		mMaxSeek = video_view.getDuration();

		long index = (long) (mSeek + msc);
		if (index > mMaxSeek)
			index = mMaxSeek;
		else if (index < 0)
			index = 0;

		video_view.seekTo(index);
	}

	/** 定时隐藏 */
	private Handler mDismissHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			mVolumeBrightnessLayout.setVisibility(View.GONE);
		}
	};
	private GestureDetector mGestureDetector;

	/** 手势结束 */
	private void endGesture() {
		mVolume = -1;
		// 隐藏
		mDismissHandler.removeMessages(0);
		mDismissHandler.sendEmptyMessageDelayed(0, 1000);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		mGestureDetector.onTouchEvent(ev);
		// 处理手势结束
		switch (ev.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_UP:
			endGesture();
			break;
		}
		return super.dispatchTouchEvent(ev);
	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		ToastUtils.Errortoast(mContext, "视频文件已损坏,请重新下载");

		// 判断空，我就不判断了。。。。
		Intent data = new Intent();
		// 请求代码可以自己设置，这里设置成20
		data.putExtra("POSITION", Integer.parseInt(position));
		setResult(20, data);
		AnimFinsh();
		return true;
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		AnimFinsh();
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		hasBeenPaly = true;
	}

	@Override
	public boolean onInfo(MediaPlayer mp, int what, int extra) {

		switch (what) {
		case MediaPlayer.MEDIA_INFO_BUFFERING_START:
			if (video_view.isPlaying()) {
				video_view.pause();
				video_pb.setVisibility(View.VISIBLE);
			}
			break;
		case MediaPlayer.MEDIA_INFO_BUFFERING_END:
			video_view.start();
			video_pb.setVisibility(View.GONE);
			break;
		case MediaPlayer.MEDIA_INFO_DOWNLOAD_RATE_CHANGED:

			break;
		}
		return true;

	}

	// videoView的状态
	private boolean isPlayer;
	private MultiImageView video_pb;
	private String position;

	@Override
	protected void onPause() {
		super.onPause();
		if (video_view.isPlaying()) {
			video_view.pause();
			isPlayer = true;
		} else {
			if (video_view.getCurrentPosition() > 0) {
				video_controller.getContent_video_temp_image().setVisibility(
						View.VISIBLE);
				video_controller.getContent_video_temp_image().setImageBitmap(
						video_view.getCurrentFrame());
			}
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		if (isPlayer) {
			video_view.start();
			isPlayer = false;
		}
	}

	// 播放完成回到初始状态
	private void resetVideo() {
		video_view.release(true);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		resetVideo();
	}
}
