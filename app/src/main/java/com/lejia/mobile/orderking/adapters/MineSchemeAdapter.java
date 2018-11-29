package com.lejia.mobile.orderking.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lejia.mobile.orderking.R;
import com.lejia.mobile.orderking.classes.ResUrlNodeXml;
import com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.bridge.MineSchemeLoadBuffer;

import java.util.ArrayList;

/**
 * Author by HEKE
 *
 * @time 2017/11/6 16:23
 * TODO: 个人保存平面设计方案适配器对象
 */
public class MineSchemeAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<ResUrlNodeXml.ResPath> schemesList;

    public MineSchemeAdapter(Context context, ArrayList<ResUrlNodeXml.ResPath> schemesList) {
        this.mContext = context;
        this.schemesList = schemesList;
    }

    /**
     * 新增数据
     *
     * @param schemesList
     */
    public void add(ArrayList<ResUrlNodeXml.ResPath> schemesList) {
        if (schemesList == null || schemesList.size() == 0)
            return;
        this.schemesList.addAll(schemesList);
        notifyDataSetChanged();
    }

    /**
     * 刷新我的方案列表
     */
    public void refreshSchemes(ArrayList<ResUrlNodeXml.ResPath> schemesList) {
        this.schemesList = schemesList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return schemesList == null ? 0 : schemesList.size();
    }

    @Override
    public Object getItem(int position) {
        return schemesList == null ? null : schemesList.get(position);
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
                holder = new ViewHolder();
                v = View.inflate(mContext, R.layout.scheme_item, null);
                holder.preview = v.findViewById(R.id.schemePreview);
                holder.name = v.findViewById(R.id.schemeName);
                v.setTag(holder);
            } else {
                holder = (ViewHolder) v.getTag();
            }
            // 加载内容
            ResUrlNodeXml.ResPath resPath = schemesList.get(position);
            holder.name.setText("" + resPath.name);
            MineSchemeLoadBuffer loadBufferTask = new MineSchemeLoadBuffer(mContext, holder.preview, resPath);
            loadBufferTask.fetch();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return v;
    }

    private class ViewHolder {
        public ImageView preview;
        public TextView name;
    }

}
