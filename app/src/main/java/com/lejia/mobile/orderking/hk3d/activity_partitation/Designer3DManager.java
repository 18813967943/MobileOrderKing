package com.lejia.mobile.orderking.hk3d.activity_partitation;

import android.content.Context;
import android.widget.LinearLayout;

import com.lejia.mobile.orderking.hk3d.Designer3DRender;
import com.lejia.mobile.orderking.hk3d.Designer3DSurfaceView;
import com.lejia.mobile.orderking.hk3d.OnRenderStatesListener;
import com.lejia.mobile.orderking.hk3d.datas_3d.DesignerShadow3DSurfaceView;
import com.lejia.mobile.orderking.hk3d.datas_3d.ShadowRenderer;
import com.lejia.mobile.orderking.hk3d.datas_3d.widgets.T3DLayout;

/**
 * Author by HEKE
 *
 * @time 2018/7/13 10:42
 * TODO: 三维管理对象
 */
public class Designer3DManager {

    private Context mContext;
    private LinearLayout designer3dLayout;
    private T3DLayout t3dLayout; // 兼容前平面功能不修改的前提下，合并即时光影控件内容承接对象

    // 三维平面控件
    private Designer3DSurfaceView designer3DSurfaceView;
    // 三维3D数据控件
    private DesignerShadow3DSurfaceView designerShadow3DSurfaceView;
    // 三维平面渲染对象
    private Designer3DRender designer3DRender;
    // 三维3D渲染对象
    private ShadowRenderer shadowRenderer;

    private void initViews() {
        t3dLayout = new T3DLayout(mContext.getApplicationContext());
        designer3dLayout.addView(t3dLayout, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT
                , LinearLayout.LayoutParams.MATCH_PARENT));
        designer3DSurfaceView = t3dLayout.getDesigner2DSurfaceView();
        designerShadow3DSurfaceView = t3dLayout.getDesignerShadow3DSurfaceView();
        designer3DRender = designer3DSurfaceView.getDesigner3DRender();
        shadowRenderer = designerShadow3DSurfaceView.getDesignerShadow3DRender();
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
     * 获取三维3D数据渲染对象
     */
    public ShadowRenderer getShadowRenderer() {
        return shadowRenderer;
    }

    /**
     * 获取兼容平面数据内容的管理对象
     */
    public T3DLayout getT3dLayout() {
        return t3dLayout;
    }

    /**
     * 三维操作回调状态接口
     */
    private OnRenderStatesListener onRenderStatesListener = new OnRenderStatesListener() {
    };

}
