package com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.lejia.mobile.orderking.hk3d.classes.Point;
import com.lejia.mobile.orderking.hk3d.classes.PointList;
import com.lejia.mobile.orderking.hk3d.datas_2d.Area3D;
import com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.classes.PhyLogicalPackage;
import com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.classes.TilePlan;
import com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.classes.WaveMutliPlan;
import com.lejia.mobile.orderking.hk3d.gpc.OnTilesResultListener;
import com.lejia.mobile.orderking.utils.BitmapUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import geom.gpc.GPCConfig;
import geom.gpc.GPCManager;
import geom.gpc.OffsetCornerType;
import geom.gpc.OffsetLevel;
import geom.gpc.OffsetType;
import geom.gpc.TileInfoList;
import geom.gpc.WaveLine;

/**
 * Author by HEKE
 *
 * @time 2018/11/21 16:21
 * TODO: 波打线铺砖对象
 */
public class WaveLinesPave {

    private PointList pointList; // 铺砖围点
    private WaveMutliPlan waveMutliPlan; // 波打线资源数据对象
    private NormalPave normalPave; // 当前地面使用的普通砖铺贴数据对象
    private OnTilesResultListener onTilesResultListener; // 回调接口

    /**
     * 铺砖起始方向
     */
    public int direction;

    /**
     * 是否45°斜铺
     */
    public boolean skewTile;

    /**
     * 砖缝颜色
     */
    public int gapsColor;

    /**
     * 砖缝厚度
     */
    public float brickGap;

    /**
     * 随机纹理标志
     */
    public boolean randRotate;

    /**
     * 编码对应贴图位图集合
     */
    private HashMap<String, Bitmap> bitmapHashMap = new HashMap<>();

    /**
     * 铺砖结果集对象
     */
    private WLTileResult wlTileResult;

    /**
     * 中心铺砖围点
     */
    private ArrayList<Point> centerPointsList;

    /**
     * 每层波打线的起铺围点
     */
    private ArrayList<ArrayList<Point>> wavelinesCellsPointList = new ArrayList<>();

    public WaveLinesPave(PointList pointList, WaveMutliPlan waveMutliPlan, NormalPave normalPave, OnTilesResultListener onTilesResultListener) {
        this.pointList = pointList;
        this.waveMutliPlan = waveMutliPlan;
        this.normalPave = normalPave;
        this.onTilesResultListener = onTilesResultListener;
        this.brickGap = normalPave.getBrickGap();
        this.direction = normalPave.getDirection();
        this.gapsColor = normalPave.getGapsColor();
        this.skewTile = normalPave.isSkewTile();
        this.randRotate = normalPave.randRotate;
        tile();
    }

    /**
     * 根据瓷砖编码获取对应的贴图位图
     *
     * @param code
     * @return
     */
    public Bitmap getBitmap(String code) {
        return bitmapHashMap.get(code);
    }

    /**
     * 获取中心砖铺贴的瓷砖位图
     */
    public Bitmap getCenterBitmap() {
        return normalPave.getOriginBitmap();
    }

    // 获取砖缝颜色
    public int getGapsColor() {
        return gapsColor;
    }

    // 获取砖缝厚度
    public float getBrickGap() {
        return brickGap;
    }

    /**
     * 设置铺砖方向
     *
     * @param direction
     */
    public void setDirection(int direction) {
        if (this.direction == direction)
            return;
        this.direction = direction;
        tile();
    }

    /**
     * 设置斜铺
     *
     * @param skewTile
     */
    public void setSkewTile(boolean skewTile) {
        this.skewTile = skewTile;
        tile();
    }

    /**
     * 设置砖缝颜色
     *
     * @param color
     */
    public void setGapsColor(int color) {
        this.gapsColor = color;
        tile();
    }

    /**
     * 设置砖缝厚度
     *
     * @param brickGap
     */
    public void setBrickGap(float brickGap) {
        this.brickGap = brickGap;
        tile();
    }

    public int getDirection() {
        return direction;
    }

    public boolean isSkewTile() {
        return skewTile;
    }

    /**
     * 执行切割铺砖
     */
    @SuppressLint("StaticFieldLeak")
    private void tile() {
        new AsyncTask<String, Integer, WLTileResult>() {
            @Override
            protected WLTileResult doInBackground(String... params) {
                try {
                    // 优先释放之前加载的数据
                    if (wlTileResult != null) {
                        wlTileResult.release();
                        wlTileResult = null;
                    }
                    wlTileResult = new WLTileResult(pointList.getRectBox(), WaveLinesPave.this);
                    wavelinesCellsPointList.clear();
                    // 优先加载资源位图，运算出最内层瓷砖铺贴围点列表
                    float gap = brickGap;
                    ArrayList<Point> innerRingList = pointList.copy();
                    ArrayList<TilePlan> tilePlanArrayList = waveMutliPlan.tilePlanArrayList;
                    if (tilePlanArrayList == null || tilePlanArrayList.size() == 0) {
                        return null;
                    }
                    for (TilePlan tilePlan : tilePlanArrayList) {
                        ArrayList<PhyLogicalPackage> phyLogicalPackageArrayList = tilePlan.phyLogicalPackageArrayList;
                        for (PhyLogicalPackage phyLogicalPackage : phyLogicalPackageArrayList) {
                            float length = phyLogicalPackage.logicalTile.length;
                            float width = phyLogicalPackage.logicalTile.width;
                            Bitmap bitmap = BitmapUtils.createBitmapByXInfo(phyLogicalPackage.xInfo, length, width);
                            bitmapHashMap.put(phyLogicalPackage.tile.codeNum, bitmap);
                        }
                        // 往内偏置
                        float offset = phyLogicalPackageArrayList.get(0).logicalTile.width * 0.1f + gap;
                        innerRingList = new PointList(innerRingList).offsetList(false, offset);
                    }
                    // 中心砖铺贴
                    Bitmap centerTileBitmap = getCenterBitmap();
                    ArrayList<Point> innerFixedList = new PointList(innerRingList).fixToLeftTopPointsList();
                    centerPointsList = new PointList(innerFixedList).copy();
                    normalPave.setPointList(new PointList(centerPointsList));
                    GPCManager gpcManager = new GPCManager(new PointList(innerFixedList).toGeomPointList(), centerTileBitmap.getWidth(), centerTileBitmap.getHeight()
                            , gap, gap, direction, skewTile ? GPCConfig.TILT : GPCConfig.STRAIGHT);
                    String centerCode = normalPave.getNormalXInfo().materialCode;
                    // 实质砖
                    TileInfoList mTileInfoList = gpcManager.getTileInfoList();
                    createArea3D(mTileInfoList, centerCode, 0, false, false);
                    // 砖缝
                    TileInfoList xGapTileInfoList = gpcManager.getXGapInfoList();
                    TileInfoList yGapTileInfoList = gpcManager.getYGapInfoList();
                    createArea3D(xGapTileInfoList, centerCode, 0, false, true);
                    createArea3D(yGapTileInfoList, centerCode, 0, false, true);
                    // 倒序遍历铺贴波打线
                    ArrayList<Point> pointArrayList = innerRingList;
                    int size = tilePlanArrayList.size();
                    boolean wavelinesCellsLayout = size > 1;
                    for (int i = size - 1; i > -1; i--) {
                        TilePlan tilePlan = tilePlanArrayList.get(i);
                        // 获取打包数据对象
                        ArrayList<PhyLogicalPackage> phyLogicalPackageArrayList = tilePlan.phyLogicalPackageArrayList;
                        PhyLogicalPackage phyLogicalPackage = phyLogicalPackageArrayList.get(0);
                        float tileWidth = phyLogicalPackage.logicalTile.length * 0.1f;
                        float tileHeight = phyLogicalPackage.logicalTile.width * 0.1f;
                        // 获取波打线铺砖区域，自建砖缝区域
                        if (wavelinesCellsLayout) {
                            pointArrayList = new PointList(pointArrayList).offsetList(true, tileHeight + gap);
                            wavelinesCellsPointList.add(new PointList(pointArrayList).copy());
                        } else {
                            pointArrayList = pointList.copy();
                            ArrayList<Point> waveLineList = new PointList(pointArrayList).offsetList(false, tileHeight + gap);
                            wavelinesCellsPointList.add(new PointList(new PointList(waveLineList).copy()).fixToLeftTopPointsList());
                        }
                        createWaveLineOuterGaps(pointArrayList, phyLogicalPackage.tile.codeNum, tileHeight, gap);
                        // 波打线数据对象
                        WaveLine waveLine = new WaveLine(tileWidth, tileHeight, gap, OffsetLevel.SINGLE, OffsetType.OFFSET_IN,
                                (tilePlan.phy.size() == 1) ? OffsetCornerType.TILT : OffsetCornerType.SQUARE);
                        // 波打线切割
                        ArrayList<Point> fixlefttopList = new PointList(pointArrayList).fixToLeftTopPointsList();
                        GPCManager waveGpcManager = new GPCManager(new PointList(fixlefttopList).toGeomPointList(),
                                tileWidth, tileHeight, gap, gap, GPCConfig.FROM_MIDDLE, GPCConfig.STRAIGHT, waveLine);
                        // 斜切波打线
                        if (phyLogicalPackageArrayList.size() == 1) {
                            TileInfoList tileInfoList = waveLine.tiltCornerTileInfo;
                            createArea3D(tileInfoList, phyLogicalPackage.tile.codeNum, tileHeight, true, false);
                            TileInfoList waveLineGapList = waveLine.waveLineGapInfo;
                            createArea3D(waveLineGapList, phyLogicalPackage.tile.codeNum, tileHeight, true, true);
                        }
                        // 转角波打线
                        else {
                            PhyLogicalPackage shortPhyLogicalPackage = phyLogicalPackageArrayList.get(1); // 短砖对应数据包
                            TileInfoList square = waveLine.squareCornerInfo; // 转角短砖
                            TileInfoList squareCorner = waveLine.squareCornerTileInfo; // 转角长砖
                            TileInfoList waveLineGapList = waveLine.waveLineGapInfo;
                            createArea3D(squareCorner, phyLogicalPackage.tile.codeNum, tileHeight, true, false);
                            createArea3D(square, shortPhyLogicalPackage.tile.codeNum, tileHeight, true, false);
                            createArea3D(waveLineGapList, phyLogicalPackage.tile.codeNum, tileHeight, true, true);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return wlTileResult;
            }

            @Override
            protected void onPostExecute(WLTileResult wlTileResult) {
                super.onPostExecute(wlTileResult);
                if (onTilesResultListener != null) {
                    onTilesResultListener.textureJointCompleted(wlTileResult.getHoleBitmap());
                }
            }
        }.execute();
    }

    /**
     * 创建波打线外部砖缝数据
     *
     * @param pointArrayList 当前波打线层使用的切割围点
     * @param materialCode   所属波打线主砖编码，用于绑定关系
     * @param cellThickness  波打线层厚度
     * @param gap            砖缝厚度
     * @return 返回下一层波打线或中心砖铺贴区域围点列表
     */
    private ArrayList<Point> createWaveLineOuterGaps(ArrayList<Point> pointArrayList, String materialCode, float cellThickness, float gap) {
        float offset = cellThickness + gap;
        ArrayList<Point> thicknessList = new PointList(pointArrayList).offsetList(false, cellThickness);
        thicknessList = new PointList(thicknessList).fixToLeftTopPointsList();
        ArrayList<Point> offsetList = new PointList(pointArrayList).offsetList(false, offset);
        offsetList = new PointList(offsetList).fixToLeftTopPointsList();
        int size = thicknessList.size();
        for (int i = 0; i < size; i++) {
            Point tnow = thicknessList.get(i);
            Point tnext = null;
            Point onow = offsetList.get(i);
            Point onext = null;
            if (i == size - 1) {
                tnext = thicknessList.get(0);
                onext = offsetList.get(0);
            } else {
                tnext = thicknessList.get(i + 1);
                onext = offsetList.get(i + 1);
            }
            ArrayList<Point> useList = new ArrayList<>();
            useList.add(tnow.copy());
            useList.add(tnext.copy());
            useList.add(onext.copy());
            useList.add(onow.copy());
            ArrayList<Point> boxList = new PointList(useList).getBoxList();
            // 创建缝隙
            Area3D area3D = new Area3D(true, materialCode, useList, boxList);
            area3D.setSkewTile(false);
            // 存入图片处理器
            wlTileResult.putArea3D(area3D, true);
        }
        return offsetList;
    }

    /**
     * 创建区域
     *
     * @param tileInfoList  切割区域围点
     * @param materialCode  区域使用的铺砖编码
     * @param tilethickness 当前波打线层厚度
     * @param isWaveLine    波打线区域标记
     * @param isGap         砖缝标记
     */
    private void createArea3D(TileInfoList tileInfoList, String materialCode, float tilethickness, boolean isWaveLine, boolean isGap) {
        ArrayList<ArrayList<geom.Point>> geomIntersectList = tileInfoList.intersect_point;
        ArrayList<ArrayList<geom.Point>> geomOriginList = tileInfoList.original_point;
        for (int i = 0; i < geomIntersectList.size(); i++) {
            ArrayList<geom.Point> pointsList = geomIntersectList.get(i);
            ArrayList<geom.Point> originList = geomOriginList.get(i);
            ArrayList<com.lejia.mobile.orderking.hk3d.classes.Point> changePointsList = PointList.staticExchangeGemoListToThisList(pointsList);
            ArrayList<com.lejia.mobile.orderking.hk3d.classes.Point> changeOriginList = PointList.staticExchangeGemoListToThisList(originList);
            boolean randChecker = (isGap ? false : (isWaveLine ? false : randRotate));
            Area3D area3D = new Area3D(isGap, materialCode, randChecker, changePointsList, changeOriginList);
            if (!isWaveLine)
                area3D.setSkewTile(skewTile);
            else {
                area3D.setSkewTile(false);
                if (!isGap) {
                    area3D.setWaveAngle(pointList, tilethickness);
                }
            }
            // 存入图片处理器
            wlTileResult.putArea3D(area3D, isWaveLine);
        }
    }

    // 获取中心区域铺砖围点
    public ArrayList<Point> getCenterPointsList() {
        return centerPointsList;
    }

    // 获取每层波打线对应的围点列表
    public ArrayList<ArrayList<Point>> getWavelinesCellsPointList() {
        return wavelinesCellsPointList;
    }

    /**
     * 释放数据
     */
    public void release() {
        wlTileResult.release();
        wlTileResult = null;
        Iterator<Map.Entry<String, Bitmap>> iterator = bitmapHashMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Bitmap> entry = iterator.next();
            Bitmap bitmap = entry.getValue();
            bitmap.recycle();
        }
        bitmapHashMap.clear();
        bitmapHashMap = null;
    }

}
