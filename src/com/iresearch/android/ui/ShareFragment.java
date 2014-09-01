
package com.iresearch.android.ui;

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.SinaWeiboApi20;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import com.android.sdk.base.BaseFragment;
import com.android.sdk.base.WebViewFragment;
import com.android.sdk.log.DebugLog;
import com.android.sdk.utils.SettingUtils;
import com.android.sdk.utils.Toaster;
import com.iresearch.android.R;
import com.iresearch.android.app.AppContext;
import com.iresearch.android.thread.RequestExecutor;

public class ShareFragment extends BaseFragment implements OnClickListener {

    public static final int SHARE_SINA_WEIBO = 0x1000;
    
    private static final Token EMPTY_TOKEN = null;
    private static final String GET_USER_INFO = "https://api.weibo.com/2/users/show.json";
    private static final String PUBLISH_WEIBO_TEXT = "https://api.weibo.com/2/statuses/update.json";
    private static final String PUBLISH_WEIBO_TEXT_WITH_IMAGE = "https://upload.api.weibo.com/2/statuses/upload.json";
    
    private OAuthService mOAuthervice;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.share_layout, container, false);
    }
    
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
       view.findViewById(R.id.share_sina_weibo).setOnClickListener(this);
       view.findViewById(R.id.share_tencent_weibo).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.share_sina_weibo:
                mOAuthervice=createSinaWeibo();
                if (!hasToken(R.string.sina_weibo_token)) {
                    launchAuthClient(mOAuthervice.getAuthorizationUrl(EMPTY_TOKEN));
                } else {
                    RequestExecutor.doAsync(new Runnable() {
                        @Override
                        public void run() {
                            String token=SettingUtils.get(mActivity, R.string.sina_weibo_token, "");
                            getUserUid(new Token(token, token));
                        }
                    });
                }
                break;
            case R.id.share_tencent_weibo:
                break;
        }
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode==Activity.RESULT_OK && requestCode==SHARE_SINA_WEIBO) {
            final String code = data.getStringExtra("code");

            RequestExecutor.doAsync(new Runnable() {
                @Override
                public void run() {
                    Verifier verifier = new Verifier(code);
                    Token accessToken = mOAuthervice.getAccessToken(EMPTY_TOKEN, verifier);
                    SettingUtils.set(mActivity, R.string.sina_weibo_token, accessToken.getToken());
                    DebugLog.v(accessToken.toString());
                    
                    getUserUid(accessToken);
                }
            });
        }
    }
    
    private boolean hasToken(int resId) {
        return SettingUtils.contains(mActivity, resId);
    }
    
    private OAuthService createSinaWeibo() {
        return new ServiceBuilder()
            .provider(SinaWeiboApi20.class)
            .apiKey("3275598438")
            .apiSecret("8d60dedab2c572e6f7603ea9f47fa729")
            .callback("http://weibo.com/selience")
            .build();
    }
    
    private void launchAuthClient(String authorizationUrl) {
        Bundle bundle = new Bundle();
        bundle.putString(WebViewFragment.INTENT_KEY_URI, authorizationUrl);
        replace(WebAuthFragment.class, "WebAuthFragment", bundle, this, SHARE_SINA_WEIBO);
    }
    
    /*
     * 获取用户信息
     */
    private void getUserUid(Token accessToken) {
        OAuthRequest request = new OAuthRequest(Verb.GET, GET_USER_INFO);
        request.addQuerystringParameter("screen_name", "selience");
        mOAuthervice.signRequest(accessToken, request);
        Response response = request.send();
        Toaster.show(mActivity, response.getCode()+"-"+response.getBody());
        DebugLog.e(response.getCode()+","+response.getBody());
        
        sendMessage(accessToken);
    }
    
    /*
     * 发布一条微博消息
     */
    private void sendMessage(Token accessToken) {
        OAuthRequest request = new OAuthRequest(Verb.POST, PUBLISH_WEIBO_TEXT);
        request.addBodyParameter("status", "发布一条测试消息"); // 要发布的微博文本内容，必须做URLencode，内容不超过140个汉字
        request.addBodyParameter("lat", AppContext.latitude+"");
        request.addBodyParameter("long", AppContext.longitude+"");
        mOAuthervice.signRequest(accessToken, request);
        Response response = request.send();
        
        DebugLog.e(response.getCode()+","+response.getBody());
    }
    
    /*
     * 发布一条微博消息并附带一张图片
     */
    private void sendMessageWithImage(Token accessToken) {
        OAuthRequest request = new OAuthRequest(Verb.POST, PUBLISH_WEIBO_TEXT_WITH_IMAGE);
        request.addHeader("Content-Type", "multipart/form-data");  // 请求必须用POST方式提交，并且注意采用multipart/form-data编码方式； 
        
        request.addBodyParameter("status", "发布一条测试消息"); // 要发布的微博文本内容，必须做URLencode，内容不超过140个汉字
        request.addBodyParameter("lat", AppContext.latitude+"");
        request.addBodyParameter("long", AppContext.longitude+"");
        
        mOAuthervice.signRequest(accessToken, request);
        Response response = request.send();
        
        DebugLog.e(response.getCode()+","+response.getBody());
    }
}
