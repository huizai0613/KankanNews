package com.kankan.kankanews.utils;

import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.widget.TextView;

public class FontUtils {
	public static boolean fontSizeHasChanged = false;
	
	public static float DEFAULT_FONT_RADIX = 1;

	public static void setTextViewFontSize(Fragment fragment, TextView view,
			int resourceId, float radix) {
		float fontSize = Float.parseFloat(fragment.getResources().getString(
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
}
