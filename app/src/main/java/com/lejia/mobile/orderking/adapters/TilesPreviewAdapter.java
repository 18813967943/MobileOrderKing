package com.lejia.mobile.orderking.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lejia.mobile.orderking.R;
import com.lejia.mobile.orderking.hk3d.classes.TileDescription;
import com.lejia.mobile.orderking.utils.TextUtils;

import java.util.ArrayList;

/**
 * Author by HEKE
 *
 * @time 2018/7/12 17:54
 * TODO: 材质预览适配器
 */
public class TilesPreviewAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<TileDescription> tilesList;

    public TilesPreviewAdapter(Context context, ArrayList<TileDescription> tilesList) {
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

    /**
     * 获取预览图名称
     */
    private String getMaterialName(String url) {
        if (TextUtils.isTextEmpty(url))
            return null;
        String[] vs = url.split("[//]");
        return vs[vs.length - 1].split("[.]")[0];
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
        TileDescription tile = tilesList.get(position);
        String imageUrl = tile.getPreviewImg();
        String name = getMaterialName(imageUrl);
        holder.name.setText(name);
        Glide.with(mContext).load(imageUrl).into(holder.preview);
        return v;
    }

    private class ViewHolder {
        public ImageView preview;
        public TextView name;
    }

}
