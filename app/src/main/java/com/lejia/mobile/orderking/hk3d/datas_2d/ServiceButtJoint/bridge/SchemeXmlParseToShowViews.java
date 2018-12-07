package com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.bridge;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;

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
import com.lejia.mobile.orderking.utils.FileUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

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

    // 临时解析文件路径
    private String tempCachePath;

    /**
     * 解析总数据对象
     */
    private FloorPlan floorPlan;

    public SchemeXmlParseToShowViews(Context context, String xml) {
        OrderKingApplication orderKingApplication = (OrderKingApplication) context.getApplicationContext();
        this.mContext = orderKingApplication;
        this.designer3DRender = orderKingApplication.getDesigner3DSurfaceView().getDesigner3DRender();
        this.houseDatasManager = designer3DRender.getHouseDatasManager();
        tempCachePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Yi3D/xmlparse/cache.xml";
        FileUtils.createFile(tempCachePath);
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
                    DocumentBuilderFactory mDocumentBuilderFactory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder mDocumentBuilder = mDocumentBuilderFactory.newDocumentBuilder();
                    Document mDocument = mDocumentBuilder.parse(new ByteArrayInputStream(xmls[0].getBytes()));
                    Element mElement = mDocument.getDocumentElement();
                    ArrayList<Element> elementsList = getList(mElement);
                    if (elementsList != null) {
                        for (Element e : elementsList) {
                            String nodeName = e.getNodeName().toLowerCase();
                            switch (nodeName) {
                                case "version":
                                    floorPlan.version = e.getAttribute("code");
                                    break;
                                case "sceneid":
                                    floorPlan.sceneID = new SceneID();
                                    floorPlan.sceneID.id = e.getAttribute("id");
                                    floorPlan.sceneID.numID = e.getAttribute("numID");
                                    break;
                                case "author":
                                    floorPlan.authorCode = e.getAttribute("code");
                                    break;
                                case "preview":
                                    floorPlan.previewUrl = e.getAttribute("url");
                                    break;
                                case "ceilingheight":
                                    floorPlan.ceilingHeight = Integer.parseInt(e.getAttribute("value"));
                                    break;
                                case "layerdata":
                                    floorPlan.layerData = new LayerData();
                                    floorPlan.layerData.groundLayerVisible = e.getAttribute("GroundLayerVisible").equals("true");
                                    floorPlan.layerData.wallLayerVisible = e.getAttribute("WallLayerVisible").equals("true");
                                    floorPlan.layerData.furnitureLayerVisible = e.getAttribute("FurnitureLayerVisible").equals("true");
                                    floorPlan.layerData.ceilingLayerVisible = e.getAttribute("CeilingLayerVisible").equals("true");
                                    floorPlan.layerData.dimensionLayerVisible = e.getAttribute("DimensionLayerVisible").equals("true");
                                    floorPlan.layerData.virtualLightLayerVisible = e.getAttribute("VirtualLightLayerVisible").equals("true");
                                    floorPlan.layerData.rectLightLayerVisible = e.getAttribute("RectLightLayerVisible").equals("true");
                                    break;
                                case "wall":
                                    floorPlan.wallNum = Integer.parseInt(e.getAttribute("num"));
                                    break;
                                case "walldata":
                                    WallData wallData = new WallData();
                                    wallData.id = e.getAttribute("ID");
                                    wallData.startX = Float.parseFloat(e.getAttribute("StartX"));
                                    wallData.startY = Float.parseFloat(e.getAttribute("StartY"));
                                    wallData.startZ = Float.parseFloat(e.getAttribute("StartZ"));
                                    wallData.endX = Float.parseFloat(e.getAttribute("EndX"));
                                    wallData.endY = Float.parseFloat(e.getAttribute("EndY"));
                                    wallData.endZ = Float.parseFloat(e.getAttribute("EndZ"));
                                    wallData.thickness = Float.parseFloat(e.getAttribute("Thickness"));
                                    wallData.offSide = Float.parseFloat(e.getAttribute("OffSide"));
                                    floorPlan.wallDataArrayList.add(wallData);
                                    break;
                                case "floor":
                                    floorPlan.floorNum = Integer.parseInt(e.getAttribute("num"));
                                    break;
                                case "floordata":
                                    readFloorDatas(e);
                                    break;
                                case "furniture":
                                    floorPlan.furnitureNum = Integer.parseInt(e.getAttribute("num"));
                                    break;
                                case "furnituredata":
                                    FurnitureData furnitureData = new FurnitureData();
                                    furnitureData.name = e.getAttribute("Name");
                                    furnitureData.code = e.getAttribute("Code");
                                    furnitureData.ERPCode = e.getAttribute("ERPCode");
                                    furnitureData.ID = e.getAttribute("ID");
                                    furnitureData.catalog = e.getAttribute("Catalog");
                                    furnitureData.URL = e.getAttribute("URL");
                                    furnitureData.PositionX = Float.parseFloat(e.getAttribute("PositionX"));
                                    furnitureData.PositionY = Float.parseFloat(e.getAttribute("PositionY"));
                                    furnitureData.PositionZ = Float.parseFloat(e.getAttribute("PositionZ"));
                                    furnitureData.OffGround = Float.parseFloat(e.getAttribute("OffGround"));
                                    furnitureData.Rotation = Float.parseFloat(e.getAttribute("Rotation"));
                                    furnitureData.Length = Float.parseFloat(e.getAttribute("Length"));
                                    furnitureData.Width = Float.parseFloat(e.getAttribute("Width"));
                                    furnitureData.Height = Float.parseFloat(e.getAttribute("Height"));
                                    furnitureData.LinkWallID = e.getAttribute("LinkWallID");
                                    furnitureData.LinkWall2ID = e.getAttribute("LinkWall2ID");
                                    furnitureData.Mirror = Integer.parseInt(e.getAttribute("Mirror"));
                                    furnitureData.CeilingLayerHeight = Integer.parseInt(e.getAttribute("CeilingLayerHeight"));
                                    furnitureData.CeilingLightDeep = Integer.parseInt(e.getAttribute("CeilingLightDeep"));
                                    furnitureData.CeilingTipHeight = Integer.parseInt(e.getAttribute("CeilingTipHeight"));
                                    furnitureData.CeilingLightColor = Integer.parseInt(e.getAttribute("CeilingLightColor"));
                                    furnitureData.CeilingLightWattage = Integer.parseInt(e.getAttribute("CeilingLightWattage"));
                                    furnitureData.LinkedFloorID = e.getAttribute("LinkedFloorID");
                                    furnitureData.PickCornerPointX = Integer.parseInt(e.getAttribute("PickCornerPointX"));
                                    furnitureData.PickCornerPointY = Integer.parseInt(e.getAttribute("PickCornerPointY"));
                                    furnitureData.PickCornerPointZ = Integer.parseInt(e.getAttribute("PickCornerPointZ"));
                                    furnitureData.PickDir1X = Integer.parseInt(e.getAttribute("PickDir1X"));
                                    furnitureData.PickDir1Y = Integer.parseInt(e.getAttribute("PickDir1Y"));
                                    furnitureData.PickDir1Z = Integer.parseInt(e.getAttribute("PickDir1Z"));
                                    furnitureData.PickDir2X = Integer.parseInt(e.getAttribute("PickDir2X"));
                                    furnitureData.PickDir2Y = Integer.parseInt(e.getAttribute("PickDir2Y"));
                                    furnitureData.PickDir2Z = Integer.parseInt(e.getAttribute("PickDir2Z"));
                                    furnitureData.WindowName = e.getAttribute("WindowName");
                                    furnitureData.WindowLine0X = Integer.parseInt(e.getAttribute("WindowLine0X"));
                                    furnitureData.WindowLine0Y = Integer.parseInt(e.getAttribute("WindowLine0Y"));
                                    furnitureData.WindowLine0Z = Integer.parseInt(e.getAttribute("WindowLine0Z"));
                                    furnitureData.WindowLine1X = Integer.parseInt(e.getAttribute("WindowLine1X"));
                                    furnitureData.WindowLine1Y = Integer.parseInt(e.getAttribute("WindowLine1Y"));
                                    furnitureData.WindowLine1Z = Integer.parseInt(e.getAttribute("WindowLine1Z"));
                                    furnitureData.WindowLine2X = Integer.parseInt(e.getAttribute("WindowLine2X"));
                                    furnitureData.WindowLine2Y = Integer.parseInt(e.getAttribute("WindowLine2Y"));
                                    furnitureData.WindowLine2Z = Integer.parseInt(e.getAttribute("WindowLine2Z"));
                                    furnitureData.WindowOffGround = Integer.parseInt(e.getAttribute("WindowOffGround"));
                                    furnitureData.WindowHeight = Integer.parseInt(e.getAttribute("WindowHeight"));
                                    furnitureData.WindowFloatDistance = Integer.parseInt(e.getAttribute("WindowFloatDistance"));
                                    furnitureData.WindowFloatSide = Integer.parseInt(e.getAttribute("WindowFloatSide"));
                                    furnitureData.WindowWallThickness = Integer.parseInt(e.getAttribute("WindowWallThickness"));
                                    furnitureData.BeamThickness = Integer.parseInt(e.getAttribute("BeamThickness"));
                                    furnitureData.BeamRotation = Integer.parseInt(e.getAttribute("BeamRotation"));
                                    furnitureData.HadCurveRailingPoints = Integer.parseInt(e.getAttribute("HadCurveRailingPoints"));
                                    furnitureData.BuildCurtainLength = Integer.parseInt(e.getAttribute("BuildCurtainLength"));
                                    furnitureData.BuildCurtainPositionX = Integer.parseInt(e.getAttribute("BuildCurtainPositionX"));
                                    furnitureData.BuildCurtainPositionY = Integer.parseInt(e.getAttribute("BuildCurtainPositionY"));
                                    furnitureData.BuildCurtainPositionZ = Integer.parseInt(e.getAttribute("BuildCurtainPositionZ"));
                                    furnitureData.BuildCurtainOffGround = Integer.parseInt(e.getAttribute("BuildCurtainOffGround"));
                                    furnitureData.VirtualLightColor = Integer.parseInt(e.getAttribute("VirtualLightColor"));
                                    furnitureData.VirtualLightWattage = Integer.parseInt(e.getAttribute("VirtualLightWattage"));
                                    furnitureData.OffDistance = Integer.parseInt(e.getAttribute("OffDistance"));
                                    furnitureData.LinkedThresholdMaterialURL = e.getAttribute("LinkedThresholdMaterialURL");
                                    furnitureData.LightRatio = Integer.parseInt(e.getAttribute("LightRatio"));
                                    furnitureData.LightColor = Integer.parseInt(e.getAttribute("LightColor"));
                                    furnitureData.LightDoubleSide = e.getAttribute("LightDoubleSide");
                                    furnitureData.comonID = e.getAttribute("comonID");
                                    furnitureData.IsPolyMode = e.getAttribute("IsPolyMode");
                                    furnitureData.moduleCode = e.getAttribute("ModuleCode");
                                    furnitureData.curtainModuleCode = e.getAttribute("CurtainModuleCode");
                                    furnitureData.curtainMainClothCode = e.getAttribute("CurtainMainClothCode");
                                    floorPlan.furnitureDataArrayList.add(furnitureData);
                                    break;
                            }
                        }
                    }
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
     * 获取元素列表
     *
     * @param e
     * @return
     */
    private ArrayList<Element> getList(Element e) {
        if (e == null)
            return null;
        ArrayList<Element> elementList = new ArrayList<>();
        NodeList nodeList = e.getChildNodes();
        if (nodeList != null) {
            for (int i = 0; i < nodeList.getLength(); i++) {
                if (nodeList.item(i).getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) nodeList.item(i);
                    elementList.add(element);
                }
            }
        }
        if (elementList.size() == 0)
            return null;
        return elementList;
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
        floorData.roomName = e.getAttribute("RoomName");
        floorData.centerX = Float.parseFloat(e.getAttribute("CenterX"));
        floorData.centerY = Float.parseFloat(e.getAttribute("CenterY"));
        floorData.centerZ = Float.parseFloat(e.getAttribute("CenterZ"));
        floorData.area = Float.parseFloat(e.getAttribute("Area"));
        floorData.materialType = Integer.parseInt(e.getAttribute("MaterialType"));
        floorData.materialCode = e.getAttribute("MaterialCode");
        floorData.url = e.getAttribute("URL");
        floorData.floorID = e.getAttribute("FloorID");
        floorData.roomHeight = Integer.parseInt(e.getAttribute("RoomHeight"));
        floorData.buildAngularLines = e.getAttribute("BuildAngularLines").equals("true");
        floorData.buildCeilingLines = e.getAttribute("BuildCeilingLines").equals("true");
        floorData.angularLineType = Integer.parseInt(e.getAttribute("AngularLineType"));
        floorData.ceilingLinesMatCode = e.getAttribute("CeilingLinesMatCode");
        floorData.ceilingLinesMatUrl = e.getAttribute("CeilingLinesMatUrl");
        floorData.angularLinesMatCode = e.getAttribute("AngularLinesMatCode");
        floorData.angularLinesMatUrl = e.getAttribute("AngularLinesMatUrl");
        floorData.isSpecialMode = e.getAttribute("IsSpecialMode").equals("true");
        floorData.materialMode = Integer.parseInt(e.getAttribute("MaterialMode"));
        // 其他数据对象
        ArrayList<Element> elementList = getList(e);
        for (Element element : elementList) {
            String ename = element.getNodeName().toLowerCase();
            switch (ename) {
                case "tileviewpanel":
                    TileViewPanel tileViewPanel = new TileViewPanel(); // 铺砖面板数据对象
                    ArrayList<Element> tileElementsList = getList(element);
                    for (Element te : tileElementsList) {
                        String tename = te.getNodeName().toLowerCase();
                        switch (tename) {
                            case "tilelayers":
                                tileViewPanel.tileLayers = new TileLayers();
                                Element layerElement = getList(te).get(0);
                                TileLayer tileLayer = new TileLayer();
                                tileLayer.main = layerElement.getAttribute("main").equals("true");
                                tileLayer.isMainArea = layerElement.getAttribute("isMainArea").equals("true");
                                tileLayer.hasOffset = layerElement.getAttribute("hasOffset").equals("true");
                                ArrayList<Element> layerElementList = getList(layerElement);
                                for (Element laye : layerElementList) {
                                    String layname = laye.getNodeName().toLowerCase();
                                    switch (layname) {
                                        case "tileregion":
                                            TileRegion tileRegion = new TileRegion();
                                            tileRegion.isAddArea = laye.getAttribute("isAddArea").equals("true");
                                            tileRegion.isWallTile = laye.getAttribute("isWallTile").equals("true");
                                            tileRegion.isEmportTile = laye.getAttribute("isEmportTile").equals("true");
                                            tileRegion.area = Long.parseLong(laye.getAttribute("area"));
                                            tileRegion.opType = Integer.parseInt(laye.getAttribute("opType"));
                                            ArrayList<Element> treList = getList(laye);
                                            for (Element tre : treList) {
                                                String trename = tre.getNodeName().toLowerCase();
                                                switch (trename) {
                                                    case "shape":
                                                        Shape shape = new Shape();
                                                        ArrayList<Element> shapeEList = getList(tre);
                                                        for (Element se : shapeEList) {
                                                            Point point = new Point();
                                                            point.x = Double.parseDouble(se.getAttribute("x"));
                                                            point.y = Double.parseDouble(se.getAttribute("y"));
                                                            shape.add(point);
                                                        }
                                                        tileRegion.shape = shape;
                                                        break;
                                                    case "plan":
                                                        Plan plan = new Plan();
                                                        parseTilePlan(plan, tre, false);
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
                                            waveLine.planType = Integer.parseInt(laye.getAttribute("planType"));
                                            waveLine.openDir = Integer.parseInt(laye.getAttribute("openDir"));
                                            waveLine.parentGuid = Integer.parseInt(laye.getAttribute("parentGuid"));
                                            parseTilePlan(waveLine, laye, true);
                                            tileLayer.waveLine = waveLine;
                                            break;
                                    }
                                }
                                tileViewPanel.tileLayers.tileLayersList.add(tileLayer);
                                break;
                            case "roomlayer":
                                tileViewPanel.roomLayer = new RoomLayer();
                                tileViewPanel.roomLayer.roomRegion = new RoomRegion();
                                ArrayList<Element> tpEList = getList(getList(te).get(0));
                                for (Element tpe : tpEList) {
                                    TPoint tPoint = new TPoint();
                                    tPoint.type = Integer.parseInt(tpe.getAttribute("type"));
                                    tPoint.xpt1 = Float.parseFloat(tpe.getAttribute("xpt1"));
                                    tPoint.ypt1 = Float.parseFloat(tpe.getAttribute("ypt1"));
                                    tPoint.xpt2 = Float.parseFloat(tpe.getAttribute("xpt2"));
                                    tPoint.ypt2 = Float.parseFloat(tpe.getAttribute("ypt2"));
                                    tPoint.xpt3 = Float.parseFloat(tpe.getAttribute("xpt3"));
                                    tPoint.ypt3 = Float.parseFloat(tpe.getAttribute("ypt3"));
                                    tileViewPanel.roomLayer.roomRegion.tPointsList.add(tPoint);
                                }
                                break;
                        }
                    }
                    floorData.tileViewPanel = tileViewPanel;
                    break;
                case "roundpointdata":
                    RoundPointData roundPointData = new RoundPointData();
                    roundPointData.x = Integer.parseInt(element.getAttribute("X"));
                    roundPointData.y = Integer.parseInt(element.getAttribute("Y"));
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
     * @param obj       保存数据对象
     * @param element   xml节点
     * @param wavelines
     */
    private void parseTilePlan(Object obj, Element element, boolean wavelines) {
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
        ArrayList<Element> elementList = getList(element);
        if (elementList != null && elementList.size() > 0) {
            for (Element e : elementList) {
                String name = e.getNodeName().toLowerCase();
                switch (name) {
                    case "tileplan":
                        TilePlan tilePlan = new TilePlan();
                        tilePlan.code = e.getAttribute("code");
                        tilePlan.type = e.getAttribute("type");
                        tilePlan.name = e.getAttribute("name");
                        tilePlan.gap = Float.parseFloat(e.getAttribute("gap"));
                        tilePlan.locate = Integer.parseInt(e.getAttribute("locate"));
                        tilePlan.rotate = Float.parseFloat(e.getAttribute("rotate"));
                        if (e.hasAttribute("gapColor"))
                            tilePlan.gapColor = Integer.parseInt(e.getAttribute("gapColor"));
                        // 波打线
                        if (wavelines) {
                            // 多层波打线
                            if (e.hasAttribute("layerCount")) {
                                tilePlan.wavelinesCellsLayout = true;
                                tilePlan.layerCount = Integer.parseInt(e.getAttribute("layerCount"));
                                tilePlan.waveLineOrientation = Integer.parseInt(e.getAttribute("waveLineOrientation"));
                                tilePlan.openDir = Integer.parseInt(e.getAttribute("openDir"));
                                tilePlan.waveWidth = Float.parseFloat(e.getAttribute("waveWidth"));
                                tilePlan.waveType = Integer.parseInt(e.getAttribute("waveType"));
                                tilePlan.gapTileRegion = Integer.parseInt(e.getAttribute("gapTileRegion"));
                            }
                        }
                        // 添加默认砖缝数值
                        tilePlan.putSymbol("G", "" + ((int) tilePlan.gap));
                        // 遍历创建子元素
                        ArrayList<Element> tpList = getList(e);
                        if (tpList != null && tpList.size() > 0) {
                            for (Element tpe : tpList) {
                                String tpename = tpe.getNodeName().toLowerCase();
                                switch (tpename) {
                                    case "symbol":
                                        String key = tpe.getAttribute("key");
                                        String value = tpe.getAttribute("value");
                                        tilePlan.putSymbol(key, value);
                                        break;
                                    case "phy":
                                        ArrayList<Element> phyList = getList(tpe);
                                        for (Element phye : phyList) {
                                            Tile tile = new Tile();
                                            tile.code = phye.getAttribute("code");
                                            tile.codeNum = phye.getAttribute("codeNum");
                                            tile.length = Float.parseFloat(phye.getAttribute("length"));
                                            tile.width = Float.parseFloat(phye.getAttribute("width"));
                                            tile.url = phye.getAttribute("url");
                                            tilePlan.phy.add(tile);
                                        }
                                        break;
                                    case "logtile":
                                        ArrayList<Element> logList = getList(tpe);
                                        for (Element loge : logList) {
                                            LogicalTile logicalTile = new LogicalTile();
                                            logicalTile.code = loge.getAttribute("code");
                                            logicalTile.isMain = loge.getAttribute("isMain").equals("true");
                                            logicalTile.rotate = Float.parseFloat(loge.getAttribute("rotate"));
                                            logicalTile.length = Float.parseFloat(loge.getAttribute("length"));
                                            logicalTile.width = Float.parseFloat(loge.getAttribute("width"));
                                            logicalTile.dirx = Float.parseFloat(loge.getAttribute("dirx"));
                                            logicalTile.diry = Float.parseFloat(loge.getAttribute("diry"));
                                            logicalTile.dirz = Float.parseFloat(loge.getAttribute("dirz"));
                                            logicalTile.notchStyle = Integer.parseInt(loge.getAttribute("notchStyle"));
                                            if (loge.hasAttribute("randRotate")) {
                                                logicalTile.randRotate = loge.getAttribute("randRotate").equals("true");
                                            }
                                            tilePlan.logtile.add(logicalTile);
                                        }
                                        break;
                                    case "tileregion":
                                        ArrayList<Element> trList = getList(tpe);
                                        if (trList != null) {
                                            ArrayList<Point> pointsList = new ArrayList<>();
                                            for (Element trne : trList) {
                                                Point point = new Point();
                                                point.x = Double.parseDouble(trne.getAttribute("x"));
                                                point.y = Double.parseDouble(trne.getAttribute("y"));
                                                pointsList.add(point);
                                            }
                                            tileplanPointsList.add(pointsList);
                                        }
                                        break;
                                    case "direxp1":
                                        tilePlan.dirExp1 = new DirExp1();
                                        tilePlan.dirExp1.symbolVector3D = new SymbolVector3D();
                                        Element symbol3d = getList(tpe).get(0);
                                        String u = symbol3d.getAttribute("u");
                                        String v = symbol3d.getAttribute("v");
                                        tilePlan.dirExp1.symbolVector3D.calculateValues(u, v, tilePlan.symbolMaps);
                                        break;
                                    case "direxp2":
                                        tilePlan.dirExp2 = new DirExp2();
                                        tilePlan.dirExp2.symbolVector3D = new SymbolVector3D();
                                        Element symbol3d2 = getList(tpe).get(0);
                                        String u2 = symbol3d2.getAttribute("u");
                                        String v2 = symbol3d2.getAttribute("v");
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
