package com.lejia.mobile.orderking.hk3d.activity_partitation;

import android.content.Context;
import android.view.MotionEvent;

import com.lejia.mobile.orderking.bases.OrderKingApplication;
import com.lejia.mobile.orderking.dialogs.FurnitureEditorDialog;
import com.lejia.mobile.orderking.hk3d.Designer3DRender;
import com.lejia.mobile.orderking.hk3d.TouchSelectedManager;
import com.lejia.mobile.orderking.hk3d.classes.LJ3DPoint;
import com.lejia.mobile.orderking.hk3d.classes.Point;
import com.lejia.mobile.orderking.hk3d.datas_2d.HouseDatasManager;
import com.lejia.mobile.orderking.hk3d.datas_2d.RendererObject;
import com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.models.InterObserver;
import com.lejia.mobile.orderking.hk3d.datas_2d.cadwidgets.BaseCad;
import com.lejia.mobile.orderking.hk3d.datas_2d.cadwidgets.FurTypes;

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
    private HouseDatasManager houseDatasManager;
    private TouchSelectedManager touchSelectedManager;
    private InterObserver interObserver;

    private BaseCad selectFur;

    public FurnitureTouchManager(Context context, TilesManager tilesManager, Designer3DManager designer3DManager) {
        this.mContext = context;
        this.tilesManager = tilesManager;
        this.designer3DManager = designer3DManager;
        this.designer3DRender = designer3DManager.getDesigner3DRender();
        this.houseDatasManager = designer3DRender.getHouseDatasManager();
        this.touchSelectedManager = designer3DRender.getTouchSelectedManager();
        this.interObserver = ((OrderKingApplication) context.getApplicationContext()).getDesigner3DSurfaceView().getInterObserver();
    }

    /**
     * 三维按下点
     */
    private Point down;
    private Point d3Down;

    // 拦截操作
    private boolean interruptTouch;

    /**
     * 短按、长按区分
     */
    private long downTime;
    private long upTime;
    private Point checkUp;
    private boolean hadMove;

    /**
     * 模型编辑窗口
     */
    private FurnitureEditorDialog furnitureEditorDialog;

    private void downCheck() {
        downTime = System.currentTimeMillis();
    }

    private void upCheck() {
        upTime = System.currentTimeMillis();
        long poor = upTime - downTime;
        if (!hadMove) {
            if (poor <= 350) {
                // 短按，回调编辑窗口
                if (furnitureEditorDialog == null)
                    furnitureEditorDialog = new FurnitureEditorDialog(mContext, houseDatasManager, tilesManager);
                furnitureEditorDialog.show(checkUp, selectFur);
            } else {
                // 长按
            }
        }
        hadMove = false;
    }

    /**
     * 触摸事件
     *
     * @param event
     */
    public boolean canDrawCheck(MotionEvent event) {
        int action = event.getAction();
        // 检测第一次按下操作，若选中家具，则在此方法内执行完触摸操作。相反，需要将事件回传至OnTouchEvent中处理事件。
        if (action == MotionEvent.ACTION_DOWN) {
            float dx = event.getX();
            float dy = event.getY();
            designer3DRender.checkClickAtViews(dx, dy);
            downCheck();
            // 检测是否模型被触摸
            RendererObject selector = touchSelectedManager.getSelector();
            if (selector != null) {
                interruptTouch = (selector instanceof BaseCad);
                if (interruptTouch) {
                    selectFur = (BaseCad) selector;
                    down = new Point(event.getX(), event.getY());
                    LJ3DPoint td = designer3DRender.touchPlanTo3D(event.getX(), event.getY(), false);
                    d3Down = new Point(td.x, td.y);
                }
            } else {
                interruptTouch = false;
            }
        }
        if (interruptTouch && selectFur != null) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_MOVE:
                    Point mp = new Point(event.getX(), event.getY());
                    double dist = mp.dist(down);
                    if (dist >= 24) {
                        hadMove = true;
                        LJ3DPoint tm = designer3DRender.touchPlanTo3D(event.getX(), event.getY(), false);
                        double poorX = tm.x - d3Down.x;
                        double poorY = tm.y - d3Down.y;
                        if (selectFur != null) {
                            // 设置偏置
                            selectFur.translate(poorX, poorY);
                            // 根据类型做出吸附处理
                            HouseDatasManager.DragAdsorbRet dragAdsorbRet = houseDatasManager.checkModelAdsorb(selectFur);
                            if (dragAdsorbRet != null) {
                                selectFur.setDragResult(dragAdsorbRet);
                            }
                        }
                        down.setXY(mp.x, mp.y);
                        d3Down.setXY(tm.x, tm.y);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    checkUp = new Point(event.getX(), event.getY());
                    if (selectFur != null) {
                        selectFur.setSelected(false);
                        // 刷新选中对象的矩阵对象信息
                        if (hadMove) {
                            interObserver.notification();
                        }
                    }
                    // 门窗检测拆立面墙
                    if (selectFur != null) {
                        if (selectFur.furTypes.ordinal() < FurTypes.GENERAL_L3D.ordinal()) {
                        }
                    }
                    upCheck();
                    break;
            }
        }
        return interruptTouch;
    }

    /**
     * Author by HEKE
     *
     * @time 2018/8/28 17:04
     * TODO: 家具编辑回调接口
     */
    public interface OnFurnitureEditorListener {
        void onShortClickEdit(BaseCad selector);
    }

}