package com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.lejia.mobile.orderking.bases.OrderKingApplication;
import com.lejia.mobile.orderking.classes.ResUrlNodeXml;
import com.lejia.mobile.orderking.classes.XInfo;
import com.lejia.mobile.orderking.hk3d.classes.PointList;
import com.lejia.mobile.orderking.hk3d.datas_2d.Area3D;
import com.lejia.mobile.orderking.hk3d.datas_2d.Ground;
import com.lejia.mobile.orderking.hk3d.gpc.OnTilesResultListener;
import com.lejia.mobile.orderking.utils.BitmapUtils;

import java.util.ArrayList;

import geom.Point;
import geom.gpc.GPCConfig;
import geom.gpc.GPCManager;
import geom.gpc.TileInfoList;

/**
 * Author by HEKE
 *
 * @time 2018/11/19 18:03
 * TODO: 普通砖铺砖
 */
public class NormalPave {

    private Context mContext;
    private PointList pointList;
    private ResUrlNodeXml.ResPath resPath;
    private OnTilesResultListener onTilesResultListener;
    private Ground ground; // 所在的房间地面

    private XInfo normalXInfo; // 使用的普通砖信息对象
    private Bitmap originBitmap; // 使用铺砖位图

    /**
     * 铺砖起始方向
     */
    public int direction = GPCConfig.FROM_RIGHT_TOP;

    /**
     * 是否45°斜铺
     */
    public boolean skewTile;

    /**
     * 砖缝颜色
     */
    public int gapsColor = 0xFF333333;

    /**
     * 砖缝厚度
     */
    public float brickGap = 0.2f;

    /**
     * 纹理随机标志
     */
    public boolean randRotate;

    /**
     * 铺砖结果对象
     */
    private NTTileResult NTTileResult;

    /**
     * 仅用于替换波打线中心区域铺砖标志
     */
    private boolean onlyUseWavelinesCenterTile;

    /**
     * 基础构造函数一
     *
     * @param pointList
     * @param resPath
     * @param ground
     * @param onTilesResultListener
     */
    public NormalPave(PointList pointList, ResUrlNodeXml.ResPath resPath, Ground ground, OnTilesResultListener onTilesResultListener) {
        this.mContext = OrderKingApplication.getInstant();
        this.pointList = pointList;
        this.resPath = resPath;
        this.ground = ground;
        this.onTilesResultListener = onTilesResultListener;
        new DefaultTile().getTilesXInfo(resPath.name, resPath.nodeName, new DefaultTile.OnDefaultTilesListener() {
            @Override
            public void compelet(XInfo xInfo, Bitmap bitmap) {
                normalXInfo = xInfo;
                tile();
            }
        });
    }

    /**
     * 基础构造函数二
     *
     * @param pointList
     * @param resPath
     * @param xInfo
     * @param ground
     * @param onTilesResultListener
     */
    public NormalPave(PointList pointList, ResUrlNodeXml.ResPath resPath, XInfo xInfo, Ground ground, OnTilesResultListener onTilesResultListener) {
        this.mContext = OrderKingApplication.getInstant();
        this.pointList = pointList;
        this.resPath = resPath;
        this.normalXInfo = xInfo;
        this.ground = ground;
        this.onTilesResultListener = onTilesResultListener;
        tile();
    }

    /**
     * 基础构造函数三
     *
     * @param pointList
     * @param resPath
     * @param xInfo
     * @param ground
     * @param randRotate
     * @param isSkewTile
     * @param direction
     * @param gap
     * @param onTilesResultListener
     */
    public NormalPave(PointList pointList, ResUrlNodeXml.ResPath resPath, XInfo xInfo, Ground ground, boolean randRotate,
                      boolean isSkewTile, int direction, float gap, OnTilesResultListener onTilesResultListener) {
        this.mContext = OrderKingApplication.getInstant();
        this.pointList = pointList;
        this.resPath = resPath;
        this.normalXInfo = xInfo;
        this.ground = ground;
        this.randRotate = randRotate;
        this.skewTile = isSkewTile;
        this.direction = direction;
        this.brickGap = gap;
        this.onTilesResultListener = onTilesResultListener;
        tile();
    }

    /**
     * 构造函数三，基于无需进行铺砖操作的前提下，创建NormalPave对象
     *
     * @param resPath
     * @param onNoNeedTileListener
     */
    @SuppressLint("StaticFieldLeak")
    public NormalPave(ResUrlNodeXml.ResPath resPath, OnNoNeedTileListener onNoNeedTileListener) {
        this.resPath = resPath;
        this.onlyUseWavelinesCenterTile = true;
        final OnNoNeedTileListener myOnNoNeedTileListener = onNoNeedTileListener;
        new DefaultTile().getTilesXInfo(resPath.name, resPath.nodeName, new DefaultTile.OnDefaultTilesListener() {
            @Override
            public void compelet(final XInfo xInfo, Bitmap bitmap) {
                normalXInfo = xInfo;
                new AsyncTask<String, Integer, String>() {
                    @Override
                    protected String doInBackground(String... strings) {
                        originBitmap = BitmapUtils.createBitmapByXInfo(xInfo);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(String s) {
                        super.onPostExecute(s);
                        if (myOnNoNeedTileListener != null) {
                            myOnNoNeedTileListener.created();
                        }
                    }
                }.execute();
            }
        });
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

    public int getGapsColor() {
        return gapsColor;
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

    public float getBrickGap() {
        return brickGap;
    }

    // 获取当前铺贴方向
    public int getDirection() {
        return direction;
    }

    // 获取当前是否斜铺
    public boolean isSkewTile() {
        return skewTile;
    }

    // 获取所在的房间地面
    public Ground getGround() {
        return ground;
    }

    // 获取铺砖结果对象
    public NTTileResult getTilesResult() {
        return NTTileResult;
    }

    // 基础瓷砖信息对象
    public XInfo getNormalXInfo() {
        return normalXInfo;
    }

    /**
     * 原始贴图的位图
     */
    public Bitmap getOriginBitmap() {
        return originBitmap;
    }

    /**
     * TODO 进行切割铺贴
     */
    @SuppressLint("StaticFieldLeak")
    private void tile() {
        if (onlyUseWavelinesCenterTile) // 用于中心砖，无需自身铺砖
            return;
        new AsyncTask<String, Integer, NTTileResult>() {
            @Override
            protected NTTileResult doInBackground(String... strings) {
                if (NTTileResult != null) {
                    NTTileResult.release();
                    NTTileResult = null;
                }
                NTTileResult = new NTTileResult(pointList.getRectBox(), NormalPave.this);
                try {
                    // 加载贴图
                    if (originBitmap == null) {
                        originBitmap = BitmapUtils.createBitmapByXInfo(normalXInfo);
                    }
                    int tileWidth = originBitmap.getWidth();
                    int tileHeight = originBitmap.getHeight();
                    ArrayList<Point> geomList = pointList.toGeomPointList();
                    GPCManager gpcManager = new GPCManager(geomList, tileWidth, tileHeight, brickGap, brickGap,
                            direction, skewTile ? GPCConfig.TILT : GPCConfig.STRAIGHT);
                    // 实质砖
                    TileInfoList mTileInfoList = gpcManager.getTileInfoList();
                    createArea3D(mTileInfoList, false);
                    // 砖缝
                    TileInfoList xGapTileInfoList = gpcManager.getXGapInfoList();
                    TileInfoList yGapTileInfoList = gpcManager.getYGapInfoList();
                    createArea3D(xGapTileInfoList, true);
                    createArea3D(yGapTileInfoList, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return NTTileResult;
            }

            @Override
            protected void onPostExecute(NTTileResult tilesResult) {
                super.onPostExecute(tilesResult);
                if (onTilesResultListener != null)
                    onTilesResultListener.textureJointCompleted(tilesResult.getHoleBitmap());
            }
        }.execute();
    }

    /**
     * 创建区域
     *
     * @param tileInfoList
     * @param isGap
     */
    private void createArea3D(TileInfoList tileInfoList, boolean isGap) {
        String materialCode = normalXInfo.materialCode;
        ArrayList<ArrayList<geom.Point>> geomIntersectList = tileInfoList.intersect_point;
        ArrayList<ArrayList<geom.Point>> geomOriginList = tileInfoList.original_point;
        for (int i = 0; i < geomIntersectList.size(); i++) {
            ArrayList<geom.Point> pointsList = geomIntersectList.get(i);
            ArrayList<geom.Point> originList = geomOriginList.get(i);
            ArrayList<com.lejia.mobile.orderking.hk3d.classes.Point> changePointsList = PointList.staticExchangeGemoListToThisList(pointsList);
            ArrayList<com.lejia.mobile.orderking.hk3d.classes.Point> changeOriginList = PointList.staticExchangeGemoListToThisList(originList);
            boolean randChecker = (isGap ? false : randRotate);
            Area3D area3D = new Area3D(isGap, materialCode, randChecker, changePointsList, changeOriginList);
            area3D.setSkewTile(skewTile);
            NTTileResult.putArea3D(area3D);
        }
    }

    /**
     * 释放数据
     */
    public void release() {
        if (NTTileResult != null) {
            NTTileResult.release();
            NTTileResult = null;
        }
        normalXInfo = null;
        if (originBitmap != null) {
            originBitmap.recycle();
            originBitmap = null;
        }
    }

    /**
     * Author by HEKE
     *
     * @time 2018/11/22 11:53
     * TODO: 回调创建无需切割铺砖的次对象的接口
     */
    public interface OnNoNeedTileListener {
        void created();
    }

    /**
     * 起铺方向转换为接单王的起铺方向
     */
    private int jdwDir() {
        int dir = 4;
        switch (direction) {
            case GPCConfig.FROM_RIGHT_TOP:
                dir = 0;
                break;
            case GPCConfig.FROM_LEFT_TOP:
                dir = 2;
                break;
            case GPCConfig.FROM_LEFT_BOTTOM:
                dir = 8;
                break;
            case GPCConfig.FROM_MIDDLE:
                dir = 4;
                break;
            case GPCConfig.FROM_MIDDLE_BOTTOM:
                dir = 7;
                break;
            case GPCConfig.FROM_MIDDLE_LEFT:
                dir = 5;
                break;
            case GPCConfig.FROM_MIDDLE_RIGHT:
                dir = 3;
                break;
            case GPCConfig.FROM_MIDDLE_TOP:
                dir = 1;
                break;
            case GPCConfig.FROM_RIGHT_BOTTOM:
                dir = 6;
                break;
        }
        return dir;
    }

    /**
     * 转化为中心区域铺砖计划对应的xml数据
     *
     * @param pointArrayList 中心瓷砖铺贴区域
     * @return 返回xml格式数据封装
     */
    public String toXml(ArrayList<com.lejia.mobile.orderking.hk3d.classes.Point> pointArrayList) {
        if (normalXInfo == null)
            return null;
        int rotate = (isSkewTile() ? -45 : 0);
        int length = normalXInfo.X;
        int width = normalXInfo.Y;
        String v = "<TilePlan code=\"" + normalXInfo.materialCode + "\" type=\"0\" name=\"单砖连续直铺\" mosaicEditXml=\"null\"" +
                "gap=\"" + (int) (brickGap * 10) + "\" gapColor=\"" + gapsColor + "\" locate=\"" + jdwDir() + "\" rotate=\"" + rotate + "\">";
        try {
            v += "\n <symbol key=\"WA\" value=\"" + normalXInfo.X + "\"/>";
            v += "\n <symbol key=\"WA\" value=\"" + normalXInfo.Y + "\"/>";
            v += "\n<phy>\n" +
                    "<Tile code=\"A\" codeNum=\"" + normalXInfo.materialCode + "\" length=\"" + length + "\" width=\"" + width + "\" " +
                    "url=\"" + normalXInfo.linkUrl + "\" family=\"\" parentCode=\"\"/>\n" +
                    "</phy>";
            v += "\n<logTile>\n" +
                    "<LogicalTile code=\"A\" isMain=\"true\" rotate=\"" + rotate + "\" length=\"" + width + "\" width=\"" + width + "\" " +
                    "dirx=\"0\" diry=\"0\" dirz=\"0\" notchStyle=\"0\"/>\n" +
                    "</logTile>";
            v += "\n <dir1 x=\"" + length + "\" y=\"0\" z=\"0\"/>\n" +
                    "<dir2 x=\"0\" y=\"" + -width + "\" z=\"0\"/>";
            v += "\n<dirExp1>\n" +
                    "  <SymbolVector3D u=\"LA+G\" v=\"0\" w=\"0\"/>\n" +
                    " </dirExp1>\n" +
                    " <dirExp2>\n" +
                    "   <SymbolVector3D u=\"0\" v=\"0-WA-G\" w=\"0\"/>\n" +
                    " </dirExp2>";
            if (pointArrayList != null && pointArrayList.size() > 0) {
                v += "\n<tileRegion>";
                for (com.lejia.mobile.orderking.hk3d.classes.Point point : pointArrayList) {
                    v += "\n<TPoint x=\"" + (int) (point.x * 10) + "\" y=\"" + (int) (point.y * 10) + "\"/>";
                }
                v += "\n</tileRegion>";
            }
            v += "\n </TilePlan>";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return v;
    }
}
