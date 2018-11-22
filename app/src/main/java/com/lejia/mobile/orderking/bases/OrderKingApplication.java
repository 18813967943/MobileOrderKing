package com.lejia.mobile.orderking.bases;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.lejia.mobile.orderking.hk3d.Designer3DSurfaceView;
import com.lejia.mobile.orderking.hk3d.datas_3d.ShadowsGLSurfaceView;
import com.lejia.mobile.orderking.httpsResult.classes.User;

/**
 * Author by HEKE
 *
 * @time 2018/7/6 21:00
 * TODO: 自定义程序对象
 */
public class OrderKingApplication extends Application {

    /**
     * 当前登入用户
     */
    public User mUser;

    // 应用存储信息
    private SharedPreferences sp;

    /**
     * 三维平面数据渲染控件
     */
    private Designer3DSurfaceView designer3DSurfaceView;

    /**
     * 三维即时光影数据渲染控件
     */
    private ShadowsGLSurfaceView shadowsGLSurfaceView;

    /**
     * 单例程序上下文内容
     */
    private static Context context;

    /**
     * 获取程序上下文
     */
    public static Context getInstant() {
        return context;
    }


    /**
     * 主界面上下文
     */
    private static Context mainActivityContext;

    public static Context getMainActivityContext() {
        return mainActivityContext;
    }

    /**
     * 默认铺贴瓷砖
     */
    private String[] defaultTilesCodeArray;

    @Override
    public void onCreate() {
        super.onCreate();
        // 设置单例程序上下文
        context = getApplicationContext();
        // 默认铺砖编码集合
        defaultTilesCodeArray = new String[]{"SAY0889504-1-800X800", "SAY0889489-1-800X800", "SAY0889032-1-800X800", "SAY0989508-1-900X900", "SAY9689423-1-900X600"
                , "SAY9689417-1-900X600", "SAY9689416-1-900X600", "SAY0889487-1-800X800", "SAY0989511-1-900X900", "SAY0889035-1-800X800", "SDF0888442-1-800X800"
                , "SDF0888446-1-800X800", "SAY0889415-R5-1-800X800", "SAY0889526-1-800X800", "SAY0889854-1-800X800", "SAY0889504-1-800X800", "SAY1089236-1-1000X1000"
                , "SAY0889247-1-800X800", "SDF0888243-1-800X800", "SDF0888239-1-800X800", "SDF0888725-1-800X800", "SDF0888237-1-800X800"
                , "SDF0888718-1-800X800", "SDF0888602-1-800X800"};
        // 自动读取用户登入缓存信息
        sp = getSharedPreferences("USER_CACHE", Context.MODE_PRIVATE);
        String vs = sp.getString("USER", null);
        if (vs != null) {
            mUser = new User(vs);
        }
    }

    public static void setMainActivityContext(Context mainActivityContext) {
        OrderKingApplication.mainActivityContext = mainActivityContext;
    }

    /**
     * 释放单例程序上下文
     */
    public void releaseSingleContext() {
        if (context != null)
            context = null;
    }

    /**
     * 设置当前登入账户信息
     */
    public void setUser(User mUser) {
        this.mUser = mUser;
        // 缓存登入用户信息
        sp = getSharedPreferences("USER_CACHE", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("USER", mUser.toString());
        editor.commit();
    }

    /**
     * 绑定三维渲染控件
     *
     * @param designer3DSurfaceView
     */
    public void setDesigner3DSurfaceView(Designer3DSurfaceView designer3DSurfaceView) {
        this.designer3DSurfaceView = designer3DSurfaceView;
    }

    public Designer3DSurfaceView getDesigner3DSurfaceView() {
        return designer3DSurfaceView;
    }

    public ShadowsGLSurfaceView getShadowsGLSurfaceView() {
        return shadowsGLSurfaceView;
    }

    public void setShadowsGLSurfaceView(ShadowsGLSurfaceView shadowsGLSurfaceView) {
        this.shadowsGLSurfaceView = shadowsGLSurfaceView;
    }

    /**
     * 渲染刷新渲染控件
     */
    public void render() {
        if (designer3DSurfaceView == null)
            return;
        designer3DSurfaceView.requestRender();
    }

    /**
     * 渲染刷新即时光影渲染控件
     */
    public void render3D() {
        if (shadowsGLSurfaceView == null)
            return;
        shadowsGLSurfaceView.requestRender();
    }

    /**
     * 在主进程中执行操作
     *
     * @param r
     */
    public void runOnMainUIThread(Runnable r) {
        if (mainActivityContext == null || r == null)
            return;
        ((Activity) mainActivityContext).runOnUiThread(r);
    }

    /**
     * 释放三维加载过的数据
     */
    public void release3DViews() {
        if (designer3DSurfaceView == null)
            return;
        designer3DSurfaceView.getDesigner3DRender().requestRelease();
    }

    /**
     * 获取随机默认铺砖编码
     */
    public String getDefaultTileCode() {
        return defaultTilesCodeArray[(int) (Math.random() * defaultTilesCodeArray.length)];
    }

    /**
     * 根据设备尺寸分辨手机或平板
     *
     * @return
     */
    public boolean isPad() {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(dm);
        double x = Math.pow(dm.widthPixels / dm.xdpi, 2);
        double y = Math.pow(dm.heightPixels / dm.ydpi, 2);
        double screenInches = Math.sqrt(x + y);
        return screenInches >= 7.0d;
    }

}
