package com.kankan.kankanews.ui.view;

import io.vov.vitamio.utils.StringUtils;
import io.vov.vitamio.widget.MediaController.MediaPlayerControl;
import io.vov.vitamio.widget.VideoView;
import android.content.Context;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.kankan.kankanews.base.BaseVideoActivity;
import com.kankan.kankanews.ui.item.Activity_OffLinePlay;
import com.kankanews.kankanxinwen.R;

public class VideoViewController extends RelativeLayout implements
		OnClickListener {

	// 控制条类型
	public enum ControllerType {
		SmallController, FullScrennController
	}

	private static final int sDefaultTimeout = 5000;
	private static final int FADE_OUT = 1;
	private static final int SHOW_PROGRESS = 2;

	private boolean isShow;// 控制条是否显示
	private boolean mDragging;
	private boolean mInstantSeeking = false;
	private ControllerType mControllerType;

	private VideoView video;
	private BaseVideoActivity activity_Content;
	private Activity_OffLinePlay activity_OffLinePlay;
	private LayoutInflater inflater;
	private View inflate;
	private ImageView video_controller_player;
	private MyTextView video_controller_curTime;
	private MyTextView video_controller_totalTime;
	private SeekBar video_controller_seek;
	private SeekBar video_controller_seek_full;
	private VerticalBar video_controller_volume_seek;
	private ImageView video_controller_full_screen;

	public Activity_OffLinePlay getActivity_OffLinePlay() {
		return activity_OffLinePlay;
	}

	public void setActivity_OffLinePlay(
			Activity_OffLinePlay activity_OffLinePlay) {
		this.activity_OffLinePlay = activity_OffLinePlay;
	}

	public BaseVideoActivity getActivity_Content() {
		return activity_Content;
	}

	public void setActivity_Content(BaseVideoActivity activity_Content) {
		this.activity_Content = activity_Content;
	}

	public ControllerType getmControllerType() {
		return mControllerType;
	}

	public void setmControllerType(ControllerType mControllerType) {
		this.mControllerType = mControllerType;
	}

	public boolean isShow() {
		return isShow;
	}

	public void setShow(boolean isShow) {
		this.isShow = isShow;
	}

	public MediaPlayerControl getPlayerControl() {
		return video;
	}

	public void setPlayerControl(VideoView playerControl) {
		this.video = playerControl;
	}

	public VideoViewController(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView();
	}

	public VideoViewController(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}

	public VideoViewController(Context context) {
		super(context);
		initView();
	}

	boolean isOren;// true为横屏,false为竖屏

	// 设置横竖屏
	public void setOrentation(boolean isOren) {
		this.isOren = isOren;
	}

	public void setMobile(int mobileWidth, int mobileHeight) {
		this.mobileHeight = mobileHeight;
		this.mobileWidth = mobileWidth;
	}

	int mobileWidth;
	int mobileHeight;

	private void initView() {
		mControllerType = ControllerType.SmallController;
		inflater = LayoutInflater.from(getContext());
		inflate = inflater.inflate(R.layout.videoview_controller, null);
		addView(inflate);
		initController();
	}

	public void setTitle(String title) {
		video_controller_title.setText(title);
	}

	public ImageView getContent_video_temp_image() {
		return content_video_temp_image;
	}

	private void initController() {

		mAM = (AudioManager) getContext().getSystemService(
				Context.AUDIO_SERVICE);
		mMaxVolume = mAM.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

		content_video_temp_image = (ImageView) findViewById(R.id.content_video_temp_image);// 花屏调整临时图片
		video_controller_bar = (RelativeLayout) findViewById(R.id.video_controller_bar);// 整个控制条界面
		video_controller_full = (RelativeLayout) findViewById(R.id.video_controller_full);// 大屏控制条
		video_controller_volume_box = findViewById(R.id.video_controller_volume_box);
		video_controller_top_bar = (LinearLayout) findViewById(R.id.video_controller_top_bar);// 整个头部bar
		video_controller_back = (ImageView) findViewById(R.id.video_controller_back);// 大屏幕返回按钮
		video_controller_full_play = (ImageView) findViewById(R.id.video_controller_full_play);// 大屏幕播放按钮
		video_controller_volume = (ImageView) findViewById(R.id.video_controller_volume);// 大屏幕显示音量按钮
		video_controller_title = (MyTextView) findViewById(R.id.video_controller_title);// 大屏幕标题
		video_controller_totalAndCurTime = (MyTextView) findViewById(R.id.video_controller_totalAndCurTime);// 大屏幕当前时间与总时间
		video_controller_seek_full = (SeekBar) findViewById(R.id.video_controller_seek_full);// 大屏进度条
		video_controller_volume_seek = (VerticalBar) findViewById(R.id.video_controller_volume_seek);// 大屏音量控制条

		video_controller_small = (RelativeLayout) findViewById(R.id.video_controller_small);// 小屏控制条
		video_controller_player = (ImageView) findViewById(R.id.video_controller_player);// 小屏幕播放按钮
		video_controller_curTime = (MyTextView) findViewById(R.id.video_controller_curTime);// 小屏幕当前时间
		video_controller_totalTime = (MyTextView) findViewById(R.id.video_controller_totalTime);// 小屏幕总时间
		video_controller_full_screen = (ImageView) findViewById(R.id.video_controller_full_screen);// 小屏幕全屏按钮
		video_controller_seek = (SeekBar) findViewById(R.id.video_controller_seek);// 小屏进度条

		if (video_controller_seek != null) {
			if (video_controller_seek instanceof SeekBar) {
				SeekBar seeker = (SeekBar) video_controller_seek;
				seeker.setOnSeekBarChangeListener(mSeekListener);
				video_controller_seek_full
						.setOnSeekBarChangeListener(mSeekListener);
			}
			video_controller_seek.setMax(1000);
			video_controller_seek_full.setMax(1000);
		}
		initLinstener();
		changeView();

	}

	public void reset() {
		video_controller_curTime.setText("00:00");
		video_controller_totalTime.setText("00:00");
		video_controller_seek.setProgress(0);
		video_controller_seek_full.setProgress(0);
	}

	public void changeView() {

		switch (mControllerType) {
		case SmallController: // 小屏幕
			video_controller_full.setVisibility(View.GONE);
			video_controller_small.setVisibility(View.VISIBLE);

			break;
		case FullScrennController: // 大屏幕
			video_controller_full.setVisibility(View.VISIBLE);
			video_controller_small.setVisibility(View.GONE);

			break;
		}
	}

	@Override
	public void setEnabled(boolean enabled) {

		if (video_controller_player != null)
			video_controller_player.setEnabled(enabled);
		if (video_controller_seek != null)
			video_controller_seek.setEnabled(enabled);

		if (video_controller_back != null)
			video_controller_back.setEnabled(enabled);

		if (video_controller_full_play != null)
			video_controller_full_play.setEnabled(enabled);

		if (video_controller_volume != null)
			video_controller_volume.setEnabled(enabled);

		if (video_controller_seek_full != null)
			video_controller_seek_full.setEnabled(enabled);

		if (video_controller_volume_seek != null)
			video_controller_volume_seek.setEnabled(enabled);

		if (video_controller_full_screen != null)
			video_controller_full_screen.setEnabled(enabled);
		super.setEnabled(enabled);
	}

	private void initLinstener() {
		video_controller_volume_seek.setOnSeekBarChangeListener(mVSeekListener);
		video_controller_back.setOnClickListener(this);
		video_controller_full_play.setOnClickListener(this);
		video_controller_volume.setOnClickListener(this);
		video_controller_full_screen.setOnClickListener(this);
		video_controller_bar.setOnClickListener(this);
		video_controller_player.setOnClickListener(this);

	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			long pos;
			switch (msg.what) {
			case FADE_OUT:
				hide();
				break;
			case SHOW_PROGRESS:
				pos = setProgress();
				if (!mDragging && isShow) {
					msg = obtainMessage(SHOW_PROGRESS);
					sendMessageDelayed(msg, 1000 - (pos % 1000));
					updatePausePlay();
				}
				break;
			}
		}
	};

	private long setProgress() {
		if (video == null)
			return 0;

		long position = video.getCurrentPosition();
		long duration = video.getDuration();
		if (video_controller_seek != null) {
			if (duration > 0) {
				long pos = 1000L * position / duration;
				switch (mControllerType) {
				case SmallController: // 小屏幕
					video_controller_seek.setProgress((int) pos);
					break;
				case FullScrennController: // 小屏幕
					video_controller_seek_full.setProgress((int) pos);
					break;
				}
			}
			// int percent = video.getBufferPercentage();
			// video_controller_seek.setSecondaryProgress(percent * 10);
		}

		mDuration = duration;

		switch (mControllerType) {
		case SmallController: // 小屏幕
			if (video_controller_totalTime != null)
				video_controller_totalTime.setText(StringUtils
						.generateTime(mDuration));
			if (video_controller_curTime != null)
				video_controller_curTime.setText(StringUtils
						.generateTime(position));

			break;
		case FullScrennController: // 小屏幕
			if (video_controller_totalTime != null)
				video_controller_totalAndCurTime.setText(StringUtils
						.generateTime(position)
						+ "/"
						+ StringUtils.generateTime(mDuration));
			break;
		}

		return position;
	}

	public void hide() {
		if (isShow) {
			mHandler.removeMessages(SHOW_PROGRESS);
			setV(View.GONE);
			video_controller_volume
					.setBackgroundResource(R.drawable.volume_icon_up);
			video_controller_volume_box.setVisibility(View.GONE);
			if (activity_Content != null) {
				activity_Content.setCanScrool(true);
			}
			isShowVolume = false;
		}
	}

	public void show() {
		show(sDefaultTimeout);
	}

	public void show(int timeout) {
		if (!isShow) {
			if (video_controller_player != null)
				video_controller_player.requestFocus();
			setV(View.VISIBLE);
		}
		updatePausePlay();
		mHandler.sendEmptyMessage(SHOW_PROGRESS);
		if (timeout != 0) {
			mHandler.removeMessages(FADE_OUT);
			mHandler.sendMessageDelayed(mHandler.obtainMessage(FADE_OUT),
					timeout);
		}
	}

	private VerticalBar.OnSeekBarChangeListener mVSeekListener = new VerticalBar.OnSeekBarChangeListener() {

		@Override
		public void onProgressChanged(VerticalBar VerticalSeekBar,
				int progress, boolean fromUser) {
			int index = (int) (progress / 100.00 * mMaxVolume);
			if (index > mMaxVolume)
				index = mMaxVolume;
			else if (index < 0)
				index = 0;
			// 变更声音
			mAM.setStreamVolume(AudioManager.STREAM_MUSIC, index, 0);
		}

		@Override
		public void onStartTrackingTouch(VerticalBar VerticalSeekBar) {

			mDragging = true;
			show(3600000);
			mHandler.removeMessages(SHOW_PROGRESS);
		}

		@Override
		public void onStopTrackingTouch(VerticalBar VerticalSeekBar) {
			show(sDefaultTimeout);
			mHandler.removeMessages(SHOW_PROGRESS);
			mAM.setStreamMute(AudioManager.STREAM_MUSIC, false);
			mDragging = false;
			mHandler.sendEmptyMessageDelayed(SHOW_PROGRESS, 1000);

		}
	};

	private OnSeekBarChangeListener mSeekListener = new OnSeekBarChangeListener() {
		public void onStartTrackingTouch(SeekBar bar) {
			mDragging = true;
			show(3600000);
			mHandler.removeMessages(SHOW_PROGRESS);
			if (mInstantSeeking)
				mAM.setStreamMute(AudioManager.STREAM_MUSIC, true);

			if (activity_Content != null) {
				activity_Content.setRightFinsh(false);
			}

		}

		public void onProgressChanged(SeekBar bar, int progress,
				boolean fromuser) {
			if (!fromuser)
				return;

			long newposition = (mDuration * progress) / 1000;
			String time = StringUtils.generateTime(newposition);
			if (mInstantSeeking) {
				for (long i = newposition - 1000; i < newposition + 1000; i++) {
					video.seekTo(i);
				}
			}
			if (video_controller_curTime != null)
				video_controller_curTime.setText(time);

			if (video_controller_totalTime != null)
				video_controller_totalAndCurTime.setText(StringUtils
						.generateTime(newposition)
						+ "/"
						+ StringUtils.generateTime(mDuration));

		}

		public void onStopTrackingTouch(SeekBar bar) {
			if (!mInstantSeeking) {
				long seek = (mDuration * bar.getProgress()) / 1000;
				for (long i = seek - 1000; i < seek + 1000; i++) {
					video.seekTo(i);
				}
			}
			show(sDefaultTimeout);
			mHandler.removeMessages(SHOW_PROGRESS);
			mAM.setStreamMute(AudioManager.STREAM_MUSIC, false);
			mDragging = false;
			mHandler.sendEmptyMessageDelayed(SHOW_PROGRESS, 1000);
			if (activity_Content != null) {
				if (!activity_Content.isFullScrenn())
					activity_Content.setRightFinsh(true);
			}

		}
	};

	private RelativeLayout video_controller_bar;
	private RelativeLayout video_controller_small;
	private RelativeLayout video_controller_full;

	private long mDuration;

	private AudioManager mAM;
	private int width;
	private int height;
	private ImageView content_video_temp_image;
	private LinearLayout video_controller_top_bar;
	private ImageView video_controller_back;
	private ImageView video_controller_full_play;
	private ImageView video_controller_volume;
	private MyTextView video_controller_title;
	private MyTextView video_controller_totalAndCurTime;
	private View video_controller_volume_box;
	private int mMaxVolume;
	private int mVolume;
	private boolean isShowVolume;

	public void setV(int visibility) {
		video_controller_bar.setVisibility(visibility);
		switch (visibility) {
		case View.VISIBLE:
			isShow = true;
			break;
		default:
			isShow = false;
			break;
		}
		updatePausePlay();
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();

		switch (id) {
		case R.id.video_controller_bar:
			hide();
			break;
		case R.id.video_controller_player:
			if (video.isPlaying())
				video.pause();
			else {
				if (content_video_temp_image != null) {
					content_video_temp_image.setVisibility(View.GONE);
				}
				video.start();
				activity_Content.video_pb.setVisibility(View.GONE);
				activity_Content.small_video_pb.setVisibility(View.GONE);
			}
			updatePausePlay();
			break;
		case R.id.video_controller_full_play:
			if (video.isPlaying())
				video.pause();
			else {
				if (content_video_temp_image != null) {
					content_video_temp_image.setVisibility(View.GONE);
				}
				video.start();
			}
			updatePausePlay();
			break;

		case R.id.video_controller_volume:// 显示音量
			volume();
			break;
		case R.id.video_controller_back:// 后退
			if (activity_Content != null) {
				activity_Content.fullScrenntoSamll();
			}

			if (activity_OffLinePlay != null) {
				activity_OffLinePlay.AnimFinsh();
			}

			break;
		case R.id.video_controller_full_screen:// 全屏
			if (activity_Content != null) {
				activity_Content.samllScrenntoFull();
			}
			break;
		}

	}

	private void volume() {
		mVolume = mAM.getStreamVolume(AudioManager.STREAM_MUSIC);
		video_controller_volume_seek
				.setProgress((int) (((mVolume * 1.00) / (mMaxVolume * 1.00)) * 100));

		if (isShowVolume) {
			video_controller_volume_box.setVisibility(View.GONE);
			isShowVolume = false;
			video_controller_volume
					.setBackgroundResource(R.drawable.volume_icon_up);
			if (activity_Content != null) {
				activity_Content.setCanScrool(true);
			}
		} else {
			isShowVolume = true;
			video_controller_volume_box.setVisibility(View.VISIBLE);
			video_controller_volume
					.setBackgroundResource(R.drawable.volume_button_touch);
			if (activity_Content != null) {
				activity_Content.setCanScrool(false);
			}
		}

	}

	private void updatePausePlay() {

		switch (mControllerType) {
		case SmallController: // 小屏幕
			if (video_controller_player == null)
				return;
			if (video.isPlaying())
				video_controller_player
						.setImageResource(R.drawable.icon_pause_small);
			else
				video_controller_player
						.setImageResource(R.drawable.icon_play_big);
			break;
		case FullScrennController: // 小屏幕
			if (video_controller_full_play == null)
				return;

			if (video.isPlaying())
				video_controller_full_play
						.setImageResource(R.drawable.icon_pause_small);
			else
				video_controller_full_play
						.setImageResource(R.drawable.icon_play_big);
			break;

		}

	}

	// private long setProgress() {
	// if (mPlayer == null || mDragging)
	// return 0;
	//
	// int position = mPlayer.getCurrentPosition();
	// int duration = mPlayer.getDuration();
	// if (mProgress != null) {
	// if (duration > 0) {
	// long pos = 1000L * position / duration;
	// mProgress.setProgress((int) pos);
	// }
	// int percent = mPlayer.getBufferPercentage();
	// mProgress.setSecondaryProgress(percent * 10);
	// }
	//
	// mDuration = duration;
	//
	// if (mEndTime != null)
	// mEndTime.setText(generateTime(mDuration));
	// if (mCurrentTime != null)
	// mCurrentTime.setText(generateTime(position));
	//
	// return position;
	// }

	// // 显示控制条
	// private void showController() {
	//
	// if (!isShow && video_controller != null) {
	// video_controller.setVisibility(View.VISIBLE);
	// if (video_view.isPlaying()) {
	// video_controller_player
	// .setImageResource(R.drawable.icon_pause_big);
	// } else {
	// video_controller_player
	// .setImageResource(R.drawable.icon_play_big);
	// }
	// isShow = true;
	// }
	//
	// }
	//
	// // 隐藏控制条
	// private void hideController() {
	// video_controller.setVisibility(View.GONE);
	// }

}
