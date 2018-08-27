package com.lejia.mobile.orderking.hk3d.factory;

import com.lejia.mobile.orderking.bases.CatlogChecker;
import com.lejia.mobile.orderking.hk3d.TouchSelectedManager;
import com.lejia.mobile.orderking.hk3d.activity_partitation.Designer3DManager;
import com.lejia.mobile.orderking.hk3d.classes.Line;
import com.lejia.mobile.orderking.hk3d.classes.Point;
import com.lejia.mobile.orderking.hk3d.datas.Furniture;
import com.lejia.mobile.orderking.hk3d.datas.Ground;
import com.lejia.mobile.orderking.hk3d.datas.House;
import com.lejia.mobile.orderking.hk3d.datas.HouseDatasManager;
import com.lejia.mobile.orderking.hk3d.datas.RendererObject;
import com.lejia.mobile.orderking.hk3d.datas.cadwidgets.BaseCad;
import com.lejia.mobile.orderking.hk3d.datas.cadwidgets.FurTypes;
import com.lejia.mobile.orderking.hk3d.datas.cadwidgets.GeneralFurniture;
import com.lejia.mobile.orderking.hk3d.datas.cadwidgets.SimpleWindow;
import com.lejia.mobile.orderking.hk3d.datas.cadwidgets.SingleDoor;

import java.util.ArrayList;

/**
 * Author by HEKE
 *
 * @time 2018/8/23 14:28
 * TODO: 创建家具对象
 */
public class CadFurnitureCreator {

    /**
     * 创建门窗
     *
     * @param houseDatasManager 房间管理对象
     * @param designer3DManager 三维管理对象
     * @param furniture         家具信息
     * @return 返回门窗对象
     */
    public static BaseCad createDoorOrWindow(HouseDatasManager houseDatasManager, Designer3DManager designer3DManager, Furniture furniture) {
        if (houseDatasManager == null || designer3DManager == null || furniture == null)
            return null;
        Line onLine = null;
        try {
            // 吸附点、角度、厚度
            Point pointAt = new Point(0.d, 0.d);
            double angle = 0.0d;
            double thickness = 24.0d;
            // 优先检测选中房间
            RendererObject rendererObject = null;
            TouchSelectedManager touchSelectedManager = designer3DManager.getDesigner3DRender().getTouchSelectedManager();
            if (touchSelectedManager != null)
                rendererObject = touchSelectedManager.getSelector();
            ArrayList<Line> selectorLinesList = null;
            if (rendererObject != null && (rendererObject instanceof Ground)) {
                Ground ground = (Ground) rendererObject;
                selectorLinesList = ground.getHouse().centerPointList.toLineList();
            }
            if (selectorLinesList != null) {
                int random = (int) (Math.random() * selectorLinesList.size());
                Line atLine = selectorLinesList.get(random);
                onLine = atLine.copy();
            } else {
                // 无选中房间，随机一个房间位置
                ArrayList<House> housesList = houseDatasManager.getHousesList();
                if (housesList != null) {
                    int randomHouse = (int) (Math.random() * housesList.size());
                    House house = housesList.get(randomHouse);
                    ArrayList<Line> linesList = house.isWallClosed ? house.centerPointList.toLineList() : house.centerPointList.toNotClosedLineList();
                    int random = (int) (Math.random() * linesList.size());
                    Line atLine = linesList.get(random);
                    onLine = atLine.copy();
                }
            }
            // 有效
            if (onLine != null) {
                pointAt = onLine.getCenter();
                angle = onLine.getAngle();
                thickness = onLine.getThickess();
            }
            // 根据门窗类型创建
            int checkResult = CatlogChecker.checkDoorOrWindow(furniture.modelMaterialTypeID);
            BaseCad baseCad = null;
            // 单开门
            if (checkResult == 0) {
                baseCad = new SingleDoor(angle, thickness, furniture.xLong / 10, pointAt, FurTypes.SINGLE_DOOR, furniture);
            }
            // 窗
            else if (checkResult == 1) {
                baseCad = new SimpleWindow(angle, thickness, furniture.xLong / 10, pointAt, FurTypes.SIMPLE_WINDOW, furniture);
            }
            return baseCad;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 模型顶视图
     *
     * @param houseDatasManager 房间管理对象
     * @param designer3DManager 三维管理对象
     * @param furniture         家具信息
     * @return
     */
    public static BaseCad createGeneralFurniture(HouseDatasManager houseDatasManager, Designer3DManager designer3DManager, Furniture furniture) {
        if (houseDatasManager == null || designer3DManager == null || furniture == null)
            return null;
        // 吸附点、角度、厚度
        Point pointAt = null;
        double angle = 0.0d;
        double thickness = 24.0d;
        // 优先检测选中房间
        RendererObject rendererObject = null;
        TouchSelectedManager touchSelectedManager = designer3DManager.getDesigner3DRender().getTouchSelectedManager();
        if (touchSelectedManager != null)
            rendererObject = touchSelectedManager.getSelector();
        if (rendererObject != null && (rendererObject instanceof Ground)) {
            Ground ground = (Ground) rendererObject;
            pointAt = ground.getHouse().centerPointList.getInnerValidPoint(false);
        }
        if (pointAt == null) {
            // 无选中房间，随机一个房间位置
            ArrayList<House> housesList = houseDatasManager.getHousesList();
            if (housesList != null) {
                int randomHouse = (int) (Math.random() * housesList.size());
                House house = housesList.get(randomHouse);
                pointAt = house.centerPointList.getInnerValidPoint(false);
            }
        }
        if (pointAt == null)
            pointAt = new Point(0.d, 0.d);
        // 创建家具
        BaseCad baseCad = new GeneralFurniture(angle, furniture.width / 10, furniture.xLong / 10, pointAt, FurTypes.GENERAL_L3D, furniture);
        return baseCad;
    }

}
