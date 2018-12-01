package com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.bridge;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Handler;
import android.widget.Toast;

import com.lejia.mobile.orderking.bases.OrderKingApplication;
import com.lejia.mobile.orderking.classes.XInfo;
import com.lejia.mobile.orderking.dialogs.UpSchemeSetNameDialog;
import com.lejia.mobile.orderking.dialogs.WaitBar;
import com.lejia.mobile.orderking.hk3d.activity_partitation.Designer3DManager;
import com.lejia.mobile.orderking.hk3d.classes.Line;
import com.lejia.mobile.orderking.hk3d.classes.NameData;
import com.lejia.mobile.orderking.hk3d.classes.Point;
import com.lejia.mobile.orderking.hk3d.classes.PointList;
import com.lejia.mobile.orderking.hk3d.datas_2d.Ground;
import com.lejia.mobile.orderking.hk3d.datas_2d.House;
import com.lejia.mobile.orderking.hk3d.datas_2d.HouseDatasManager;
import com.lejia.mobile.orderking.hk3d.datas_2d.HouseName;
import com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.NormalPave;
import com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.WaveLinesPave;
import com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.classes.DirExp1;
import com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.classes.DirExp2;
import com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.classes.LogicalTile;
import com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.classes.PhyLogicalPackage;
import com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.classes.SymbolVector3D;
import com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.classes.Tile;
import com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.classes.TilePlan;
import com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.classes.WaveMutliPlan;
import com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.classes.schemes.FloorData;
import com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.classes.schemes.FloorPlan;
import com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.classes.schemes.LayerData;
import com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.classes.schemes.Plan;
import com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.classes.schemes.RoomLayer;
import com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.classes.schemes.RoomRegion;
import com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.classes.schemes.SceneID;
import com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.classes.schemes.Shape;
import com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.classes.schemes.TPoint;
import com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.classes.schemes.TileLayer;
import com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.classes.schemes.TileLayers;
import com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.classes.schemes.TileRegion;
import com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.classes.schemes.TileViewPanel;
import com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.classes.schemes.WallData;
import com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.classes.schemes.WaveLine;
import com.lejia.mobile.orderking.https.KosapRequest;
import com.lejia.mobile.orderking.https.OnKosapResponseListener;
import com.lejia.mobile.orderking.httpsResult.classes.User;
import com.lejia.mobile.orderking.utils.BitmapUtils;
import com.lejia.mobile.orderking.utils.TimeUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 * Author by HEKE
 *
 * @time 2018/11/24 11:01
 * TODO: 保存操作对象
 */
public class SaveDatasToService {

    private Context mContext;
    private Designer3DManager designer3DManager;
    private HouseDatasManager houseDatasManager;

    private Bitmap previewBmp; // 预览图

    // 用户
    private User mUser;

    /**
     * 创建提示窗口
     */
    private WaitBar waitBar;

    /**
     * 所有房间列表
     */
    ArrayList<House> houseArrayList;

    /**
     * 用于保存的xml数据对象
     */
    private FloorPlan floorPlan;

    public SaveDatasToService(Context context, Designer3DManager designer3DManager, HouseDatasManager houseDatasManager) {
        this.mContext = context;
        this.designer3DManager = designer3DManager;
        this.houseDatasManager = houseDatasManager;
        this.mUser = ((OrderKingApplication) mContext.getApplicationContext()).mUser;
        this.waitBar = new WaitBar(context);
        this.waitBar.setWindow(true);
        preview();
    }

    /**
     * 创建上传数据
     */
    @SuppressLint("StaticFieldLeak")
    private void createUpDatas() {
        // 创建xml数据
        new AsyncTask<String, Integer, FloorPlan>() {
            @Override
            protected FloorPlan doInBackground(String... params) {
                floorPlan = new FloorPlan();
                try {
                    // 基础信息
                    floorPlan.version = "LJ003";
                    floorPlan.sceneID = new SceneID("Scene" + TimeUtils.timeNumber(), "");
                    floorPlan.authorCode = mUser.token;
                    floorPlan.previewUrl = "";
                    floorPlan.ceilingHeight = 2800;
                    floorPlan.layerData = new LayerData();
                    // 创建墙体、地面
                    ArrayList<House> closeHouseList = new ArrayList<>();
                    int floorNumber = 0;
                    int wallNumber = 0;
                    floorPlan.wallDataArrayList = new ArrayList<>();
                    for (House house : houseArrayList) {
                        PointList pointList = house.innerPointList;
                        PointList centerList = house.centerPointList;
                        ArrayList<Line> linesList = house.isWallClosed ? centerList.toLineList() : centerList.toNotClosedLineList();
                        wallNumber += linesList.size();
                        if (house.isWallClosed) {
                            floorNumber++;
                            closeHouseList.add(house);
                        }
                        for (Line line : linesList) {
                            WallData wallData = new WallData();
                            wallData.id = "Wall" + UUID.randomUUID();
                            wallData.startX = (float) (line.down.x * -10d);
                            wallData.startY = (float) (line.down.y * 10d);
                            wallData.startZ = 0;
                            wallData.endX = (float) (line.up.x * -10d);
                            wallData.endY = (float) (line.up.y * 10d);
                            wallData.endZ = 0;
                            wallData.thickness = (float) (line.getThickess() * 10d);
                            wallData.offSide = 0;
                            floorPlan.wallDataArrayList.add(wallData);
                        }
                    }
                    floorPlan.wallNum = wallNumber;
                    // 创建地面
                    floorPlan.floorNum = floorNumber;
                    floorPlan.floorDataArrayList = new ArrayList<>();
                    for (House house : closeHouseList) {
                        Ground ground = house.ground;
                        HouseName houseName = house.houseName;
                        FloorData floorData = new FloorData();
                        NameData nameData = houseName.getNameData();
                        PointList innerList = house.innerPointList;
                        if (nameData != null) {
                            floorData.roomName = nameData.name;
                            floorData.area = Float.parseFloat(nameData.area);
                        }
                        floorData.materialMode = 17;
                        floorData.roomHeight = 2800;
                        Point center = innerList.getInnerValidPoint(false);
                        floorData.centerX = (float) (center.x * -10.0d);
                        floorData.centerY = (float) (center.y * 10.0d);
                        floorData.centerZ = 0;
                        floorData.url = "image/floorMats/mub5.jpg";
                        floorData.floorID = "floor_" + UUID.randomUUID();
                        // 数据面板
                        floorData.tileViewPanel = new TileViewPanel();
                        // 房间层
                        floorData.tileViewPanel.roomLayer = new RoomLayer();
                        floorData.tileViewPanel.roomLayer.roomRegion = new RoomRegion();
                        for (int i = 0; i < innerList.size(); i++) {
                            Point point = innerList.getIndexAt(i);
                            TPoint tPoint = new TPoint();
                            tPoint.xpt1 = (float) (point.x * -10d);
                            tPoint.ypt1 = (float) (point.y * 10d);
                            floorData.tileViewPanel.roomLayer.roomRegion.tPointsList.add(tPoint);
                        }
                        // 地面层(暂无区域创建单层)
                        floorData.tileViewPanel.tileLayers = new TileLayers();
                        TileLayer tileLayer = new TileLayer();
                        tileLayer.tileRegion = new TileRegion();
                        tileLayer.tileRegion.plan = new Plan();
                        tileLayer.tileRegion.shape = new Shape();
                        // 创建中心砖铺贴数据
                        NormalPave normalPave = ground.getNormalPave();
                        WaveLinesPave waveLinesPave = ground.getWaveLinesPave();
                        boolean hasWaveLines = (waveLinesPave != null);
                        PointList centerList = normalPave.getPointList();
                        for (int i = 0; i < centerList.size(); i++) {
                            Point point = centerList.getIndexAt(i);
                            tileLayer.tileRegion.shape.add(new Point(point.x, point.y));
                        }
                        TilePlan centerTilePlan = new TilePlan();
                        XInfo xInfo = normalPave.getNormalXInfo();
                        centerTilePlan.code = xInfo.materialCode;
                        centerTilePlan.name = xInfo.materialName;
                        centerTilePlan.gap = normalPave.brickGap * 10;
                        centerTilePlan.gapColor = normalPave.gapsColor;
                        centerTilePlan.locate = normalPave.jdwDir();
                        centerTilePlan.putSymbol("WA", "" + xInfo.Y);
                        centerTilePlan.putSymbol("LA", "" + xInfo.X);
                        Tile tile = new Tile(); // 物理砖
                        tile.code = "A";
                        tile.codeNum = xInfo.materialCode;
                        tile.length = xInfo.X;
                        tile.width = xInfo.Y;
                        tile.url = xInfo.linkUrl;
                        centerTilePlan.phy.tileArrayList.add(tile);
                        LogicalTile logicalTile = new LogicalTile(); // 逻辑砖
                        logicalTile.code = "A";
                        logicalTile.isMain = true;
                        logicalTile.randRotate = normalPave.randRotate;
                        logicalTile.rotate = normalPave.isSkewTile() ? -45 : 0;
                        logicalTile.length = xInfo.X;
                        logicalTile.width = xInfo.Y;
                        centerTilePlan.dirExp1 = new DirExp1();
                        centerTilePlan.dirExp1.symbolVector3D = new SymbolVector3D();
                        centerTilePlan.dirExp1.symbolVector3D.su = "LA+G";
                        centerTilePlan.dirExp1.symbolVector3D.sv = "0";
                        centerTilePlan.dirExp2 = new DirExp2();
                        centerTilePlan.dirExp2.symbolVector3D = new SymbolVector3D();
                        centerTilePlan.dirExp2.symbolVector3D.su = "0";
                        centerTilePlan.dirExp2.symbolVector3D.sv = "0-WA-G";
                        centerTilePlan.logtile.logicalTileArrayList.add(logicalTile);
                        tileLayer.tileRegion.plan.tilePlanArrayList.add(centerTilePlan);
                        if (hasWaveLines) {
                            tileLayer.tileRegion.plan.tileplanPointsList.add(centerList.copy());
                        }
                        // 创建波打线数据
                        if (hasWaveLines) {
                            WaveMutliPlan waveMutliPlan = ground.getWaveLinesPaveRes();
                            WaveLine waveLine = new WaveLine();
                            waveLine.openDir = -1;
                            waveLine.parentGuid = 0;
                            waveLine.planType = -1;
                            // 对应波打线数据
                            ArrayList<TilePlan> tilePlanArrayList = waveMutliPlan.tilePlanArrayList;
                            boolean wavelinesCellsLayout = tilePlanArrayList.size() > 1; // 多层铺砖标志
                            ArrayList<ArrayList<Point>> wavelinesCellsPointList = waveLinesPave.getWavelinesCellsPointList();
                            waveLine.tilePlanArrayList.addAll(tilePlanArrayList);
                            waveLine.tilePlanPointArrayList = wavelinesCellsPointList;
                            // 设置TilePlan中的Tile的对应url路径
                            int layerCount = tilePlanArrayList.size();
                            for (TilePlan tilePlan : tilePlanArrayList) {
                                ArrayList<PhyLogicalPackage> phylogList = tilePlan.phyLogicalPackageArrayList;
                                for (PhyLogicalPackage phyLogicalPackage : phylogList) {
                                    phyLogicalPackage.tile.url = phyLogicalPackage.xInfo.linkUrl;
                                }
                                tilePlan.wavelinesCellsLayout = true;
                                tilePlan.layerCount = layerCount;
                                layerCount--;
                                tilePlan.waveLineOrientation = 1; // 单层
                                if (wavelinesCellsLayout) {
                                    tilePlan.waveLineOrientation = 0; // 多层
                                }
                                tilePlan.openDir = -1;
                                tilePlan.waveWidth = tilePlan.logtile.logicalTileArrayList.get(0).width;
                                tilePlan.waveType = tilePlan.phy.tileArrayList.size() == 1 ? 2 : 5;
                                tilePlan.gapTileRegion = 0;
                            }
                            tileLayer.waveLine = waveLine;
                        }
                        floorData.tileViewPanel.tileLayers.add(tileLayer);
                        // 加入地面信息
                        floorPlan.floorDataArrayList.add(floorData);
                    }
                    // 家具信息
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return floorPlan;
            }

            @Override
            protected void onPostExecute(FloorPlan r) {
                super.onPostExecute(r);
                waitBar.setText("创建完成，请输入命名后提交保存!");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        waitBar.hide();
                        // 弹出输入命名窗口
                        new UpSchemeSetNameDialog(OrderKingApplication.getMainActivityContext(), new UpSchemeSetNameDialog.OnSchemeNameSetListener() {
                            @Override
                            public void setName(String name, boolean cancle) {
                                if (!cancle) {
                                    upload(name);
                                } else {
                                    release();
                                }
                            }
                        }).show();
                    }
                }, 500);
            }
        }.execute();
    }

    /**
     * TODO 上传数据
     *
     * @param name
     */
    private void upload(String name) {
        waitBar.show();
        waitBar.setText("上传保存中....");
        HashMap<String, String> params = new HashMap<>();
        params.put("userCode", mUser.token);
        params.put("name", name);
        params.put("xml", floorPlan.toXml());
        params.put("previewBuffer", BitmapUtils.bitmapToBase64(previewBmp));
        // SaveFile   SaveFileForDatas
        KosapRequest request = new KosapRequest(mContext, "SaveFileForDatas", params, new OnKosapResponseListener() {
            @Override
            public void response(String result, boolean error) {
                waitBar.hide();
                if (!error) {
                    Toast.makeText(mContext, "保存完成！", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContext, "保存失败！", Toast.LENGTH_SHORT).show();
                }
                release();
            }

            @Override
            public void useLocal() {
                waitBar.hide();
            }
        });
        request.request();
    }

    // 释放数据
    private void release() {
        if (previewBmp != null) {
            previewBmp.recycle();
            previewBmp = null;
            floorPlan = null;
        }
    }

    /**
     * 预览图
     */
    private void preview() {
        try {
            // 获取房间数据
            houseArrayList = houseDatasManager.getHousesList();
            // 无绘制方案，不进行操作
            if (houseArrayList == null || houseArrayList.size() == 0) {
                Toast.makeText(mContext, "请先设计方案！", Toast.LENGTH_SHORT).show();
                return;
            }
            this.waitBar.show();
            waitBar.setText("创建预览图中...");
            // 三维截图
            designer3DManager.getDesigner3DRender().readPixs(new OnReadPixsListener() {

                @SuppressLint("StaticFieldLeak")
                @Override
                public void complelted(Bitmap bitmap) {
                    new AsyncTask<Bitmap, Integer, Bitmap>() {
                        @Override
                        protected Bitmap doInBackground(Bitmap... bitmaps) {
                            Bitmap ret = BitmapUtils.toSize(bitmaps[0], -1, 512);
                            return ret;
                        }

                        @Override
                        protected void onPostExecute(Bitmap bitmap) {
                            super.onPostExecute(bitmap);
                            previewBmp = bitmap;
                            waitBar.setText("创建房间数据中...");
                            createUpDatas();
                        }
                    }.execute(bitmap);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
