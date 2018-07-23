package com.lejia.mobile.orderking.hk3d.datas;

import android.content.Context;

import com.lejia.mobile.orderking.hk3d.classes.Point;
import com.lejia.mobile.orderking.hk3d.classes.PointList;
import com.lejia.mobile.orderking.hk3d.classes.PolyE;
import com.lejia.mobile.orderking.hk3d.classes.PolyIntersectedResult;
import com.seisw.util.geom.Poly;
import com.seisw.util.geom.PolyDefault;

import java.util.ArrayList;

/**
 * Author by HEKE
 *
 * @time 2018/7/17 9:11
 * TODO: 基础房间定义
 */
public abstract class House {

    private Context mContext;

    // 房间是否闭合
    public boolean isWallClosed;

    // 房间是否与其他房间重叠
    public boolean isOverlap;

    /**
     * 墙中点列表
     */
    public PointList centerPointList;

    /**
     * 墙内点列表
     */
    public PointList innerPointList;

    /**
     * 墙外点列表
     */
    public PointList outerPointList;

    /**
     * 渲染墙体对象
     */
    public ArrayList<Wall> wallsList;

    /**
     * 渲染地面对象
     */
    public Ground ground;

    /**
     * 选中区域
     */
    public Selector selector;

    /**
     * 是否被选中标志
     */
    private boolean selected;

    public House(Context context) {
        this.mContext = context;
        this.wallsList = new ArrayList<>();
    }

    /**
     * 根据已知的区域组合生成房间的构造函数
     *
     * @param context
     * @param center_pointList 墙中点列表
     * @param thickness        所有墙体通用厚度
     */
    public House(Context context, PointList center_pointList, int thickness) {
        this.mContext = context;
        this.wallsList = new ArrayList<>();
        this.isWallClosed = true;
        loadDatas(center_pointList, thickness);
    }

    /**
     * 指定房间围点加载
     *
     * @param center_pointList
     * @param thickness
     */
    private void loadDatas(PointList center_pointList, int thickness) {
        if (center_pointList == null || center_pointList.invalid())
            return;
        int halfThickness = thickness / 2;
        centerPointList = new PointList(center_pointList.copy());
        innerPointList = new PointList(centerPointList.offsetList(false, halfThickness));
        outerPointList = new PointList(centerPointList.offsetList(true, halfThickness));
        createRenderer();
        initGroundAndSelector();
    }

    public Context getContext() {
        return mContext;
    }

    // 判断是否选中
    public boolean isSelected() {
        return selected;
    }

    // 设置是否被选中
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    /**
     * TODO 渲染数据
     *
     * @param positionAttribute
     * @param normalAttribute
     * @param colorAttribute
     * @param onlyPosition
     */
    public void render(int positionAttribute, int normalAttribute, int colorAttribute, boolean onlyPosition) {
        try {
            for (Wall wall : wallsList) {
                wall.render(positionAttribute, normalAttribute, colorAttribute, onlyPosition);
            }
            if (ground != null) {
                ground.render(positionAttribute, normalAttribute, colorAttribute, onlyPosition);
            }
            if (selector != null && selected) {
                selector.render(positionAttribute, normalAttribute, colorAttribute, onlyPosition);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * TODO 创建绘制对象
     */
    public void createRenderer() {
        wallsList.clear();
        int size = innerPointList.size();
        for (int i = 0; i < size; i++) {
            Point inow = innerPointList.getIndexAt(i);
            Point onow = outerPointList.getIndexAt(i);
            Point inext = null;
            Point onext = null;
            if (i == size - 1) {
                inext = innerPointList.getIndexAt(0);
                onext = outerPointList.getIndexAt(0);
            } else {
                inext = innerPointList.getIndexAt(i + 1);
                onext = outerPointList.getIndexAt(i + 1);
            }
            ArrayList<Point> pointsList = new ArrayList<>();
            pointsList.add(onow);
            pointsList.add(onext);
            pointsList.add(inext);
            pointsList.add(inow);
            PointList pointList = new PointList(pointsList);
            pointsList = pointList.antiClockwise();
            if (pointsList != null && pointsList.size() > 0) {
                Wall wall = new Wall(pointsList);
                wallsList.add(wall);
            }
        }
    }

    /**
     * 加载地面及选中对象
     */
    public void initGroundAndSelector() {
        if (innerPointList == null || innerPointList.invalid())
            return;
        ground = new Ground(innerPointList);
        selector = new Selector(innerPointList);
    }

    /**
     * 判断是否相交
     *
     * @param house
     * @return 相交结果对象
     */
    public PolyIntersectedResult getHouseIntersetedResult(House house) {
        if (house == null || house.equals(this))
            return null;
        if (house.centerPointList == null || centerPointList == null)
            return null;
        try {
            // 重叠
            if (house.centerPointList.equals(centerPointList)) {
                isOverlap = true;
                return new PolyIntersectedResult(0, null, null);
            }
            // 自身区域
            PolyDefault selfPoly = PolyE.toPolyDefault(centerPointList);
            // 被检测房间区域
            PolyDefault checkPoly = PolyE.toPolyDefault(house.centerPointList);
            // 获取相交情况
            Poly poly = selfPoly.intersection(checkPoly);
            if (poly != null && !poly.isEmpty()) {
                // 创建数据并返回
                ArrayList<PointList> pointListsList = new ArrayList<>();
                for (int i = 0; i < poly.getNumInnerPoly(); i++) {
                    Poly item = poly.getInnerPoly(i);
                    PointList pointList = PolyE.toPointList(item);
                    if (pointList.area() > 0.1d)
                        pointListsList.add(pointList);
                }
                if (pointListsList.size() == 0)
                    return null;
                return new PolyIntersectedResult(1, pointListsList, house);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取房间所有可触摸渲染数据
     */
    public ArrayList<RendererObject> getTotalRendererObjectList() {
        ArrayList<RendererObject> rendererObjectsList = new ArrayList<>();
        if (wallsList != null && wallsList.size() > 0) {
            rendererObjectsList.addAll(wallsList);
        }
        if (ground != null) {
            rendererObjectsList.add(ground);
        }
        return rendererObjectsList;
    }

}
