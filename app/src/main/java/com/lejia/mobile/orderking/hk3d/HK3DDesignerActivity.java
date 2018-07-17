package com.lejia.mobile.orderking.hk3d;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.lejia.mobile.orderking.R;
import com.lejia.mobile.orderking.activitys.PermissionsActivity;
import com.lejia.mobile.orderking.hk3d.activity_partitation.Designer3DManager;
import com.lejia.mobile.orderking.hk3d.activity_partitation.TilesManager;
import com.lejia.mobile.orderking.hk3d.activity_partitation.TouchManager;
import com.lejia.mobile.orderking.widgets.ScrollerGridView;
import com.lejia.mobile.orderking.widgets.TitlesView;

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
    @BindView(R.id.title)
    TitlesView title;
    @BindView(R.id.zhouce)
    ImageButton zhouce;
    @BindView(R.id.threed)
    ImageButton threed;
    @BindView(R.id.more)
    ImageButton more;
    @BindView(R.id.designer3dLayout)
    LinearLayout designer3dLayout;
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

    private void initViews() {
        designer3DManager = new Designer3DManager(this, designer3dLayout);
        tilesManager = new TilesManager(this, title, rightLayout, nodesList, detialsList, resGrid, drawStates, designer3DManager);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initViews();
        // 打开权限申请
        startActivityForResult(new Intent(this, PermissionsActivity.class), -1);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @OnClick({R.id.getback, R.id.forward, R.id.jingzhun, R.id.zhouce, R.id.threed, R.id.more, R.id.drawStates})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.getback:
                break;
            case R.id.forward:
                break;
            case R.id.jingzhun:
                break;
            case R.id.zhouce:
                break;
            case R.id.threed:
                break;
            case R.id.more:
                break;
            case R.id.drawStates:
                break;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (touchManager == null)
            touchManager = new TouchManager(this, tilesManager, designer3DManager);
        return touchManager.onTouchEvent(event);
    }

}
