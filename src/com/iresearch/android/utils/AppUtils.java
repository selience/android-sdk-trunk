package com.iresearch.android.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import org.mariotaku.android.app.AppManager;
import org.mariotaku.android.utils.IntentUtils;
import com.iresearch.android.R;

public class AppUtils {
    
    /**
     * 发送App异常崩溃报告
     * 
     * @param context
     * @param crashReport
     */
    public static void sendAppCrashReport(final Context context, final String crashReport) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setIcon(android.R.drawable.ic_dialog_info);
        builder.setTitle(R.string.app_error);
        builder.setMessage(R.string.app_error_message);
        builder.setPositiveButton(R.string.submit_report,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        // 发送异常报告
                        Intent intent=IntentUtils.newEmailIntent("Android客户端 -错误报告", 
                                crashReport, null, "453154389@qq.com");
                        context.startActivity(Intent.createChooser(intent, "发送错误报告"));                        
                        // 退出
                        AppManager.getAppManager().AppExit(context);
                    }
                });
        builder.setNegativeButton(R.string.sure,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        // 退出
                        AppManager.getAppManager().AppExit(context);
                    }
                });
        builder.show();
    }
    
    /**
     * 退出程序
     * 
     * @param context
     */
    public static void onAppExit(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setIcon(android.R.drawable.ic_dialog_info);
        builder.setTitle(R.string.app_menu_surelogout);
        builder.setPositiveButton(R.string.sure,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        // 退出
                        AppManager.getAppManager().AppExit(context);
                    }
                });
        builder.setNegativeButton(R.string.cancle,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.show();
    }

}
