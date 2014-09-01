package com.iresearch.android.adapter;

import java.util.List;
import android.view.View;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import com.android.sdk.adapter.BaseGroupAdapter;

public class MenuListAdapter extends BaseGroupAdapter<String, MenuListAdapter.ViewHolder> {

    public MenuListAdapter(Context context, List<String> dataList) {
        super(context, dataList);
    }

    @Override
    public View newView(LayoutInflater inflater, ViewGroup parent) {
        return inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
    }

    @Override
    public ViewHolder onCreateViewHolder(View convertView) {
        ViewHolder viewHolder = new ViewHolder();
        viewHolder.textView = (TextView)convertView.findViewById(android.R.id.text1);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        viewHolder.textView.setText(getItem(position));
    }
    
    static class ViewHolder {
        TextView textView;
    }

}
