package com.lejia.mobile.orderking.hk3d.datas;

import android.content.Context;

import com.lejia.mobile.orderking.hk3d.classes.PointList;

import java.util.ArrayList;

/**
 * Author by HEKE
 *
 * @time 2018/7/17 9:11
 * TODO: 基础房间定义
 */
public abstract class House {

    private Context mContext;

    /**
     * 墙中点列表
     */
    public PointList centerPointList;

    /**
     * 墙内点列表
     */
    public PointList innerPointList;

    /**
     * 墙外点列表
     */
    public PointList outerPointList;

    /**
     * 渲染墙体对象
     */
    public ArrayList<Wall> wallsList;

    /**
     * 渲染地面对象
     */
    public Ground ground;

    public House(Context context) {
        this.mContext = context;
        this.wallsList = new ArrayList<>();
    }

    public Context getContext() {
        return mContext;
    }

}
