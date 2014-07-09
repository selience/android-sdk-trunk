
package com.iresearch.android.receiver;

import java.io.File;
import android.net.Uri;
import android.content.Context;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;

public class MediaScannerNotification implements MediaScannerConnectionClient {

    private File mRootFile;
    private MediaScannerConnection mConnection;
    private OnMediaScannerStateListener mOnMediaScannerStateListener;

    public MediaScannerNotification(Context context, File rootFile) {
        this.mRootFile = rootFile;
        this.mConnection = new MediaScannerConnection(context, this);
    }

    public void start() {
        mConnection.connect();
    }

    @Override
    public void onMediaScannerConnected() {
        mConnection.scanFile(mRootFile.getAbsolutePath(), null);
        if (mOnMediaScannerStateListener != null) {
            mOnMediaScannerStateListener.onMediaScannerConnected();
        }
    }

    @Override
    public void onScanCompleted(String path, Uri uri) {
        mConnection.disconnect();
        if (mOnMediaScannerStateListener != null) {
            mOnMediaScannerStateListener.onScanCompleted(path, uri);
        }
    }

    public static interface OnMediaScannerStateListener {

        void onMediaScannerConnected();

        void onScanCompleted(String path, Uri uri);
    }
}
