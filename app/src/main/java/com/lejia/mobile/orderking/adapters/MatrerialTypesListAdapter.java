package com.lejia.mobile.orderking.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lejia.mobile.orderking.R;
import com.lejia.mobile.orderking.httpsResult.classes.LJNodes;

import java.util.ArrayList;

/**
 * Author by HEKE
 *
 * @time 2018/7/12 15:10
 * TODO: 铺砖材质列表资源标题适配器
 */
public class MatrerialTypesListAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<LJNodes> nodesList;

    public MatrerialTypesListAdapter(Context context, ArrayList<LJNodes> nodesList) {
        mContext = context;
        this.nodesList = nodesList;
    }

    /**
     * 修改数据
     *
     * @param nodesList
     */
    public void changeList(ArrayList<LJNodes> nodesList) {
        this.nodesList = nodesList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return nodesList == null ? 0 : nodesList.size();
    }

    @Override
    public Object getItem(int position) {
        return nodesList == null ? null : nodesList.get(position);
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
                v = View.inflate(mContext, R.layout.material_item, null);
                holder = new ViewHolder();
                holder.title = v.findViewById(R.id.materialItem);
                v.setTag(holder);
            } else {
                holder = (ViewHolder) v.getTag();
            }
            holder.title.setText(nodesList.get(position).getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return v;
    }

    private class ViewHolder {
        public TextView title;
    }

}
