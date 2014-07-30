
package com.iresearch.android.log;

import android.util.Log;

import java.util.Locale;

/**
 * Convenience class for logging. Logs the given parameters plus the 
 * calling class and line as a tag and the calling method's name
 */
public class L {

    private static boolean DEBUG = false; 

    /**
     * @param enabled 是否开启打印日志
     */
    public static void enableDebugLogging(boolean enabled) {
        DEBUG = enabled;
    }

    static public void v(String msgFormat) {
        log(Log.VERBOSE, null, msgFormat, null);
    }
    
    static public void v(String tag, String msgFormat) {
        log(Log.VERBOSE, tag, msgFormat, null);
    }
    
    static public void v(String tag, String msgFormat, Throwable t) {
        log(Log.VERBOSE, tag, msgFormat, t);
    }

    static public void d(String msgFormat) {
        log(Log.DEBUG, null, msgFormat, null);
    }
    
    static public void d(String tag, String msgFormat) {
        log(Log.DEBUG, tag, msgFormat, null);
    }
    
    static public void d(String tag, String msgFormat, Throwable t) {
        log(Log.DEBUG, tag, msgFormat, t);
    }
    
    static public void i(String msgFormat) {
        log(Log.INFO, null, msgFormat, null);
    }
    
    static public void i(String tag, String msgFormat) {
        log(Log.INFO, tag, msgFormat, null);
    }
    
    static public void i(String tag, String msgFormat, Throwable t) {
        log(Log.INFO, tag, msgFormat, t);
    }
    
    static public void w(String msgFormat) {
        log(Log.WARN, null, msgFormat, null);
    }
    
    static public void w(String tag, String msgFormat) {
        log(Log.WARN, tag, msgFormat, null);
    }
    
    static public void w(String tag, String msgFormat, Throwable t) {
        log(Log.WARN, tag, msgFormat, t);
    }
    
    static public void e(String msgFormat) {
        log(Log.ERROR, null, msgFormat, null);
    }
    
    static public void e(String tag, String msgFormat) {
        log(Log.ERROR, tag, msgFormat, null);
    }
    
    static public void e(String tag, String msgFormat, Throwable t) {
        log(Log.ERROR, tag, msgFormat, t);
    }
    
    private static void log(final int level, final String s1, final String msgFormat, final Throwable t) {
        if (!DEBUG) return;

        final Thread thread = Thread.currentThread();
        final String msg = (msgFormat == null) ? "" : msgFormat;
        final StackTraceElement stackTraceElement = thread.getStackTrace()[4];
        final String fullClassName = stackTraceElement.getClassName();
        final String className = fullClassName.substring(fullClassName.lastIndexOf(".") + 1);
        final String tag = (s1 == null) ? className : s1; 
                
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(stackTraceElement.getMethodName());
        stringBuilder.append("@");
        stringBuilder.append(stackTraceElement.getLineNumber());
        stringBuilder.append(">>>");
        stringBuilder.append(String.format(Locale.US, "[%d:%s] %s", thread.getId(), thread.getName(), msg));
        
        switch (level) {
            case Log.VERBOSE:
                if (t != null) {
                    Log.v(tag, stringBuilder.toString(), t);
                } else {
                    Log.v(tag, stringBuilder.toString());
                }
                break;
            case Log.DEBUG:
                if (t != null) {
                    Log.d(tag, stringBuilder.toString(), t);
                } else {
                    Log.d(tag, stringBuilder.toString());
                }
                break;
            case Log.INFO:
                if (t != null) {
                    Log.i(tag, stringBuilder.toString(), t);
                } else {
                    Log.i(tag, stringBuilder.toString());
                }
                break;
            case Log.WARN:
                if (t != null) {
                    Log.w(tag, stringBuilder.toString(), t);
                } else {
                    Log.w(tag, stringBuilder.toString());
                }
                break;
            case Log.ERROR:
                if (t != null) {
                    Log.e(tag, stringBuilder.toString(), t);
                } else {
                    Log.e(tag, stringBuilder.toString());
                }
                break;
        }
    }

}