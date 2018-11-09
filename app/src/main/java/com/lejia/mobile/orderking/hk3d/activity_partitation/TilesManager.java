package com.lejia.mobile.orderking.hk3d.activity_partitation;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.lejia.mobile.orderking.R;
import com.lejia.mobile.orderking.adapters.FurniturePreviewAdapter;
import com.lejia.mobile.orderking.adapters.MatrerialTypesListAdapter;
import com.lejia.mobile.orderking.adapters.MenuBarAdapter;
import com.lejia.mobile.orderking.adapters.TilesPreviewAdapter;
import com.lejia.mobile.orderking.adapters.TilesRightIconsAdapter;
import com.lejia.mobile.orderking.bases.HttpsConfig;
import com.lejia.mobile.orderking.bases.OrderKingApplication;
import com.lejia.mobile.orderking.dialogs.TileDirectionDialog;
import com.lejia.mobile.orderking.dialogs.TileGapsSettingDialog;
import com.lejia.mobile.orderking.hk3d.TouchSelectedManager;
import com.lejia.mobile.orderking.hk3d.classes.Tile;
import com.lejia.mobile.orderking.hk3d.classes.TileDescription;
import com.lejia.mobile.orderking.hk3d.datas_2d.Furniture;
import com.lejia.mobile.orderking.hk3d.datas_2d.Ground;
import com.lejia.mobile.orderking.hk3d.datas_2d.HouseDatasManager;
import com.lejia.mobile.orderking.hk3d.datas_2d.RendererObject;
import com.lejia.mobile.orderking.hk3d.datas_2d.cadwidgets.BaseCad;
import com.lejia.mobile.orderking.hk3d.factory.CadFurnitureCreator;
import com.lejia.mobile.orderking.hk3d.gpc.NSGPCManager;
import com.lejia.mobile.orderking.https.OkHttpRequest;
import com.lejia.mobile.orderking.https.ReqCallBack;
import com.lejia.mobile.orderking.httpsResult.ResponseEntity;
import com.lejia.mobile.orderking.httpsResult.classes.LJNodes;
import com.lejia.mobile.orderking.httpsResult.classes.MaterialTypeList;
import com.lejia.mobile.orderking.httpsResult.classes.User;
import com.lejia.mobile.orderking.widgets.ScrollerGridView;
import com.lejia.mobile.orderking.widgets.TileDirectionSelectorView;
import com.lejia.mobile.orderking.widgets.TitlesView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Author by HEKE
 *
 * @time 2018/7/12 12:59
 * TODO: 材质菜单管理对象
 */
public class TilesManager {

    // 矩形画墙
    public static final int DRAW_RECT = 0;
    // 普通画墙
    public static final int DRAW_NORMAL = 1;
    // 线建墙
    public static final int DRAW_LINE_BUILD = 2;

    // 铺砖
    public static final int FLAG_TILES = 0;
    // 布置
    public static final int FLAG_LAYOUTS = 1;

    private Activity mActivity;
    private TitlesView titlesView;
    private RelativeLayout menuLayout;
    private ListView nodesList;
    private ListView detaileList;
    private ScrollerGridView tilesGrid;
    private ImageView drawStates;

    /**
     * 三维管理对象
     */
    private Designer3DManager designer3DManager;

    /**
     * 绘制状态标记
     */
    private int drawState = DRAW_RECT;

    /**
     * 节点列表对象
     */
    private MaterialTypeList materialTypeList;

    // 主节点列表适配器
    private MatrerialTypesListAdapter nodesAdapter;
    private ArrayList<LJNodes> materialNodesList;

    /**
     * 修改版本后的右边按钮
     */
    private int[] tilesNormalTopIcons; // 铺砖右上角普通资源
    private int[] tilesSelectTopIcons; // 铺砖右上角选中资源
    private int[] tilesNormalBottomIcons; // 铺砖右下角普通资源
    private int[] tilesSelectBottomIcons; // 铺砖右下角选中资源

    private int[] layoutNormalTopIcons; // 布置右上角普通资源
    private int[] layoutSelectTopIcons; // 布置右上角选中资源
    private int[] layoutMoreTopIcons; // 布置右上角更多资源
    private int[] layoutMoreSelectTopIcons; // 布置右上角选中更多资源
    private String[] layoutBottomTitles; // 布置右下角标题

    private TilesRightIconsAdapter tilesRightTopIconsAdapter;
    private TilesRightIconsAdapter tilesRightBottomIconsAdapter;
    private MenuBarAdapter furnitureTitlesAdapter;

    /**
     * 当前加载详细节点
     */
    private LJNodes currentLoadNode;
    private int currentNodeIndex;

    /**
     * 当前请求页
     */
    private int page = 1;
    private int pageSize;

    /**
     * 当前总资源预览图列表
     */
    private ArrayList<TileDescription> tileDescriptionsList;

    /**
     * 当前模型总资源预览图数据列表
     */
    private ArrayList<Furniture> furnituresList;
    private FurniturePreviewAdapter furniturePreviewAdapter;
    private boolean showFurnitureRoomStyleMore; // 是否显示更多空间类型
    private boolean showFurnitureBottomCatlogView; // 是否显示家具大类内容

    // 资源预览图列表适配器
    private TilesPreviewAdapter tilesPreviewAdapter;

    /**
     * 铺砖起铺方向窗口
     */
    private TileDirectionDialog tileDirectionDialog;

    /**
     * 砖缝数据设置窗口
     */
    private TileGapsSettingDialog tileGapsSettingDialog;

    /**
     * 设置铺砖或者布置内容
     */
    private int flag = -1;

    // 当前登入用户
    private User user;

    /**
     * 是否来自于替换操作
     */
    private boolean fromReplace;
    private OnReplaceFurnitureListener onReplaceFurnitureListener;

    private void init() {
        // 菜单图标资源设定
        tilesNormalTopIcons = new int[]{R.mipmap.yangshi_copy, R.mipmap.huanzhuan, R.mipmap.fangan, R.mipmap.wode};
        tilesSelectTopIcons = new int[]{R.mipmap.yangshi, R.mipmap.huanzhuan_copy, R.mipmap.rectangle_3_copy_4, R.mipmap.wode_copy};
        tilesNormalBottomIcons = new int[]{R.mipmap.quyu, R.mipmap.huagong, R.mipmap.qipu, R.mipmap.jiaodu};
        tilesSelectBottomIcons = new int[]{R.mipmap.quyu_copy, R.mipmap.huagong_copy, R.mipmap.qipu_copy, R.mipmap.jiaodu_copy};
        layoutNormalTopIcons = new int[]{R.mipmap.kecanting, R.mipmap.woshi, R.mipmap.chufang,
                R.mipmap.weishengjian, R.mipmap.gengduo};
        layoutSelectTopIcons = new int[]{R.mipmap.kecanting_chosen, R.mipmap.woshi_chosen, R.mipmap.chufang_chosen,
                R.mipmap.weishengjian_chosen, R.mipmap.gengduo_copy};
        layoutMoreTopIcons = new int[]{R.mipmap.xiaohaifang, R.mipmap.shufang, R.mipmap.qita,
                R.mipmap.shiwai, R.mipmap.gengduo};
        layoutMoreSelectTopIcons = new int[]{R.mipmap.xiaohaifang_copy, R.mipmap.shufang_copy, R.mipmap.qita_copy,
                R.mipmap.shiwai_copy, R.mipmap.gengduo_copy};
        layoutBottomTitles = mActivity.getResources().getStringArray(R.array.furniture_bottom_titles);
        // 主界面底部菜单栏、绘制墙体方式
        titlesView.setOnTitlesStatusListener(onTitlesStatusListener);
        drawStates.setOnClickListener(onClickListener);
        Context context = mActivity.getApplicationContext();
        // 区分手机与平板
        boolean isPad = ((OrderKingApplication) context).isPad();
        pageSize = isPad ? 20 : 10;
        setFlag(FLAG_TILES);
    }

    public TilesManager(Activity activity, TitlesView titlesView, RelativeLayout menuLayout
            , ListView nodesList, ListView detaileList, ScrollerGridView tilesGrid
            , ImageView drawStates, Designer3DManager designer3DManager) {
        this.mActivity = activity;
        this.titlesView = titlesView;
        this.menuLayout = menuLayout;
        this.nodesList = nodesList;
        this.detaileList = detaileList;
        this.tilesGrid = tilesGrid;
        this.drawStates = drawStates;
        this.designer3DManager = designer3DManager;
        init();
    }

    /**
     * 设置当前加载数据内容
     */
    public void setFlag(int flag) {
        if (this.flag == flag)
            return;
        this.flag = flag;
        Context context = mActivity.getApplicationContext();
        // 铺砖
        if (this.flag == FLAG_TILES) {
            // 菜单有边框文本标题
            materialTypeList = ((OrderKingApplication) context).materialTypeList;
            materialNodesList = materialTypeList.getMaterialTypeList();
            tilesRightTopIconsAdapter = new TilesRightIconsAdapter(context, tilesNormalTopIcons, tilesSelectTopIcons);
            tilesRightBottomIconsAdapter = new TilesRightIconsAdapter(context, tilesNormalBottomIcons, tilesSelectBottomIcons);
            nodesList.setAdapter(tilesRightBottomIconsAdapter);
            detaileList.setAdapter(tilesRightTopIconsAdapter);
            nodesList.setSelector(R.drawable.grid_selector);
            detaileList.setSelector(R.drawable.grid_selector);
            nodesList.setOnItemClickListener(mainNodesItemClickListener);
            detaileList.setOnItemClickListener(detailesItemClickListener);
            // 材质展示窗口
            tileDescriptionsList = new ArrayList<>();
            tilesPreviewAdapter = new TilesPreviewAdapter(context, tileDescriptionsList);
            tilesGrid.setAdapter(tilesPreviewAdapter);
            tilesGrid.setOnScrollerGridListener(onScrollerGridListener);
            tilesGrid.setOnItemClickListener(onItemClickListener);
            tilesGrid.setSelector(R.drawable.grid_selector);
            // 修改版本展示使用
            tilesRightTopIconsAdapter.setSelectePosition(1);
            showPage(1, 1, true);
        }
        // 布置
        else if (this.flag == FLAG_LAYOUTS) {
            // 设置模型节点
            materialTypeList = ((OrderKingApplication) context).furnitureMaterialTypeList;
            materialNodesList = materialTypeList.getMaterialTypeList();
            tilesRightTopIconsAdapter = new TilesRightIconsAdapter(context, layoutNormalTopIcons, layoutSelectTopIcons);
            furnitureTitlesAdapter = new MenuBarAdapter(context, layoutBottomTitles, 12);
            nodesList.setAdapter(furnitureTitlesAdapter);
            detaileList.setAdapter(tilesRightTopIconsAdapter);
            nodesList.setSelector(R.drawable.grid_selector);
            detaileList.setSelector(R.drawable.grid_selector);
            nodesList.setOnItemClickListener(mainNodesItemClickListener);
            detaileList.setOnItemClickListener(detailesItemClickListener);
            // 材质展示窗口
            furnituresList = new ArrayList<>();
            furniturePreviewAdapter = new FurniturePreviewAdapter(context, furnituresList);
            tilesGrid.setAdapter(furniturePreviewAdapter);
            tilesGrid.setOnScrollerGridListener(onScrollerGridListener);
            tilesGrid.setOnItemClickListener(onItemClickListener);
            tilesGrid.setSelector(R.drawable.grid_selector);
            // 修改版本展示使用
            tilesRightTopIconsAdapter.setSelectePosition(0);
            furnitureTitlesAdapter.setSelectedPosition(-1);
            showPage(0, 1, true);
        }
    }

    /**
     * 获取当前绘制房间类型
     */
    public int getDrawState() {
        return drawState;
    }

    public boolean isFromReplace() {
        return fromReplace;
    }

    /**
     * 绑定替换操作
     */
    public void setFromReplace(boolean fromReplace, OnReplaceFurnitureListener onReplaceFurnitureListener) {
        this.fromReplace = fromReplace;
        this.onReplaceFurnitureListener = onReplaceFurnitureListener;
    }

    // 底部菜单栏点击操作监听接口
    private TitlesView.OnTitlesStatusListener onTitlesStatusListener = new TitlesView.OnTitlesStatusListener() {
        @Override
        public void onItemTitleClick(int position, String title, boolean isfromUser) {
            switch (position) {
                case 0:
                    // 房间
                    drawStates.setVisibility((drawStates.getVisibility() == View.VISIBLE) ? View.GONE : View.VISIBLE);
                    break;
                case 1:
                    // 门窗
                    break;
                case 2:
                    // 铺砖
                    menuLayout.setVisibility(View.VISIBLE);
                    showFurnitureBottomCatlogView = false;
                    setFlag(FLAG_TILES);
                    break;
                case 3:
                    // 布置
                    menuLayout.setVisibility(View.VISIBLE);
                    setFlag(FLAG_LAYOUTS);
                    break;
            }
        }

        @Override
        public void onSelectedCancle(int position, String title) {
            switch (position) {
                case 0:
                    // 房间
                    drawStates.setVisibility(View.GONE);
                    drawState = DRAW_RECT;
                    drawStates.setBackgroundResource(R.mipmap.juxinghuafangjian);
                    break;
                case 1:
                    // 门窗
                    break;
                case 2:
                    // 铺砖
                    menuLayout.setVisibility(View.GONE);
                    break;
                case 3:
                    // 布置
                    menuLayout.setVisibility(View.GONE);
                    break;
            }
        }
    };

    // 设置资源菜单栏是否显示
    public void setTilesMenuLayoutShowFromNewCreate(boolean visiable) {
        menuLayout.setVisibility(visiable ? View.VISIBLE : View.GONE);
        if (!visiable) {
            titlesView.setPosition(-1);
            drawState = DRAW_RECT;
            fromReplace = false;
        }
    }

    // 画墙状态切换监听接口
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            drawState++;
            if (drawState > DRAW_LINE_BUILD)
                drawState = DRAW_RECT;
            switch (drawState) {
                case DRAW_RECT:
                    drawStates.setBackgroundResource(R.mipmap.juxinghuafangjian);
                    break;
                case DRAW_NORMAL:
                    drawStates.setBackgroundResource(R.mipmap.huaqiang);
                    break;
                case DRAW_LINE_BUILD:
                    drawStates.setBackgroundResource(R.mipmap.xianjianqiang);
                    break;
            }
        }
    };

    // 主节点点击事件监听接口
    private AdapterView.OnItemClickListener mainNodesItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // 铺砖
            if (flag == FLAG_TILES) {
                tilesRightBottomIconsAdapter.setSelectePosition(position);
                // 根据位置弹出响应的操作窗口
                switch (position) {
                    case Classify.FLAG_AREAS:
                        break;
                    case Classify.FLAG_GAPS:
                        // 弹出与隐藏砖缝设置窗口
                        if (tileGapsSettingDialog == null)
                            tileGapsSettingDialog = new TileGapsSettingDialog(mActivity, onTileGapSettingListener);
                        Ground gapsSelectedGround = getSelectedGround();
                        if (gapsSelectedGround != null) {
                            NSGPCManager nsgpcManager = gapsSelectedGround.getGpcManager();
                            if (nsgpcManager != null) {
                                tileGapsSettingDialog.setGapsColor(nsgpcManager.getGapsColor());
                                tileGapsSettingDialog.setGapsSize((int) (nsgpcManager.getBrickGap() * 10));
                            }
                        }
                        tileGapsSettingDialog.autoShowOrHide();
                        break;
                    case Classify.FLAG_DIRECTION:
                        // 弹出与隐藏设置起铺方向窗口
                        if (tileDirectionDialog == null)
                            tileDirectionDialog = new TileDirectionDialog(mActivity, onTileDirectionsSelectedListener);
                        Ground selectedGround = getSelectedGround();
                        if (selectedGround != null) {
                            NSGPCManager nsgpcManager = selectedGround.getGpcManager();
                            if (nsgpcManager != null)
                                tileDirectionDialog.setDirection(nsgpcManager.getDirection());
                        }
                        tileDirectionDialog.autoShowOrHide();
                        break;
                    case Classify.FLAG_SKEW_TILE:
                        // 设置选中地面铺砖是否斜铺
                        Ground ground = getSelectedGround();
                        if (ground != null) {
                            NSGPCManager nsgpcManager = ground.getGpcManager();
                            nsgpcManager.setSkewTile(!nsgpcManager.isSkewTile());
                        }
                        break;
                }
            }
            // 布置
            else if (flag == FLAG_LAYOUTS) {
                furnitureTitlesAdapter.setSelectedPosition(position);
                showFurnitureBottomCatlogView = true;
                // 获取大类节点对应数据
                MaterialTypeList catlogList = ((OrderKingApplication) mActivity.getApplicationContext()).furnitureCatlogList;
                if (catlogList != null) {
                    currentLoadNode = catlogList.getChildByName(layoutBottomTitles[position]);
                }
                // 根据节点显示数据内容
                if (currentLoadNode != null) {
                    tilesRightTopIconsAdapter.setSelectePosition(-1);
                    showPage(-2, 1, true);
                }
            }
        }
    };

    // 砖缝设置回调接口
    private TileGapsSettingDialog.OnTileGapSettingListener onTileGapSettingListener = new TileGapsSettingDialog.OnTileGapSettingListener() {
        @Override
        public void setGapSize(int size) {
            // 设置砖缝大小
            Ground ground = getSelectedGround();
            if (ground != null) {
                NSGPCManager nsgpcManager = ground.getGpcManager();
                if (nsgpcManager != null) {
                    nsgpcManager.setBrickGap(size * 0.1f);
                }
            }
        }

        @Override
        public void setGapColor(int color) {
            // 设置砖缝颜色
            Ground ground = getSelectedGround();
            if (ground != null) {
                NSGPCManager nsgpcManager = ground.getGpcManager();
                if (nsgpcManager != null) {
                    nsgpcManager.setGapsColor(color);
                }
            }
        }

        @Override
        public void calBrickCounts() {
        }
    };

    /**
     * TODO 获取选中的地面
     */
    private Ground getSelectedGround() {
        TouchSelectedManager touchSelectedManager = designer3DManager.getDesigner3DRender().getTouchSelectedManager();
        Ground ground = null;
        if (touchSelectedManager != null)
            ground = touchSelectedManager.getSelectedGround();
        return ground;
    }

    // 铺砖起始方向选中回调接口
    private TileDirectionSelectorView.OnTileDirectionsSelectedListener onTileDirectionsSelectedListener = new TileDirectionSelectorView.OnTileDirectionsSelectedListener() {
        @Override
        public void selected(int direction) {
            System.out.println("### direction : " + direction);
            // 设置当前选中地面的铺砖起铺方向
            Ground ground = getSelectedGround();
            if (ground != null) {
                NSGPCManager nsgpcManager = ground.getGpcManager();
                if (nsgpcManager != null) {
                    nsgpcManager.setDirection(direction);
                }
            }
        }
    };

    // 详细点击事件监听接口
    private AdapterView.OnItemClickListener detailesItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            tilesRightTopIconsAdapter.setSelectePosition(position);
            // 铺砖
            if (flag == FLAG_TILES) {
                showPage(position, 1, true);
            }
            // 布置
            else if (flag == FLAG_LAYOUTS) {
                showFurnitureBottomCatlogView = false;
                furnitureTitlesAdapter.setSelectedPosition(-1);
                // 切换
                if (position == 4) {
                    showFurnitureRoomStyleMore = !showFurnitureRoomStyleMore;
                    if (showFurnitureRoomStyleMore) {
                        tilesRightTopIconsAdapter.refreshDatas(layoutMoreTopIcons, layoutMoreSelectTopIcons);
                    } else {
                        tilesRightTopIconsAdapter.refreshDatas(layoutNormalTopIcons, layoutSelectTopIcons);
                    }
                }
                // 加载具体空间类型显示
                else {
                    int showPosition = showFurnitureRoomStyleMore ? (position + 4) : position;
                    showPage(showPosition, 1, true);
                }
            }
        }
    };

    /**
     * 调用获取第几页数据
     *
     * @param detailNodeIndex 详细节点位置编号
     * @param page            第几页
     * @param needClearList   是否清楚当前已加载的数据内容
     */
    private void showPage(int detailNodeIndex, int page, boolean needClearList) {
        if (!showFurnitureBottomCatlogView) {
            currentLoadNode = materialNodesList.get(detailNodeIndex);
            currentNodeIndex = detailNodeIndex;
        }
        this.page = page;
        if (needClearList) {
            if (flag == FLAG_TILES) // 铺砖
                tileDescriptionsList.clear();
            else if (flag == FLAG_LAYOUTS) // 布置
                furnituresList.clear();
        }
        getPage();
    }

    /**
     * 获取指定页的数据
     */
    private void getPage() {
        if (currentLoadNode == null)
            return;
        user = ((OrderKingApplication) mActivity.getApplicationContext()).mUser;
        // 铺砖
        if (flag == FLAG_TILES) {
            // 普通砖
            if (currentLoadNode.getName().equals("换砖")) {
                HashMap<String, String> params = new HashMap<>();
                params.put("token", user.getToken());
                params.put("pageIndex", "" + page);
                params.put("pageSize", "" + pageSize);
                params.put("materialTypeID", "" + currentLoadNode.getId());
                OkHttpRequest request = OkHttpRequest.getInstance(mActivity.getApplicationContext());
                request.requestAsyn(HttpsConfig.GET_DETAILE_NODE_DATAS, OkHttpRequest.TYPE_POST_JSON, params, new ReqCallBack<Object>() {
                    @Override
                    public void onReqSuccess(Object result) {
                        if (result != null) {
                            try {
                                ResponseEntity entity = new ResponseEntity(result);
                                // 有数据时刷新
                                if (entity.state == 1) {
                                    JSONArray array = entity.getJSonArray("materialList");
                                    ArrayList<TileDescription> pageTileDiscriptionsList = new ArrayList<>();
                                    for (int i = 0; i < array.length(); i++) {
                                        JSONObject object = array.getJSONObject(i);
                                        TileDescription tileDescription = new Gson().fromJson(object.toString(), TileDescription.class);
                                        pageTileDiscriptionsList.add(tileDescription);
                                    }
                                    tileDescriptionsList.addAll(pageTileDiscriptionsList);
                                    // 默认装载第一页用于换砖的材质数据
                                    OrderKingApplication app = ((OrderKingApplication) mActivity.getApplicationContext());
                                    if (app.defaultTileDescriptionList != null && app.defaultTileDescriptionList.size() == 0) {
                                        app.setDefaultTileDescriptionList(pageTileDiscriptionsList);
                                    }
                                    // 刷新列表显示
                                    tilesPreviewAdapter.notifyDataSetChanged();
                                }
                                // 没有更多页数据，则返回最后一页
                                else {
                                    page--;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onReqFailed(String errorMsg) {
                    }
                });
            }
            // 样式砖
            else if (currentLoadNode.getName().equals("样式")) {
                HashMap<String, String> params = new HashMap<>();
                params.put("token", user.getToken());
                params.put("pageIndex", "" + page);
                params.put("pageSize", "" + pageSize);
                OkHttpRequest request = OkHttpRequest.getInstance(mActivity);
                request.requestAsyn(HttpsConfig.GET_STYLIES_TILES_DATAS_LIST, OkHttpRequest.TYPE_POST_JSON, params, new ReqCallBack<Object>() {
                    @Override
                    public void onReqSuccess(Object result) {
                        if (result != null) {
                            try {
                                ResponseEntity entity = new ResponseEntity(result);
                                // 请求数据
                                if (entity.state == 1) {
                                    JSONArray jsonArray = entity.getJSonArray("materialList");
                                    ArrayList<TileDescription> pageTileDiscriptionsList = new ArrayList<>();
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject object = jsonArray.getJSONObject(i);
                                        TileDescription tileDescription = new TileDescription();
                                        tileDescription.id = object.getInt("id");
                                        tileDescription.styleType = object.getInt("styleType");
                                        tileDescription.previewImg = object.getString("previewImg");
                                        tileDescription.styliesMaterialList = new ArrayList<>();
                                        JSONArray tilesArray = object.getJSONArray("materialList");
                                        if (tilesArray != null) {
                                            for (int j = 0; j < tilesArray.length(); j++) {
                                                JSONArray cellArray = tilesArray.getJSONArray(j);
                                                ArrayList<Tile> tilesList = new ArrayList<>();
                                                for (int k = 0; k < cellArray.length(); k++) {
                                                    JSONObject cell = cellArray.getJSONObject(k);
                                                    Tile tile = new Gson().fromJson(cell.toString(), Tile.class);
                                                    tilesList.add(tile);
                                                }
                                                tileDescription.styliesMaterialList.add(tilesList);
                                            }
                                        }
                                        pageTileDiscriptionsList.add(tileDescription);
                                    }
                                    tileDescriptionsList.addAll(pageTileDiscriptionsList);
                                    tilesPreviewAdapter.notifyDataSetChanged();
                                }
                                // 无数据
                                else {
                                    page--;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onReqFailed(String errorMsg) {
                    }
                });
            }
        }
        // 布置
        else if (flag == FLAG_LAYOUTS) {
            fetchFurnitureType(!showFurnitureBottomCatlogView);
        }
    }

    /**
     * 拉取模型数据
     *
     * @param top 顶部空间类型请求
     */
    private void fetchFurnitureType(boolean top) {
        HashMap<String, String> params = new HashMap<>();
        params.put("token", user.getToken());
        params.put("pageIndex", "" + page);
        params.put("pageSize", "" + pageSize);
        if (top) {
            params.put("roomTypeID", "" + currentLoadNode.getId());
            params.put("typeID", "");
        } else {
            params.put("roomTypeID", "");
            params.put("typeID", "" + currentLoadNode.getId());
        }
        OkHttpRequest request = OkHttpRequest.getInstance(mActivity);
        request.requestAsyn(HttpsConfig.GET_FURNITURE_ID_DATAS_LIST, OkHttpRequest.TYPE_POST_JSON, params, new ReqCallBack<Object>() {
            @Override
            public void onReqSuccess(Object result) {
                try {
                    ResponseEntity entity = new ResponseEntity(result);
                    // 包含数据
                    if (entity.state == 1) {
                        JSONArray array = entity.getJSonArray("modelMaterialList");
                        if (array != null) {
                            ArrayList<Furniture> pageFurnituresList = new ArrayList<>();
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject object = array.getJSONObject(i);
                                Furniture furniture = new Gson().fromJson(object.toString(), Furniture.class);
                                pageFurnituresList.add(furniture);
                            }
                            furnituresList.addAll(pageFurnituresList);
                        }
                        furniturePreviewAdapter.notifyDataSetChanged();
                    }
                    // 没有更多页数据，则返回最后一页
                    else {
                        page--;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onReqFailed(String errorMsg) {
            }
        });
    }

    // 可滚动材质窗口拉动到底部监听接口
    private ScrollerGridView.OnScrollerGridListener onScrollerGridListener = new ScrollerGridView.OnScrollerGridListener() {
        @Override
        public void toLastPage() {
            // 加载下一页数据
            page++;
            showPage(currentNodeIndex, page, false);
        }
    };

    /**
     * 具体材质资源点击
     */
    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // 铺砖
            if (flag == FLAG_TILES) {
                // 获取三维场景数据触摸管理对象
                TouchSelectedManager touchSelectedManager = designer3DManager.getDesigner3DRender().getTouchSelectedManager();
                if (touchSelectedManager != null) {
                    RendererObject selector = touchSelectedManager.getSelector();
                    if (selector != null && (selector instanceof Ground)) { // 选中对象为地面时
                        Ground ground = (Ground) selector;
                        ArrayList<TileDescription> useTileDescriptionsList = new ArrayList<>();
                        useTileDescriptionsList.add(tileDescriptionsList.get(position));
                        ground.setTileDescriptionsList(useTileDescriptionsList);
                    }
                }
            }
            // 布置
            else if (flag == FLAG_LAYOUTS) {
                // 获取点击的家具对象
                Furniture furniture = furnituresList.get(position);
                // 来自于模型替换
                if (fromReplace) {
                    if (onReplaceFurnitureListener != null)
                        onReplaceFurnitureListener.replaced(furniture);
                    fromReplace = false;
                    setTilesMenuLayoutShowFromNewCreate(false);
                } else {
                    // 获取三维数据管理对象
                    HouseDatasManager houseDatasManager = designer3DManager.getDesigner3DRender().getHouseDatasManager();
                    // 门窗数据特殊处理
                    if (showFurnitureBottomCatlogView) {
                        String nodeName = currentLoadNode.getName();
                        // 门窗
                        if (nodeName.equals("门") || nodeName.equals("窗")) {
                            BaseCad wd = CadFurnitureCreator.createDoorOrWindow(houseDatasManager, designer3DManager, furniture);
                            houseDatasManager.addFurniture(wd);
                            houseDatasManager.punchWallFacedes(wd);
                        }
                        // 其他任何家具
                        else {
                            houseDatasManager.addFurniture(CadFurnitureCreator.createGeneralFurniture(houseDatasManager, designer3DManager, furniture));
                        }
                    }
                    // 其他任何家具
                    else {
                        houseDatasManager.addFurniture(CadFurnitureCreator.createGeneralFurniture(houseDatasManager, designer3DManager, furniture));
                    }
                }
                // 加载同步三维数据
                furniture.loadSubsets();
            }
        }
    };

    /**
     * Author by HEKE
     *
     * @time 2018/8/29 12:15
     * TODO: 模型替换回调接口
     */
    public interface OnReplaceFurnitureListener {
        void replaced(Furniture furniture);
    }

}
