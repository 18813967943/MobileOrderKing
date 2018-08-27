package com.lejia.mobile.orderking.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.lejia.mobile.orderking.R;

/**
 * Author by HEKE
 *
 * @time 2018/7/20 15:58
 * TODO: 铺砖、布置菜单栏右边图标展示适配器
 */
public class TilesRightIconsAdapter extends BaseAdapter {

    private Context mContext;
    private int[] normalIcons; // 常态下正常显示图标
    private int[] selectedIcons; // 点击显示图标

    private int selectePosition = -1; // 选中位置

    public TilesRightIconsAdapter(Context context, int[] normalIcons, int[] selectedIcons) {
        mContext = context;
        this.normalIcons = normalIcons;
        this.selectedIcons = selectedIcons;
    }

    public void refreshDatas(int[] normalIcons, int[] selectedIcons) {
        this.normalIcons = normalIcons;
        this.selectedIcons = selectedIcons;
        notifyDataSetChanged();
    }

    public void setSelectePosition(int selectePosition) {
        this.selectePosition = selectePosition;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return normalIcons == null ? 0 : normalIcons.length;
    }

    @Override
    public Object getItem(int position) {
        return normalIcons == null ? null : normalIcons[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        ViewHoler holder = null;
        if (v == null) {
            v = View.inflate(mContext, R.layout.res_right_icon_item, null);
            holder = new ViewHoler();
            holder.resIcon = v.findViewById(R.id.resIcon);
            v.setTag(holder);
        } else {
            holder = (ViewHoler) v.getTag();
        }
        if (selectePosition == position) {
            holder.resIcon.setBackgroundResource(selectedIcons[position]);
        } else {
            holder.resIcon.setBackgroundResource(normalIcons[position]);
        }
        return v;
    }

    private class ViewHoler {
        public ImageView resIcon;
    }

}
