package com.iresearch.android.http;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ConnectionChangedBroadcastReceiver extends BroadcastReceiver {

    private BetterHttp http;

    public ConnectionChangedBroadcastReceiver(BetterHttp http) {
        this.http = http;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        http.updateProxySettings(context);
    }

}
