package com.iresearch.android.receiver;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

public class DetachableResultReceiver extends ResultReceiver {

    private Receiver mReceiver;

    public DetachableResultReceiver(Handler handler) {
        super(handler);
    }

    public void clearReceiver() {
        mReceiver = null;
    }

    public void setReceiver(Receiver receiver) {
        mReceiver = receiver;
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        if (mReceiver != null) {
            mReceiver.onReceiveResult(resultCode, resultData);
        } 
    }
    
    public interface Receiver {

    	void onReceiveResult(int resultCode, Bundle resultData);
    }
}