package com.lejia.mobile.orderking.hk3d.datas;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;

import com.lejia.mobile.orderking.bases.OrderKingApplication;
import com.lejia.mobile.orderking.hk3d.classes.CloseHouseCheckResult;
import com.lejia.mobile.orderking.hk3d.classes.Point;
import com.lejia.mobile.orderking.hk3d.classes.PointList;
import com.lejia.mobile.orderking.hk3d.classes.PolyE;
import com.lejia.mobile.orderking.hk3d.classes.PolyIntersectedResult;
import com.lejia.mobile.orderking.hk3d.classes.PolyM;
import com.seisw.util.geom.Poly;

import java.util.ArrayList;

/**
 * Author by HEKE
 *
 * @time 2018/7/16 15:39
 * TODO: 总房间数据管理
 */
public class HouseDatasManager {

    private Context mContext;
    private ArrayList<House> housesList; // 矩形房间

    public HouseDatasManager(Context context) {
        mContext = context;
        this.housesList = new ArrayList<>();
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
    public void add(PointList pointList) {
        if (pointList == null || pointList.invalid())
            return;
        NormalHouse normalHouse = new NormalHouse(mContext, pointList, 24);
        housesList.add(normalHouse);
    }

    /**
     * 新增房间
     *
     * @param poly
     */
    public void add(Poly poly) {
        add(PolyE.toPointList(poly));
    }

    /**
     * 新增房间
     *
     * @param pointsList
     */
    public void add(ArrayList<Point> pointsList) {
        add(new PointList(pointsList));
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
        return housesList.remove(house);
    }

    /**
     * 检测区域切割问题
     *
     * @param checkHouse
     */
    @SuppressLint("StaticFieldLeak")
    public void gpcClosedCheck(House checkHouse) {
        // 空或者未闭合房间直接返回
        if (checkHouse == null || !checkHouse.isWallClosed)
            return;
        // 手指弹起后，如果房间的面积小于1平方米，直接删除
        if (checkHouse.centerPointList.area() < 1) {
            housesList.remove(checkHouse);
            refreshRender();
            return;
        }
        new AsyncTask<House, Integer, CloseHouseCheckResult>() {
            @Override
            protected CloseHouseCheckResult doInBackground(House... houses) {
                CloseHouseCheckResult closeHouseCheckResult = null;
                long begainTime = System.currentTimeMillis();
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
                // 测试耗时
                System.out.println("####### Take time : " + (System.currentTimeMillis() - begainTime) + "  ms !");
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
        return housesList;
    }

    // 刷新画布
    private void refreshRender() {
        ((OrderKingApplication) mContext.getApplicationContext()).render();
    }

    // 清空房间数据列表
    public void laterClearWhen3DViewsClearFinished() {
        housesList.clear();
    }

    /**
     * 清空数据
     */
    public void clear() {
        // 释放当前三维绑定缓存数据
        ((OrderKingApplication) mContext.getApplicationContext()).release3DViews();
        // 清空房间组合信息
        PolyM.clear();
        // 清除材质缓存
        TexturesCache.release();
        refreshRender();
    }

}
