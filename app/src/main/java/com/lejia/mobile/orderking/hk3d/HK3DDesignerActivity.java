package com.lejia.mobile.orderking.hk3d;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.lejia.mobile.orderking.R;
import com.lejia.mobile.orderking.activitys.PermissionsActivity;
import com.lejia.mobile.orderking.bases.OrderKingApplication;
import com.lejia.mobile.orderking.classes.ServiceNodesFetcher;
import com.lejia.mobile.orderking.dialogs.CameraDrawSelectDialog;
import com.lejia.mobile.orderking.hk3d.activity_partitation.Designer3DManager;
import com.lejia.mobile.orderking.hk3d.activity_partitation.FurnitureTouchManager;
import com.lejia.mobile.orderking.hk3d.activity_partitation.MoreManager;
import com.lejia.mobile.orderking.hk3d.activity_partitation.On3DTouchManager;
import com.lejia.mobile.orderking.hk3d.activity_partitation.TilesManager;
import com.lejia.mobile.orderking.hk3d.activity_partitation.TouchManager;
import com.lejia.mobile.orderking.hk3d.housetype.UnitDiscern;
import com.lejia.mobile.orderking.widgets.ScrollLayout;
import com.lejia.mobile.orderking.widgets.ScrollerGridView;
import com.lejia.mobile.orderking.widgets.TitlesView;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Author by HEKE
 *
 * @time 2018/7/2 9:52
 * TODO: 三维设计界面
 */
public class HK3DDesignerActivity extends Activity {

    @BindView(R.id.getback)
    ImageButton getback;
    @BindView(R.id.forward)
    ImageButton forward;
    @BindView(R.id.jingzhun)
    ImageButton jingzhun;
    @BindView(R.id.cameraDraw)
    ImageButton cameraDraw;
    @BindView(R.id.title)
    TitlesView title;
    @BindView(R.id.zhouce)
    ImageButton zhouce;
    @BindView(R.id.threed)
    ImageButton threed;
    @BindView(R.id.more)
    ImageButton more;
    @BindView(R.id.designer3dLayout)
    ScrollLayout designer3dLayout;
    @BindView(R.id.drawStates)
    ImageView drawStates;
    @BindView(R.id.detialsList)
    ListView detialsList;
    @BindView(R.id.nodesList)
    ListView nodesList;
    @BindView(R.id.resGrid)
    ScrollerGridView resGrid;
    @BindView(R.id.rightLayout)
    RelativeLayout rightLayout;

    /**
     * 服务资源节点数据对象
     */
    private ServiceNodesFetcher serviceNodesFetcher;

    /**
     * 三维管理对象
     */
    private Designer3DManager designer3DManager;

    /**
     * 材质管理对象
     */
    private TilesManager tilesManager;

    /**
     * 触摸管理对象
     */
    private TouchManager touchManager;

    /**
     * 家具触摸管理对象
     */
    private FurnitureTouchManager furnitureTouchManager;
    private boolean interruptTouch;

    /**
     * 更多菜单栏操作
     */
    private MoreManager moreManager;

    /**
     * 三维触摸管理
     */
    private On3DTouchManager on3DTouchManager;

    /**
     * 图像识别操作窗口
     */
    private CameraDrawSelectDialog cameraDrawSelectDialog;

    private void initViews() {
        if (serviceNodesFetcher == null) {
            serviceNodesFetcher = new ServiceNodesFetcher(this, new ServiceNodesFetcher.OnServiceNodesFetchedListener() {
                @Override
                public void fetchStatues(boolean error, String errorInfo) {
                    // 正确加载完毕，执行数据创建
                    if (!error) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                designer3DManager = new Designer3DManager(HK3DDesignerActivity.this, designer3dLayout);
                                tilesManager = new TilesManager(HK3DDesignerActivity.this, title, rightLayout, nodesList,
                                        detialsList, resGrid, drawStates, designer3DManager, serviceNodesFetcher);
                            }
                        });
                    }
                    // 服务资源节点加载失败，则关闭程序
                    else {
                        Log.e("Yi3D", "Service error -- " + errorInfo);
                        android.os.Process.killProcess(android.os.Process.myPid());
                    }
                }
            });
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        OrderKingApplication.setMainActivityContext(this);
        // 打开权限申请，并执行控件相关操作，解决权限操作时序加载数据问题
        startActivityForResult(new Intent(this, PermissionsActivity.class), -1);
    }

    /**
     * 加载控件
     */
    public void loadViews() {
        initViews();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @OnClick({R.id.getback, R.id.forward, R.id.jingzhun, R.id.cameraDraw, R.id.zhouce, R.id.threed, R.id.more, R.id.drawStates})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.getback:
                break;
            case R.id.forward:
                break;
            case R.id.jingzhun:
                boolean isAccurate = !RendererState.isIsAccurate();
                RendererState.setAccurate(isAccurate);
                jingzhun.setSelected(isAccurate);
                break;
            case R.id.cameraDraw:
                // 弹出照片选择方式窗口
                cameraDrawSelectDialog = new CameraDrawSelectDialog(HK3DDesignerActivity.this);
                cameraDrawSelectDialog.show();
                break;
            case R.id.zhouce:
                if (RendererState.isNot25D()) {
                    RendererState.setRenderState(RendererState.STATE_25D);
                    designer3DManager.toAxisSide();
                } else {
                    RendererState.setRenderState(RendererState.STATE_2D);
                    designer3DManager.to2D();
                }
                break;
            case R.id.threed:
                if (RendererState.isNot3D()) {
                    RendererState.setRenderState(RendererState.STATE_3D);
                    designer3DManager.enterHouse();
                } else {
                    RendererState.setRenderState(RendererState.STATE_2D);
                    designer3DManager.to2D();
                }
                break;
            case R.id.more:
                if (moreManager == null)
                    moreManager = new MoreManager(HK3DDesignerActivity.this, tilesManager, designer3DManager);
                moreManager.autoShowOrHide();
                break;
            case R.id.drawStates:
                break;
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // 底部栏触摸允许，并拦截后续触摸事件
        float y = ev.getY();
        float bottomBarHeight = getResources().getDimension(R.dimen.main_bottom_height);
        int screenHeight = getResources().getDisplayMetrics().heightPixels;
        if (y >= (screenHeight - bottomBarHeight)) {
            interruptTouch = true;
            return super.dispatchTouchEvent(ev);
        }
        // 优先检测模型点击操作事件
        if (furnitureTouchManager == null) {
            furnitureTouchManager = new FurnitureTouchManager(this, tilesManager, designer3DManager);
        }
        // 三维平面模型触摸操作
        if (RendererState.isNot25D() && RendererState.isNot3D()) {
            interruptTouch = furnitureTouchManager.canDrawCheck(ev);
        } else {
            interruptTouch = false;
            // 三维手势触摸管理
            if (on3DTouchManager == null) {
                on3DTouchManager = new On3DTouchManager(this, tilesManager, designer3DManager);
            }
            on3DTouchManager.touch(ev);
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 三维平面控件触摸处理
        if (!interruptTouch && RendererState.isNot25D() && RendererState.isNot3D()) {
            if (touchManager == null)
                touchManager = new TouchManager(this, tilesManager, designer3DManager);
            return touchManager.onTouchEvent(event);
        } else {
            return super.onTouchEvent(event);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 请求摄像机权限
        if (requestCode == 1013) {
            if (cameraDrawSelectDialog != null) // 拍照
                cameraDrawSelectDialog.takePhone();
        }
        // 拍照识别
        else if (requestCode == 1014) {
            if (cameraDrawSelectDialog != null) { // 获取选择的照片
                File file = cameraDrawSelectDialog.getOutputImagepath();
                new UnitDiscern(this, file);
            }
        }
        // 相册选择
        else if (requestCode == 1015) {
            // 获取拍照的照片
            if (resultCode == RESULT_OK && null != data) {
                new UnitDiscern(this, data);
            }
        }
    }

}
