package android.support.v7.extensions;

import android.view.View;

public interface OnRecyclerViewItemClickListener<Model> {

	void onItemClick(View view, Model model);
}