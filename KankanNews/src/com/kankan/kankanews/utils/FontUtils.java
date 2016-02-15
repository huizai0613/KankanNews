package com.kankan.kankanews.utils;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.widget.TextView;

public class FontUtils {
	public static boolean fontSizeHasChanged = false;

	public static boolean mainActivityFontSizeHasChanged = false;

	public static boolean subjectFontSizeHasChanged = false;

	public static boolean searchFontSizeHasChanged = false;

	public static boolean revelationsBreaknewsFontSizeHasChanged = false;

	public static boolean revelationsActivityFontSizeHasChanged = false;

	public static boolean revelationsFragmentFontSizeHasChanged = false;

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

	public static void setTextViewFontSizeDIP(Fragment fragment, TextView view,
			int resourceId, float radix) {
		float fontSize = Float.parseFloat(fragment.getResources().getString(
				resourceId))
				* radix;

		view.setTextSize(TypedValue.COMPLEX_UNIT_DIP, fontSize);
	}

	public static void setTextViewFontSizeDIP(Activity activity, TextView view,
			int resourceId, float radix) {
		float fontSize = Float.parseFloat(activity.getResources().getString(
				resourceId))
				* radix;

		view.setTextSize(TypedValue.COMPLEX_UNIT_DIP, fontSize);
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

	public static void setChangseFontSize(boolean hasChanged) {
		// TODO Auto-generated method stub
		fontSizeHasChanged = hasChanged;
	}

	public static void chagneFontSizeGlobal() {

		mainActivityFontSizeHasChanged = true;

		subjectFontSizeHasChanged = true;

		searchFontSizeHasChanged = true;

		revelationsBreaknewsFontSizeHasChanged = true;

		revelationsActivityFontSizeHasChanged = true;

		revelationsFragmentFontSizeHasChanged = true;

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

	public static boolean isMainActivityFontSizeHasChanged() {
		return mainActivityFontSizeHasChanged;
	}

	public static void setMainActivityFontSizeHasChanged(
			boolean mainActivityFontSizeHasChanged) {
		FontUtils.mainActivityFontSizeHasChanged = mainActivityFontSizeHasChanged;
	}

	public static boolean isSubjectFontSizeHasChanged() {
		return subjectFontSizeHasChanged;
	}

	public static void setSubjectFontSizeHasChanged(
			boolean subjectFontSizeHasChanged) {
		FontUtils.subjectFontSizeHasChanged = subjectFontSizeHasChanged;
	}

	public static boolean isSearchFontSizeHasChanged() {
		return searchFontSizeHasChanged;
	}

	public static void setSearchFontSizeHasChanged(
			boolean searchFontSizeHasChanged) {
		FontUtils.searchFontSizeHasChanged = searchFontSizeHasChanged;
	}

	public static boolean isRevelationsBreaknewsFontSizeHasChanged() {
		return revelationsBreaknewsFontSizeHasChanged;
	}

	public static void setRevelationsBreaknewsFontSizeHasChanged(
			boolean revelationsBreaknewsFontSizeHasChanged) {
		FontUtils.revelationsBreaknewsFontSizeHasChanged = revelationsBreaknewsFontSizeHasChanged;
	}

	public static boolean isRevelationsActivityFontSizeHasChanged() {
		return revelationsActivityFontSizeHasChanged;
	}

	public static void setRevelationsActivityFontSizeHasChanged(
			boolean revelationsActivityFontSizeHasChanged) {
		FontUtils.revelationsActivityFontSizeHasChanged = revelationsActivityFontSizeHasChanged;
	}

	public static boolean isRevelationsFragmentFontSizeHasChanged() {
		return revelationsFragmentFontSizeHasChanged;
	}

	public static void setRevelationsFragmentFontSizeHasChanged(
			boolean revelationsFragmentFontSizeHasChanged) {
		FontUtils.revelationsFragmentFontSizeHasChanged = revelationsFragmentFontSizeHasChanged;
	}

}
