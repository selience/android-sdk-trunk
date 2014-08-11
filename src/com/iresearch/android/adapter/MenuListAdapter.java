package com.iresearch.android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import java.util.List;
import android.widget.TextView;
import com.android.sdk.base.BaseGroupAdapter;

public class MenuListAdapter extends BaseGroupAdapter<String> {

    public MenuListAdapter(Context context, List<String> dataList) {
        super(context, dataList);
    }

    @Override
    public View newView(LayoutInflater inflater, View v, int position) {
        return inflater.inflate(android.R.layout.simple_list_item_1, null);
    }

    @Override
    public void bindView(View v, String item) {
        ((TextView) v).setText(item);
    }
}
