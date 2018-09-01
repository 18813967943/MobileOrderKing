package com.lejia.mobile.orderking.hk3d.datas;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;

import com.lejia.mobile.orderking.bases.OrderKingApplication;
import com.lejia.mobile.orderking.hk3d.classes.CloseHouseCheckResult;
import com.lejia.mobile.orderking.hk3d.classes.Line;
import com.lejia.mobile.orderking.hk3d.classes.LineList;
import com.lejia.mobile.orderking.hk3d.classes.Point;
import com.lejia.mobile.orderking.hk3d.classes.PointList;
import com.lejia.mobile.orderking.hk3d.classes.PolyE;
import com.lejia.mobile.orderking.hk3d.classes.PolyIntersectedResult;
import com.lejia.mobile.orderking.hk3d.classes.PolyM;
import com.lejia.mobile.orderking.hk3d.classes.PolyUM;
import com.lejia.mobile.orderking.hk3d.classes.UncloseCheckResult;
import com.lejia.mobile.orderking.hk3d.datas.cadwidgets.BaseCad;
import com.lejia.mobile.orderking.hk3d.datas.cadwidgets.FurTypes;
import com.lejia.mobile.orderking.hk3d.datas.cadwidgets.GeneralFurniture;
import com.lejia.mobile.orderking.hk3d.datas.cadwidgets.SimpleWindow;
import com.lejia.mobile.orderking.hk3d.datas.cadwidgets.SingleDoor;
import com.lejia.mobile.orderking.hk3d.factory.PointsSplitor;
import com.seisw.util.geom.Poly;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

/**
 * Author by HEKE
 *
 * @time 2018/7/16 15:39
 * TODO: 总房间数据管理
 */
public class HouseDatasManager {

    private Context mContext;
    private ArrayList<House> housesList; // 矩形房间

    /**
     * 所有家具列表
     */
    private ArrayList<BaseCad> furnituresList;

    public HouseDatasManager(Context context) {
        mContext = context;
        this.housesList = new ArrayList<>();
        this.furnituresList = new ArrayList<>();
        this.furnitureArrayList = new ArrayList<>();
        this.wallOuterFacadesList = new ArrayList<>();
    }

    /**
     * 判断当前三维是否包含数据
     */
    public boolean hasDatas() {
        return housesList.size() > 1;
    }

    /**
     * 新增房间
     *
     * @param house
     */
    public void add(House house) {
        if (house == null)
            return;
        housesList.add(house);
    }

    /**
     * 新增房间
     *
     * @param pointList
     */
    public House add(PointList pointList) {
        if (pointList == null || pointList.invalid())
            return null;
        NormalHouse normalHouse = new NormalHouse(mContext, pointList, 24);
        housesList.add(normalHouse);
        return normalHouse;
    }

    /**
     * 新增房间
     *
     * @param poly
     */
    public House add(Poly poly) {
        return add(PolyE.toPointList(poly));
    }

    /**
     * 新增房间
     *
     * @param pointsList
     */
    public House add(ArrayList<Point> pointsList) {
        return add(new PointList(pointsList));
    }

    /**
     * 移除房间
     *
     * @param house 指定移除的房间
     * @return 返回移除结果
     */
    public boolean remove(House house) {
        if (house == null || housesList.size() == 0)
            return false;
        boolean removed = housesList.remove(house);
        // 未闭合普通房间或者线建墙去除缓存数据
        if (house instanceof NormalHouse) {
            if (!house.isWallClosed) {
                PolyUM.remove("" + house.hashCode());
            }
        }
        refreshRender();
        return removed;
    }

    /**
     * 检测区域切割问题
     *
     * @param checkHouse
     */
    @SuppressLint("StaticFieldLeak")
    public void gpcClosedCheck(House checkHouse) {
        // 空或者未闭合房间直接返回
        if (checkHouse == null || !checkHouse.isWallClosed) {
            remove(checkHouse);
            refreshRender();
            return;
        }
        // 手指弹起后，如果房间的面积小于1平方米，直接删除
        if (checkHouse.centerPointList.area() < 1) {
            remove(checkHouse);
            refreshRender();
            return;
        }
        // 检测房间宽度或高度过小
        if (checkHouse instanceof RectHouse) {
            RectHouse rectHouse = (RectHouse) checkHouse;
            if (rectHouse.isSizeInvalid()) {
                remove(checkHouse);
                refreshRender();
                return;
            }
        }
        new AsyncTask<House, Integer, CloseHouseCheckResult>() {
            @Override
            protected CloseHouseCheckResult doInBackground(House... houses) {
                CloseHouseCheckResult closeHouseCheckResult = null;
                // 第一个闭合区域，直接组合
                House checkHouse = houses[0];
                if (housesList.size() == 1) {
                    PolyM.put(PolyM.newCreateIndex(), PolyE.toPolyDefault(checkHouse.centerPointList));
                    closeHouseCheckResult = new CloseHouseCheckResult(checkHouse, null);
                } else {
                    // 获取所有相交的列表集
                    ArrayList<PolyIntersectedResult> intersectedResultsList = new ArrayList<>();
                    for (House house : housesList) {
                        if (house.isWallClosed) {
                            PolyIntersectedResult result = checkHouse.getHouseIntersetedResult(house);
                            if (result != null)
                                intersectedResultsList.add(result);
                        }
                    }
                    if (intersectedResultsList.size() > 0) {
                        // 如果有重合，直接去除
                        if (checkHouse.isOverlap) {
                            housesList.remove(checkHouse);
                        }
                        // 未重合，相交分割房间
                        else {
                            // 获取相交房间的所属组合区域，并先从渲染数据中移除当前房间与相交房间
                            remove(checkHouse);
                            for (PolyIntersectedResult ret : intersectedResultsList) {
                                // 从渲染缓存中移除相交房间
                                remove(ret.house);
                            }
                            // 重新组合替换组合区域
                            Poly checkPoly = PolyE.toPolyDefault(checkHouse.centerPointList);
                            PolyM.put(PolyM.newCreateIndex(), checkPoly);
                            // 新增相交的房间及切割后的相交房间
                            closeHouseCheckResult = new CloseHouseCheckResult();
                            ArrayList<PointList> totalPointList = new ArrayList<>();
                            for (PolyIntersectedResult ret : intersectedResultsList) {
                                // 切割后的区域
                                closeHouseCheckResult.add(ret.differencePointList);
                                // 相交区域
                                ArrayList<PointList> pointLists = ret.pointListsList;
                                if (pointLists != null && pointLists.size() > 0) {
                                    totalPointList.addAll(pointLists);
                                    for (PointList pointList : pointLists) {
                                        closeHouseCheckResult.add(pointList);
                                    }
                                }
                            }
                            // 新增自身切割后的房间
                            PolyIntersectedResult checkHouseResult = new PolyIntersectedResult(1, totalPointList, checkHouse);
                            if (checkHouseResult.differencePointList != null)
                                closeHouseCheckResult.add(checkHouseResult.differencePointList);
                            // 释放数据
                            checkHouseResult.release();
                            for (PolyIntersectedResult ret : intersectedResultsList) {
                                ret.release();
                            }
                        }
                    }
                    // 未与任何区域相交，直接加入新区域
                    else {
                        PolyM.put(PolyM.newCreateIndex(), PolyE.toPolyDefault(checkHouse.centerPointList));
                        closeHouseCheckResult = new CloseHouseCheckResult(checkHouse, null);
                    }
                }
                return closeHouseCheckResult;
            }

            @Override
            protected void onPostExecute(CloseHouseCheckResult v) {
                super.onPostExecute(v);
                // 根据结果对应操作，在主进程中操作
                if (v != null) {
                    // 需要加载地面的房间
                    if (v.needLoadGroundHouse != null) {
                        v.needLoadGroundHouse.initGroundAndSelector();
                    }
                    // 切割出的新房间
                    if (v.pointListsList != null && v.pointListsList.size() > 0) {
                        ArrayList<PointList> pointListsList = v.pointListsList;
                        for (PointList pointList : pointListsList) {
                            add(pointList);
                        }
                    }
                }
                // 刷新三维渲染内容
                refreshRender();
            }
        }.execute(checkHouse);
    }

    /************************************
     * TODO 未闭合房间绘制处理
     * ********************************/

    /**
     * 未闭合房间检测
     *
     * @param house
     */
    public void gpcUncloseCheck(House house) {
        if (house == null)
            return;
        try {
            // 自身数据
            PointList centerList = house.centerPointList;
            Line selfLine = centerList.toNotClosedLineList().get(0);
            // 循环遍历求出相交数据
            ArrayList<UncloseCheckResult> uncloseCheckResultsList = new ArrayList<>();
            for (House house1 : housesList) {
                if (!(house.equals(house1))) {
                    ArrayList<Line> wallLineList = null;
                    if (house1.centerPointList != null && house1.centerPointList.size() > 1) {
                        if (house1.isWallClosed)
                            wallLineList = house1.centerPointList.toLineList();
                        else
                            wallLineList = house1.centerPointList.toNotClosedLineList();
                    }
                    if (wallLineList != null) {
                        for (Line line : wallLineList) {
                            Point interPoint = selfLine.getLineIntersectedPoint(line);
                            if (interPoint != null) {
                                int sideIndex = selfLine.isSidePoint(interPoint);
                                if (uncloseCheckResultsList.size() == 0) {
                                    uncloseCheckResultsList.add(new UncloseCheckResult(house1, interPoint, sideIndex, (sideIndex != -1)));
                                } else {
                                    boolean existed = false;
                                    for (UncloseCheckResult uncloseCheckResult : uncloseCheckResultsList) {
                                        if (uncloseCheckResult.interPoint.equals(interPoint)) {
                                            existed = true;
                                            break;
                                        }
                                    }
                                    if (!existed)
                                        uncloseCheckResultsList.add(new UncloseCheckResult(house1, interPoint, sideIndex, (sideIndex != -1)));
                                }
                            }
                        }
                    }
                }
            }
            // 根据相交结果对象列表分别处理数据
            // 1、无任何交集，直接存入数据缓存
            int size = uncloseCheckResultsList.size();
            if (size == 0) {
                PolyUM.put("" + house.hashCode(), PolyE.toPolyDefault(centerList));
            }
            // 2、相交于一个房间
            else if (size == 1) {
                UncloseCheckResult uncloseCheckResult = uncloseCheckResultsList.get(0);
                if (uncloseCheckResult.interHouse.isWallClosed) { // 相较于闭合房间，存入缓存
                    PolyUM.put("" + house.hashCode(), PolyE.toPolyDefault(centerList));
                } else { // 相较于未闭合房间
                    // 端点相交，合并房间
                    if (uncloseCheckResult.isSidePoint) {
                        if (uncloseCheckResult.isInterHouseSidePoint) { // 被相交房间端点相连
                            // 组合点结果列表
                            ArrayList<Point> polyPointList = new ArrayList<>();
                            // 起点相交
                            if (uncloseCheckResult.sideIndex == 0) {
                                if (uncloseCheckResult.isInterHouseSidePointBegain) {
                                    polyPointList.add(selfLine.up.copy());
                                    PointList interList = uncloseCheckResult.interHouse.centerPointList;
                                    polyPointList.addAll(interList.copy());
                                } else {
                                    PointList interList = uncloseCheckResult.interHouse.centerPointList;
                                    polyPointList.addAll(interList.copy());
                                    polyPointList.add(selfLine.up.copy());
                                }
                            }
                            // 终点相交
                            else {
                                if (uncloseCheckResult.isInterHouseSidePointBegain) {
                                    polyPointList.add(selfLine.down.copy());
                                    PointList interList = uncloseCheckResult.interHouse.centerPointList;
                                    polyPointList.addAll(interList.copy());
                                } else {
                                    PointList interList = uncloseCheckResult.interHouse.centerPointList;
                                    polyPointList.addAll(interList.copy());
                                    polyPointList.add(selfLine.down.copy());
                                }
                            }
                            // 移除房间
                            remove(house);
                            // 合并房间，检测新组合两个端点是否在绘制的墙体上或较近
                            NormalHouse polyHouse = (NormalHouse) uncloseCheckResult.interHouse;
                            polyHouse.setCenterPointList(polyPointList, false);
                            PolyUM.put("" + polyHouse.hashCode(), PolyE.toPolyDefault(polyHouse.centerPointList));
                        } else { // 不是两端端点相连，存入缓存
                            PolyUM.put("" + house.hashCode(), PolyE.toPolyDefault(centerList));
                        }
                    }
                    // 非端点相交，存入缓存
                    else {
                        PolyUM.put("" + house.hashCode(), PolyE.toPolyDefault(centerList));
                    }
                }
            }
            // 相交于2个房间
            else if (size == 2) {
                UncloseCheckResult UCR0 = uncloseCheckResultsList.get(0);
                UncloseCheckResult UCR1 = uncloseCheckResultsList.get(1);
                // 首尾相交为同一个房间
                if (UCR0.interHouse.equals(UCR1.interHouse) || UCR0.isUncloseCheckInterAlsoOn(UCR1) || UCR1.isUncloseCheckInterAlsoOn(UCR0)) {
                    // 求出目标所在的房间
                    Point innerPoint = house.getUncloseInnerLineCenterPoint();
                    House interHouse = UCR0.interHouse;
                    // 闭合房间
                    if (interHouse.isWallClosed) {
                        interHouse = getInnerLineAtHouse(UCR0, UCR1, innerPoint);
                        // 内部画线段
                        if (interHouse != null) {
                            int thickness = interHouse.thickness;
                            Poly closePoly = PolyE.toPolyDefault(interHouse.centerPointList);
                            ArrayList<Point> pointsList = new ArrayList<>();
                            pointsList.add(UCR0.interPoint);
                            pointsList.add(UCR1.interPoint);
                            Poly linePoly = PolyE.simpleAlignPoly(closePoly, PolyE.toPolyDefault(pointsList));
                            ArrayList<Poly> poliesList = PointsSplitor.splitLineAreaWithCloseArea(linePoly, closePoly);
                            if (poliesList != null) {
                                // 移除房间
                                remove(house);
                                remove(interHouse);
                                // 新建房间
                                for (Poly poly : poliesList) {
                                    if (poly != null && !poly.isEmpty()) {
                                        PointList pointList = PolyE.toPointList(poly);
                                        add(pointList);
                                        PolyM.put(PolyM.newCreateIndex(), PolyE.toPolyDefault(pointList)); // 存入缓存
                                    }
                                }
                            }
                        }
                        // 外部画线段
                        else {
                            ArrayList<Point> interList = new ArrayList<>();
                            interList.add(UCR0.interPoint);
                            interList.add(UCR1.interPoint);
                            ArrayList<Point> pointsList = UCR0.interHouse.centerPointList.fixToLeftTopPointsList();
                            Poly poly = PointsSplitor.splitSelfUnclosedArea(interList, pointsList, true);
                            if (poly != null && !poly.isEmpty()) {
                                remove(house);
                                House house1 = add(poly);
                                PolyM.put(PolyM.newCreateIndex(), poly); // 存入缓存
                            }
                        }
                    }
                    // 非闭合房间，相交同一个断墙则为自身闭合
                    else {
                        ArrayList<Point> interPointsList = new ArrayList<>();
                        interPointsList.add(UCR0.interPoint);
                        interPointsList.add(UCR1.interPoint);
                        ArrayList<Point> checkPointsList = UCR0.interHouse.centerPointList.getPointsList();
                        Poly poly = PointsSplitor.splitSelfUnclosedArea(interPointsList, checkPointsList, false);
                        if (poly != null && !poly.isEmpty()) {
                            // 移除房间
                            remove(house);
                            remove(interHouse);
                            // 新建房间
                            PointList pointList = PolyE.toPointList(poly);
                            add(pointList);
                            PolyM.put(PolyM.newCreateIndex(), PolyE.toPolyDefault(pointList)); // 存入缓存
                        }
                    }
                }
                // 相交于两个不同房间
                else {
                    // 相交于两个闭合房间
                    if (UCR0.interHouse.isWallClosed && UCR1.interHouse.isWallClosed) {
                        // 判断两个房间是否在同一个区域内
                        Poly p1 = PolyM.getPolyAtPoly(UCR0.interHouse.centerPointList);
                        Poly p2 = PolyM.getPolyAtPoly(UCR1.interHouse.centerPointList);
                        // 同一组合区域
                        if (p1 != null && p2 != null && p1.equals(p2)) {
                            ArrayList<Point> selfList = new ArrayList<>();
                            selfList.add(UCR0.interPoint);
                            selfList.add(UCR1.interPoint);
                            Poly poly = PointsSplitor.splitSelfUnclosedArea(selfList, PolyE.toPointList(p1).fixToLeftTopPointsList(), true);
                            if (poly != null && !poly.isEmpty()) {
                                // 移除房间
                                remove(house);
                                // 新建房间
                                House house1 = add(poly);
                                PolyM.put(PolyM.newCreateIndex(), poly);
                            }
                        }
                        // 其他
                        else {
                            PolyUM.put(house.hashCode() + "", PolyE.toPolyDefault(house.centerPointList));
                        }
                    }
                    // 相交于一个闭合房间、一个未闭合房间
                    else if ((UCR0.interHouse.isWallClosed && !UCR1.interHouse.isWallClosed) ||
                            (!UCR0.interHouse.isWallClosed && UCR1.interHouse.isWallClosed)) {
                        // 与未闭合房间组合
                        ArrayList<Point> selfPointsList = new ArrayList<>();
                        selfPointsList.add(UCR0.interPoint);
                        selfPointsList.add(UCR1.interPoint);
                        Line validLine = new Line(selfPointsList.get(0).copy(), selfPointsList.get(1).copy());
                        boolean isUCR0Closed = UCR0.interHouse.isWallClosed;
                        ArrayList<Point> needInterUnionList = isUCR0Closed ? UCR1.interHouse.centerPointList.getPointsList()
                                : UCR0.interHouse.centerPointList.getPointsList();
                        ArrayList<Point> unionUncloseList = PointsSplitor.polyUncloseHouses(selfPointsList, needInterUnionList);
                        // 与闭合房间切割
                        House closeHouse = isUCR0Closed ? UCR0.interHouse : UCR1.interHouse;
                        House uncloseHouse = isUCR0Closed ? UCR1.interHouse : UCR0.interHouse;
                        PointList closePointList = closeHouse.centerPointList;
                        // 内部线段墙体
                        if (PointList.pointRelationToPolygon(closePointList.getPointsList(), validLine.getCenter()) == 1) {
                            ArrayList<Poly> poliesList = PointsSplitor.splitLineAreaWithCloseArea(PolyE.toPolyDefault(unionUncloseList)
                                    , PolyE.toPolyDefault(closePointList));
                            if (poliesList != null && poliesList.size() > 0) {
                                // 移除房间
                                remove(house);
                                remove(uncloseHouse);
                                remove(closeHouse);
                                // 新建房间
                                for (Poly poly : poliesList) {
                                    add(poly);
                                    PolyM.put(PolyM.newCreateIndex(), poly);
                                }
                            }
                        }
                        // 外部线段墙体
                        else {
                            // 获取闭合房间所属区域
                            Poly atUnionPoly = PolyM.getPolyAtPoly(closePointList);
                            Poly poly = PointsSplitor.splitSelfUnclosedArea(unionUncloseList,
                                    PolyE.toPointList(atUnionPoly).fixToLeftTopPointsList(), true);
                            if (poly != null && !poly.isEmpty()) {
                                // 移除房间
                                remove(house);
                                remove(uncloseHouse);
                                // 新建房间
                                add(poly);
                                PolyM.put(PolyM.newCreateIndex(), poly);
                            }
                        }
                    }
                    // 相交于两个未闭合的房间
                    else {
                        // 相较于两个未闭合房间的端点
                        if (UCR0.isInterHouseSidePoint && UCR1.isInterHouseSidePoint) {
                            ArrayList<Point> selfPointsList = new ArrayList<>();
                            selfPointsList.add(UCR0.interPoint);
                            selfPointsList.add(UCR1.interPoint);
                            ArrayList<Point> polyPointsList = PointsSplitor.polyUncloseHouses(selfPointsList,
                                    UCR0.interHouse.centerPointList.getPointsList());
                            polyPointsList = PointsSplitor.polyUncloseHouses(polyPointsList == null ? selfPointsList : polyPointsList,
                                    UCR1.interHouse.centerPointList.getPointsList());
                            if (polyPointsList != null && polyPointsList.size() > 0) {
                                // 移除房间
                                remove(UCR0.interHouse);
                                remove(UCR1.interHouse);
                                // 替换房间点
                                if (house instanceof NormalHouse) {
                                    ((NormalHouse) house).setCenterPointList(polyPointsList, false);
                                    PolyUM.put(house.hashCode() + "", PolyE.toPolyDefault(polyPointsList));
                                    checkSpecialUncloseUnionResult(house);
                                }
                            }
                        }
                        // 其他情况暂不处理(TODO 考虑后期添加断墙非端点组合切割去除部分数据操作)
                        else {
                            PolyUM.put(house.hashCode() + "", PolyE.toPolyDefault(house.centerPointList));
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * TODO 三个未闭合线段组合后，进一步检测是否首尾有相交切割组合
     *
     * @param house
     */
    private void checkSpecialUncloseUnionResult(House house) {
        if (house == null)
            return;
        try {
            // 检测房间是否在某个房间内部
            Point selfCheck = house.centerPointList.getIndexAt(1);
            Point selfBegain = house.centerPointList.getIndexAt(0);
            Point selfEnd = house.centerPointList.getIndexAt(house.centerPointList.size() - 1);
            House checkInnerHouse = null;
            House begainHouse = null;
            House endHouse = null;
            // 遍历查找内部房间、相交房间
            for (int i = 0; i < housesList.size(); i++) {
                House house1 = housesList.get(i);
                if (house1.isWallClosed && !house1.equals(house)) {
                    ArrayList<Point> pointsList = house1.centerPointList.getPointsList();
                    // 检测所在房间
                    int inner = PointList.pointRelationToPolygon(pointsList, selfCheck);
                    if (inner == 1) {
                        checkInnerHouse = house1;
                    }
                    // 检测首尾点是否与其他房间连接
                    Point checkBegain = house1.adsorbLine(selfBegain);
                    if (checkBegain != null) {
                        begainHouse = house1;
                    }
                    Point checkEnd = house1.adsorbLine(selfEnd);
                    if (checkEnd != null) {
                        endHouse = house1;
                    }
                }
            }
            // 在一个房间内部
            if (checkInnerHouse != null) {
                ArrayList<Poly> poliesList = PointsSplitor.splitLineAreaWithCloseArea(PolyE.toPolyDefault(house.centerPointList)
                        , PolyE.toPolyDefault(checkInnerHouse.centerPointList));
                if (poliesList != null) {
                    // 移除房间
                    remove(house);
                    // 新建房间
                    for (Poly poly : poliesList) {
                        add(poly);
                        PolyM.put(PolyM.newCreateIndex(), poly);
                    }
                }
            }
            // 在外部
            else {
                // 首尾都有相交房间
                if (begainHouse != null && endHouse != null) {
                    if (begainHouse.isWallClosed && endHouse.isWallClosed) {
                        Poly begainAtPoly = PolyM.getPolyAtPoly(begainHouse.centerPointList);
                        Poly endAtPoly = PolyM.getPolyAtPoly(endHouse.centerPointList);
                        if (begainAtPoly != null && endAtPoly != null && begainAtPoly.equals(endAtPoly)) {
                            Poly poly = PointsSplitor.splitSelfUnclosedArea(house.centerPointList.getPointsList()
                                    , PolyE.toPointList(begainAtPoly).fixToLeftTopPointsList(), true);
                            if (poly != null) {
                                // 移除房间
                                remove(house);
                                // 新建
                                add(poly);
                                PolyM.put(PolyM.newCreateIndex(), poly);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取墙体所在的房间
     *
     * @param ucr0
     * @param ucr1
     * @param onLinePoint
     * @return
     */
    private House getInnerLineAtHouse(UncloseCheckResult ucr0, UncloseCheckResult ucr1, Point onLinePoint) {
        if (ucr0 == null || ucr1 == null || onLinePoint == null)
            return null;
        House house = ucr0.interHouse.isPointInner(onLinePoint) ? ucr0.interHouse : null;
        if (house != null)
            return house;
        house = ucr1.interHouse.isPointInner(onLinePoint) ? ucr1.interHouse : null;
        if (house != null)
            return house;
        return null;
    }

    /**
     * 检测点与房间相交结果
     *
     * @param housesList
     * @param point
     * @return
     */
    private ArrayList<House> checkInterList(ArrayList<House> housesList, Point point) {
        if (housesList == null || housesList.size() == 0 || point == null)
            return null;
        ArrayList<House> retList = new ArrayList<>();
        try {
            for (House house : housesList) {
                // 先检测点吸附
                Point point1 = house.centerPointList.correctAdsorbPoint(point, 2);
                if (point1 != null) {
                    retList.add(house);
                }
                // 在检测线段吸附
                if (point1 == null) {
                    ArrayList<Line> linesList = house.centerPointList.toLineList();
                    for (Line line : linesList) {
                        point1 = line.getAdsorbPoint(point.x, point.y, 2);
                        if (point1 != null) {
                            retList.add(house);
                        }
                    }
                }
            }
            if (retList.size() == 0)
                retList = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retList;
    }

    /**
     * 吸附处理
     *
     * @param check
     * @return
     */
    public Point checkAdsorb(Point check) {
        if (check == null)
            return null;
        try {
            if (housesList != null) {
                for (int i = housesList.size() - 1; i > -1; i--) { // 先检测点
                    House house = housesList.get(i);
                    PointList pointList = house.centerPointList;
                    if (pointList != null) {
                        Point adsorb = pointList.correctAdsorbPoint(check, 96);
                        if (adsorb != null) {
                            return adsorb;
                        }
                    }
                }
                for (int i = housesList.size() - 1; i > -1; i--) { // 检测线段
                    House house = housesList.get(i);
                    PointList pointList = house.centerPointList;
                    if (pointList != null) {
                        LineList lineList = new LineList(house.isWallClosed ? pointList.toLineList() : pointList.toNotClosedLineList());
                        Point adsorb = lineList.correctNearlyPoint(check.toLJ3DPoint());
                        if (adsorb != null) {
                            return adsorb;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 墙体绘制时，端点对齐检测
     *
     * @param point      实时移动点或起点
     * @param checkHouse 被检测房间
     * @return
     */
    public Point checkUpAlign(Point point, House checkHouse) {
        if (point == null || housesList == null)
            return null;
        Point ret = point.copy();
        for (int i = housesList.size() - 1; i > -1; i--) {
            House house = housesList.get(i);
            if (!(house.equals(checkHouse))) {
                if (house.centerPointList != null) {
                    ArrayList<Point> pointsList = house.centerPointList.getPointsList();
                    for (Point point1 : pointsList) {
                        double absX = Math.abs(point1.x - point.x);
                        if (absX <= 24) {
                            ret.x = point1.x;
                        }
                        double absY = Math.abs(point1.y - point.y);
                        if (absY <= 24) {
                            ret.y = point1.y;
                        }
                    }
                }
            }
        }
        return ret;
    }

    /**
     * 获取房间列表
     */
    public ArrayList<House> getHousesList() {
        ArrayList<House> copyHouseList = new ArrayList<>();
        try {
            int size = housesList.size();
            for (int i = 0; i < size; i++) {
                if (i < housesList.size()) {
                    House house = housesList.get(i);
                    if (house != null && house.centerPointList != null)
                        copyHouseList.add(house);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return copyHouseList;
    }

    /*****************************************
     *  房间外立面处理
     * ***************************************/
    private ArrayList<WallFacade> wallOuterFacadesList;

    public void initWallOuterFacades() {
        wallOuterFacadesList.clear();
        HashMap<Integer, Poly> closeMaps = PolyM.getPoliesMap();
        if (closeMaps != null && closeMaps.size() > 0) {
            Iterator<Map.Entry<Integer, Poly>> iterator = closeMaps.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Integer, Poly> entry = iterator.next();
                String uuid = UUID.randomUUID().toString();
                ArrayList<Point> outerList = PolyE.toPointList(entry.getValue()).offsetList(true, 12);
                ArrayList<Line> linesList = new PointList(outerList).toLineList();
                if (linesList != null && linesList.size() > 0) {
                    for (Line line : linesList) {
                        wallOuterFacadesList.add(new WallFacade(WallFacade.FLAG_OUTER, 1, line.toPointList(), uuid));
                    }
                }
            }
        }
    }

    public ArrayList<WallFacade> getWallOuterFacadesList() {
        return wallOuterFacadesList;
    }

    /*****************************************
     *  拖动吸附处理
     * ***************************************/

    /**
     * Author by HEKE
     *
     * @time 2018/8/27 16:33
     * TODO: 拖动吸附处理结果对象
     */
    public class DragAdsorbRet {

        public House atHouse;
        public Line originLine;
        public Line adsorbLine;
        public Point point;

        public DragAdsorbRet() {
            super();
        }

        public DragAdsorbRet(House atHouse, Line originLine, Line adsorbLine, Point point) {
            this.atHouse = atHouse;
            this.originLine = originLine;
            this.adsorbLine = adsorbLine;
            this.point = point;
        }
    }

    /**
     * 检测模型的吸附
     *
     * @param furniture 拖动的模型对象
     * @return 返回吸附结果对象
     */
    public DragAdsorbRet checkModelAdsorb(BaseCad furniture) {
        if (furniture == null || housesList == null)
            return null;
        try {
            FurTypes furTypes = furniture.furTypes;
            Point check = furniture.point;
            boolean isDoorOrWindow = (furTypes.ordinal() < FurTypes.GENERAL_L3D.ordinal());
            for (House house : housesList) {
                ArrayList<Line> linesList = null;
                if (isDoorOrWindow) {
                    linesList = house.isWallClosed ? house.centerPointList.toLineList() : house.centerPointList.toNotClosedLineList();
                } else {
                    if (house.isWallClosed) {
                        linesList = new PointList(house.innerPointList.offsetList(false, 1.0d)).toLineList();
                    }
                }
                if (linesList != null) { // 非门窗家具断墙不需要吸附操作
                    for (Line line : linesList) {
                        Line intentation = line.toIndentationLine(24);
                        if (intentation != null) {
                            Point adsorb = intentation.getAdsorbPoint(check.x, check.y, 50);
                            if (adsorb != null) {
                                if (isDoorOrWindow) { // 门窗类吸附
                                    return new DragAdsorbRet(house, line, intentation, adsorb);
                                } else { // 家具吸附，根据线段角度
                                    adsorb = checkFurnitureOffsetPosition(house, intentation, adsorb, furniture);
                                    if (adsorb != null)
                                        return new DragAdsorbRet(house, line, intentation, adsorb);
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 求出内部家具的吸附点
     *
     * @param house     所在房间
     * @param line      吸附线段
     * @param point     当前吸附点
     * @param furniture 家具
     * @return 家具模型的中心吸附点
     */
    private Point checkFurnitureOffsetPosition(House house, Line line, Point point, BaseCad furniture) {
        if (house == null || line == null || point == null || furniture == null)
            return null;
        // 根据家具的宽度从新计算吸附点
        ArrayList<Point> lepsList = PointList.getRotateLEPS(line.getAngle() + 90.0d, furniture.thickness, point);
        // 判断哪个点与交点的连线会与房间墙中线相交
        ArrayList<Line> centerList = house.centerPointList.toLineList();
        Line line1 = new Line(lepsList.get(1), point.copy());
        boolean line1Inter = false;
        for (Line l : centerList) {
            if (l.getLineIntersectedPoint(line1) != null) {
                line1Inter = true;
                break;
            }
        }
        // 判断返回点
        Point ret = null;
        if (line1Inter) {
            ret = lepsList.get(0);
        } else {
            ret = lepsList.get(1);
        }
        return ret;
    }

    /*******************************************************
     * TODO 家具处理
     * *****************************************************/

    /**
     * 移除家具
     *
     * @param furniture
     */
    public void remove(BaseCad furniture) {
        if (furniture == null)
            return;
        if (furnituresList == null || furnituresList.size() == 0)
            return;
        ArrayList<BaseCad> removedList = new ArrayList<>();
        for (BaseCad baseCad : furnituresList) {
            if (!baseCad.equals(furniture)) {
                removedList.add(baseCad);
            }
        }
        furnituresList = removedList;
        refreshRender();
    }

    /**
     * 复制模型
     *
     * @param furniture
     */
    public void copy(BaseCad furniture) {
        if (furniture == null)
            return;
        FurTypes furTypes = furniture.furTypes;
        double angle = furniture.angle;
        double width = furniture.thickness;
        double xlong = furniture.xlong;
        Point point = furniture.point.copy();
        BaseCad copy = null;
        // 窗
        if (furTypes == FurTypes.SIMPLE_WINDOW) {
            copy = new SimpleWindow(angle, width, xlong, point, furTypes, furniture.furniture);
        }
        // 单开门
        else if (furTypes == FurTypes.SINGLE_DOOR) {
            copy = new SingleDoor(angle, width, xlong, point, furTypes, furniture.furniture);
        }
        // 其他家具
        else if (furTypes == FurTypes.GENERAL_L3D) {
            copy = new GeneralFurniture(angle, width, xlong, point, furTypes, furniture.furniture);
        }
        addFurniture(copy);
    }

    /**
     * 镜像
     *
     * @param furniture
     */
    public void mirror(BaseCad furniture) {
        if (furniture == null)
            return;
        furniture.mirror();
    }

    /**
     * 替换
     *
     * @param furniture
     * @param furniture
     */
    public void relace(BaseCad furniture, Furniture fur) {
        if (furniture == null)
            return;
        furniture.setFurniture(fur);
    }

    /**
     * 删除
     *
     * @param furniture
     */
    public void delete(BaseCad furniture) {
        remove(furniture);
    }

    /**
     * 增加
     *
     * @param furniture
     */
    public void addFurniture(BaseCad furniture) {
        if (furniture == null)
            return;
        // 增加渲染模型对象
        furnituresList.add(furniture);
        // 创建模型所在位置数据对象
        furniture.furniture.put(new FurnitureMatrixs(furniture.point, 0, 0, (float) furniture.angle
                , 0.1f, 0.1f, 0.1f, (float) furniture.point.x, (float) furniture.point.y, 0.0f));
        // 保存至总数据列表
        putFurniture(furniture);
        refreshRender();
    }

    /**
     * 家具总数据列表
     */
    private ArrayList<Furniture> furnitureArrayList;

    /**
     * 存入家具信息
     *
     * @param baseCad
     */
    private void putFurniture(BaseCad baseCad) {
        Furniture save = baseCad.furniture;
        Furniture existedFur = null;
        for (int i = 0; i < furnitureArrayList.size(); i++) {
            Furniture furniture = furnitureArrayList.get(i);
            if (save.materialCode.equals(furniture.materialCode)) {
                existedFur = furniture;
                break;
            }
        }
        if (existedFur == null) {
            furnitureArrayList.add(save);
        } else {
            if (!existedFur.equals(save)) {
                existedFur.putAll(save.getFurnitureMatrixsList());
            }
        }
    }

    // 获取所有家具
    public ArrayList<BaseCad> getFurnituresList() {
        return furnituresList;
    }

    // 获取用于渲染的所有家具
    public ArrayList<Furniture> getFurnitureArrayList() {
        return furnitureArrayList;
    }

    /**
     * 获取当前进入房间的内部一点
     */
    public Point getEnterHouse3DInnerPosition() {
        if (housesList == null || housesList.size() == 0)
            return new Point(0, 0);
        // 查询客餐厅
        House enterHouse = null;
        for (House house : housesList) {
            if (house.isWallClosed) {
                if (house.houseName.getNameData().name.contains("客餐厅")) {
                    enterHouse = house;
                    break;
                }
            }
        }
        if (enterHouse == null)
            enterHouse = housesList.get(0);
        if (enterHouse.isWallClosed) {
            return enterHouse.innerPointList.getInnerValidPoint(false);
        } else {
            return enterHouse.innerPointList.get(0);
        }
    }

    // 刷新画布
    private void refreshRender() {
        ((OrderKingApplication) mContext.getApplicationContext()).render();
    }

    // 清空房间数据列表
    public void laterClearWhen3DViewsClearFinished() {
        housesList.clear();
        furnituresList.clear();
        furnitureArrayList.clear();
        wallOuterFacadesList.clear();
    }

    /**
     * 清空数据
     */
    public void clear() {
        // 释放当前三维绑定缓存数据
        ((OrderKingApplication) mContext.getApplicationContext()).release3DViews();
        // 清空房间组合信息
        PolyM.clear();
        // 清空普通房间缓存信息
        PolyUM.clear();
        // 清除材质缓存
        TexturesCache.release();
        refreshRender();
    }

}
