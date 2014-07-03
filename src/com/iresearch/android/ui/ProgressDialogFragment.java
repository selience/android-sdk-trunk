
package com.iresearch.android.ui;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;

/**
 * A simple dialog showing a progress message.
 */
public class ProgressDialogFragment extends DialogFragment {
    private static final String PREFIX = ProgressDialogFragment.class.getName() + ".";
    public static final String FRAGMENT_TAG = PREFIX + "FRAGMENT_TAG";
    
    public static ProgressDialogFragment newInstance() {
        return new ProgressDialogFragment();
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Waiting...");
        return dialog;
    }

    public void showFragment(FragmentManager manager) {
        show(manager, FRAGMENT_TAG);
    }
    
    public void hideFragment() {
        dismissAllowingStateLoss();
    }
    
    public void setMessage(CharSequence message) {
        Dialog dialog = getDialog();
        if (dialog!=null && dialog.isShowing()) {
            ((ProgressDialog)dialog).setMessage(message);
        }
    }
}
