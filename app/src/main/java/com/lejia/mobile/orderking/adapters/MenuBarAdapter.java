package com.lejia.mobile.orderking.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lejia.mobile.orderking.R;


/**
 * @author HEKE 菜单适配器
 */
public class MenuBarAdapter extends BaseAdapter {

    private Context mContext;
    private String[] items;

    public MenuBarAdapter(Context context, String[] items) {
        this.mContext = context;
        this.items = items;
    }

    public void refreshTitles(String[] items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (items == null) {
            return 0;
        }
        return items.length;
    }

    @Override
    public Object getItem(int position) {
        if (items == null) {
            return null;
        }
        return items[position];
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
                v = View.inflate(mContext, R.layout.menu_item, null);
                holder.title = (TextView) v.findViewById(R.id.menuItem);
                v.setTag(holder);
            } else {
                holder = (ViewHolder) v.getTag();
            }
            holder.title.setText(items[position]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return v;
    }

    private class ViewHolder {
        public TextView title;
    }
}
