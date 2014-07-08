
package com.iresearch.android.app;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.UnknownHostException;
import android.content.Context;
import android.widget.Toast;
import com.iresearch.android.R;

public class AppException extends Exception {
    private static final long serialVersionUID = 1L;

    /** 定义异常类型 */
    public final static byte TYPE_NETWORK = 0x01;
    public final static byte TYPE_SOCKET = 0x02;
    public final static byte TYPE_HTTP_CODE = 0x03;
    public final static byte TYPE_HTTP_ERROR = 0x04;
    public final static byte TYPE_XML = 0x05;
    public final static byte TYPE_IO = 0x06;
    public final static byte TYPE_RUN = 0x07;
    public final static byte TYPE_JSON = 0x08;

    private int mErrorCode;
    private byte mErrorType;

    private AppException() {
    }

    private AppException(byte type, int code, Exception exception) {
        super(exception);
        this.mErrorType = type;
        this.mErrorCode = code;
    }

    public int getErrorCode() {
        return mErrorCode;
    }

    public int getErrorType() {
        return mErrorType;
    }

    public static AppException http(int code) {
        return new AppException(TYPE_HTTP_CODE, code, null);
    }

    public static AppException http(Exception e) {
        return new AppException(TYPE_HTTP_ERROR, 0, e);
    }

    public static AppException socket(Exception e) {
        return new AppException(TYPE_SOCKET, 0, e);
    }

    public static AppException io(Exception e) {
        if (e instanceof UnknownHostException || e instanceof ConnectException) {
            return new AppException(TYPE_NETWORK, 0, e);
        } else if (e instanceof IOException) {
            return new AppException(TYPE_IO, 0, e);
        }
        return run(e);
    }

    public static AppException xml(Exception e) {
        return new AppException(TYPE_XML, 0, e);
    }

    public static AppException json(Exception e) {
        return new AppException(TYPE_JSON, 0, e);
    }

    public static AppException network(Exception e) {
        if (e instanceof UnknownHostException || e instanceof ConnectException) {
            return new AppException(TYPE_NETWORK, 0, e);
        } else if (e instanceof SocketException) {
            return socket(e);
        }
        return http(e);
    }

    public static AppException run(Exception e) {
        return new AppException(TYPE_RUN, 0, e);
    }

    /**
     * 获取APP异常崩溃处理对象
     * 
     * @param context
     * @return
     */
    public static AppException getAppExceptionHandler() {
        return new AppException();
    }
    
    public void makeToast(Context ctx) {
        switch (getErrorType()) {
            case TYPE_HTTP_CODE:
                String err = ctx.getString(R.string.http_status_code_error, getErrorCode());
                Toast.makeText(ctx, err, Toast.LENGTH_SHORT).show();
                break;
            case TYPE_HTTP_ERROR:
                Toast.makeText(ctx, R.string.http_exception_error, Toast.LENGTH_SHORT).show();
                break;
            case TYPE_SOCKET:
                Toast.makeText(ctx, R.string.socket_exception_error, Toast.LENGTH_SHORT).show();
                break;
            case TYPE_NETWORK:
                Toast.makeText(ctx, R.string.network_not_connected, Toast.LENGTH_SHORT).show();
                break;
            case TYPE_XML:
                Toast.makeText(ctx, R.string.xml_parser_failed, Toast.LENGTH_SHORT).show();
                break;
            case TYPE_JSON:
                Toast.makeText(ctx, R.string.xml_parser_failed, Toast.LENGTH_SHORT).show();
                break;
            case TYPE_IO:
                Toast.makeText(ctx, R.string.io_exception_error, Toast.LENGTH_SHORT).show();
                break;
            case TYPE_RUN:
                Toast.makeText(ctx, R.string.app_run_code_error, Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
