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
import com.lejia.mobile.orderking.bases.HttpsConfig;
import com.lejia.mobile.orderking.bases.OrderKingApplication;
import com.lejia.mobile.orderking.hk3d.classes.Tile;
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

    // 节点详细节点列表适配器
    private MatrerialTypesListAdapter detailesAdapter;
    private ArrayList<LJNodes> detailesNodesList;

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
    private ArrayList<Tile> tilesList;

    // 资源预览图列表适配器
    private TilesPreviewAdapter tilesPreviewAdapter;

    private void init() {
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
        nodesAdapter = new MatrerialTypesListAdapter(context, materialNodesList);
        nodesList.setAdapter(nodesAdapter);
        detailesNodesList = materialNodesList.get(0).getChildrenList();
        detailesAdapter = new MatrerialTypesListAdapter(context, detailesNodesList);
        detaileList.setAdapter(detailesAdapter);
        nodesList.setOnItemClickListener(mainNodesItemClickListener);
        detaileList.setOnItemClickListener(detailesItemClickListener);
        // 材质展示窗口
        tilesList = new ArrayList<>();
        tilesPreviewAdapter = new TilesPreviewAdapter(context, tilesList);
        tilesGrid.setAdapter(tilesPreviewAdapter);
        tilesGrid.setOnScrollerGridListener(onScrollerGridListener);
        tilesGrid.setOnItemClickListener(onItemClickListener);
        tilesGrid.setSelector(R.drawable.grid_selector);
        // 加载默认节点材质数据展示
        showPage(0, 1, true);
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
                    nodesAdapter.changeList(materialNodesList);
                    detailesAdapter.changeList(materialNodesList.get(0).getChildrenList());
                    break;
                case 3:
                    // 布置
                    menuLayout.setVisibility(View.VISIBLE);
                    nodesAdapter.changeList(null);
                    detailesAdapter.changeList(null);
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
            // 混铺
            if (position == materialNodesList.size() - 1) {

            }
            // 详细节点刷新
            else {
                detailesNodesList = materialNodesList.get(position).getChildrenList();
                detailesAdapter.changeList(detailesNodesList);
                // 加载详细节点第一个数据
                currentNodeIndex = 0;
                showPage(currentNodeIndex, 1, true);
            }
        }
    };

    // 详细点击事件监听接口
    private AdapterView.OnItemClickListener detailesItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // 获取详细节点第一页数据
            currentNodeIndex = position;
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
        currentLoadNode = detailesNodesList.get(detailNodeIndex);
        this.page = page;
        if (needClearList) {
            tilesList.clear();
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
                            ArrayList<Tile> pageTilesList = new ArrayList<>();
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject object = array.getJSONObject(i);
                                Tile tile = new Gson().fromJson(object.toString(), Tile.class);
                                pageTilesList.add(tile);
                            }
                            tilesList.addAll(pageTilesList);
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
            System.out.println("##### position : " + position + "  code : " + tilesList.get(position).getMaterialCode());
        }
    };

}
