package com.lejia.mobile.orderking.hk3d;

import android.content.Context;

import com.lejia.mobile.orderking.hk3d.datas_2d.Ground;
import com.lejia.mobile.orderking.hk3d.datas_2d.House;
import com.lejia.mobile.orderking.hk3d.datas_2d.RendererObject;

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
        selector = object;
        // 未选中任何对象
        if (selector == null) {
            for (RendererObject rendererObject : rendererObjectsList) {
                rendererObject.setSelected(false);
                if (rendererObject instanceof Ground) { // 地面
                    House house = ((Ground) rendererObject).getHouse();
                    house.setSelected(false);
                }
            }
        }
        // 有选中内容
        else {
            if (rendererObjectsList != null && rendererObjectsList.size() > 0) {
                for (RendererObject rendererObject : rendererObjectsList) {
                    if (selector.equals(rendererObject)) {
                        rendererObject.setSelected(true);
                        if (rendererObject instanceof Ground) {  // 地面
                            House house = ((Ground) rendererObject).getHouse();
                            house.setSelected(!house.isSelected());
                        }
                    } else {
                        rendererObject.setSelected(false);
                        if (rendererObject instanceof Ground) { // 地面
                            House house = ((Ground) rendererObject).getHouse();
                            house.setSelected(false);
                        }
                    }
                }
            }
        }
    }

    /**
     * 获取选中对象
     */
    public RendererObject getSelector() {
        return selector;
    }

    /**
     * 获取选中的地面
     */
    public Ground getSelectedGround() {
        if (selector == null || !(selector instanceof Ground))
            return null;
        return (Ground) selector;
    }

    /**
     * 清空数据
     */
    public void clear() {
        if (rendererObjectsList != null)
            rendererObjectsList.clear();
    }

}
