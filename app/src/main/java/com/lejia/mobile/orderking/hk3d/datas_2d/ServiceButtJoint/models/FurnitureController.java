package com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.models;

import android.content.Context;
import android.graphics.Bitmap;

import com.lejia.mobile.orderking.classes.ResUrlNodeXml;
import com.lejia.mobile.orderking.classes.XInfo;
import com.lejia.mobile.orderking.hk3d.datas_2d.HouseDatasManager;
import com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.DefaultTile;
import com.lejia.mobile.orderking.hk3d.datas_2d.cadwidgets.BaseCad;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Author by HEKE
 *
 * @time 2018/12/1 14:49
 * TODO: 家具管理对象
 */
public class FurnitureController {

    private Context mContext;
    private HouseDatasManager houseDatasManager;

    /**
     * 模型顶视数据集集合
     */
    private HashMap<String, TopView> topViewHashMap = new HashMap<>();

    /**
     * 家具平面对象列表
     */
    private ArrayList<BaseCad> baseCadsList;

    public FurnitureController(Context context, HouseDatasManager houseDatasManager) {
        this.mContext = context;
        this.houseDatasManager = houseDatasManager;
        this.baseCadsList = new ArrayList<>();
    }

    /**
     * 添加家具
     *
     * @param type    -1为室内室外家具;0为门;1为窗
     * @param resPath 家具路径对象
     */
    public void add(final int type, final ResUrlNodeXml.ResPath resPath) {
        // 判断是否存在视图数据
        TopView topView = topViewHashMap.get(resPath.name);
        // 没有数据缓存，加载
        if (topView == null) {
            // 拉去顶视图等模型数据信息(铺砖资源通用)
            new DefaultTile(mContext, resPath.name, new DefaultTile.OnDefaultTilesListener() {
                @Override
                public void compelet(XInfo xInfo, Bitmap bitmap) {
                    TopView tv = new TopView(type, resPath, xInfo);
                    topViewHashMap.put(xInfo.materialCode, tv);
                    create(tv);
                }
            });
        }
        // 有同一模型数据
        else {
            create(topView);
        }
    }

    /**
     * 创建家具
     *
     * @param topView
     */
    private void create(TopView topView) {
        switch (topView.type) {
            case -1:
                // 常态家具
                
                break;
            case 0:
                // 门

                break;
            case 1:
                // 窗

                break;
        }
    }

}
