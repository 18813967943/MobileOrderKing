package com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.bridge;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;

import com.lejia.mobile.orderking.bases.OrderKingApplication;
import com.lejia.mobile.orderking.hk3d.Designer3DRender;
import com.lejia.mobile.orderking.hk3d.classes.Point;
import com.lejia.mobile.orderking.hk3d.classes.PointList;
import com.lejia.mobile.orderking.hk3d.classes.RectD;
import com.lejia.mobile.orderking.hk3d.datas_2d.House;
import com.lejia.mobile.orderking.hk3d.datas_2d.HouseDatasManager;
import com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.classes.DirExp1;
import com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.classes.DirExp2;
import com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.classes.LogicalTile;
import com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.classes.SymbolVector3D;
import com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.classes.Tile;
import com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.classes.TilePlan;
import com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.classes.schemes.FloorData;
import com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.classes.schemes.FloorPlan;
import com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.classes.schemes.FurnitureData;
import com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.classes.schemes.LayerData;
import com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.classes.schemes.Plan;
import com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.classes.schemes.RoomLayer;
import com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.classes.schemes.RoomRegion;
import com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.classes.schemes.RoundPointData;
import com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.classes.schemes.SceneID;
import com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.classes.schemes.Shape;
import com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.classes.schemes.TPoint;
import com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.classes.schemes.TileLayer;
import com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.classes.schemes.TileLayers;
import com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.classes.schemes.TileRegion;
import com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.classes.schemes.TileViewPanel;
import com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.classes.schemes.WallData;
import com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.classes.schemes.WaveLine;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Author by HEKE
 *
 * @time 2018/11/26 20:38
 * TODO: 方案加载解析xml对象
 */
public class SchemeXmlParseToShowViews {

    private Context mContext;
    private HouseDatasManager houseDatasManager;
    private Designer3DRender designer3DRender;

    /**
     * 解析总数据对象
     */
    private FloorPlan floorPlan;

    public SchemeXmlParseToShowViews(Context context, String xml) {
        OrderKingApplication orderKingApplication = (OrderKingApplication) context.getApplicationContext();
        this.mContext = orderKingApplication;
        this.designer3DRender = orderKingApplication.getDesigner3DSurfaceView().getDesigner3DRender();
        this.houseDatasManager = designer3DRender.getHouseDatasManager();
        parse(xml);
    }

    /**
     * 解析xml数据
     */
    @SuppressLint("StaticFieldLeak")
    private void parse(String xml) {
        // 优先清空当前绘制数据
        houseDatasManager.clear();
        floorPlan = new FloorPlan();
        // 解析
        new AsyncTask<String, Integer, String>() {
            @Override
            protected String doInBackground(String... xmls) {
                try {
                    SAXReader saxReader = new SAXReader();
                    ByteArrayInputStream bais = new ByteArrayInputStream(xmls[0].getBytes());
                    Document document = saxReader.read(bais);
                    Element element = document.getRootElement();
                    String nodeNeme = element.getName().toLowerCase();
                    switch (nodeNeme) {
                        case "floordata":
                            readFloorDatas(element);
                            break;
                        case "root":
                            List<Element> elementList = element.elements();
                            for (Element e : elementList) {
                                String ename = e.getName().toLowerCase();
                                switch (ename) {
                                    case "version":
                                        floorPlan.version = e.attributeValue("code");
                                        break;
                                    case "sceneid":
                                        floorPlan.sceneID = new SceneID();
                                        floorPlan.sceneID.id = e.attributeValue("id");
                                        floorPlan.sceneID.numID = e.attributeValue("numID");
                                        break;
                                    case "author":
                                        floorPlan.authorCode = e.attributeValue("code");
                                        break;
                                    case "preview":
                                        floorPlan.previewUrl = e.attributeValue("url");
                                        break;
                                    case "ceilingheight":
                                        floorPlan.ceilingHeight = Integer.parseInt(e.attributeValue("value"));
                                        break;
                                    case "layerdata":
                                        floorPlan.layerData = new LayerData();
                                        floorPlan.layerData.groundLayerVisible = e.attributeValue("GroundLayerVisible").equals("true");
                                        floorPlan.layerData.wallLayerVisible = e.attributeValue("WallLayerVisible").equals("true");
                                        floorPlan.layerData.furnitureLayerVisible = e.attributeValue("FurnitureLayerVisible").equals("true");
                                        floorPlan.layerData.ceilingLayerVisible = e.attributeValue("CeilingLayerVisible").equals("true");
                                        floorPlan.layerData.dimensionLayerVisible = e.attributeValue("DimensionLayerVisible").equals("true");
                                        floorPlan.layerData.virtualLightLayerVisible = e.attributeValue("VirtualLightLayerVisible").equals("true");
                                        floorPlan.layerData.rectLightLayerVisible = e.attributeValue("RectLightLayerVisible").equals("true");
                                        break;
                                    case "wall":
                                        floorPlan.wallNum = Integer.parseInt(e.attributeValue("num"));
                                        break;
                                    case "walldata":
                                        WallData wallData = new WallData();
                                        wallData.id = e.attributeValue("ID");
                                        wallData.startX = Float.parseFloat(e.attributeValue("StartX"));
                                        wallData.startY = Float.parseFloat(e.attributeValue("StartY"));
                                        wallData.startZ = Float.parseFloat(e.attributeValue("StartZ"));
                                        wallData.endX = Float.parseFloat(e.attributeValue("EndX"));
                                        wallData.endY = Float.parseFloat(e.attributeValue("EndY"));
                                        wallData.endZ = Float.parseFloat(e.attributeValue("EndZ"));
                                        wallData.thickness = Float.parseFloat(e.attributeValue("Thickness"));
                                        wallData.offSide = Float.parseFloat(e.attributeValue("OffSide"));
                                        floorPlan.wallDataArrayList.add(wallData);
                                        break;
                                    case "floor":
                                        floorPlan.floorNum = Integer.parseInt(e.attributeValue("num"));
                                        break;
                                    case "floordata":
                                        readFloorDatas(e);
                                        break;
                                    case "furniture":
                                        floorPlan.furnitureNum = Integer.parseInt(e.attributeValue("num"));
                                        break;
                                    case "furnituredata":
                                        FurnitureData furnitureData = new FurnitureData();
                                        furnitureData.name = e.attributeValue("Name");
                                        furnitureData.code = e.attributeValue("Code");
                                        furnitureData.ERPCode = e.attributeValue("ERPCode");
                                        furnitureData.ID = e.attributeValue("ID");
                                        furnitureData.catalog = e.attributeValue("Catalog");
                                        furnitureData.URL = e.attributeValue("URL");
                                        furnitureData.PositionX = Float.parseFloat(e.attributeValue("PositionX"));
                                        furnitureData.PositionY = Float.parseFloat(e.attributeValue("PositionY"));
                                        furnitureData.PositionZ = Float.parseFloat(e.attributeValue("PositionZ"));
                                        furnitureData.OffGround = Float.parseFloat(e.attributeValue("OffGround"));
                                        furnitureData.Rotation = Float.parseFloat(e.attributeValue("Rotation"));
                                        furnitureData.Length = Float.parseFloat(e.attributeValue("Length"));
                                        furnitureData.Width = Float.parseFloat(e.attributeValue("Width"));
                                        furnitureData.Height = Float.parseFloat(e.attributeValue("Height"));
                                        furnitureData.LinkWallID = e.attributeValue("LinkWallID");
                                        furnitureData.LinkWall2ID = e.attributeValue("LinkWall2ID");
                                        furnitureData.Mirror = Integer.parseInt(e.attributeValue("Mirror"));
                                        furnitureData.CeilingLayerHeight = Integer.parseInt(e.attributeValue("CeilingLayerHeight"));
                                        furnitureData.CeilingLightDeep = Integer.parseInt(e.attributeValue("CeilingLightDeep"));
                                        furnitureData.CeilingTipHeight = Integer.parseInt(e.attributeValue("CeilingTipHeight"));
                                        furnitureData.CeilingLightColor = Integer.parseInt(e.attributeValue("CeilingLightColor"));
                                        furnitureData.CeilingLightWattage = Integer.parseInt(e.attributeValue("CeilingLightWattage"));
                                        furnitureData.LinkedFloorID = e.attributeValue("LinkedFloorID");
                                        furnitureData.PickCornerPointX = Integer.parseInt(e.attributeValue("PickCornerPointX"));
                                        furnitureData.PickCornerPointY = Integer.parseInt(e.attributeValue("PickCornerPointY"));
                                        furnitureData.PickCornerPointZ = Integer.parseInt(e.attributeValue("PickCornerPointZ"));
                                        furnitureData.PickDir1X = Integer.parseInt(e.attributeValue("PickDir1X"));
                                        furnitureData.PickDir1Y = Integer.parseInt(e.attributeValue("PickDir1Y"));
                                        furnitureData.PickDir1Z = Integer.parseInt(e.attributeValue("PickDir1Z"));
                                        furnitureData.PickDir2X = Integer.parseInt(e.attributeValue("PickDir2X"));
                                        furnitureData.PickDir2Y = Integer.parseInt(e.attributeValue("PickDir2Y"));
                                        furnitureData.PickDir2Z = Integer.parseInt(e.attributeValue("PickDir2Z"));
                                        furnitureData.WindowName = e.attributeValue("WindowName");
                                        furnitureData.WindowLine0X = Integer.parseInt(e.attributeValue("WindowLine0X"));
                                        furnitureData.WindowLine0Y = Integer.parseInt(e.attributeValue("WindowLine0Y"));
                                        furnitureData.WindowLine0Z = Integer.parseInt(e.attributeValue("WindowLine0Z"));
                                        furnitureData.WindowLine1X = Integer.parseInt(e.attributeValue("WindowLine1X"));
                                        furnitureData.WindowLine1Y = Integer.parseInt(e.attributeValue("WindowLine1Y"));
                                        furnitureData.WindowLine1Z = Integer.parseInt(e.attributeValue("WindowLine1Z"));
                                        furnitureData.WindowLine2X = Integer.parseInt(e.attributeValue("WindowLine2X"));
                                        furnitureData.WindowLine2Y = Integer.parseInt(e.attributeValue("WindowLine2Y"));
                                        furnitureData.WindowLine2Z = Integer.parseInt(e.attributeValue("WindowLine2Z"));
                                        furnitureData.WindowOffGround = Integer.parseInt(e.attributeValue("WindowOffGround"));
                                        furnitureData.WindowHeight = Integer.parseInt(e.attributeValue("WindowHeight"));
                                        furnitureData.WindowFloatDistance = Integer.parseInt(e.attributeValue("WindowFloatDistance"));
                                        furnitureData.WindowFloatSide = Integer.parseInt(e.attributeValue("WindowFloatSide"));
                                        furnitureData.WindowWallThickness = Integer.parseInt(e.attributeValue("WindowWallThickness"));
                                        furnitureData.BeamThickness = Integer.parseInt(e.attributeValue("BeamThickness"));
                                        furnitureData.BeamRotation = Integer.parseInt(e.attributeValue("BeamRotation"));
                                        furnitureData.HadCurveRailingPoints = Integer.parseInt(e.attributeValue("HadCurveRailingPoints"));
                                        furnitureData.BuildCurtainLength = Integer.parseInt(e.attributeValue("BuildCurtainLength"));
                                        furnitureData.BuildCurtainPositionX = Integer.parseInt(e.attributeValue("BuildCurtainPositionX"));
                                        furnitureData.BuildCurtainPositionY = Integer.parseInt(e.attributeValue("BuildCurtainPositionY"));
                                        furnitureData.BuildCurtainPositionZ = Integer.parseInt(e.attributeValue("BuildCurtainPositionZ"));
                                        furnitureData.BuildCurtainOffGround = Integer.parseInt(e.attributeValue("BuildCurtainOffGround"));
                                        furnitureData.VirtualLightColor = Integer.parseInt(e.attributeValue("VirtualLightColor"));
                                        furnitureData.VirtualLightWattage = Integer.parseInt(e.attributeValue("VirtualLightWattage"));
                                        furnitureData.OffDistance = Integer.parseInt(e.attributeValue("OffDistance"));
                                        furnitureData.LinkedThresholdMaterialURL = e.attributeValue("LinkedThresholdMaterialURL");
                                        furnitureData.LightRatio = Integer.parseInt(e.attributeValue("LightRatio"));
                                        furnitureData.LightColor = Integer.parseInt(e.attributeValue("LightColor"));
                                        furnitureData.LightDoubleSide = e.attributeValue("LightDoubleSide");
                                        furnitureData.comonID = e.attributeValue("comonID");
                                        furnitureData.IsPolyMode = e.attributeValue("IsPolyMode");
                                        furnitureData.moduleCode = e.attributeValue("ModuleCode");
                                        furnitureData.curtainModuleCode = e.attributeValue("CurtainModuleCode");
                                        furnitureData.curtainMainClothCode = e.attributeValue("CurtainMainClothCode");
                                        floorPlan.furnitureDataArrayList.add(furnitureData);
                                        break;
                                }
                            }
                            break;
                    }
                    bais.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                // 解析完毕，根据数据对象进行铺砖转化
                toShow();
            }
        }.execute(xml);
    }

    /**
     * 读取房间地面信息
     *
     * @param e 地面信息元素节点
     */
    private void readFloorDatas(Element e) {
        if (e == null || floorPlan == null)
            return;
        FloorData floorData = new FloorData();
        floorData.roomName = e.attributeValue("RoomName");
        floorData.centerX = Float.parseFloat(e.attributeValue("CenterX"));
        floorData.centerY = Float.parseFloat(e.attributeValue("CenterY"));
        floorData.centerZ = Float.parseFloat(e.attributeValue("CenterZ"));
        floorData.area = Float.parseFloat(e.attributeValue("Area"));
        floorData.materialType = Integer.parseInt(e.attributeValue("MaterialType"));
        floorData.materialCode = e.attributeValue("MaterialCode");
        floorData.url = e.attributeValue("URL");
        floorData.floorID = e.attributeValue("FloorID");
        floorData.roomHeight = Integer.parseInt(e.attributeValue("RoomHeight"));
        floorData.buildAngularLines = e.attributeValue("BuildAngularLines").equals("true");
        floorData.buildCeilingLines = e.attributeValue("BuildCeilingLines").equals("true");
        floorData.angularLineType = Integer.parseInt(e.attributeValue("AngularLineType"));
        floorData.ceilingLinesMatCode = e.attributeValue("CeilingLinesMatCode");
        floorData.ceilingLinesMatUrl = e.attributeValue("CeilingLinesMatUrl");
        floorData.angularLinesMatCode = e.attributeValue("AngularLinesMatCode");
        floorData.angularLinesMatUrl = e.attributeValue("AngularLinesMatUrl");
        floorData.isSpecialMode = e.attributeValue("IsSpecialMode").equals("true");
        floorData.materialMode = Integer.parseInt(e.attributeValue("MaterialMode"));
        // 其他数据对象
        List<Element> elementList = e.elements();
        for (Element element : elementList) {
            String ename = element.getName().toLowerCase();
            switch (ename) {
                case "tileviewpanel":
                    TileViewPanel tileViewPanel = new TileViewPanel(); // 铺砖面板数据对象
                    List<Element> tileElementsList = element.elements();
                    for (Element te : tileElementsList) {
                        String tename = te.getName().toLowerCase();
                        switch (tename) {
                            case "tilelayers":
                                tileViewPanel.tileLayers = new TileLayers();
                                Element layerElement = te.elements().get(0);
                                TileLayer tileLayer = new TileLayer();
                                tileLayer.main = layerElement.attributeValue("main").equals("true");
                                tileLayer.isMainArea = layerElement.attributeValue("isMainArea").equals("true");
                                tileLayer.hasOffset = layerElement.attributeValue("hasOffset").equals("true");
                                List<Element> layerElementList = layerElement.elements();
                                for (Element laye : layerElementList) {
                                    String layname = laye.getName().toLowerCase();
                                    switch (layname) {
                                        case "tileregion":
                                            TileRegion tileRegion = new TileRegion();
                                            tileRegion.isAddArea = laye.attributeValue("isAddArea").equals("true");
                                            tileRegion.isWallTile = laye.attributeValue("isWallTile").equals("true");
                                            tileRegion.isEmportTile = laye.attributeValue("isEmportTile").equals("true");
                                            tileRegion.area = Long.parseLong(laye.attributeValue("area"));
                                            tileRegion.opType = Integer.parseInt(laye.attributeValue("opType"));
                                            List<Element> treList = laye.elements();
                                            for (Element tre : treList) {
                                                String trename = tre.getName().toLowerCase();
                                                switch (trename) {
                                                    case "shape":
                                                        Shape shape = new Shape();
                                                        List<Element> shapeEList = tre.elements();
                                                        for (Element se : shapeEList) {
                                                            Point point = new Point();
                                                            point.x = Double.parseDouble(se.attributeValue("x"));
                                                            point.y = Double.parseDouble(se.attributeValue("y"));
                                                            shape.add(point);
                                                        }
                                                        tileRegion.shape = shape;
                                                        break;
                                                    case "plan":
                                                        Plan plan = new Plan();
                                                        parseTilePlan(plan, tre);
                                                        tileRegion.plan = plan;
                                                        break;
                                                    case "ts":
                                                        // 暂不处理，用于混铺等
                                                        break;
                                                }
                                            }
                                            tileLayer.tileRegion = tileRegion;
                                            break;
                                        case "waveline":
                                            WaveLine waveLine = new WaveLine();
                                            waveLine.planType = Integer.parseInt(laye.attributeValue("planType"));
                                            waveLine.openDir = Integer.parseInt(laye.attributeValue("openDir"));
                                            waveLine.parentGuid = Integer.parseInt(laye.attributeValue("parentGuid"));
                                            parseTilePlan(waveLine, laye);
                                            tileLayer.waveLine = waveLine;
                                            break;
                                    }
                                }
                                tileViewPanel.tileLayers.tileLayersList.add(tileLayer);
                                break;
                            case "roomlayer":
                                tileViewPanel.roomLayer = new RoomLayer();
                                tileViewPanel.roomLayer.roomRegion = new RoomRegion();
                                List<Element> tpEList = te.elements().get(0).elements();
                                for (Element tpe : tpEList) {
                                    TPoint tPoint = new TPoint();
                                    tPoint.type = Integer.parseInt(tpe.attributeValue("type"));
                                    tPoint.xpt1 = Float.parseFloat(tpe.attributeValue("xpt1"));
                                    tPoint.ypt1 = Float.parseFloat(tpe.attributeValue("ypt1"));
                                    tPoint.xpt2 = Float.parseFloat(tpe.attributeValue("xpt2"));
                                    tPoint.ypt2 = Float.parseFloat(tpe.attributeValue("ypt2"));
                                    tPoint.xpt3 = Float.parseFloat(tpe.attributeValue("xpt3"));
                                    tPoint.ypt3 = Float.parseFloat(tpe.attributeValue("ypt3"));
                                    tileViewPanel.roomLayer.roomRegion.tPointsList.add(tPoint);
                                }
                                break;
                        }
                    }
                    floorData.tileViewPanel = tileViewPanel;
                    break;
                case "roundpointdata":
                    RoundPointData roundPointData = new RoundPointData();
                    roundPointData.x = Integer.parseInt(element.attributeValue("X"));
                    roundPointData.y = Integer.parseInt(element.attributeValue("Y"));
                    floorData.roundPointDataList.add(roundPointData);
                    break;
            }
        }
        // 加入地面数据对象列表
        floorPlan.floorDataArrayList.add(floorData);
    }

    /**
     * 解析TilePlan
     *
     * @param obj     保存数据对象
     * @param element xml节点
     */
    private void parseTilePlan(Object obj, Element element) {
        if (obj == null || element == null)
            return;
        ArrayList<TilePlan> tilePlanArrayList = null;
        ArrayList<ArrayList<Point>> tileplanPointsList = null;
        if (obj instanceof Plan) {
            Plan plan = (Plan) obj;
            tilePlanArrayList = plan.tilePlanArrayList;
            tileplanPointsList = plan.tileplanPointsList;
        } else if (obj instanceof WaveLine) {
            WaveLine waveLine = (WaveLine) obj;
            tilePlanArrayList = waveLine.tilePlanArrayList;
            tileplanPointsList = waveLine.tilePlanPointArrayList;
        }
        List<Element> elementList = element.elements();
        if (elementList != null && elementList.size() > 0) {
            for (Element e : elementList) {
                String name = e.getName().toLowerCase();
                switch (name) {
                    case "tileplan":
                        TilePlan tilePlan = new TilePlan();
                        tilePlan.code = e.attributeValue("code");
                        tilePlan.type = e.attributeValue("type");
                        tilePlan.name = e.attributeValue("name");
                        tilePlan.gap = Float.parseFloat(e.attributeValue("gap"));
                        tilePlan.locate = Integer.parseInt(e.attributeValue("locate"));
                        tilePlan.rotate = Float.parseFloat(e.attributeValue("rotate"));
                        if (e.attribute("gapColor") != null)
                            tilePlan.gapColor = Integer.parseInt(e.attributeValue("gapColor"));
                        // 添加默认砖缝数值
                        tilePlan.putSymbol("G", "" + ((int) tilePlan.gap));
                        // 遍历创建子元素
                        List<Element> tpList = e.elements();
                        if (tpList != null && tpList.size() > 0) {
                            for (Element tpe : tpList) {
                                String tpename = tpe.getName().toLowerCase();
                                switch (tpename) {
                                    case "symbol":
                                        String key = tpe.attributeValue("key");
                                        String value = tpe.attributeValue("value");
                                        tilePlan.putSymbol(key, value);
                                        break;
                                    case "phy":
                                        List<Element> phyList = tpe.elements();
                                        for (Element phye : phyList) {
                                            Tile tile = new Tile();
                                            tile.code = phye.attributeValue("code");
                                            tile.codeNum = phye.attributeValue("codeNum");
                                            tile.length = Float.parseFloat(phye.attributeValue("length"));
                                            tile.width = Float.parseFloat(phye.attributeValue("width"));
                                            tile.url = phye.attributeValue("url");
                                            tilePlan.phy.add(tile);
                                        }
                                        break;
                                    case "logtile":
                                        List<Element> logList = tpe.elements();
                                        for (Element loge : logList) {
                                            LogicalTile logicalTile = new LogicalTile();
                                            logicalTile.code = loge.attributeValue("code");
                                            logicalTile.isMain = loge.attributeValue("isMain").equals("true");
                                            logicalTile.rotate = Float.parseFloat(loge.attributeValue("rotate"));
                                            logicalTile.length = Float.parseFloat(loge.attributeValue("length"));
                                            logicalTile.width = Float.parseFloat(loge.attributeValue("width"));
                                            logicalTile.dirx = Float.parseFloat(loge.attributeValue("dirx"));
                                            logicalTile.diry = Float.parseFloat(loge.attributeValue("diry"));
                                            logicalTile.dirz = Float.parseFloat(loge.attributeValue("dirz"));
                                            logicalTile.notchStyle = Integer.parseInt(loge.attributeValue("notchStyle"));
                                            if (loge.attribute("randRotate") != null) {
                                                logicalTile.randRotate = loge.attributeValue("randRotate").equals("true");
                                            }
                                            tilePlan.logtile.add(logicalTile);
                                        }
                                        break;
                                    case "tileregion":
                                        List<Element> trList = tpe.elements();
                                        ArrayList<Point> pointsList = new ArrayList<>();
                                        for (Element trne : trList) {
                                            Point point = new Point();
                                            point.x = Double.parseDouble(trne.attributeValue("x"));
                                            point.y = Double.parseDouble(trne.attributeValue("y"));
                                            pointsList.add(point);
                                        }
                                        tileplanPointsList.add(pointsList);
                                        break;
                                    case "direxp1":
                                        tilePlan.dirExp1 = new DirExp1();
                                        tilePlan.dirExp1.symbolVector3D = new SymbolVector3D();
                                        Element symbol3d = tpe.element("SymbolVector3D");
                                        String u = symbol3d.attributeValue("u");
                                        String v = symbol3d.attributeValue("v");
                                        tilePlan.dirExp1.symbolVector3D.calculateValues(u, v, tilePlan.symbolMaps);
                                        break;
                                    case "direxp2":
                                        tilePlan.dirExp2 = new DirExp2();
                                        tilePlan.dirExp2.symbolVector3D = new SymbolVector3D();
                                        Element symbol3d2 = tpe.element("SymbolVector3D");
                                        String u2 = symbol3d2.attributeValue("u");
                                        String v2 = symbol3d2.attributeValue("v");
                                        tilePlan.dirExp2.symbolVector3D.calculateValues(u2, v2, tilePlan.symbolMaps);
                                        break;
                                }
                            }
                        }
                        tilePlan.bindingPhyLogicalPackages();
                        tilePlanArrayList.add(tilePlan);
                        break;
                }
            }
        }
    }

    /**
     * TODO 转化数据并执行显示
     */
    private void toShow() {
        if (floorPlan == null)
            return;
        try {
            // 根据所有房间围点列表，计算平移量及平移后的围点
            ArrayList<FloorData> floorDataArrayList = floorPlan.floorDataArrayList;
            if (floorDataArrayList == null || floorDataArrayList.size() == 0) {
                return;
            }
            ArrayList<ArrayList<Point>> housesPointsList = new ArrayList<>();
            ArrayList<ArrayList<Point>> nooffsetPointsList = new ArrayList<>();
            ArrayList<Point> allList = new ArrayList<>();
            for (FloorData floorData : floorDataArrayList) {
                ArrayList<RoundPointData> roundPointDataList = floorData.roundPointDataList;
                ArrayList<Point> pointsList = new ArrayList<>();
                if (roundPointDataList != null && roundPointDataList.size() > 0) {
                    for (RoundPointData rpd : roundPointDataList) {
                        pointsList.add(rpd.toPoint());
                    }
                    nooffsetPointsList.add(pointsList);
                    allList.addAll(pointsList);
                }
            }
            RectD box = new PointList(allList).getRectBox();
            double offsetX = -box.centerX();
            double offsetY = -box.centerY();
            for (ArrayList<Point> pointsList : nooffsetPointsList) {
                ArrayList<Point> transList = new ArrayList<>();
                for (Point point : pointsList) {
                    transList.add(new Point(-1.0d * (point.x + offsetX), point.y + offsetY)); // x轴与接单王相反
                }
                PointList pointList = new PointList(transList);
                pointList.setPointsList(pointList.antiClockwise());
                housesPointsList.add(pointList.offsetList(true, 12));
            }
            // 创建房间
            ArrayList<House> housesList = new ArrayList<>();
            if (housesPointsList != null && housesPointsList.size() > 0) {
                for (ArrayList<Point> pointsList : housesPointsList) {
                    House house = houseDatasManager.add(pointsList);
                    housesList.add(house);
                    houseDatasManager.gpcClosedCheck(house);
                }
            }
            // 创建数据分组，并执行名称、铺砖设置
            ArrayList<HouseXmlCreator> houseXmlCreatorArrayList = new ArrayList<>();
            for (int i = 0; i < housesList.size(); i++) {
                House house = housesList.get(i);
                FloorData floorData = floorDataArrayList.get(i);
                houseXmlCreatorArrayList.add(new HouseXmlCreator(mContext, house, floorData, houseDatasManager));
            }
            // 增加模型，暂不处理
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
