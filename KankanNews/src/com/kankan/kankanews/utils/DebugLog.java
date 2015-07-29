package com.kankan.kankanews.utils;

import java.util.Locale;

import com.kankanews.kankanxinwen.BuildConfig;

import android.util.Log;

public class DebugLog {
	public final static String tag = "DebugLog";
	private static final boolean DEBUG = BuildConfig.DEBUG;

	public static int e(String msg) {
		if (DEBUG) {
			return Log.e(tag, msg);
		}
		return 0;
	}

	public static int e(String tag, String msg, Throwable tr) {
		if (DEBUG) {
			return Log.e(tag, msg, tr);
		}

		return 0;
	}

	public static int efmt(String tag, String fmt, Object... args) {
		if (DEBUG) {
			String msg = String.format(Locale.US, fmt, args);
			return Log.e(tag, msg);
		}

		return 0;
	}

	public static int i(String tag, String msg) {
		if (DEBUG) {
			return Log.i(tag, msg);
		}

		return 0;
	}

	public static int i(String tag, String msg, Throwable tr) {
		if (DEBUG) {
			return Log.i(tag, msg, tr);
		}

		return 0;
	}

	public static int ifmt(String tag, String fmt, Object... args) {
		if (DEBUG) {
			String msg = String.format(Locale.US, fmt, args);
			return Log.i(tag, msg);
		}

		return 0;
	}

	public static int w(String tag, String msg) {
		if (DEBUG) {
			return Log.w(tag, msg);
		}

		return 0;
	}

	public static int w(String tag, String msg, Throwable tr) {
		if (DEBUG) {
			return Log.w(tag, msg, tr);
		}

		return 0;
	}

	public static int wfmt(String tag, String fmt, Object... args) {
		if (DEBUG) {
			String msg = String.format(Locale.US, fmt, args);
			return Log.w(tag, msg);
		}

		return 0;
	}

	public static int d(String tag, String msg) {
		if (DEBUG) {
			return Log.d(tag, msg);
		}

		return 0;
	}

	public static int d(String tag, String msg, Throwable tr) {
		if (DEBUG) {
			return Log.d(tag, msg, tr);
		}

		return 0;
	}

	public static int dfmt(String tag, String fmt, Object... args) {
		if (DEBUG) {
			String msg = String.format(Locale.US, fmt, args);
			return Log.d(tag, msg);
		}

		return 0;
	}

	public static int v(String tag, String msg) {
		if (DEBUG) {
			return Log.v(tag, msg);
		}

		return 0;
	}

	public static int v(String tag, String msg, Throwable tr) {
		if (DEBUG) {
			return Log.v(tag, msg, tr);
		}

		return 0;
	}

	public static int vfmt(String tag, String fmt, Object... args) {
		if (DEBUG) {
			String msg = String.format(Locale.US, fmt, args);
			return Log.v(tag, msg);
		}

		return 0;
	}

	public static void printStackTrace(Throwable e) {
		if (DEBUG) {
			e.printStackTrace();
		}
	}

	public static void printCause(Throwable e) {
		if (DEBUG) {
			Throwable cause = e.getCause();
			if (cause != null)
				e = cause;

			printStackTrace(e);
		}
	}
}
