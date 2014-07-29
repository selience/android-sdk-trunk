package com.iresearch.android.ui;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;

/**
 * A simple dialog showing a progress message.
 */
public class ProgressDialogFragment extends DialogFragment {
	private static final String PREFIX = ProgressDialogFragment.class.getName()
			+ ".";
	public static final String FRAGMENT_TAG = PREFIX + "FRAGMENT_TAG";
	private static final String FRAGMENT_STATE = PREFIX + "_state";

	private boolean isDialogVisible;

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
		if (savedInstanceState != null) {
			isDialogVisible = savedInstanceState.getBoolean(FRAGMENT_STATE);
		}

		ProgressDialog dialog = new ProgressDialog(getActivity());
		dialog.setMessage("Waiting...");
		return dialog;
	}

	public void showFragment(FragmentManager manager) {
		isDialogVisible = true;
		show(manager, FRAGMENT_TAG);
	}

	public void hideFragment() {
		isDialogVisible = false;
		dismissAllowingStateLoss();
	}

	public boolean isShowing() {
		return isDialogVisible;
	}

	public void setMessage(CharSequence message) {
		Dialog dialog = getDialog();
		if (dialog != null && dialog.isShowing()) {
			((ProgressDialog) dialog).setMessage(message);
		}
	}

	@Override
	public void onSaveInstanceState(Bundle bundle) {
		bundle.putBoolean(FRAGMENT_STATE, isDialogVisible);
		super.onSaveInstanceState(bundle);
	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		super.onDismiss(dialog);
		triggerActivityListener();
	}

	private void triggerActivityListener() {
		if (getActivity() instanceof OnDialogClosedListener) {
			OnDialogClosedListener listener = (OnDialogClosedListener) getActivity();
			listener.onDialogClosed();
		}
	}

	public interface OnDialogClosedListener {

		void onDialogClosed();
	}
}
