
package com.iresearch.android.constants;

/**
 * @file Constants.java
 * @create 2012-10-9 上午9:54:22
 * @author lilong
 * @description TODO 应用存储常量
 */
public interface Constants {

    /** apk文件映射类型   */
    public static final String MIMETYPE_APK = "application/vnd.android.package-archive";

    // google map constants
    public static final String INTENT_KEY_URI = "uri";
    public static final String AUTHORITY_MAP = "map";
    public static final String QUERY_PARAM_LAT = "lat";
    public static final String QUERY_PARAM_LNG = "lng";
    public static final String INTENT_KEY_LATITUDE = "latitude";
    public static final String INTENT_KEY_LONGITUDE = "longitude";
    public static final String GOOGLE_MAPS_API_KEY_DEBUG = "AIzaSyBUqVVSlsAlJwwQ0yJnxHD8o_S_oIHxTUg";
    public static final String GOOGLE_MAPS_API_KEY_RELEASE = "AIzaSyAzEWKI7wwyp_PgZ0LW9RlESZb3qWxq_QY";
    public static final String GOOGLE_MAPS_API_KEY = Config.DEBUG ? GOOGLE_MAPS_API_KEY_DEBUG : GOOGLE_MAPS_API_KEY_RELEASE;
}
