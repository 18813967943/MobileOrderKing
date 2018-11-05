package com.lejia.mobile.orderking.hk3d.datas_2d;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;

import com.lejia.mobile.orderking.bases.OrderKingApplication;
import com.lejia.mobile.orderking.hk3d.RendererState;
import com.lejia.mobile.orderking.hk3d.classes.Line;
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

    public Point down; // 按下点
    public Point up; // 弹起点

    /**
     * 墙体厚度
     */
    public int thickness = 24;

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
     * 房间名称对象
     */
    public HouseName houseName;

    /**
     * 是否被选中标志
     */
    private boolean selected;

    /**
     * 是否线建墙
     */
    public boolean isLineSeg;

    /**
     * 线建墙墙体
     */
    public LineSeg lineSeg;

    public House(Context context) {
        this.mContext = context;
        this.wallsList = new ArrayList<>();
    }

    public House(Context context, boolean isLineSeg) {
        this.mContext = context;
        this.isLineSeg = isLineSeg;
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
     * 根据已知的区域组合生成房间的构造函数
     *
     * @param context
     * @param center_pointList 墙中点列表
     * @param thickness        所有墙体通用厚度
     * @param isLineSeg        是否线建墙
     */
    public House(Context context, PointList center_pointList, int thickness, boolean isLineSeg) {
        this.mContext = context;
        this.wallsList = new ArrayList<>();
        this.isWallClosed = true;
        this.isLineSeg = isLineSeg;
        loadDatas(center_pointList, thickness);
    }

    /**
     * 判断房间是否无效
     */
    public boolean isInvalid() {
        return down == null || up == null;
    }

    ArrayList<Point> innerTList;
    ArrayList<Point> outerTList;

    /**
     * 指定房间围点加载
     *
     * @param center_pointList
     * @param thickness
     */
    @SuppressLint("StaticFieldLeak")
    private void loadDatas(PointList center_pointList, int thickness) {
        if (center_pointList == null || center_pointList.invalid())
            return;
        int halfThickness = thickness / 2;
        centerPointList = new PointList(center_pointList.copy());
        innerTList = centerPointList.offsetList(false, halfThickness);
        innerPointList = new PointList(innerTList);
        outerTList = centerPointList.offsetList(true, halfThickness);
        outerPointList = new PointList(outerTList);
        if (innerPointList.size() != outerPointList.size()) {
            System.out.println();
        }
        // 在主进程中执行
        ((Activity) OrderKingApplication.getMainActivityContext()).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                createRenderer();
                initGroundAndSelector();
            }
        });
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

    public int getThickness() {
        return thickness;
    }

    public void setThickness(int thickness) {
        this.thickness = thickness;
    }

    /**
     * 点吸附检测
     *
     * @param check
     */
    public Point adsorbPoint(Point check) {
        if (check == null || centerPointList == null)
            return null;
        for (int i = 0; i < centerPointList.size(); i++) {
            Point point = centerPointList.getIndexAt(i);
            if (point.dist(check) <= 50) {
                return point.copy();
            }
        }
        return null;
    }

    /**
     * 线段吸附
     *
     * @param check
     */
    public Point adsorbLine(Point check) {
        if (check == null || centerPointList == null)
            return null;
        ArrayList<Line> linesList = isWallClosed ? centerPointList.toLineList() : centerPointList.toNotClosedLineList();
        for (Line line : linesList) {
            Point adsorb = line.getAdsorbPoint(check.x, check.y, 50);
            if (adsorb != null) {
                return adsorb;
            }
        }
        return null;
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
            boolean isNot2D = RendererState.isNot2D();
            if (!isNot2D) { // 三维平面绘制内容
                // 线建墙
                if (isLineSeg) {
                    if (lineSeg != null) {
                        lineSeg.render(positionAttribute, normalAttribute, colorAttribute, onlyPosition);
                    }
                }
                // 非线建墙
                else {
                    if (wallsList.size() > 0) {
                        for (int i = 0; i < wallsList.size(); i++) { // 不使用迭代操作
                            Wall wall = wallsList.get(i);
                            if (wall != null)
                                wall.render(positionAttribute, normalAttribute, colorAttribute, onlyPosition);
                        }
                    }
                }
            }
            if (ground != null) {
                ground.render(positionAttribute, normalAttribute, colorAttribute, onlyPosition);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 渲染名称
     */
    public void renderName(int positionAttribute, int normalAttribute, int colorAttribute, boolean onlyPosition) {
        boolean isNot2D = RendererState.isNot2D();
        if (!isNot2D) {
            if (houseName != null && isWallClosed) {
                houseName.render(positionAttribute, normalAttribute, colorAttribute, onlyPosition);
            }
            if (selector != null && selected) {
                selector.render(positionAttribute, normalAttribute, colorAttribute, onlyPosition);
            }
        }
    }

    /**
     * TODO 创建绘制对象
     */
    public void createRenderer() {
        try {
            wallsList.clear();
            int size = innerPointList.size();
            int hashCode = hashCode();
            int cellHeight = 280;
            int index = 0;
            for (int i = 0; i < size; i++) {
                Point inow = innerPointList.getIndexAt(i);
                Point onow = outerPointList.getIndexAt(i);
                Point inext = null;
                Point onext = null;
                if (i == size - 1) {
                    if (!isWallClosed) // 非闭合房间，最后一面墙不处理
                        break;
                    inext = innerPointList.getIndexAt(0);
                    onext = outerPointList.getIndexAt(0);
                } else {
                    inext = innerPointList.getIndexAt(i + 1);
                    onext = outerPointList.getIndexAt(i + 1);
                }
                ArrayList<Point> pointsList = new ArrayList<>();
                pointsList.add(onow);
                pointsList.add(onext);
                // 判断内部两点哪个与外部下一个点更近，视为连接点
                double nondist = onext.dist(inext);
                double nindist = onext.dist(inow);
                if (nondist <= nindist) {
                    pointsList.add(inext);
                    pointsList.add(inow);
                } else {
                    pointsList.add(inow);
                    pointsList.add(inext);
                }
                PointList pointList = new PointList(pointsList);
                pointsList = pointList.antiClockwise();
                if (pointsList != null && pointsList.size() > 0) {
                    Wall wall = new Wall(pointsList, this);
                    wallsList.add(wall);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 加载地面及选中对象
     */
    public void initGroundAndSelector() {
        if (innerPointList == null || innerPointList.invalid())
            return;
        ground = new Ground(innerPointList, this); // 地面
        selector = new Selector(innerPointList); // 选中框
        houseName = new HouseName(innerPointList); // 房间名称
    }

    /**
     * 获取选中对象
     */
    public Selector getSelector() {
        return selector;
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
    public ArrayList<RendererObject> getCurrentTotalRendererObjectList() {
        ArrayList<RendererObject> rendererObjectsList = new ArrayList<>();
        boolean isNot2D = RendererState.isNot2D();
        if (isNot2D) {
            if (ground != null) {
                rendererObjectsList.add(ground);
            }
        } else {
            if (wallsList != null && wallsList.size() > 0) {
                rendererObjectsList.addAll(wallsList);
            }
            if (ground != null) {
                rendererObjectsList.add(ground);
            }
            if (houseName != null) {
                rendererObjectsList.add(houseName);
            }
        }
        return rendererObjectsList;
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
        if (houseName != null) {
            rendererObjectsList.add(houseName);
        }
        return rendererObjectsList;
    }

    /**
     * 判断点是否在此房间内部
     *
     * @param point
     * @return
     */
    public boolean isPointInner(Point point) {
        if (!isWallClosed || innerPointList == null || point == null)
            return false;
        return PointList.pointRelationToPolygon(innerPointList.getPointsList(), point) == 1;
    }

    /**
     * 获取未闭合房间的内部线段一点
     */
    public Point getUncloseInnerLineCenterPoint() {
        if (centerPointList == null || isWallClosed)
            return null;
        if (centerPointList.size() <= 2)
            return centerPointList.toNotClosedLineList().get(0).getCenter();
        else
            return centerPointList.toNotClosedLineList().get(1).getCenter();
    }

    /**
     * 刷新请求
     */
    public void refreshRenderer() {
        ((OrderKingApplication) getContext().getApplicationContext()).render(); // refresh render contents
    }

}
