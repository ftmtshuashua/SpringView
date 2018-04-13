package com.lfp.widget.demo.activity.demo3;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.util.Log;

public class LogUtil {

    private static String TAG = "LogUtil";
    public static int logLevel = Log.VERBOSE;
    private static LogUtil logger = new LogUtil();

    public static LogUtil getLogger() {
        return logger;
    }

    private LogUtil() {
    }

    private static boolean isDebug = true;

    public void setTag(String tag) {
        TAG = tag;
    }

    public static void setDebug(boolean is) {
        isDebug = is;
        Log.i(TAG, "isDebug:" + is);
    }

    public static void init(Context context) {
        setDebug(isApkDebugable(context));
    }

    /**
     * 但是当我们没在AndroidManifest.xml中设置其debug属性时:
     * 使用Eclipse运行这种方式打包时其debug属性为true,使用Eclipse导出这种方式打包时其debug属性为法false.
     * 在使用ant打包时，其值就取决于ant的打包参数是release还是debug.
     * 因此在AndroidMainifest.xml中最好不设置android:debuggable属性置，而是由打包方式来决定其值.
     *
     * @param context
     * @return
     * @author SHANHY
     * @date 2015-8-7
     */
    public static boolean isApkDebugable(Context context) {
        try {
            ApplicationInfo info = context.getApplicationInfo();
            return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Exception e) {
        }
        return false;
    }

    public static boolean isDebug() {
        return isDebug;
    }

    /**
     * 分开打印长度很长的日志
     */
    public void longStr(String longStr) {
        int index = 0;
        while ((longStr.length() - index) > 0) {
            int end = index + 2000;
            String text = longStr.substring(index, end < longStr.length() ? end : longStr.length());
            LogUtil.getLogger().e(text);
            index += 2000;
        }
    }

    private String getFunctionName() {
        StackTraceElement[] sts = Thread.currentThread().getStackTrace();
        if (sts == null) return null;
        for (StackTraceElement st : sts) {
            if (st.isNativeMethod()) continue;
            if (st.getClassName().equals(Thread.class.getName())) continue;
            if (st.getClassName().equals(this.getClass().getName())) continue;
            return "[ " + Thread.currentThread().getName() + ": " + st.getFileName() + ":" + st.getLineNumber() + ":" + st.getMethodName() + " ]";
        }
        return null;
    }

    public void i(Object str) {
        if (!isDebug) return;
        if (logLevel <= Log.INFO) {
            String name = getFunctionName();
            if (name != null) Log.i(TAG, name + " - " + str);
            else Log.i(TAG, str.toString());
        }
    }

    public void v(Object str) {
        if (!isDebug) return;
        if (logLevel <= Log.VERBOSE) {
            String name = getFunctionName();
            if (name != null) Log.v(TAG, name + " - " + str);
            else Log.v(TAG, str.toString());
        }
    }

    public void w(Object str) {
        if (!isDebug) return;
        if (logLevel <= Log.WARN) {
            String name = getFunctionName();
            if (name != null) Log.w(TAG, name + " - " + str);
            else Log.w(TAG, str.toString());
        }
    }


    public void e(Object str) {
        if (!isDebug) return;
        if (logLevel <= Log.ERROR) {
            String name = getFunctionName();
            if (name != null) Log.e(TAG, name + " - " + str);
            else Log.e(TAG, str.toString());
        }
    }

    public void e(Exception ex) {
        if (!isDebug) return;
//        if (logLevel <= Log.ERROR) Log.e(TAG, "error", ex);
        String name = getFunctionName();
        if (name != null) Log.e(TAG, "Error:" + name, ex);
        else Log.e(TAG, "Error:", ex);

    }

    public void d(Object str) {
        if (!isDebug) return;
        if (logLevel <= Log.DEBUG) {
            String name = getFunctionName();
            if (name != null) Log.d(TAG, name + " - " + str);
            else Log.d(TAG, str.toString());
        }
    }
}
