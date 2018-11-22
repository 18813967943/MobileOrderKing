package com.lejia.mobile.orderking.hk3d.activity_partitation;

import android.app.Activity;
import android.content.Context;
import android.view.MotionEvent;

import com.lejia.mobile.orderking.dialogs.AccurateInputDialog;
import com.lejia.mobile.orderking.hk3d.Designer3DRender;
import com.lejia.mobile.orderking.hk3d.RendererState;
import com.lejia.mobile.orderking.hk3d.classes.LJ3DPoint;
import com.lejia.mobile.orderking.hk3d.classes.Point;
import com.lejia.mobile.orderking.hk3d.datas_2d.HouseDatasManager;
import com.lejia.mobile.orderking.hk3d.datas_2d.LineSegHouse;
import com.lejia.mobile.orderking.hk3d.datas_2d.NormalHouse;
import com.lejia.mobile.orderking.hk3d.datas_2d.RectHouse;

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

    private boolean isLineSegDraw; // 是否线建墙操作

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
            // 多手指缩放操作弹起拦截
            if (scaleHandler) {
                scaleHandler = false;
                return true;
            }
            // 绘制方法类型
            int drawState = tilesManager.getDrawStatues();
            switch (drawState) {
                case TilesManager.DRAW_RECT:
                    // 矩形画房间
                    isLineSegDraw = false;
                    drawRectHouse(event);
                    break;
                case TilesManager.DRAW_NORMAL:
                    // 线段画房间
                    isLineSegDraw = false;
                    drawNormalHouse(event);
                    break;
                case TilesManager.DRAW_LINE_BUILD:
                    // 线建房间
                    isLineSegDraw = true;
                    drawNormalHouse(event);
                    break;
            }
        }
        // 多指操作
        else if (fingerCount > 1) {
            try {
                scaleHandler = true;
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
                                designer3DRender.setScale(false);
                            }
                            // 放大
                            else if (poor > 0) {
                                designer3DRender.setScale(true);
                            }
                            downDist = moveDist; // 重置操作
                        }
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /*******************************************************
     *  单手指平移、多手指缩放操作
     * ******************************************************/

    private double downDist; // 按下时的双指距离
    private double moveDist; // 移动后的实时距离
    private boolean scaleHandler; // 缩放操作

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

    private Point transDown; // 平移按下点
    private long transDownTime;
    private Point transMove; // 平移实时移动点
    private long transMoveTime;
    private boolean openedTranslate; // 是否打开平移操作
    private boolean isMoveMaxMinDist; // 已经有滑动操作
    private boolean needRemoveCurrentEditHouse;

    /**
     * 设置平移按下点(三维坐标)
     *
     * @param x
     * @param y
     */
    private void setTransDown(double x, double y) {
        if (transMove == null)
            transMove = new Point(x, y);
        else
            transMove.setXY(x, y);
        transDownTime = System.currentTimeMillis();
    }

    /**
     * 设置平移按下点(三维坐标)
     *
     * @param x
     * @param y
     */
    private void setTransMove(double x, double y) {
        if (transDown == null)
            transDown = new Point(x, y);
        else
            transDown.setXY(x, y);
        // 对比两点之间的距离、时长，开启平移开关
        double dist = transMove.dist(transDown);
        double minDist = 24;
        if (!openedTranslate) {
            transMoveTime = System.currentTimeMillis();
            if (dist < minDist) {
                if (transMoveTime - transDownTime >= 1000 && !isMoveMaxMinDist) {
                    needRemoveCurrentEditHouse = true;
                    openedTranslate = true;
                }
            } else {
                if (!isMoveMaxMinDist) {
                    isMoveMaxMinDist = true;
                }
            }
        }
        // 开启平移模式
        else {
            // 移除当前编辑的房间
            if (needRemoveCurrentEditHouse) {
                needRemoveCurrentEditHouse = false;
                int drawState = tilesManager.getDrawStatues();
                switch (drawState) {
                    case TilesManager.DRAW_RECT:
                        // 矩形画房间
                        houseDatasManager.remove(rectHouse);
                        break;
                    case TilesManager.DRAW_NORMAL:
                        // 线段画房间
                        houseDatasManager.remove(normalHouse);
                        break;
                    case TilesManager.DRAW_LINE_BUILD:
                        // 线建房间
                        houseDatasManager.remove(normalHouse);
                        break;
                }
            }
            // 进行平移操作
            if (dist > 24) {
                float transX = (float) (transMove.x - transDown.x);
                float transY = (float) (transMove.y - transDown.y);
                designer3DRender.setTransLate(transX, transY);
                transDown.setXY(transMove.x, transMove.y);
            }
        }
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
                setTransDown(touchDown.x, touchDown.y);
                rectHouse.setDown(new Point(touchDown.x, touchDown.y));
                houseDatasManager.add(rectHouse);
                break;
            case MotionEvent.ACTION_MOVE:
                if (rectDown != null) {
                    float mx = event.getX();
                    float my = event.getY();
                    LJ3DPoint touchMove = designer3DRender.touchPlanTo3D(mx, my, false);
                    setTransMove(touchMove.x, touchMove.y);
                    double dist = rectDown.dist(mx, my);
                    if (dist >= 24 && !openedTranslate) {
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
                setCheckUp(event.getX(), event.getY());
                if (!openedTranslate) {
                    // 精准模式
                    if (RendererState.isIsAccurate() && rectHouse != null && !rectHouse.isSizeInvalid()) {
                        // 弹出精准输入窗口
                        new AccurateInputDialog(mContext, rectHouse, AccurateInputDialog.RECT, new AccurateInputDialog.OnAccurateInputListener() {
                            @Override
                            public void onInputed(int xlong, int width, int height, int flag) {
                                rectHouse.accurateSet(width / 10, height / 10);
                                // 检测闭合房间切割组合
                                houseDatasManager.gpcClosedCheck(rectHouse);
                            }

                            @Override
                            public void cancel() {
                                // 检测闭合房间切割组合
                                houseDatasManager.gpcClosedCheck(rectHouse);
                            }
                        }).show();
                    } else {
                        // 检测闭合房间切割组合
                        houseDatasManager.gpcClosedCheck(rectHouse);
                    }
                }
                openedTranslate = false; // 关闭平移
                isMoveMaxMinDist = false;
                break;
        }
    }

    /*************************************************
     *  绘制线段房间
     * ***********************************************/
    private Point normalDown; // 按下点
    private Point normalUp; // 弹起点、实时移动点
    private NormalHouse normalHouse; // 当前绘制的线段墙体

    private void drawNormalHouse(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 获取触摸在的三维点、设置检测手势按下点
                normalDown = new Point(event.getX(), event.getY());
                setCheckDown(event.getX(), event.getY());
                LJ3DPoint touchDown = designer3DRender.touchPlanTo3D(event.getX(), event.getY(), true);
                setTransDown(touchDown.x, touchDown.y);
                // 每次绘制线段墙体时都新建
                if (isLineSegDraw) {
                    normalHouse = new LineSegHouse(mContext);
                } else {
                    normalHouse = new NormalHouse(mContext);
                }
                normalHouse.setDown(new Point(touchDown.x, touchDown.y));
                houseDatasManager.add(normalHouse);
                break;
            case MotionEvent.ACTION_MOVE:
                normalUp = new Point(event.getX(), event.getY());
                LJ3DPoint touchMove = designer3DRender.touchPlanTo3D(event.getX(), event.getY(), false);
                setTransMove(touchMove.x, touchMove.y);
                double dist = normalUp.dist(normalDown);
                if (dist >= 24 && !openedTranslate) {
                    // 与其他房间的端点对齐检测
                    Point alignPoint = houseDatasManager.checkUpAlign(touchMove.off(), normalHouse);
                    normalHouse.setUp(alignPoint);
                }
                break;
            case MotionEvent.ACTION_UP:
                setCheckUp(event.getX(), event.getY());
                if (!openedTranslate) {
                    boolean invalid = normalHouse.checkInvalid();
                    if (invalid)
                        houseDatasManager.remove(normalHouse);
                    else {
                        // 精准模式
                        if (RendererState.isIsAccurate()) {
                            new AccurateInputDialog(mContext, normalHouse, AccurateInputDialog.USUALLY, new AccurateInputDialog.OnAccurateInputListener() {
                                @Override
                                public void onInputed(int xlong, int width, int height, int flag) {
                                    normalHouse.accurateSet(xlong / 10);
                                    // 非闭合线段检测组合切割操作
                                    houseDatasManager.gpcUncloseCheck(normalHouse);
                                }

                                @Override
                                public void cancel() {
                                    // 非闭合线段检测组合切割操作
                                    houseDatasManager.gpcUncloseCheck(normalHouse);
                                }
                            }).show();
                        } else {
                            // 非闭合线段检测组合切割操作
                            houseDatasManager.gpcUncloseCheck(normalHouse);
                        }
                    }
                }
                openedTranslate = false; // 关闭平移
                isMoveMaxMinDist = false;
                break;
        }
    }

}
