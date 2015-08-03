package com.kankan.kankanews.utils;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.widget.TextView;

public class FontUtils {
	public static boolean fontSizeHasChanged = false;

	public static float DEFAULT_FONT_RADIX = 1;

	public static String[] fontSizeShow = new String[] { "小", "中", "大", "特大" };

	public static float[] fontSize = new float[] { 0.8f, 1, 1.2f, 1.4f };

	public static String[] fontSizeWeb = new String[] { "s", "m", "l", "xl" };

	public static void setTextViewFontSize(Fragment fragment, TextView view,
			int resourceId, float radix) {
		float fontSize = Float.parseFloat(fragment.getResources().getString(
				resourceId))
				* radix;

		view.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
	}

	public static void setTextViewFontSize(Activity activity, TextView view,
			int resourceId, float radix) {
		float fontSize = Float.parseFloat(activity.getResources().getString(
				resourceId))
				* radix;

		view.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
	}

	public static boolean hasChangeFontSize() {
		// TODO Auto-generated method stub
		return fontSizeHasChanged;
	}

	public static void setChangeFontSize(boolean hasChanged) {
		// TODO Auto-generated method stub
		fontSizeHasChanged = hasChanged;
	}

	public static int getFontSetIndex(float radix) {
		if (radix == fontSize[0])
			return 0;
		if (radix == fontSize[2])
			return 2;
		if (radix == fontSize[3])
			return 3;
		return 1;
	}
}
