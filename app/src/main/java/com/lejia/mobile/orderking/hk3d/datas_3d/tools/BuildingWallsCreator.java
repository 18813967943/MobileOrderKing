package com.lejia.mobile.orderking.hk3d.datas_3d.tools;

import com.lejia.mobile.orderking.hk3d.classes.Point;
import com.lejia.mobile.orderking.hk3d.classes.PointList;
import com.lejia.mobile.orderking.hk3d.datas_2d.House;
import com.lejia.mobile.orderking.hk3d.datas_2d.HouseDatasManager;
import com.lejia.mobile.orderking.hk3d.datas_3d.classes.BuildingGround;
import com.lejia.mobile.orderking.hk3d.datas_3d.classes.BuildingWall;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Author by HEKE
 *
 * @time 2018/11/7 12:00
 * TODO: 墙体创建线程
 */
public class BuildingWallsCreator extends Thread {

    private HouseDatasManager houseDatasManager; // 平面详细数据管理对象
    private OnBuildingWallsChangeListener onBuildingWallsChangeListener; // 回调接口
    private HashMap<Integer, ArrayList<BuildingWall>> buildingWallsMaps; // 返回结果
    private boolean executting; // 正在执行操作标志
    private boolean completed; // 完成标志
    private boolean requestExcute; // 请求执行创建操作

    private Executor executor; // 执行具体操作线程

    public BuildingWallsCreator(HouseDatasManager houseDatasManager, OnBuildingWallsChangeListener onBuildingWallsChangeListener) {
        this.houseDatasManager = houseDatasManager;
        this.onBuildingWallsChangeListener = onBuildingWallsChangeListener;
        this.buildingWallsMaps = new HashMap<>();
        this.executor = new Executor();
        this.executor.start();
    }

    /**
     * 强行中断操作
     */
    public void forceInterrupt() {
        if (executor != null)
            executor.forceInterrupt();
    }

    /**
     * 是否正在执行
     */
    public boolean isExecutting() {
        return executting;
    }

    /**
     * 是否执行完成
     */
    public boolean isCompleted() {
        return completed;
    }

    /**
     * 执行请求创建操作
     */
    public void requestExcute() {
        this.requestExcute = true;
    }

    @Override
    public void run() {
        super.run();
        while (true) {
            try {
                // 每隔100ms检测一次执行操作
                Thread.sleep(100);
                // 已完成或中断操作
                if (completed) {
                    completed = false;
                    executor = null;
                }
                // 修复中断后的执行线程
                if (executor == null) {
                    executor = new Executor();
                    executor.start();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Author by HEKE
     *
     * @time 2018/11/7 12:12
     * TODO: 内部具体执行线程
     */
    private class Executor extends Thread {

        private boolean forceInterrupt;

        public void forceInterrupt() {
            this.forceInterrupt = true;
        }

        @Override
        public void run() {
            super.run();
            while (true) {
                try {
                    // 延迟50ms钟执行
                    Thread.sleep(50);
                    if (!executting && requestExcute) {
                        executting = true;
                        requestExcute = false;
                        // TODO 具体执行操作
                        ArrayList<House> houseArrayList = houseDatasManager.getHousesList();
                        if (houseArrayList != null && houseArrayList.size() > 0) {
                            buildingWallsMaps.clear();
                            // 楼层信息
                            Cell cell = CellsRecord.get(CellsRecord.current_edit_cell);
                            // 保存数据对象
                            ArrayList<BuildingWall> insideBuildingWallsList = new ArrayList<>();
                            ArrayList<BuildingWall> outsideBuildingWallsList = new ArrayList<>();
                            ArrayList<BuildingWall> roofBuildingWallsList = new ArrayList<>();
                            ArrayList<BuildingWall> topsideBuildingWallsList = new ArrayList<>();
                            boolean broken = false;
                            // 遍历创建
                            for (int i = 0; i < houseArrayList.size(); i++) {
                                House house = houseArrayList.get(i);
                                PointList innerList = new PointList(house.innerPointList.antiClockwise());
                                PointList centerList = new PointList(house.centerPointList.antiClockwise());
                                PointList outerList = new PointList(house.outerPointList.antiClockwise());
                                // 中断检测
                                if (broken) {
                                    break;
                                } else {
                                    if (forceInterrupt) {
                                        forceInterrupt = false;
                                        broken = true;
                                        break;
                                    }
                                }
                                if (house.isWallClosed) {
                                    // 地面
                                    BuildingGround buildingGround = new BuildingGround(house.ground);
                                    // 墙体
                                    for (int j = 0; j < innerList.size(); j++) {
                                        Point inow = innerList.getIndexAt(j);
                                        Point cnow = centerList.getIndexAt(j);
                                        Point inext = null;
                                        Point cnext = null;
                                        if (j == innerList.size() - 1) {
                                            inext = innerList.getIndexAt(0);
                                            cnext = centerList.getIndexAt(0);
                                        } else {
                                            inext = innerList.getIndexAt(j + 1);
                                            cnext = centerList.getIndexAt(j + 1);
                                        }
                                        // 内墙体
                                        ArrayList<Point> insideList = new ArrayList<>();
                                        insideList.add(inow.copy());
                                        insideList.add(inext.copy());
                                        BuildingWall insideBuildingWall = new BuildingWall(cell, BuildingWall.Type.INSIDE, insideList);
                                        insideBuildingWallsList.add(insideBuildingWall);
                                        // 顶面墙厚面
                                        ArrayList<Point> topsideList = new ArrayList<>();
                                        topsideList.add(inow.copy());
                                        topsideList.add(inext.copy());
                                        topsideList.add(cnext.copy());
                                        topsideList.add(cnow.copy());
                                        BuildingWall topsideBuildingWall = new BuildingWall(cell, BuildingWall.Type.TOPSIDE, topsideList);
                                        topsideBuildingWallsList.add(topsideBuildingWall);
                                        // 中断检测
                                        if (forceInterrupt) {
                                            forceInterrupt = false;
                                            broken = true;
                                            break;
                                        }
                                    }
                                    // 墙顶面
                                    ArrayList<Point> roofList = innerList.copy();
                                    BuildingWall roofBuildingWall = new BuildingWall(cell, BuildingWall.Type.ROOF, roofList);
                                    roofBuildingWallsList.add(roofBuildingWall);
                                } else {
                                    for (int j = 0; j < innerList.size() - 1; j++) {
                                        Point inow = innerList.getIndexAt(j);
                                        Point inext = innerList.getIndexAt(j + 1);
                                        Point onow = outerList.getIndexAt(j);
                                        Point onext = outerList.getIndexAt(j + 1);
                                        // 内墙体
                                        ArrayList<Point> insideList = new ArrayList<>();
                                        insideList.add(inow.copy());
                                        insideList.add(inext.copy());
                                        BuildingWall insideBuildingWall = new BuildingWall(cell, BuildingWall.Type.INSIDE, insideList);
                                        insideBuildingWallsList.add(insideBuildingWall);
                                        // 顶面墙厚面
                                        ArrayList<Point> topsideList = new ArrayList<>();
                                        topsideList.add(inow.copy());
                                        topsideList.add(inext.copy());
                                        topsideList.add(onext.copy());
                                        topsideList.add(onow.copy());
                                        BuildingWall topsideBuildingWall = new BuildingWall(cell, BuildingWall.Type.TOPSIDE, topsideList);
                                        topsideBuildingWallsList.add(topsideBuildingWall);
                                        // 外墙面
                                        ArrayList<Point> outsideList = new ArrayList<>();
                                        outsideList.add(onow.copy());
                                        outsideList.add(onext.copy());
                                        BuildingWall outsideBuildingWall = new BuildingWall(cell, BuildingWall.Type.OUTSIDE, outsideList);
                                        outsideBuildingWallsList.add(outsideBuildingWall);
                                        // 中断检测
                                        if (forceInterrupt) {
                                            forceInterrupt = false;
                                            broken = true;
                                            break;
                                        }
                                    }
                                }

                            }
                            // 中断操作
                            if (broken) {
                                buildingWallsMaps.clear();
                                completed = true;
                                executting = false;
                                // 重启创建
                                requestExcute();
                                break;
                            }
                            // 存入数据，并回调
                            buildingWallsMaps.put(BuildingWall.Type.INSIDE, insideBuildingWallsList);
                            buildingWallsMaps.put(BuildingWall.Type.OUTSIDE, outsideBuildingWallsList);
                            buildingWallsMaps.put(BuildingWall.Type.TOPSIDE, topsideBuildingWallsList);
                            buildingWallsMaps.put(BuildingWall.Type.ROOF, roofBuildingWallsList);
                            if (onBuildingWallsChangeListener != null) {
                                onBuildingWallsChangeListener.completed(buildingWallsMaps);
                            }
                            // 完成
                            executting = false;
                            completed = true;
                            break;
                        } else {
                            executting = false;
                            completed = true;
                            break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Author by HEKE
     *
     * @time 2018/11/7 12:06
     * TODO: 创建墙体时监听接口
     */
    public interface OnBuildingWallsChangeListener {
        void completed(HashMap<Integer, ArrayList<BuildingWall>> buildingWallsMaps);
    }

}
