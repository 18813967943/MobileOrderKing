package com.lejia.mobile.orderking.hk3d.activity_partitation;

import android.content.Context;
import android.widget.LinearLayout;

import com.lejia.mobile.orderking.hk3d.Designer3DRender;
import com.lejia.mobile.orderking.hk3d.Designer3DSurfaceView;
import com.lejia.mobile.orderking.hk3d.OnRenderStatesListener;

/**
 * Author by HEKE
 *
 * @time 2018/7/13 10:42
 * TODO: 三维管理对象
 */
public class Designer3DManager {

    private Context mContext;
    private LinearLayout designer3dLayout;

    // 三维控件
    private Designer3DSurfaceView designer3DSurfaceView;
    // 三维渲染对象
    private Designer3DRender designer3DRender;

    private void initViews() {
        designer3DSurfaceView = new Designer3DSurfaceView(mContext.getApplicationContext(), onRenderStatesListener);
        designer3dLayout.addView(designer3DSurfaceView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT
                , LinearLayout.LayoutParams.MATCH_PARENT));
        designer3DRender = designer3DSurfaceView.getDesigner3DRender();
    }

    public Designer3DManager(Context context, LinearLayout designer3dLayout) {
        mContext = context;
        this.designer3dLayout = designer3dLayout;
        initViews();
    }

    // 获取渲染对象
    public Designer3DRender getDesigner3DRender() {
        return designer3DRender;
    }

    /**
     * 三维操作回调状态接口
     */
    private OnRenderStatesListener onRenderStatesListener = new OnRenderStatesListener() {
    };

}
