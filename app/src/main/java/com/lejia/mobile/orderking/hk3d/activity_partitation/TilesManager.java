package com.lejia.mobile.orderking.hk3d.activity_partitation;

import android.app.Activity;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.lejia.mobile.orderking.R;
import com.lejia.mobile.orderking.adapters.MenuBarAdapter;
import com.lejia.mobile.orderking.adapters.ResPathDatasAdapter;
import com.lejia.mobile.orderking.adapters.RightIconsAdapter;
import com.lejia.mobile.orderking.bases.OrderKingApplication;
import com.lejia.mobile.orderking.classes.ResUrlNodeXml;
import com.lejia.mobile.orderking.classes.ServiceNodesFetcher;
import com.lejia.mobile.orderking.hk3d.datas_2d.Ground;
import com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.ButtJTilesXml;
import com.lejia.mobile.orderking.https.KosapRequest;
import com.lejia.mobile.orderking.https.OnKosapResponseListener;
import com.lejia.mobile.orderking.httpsResult.classes.LJNodes;
import com.lejia.mobile.orderking.utils.FetchLocal;
import com.lejia.mobile.orderking.utils.FileUtils;
import com.lejia.mobile.orderking.widgets.ScrollerGridView;
import com.lejia.mobile.orderking.widgets.TitlesView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Author by HEKE
 *
 * @time 2018/7/12 12:59
 * TODO: 材质菜单管理对象
 */
public class TilesManager {

    /**
     * 画墙类型标记
     */
    public static final int DRAW_RECT = 0;
    public static final int DRAW_NORMAL = 1;
    public static final int DRAW_LINE_BUILD = 2;

    // 当前画墙采取的画墙方式，默认为矩形画墙
    private int drawStatues = DRAW_RECT;

    private Activity mActivity;
    private TitlesView titlesView; // 底部标题栏
    private RelativeLayout menuLayout; // 菜单主控件
    private ListView nodesList; // 底部一级节点列表控件
    private ListView detaileList; // 详细节点列表控件
    private ScrollerGridView tilesGrid; // 具体显示加载控件，可下滑进行分页加载
    private ImageView drawStates; // 屏幕左中用于切换显示画墙方式的按钮
    private Designer3DManager designer3DManager; // 三维控件管理对象
    private ServiceNodesFetcher serviceNodesFetcher; // 对应服务器资源节点对象

    // 加载基础信息
    private void initAttrs() {
        titlesView.setOnTitlesStatusListener(onTitlesStatusListener);
        tilesGrid.setOnItemClickListener(onItemClickListener);
        tilesGrid.setOnScrollerGridListener(onScrollerGridListener);
        nodesList.setOnItemClickListener(onBottomItemClickListener);
        detaileList.setOnItemClickListener(onUpItemClickListener);
    }

    public TilesManager(Activity activity, TitlesView titlesView, RelativeLayout menuLayout
            , ListView nodesList, ListView detaileList, ScrollerGridView tilesGrid
            , ImageView drawStates, Designer3DManager designer3DManager, ServiceNodesFetcher serviceNodesFetcher) {
        this.mActivity = activity;
        this.titlesView = titlesView;
        this.menuLayout = menuLayout;
        this.nodesList = nodesList;
        this.detaileList = detaileList;
        this.tilesGrid = tilesGrid;
        this.drawStates = drawStates;
        this.designer3DManager = designer3DManager;
        this.serviceNodesFetcher = serviceNodesFetcher;
        this.pageCount = ((OrderKingApplication) activity.getApplicationContext()).isPad() ? 20 : 10;
        initAttrs();
    }

    /**
     * 获取当前绘制方式
     */
    public int getDrawStatues() {
        return drawStatues;
    }

    /**
     * 底部标题栏操作监听
     */
    private TitlesView.OnTitlesStatusListener onTitlesStatusListener = new TitlesView.OnTitlesStatusListener() {
        @Override
        public void onItemTitleClick(int position, String title, boolean isfromUser) {
            switch (position) {
                case 0:
                    // 房间
                    drawStates.setVisibility(View.VISIBLE);
                    drawStates.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            drawStatues++;
                            if (drawStatues > DRAW_LINE_BUILD) {
                                drawStatues = DRAW_RECT;
                            }
                            int[] resId = new int[]{R.mipmap.juxinghuafangjian, R.mipmap.huaqiang, R.mipmap.xianjianqiang};
                            drawStates.setBackgroundResource(resId[drawStatues]);
                        }
                    });
                    break;
                case 1:
                    // 门窗

                    break;
                case 2:
                    // 铺砖
                    menuLayout.setVisibility(View.VISIBLE);
                    showTiles();
                    break;
                case 3:
                    // 布置
                    menuLayout.setVisibility(View.VISIBLE);
                    showModels();
                    break;
            }
        }

        @Override
        public void onSelectedCancle(int position, String title) {
            switch (position) {
                case 0:
                    // 房间
                    drawStates.setVisibility(View.GONE);
                    break;
                case 1:
                    // 门窗
                    break;
                case 2:
                    // 铺砖
                case 3:
                    // 布置
                    menuLayout.setVisibility(View.GONE);
                    break;
            }
        }
    };

    /********************************************************
     *  铺砖、布置家具资源窗口
     * ******************************************************/

    public static final int FLAG_TILES = 0; // 铺砖标记
    public static final int FLAG_MODELS = 1; // 模型标记

    private int flag = -1; // 当前窗口显示内容标记类型，默认为未显示过窗口

    // 铺砖
    private int[] tilesDetailsResIds;
    private int[] tilesDetailsSelectResIds;
    private RightIconsAdapter tilesRightIconsAdapter;
    private int[] tilesLevelOnResIds;
    private int[] tilesLevelOnSelectResIds;
    private RightIconsAdapter tilesLevel1RightIconsAdapter;
    private ArrayList<LJNodes> tilesNodesList;
    private int tileVrMode;
    // 模型
    private int[] modelsDetailsResIds;
    private int[] modelsDetailsSelectResIds;
    private RightIconsAdapter modelsRightIconsAdapter;
    private String[] modelsLevelOneTitlesArray;
    private MenuBarAdapter modelsLevelOneAdapter;
    private ArrayList<LJNodes> modelsNodeList;
    private ArrayList<LJNodes> modelsBottomNodeList;
    private LJNodes currentLoadModelsNode;
    private boolean showMoreModels; // 模型空间类型切换标记

    private ArrayList<ResUrlNodeXml.ResPath> resPathArrayList; // 资源路径数据列表

    // 分页信息
    private String cachePath;
    private int pageSize;
    private int pageCount;
    private int currentPage;
    private int checkPage;
    private ArrayList<ArrayList<ResUrlNodeXml.ResPath>> pagesResPathArrayList; // 对资源路径数据列表进行分页后的数据列表
    private ResPathDatasAdapter resPathDatasAdapter;

    /**
     * 显示铺砖
     */
    public void showTiles() {
        if (flag == FLAG_TILES)
            return;
        flag = FLAG_TILES;
        tilesDetailsResIds = new int[]{R.mipmap.yangshi_copy, R.mipmap.huanzhuan, R.mipmap.waveline,
                R.mipmap.fangan, R.mipmap.wode};
        tilesDetailsSelectResIds = new int[]{R.mipmap.yangshi, R.mipmap.huanzhuan_copy, R.mipmap.waveline_copy,
                R.mipmap.rectangle_3_copy_4, R.mipmap.wode_copy};
        tilesLevelOnResIds = new int[]{R.mipmap.quyu, R.mipmap.huagong, R.mipmap.qipu, R.mipmap.jiaodu};
        tilesLevelOnSelectResIds = new int[]{R.mipmap.quyu_copy, R.mipmap.huagong_copy, R.mipmap.qipu_copy, R.mipmap.jiaodu_copy};
        tilesRightIconsAdapter = new RightIconsAdapter(mActivity.getApplicationContext(), tilesDetailsResIds, tilesDetailsSelectResIds);
        tilesLevel1RightIconsAdapter = new RightIconsAdapter(mActivity.getApplicationContext(), tilesLevelOnResIds, tilesLevelOnSelectResIds);
        detaileList.setAdapter(tilesRightIconsAdapter);
        nodesList.setAdapter(tilesLevel1RightIconsAdapter);
        tilesNodesList = new ArrayList<>();
        LJNodes groundTilesNode = serviceNodesFetcher.getGroundAndRoofTilesNode();
        tilesNodesList.add(serviceNodesFetcher.getChildNodeByNameFromParentNode(groundTilesNode, "样式砖"));
        tilesNodesList.add(serviceNodesFetcher.getChildNodeByNameFromParentNode(groundTilesNode, "常用砖"));
        tilesNodesList.add(serviceNodesFetcher.getChildNodeByNameFromParentNode(groundTilesNode, "波打线"));
        tilesNodesList.add(serviceNodesFetcher.getChildNodeByNameFromParentNode(groundTilesNode, "方案"));
        currentPage = -1;
        checkPage = -1;
        currentLoadModelsNode = tilesNodesList.get(0);
        tilesRightIconsAdapter.setSelectePosition(0);
        loadNodeUrlListThenPaging(currentLoadModelsNode);
    }

    /**
     * 显示家具
     */
    public void showModels() {
        if (flag == FLAG_MODELS)
            return;
        flag = FLAG_MODELS;
        modelsDetailsResIds = new int[]{R.mipmap.kecanting, R.mipmap.woshi, R.mipmap.chufang,
                R.mipmap.weishengjian, R.mipmap.gengduo};
        modelsDetailsSelectResIds = new int[]{R.mipmap.kecanting_chosen, R.mipmap.woshi_chosen, R.mipmap.chufang_chosen,
                R.mipmap.weishengjian_chosen, R.mipmap.gengduo_copy};
        modelsLevelOneTitlesArray = mActivity.getResources().getStringArray(R.array.furniture_bottom_titles);
        modelsRightIconsAdapter = new RightIconsAdapter(mActivity, modelsDetailsResIds, modelsDetailsSelectResIds);
        detaileList.setAdapter(modelsRightIconsAdapter);
        modelsLevelOneAdapter = new MenuBarAdapter(mActivity, modelsLevelOneTitlesArray, 12);
        nodesList.setAdapter(modelsLevelOneAdapter);
        modelsNodeList = serviceNodesFetcher.getModelsTypeNodesList(); // 空间类型节点列表
        modelsBottomNodeList = new ArrayList<>(); // 模型底部文字对应节点列表
        modelsBottomNodeList.add(serviceNodesFetcher.getDoorsNode());
        modelsBottomNodeList.add(serviceNodesFetcher.getWindowsNode());
        modelsBottomNodeList.add(serviceNodesFetcher.getTHNode());
        modelsBottomNodeList.add(serviceNodesFetcher.getOneKeylayoutNode());
        // 设置默认选中加载项
        currentLoadModelsNode = modelsNodeList.get(0);
        modelsRightIconsAdapter.setSelectePosition(0);
        loadNodeUrlListThenPaging(currentLoadModelsNode);
    }

    /**
     * 加载列表数据信息并进行分页处理
     *
     * @param node
     */
    private void loadNodeUrlListThenPaging(final LJNodes node) {
        if (node == null)
            return;
        // 缓存根目录
        cachePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Yi3D/Nodes/" + node.getLabel() + "/";
        FileUtils.createDirectory(cachePath);
        // 创建对应数据链接列表
        final String cacheUrlPath = cachePath + "url.xml";
        File urlfile = new File(cacheUrlPath);
        if (urlfile.exists()) {
            new FetchLocal(cacheUrlPath).pullAsync(new FetchLocal.OnAsyncPullListener() {
                @Override
                public void pullCompleted(String contents) {
                    new ResUrlNodeXml(contents, node.getLabel(), new ResUrlNodeXml.OnResUrlNodeXmlParseListener() {
                        @Override
                        public void onParse(ArrayList<ResUrlNodeXml.ResPath> resPathArrayList) {
                            TilesManager.this.resPathArrayList = resPathArrayList;
                            calculatePages();
                        }
                    });
                }
            });
        } else {
            HashMap<String, String> params = new HashMap<>();
            params.put("nodeName", node.getId());
            KosapRequest fetchItemsRequest = new KosapRequest(mActivity, "DownloadNodeURLXML", params, new OnKosapResponseListener() {
                @Override
                public void response(String result, boolean error) {
                    if (!error) {
                        new FetchLocal(cacheUrlPath).push(result);
                        new ResUrlNodeXml(result, node.getLabel(), new ResUrlNodeXml.OnResUrlNodeXmlParseListener() {
                            @Override
                            public void onParse(ArrayList<ResUrlNodeXml.ResPath> resPathArrayList) {
                                TilesManager.this.resPathArrayList = resPathArrayList;
                                calculatePages();
                            }
                        });
                    }
                }

                @Override
                public void useLocal() {
                }
            });
            fetchItemsRequest.request();
        }
    }

    /**
     * 计算分页数据
     */
    private void calculatePages() {
        if (resPathArrayList == null || resPathArrayList.size() == 0)
            return;
        int size = resPathArrayList.size();
        int mod = size % pageCount;
        if (mod == 0) {
            pageSize = size / pageCount;
        } else {
            pageSize = size / pageCount + 1;
        }
        pagesResPathArrayList = new ArrayList<>();
        for (int i = 0; i < pageSize; i++) {
            ArrayList<ResUrlNodeXml.ResPath> resPathsList = new ArrayList<>();
            int begain = i * pageCount;
            int end = (i == pageSize - 1) ? size : (i + 1) * pageCount;
            for (int j = begain; j < end; j++) {
                resPathsList.add(resPathArrayList.get(j));
            }
            pagesResPathArrayList.add(resPathsList);
        }
        currentPage = -1;
        checkPage = -1;
        loadPageDatasShow(0, true);
    }

    /**
     * TODO 加载分页数据对象
     *
     * @param page
     * @param clear
     */
    private void loadPageDatasShow(int page, boolean clear) {
        if (currentPage == page)
            return;
        currentPage = page;
        checkPage = page;
        ArrayList<ResUrlNodeXml.ResPath> resPathList = pagesResPathArrayList.get(page);
        if (resPathDatasAdapter == null) {
            resPathDatasAdapter = new ResPathDatasAdapter(mActivity.getApplicationContext(), cachePath, resPathList);
            tilesGrid.setAdapter(resPathDatasAdapter);
        } else {
            if (clear) {
                if (resPathDatasAdapter != null) {
                    resPathDatasAdapter.clear();
                    resPathDatasAdapter = null;
                }
                resPathDatasAdapter = new ResPathDatasAdapter(mActivity.getApplicationContext(), cachePath, resPathList);
                tilesGrid.setAdapter(resPathDatasAdapter);
            } else {
                resPathDatasAdapter.add(resPathList);
            }
        }
    }

    // 详细子项内容被选中
    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // 铺砖
            if (flag == FLAG_TILES) {
                Ground ground = designer3DManager.getDesigner3DRender().getTouchSelectedManager().getSelectedGround();
                if (ground != null) {
                    ResUrlNodeXml.ResPath resPath = (ResUrlNodeXml.ResPath) resPathDatasAdapter.getItem(position);
                    // 普通砖
                    if (tileVrMode == ButtJTilesXml.NORMAL) {
                        ground.setNormalPaveRes(resPath);
                    }
                    // 波打线、样式、方案
                    else {
                        new ButtJTilesXml(mActivity, resPath, cachePath, tileVrMode, ground);
                    }
                }
            }
        }
    };

    // 底部单击事件
    private AdapterView.OnItemClickListener onBottomItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // 家具
            if (flag == FLAG_MODELS) {
                // 方案
                if (position == 3) {
                    currentLoadModelsNode = modelsBottomNodeList.get(position);
                    modelsRightIconsAdapter.setSelectePosition(-1);
                    modelsLevelOneAdapter.setSelectedPosition(position);
                    ArrayList<LJNodes> selectItemsNodeList = currentLoadModelsNode.getChildNodesList();
                    if (selectItemsNodeList != null) {
                        // 无选中房间弹出类型选择窗口
                        Ground ground = designer3DManager.getDesigner3DRender().getTouchSelectedManager().getSelectedGround();
                        if (ground == null || !ground.getHouse().isSelected()) {
                        } else {
                            // 获取房间名称，显示对应的一键布置方案
                            String houseName = ground.getHouse().houseName.getNameData().name;
                            LJNodes schemeNode = modelsBottomNodeList.get(modelsBottomNodeList.size() - 1);
                            switch (houseName) {
                                case "客餐厅":
                                case "客厅":
                                case "餐厅":
                                    currentLoadModelsNode = serviceNodesFetcher.getChildNodeByNameFromParentNode(schemeNode, "客餐厅");
                                    break;
                                case "主卧":
                                case "次卧":
                                case "客卧":
                                case "儿童房":
                                    currentLoadModelsNode = serviceNodesFetcher.getChildNodeByNameFromParentNode(schemeNode, "卧室");
                                    break;
                                case "厨房":
                                    currentLoadModelsNode = serviceNodesFetcher.getChildNodeByNameFromParentNode(schemeNode, "厨房");
                                    break;
                                case "卫生间":
                                    currentLoadModelsNode = serviceNodesFetcher.getChildNodeByNameFromParentNode(schemeNode, "卫生间");
                                    break;
                            }
                            modelsLevelOneAdapter.setSelectedPosition(3);
                            modelsRightIconsAdapter.setSelectePosition(-1);
                            // loadNodeUrlListThenPaging(currentLoadModelsNode);
                        }
                    }
                }
                // 其他
                else {
                    currentLoadModelsNode = modelsBottomNodeList.get(position);
                    modelsRightIconsAdapter.setSelectePosition(-1);
                    modelsLevelOneAdapter.setSelectedPosition(position);
                    loadNodeUrlListThenPaging(currentLoadModelsNode);
                }
            }
            // 铺砖
            else if (flag == FLAG_TILES) {

            }
        }
    };

    // 顶部单击事件
    private AdapterView.OnItemClickListener onUpItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // 家具
            if (flag == FLAG_MODELS) {
                // 更多
                if (position == 4) {
                    showMoreModels = !showMoreModels;
                    int nodeIndex = 0;
                    if (showMoreModels) {
                        modelsDetailsResIds = new int[]{R.mipmap.xiaohaifang, R.mipmap.shufang, R.mipmap.qita,
                                R.mipmap.shiwai, R.mipmap.gengduo_copy};
                        modelsDetailsSelectResIds = new int[]{R.mipmap.xiaohaifang_copy, R.mipmap.shufang_copy, R.mipmap.qita_copy,
                                R.mipmap.shiwai_copy, R.mipmap.gengduo_copy};
                        nodeIndex = 4;
                    } else {
                        modelsDetailsResIds = new int[]{R.mipmap.kecanting, R.mipmap.woshi, R.mipmap.chufang,
                                R.mipmap.weishengjian, R.mipmap.gengduo};
                        modelsDetailsSelectResIds = new int[]{R.mipmap.kecanting_chosen, R.mipmap.woshi_chosen, R.mipmap.chufang_chosen,
                                R.mipmap.weishengjian_chosen, R.mipmap.gengduo_copy};
                        nodeIndex = 0;
                    }
                    modelsRightIconsAdapter.refreshDatas(modelsDetailsResIds, modelsDetailsSelectResIds);
                    // 加载显示
                    currentLoadModelsNode = modelsNodeList.get(nodeIndex);
                    modelsRightIconsAdapter.setSelectePosition(0);
                    modelsLevelOneAdapter.setSelectedPosition(-1);
                    loadNodeUrlListThenPaging(currentLoadModelsNode);
                }
                // 切换节点
                else {
                    int nodeIndex = showMoreModels ? 4 + position : position;
                    currentLoadModelsNode = modelsNodeList.get(nodeIndex);
                    modelsRightIconsAdapter.setSelectePosition(position);
                    modelsLevelOneAdapter.setSelectedPosition(-1);
                    loadNodeUrlListThenPaging(currentLoadModelsNode);
                }
            }
            // 铺砖
            else if (flag == FLAG_TILES) {
                // 我的
                if (position == 4) {

                }
                // 其他
                else {
                    tileVrMode = position;
                    tilesRightIconsAdapter.setSelectePosition(position);
                    currentLoadModelsNode = tilesNodesList.get(position);
                    loadNodeUrlListThenPaging(currentLoadModelsNode);
                }
            }
        }
    };

    /**
     * 滚动控件底部加载下一页数据
     */
    private ScrollerGridView.OnScrollerGridListener onScrollerGridListener = new ScrollerGridView.OnScrollerGridListener() {
        @Override
        public void toLastPage() {
            checkPage++;
            if (checkPage >= pageSize) {
                checkPage = pageSize - 1;
                return;
            }
            loadPageDatasShow(checkPage, false);
        }
    };

}
