package com.lejia.mobile.orderking.hk3d.activity_partitation;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import com.lejia.mobile.orderking.hk3d.Designer3DRender;
import com.lejia.mobile.orderking.hk3d.Designer3DSurfaceView;
import com.lejia.mobile.orderking.hk3d.OnRenderStatesListener;
import com.lejia.mobile.orderking.hk3d.datas_3d.ShadowsGLSurfaceView;
import com.lejia.mobile.orderking.hk3d.datas_3d.ShadowsRenderer;

/**
 * Author by HEKE
 *
 * @time 2018/7/13 10:42
 * TODO: 三维管理对象
 */
public class Designer3DManager {

    private Context mContext;
    private LinearLayout designer3dLayout;

    // 三维平面控件
    private Designer3DSurfaceView designer3DSurfaceView;
    // 三维平面渲染对象
    private Designer3DRender designer3DRender;

    // 即时光影控件
    private ShadowsGLSurfaceView shadowsGLSurfaceView;
    // 即时光影渲染对象
    private ShadowsRenderer shadowsRenderer;

    private void initViews() {
        designer3DSurfaceView = new Designer3DSurfaceView(mContext, onRenderStatesListener);
        designer3DRender = designer3DSurfaceView.getDesigner3DRender();
        shadowsGLSurfaceView = new ShadowsGLSurfaceView(mContext);
        shadowsRenderer = shadowsGLSurfaceView.getRenderer();
        designer3dLayout.addView(designer3DSurfaceView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT
                , LinearLayout.LayoutParams.MATCH_PARENT));
        designer3dLayout.addView(shadowsGLSurfaceView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT
                , LinearLayout.LayoutParams.MATCH_PARENT));

        // test
        designer3DSurfaceView.setVisibility(View.GONE);
    }

    public Designer3DManager(Context context, LinearLayout designer3dLayout) {
        mContext = context;
        this.designer3dLayout = designer3dLayout;
        initViews();
    }

    /**
     * 获取三维平面数据渲染对象
     */
    public Designer3DRender getDesigner3DRender() {
        return designer3DRender;
    }

    /**
     * 三维操作回调状态接口
     */
    private OnRenderStatesListener onRenderStatesListener = new OnRenderStatesListener() {
    };

}
