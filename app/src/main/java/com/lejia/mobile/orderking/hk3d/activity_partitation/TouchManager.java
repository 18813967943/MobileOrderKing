package com.lejia.mobile.orderking.hk3d.activity_partitation;

import android.app.Activity;
import android.content.Context;
import android.view.MotionEvent;

import com.lejia.mobile.orderking.hk3d.Designer3DRender;
import com.lejia.mobile.orderking.hk3d.classes.LJ3DPoint;
import com.lejia.mobile.orderking.hk3d.classes.Point;
import com.lejia.mobile.orderking.hk3d.datas.HouseDatasManager;
import com.lejia.mobile.orderking.hk3d.datas.RectHouse;

/**
 * Author by HEKE
 *
 * @time 2018/7/16 14:53
 * TODO: 触摸管理对象
 */
public class TouchManager {

    private Context mContext;
    private TilesManager tilesManager;
    private Designer3DManager designer3DManager;
    private HouseDatasManager houseDatasManager; // 总渲染数据管理对象
    private Designer3DRender designer3DRender; // 渲染对象

    public TouchManager(Context context, TilesManager tilesManager, Designer3DManager designer3DManager) {
        this.mContext = context;
        if (!(mContext instanceof Activity)) {
            throw new ClassCastException("Msg : context must be Activity !");
        }
        this.tilesManager = tilesManager;
        this.designer3DManager = designer3DManager;
        this.designer3DRender = designer3DManager.getDesigner3DRender();
        this.houseDatasManager = this.designer3DRender.getHouseDatasManager();
    }

    /**
     * 触摸事件
     *
     * @param event
     */
    public boolean onTouchEvent(MotionEvent event) {
        int fingerCount = event.getPointerCount();
        // 单指操作
        if (fingerCount == 1) {
            int drawState = tilesManager.getDrawState();
            switch (drawState) {
                case TilesManager.DRAW_RECT:
                    // 矩形画房间
                    drawRectHouse(event);
                    break;
                case TilesManager.DRAW_NORMAL:
                    break;
                case TilesManager.DRAW_LINE_BUILD:
                    break;
            }
        }
        // 多指操作
        else if (fingerCount > 1) {

        }
        return false;
    }

    /*************************************************
     *  绘制矩形房间
     * ***********************************************/

    private Point rectDown; // 按下点
    private RectHouse rectDHouse; // 矩形房间

    private void drawRectHouse(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                rectDHouse = new RectHouse(mContext);
                rectDown = new Point(event.getX(), event.getY());
                LJ3DPoint touchDown = designer3DRender.touchPlanTo3D(event.getX(), event.getY());
                rectDHouse.setDown(new Point(touchDown.x, touchDown.y));
                houseDatasManager.checkThenAdd(rectDHouse);
                break;
            case MotionEvent.ACTION_MOVE:
                float mx = event.getX();
                float my = event.getY();
                double dist = rectDown.dist(mx, my);
                if (dist >= 24) {
                    LJ3DPoint touchMove = designer3DRender.touchPlanTo3D(mx, my);
                    rectDHouse.setUp(touchMove.x, touchMove.y);
                    rectDown.x = mx;
                    rectDown.y = my;
                }
                break;
            case MotionEvent.ACTION_UP:

                break;
        }
    }

}
