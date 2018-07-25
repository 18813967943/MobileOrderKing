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
import com.lejia.mobile.orderking.adapters.MatrerialTypesListAdapter;
import com.lejia.mobile.orderking.adapters.TilesPreviewAdapter;
import com.lejia.mobile.orderking.adapters.TilesRightIconsAdapter;
import com.lejia.mobile.orderking.bases.HttpsConfig;
import com.lejia.mobile.orderking.bases.OrderKingApplication;
import com.lejia.mobile.orderking.hk3d.TouchSelectedManager;
import com.lejia.mobile.orderking.hk3d.classes.TileDescription;
import com.lejia.mobile.orderking.hk3d.datas.Ground;
import com.lejia.mobile.orderking.hk3d.datas.RendererObject;
import com.lejia.mobile.orderking.https.OkHttpRequest;
import com.lejia.mobile.orderking.https.ReqCallBack;
import com.lejia.mobile.orderking.httpsResult.ResponseEntity;
import com.lejia.mobile.orderking.httpsResult.classes.LJNodes;
import com.lejia.mobile.orderking.httpsResult.classes.MaterialTypeList;
import com.lejia.mobile.orderking.httpsResult.classes.User;
import com.lejia.mobile.orderking.widgets.ScrollerGridView;
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

    private TilesRightIconsAdapter tilesRightTopIconsAdapter;
    private TilesRightIconsAdapter tilesRightBottomIconsAdapter;

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

    // 资源预览图列表适配器
    private TilesPreviewAdapter tilesPreviewAdapter;

    private void init() {
        // 菜单图标资源设定
        tilesNormalTopIcons = new int[]{R.mipmap.yangshi_copy, R.mipmap.huanzhuan, R.mipmap.fangan, R.mipmap.wode};
        tilesSelectTopIcons = new int[]{R.mipmap.yangshi, R.mipmap.huanzhuan_copy, R.mipmap.rectangle_3_copy_4, R.mipmap.wode_copy};
        tilesNormalBottomIcons = new int[]{R.mipmap.quyu, R.mipmap.huagong, R.mipmap.qipu, R.mipmap.jiaodu};
        tilesSelectBottomIcons = new int[]{R.mipmap.quyu_copy, R.mipmap.huagong_copy, R.mipmap.qipu_copy, R.mipmap.jiaodu_copy};
        layoutNormalTopIcons = new int[]{R.mipmap.kecanting, R.mipmap.woshi, R.mipmap.chufang,
                R.mipmap.weishengjian, R.mipmap.gengduo};
        layoutSelectTopIcons = new int[]{R.mipmap.kecanting_chosen, R.mipmap.woshi_chosen, R.mipmap.chufang_chosen,
                R.mipmap.weishengjian_chosen, R.mipmap.gengduo_chosen};
        // 主界面底部菜单栏、绘制墙体方式
        titlesView.setOnTitlesStatusListener(onTitlesStatusListener);
        drawStates.setOnClickListener(onClickListener);
        Context context = mActivity.getApplicationContext();
        // 区分手机与平板
        boolean isPad = ((OrderKingApplication) context).isPad();
        pageSize = isPad ? 20 : 10;
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
     * 获取当前绘制房间类型
     */
    public int getDrawState() {
        return drawState;
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
                    break;
                case 3:
                    // 布置
                    menuLayout.setVisibility(View.VISIBLE);
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
            tilesRightBottomIconsAdapter.setSelectePosition(position);
        }
    };

    // 详细点击事件监听接口
    private AdapterView.OnItemClickListener detailesItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            tilesRightTopIconsAdapter.setSelectePosition(position);
            showPage(position, 1, true);
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
        currentLoadNode = materialNodesList.get(detailNodeIndex);
        this.page = page;
        if (needClearList) {
            tileDescriptionsList.clear();
        }
        getPage();
    }

    /**
     * 获取指定页的数据
     */
    private void getPage() {
        if (currentLoadNode == null)
            return;
        User user = ((OrderKingApplication) mActivity.getApplicationContext()).mUser;
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
    };

}
