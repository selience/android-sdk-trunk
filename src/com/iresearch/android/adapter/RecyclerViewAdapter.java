package com.iresearch.android.adapter;

import java.util.List;
import android.graphics.Bitmap;
import com.android.volley.core.RequestManager;
import com.android.volley.image.NetworkImageView;
import com.iresearch.android.R;
import com.iresearch.android.model.ViewModel;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.extensions.OnRecyclerViewItemClickListener;
import android.support.v7.extensions.PaletteManager;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> implements View.OnClickListener {

    private List<ViewModel> items;
    private PaletteManager paletteManager;
    private OnRecyclerViewItemClickListener<ViewModel> itemClickListener;

    public RecyclerViewAdapter(List<ViewModel> items) {
        this.items = items;
        this.paletteManager = new PaletteManager();
    }

    @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item, parent, false);
        v.setOnClickListener(this);
        return new ViewHolder(v);
    }

    @Override public void onBindViewHolder(final ViewHolder holder, int position) {
        final ViewModel item = items.get(position);
        holder.itemView.setTag(item);
        holder.text.setText(item.getText());

        ((NetworkImageView)holder.image).setImageUrl(item.getImage(), RequestManager.loader().useDefaultLoader().obtain());
    }

    @Override public int getItemCount() {
        return items.size();
    }

    @Override public void onClick(View view) {
        if (itemClickListener != null) {
            ViewModel model = (ViewModel) view.getTag();
            itemClickListener.onItemClick(view, model);
        }
    }

    public void add(ViewModel item, int position) {
        items.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(ViewModel item) {
        int position = items.indexOf(item);
        items.remove(position);
        notifyItemRemoved(position);
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener<ViewModel> listener) {
        this.itemClickListener = listener;
    }

    private static int setColorAlpha(int color, int alpha) {
        return (alpha << 24) | (color & 0x00ffffff);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView text;
        public ImageView image;
        
        public ViewHolder(View itemView) {
            super(itemView);
            text = (TextView) itemView.findViewById(R.id.text);
            image = (ImageView) itemView.findViewById(R.id.image);
        }

        public void updatePalette(PaletteManager paletteManager) {
            String key = ((ViewModel)itemView.getTag()).getImage();
            Bitmap bitmap = ((BitmapDrawable)image.getDrawable()).getBitmap();
            paletteManager.getPalette(key, bitmap, new PaletteManager.Callback() {
                @Override
                public void onPaletteReady(Palette palette) {
                    int bgColor = palette.getDarkVibrantColor().getRgb();
                    text.setBackgroundColor(setColorAlpha(bgColor, 192));
                    if (palette.getLightMutedColor()!=null) {
                    	text.setTextColor(palette.getLightMutedColor().getRgb());
                    }
                }
            });
        }
    }
}