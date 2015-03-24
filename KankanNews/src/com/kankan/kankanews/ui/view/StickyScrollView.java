package com.kankan.kankanews.ui.view;

import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.ScrollView;

import com.kankan.kankanews.ui.interfacz.ScrollViewListener;
import com.kankanews.kankanxinwen.R;

public class StickyScrollView extends ScrollView {
	private static final String STICKY = "sticky";
	private View mCurrentStickyView;
	private Drawable mShadowDrawable;
	private List<View> mStickyViews;
	private int mStickyViewTopOffset;
	private int defaultShadowHeight = 10;
	private float density;
	private boolean redirectTouchToStickyView;

	// �ƶ�����, ��һ���ٷֱ�, ������ָ�ƶ���100px, ��ôView��ֻ�ƶ�50px
	// Ŀ���Ǵﵽһ���ӳٵ�Ч��
	private static final float MOVE_FACTOR = 0.2f;

	// �ɿ���ָ��, ����ص�����λ����Ҫ�Ķ���ʱ��
	private static final int ANIM_TIME = 200;

	// ScrollView����View�� Ҳ��ScrollView��Ψһһ����View
	private View contentView;

	// ��ָ����ʱ��Yֵ, �������ƶ�ʱ�����ƶ�����
	// �������ʱ���������������� ������ָ�ƶ�ʱ����Ϊ��ǰ��ָ��Yֵ
	private float startY;

	// ���ڼ�¼�����Ĳ���λ��
	private Rect originalRect = new Rect();

	// ��ָ����ʱ��¼�Ƿ���Լ�������
	private boolean canPullDown = false;

	// ��ָ����ʱ��¼�Ƿ���Լ�������
	private boolean canPullUp = false;

	// ����ָ�����Ĺ����м�¼�Ƿ��ƶ��˲���
	private boolean isMoved = false;

	/**
	 * 当点击Sticky的时候，实现某些背景的渐变
	 */
	private Runnable mInvalidataRunnable = new Runnable() {

		@Override
		public void run() {
			if (mCurrentStickyView != null) {
				int left = mCurrentStickyView.getLeft();
				int top = mCurrentStickyView.getTop();
				int right = mCurrentStickyView.getRight();
				int bottom = getScrollY()
						+ (mCurrentStickyView.getHeight() + mStickyViewTopOffset);

				invalidate(left, top, right, bottom);
			}

			postDelayed(this, 16);

		}
	};

	public StickyScrollView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public StickyScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mShadowDrawable = context.getResources().getDrawable(
				R.drawable.sticky_shadow_default);
		mStickyViews = new LinkedList<View>();
		density = context.getResources().getDisplayMetrics().density;
	}

	/**
	 * 找到设置tag的View
	 * 
	 * @param viewGroup
	 */
	private void findViewByStickyTag(ViewGroup viewGroup) {
		int childCount = ((ViewGroup) viewGroup).getChildCount();
		for (int i = 0; i < childCount; i++) {
			View child = viewGroup.getChildAt(i);

			if (getStringTagForView(child).contains(STICKY)) {
				mStickyViews.add(child);
			}

			if (child instanceof ViewGroup) {
				findViewByStickyTag((ViewGroup) child);
			}
		}

	}

	@Override
	protected void onFinishInflate() {
		if (getChildCount() > 0) {
			contentView = getChildAt(0);
		}
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		if (changed) {
			findViewByStickyTag((ViewGroup) getChildAt(0));
		}
		showStickyView();

		if (contentView == null)
			return;

		// ScrollView�е�Ψһ�ӿؼ���λ����Ϣ, ���λ����Ϣ�������ؼ������������б��ֲ���
		originalRect.set(contentView.getLeft(), contentView.getTop(),
				contentView.getRight(), contentView.getBottom());
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		showStickyView();
		if (scrollViewListener != null) {
			scrollViewListener.onScrollChanged(this, l, t, oldl, oldt);
		}
	}

	/**
	 * 
	 */
	private void showStickyView() {
		View curStickyView = null;
		View nextStickyView = null;

		for (View v : mStickyViews) {
			int topOffset = v.getTop() - getScrollY();

			if (topOffset <= 0) {
				if (curStickyView == null
						|| topOffset > curStickyView.getTop() - getScrollY()) {
					curStickyView = v;
				}
			} else {
				if (nextStickyView == null
						|| topOffset < nextStickyView.getTop() - getScrollY()) {
					nextStickyView = v;
				}
			}
		}

		if (curStickyView != null) {
			mStickyViewTopOffset = nextStickyView == null ? 0 : Math.min(
					0,
					nextStickyView.getTop() - getScrollY()
							- curStickyView.getHeight());
			mCurrentStickyView = curStickyView;
			post(mInvalidataRunnable);
		} else {
			mCurrentStickyView = null;
			removeCallbacks(mInvalidataRunnable);

		}

	}

	private String getStringTagForView(View v) {
		Object tag = v.getTag();
		return String.valueOf(tag);
	}

	/**
	 * 将sticky画出来
	 */
	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		if (mCurrentStickyView != null) {
			// 先保存起来
			canvas.save();
			// 将坐标原点移动到(0, getScrollY() + mStickyViewTopOffset)
			canvas.translate(0, getScrollY() + mStickyViewTopOffset);

			if (mShadowDrawable != null) {
				int left = 0;
				int top = mCurrentStickyView.getHeight() + mStickyViewTopOffset;
				int right = mCurrentStickyView.getWidth();
				int bottom = top + (int) (density * defaultShadowHeight + 0.5f);
				mShadowDrawable.setBounds(left, top, right, bottom);
				mShadowDrawable.draw(canvas);
			}

			canvas.clipRect(0, mStickyViewTopOffset,
					mCurrentStickyView.getWidth(),
					mCurrentStickyView.getHeight());

			mCurrentStickyView.draw(canvas);

			// 重置坐标原点参数
			canvas.restore();
		}
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			redirectTouchToStickyView = true;
		}

		if (redirectTouchToStickyView) {
			redirectTouchToStickyView = mCurrentStickyView != null;

			if (redirectTouchToStickyView) {
				redirectTouchToStickyView = ev.getY() <= (mCurrentStickyView
						.getHeight() + mStickyViewTopOffset)
						&& ev.getX() >= mCurrentStickyView.getLeft()
						&& ev.getX() <= mCurrentStickyView.getRight();
			}
		}

		if (redirectTouchToStickyView) {
			ev.offsetLocation(
					0,
					-1
							* ((getScrollY() + mStickyViewTopOffset) - mCurrentStickyView
									.getTop()));
		}

		if (contentView == null) {
			return super.dispatchTouchEvent(ev);
		}

		int action = ev.getAction();

		switch (action) {
		case MotionEvent.ACTION_DOWN:

			// �ж��Ƿ��������������
			canPullDown = isCanPullDown();
			canPullUp = isCanPullUp();

			// ��¼����ʱ��Yֵ
			startY = ev.getY();
			break;

		case MotionEvent.ACTION_UP:

			if (!isMoved)
				break; // ���û���ƶ����֣� ������ִ��

			// ��������
			TranslateAnimation anim = new TranslateAnimation(0, 0,
					contentView.getTop(), originalRect.top);
			anim.setDuration(ANIM_TIME);

			contentView.startAnimation(anim);

			// ���ûص������Ĳ���λ��
			contentView.layout(originalRect.left, originalRect.top,
					originalRect.right, originalRect.bottom);

			// ����־λ���false
			canPullDown = false;
			canPullUp = false;
			isMoved = false;

			break;
		case MotionEvent.ACTION_MOVE:

			// ���ƶ��Ĺ����У� ��û�й��������������ĳ̶ȣ� Ҳû�й��������������ĳ̶�
			if (!canPullDown && !canPullUp) {
				startY = ev.getY();
				canPullDown = isCanPullDown();
				canPullUp = isCanPullUp();

				break;
			}

			// ������ָ�ƶ��ľ���
			float nowY = ev.getY();
			int deltaY = (int) (nowY - startY);

			// �Ƿ�Ӧ���ƶ�����
			boolean shouldMove = (canPullDown && deltaY > 0) // ����������
																// ������ָ�����ƶ�
					|| (canPullUp && deltaY < 0) // ���������� ������ָ�����ƶ�
					|| (canPullUp && canPullDown); // �ȿ�������Ҳ�����������������������ScrollView�����Ŀؼ���ScrollView��С��

			if (shouldMove) {
				// ����ƫ����
				int offset = (int) (deltaY * MOVE_FACTOR);

				// ������ָ���ƶ����ƶ�����
				contentView.layout(originalRect.left,
						originalRect.top + offset, originalRect.right,
						originalRect.bottom + offset);

				isMoved = true; // ��¼�ƶ��˲���
			}

			break;
		default:
			break;
		}

		return super.dispatchTouchEvent(ev);
	}

	private boolean hasNotDoneActionDown = true;

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (redirectTouchToStickyView) {
			ev.offsetLocation(0,
					((getScrollY() + mStickyViewTopOffset) - mCurrentStickyView
							.getTop()));
		}

		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			hasNotDoneActionDown = false;
		}

		if (hasNotDoneActionDown) {
			MotionEvent down = MotionEvent.obtain(ev);
			down.setAction(MotionEvent.ACTION_DOWN);
			super.onTouchEvent(down);
			hasNotDoneActionDown = false;
		}

		if (ev.getAction() == MotionEvent.ACTION_UP
				|| ev.getAction() == MotionEvent.ACTION_CANCEL) {
			hasNotDoneActionDown = true;
		}
		return super.onTouchEvent(ev);
	}

	/**
	 * �ж��Ƿ����������
	 */
	private boolean isCanPullDown() {
		return getScrollY() == 0
				|| contentView.getHeight() < getHeight() + getScrollY();
	}

	/**
	 * �ж��Ƿ�������ײ�
	 */
	private boolean isCanPullUp() {
		return contentView.getHeight() <= getHeight() + getScrollY();
	}

	ScrollViewListener scrollViewListener;

	public void setScrollViewListener(ScrollViewListener scrollViewListener) {
		this.scrollViewListener = scrollViewListener;
	}

}