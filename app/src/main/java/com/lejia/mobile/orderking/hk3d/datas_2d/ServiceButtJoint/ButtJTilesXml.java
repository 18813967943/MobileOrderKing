package com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Base64;

import com.lejia.mobile.orderking.classes.ResUrlNodeXml;
import com.lejia.mobile.orderking.hk3d.datas_2d.Ground;
import com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.classes.AttachDirectionExp;
import com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.classes.Dir1;
import com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.classes.Dir2;
import com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.classes.DirExp1;
import com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.classes.DirExp2;
import com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.classes.LengthExp;
import com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.classes.LogicalTile;
import com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.classes.Style;
import com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.classes.StyleData;
import com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.classes.SymbolExp;
import com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.classes.SymbolVector3D;
import com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.classes.Tile;
import com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.classes.TilePlan;
import com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.classes.WaveMutliPlan;
import com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.classes.WidthExp;
import com.lejia.mobile.orderking.https.KosapRequest;
import com.lejia.mobile.orderking.https.OnKosapResponseListener;
import com.lejia.mobile.orderking.utils.FetchLocal;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Author by HEKE
 *
 * @time 2018/11/19 16:37
 * TODO:  下载对接服务器的铺砖xml文件管理对象
 */
public class ButtJTilesXml {

    /**
     * 常态铺砖
     */
    public static final int NORMAL = 1;

    /**
     * 样式铺砖
     */
    public static final int STYLES = 0;

    /**
     * 波打线铺砖
     */
    public static final int WAVELINES = 2;

    /**
     * 方案铺砖
     */
    public static final int SCHEME = 3;

    private Context mContext;
    private ResUrlNodeXml.ResPath resPath; // 对应
    private String cachePath; // 缓存所在目录路径
    private String nodeName; // 资源所在资源服务器对应的节点名称
    private String cahceFilePath; // 文件缓存
    private int mode; // 样式、波打线、方案区分
    private Ground ground; // 设置对应的地面对象

    /**
     * xml内部所有使用资源加载对象
     */
    private ResourceLoader resourceLoader;

    /**
     * 多层波打线层编号
     */
    private int layerCount;

    public ButtJTilesXml(Context context, ResUrlNodeXml.ResPath resPath, String cachePath, int mode, Ground ground) {
        this.mContext = context;
        this.ground = ground;
        this.resPath = resPath;
        this.mode = mode;
        this.cachePath = cachePath;
        this.cahceFilePath = this.cachePath + resPath.name + ".xmls";
        String[] splitor = this.cachePath.split("[//]");
        this.nodeName = splitor[splitor.length - 1];
        download();
    }

    /**
     * 加载
     */
    private void download() {
        File file = new File(cahceFilePath);
        if (file.exists()) {
            new FetchLocal(cahceFilePath).pullAsync(new FetchLocal.OnAsyncPullListener() {
                @Override
                public void pullCompleted(String contents) {
                    parseXml(contents);
                }
            });
        } else {
            HashMap<String, String> params = new HashMap<>();
            params.put("filename", resPath.normalDatas.linkedDataUrl);
            new KosapRequest(mContext, "Download", params, new OnKosapResponseListener() {
                @Override
                public void response(String result, boolean error) {
                    if (!error) {
                        new FetchLocal(cahceFilePath).push(result);
                        parseXml(result);
                    }
                }

                @Override
                public void useLocal() {
                }
            }).request();
        }
    }

    /**
     * 解析xml数据
     *
     * @param base64Xml 数据流
     */
    @SuppressLint("StaticFieldLeak")
    private void parseXml(String base64Xml) {
        new AsyncTask<String, Integer, String>() {
            @Override
            protected String doInBackground(String... params) {
                try {
                    String xml = new String(Base64.decode(params[0], Base64.DEFAULT));
                    // 方案
                    if (mode == SCHEME) {

                    }
                    // 波打线
                    else if (mode == WAVELINES) {
                        parseWaveMutliPlanXml(xml);
                    }
                    // 样式
                    else if (mode == STYLES) {

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
            }
        }.execute(base64Xml);
    }

    /*******************************************
     *  TODO 波打线处理
     * ******************************************/

    /**
     * 波打线铺砖数据解析结果存储对象
     */
    private WaveMutliPlan waveMutliPlan;

    /**
     * 波打线解析
     *
     * @param xml
     */
    private void parseWaveMutliPlanXml(String xml) {
        try {
            // 释放之前的加载数据
            if (waveMutliPlan != null) {
                waveMutliPlan.release();
                waveMutliPlan = null;
            }
            // 重新解析
            waveMutliPlan = new WaveMutliPlan();
            SAXReader saxReader = new SAXReader();
            ByteArrayInputStream bais = new ByteArrayInputStream(xml.getBytes());
            Document document = saxReader.read(bais);
            Element element = document.getRootElement();
            String rootName = element.getName().toLowerCase();
            // 单层波打线
            if (rootName.equals("tileplan")) {
                TilePlan tilePlan = parseTilePlan(element, true);
                waveMutliPlan.tilePlanArrayList.add(tilePlan);
            }
            // 多层波打线
            else if (rootName.equals("wavemutliplan")) {
                // 读取混合铺砖主信息
                waveMutliPlan.planType = Integer.parseInt(element.attributeValue("planType"));
                waveMutliPlan.type = Integer.parseInt(element.attributeValue("Type"));
                waveMutliPlan.guid = element.attributeValue("guid");
                // 遍历子元素
                List<Element> elementList = element.elements();
                for (Element e : elementList) {
                    String ename = e.getName();
                    if (ename.toLowerCase().equals("tileplan")) {
                        TilePlan tilePlan = parseTilePlan(e, false);
                        waveMutliPlan.tilePlanArrayList.add(tilePlan);
                    }
                }
            }
            // 执行资源数据拉取操作
            createResourcesLoaderTask(waveMutliPlan.tilePlanArrayList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 执行资源数据加载
     *
     * @param tilePlanArrayList xml中所有对应的铺砖计划数据对象列表
     */
    private void createResourcesLoaderTask(ArrayList<TilePlan> tilePlanArrayList) {
        if (tilePlanArrayList == null || tilePlanArrayList.size() == 0)
            return;
        // 如果资源加载器依然在工作，强制中断操作。 用于多次点击不同方案所造成的顺序错乱及资源占用问题。
        if (resourceLoader != null) {
            resourceLoader.RequestForceInterrupt();
            resourceLoader = null;
        }
        // 执行新任务
        resourceLoader = new ResourceLoader(tilePlanArrayList, nodeName, new ResourceLoader.OnResourceLoaderCompletedListener() {
            @Override
            public void compeleted(final boolean isInterrupted) {
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // 根据当前加载完成的铺砖方案，回调并执行铺砖操作
                        if (!isInterrupted && ground != null) {
                            // 设置波打线
                            if (mode == WAVELINES) {
                                ground.setWaveLinesPaveRes(waveMutliPlan);
                            }
                        }
                    }
                });
            }
        });
        resourceLoader.start();
    }


    /**
     * TODO TilePlan 基础数据解析
     *
     * @param tileplanElement tileplan元素接口
     * @param singleWaveLine  是否单层波打线
     * @return 返回解析好的基础铺砖数据对象
     */
    private TilePlan parseTilePlan(Element tileplanElement, boolean singleWaveLine) {
        if (tileplanElement == null)
            return null;
        TilePlan tilePlan = new TilePlan();
        try {
            // 读取计划元素数据
            tilePlan.code = tileplanElement.attributeValue("code");
            tilePlan.type = tileplanElement.attributeValue("type");
            tilePlan.name = tileplanElement.attributeValue("name");
            tilePlan.gap = Float.parseFloat(tileplanElement.attributeValue("gap"));
            tilePlan.locate = Integer.parseInt(tileplanElement.attributeValue("locate"));
            tilePlan.rotate = Float.parseFloat(tileplanElement.attributeValue("rotate"));
            // 遍历执行子标签数据
            List<Element> elementArrayList = tileplanElement.elements();
            for (Element element : elementArrayList) {
                String name = element.getName().toLowerCase();
                switch (name) {
                    // 数值标记
                    case "symbol":
                        String key = element.attributeValue("key");
                        String value = element.attributeValue("value");
                        tilePlan.putSymbol(key, value);
                        break;
                    // 物理砖信息
                    case "phy":
                        List<Element> phyList = element.elements();
                        for (Element phye : phyList) {
                            Tile tile = new Tile();
                            tile.code = phye.attributeValue("code");
                            tile.codeNum = phye.attributeValue("codeNum");
                            tile.length = Float.parseFloat(phye.attributeValue("length"));
                            tile.width = Float.parseFloat(phye.attributeValue("width"));
                            if (phye.attribute("url") != null) {
                                tile.url = phye.attributeValue("url");
                            }
                            tilePlan.phy.add(tile);
                        }
                        break;
                    // 逻辑砖信息
                    case "logtile":
                        List<Element> logicalTilesElementList = element.elements();
                        for (Element lgte : logicalTilesElementList) {
                            // 新建逻辑砖对象
                            LogicalTile logicalTile = new LogicalTile();
                            logicalTile.code = lgte.attributeValue("code");
                            logicalTile.isMain = lgte.attributeValue("isMain").equals("true");
                            logicalTile.rotate = Float.parseFloat(lgte.attributeValue("rotate"));
                            logicalTile.length = Float.parseFloat(lgte.attributeValue("length"));
                            logicalTile.width = Float.parseFloat(lgte.attributeValue("width"));
                            logicalTile.dirx = Float.parseFloat(lgte.attributeValue("dirx"));
                            logicalTile.diry = Float.parseFloat(lgte.attributeValue("diry"));
                            logicalTile.dirz = Float.parseFloat(lgte.attributeValue("dirz"));
                            logicalTile.notchStyle = Integer.parseInt(lgte.attributeValue("notchStyle"));
                            // 遍历子标签元素
                            String lgteName = lgte.getName().toLowerCase();
                            switch (lgteName) {
                                // 长度数值标记
                                case "lengthexp":
                                    logicalTile.lengthExp = new LengthExp();
                                    Element lengthExp = lgte.elements().get(0);
                                    logicalTile.lengthExp.symbolExp = new SymbolExp(lengthExp.attributeValue("sym"));
                                    break;
                                // 宽度数值标记
                                case "widthexp":
                                    logicalTile.widthExp = new WidthExp();
                                    Element widthExp = lgte.elements().get(0);
                                    logicalTile.widthExp.symbolExp = new SymbolExp(widthExp.attributeValue("sym"));
                                    break;
                                // 自身偏移量数据对象
                                case "attachdirectionexp":
                                    logicalTile.attachDirectionExp = new AttachDirectionExp();
                                    Element attachDirectionExpElement = lgte.elements().get(0);
                                    String u = attachDirectionExpElement.attributeValue("u");
                                    String v = attachDirectionExpElement.attributeValue("v");
                                    logicalTile.attachDirectionExp.setSymbolVector3DValues(u, v, tilePlan.symbolMaps);
                                    break;
                                // 三角化运算数据对象
                                case "styledata":
                                    logicalTile.styleData = new StyleData();
                                    Element styleElement = lgte.elements().get(0);
                                    String type = styleElement.attributeValue("type");
                                    int defType = Integer.parseInt(styleElement.attributeValue("defType"));
                                    String yxExp0 = styleElement.attributeValue("yxExp0");
                                    String yxExp1 = styleElement.attributeValue("yxExp1");
                                    String zxExp0 = styleElement.attributeValue("zxExp0");
                                    String zxExp1 = styleElement.attributeValue("zxExp1");
                                    logicalTile.styleData.style = new Style(type, defType, yxExp0, yxExp1, zxExp0, zxExp1);
                                    break;
                            }
                            // 加入逻辑砖
                            tilePlan.logtile.add(logicalTile);
                        }
                        break;
                    // 起铺方向偏置数据一
                    case "direxp1":
                        tilePlan.dirExp1 = new DirExp1();
                        Element dexpE = element.elements().get(0);
                        tilePlan.dirExp1.symbolVector3D = new SymbolVector3D();
                        String u = dexpE.attributeValue("u");
                        String v = dexpE.attributeValue("v");
                        tilePlan.dirExp1.symbolVector3D.calculateValues(u, v, tilePlan.symbolMaps);
                        break;
                    // 起铺方向偏置数据二
                    case "direxp2":
                        tilePlan.dirExp2 = new DirExp2();
                        Element dexpE2 = element.elements().get(0);
                        tilePlan.dirExp2.symbolVector3D = new SymbolVector3D();
                        String u2 = dexpE2.attributeValue("u");
                        String v2 = dexpE2.attributeValue("v");
                        tilePlan.dirExp2.symbolVector3D.calculateValues(u2, v2, tilePlan.symbolMaps);
                        break;
                    // 起铺方向一
                    case "dir1":
                        tilePlan.dir1 = new Dir1();
                        tilePlan.dir1.x = Float.parseFloat(element.attributeValue("x"));
                        tilePlan.dir1.y = Float.parseFloat(element.attributeValue("y"));
                        tilePlan.dir1.z = Float.parseFloat(element.attributeValue("z"));
                        break;
                    // 起铺方向二
                    case "dir2":
                        tilePlan.dir2 = new Dir2();
                        tilePlan.dir2.x = Float.parseFloat(element.attributeValue("x"));
                        tilePlan.dir2.y = Float.parseFloat(element.attributeValue("y"));
                        tilePlan.dir2.z = Float.parseFloat(element.attributeValue("z"));
                        break;
                }
            }
            // 组合数据打包操作
            tilePlan.bindingPhyLogicalPackages();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tilePlan;
    }
}
