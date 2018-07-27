package com.lejia.mobile.orderking.hk3d.gpc;

import com.lejia.mobile.orderking.hk3d.classes.Line;
import com.lejia.mobile.orderking.hk3d.classes.Point;
import com.lejia.mobile.orderking.hk3d.classes.PointList;
import com.lejia.mobile.orderking.hk3d.classes.PolyE;
import com.lejia.mobile.orderking.hk3d.classes.TileDescription;
import com.lejia.mobile.orderking.hk3d.datas.Area3D;
import com.seisw.util.geom.Poly;

import java.util.ArrayList;

/**
 * Author by HEKE
 *
 * @time 2018/7/25 17:56
 * TODO: 瓷砖区域分割对象
 */
public class TilesSplitor {

    private TilesResult mTilesResult; // 材质铺砖结果对象
    private PointList pointList; // 需要铺贴的区域
    private PointList boxList; // 有效检测盒子区域
    private Poly listPoly; // 实际区域列表区域
    private Poly boxPoly; // 有效检测盒子区域
    private Point begain; // 起始点
    private double gapThickness; // 砖缝厚度
    private int width; // 砖宽度
    private int height; // 砖厚度(高度)
    private TileDescription tileDescription; // 瓷砖信息对象
    private boolean skewTile; // 是否斜铺
    private double bevell; // 对角线长度
    private int materialPosition; // 材质所在的位置
    private String materialCode; // 材质编码

    /**
     * 已检测过的点
     */
    private ArrayList<Point> checkedList;

    public TilesSplitor(TilesResult tilesResult, ArrayList<Point> pointsList, Point begain, int gapThickness,
                        int width, int height, TileDescription tileDescription, boolean skewTile, double bevell,
                        int materialPosition) {
        this.mTilesResult = tilesResult;
        this.pointList = new PointList(pointsList);
        this.boxList = new PointList(this.pointList.getBoxList());
        this.listPoly = PolyE.toPolyDefault(this.pointList);
        this.boxPoly = PolyE.toPolyDefault(this.boxList);
        this.begain = begain;
        this.gapThickness = gapThickness * 0.1d;
        this.width = width;
        this.height = height;
        this.tileDescription = tileDescription;
        this.skewTile = skewTile;
        this.bevell = bevell;
        this.materialPosition = materialPosition;
        this.materialCode = tileDescription.getMaterialCode(materialPosition);
        this.checkedList = new ArrayList<>();
        // 铺贴第一块实质砖
        checkAndSplitArea(this.begain, this.width, this.height, false);
    }

    /**
     * 判断点是否已经检测过
     *
     * @param point
     */
    private boolean checked(Point point) {
        if (point == null)
            return false;
        for (Point point1 : checkedList) {
            if (point.equals(point1))
                return true;
        }
        return false;
    }

    /*****************************************
     *  砖缝与材质计数
     * ***************************************/
    private ArrayList<GapPoint> allGapsList; // 砖缝
    private int checkGapSize;
    private int currentGapCount;

    private ArrayList<Point> allRtilesList; // 实质材质
    private int checkTileSize = 1; // 初始化加载
    private int currentTileCount;

    private int count;

    /**
     * @param point  起始点
     * @param width  宽度
     * @param height 高度
     * @param isGap  是否砖缝
     */
    private void checkAndSplitArea(Point point, double width, double height, boolean isGap) {
        // 检测是否已经被检测过的点
        if (checked(point)) {
            checkCount(isGap);
            return;
        }
        // 加入已检测过的点列表
        checkedList.add(point);
        // 自身围点
        ArrayList<Point> selfList = null;
        if (skewTile) {
            selfList = PointList.getRotateVertexs(-45, height, width, point);
        } else {
            selfList = PointList.getRotateVertexs(0, height, width, point);
        }
        Poly self = PolyE.toPolyDefault(selfList);
        // 先检测是否在有效检测盒子内部
        Poly boxCheckPoly = self.intersection(boxPoly);
        PointList bbbb = PolyE.toPointList(boxPoly);
        Poly listCheckPoly = self.intersection(listPoly);
        PointList cccc = PolyE.toPointList(listPoly);
        System.out.println("#### boxCheckPoly : " + (boxCheckPoly == null || boxCheckPoly.isEmpty()));
        System.out.println("#### listCheckPoly : " + (listCheckPoly == null || listCheckPoly.isEmpty()));
        if (boxCheckPoly != null && !boxCheckPoly.isEmpty() && listCheckPoly != null && !listCheckPoly.isEmpty()) {
            PointList boxList = PolyE.toPointList(boxCheckPoly);
            PointList listList = PolyE.toPointList(listCheckPoly);
            System.out.println("### boxList : " + boxList);
            System.out.println("### listList : " + listList);
            // 两个区域列表相等、或者都不为空时以铺砖列表相交为结果
            Area3D area3D = new Area3D(isGap, materialCode, listList.getPointsList(), selfList);
            mTilesResult.putArea3D(area3D);
            // 继续切割
            // 实质砖，根据自身砖的完整区域，运算砖缝
            if (!isGap) {
                ArrayList<GapPoint> gapsList = getOuterAllLinesCenterPointList(new PointList(selfList), gapThickness / 2);
                if (gapsList != null) {
                    // 设置需要检测的砖缝总数据
                    if (allGapsList == null) {
                        allGapsList = new ArrayList<>();
                        allGapsList.addAll(gapsList);
                        checkGapSize = allGapsList.size();
                        currentGapCount = 0;
                    } else {
                        allGapsList.addAll(gapsList);
                        checkGapSize = allGapsList.size();
                        currentGapCount = 0;
                    }
                    // 检查实质砖是否处理完毕
                    count++;
                    currentTileCount++;
                    paveCheckTiles();
                } else {
                    checkCount(isGap);
                }
            }
            // 砖缝，根据砖缝的最长边运算两块实质砖(正常区域不可能出现砖缝大于长度的情况)
            else {
                ArrayList<Point> rtList = getOuterRealTileCenterPointList(new PointList(selfList), point);
                if (rtList != null) {
                    // 设置需要遍历的实质砖
                    if (allRtilesList == null) {
                        allRtilesList = new ArrayList<>();
                        allRtilesList.addAll(rtList);
                        checkTileSize = allRtilesList.size();
                        currentTileCount = 0;
                    } else {
                        allRtilesList.addAll(rtList);
                        checkTileSize = allRtilesList.size();
                        currentTileCount = 0;
                    }
                    // 检测是否已经完成砖缝检测
                    count++;
                    currentGapCount++;
                    paveCheckGaps();
                } else {
                    checkCount(isGap);
                }
            }
        }
        // 无效区域计数处理
        else {
            checkCount(isGap);
        }
        System.out.println("####### Count : " + count + "  次 !");
    }

    /**
     * 计数
     */
    private void checkCount(boolean isGap) {
        // 实砖
        if (!isGap) {
            currentTileCount++;
            paveCheckTiles();
        }
        // 砖缝
        else {
            currentGapCount++;
            paveCheckGaps();
        }
    }

    /**
     * 铺贴当前检测的砖缝区域
     */
    private void paveCheckGaps() {
        if (currentGapCount == checkGapSize) {
            if (allGapsList != null)
                allGapsList.clear();
            // 遍历检查实质砖
            for (int i = 0; i < allRtilesList.size(); i++) {
                Point rtp = allRtilesList.get(i);
                checkAndSplitArea(rtp, width, height, false);
            }
        }
    }

    /**
     * 铺贴当前检测的实砖区域
     */
    private void paveCheckTiles() {
        if (currentTileCount == checkTileSize) {
            if (allRtilesList != null)
                allRtilesList.clear();
            // 遍历检查砖缝
            for (int i = 0; i < allGapsList.size(); i++) {
                GapPoint gapCenter = allGapsList.get(i);
                checkAndSplitArea(gapCenter.point, gapCenter.width, gapCenter.height, true);
            }
        }
    }

    /**
     * 获取砖缝相连的两块实质砖的中心点列表
     *
     * @param pointList 砖缝区域
     * @param gapPoint  砖缝中心点
     * @return
     */
    private ArrayList<Point> getOuterRealTileCenterPointList(PointList pointList, Point gapPoint) {
        if (pointList == null || pointList.invalid())
            return null;
        ArrayList<Point> resultList = new ArrayList<>();
        ArrayList<Line> linesList = pointList.toLineList();
        for (Line line : linesList) {
            double length = line.getLength();
            if (length > gapThickness) {
                ArrayList<Point> lepsList = null;
                if (Math.abs(length - width) < 1d) {
                    lepsList = PointList.getRotateLEPS(line.getAngle() + 90f, height + gapThickness, gapPoint);
                } else if (Math.abs(length - height) < 1d) {
                    lepsList = PointList.getRotateLEPS(line.getAngle() + 90f, width + gapThickness, gapPoint);
                }
                if (lepsList != null) {
                    for (Point point : lepsList) {
                        if (!checked(point)) {
                            resultList.add(point);
                        }
                    }
                }
                break;
            }
        }
        if (resultList.size() == 0)
            return null;
        return resultList;
    }

    /**
     * 获取区域每条线段中点朝外指定距离的点列表
     *
     * @param pointList 点区域
     * @param distance  偏移距离
     * @return
     */
    private ArrayList<GapPoint> getOuterAllLinesCenterPointList(PointList pointList, double distance) {
        if (pointList == null || pointList.invalid())
            return null;
        ArrayList<GapPoint> resultList = new ArrayList<>();
        ArrayList<Line> linesList = pointList.toLineList();
        for (Line line : linesList) {
            ArrayList<Point> lepsList = PointList.getRotateLEPS(line.getAngle() + 90f, 2 * distance, line.getCenter());
            for (Point point : lepsList) {
                if (PointList.pointRelationToPolygon(pointList.getPointsList(), point) == -1) {
                    if (!checked(point)) {
                        GapPoint gapPoint = new GapPoint();
                        gapPoint.point = point;
                        gapPoint.height = gapThickness;
                        gapPoint.width = line.getLength();
                        resultList.add(gapPoint);
                    }
                    break;
                }
            }
        }
        if (resultList.size() == 0)
            return null;
        return resultList;
    }

}
