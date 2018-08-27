package com.lejia.mobile.orderking.hk3d.activity_partitation;

import android.content.Context;
import android.view.MotionEvent;

import com.lejia.mobile.orderking.hk3d.Designer3DRender;
import com.lejia.mobile.orderking.hk3d.TouchSelectedManager;
import com.lejia.mobile.orderking.hk3d.classes.LJ3DPoint;
import com.lejia.mobile.orderking.hk3d.classes.Point;
import com.lejia.mobile.orderking.hk3d.datas.RendererObject;
import com.lejia.mobile.orderking.hk3d.datas.cadwidgets.BaseCad;

/**
 * Author by HEKE
 *
 * @time 2018/8/25 17:05
 * TODO: 家具触摸管理对象
 */
public class FurnitureTouchManager {

    private Context mContext;
    private TilesManager tilesManager;
    private Designer3DManager designer3DManager;
    private Designer3DRender designer3DRender;

    public FurnitureTouchManager(Context context, TilesManager tilesManager, Designer3DManager designer3DManager) {
        this.mContext = context;
        this.tilesManager = tilesManager;
        this.designer3DManager = designer3DManager;
        this.designer3DRender = designer3DManager.getDesigner3DRender();
    }

    /**
     * 三维按下点
     */
    private Point down;

    // 拦截操作
    private boolean interruptTouch;

    /**
     * 触摸事件
     *
     * @param event
     */
    public boolean canDrawCheck(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                float dx = event.getX();
                float dy = event.getY();
                designer3DRender.checkClickAtViews(dx, dy);
                // 检测是否模型被触摸
                TouchSelectedManager touchSelectedManager = designer3DRender.getTouchSelectedManager();
                RendererObject selector = touchSelectedManager.getSelector();
                if (selector != null) {
                    interruptTouch = (selector instanceof BaseCad);
                    if (interruptTouch) {
                        LJ3DPoint td = designer3DRender.touchPlanTo3D(event.getX(), event.getY(), false);
                        
                    }
                } else {
                    interruptTouch = false;
                }
                break;
            case MotionEvent.ACTION_MOVE:

                break;
            case MotionEvent.ACTION_UP:

                break;
        }
        return interruptTouch;
    }

}
