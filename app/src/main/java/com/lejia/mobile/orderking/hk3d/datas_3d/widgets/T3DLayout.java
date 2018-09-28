package com.lejia.mobile.orderking.hk3d.datas_3d.widgets;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.lejia.mobile.orderking.hk3d.Designer3DSurfaceView;
import com.lejia.mobile.orderking.hk3d.OnRenderStatesListener;
import com.lejia.mobile.orderking.hk3d.RendererState;
import com.lejia.mobile.orderking.hk3d.datas_3d.DesignerShadow3DSurfaceView;

/**
 * Author by HEKE
 *
 * @time 2018/9/25 15:08
 * TODO: 用于切换平面与三维的控件
 */
public class T3DLayout extends FrameLayout {

    /**
     * 三维绘制平面控件对象
     */
    private Designer3DSurfaceView designer2DSurfaceView;

    /**
     * 三维绘制3D数据内容对象
     */
    private DesignerShadow3DSurfaceView designerShadow3DSurfaceView;

    private void initAttrs() {
        Context appContext = getContext().getApplicationContext();
        designer2DSurfaceView = new Designer3DSurfaceView(appContext, onRenderStatesListener);
        designerShadow3DSurfaceView = new DesignerShadow3DSurfaceView(appContext);
        addView(designer2DSurfaceView, new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        addView(designerShadow3DSurfaceView, new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        show(RendererState.STATE_2D);
    }

    public T3DLayout(Context context) {
        super(context);
        initAttrs();
    }

    public T3DLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttrs();
    }

    public T3DLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs();
    }

    /**
     * 切换2D/25D3D显示
     *
     * @param flag 0位平面，1为轴侧，2为进入
     */
    public void show(int flag) {
        // 设置显示内容
        RendererState.setRenderState(flag);
        // 切换面板显示
        if (flag == RendererState.STATE_2D) {
            designer2DSurfaceView.setVisibility(VISIBLE);
            designerShadow3DSurfaceView.setVisibility(GONE);
            // 恢复实景三维渲染参数
            designerShadow3DSurfaceView.getDesignerShadow3DRender().resetParams();
        } else {
            designer2DSurfaceView.setVisibility(GONE);
            designerShadow3DSurfaceView.setVisibility(VISIBLE);
            // 根据三维状态切换
            if (flag == RendererState.STATE_25D) {
                designerShadow3DSurfaceView.getDesignerShadow3DRender().toAxisViews();
            } else {
                designerShadow3DSurfaceView.getDesignerShadow3DRender().gotoHouse();
            }
            // 刷新三维数据显示
            designerShadow3DSurfaceView.requestRender();
        }
    }

    /**
     * 获取当前平面数据控件
     */
    public Designer3DSurfaceView getDesigner2DSurfaceView() {
        return designer2DSurfaceView;
    }

    /**
     * 获取当前三维数据控件
     */
    public DesignerShadow3DSurfaceView getDesignerShadow3DSurfaceView() {
        return designerShadow3DSurfaceView;
    }

    /**
     * Author by HEKE
     *
     * @time 2018/9/25 15:29
     * TODO: 三维平面画墙等操作监听回调接口
     */
    private OnRenderStatesListener onRenderStatesListener = new OnRenderStatesListener() {
    };

}
