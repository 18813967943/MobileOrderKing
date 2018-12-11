package com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.models;

import android.content.Context;
import android.graphics.Bitmap;

import com.lejia.mobile.orderking.bases.OrderKingApplication;
import com.lejia.mobile.orderking.classes.ResUrlNodeXml;
import com.lejia.mobile.orderking.classes.XInfo;
import com.lejia.mobile.orderking.hk3d.Designer3DRender;
import com.lejia.mobile.orderking.hk3d.classes.Line;
import com.lejia.mobile.orderking.hk3d.classes.Point;
import com.lejia.mobile.orderking.hk3d.classes.PointList;
import com.lejia.mobile.orderking.hk3d.datas_2d.Ground;
import com.lejia.mobile.orderking.hk3d.datas_2d.House;
import com.lejia.mobile.orderking.hk3d.datas_2d.HouseDatasManager;
import com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.DefaultTile;
import com.lejia.mobile.orderking.hk3d.datas_2d.cadwidgets.BaseCad;
import com.lejia.mobile.orderking.hk3d.datas_2d.cadwidgets.FurTypes;
import com.lejia.mobile.orderking.hk3d.datas_2d.cadwidgets.GeneralFurniture;
import com.lejia.mobile.orderking.hk3d.datas_2d.cadwidgets.SimpleWindow;
import com.lejia.mobile.orderking.hk3d.datas_2d.cadwidgets.SingleDoor;
import com.lejia.mobile.orderking.hk3d.datas_2d.cadwidgets.SlideDoor;

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
    private Designer3DRender designer3DRender;
    private HouseDatasManager houseDatasManager;
    private InterObserver interObserver;

    /**
     * 模型顶视数据集集合
     */
    private HashMap<String, TopView> topViewHashMap = new HashMap<>();

    /**
     * 家具平面对象列表
     */
    private ArrayList<BaseCad> baseCadsList;

    public FurnitureController(Context context, HouseDatasManager houseDatasManager, Designer3DRender designer3DRender) {
        this.mContext = context;
        this.houseDatasManager = houseDatasManager;
        this.designer3DRender = designer3DRender;
        this.interObserver = ((OrderKingApplication) context.getApplicationContext()).getDesigner3DSurfaceView().getInterObserver();
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
     * 获取初始吸附信息对象
     *
     * @param inwall 是否在墙内
     */
    private ADI getADI(boolean inwall) {
        ADI adi = new ADI();
        try {
            ArrayList<House> houseList = houseDatasManager.getHousesList();
            if (inwall) {
                ArrayList<Line> lineArrayList = new ArrayList<>();
                Ground ground = designer3DRender.getTouchSelectedManager().getSelectedGround();
                if (ground != null) {
                    lineArrayList.addAll(ground.getHouse().centerPointList.toLineList());
                } else {
                    for (House house : houseList) {
                        lineArrayList.addAll(house.centerPointList.toLineList());
                    }
                }
                int randIndex = (int) (Math.random() * lineArrayList.size());
                Line line = lineArrayList.get(randIndex);
                adi.point = line.getCenter();
                adi.angle = (float) line.getAngle();
                adi.thickness = (float) line.getThickess();
            } else {
                Ground ground = designer3DRender.getTouchSelectedManager().getSelectedGround();
                if (ground != null) {
                    House house = ground.getHouse();
                    adi.point = house.centerPointList.getInnerValidPoint(false);
                    adi.angle = 180;
                } else {
                    House house = null;
                    for (House h : houseList) {
                        if (h.isWallClosed) {
                            house = h;
                            break;
                        }
                    }
                    if (house != null)
                        adi.point = house.centerPointList.getInnerValidPoint(false);
                    else {
                        adi.point = new Point(0, 0);
                    }
                    adi.angle = 180;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return adi;
    }

    /**
     * 创建家具
     *
     * @param topView
     */
    private void create(TopView topView) {
        if (topView == null)
            return;
        // 获取吸附点、角度
        ADI adi = getADI(topView.type != -1);
        topView.adi = adi;
        // 创建家具
        BaseCad baseCad = null;
        switch (topView.type) {
            case -1:
                // 常态家具
                baseCad = new GeneralFurniture(FurTypes.GENERAL_L3D);
                break;
            case 0:
                // 门
                String code = topView.xInfo.materialCode.toLowerCase();
                // 推拉门
                if (code.contains("tlm")) {
                    baseCad = new SlideDoor(FurTypes.SLIDE_DOOR);
                }
                // 单开门
                else {
                    baseCad = new SingleDoor(FurTypes.SINGLE_DOOR);
                }
                break;
            case 1:
                // 窗
                baseCad = new SimpleWindow(FurTypes.SIMPLE_WINDOW);
                break;
        }
        if (addFromReplace) {
            addFromReplace = false;
            topView.adi = replaceTopView.adi.copy();
            replaceTopView = null;
            baseCad.setTopView(topView);
            baseCadsList.add(baseCad);
        } else {
            baseCad.setTopView(topView);
            baseCadsList.add(baseCad);
        }
        designer3DRender.refreshRenderer();
        interObserver.notification();
    }

    /**
     * 获取所有家具列表
     */
    public ArrayList<BaseCad> getBaseCadsList() {
        return baseCadsList;
    }

    /**
     * 删除模型
     *
     * @param selctor
     */
    public void delete(BaseCad selctor) {
        if (selctor == null)
            return;
        baseCadsList.remove(selctor);
        designer3DRender.refreshRenderer();
        interObserver.notification();
    }

    /**
     * 镜像
     *
     * @param selctor
     */
    public void mirror(BaseCad selctor) {
        if (selctor == null)
            return;
        selctor.mirror();
        designer3DRender.refreshRenderer();
        interObserver.notification();
    }

    /**
     * 复制
     *
     * @param selctor
     */
    public void copy(BaseCad selctor) {
        if (selctor == null)
            return;
        try {
            ArrayList<Point> lepsList = PointList.getRotateLEPS(selctor.angle, 2 * selctor.xlong, selctor.point);
            Point point = lepsList.get((int) (Math.random() * lepsList.size()));
            TopView tpc = selctor.topView.copy();
            tpc.adi.point = point;
            FurTypes furTypes = selctor.furTypes;
            BaseCad baseCad = null;
            switch (tpc.type) {
                case -1:
                    // 常态家具
                    baseCad = new GeneralFurniture(FurTypes.GENERAL_L3D);
                    break;
                case 0:
                    // 门
                    String code = tpc.xInfo.materialCode.toLowerCase();
                    // 推拉门
                    if (code.contains("tlm")) {
                        baseCad = new SlideDoor(FurTypes.SLIDE_DOOR);
                    }
                    // 单开门
                    else {
                        baseCad = new SingleDoor(FurTypes.SINGLE_DOOR);
                    }
                    break;
                case 1:
                    // 窗
                    baseCad = new SimpleWindow(FurTypes.SIMPLE_WINDOW);
                    break;
            }
            baseCad.setTopView(tpc);
            baseCadsList.add(baseCad);
            designer3DRender.refreshRenderer();
            interObserver.notification();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 替换控件
    private TopView replaceTopView;
    private boolean addFromReplace;

    /**
     * 替换
     *
     * @param selector
     * @param type
     * @param resPath
     */
    public void replace(BaseCad selector, int type, ResUrlNodeXml.ResPath resPath) {
        replaceTopView = selector.topView.copy();
        addFromReplace = true;
        delete(selector);
        add(type, resPath);
    }

    /**
     * 数据释放
     */
    public void release() {
        if (baseCadsList.size() > 0) {
            for (BaseCad baseCad : baseCadsList) {
                baseCad.release();
                baseCad.topView.release();
            }
        }
        baseCadsList.clear();
    }

}
