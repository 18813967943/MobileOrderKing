package com.lejia.mobile.orderking.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lejia.mobile.orderking.R;
import com.lejia.mobile.orderking.hk3d.datas_2d.Furniture;

import java.util.ArrayList;

/**
 * Author by HEKE
 *
 * @time 2018/8/21 14:50
 * TODO: 家具预览适配器
 */
public class FurniturePreviewAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<Furniture> furnituresList;

    public FurniturePreviewAdapter(Context context, ArrayList<Furniture> furnituresList) {
        this.mContext = context;
        this.furnituresList = furnituresList;
    }

    /**
     * 刷新数据
     *
     * @param furnituresList
     */
    public void refreshDatas(ArrayList<Furniture> furnituresList) {
        this.furnituresList = furnituresList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return furnituresList == null ? 0 : furnituresList.size();
    }

    @Override
    public Object getItem(int position) {
        return furnituresList == null ? null : furnituresList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        ViewHolder holder = null;
        try {
            if (v == null) {
                v = View.inflate(mContext, R.layout.material_preview_item, null);
                holder = new ViewHolder();
                holder.preview = v.findViewById(R.id.materialPreview);
                holder.name = v.findViewById(R.id.materialName);
                v.setTag(holder);
            } else {
                holder = (ViewHolder) v.getTag();
            }
            Furniture furniture = furnituresList.get(position);
            holder.name.setText("" + furniture.materialCode);
            holder.preview.setBackgroundColor(0xFFFFFFFF);
            Glide.with(mContext).load(furniture.preview).into(holder.preview);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return v;
    }

    /**
     * Author by HEKE
     *
     * @time 2018/8/21 14:53
     * TODO: 缓存对象
     */
    private class ViewHolder {
        public ImageView preview;
        public TextView name;
    }

}
