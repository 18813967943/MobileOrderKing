package com.lejia.mobile.orderking.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lejia.mobile.orderking.R;
import com.lejia.mobile.orderking.hk3d.classes.Tile;

import java.util.ArrayList;

/**
 * Author by HEKE
 *
 * @time 2018/7/12 17:54
 * TODO: 材质预览适配器
 */
public class TilesPreviewAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<Tile> tilesList;

    public TilesPreviewAdapter(Context context, ArrayList<Tile> tilesList) {
        mContext = context;
        this.tilesList = tilesList;
    }

    @Override
    public int getCount() {
        return tilesList == null ? 0 : tilesList.size();
    }

    @Override
    public Object getItem(int position) {
        return tilesList == null ? null : tilesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        ViewHolder holder = null;
        if (v == null) {
            v = View.inflate(mContext, R.layout.material_preview_item, null);
            holder = new ViewHolder();
            holder.preview = v.findViewById(R.id.materialPreview);
            holder.name = v.findViewById(R.id.materialName);
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }
        Tile tile = tilesList.get(position);
        holder.name.setText(tile.getMaterialName());
        Glide.with(mContext).load(tile.getImageURL()).into(holder.preview);
        return v;
    }

    private class ViewHolder {
        public ImageView preview;
        public TextView name;
    }

}
