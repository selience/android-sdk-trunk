
package com.iresearch.android.utils;

import java.util.List;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnKeyListener;
import android.view.KeyEvent;
import com.iresearch.android.R;

public class DialogUtils {

    public static ProgressDialog newProgressDialog(final Activity activity,
            int progressDialogTitleId, int progressDialogMsgId) {
        ProgressDialog progressDialog = new ProgressDialog(activity);
        if (progressDialogTitleId <= 0) {
            progressDialogTitleId = R.string.progress_dialog_title;
        }
        progressDialog.setTitle(progressDialogTitleId);
        if (progressDialogMsgId <= 0) {
            progressDialogMsgId = R.string.progress_dialog_msg;
        }
        progressDialog.setMessage(activity.getString(progressDialogMsgId));
        progressDialog.setIndeterminate(true);
        progressDialog.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                activity.onKeyDown(keyCode, event);
                return false;
            }
        });
        return progressDialog;
    }

    public static ProgressDialog newProgressDialog(final Activity activity) {
        return newProgressDialog(activity, -1, -1);
    }

    public static AlertDialog.Builder newYesNoDialog(final Context context, String title,
            String message, String positiveButtonMessage, String negativeButtonMessage, int iconId,
            OnClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setPositiveButton(positiveButtonMessage, listener);
        builder.setNegativeButton(negativeButtonMessage, listener);

        builder.setTitle(title);
        builder.setMessage(message);
        builder.setIcon(iconId);

        return builder;
    }

    public static AlertDialog.Builder newYesNoDialog(final Context context, String title,
            String message, int iconId, OnClickListener listener) {
        return newYesNoDialog(context, title, message, context.getString(android.R.string.yes),
                context.getString(android.R.string.no), iconId, listener);
    }

    public static AlertDialog.Builder newYesNoDialog(final Context context, int titleId,
            int messageId, int iconId, OnClickListener listener) {
        return newYesNoDialog(context, context.getString(titleId), context.getString(messageId),
                context.getString(android.R.string.yes), context.getString(android.R.string.no),
                iconId, listener);
    }

    public static AlertDialog.Builder newYesNoDialog(final Context context, int titleId,
            int messageId, int positiveButtonMessageId, int negativeButtonMessageId, int iconId,
            OnClickListener listener) {
        return newYesNoDialog(context, context.getString(titleId), context.getString(messageId),
                context.getString(positiveButtonMessageId),
                context.getString(negativeButtonMessageId), iconId, listener);
    }

    public static AlertDialog.Builder newMessageDialog(final Context context, String title,
            String message, int iconId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setPositiveButton(context.getString(android.R.string.ok), new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.setTitle(title);
        builder.setMessage(message);
        builder.setIcon(iconId);

        return builder;
    }

    public static AlertDialog.Builder newMessageDialog(final Context context, int titleId,
            int messageId, int iconId) {
        return newMessageDialog(context, context.getString(titleId), context.getString(messageId),
                iconId);
    }

    public static AlertDialog.Builder newErrorDialog(final Activity activity, int titleId,
            Exception error) {
        return newErrorDialog(activity, activity.getString(titleId), error);
    }

    public static AlertDialog.Builder newErrorDialog(final Activity activity, String title,
            Exception error) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(title);
        builder.setMessage(error.getLocalizedMessage());
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setCancelable(false);
        builder.setPositiveButton(activity.getString(android.R.string.ok), new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        return builder;
    }

    public static <T> AlertDialog.Builder newListDialog(final Activity context, String title,
            final List<T> elements, final OnClickListener listener) {
        return newListDialog(context, title, elements, listener, 0);
    }

    public static <T> AlertDialog.Builder newListDialog(final Activity context, String title,
            final List<T> elements, final OnClickListener listener, int selectedItem) {
        final int entriesSize = elements.size();
        String[] entries = new String[entriesSize];
        for (int i = 0; i < entriesSize; i++) {
            entries[i] = elements.get(i).toString();
        }

        Builder builder = new AlertDialog.Builder(context);
        if (title != null) {
            builder.setTitle(title);
        }
        builder.setSingleChoiceItems(entries, selectedItem, listener);

        return builder;
    }
}
