package com.lejia.mobile.orderking.hk3d;

import android.content.Context;

import com.lejia.mobile.orderking.hk3d.datas.Ground;
import com.lejia.mobile.orderking.hk3d.datas.House;
import com.lejia.mobile.orderking.hk3d.datas.RendererObject;

import java.util.ArrayList;

/**
 * Author by HEKE
 *
 * @time 2018/7/24 13:15
 * TODO: 触摸选中管理对象
 */
public class TouchSelectedManager {

    private Context mContext;
    private ArrayList<RendererObject> rendererObjectsList;
    private RendererObject selector;

    public TouchSelectedManager(Context context, ArrayList<RendererObject> rendererObjectsList) {
        this.mContext = context;
        this.rendererObjectsList = rendererObjectsList;
    }

    /**
     * 绑定所有渲染数据对象
     *
     * @param rendererObjectsList
     */
    public void setRendererObjectsList(ArrayList<RendererObject> rendererObjectsList) {
        this.rendererObjectsList = rendererObjectsList;
    }

    /**
     * 设置选中对象
     *
     * @param object
     */
    public void setSelector(RendererObject object) {
        if (object == null)
            return;
        selector = object;
        if (object instanceof Ground) {
            // 设置点中的房间为选中状态
            Ground ground = (Ground) object;
            House belong = ground.getHouse();
            boolean flag = !belong.isSelected();
            belong.setSelected(flag);
            // 去除其他地面选中
            for (RendererObject object1 : rendererObjectsList) {
                if (!object.equals(object1)) {
                    if (object1 instanceof Ground) {
                        Ground ground1 = (Ground) object1;
                        House belong1 = ground1.getHouse();
                        belong1.setSelected(false);
                    }
                }
            }
            ground.refreshRender();
            System.out.println("#### RendererObject : " + object.getClass().getSimpleName());
        }
    }

    /**
     * 获取选中对象
     */
    public RendererObject getSelector() {
        return selector;
    }
    
}
