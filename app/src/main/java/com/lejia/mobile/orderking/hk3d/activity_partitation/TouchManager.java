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
                    // 线段画房间
                    drawNormalHouse(event);
                    break;
                case TilesManager.DRAW_LINE_BUILD:
                    // 线建房间

                    break;
            }
        }
        // 多指操作
        else if (fingerCount > 1) {

        }
        return false;
    }

    /*******************************************************
     * 短按及长按操作
     * ****************************************************/
    private Point checkDown;
    private long checkDownTime;
    private Point checkUp;
    private long checkUpTime;

    /**
     * 设置当前按下点坐标及时间
     *
     * @param x
     * @param y
     */
    private void setCheckDown(float x, float y) {
        if (checkDown == null)
            checkDown = new Point(x, y);
        else {
            checkDown.x = x;
            checkDown.y = y;
        }
        checkDownTime = System.currentTimeMillis();
    }

    /**
     * 设置弹起点时，进行检测
     *
     * @param x
     * @param y
     */
    private void setCheckUp(float x, float y) {
        if (checkUp == null)
            checkUp = new Point(x, y);
        else {
            checkUp.x = x;
            checkUp.y = y;
        }
        checkUpTime = System.currentTimeMillis();
        // 根据距离及时间差区分长按及短按
        double dist = checkUp.dist(checkDown);
        if (dist <= 16) { // 避免手指未移动下屏幕自动跳点之间的差距
            long time = checkUpTime - checkDownTime;
            // 短按
            if (time <= 350) {
                System.out.println("###### 短按 !");
                designer3DRender.checkClickAtViews(x, y);
            }
            // 长按
            else {
                System.out.println("###### 长按 !");

            }
        }
    }

    /*************************************************
     *  绘制矩形房间
     * ***********************************************/

    private Point rectDown; // 按下点
    private RectHouse rectHouse; // 矩形房间

    private void drawRectHouse(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                rectHouse = new RectHouse(mContext);
                rectDown = new Point(event.getX(), event.getY());
                setCheckDown(event.getX(), event.getY());
                LJ3DPoint touchDown = designer3DRender.touchPlanTo3D(event.getX(), event.getY(), true);
                rectHouse.setDown(new Point(touchDown.x, touchDown.y));
                houseDatasManager.add(rectHouse);
                break;
            case MotionEvent.ACTION_MOVE:
                if (rectDown != null) {
                    float mx = event.getX();
                    float my = event.getY();
                    double dist = rectDown.dist(mx, my);
                    if (dist >= 24) {
                        LJ3DPoint touchMove = designer3DRender.touchPlanTo3D(mx, my, false);
                        // 与其他房间的端点对齐检测
                        Point alignPoint = houseDatasManager.checkUpAlign(touchMove.off(), rectHouse);
                        // 设置起点
                        rectHouse.setUp(alignPoint.x, alignPoint.y);
                        rectDown.x = mx;
                        rectDown.y = my;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                houseDatasManager.gpcClosedCheck(rectHouse);
                setCheckUp(event.getX(), event.getY());
                break;
        }
    }

    /*************************************************
     *  绘制线段房间
     * ***********************************************/


    private void drawNormalHouse(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                break;
            case MotionEvent.ACTION_MOVE:

                break;
            case MotionEvent.ACTION_UP:

                break;
        }
    }

}
