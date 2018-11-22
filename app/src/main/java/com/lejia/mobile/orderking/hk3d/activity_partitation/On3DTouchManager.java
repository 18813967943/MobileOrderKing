package com.lejia.mobile.orderking.hk3d.activity_partitation;

import android.content.Context;
import android.view.MotionEvent;

import com.lejia.mobile.orderking.hk3d.RendererState;
import com.lejia.mobile.orderking.hk3d.TouchSelectedManager;
import com.lejia.mobile.orderking.hk3d.classes.Point;

/**
 * Author by HEKE
 *
 * @time 2018/9/12 10:18
 * TODO: 三维触摸管理
 */
public class On3DTouchManager {

    private Context mContext;
    private TilesManager tilesManager;
    private Designer3DManager designer3DManager;
    private TouchSelectedManager touchSelectedManager;

    public On3DTouchManager(Context context, TilesManager tilesManager, Designer3DManager designer3DManager) {
        this.mContext = context;
        this.tilesManager = tilesManager;
        this.designer3DManager = designer3DManager;
        this.touchSelectedManager = this.designer3DManager.getDesigner3DRender().getTouchSelectedManager();
    }

    // 按下点
    private Point down;
    // 移动点
    private Point move;
    // 弹起点
    private Point up;
    // 多指移动标志
    private boolean muchFCT;
    // 单手指点击标志
    private boolean singleFCT;

    /*******************************************************
     *  多手指缩放操作
     * ******************************************************/

    private double downDist; // 按下时的双指距离
    private double moveDist; // 移动后的实时距离

    /**
     * 获取触摸两点之间的距离
     *
     * @param event
     * @return
     */
    private double getTwoFinggerDistance(MotionEvent event) {
        int id1 = event.getPointerId(0);
        int id2 = event.getPointerId(1);
        float p1x = event.getX(id1);
        float p1y = event.getY(id1);
        float p2x = event.getX(id2);
        float p2y = event.getY(id2);
        return new Point(p1x, p1y).dist(p2x, p2y);
    }

    /**
     * 触摸管理
     *
     * @param event
     */
    public void touch(MotionEvent event) {
        try {
            int fc = event.getPointerCount();
            if (fc == 1) {
                if (muchFCT) {
                    muchFCT = false;
                    return;
                }
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        singleFCT = true;
                        if (down == null) {
                            down = new Point(event.getX(), event.getY());
                        } else {
                            down.setXY(event.getX(), event.getY());
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (move == null) {
                            move = new Point(event.getX(), event.getY());
                        } else {
                            move.setXY(event.getX(), event.getY());
                        }
                        double dist = move.dist(down);
                        if (dist > 24 && singleFCT) {
                            float x = (float) (move.x - down.x);
                            float y = (float) (move.y - down.y);
                            // 轴侧
                            boolean axis = !RendererState.isNot25D();
                            if (axis) {
                                if (Math.abs(x) > Math.abs(y)) {
                                } else {
                                }
                            }
                            // 进入房间
                            boolean gotoHouse = !RendererState.isNot3D();
                            if (gotoHouse) {
                                if (Math.abs(x) > Math.abs(y)) {
                                }
                            }
                            down.setXY(move.x, move.y);
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        singleFCT = false;
                        break;
                }
            } else if (fc > 1) {
                muchFCT = true;
                switch (event.getAction() & event.getActionMasked()) {
                    case MotionEvent.ACTION_POINTER_DOWN:
                        downDist = getTwoFinggerDistance(event);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        moveDist = getTwoFinggerDistance(event);
                        // 有效变化距离
                        if (Math.abs(moveDist - downDist) > 24) {
                            double poor = moveDist - downDist;
                            // 缩小
                            if (poor < 0) {
                            }
                            // 放大
                            else if (poor > 0) {
                            }
                            downDist = moveDist; // 重置操作
                        }
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
