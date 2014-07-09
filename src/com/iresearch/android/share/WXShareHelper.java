package com.iresearch.android.share;

import android.content.Context;
import android.graphics.Bitmap;
import java.io.ByteArrayOutputStream;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXAppExtendObject;
import com.tencent.mm.sdk.openapi.WXImageObject;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXMusicObject;
import com.tencent.mm.sdk.openapi.WXTextObject;
import com.tencent.mm.sdk.openapi.WXVideoObject;
import com.tencent.mm.sdk.openapi.WXWebpageObject;
import android.graphics.Bitmap.CompressFormat;

/**
 * 分享到微信朋友圈操作类
 * @author Jacky.Lee
 * @version 1.0
 * @created 2014-02-13
 */
public class WXShareHelper {
    // 分享缩略图大小
    private static final int THUMB_SIZE = 100;
    // APP_ID 替换为你的应用从官方网站申请到的合法appId
    private static String APP_ID = "wx56057ea0eaab2623";
    // 微信朋友圈支持的版本需要大于4.2
    private static final int TIMELINE_SUPPORTED_VERSION = 0x21020001; 
    
    /**
     * 创建一个WXAPI访问接口，应用初始化的时候调用
     * @param context 创建WXAPI的Context对象
     * @param appId 创建WXAPI接口的appId
     * @return 以appId身份访问的IWXAPI接口
     */
    public static IWXAPI bindWXApp(Context context) {
        IWXAPI api = WXAPIFactory.createWXAPI(context, APP_ID, true);
        api.registerApp(APP_ID);
        return api;
    }
    
    /**
     * 启动微信客户端
     * @param api IWXAPI访问接口
     * @return 启动状态
     */
    public static boolean openWXApp(IWXAPI api) {
        return api.openWXApp();
    }
    
    /**
     * 检查当前设备是否安装微信客户端
     * @param api   IWXAPI访问接口
     * @return true 安装，false 未安装
     */
    public static boolean checkWXInstalled(IWXAPI api) {
        return api.isWXAppInstalled();
    }
    
    /**
     * 检查当前微信版本是否支持发送到朋友圈
     * @param api   IWXAPI访问接口
     * @return true 大于4.2版本，false 小于4.2版本
     */
    public static boolean checkWXSupportTimeline(IWXAPI api) {
        int wxSdkVersion = api.getWXAppSupportAPI(); 
        return wxSdkVersion >= TIMELINE_SUPPORTED_VERSION;
    }
    
    /**
     * 分享文本到微信或朋友圈
     * @param api   IWXAPI访问接口
     * @param text  分享的文字
     * @param isTimelineCb 是否发送到朋友圈[true:发送朋友圈，false:发送给指定的好友]
     * @return  true 发送成功，false 发送失败
     */
    public static boolean sendTextToWXApp(IWXAPI api, String text, boolean isTimelineCb) {
        // 初始化一个WXTextObject对象
        WXTextObject textObj = new WXTextObject();
        textObj.text = text;

        // 用WXTextObject对象初始化一个WXMediaMessage对象
        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = textObj;
        msg.description = text;

        // 构造一个Req
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis()); // transaction字段用于唯一标识一个请求
        req.message = msg;
        // 是否发送到朋友圈
        req.scene = isTimelineCb ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;
        // 调用api接口发送数据到微信
        return api.sendReq(req);
    }
    
    
    /**
     * 分享图片到微信或朋友圈 ，注意图片大小不能超过32kb，否则不能跳转；
     * @param api   IWXAPI访问接口
     * @param bmp   分享的图片
     * @param isTimelineCb 是否发送到朋友圈，(true 发送朋友圈，false 发送给指定的好友)
     * @return  true 发送成功，false 发送失败
     */
    public static boolean sendImageToWXApp(IWXAPI api, Bitmap bmp, boolean isTimelineCb) {
        WXImageObject imgObj = new WXImageObject(bmp);
        
        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = imgObj;
        
        Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, THUMB_SIZE, THUMB_SIZE, true);
        bmp.recycle();
        msg.thumbData = bmpToByteArray(thumbBmp, true);  // 设置缩略图

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = msg;
        req.scene = isTimelineCb ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;
        return api.sendReq(req);
    }
    
    /**
     * 分享音乐到微信或朋友圈 
     * @param api   IWXAPI访问接口
     * @param bmp   音乐地址
     * @param title 标题
     * @param description 描述信息
     * @param thumbData 缩略图，如果为null，则默认显示认证图片；
     * @param isTimelineCb 是否发送到朋友圈[true:发送朋友圈，false:发送给指定的好友]
     * @return  true 发送成功，false 发送失败
     */
    public static boolean sendMusicToWXApp(IWXAPI api, String musicUrl, String title, 
            String description, Bitmap bmp, boolean isTimelineCb) {
        WXMusicObject music = new WXMusicObject();
        music.musicUrl = musicUrl;

        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = music;
        msg.title = title;
        msg.description = description;
        if (bmp != null) {
            Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, THUMB_SIZE, THUMB_SIZE, true);
            bmp.recycle();
            msg.thumbData = bmpToByteArray(thumbBmp, true);  // 设置缩略图
        }

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = msg;
        req.scene = isTimelineCb ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;
        return api.sendReq(req);
    }
    
    /**
     * 分享视频到微信或朋友圈 
     * @param api   IWXAPI访问接口
     * @param bmp   视频地址
     * @param title 标题
     * @param description 描述信息
     * @param isTimelineCb 是否发送到朋友圈[true:发送朋友圈，false:发送给指定的好友]
     * @return  true 发送成功，false 发送失败
     */
    public static boolean sendVideoToWX(IWXAPI api, String videoUrl, String title, 
            String description, Bitmap bmp, boolean isTimelineCb) {
        WXVideoObject video = new WXVideoObject();
        video.videoUrl = videoUrl;

        WXMediaMessage msg = new WXMediaMessage();;
        msg.mediaObject = video;
        msg.title = title;
        msg.description = description;
        if (bmp != null) {
            Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, THUMB_SIZE, THUMB_SIZE, true);
            bmp.recycle();
            msg.thumbData = bmpToByteArray(thumbBmp, true);  // 设置缩略图
        }
        
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = msg;
        req.scene = isTimelineCb ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;
        return api.sendReq(req);
    }
    
    /**
     * 分享网页链接到微信或朋友圈 
     * @param api   IWXAPI访问接口
     * @param bmp   视频地址
     * @param title 标题
     * @param description 描述信息
     * @param isTimelineCb 是否发送到朋友圈[true:发送朋友圈，false:发送给指定的好友]
     * @return  true 发送成功，false 发送失败
     */
    public static boolean sendWebPageToWX(IWXAPI api, String webUrl, String title, 
            String description, Bitmap bmp, boolean isTimelineCb) {
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = webUrl;

        WXMediaMessage msg = new WXMediaMessage();;
        msg.mediaObject = webpage;
        msg.title = title;
        msg.description = description;
        if (bmp != null) {
            Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, THUMB_SIZE, THUMB_SIZE, true);
            bmp.recycle();
            msg.thumbData = bmpToByteArray(thumbBmp, true);  // 设置缩略图
        }
        
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = msg;
        req.scene = isTimelineCb ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;
        return api.sendReq(req);
    }
    
    /**
     * 分享二进制数据网页到微信或朋友圈 
     * @param api   IWXAPI访问接口
     * @param bmp   视频地址
     * @param title 标题
     * @param description 描述信息
     * @param isTimelineCb 是否发送到朋友圈[true:发送朋友圈，false:发送给指定的好友]
     * @return  true 发送成功，false 发送失败
     */
    public static boolean sendObjectToWX(IWXAPI api, String title, String description, 
            Bitmap bmp, boolean isTimelineCb) {
        WXAppExtendObject appdata = new WXAppExtendObject();
        appdata.extInfo=title;
        
        WXMediaMessage mMsg = new WXMediaMessage();
        mMsg.title = title;
        mMsg.description = description;
        mMsg.mediaObject=appdata;
        
        if (bmp != null) {
            Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, THUMB_SIZE, THUMB_SIZE, true);
            bmp.recycle();
            mMsg.thumbData = bmpToByteArray(thumbBmp, true);  // 设置缩略图
        }
                
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = mMsg;
        req.scene = isTimelineCb ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;
        return api.sendReq(req);
    }
    
    private static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(CompressFormat.PNG, 100, output);
        if (needRecycle) {
            bmp.recycle();
        }
        
        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return result;
    }
}

