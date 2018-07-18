package com.lejia.mobile.orderking.hk3d.datas;

import android.content.Context;
import android.widget.Toast;

import com.lejia.mobile.orderking.R;
import com.lejia.mobile.orderking.bases.OrderKingApplication;
import com.lejia.mobile.orderking.hk3d.classes.HouseIntersectedResult;
import com.lejia.mobile.orderking.hk3d.classes.Point;
import com.lejia.mobile.orderking.hk3d.classes.PointList;
import com.lejia.mobile.orderking.hk3d.classes.PolyE;
import com.lejia.mobile.orderking.hk3d.classes.PolyM;
import com.seisw.util.geom.Poly;

import java.util.ArrayList;
import java.util.Map;

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
        long begainTime = System.currentTimeMillis();
        // 第一个闭合区域，直接组合
        if (housesList.size() == 1) {
            PolyM.put(PolyM.newCreateIndex(), PolyE.toPolyDefault(checkHouse.centerPointList));
        } else {
            // 获取所有相交的列表集
            ArrayList<HouseIntersectedResult> intersectedResultsList = new ArrayList<>();
            for (House house : housesList) {
                if (house.isWallClosed) {
                    HouseIntersectedResult result = checkHouse.getHouseIntersetedResult(house);
                    if (result != null)
                        intersectedResultsList.add(result);
                }
            }
            if (intersectedResultsList.size() > 0) {
                // 如果有重合，直接去除
                if (checkHouse.isOverlap) {
                    housesList.remove(checkHouse);
                    Toast.makeText(mContext, R.string.house_overlap, Toast.LENGTH_SHORT).show(); // 提示无效操作
                }
                // 未重合，相交分割房间
                else {
                    // 获取相交房间的所属组合区域，并先从渲染数据中移除当前房间与相交房间
                    remove(checkHouse);
                    for (HouseIntersectedResult ret : intersectedResultsList) {
                        Map.Entry<Integer, Poly> atEntry = PolyM.get(ret.house);
                        ret.atPolyEntry = atEntry;
                        // 从渲染缓存中移除相交房间
                        remove(ret.house);
                    }
                    // 重新组合替换组合区域
                    Poly checkPoly = PolyE.toPolyDefault(checkHouse.centerPointList);
                    Poly newPoly = null;
                    for (HouseIntersectedResult ret : intersectedResultsList) {
                        if (ret.atPolyEntry != null) {
                            if (newPoly == null) {
                                newPoly = ret.atPolyEntry.getValue().union(checkPoly);
                                PolyM.removePoly(ret.atPolyEntry.getKey());
                            } else {
                                newPoly = newPoly.union(ret.atPolyEntry.getValue());
                                PolyM.removePoly(ret.atPolyEntry.getKey());
                            }
                            ret.atPolyEntry = null;
                        }
                    }
                    PolyM.put(PolyM.newCreateIndex(), newPoly);
                    // 新增相交的房间及切割后的相交房间
                    ArrayList<PointList> totalPointList = new ArrayList<>();
                    for (HouseIntersectedResult ret : intersectedResultsList) {
                        // 切割后的区域
                        add(ret.differencePointList);
                        // 相交区域
                        ArrayList<PointList> pointLists = ret.pointListsList;
                        if (pointLists != null && pointLists.size() > 0) {
                            totalPointList.addAll(pointLists);
                            for (PointList pointList : pointLists) {
                                add(pointList);
                            }
                        }
                    }
                    // 新增自身切割后的房间
                    HouseIntersectedResult checkHouseResult = new HouseIntersectedResult(1, totalPointList, checkHouse);
                    if (checkHouseResult.differencePointList != null)
                        add(checkHouseResult.differencePointList);
                    // 释放数据
                    checkHouseResult.release();
                    for (HouseIntersectedResult ret : intersectedResultsList) {
                        ret.release();
                    }
                }
            }
            // 未与任何区域相交，直接加入新区域
            else {
                PolyM.put(PolyM.newCreateIndex(), PolyE.toPolyDefault(checkHouse.centerPointList));
            }
        }
        // 测试耗时
        System.out.println("####### Take time : " + (System.currentTimeMillis() - begainTime) + "  ms !");
        // 调用刷新
        refreshRender();
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

    /**
     * 清空数据
     */
    public void clear() {
        housesList.clear();
        PolyM.clear();
        refreshRender();
    }

}
