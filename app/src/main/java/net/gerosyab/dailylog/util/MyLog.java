package net.gerosyab.dailylog.util;

import android.util.Log;

public class MyLog {
//    private static final boolean DEBUG = true;
    private static final boolean DEBUG = false;

    public static void e(String TAG, String message) {
        if (DEBUG) Log.e(TAG, buildInfoMessage(message));
    }

    public static void w(String TAG, String message) {
        if (DEBUG) Log.w(TAG, buildInfoMessage(message));
    }

    public static void i(String TAG, String message) {
        if (DEBUG) Log.i(TAG, buildInfoMessage(message));
    }

    public static void d(String TAG, String message) {
        if (DEBUG) Log.d(TAG, buildInfoMessage(message));
    }

    public static void v(String TAG, String message) {
        if (DEBUG) Log.v(TAG, buildInfoMessage(message));
    }

    public static String buildInfoMessage(String message) {
        StackTraceElement ste = Thread.currentThread().getStackTrace()[4];
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        sb.append(ste.getFileName());
        sb.append("::");
        sb.append(ste.getMethodName()+"()");
        sb.append("] ");
        sb.append(message);
        return sb.toString();
    }
}
