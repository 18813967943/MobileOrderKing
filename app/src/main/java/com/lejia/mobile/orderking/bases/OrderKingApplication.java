package com.lejia.mobile.orderking.bases;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.lejia.mobile.orderking.hk3d.Designer3DSurfaceView;
import com.lejia.mobile.orderking.hk3d.classes.TileDescription;
import com.lejia.mobile.orderking.httpsResult.classes.MaterialTypeList;
import com.lejia.mobile.orderking.httpsResult.classes.User;

import java.util.ArrayList;

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

    /**
     * 当前用户企业信息数据节点列表
     */
    public MaterialTypeList materialTypeList;

    /**
     * 当前用户企业信息模型数据节点列表
     */
    public MaterialTypeList furnitureMaterialTypeList;

    /**
     * 当前用户企业信息大类数据节点列表
     */
    public MaterialTypeList furnitureCatlogList;

    // 应用存储信息
    private SharedPreferences sp;

    /**
     * 三维渲染控件
     */
    private Designer3DSurfaceView designer3DSurfaceView;

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
     * 默认换砖材质第一页数据对象内容
     */
    public ArrayList<TileDescription> defaultTileDescriptionList;

    @Override
    public void onCreate() {
        super.onCreate();
        // 设置单例程序上下文
        context = getApplicationContext();
        defaultTileDescriptionList = new ArrayList<>();
        // 自动读取用户登入缓存信息
        sp = getSharedPreferences("USER_CACHE", Context.MODE_PRIVATE);
        String vs = sp.getString("USER", null);
        if (vs != null) {
            mUser = new User(vs);
        }
        String mtl = sp.getString("MATERIAL_TYPE_LIST", null);
        if (mtl != null) {
            materialTypeList = new MaterialTypeList(mtl);
        }
        String fmtl = sp.getString("FURNITURES_MATERIAL_TYPE_LIST", null);
        if (fmtl != null) {
            furnitureMaterialTypeList = new MaterialTypeList(fmtl);
        }
        String catlogmtl = sp.getString("FURNITURE_CATLOG_LIST", null);
        if (catlogmtl != null) {
            furnitureCatlogList = new MaterialTypeList(catlogmtl);
            CatlogChecker.setFurnitureCatlogList(furnitureCatlogList);
        }
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
     * 设置企业对应节点数据列表对象
     */
    public void setMaterialTypeList(MaterialTypeList materialTypeList, String json) {
        this.materialTypeList = materialTypeList;
        // 缓存字典信息
        sp = getSharedPreferences("USER_CACHE", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("MATERIAL_TYPE_LIST", json);
        editor.commit();
    }

    /**
     * 设置企业对应模型节点数据列表对象
     */
    public void setFurnitureMaterialTypeList(MaterialTypeList furnitureMaterialTypeList, String json) {
        this.furnitureMaterialTypeList = furnitureMaterialTypeList;
        // 缓存字典信息
        sp = getSharedPreferences("USER_CACHE", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("FURNITURES_MATERIAL_TYPE_LIST", json);
        editor.commit();
    }

    /**
     * 设置企业对应模型大类节点数据列表对象
     */
    public void setFurnitureCatlogList(MaterialTypeList furnitureCatlogList, String json) {
        this.furnitureCatlogList = furnitureCatlogList;
        // 缓存字典信息
        sp = getSharedPreferences("USER_CACHE", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("FURNITURE_CATLOG_LIST", json);
        editor.commit();
        // 绑定大类数据
        CatlogChecker.setFurnitureCatlogList(this.furnitureCatlogList);
    }

    /**
     * 设置企业对应的换砖第一页数据材质，主要用于画房间结束后默认加载材质内容
     *
     * @param defaultTileDescriptionList
     */
    public void setDefaultTileDescriptionList(ArrayList<TileDescription> defaultTileDescriptionList) {
        this.defaultTileDescriptionList = defaultTileDescriptionList;
    }

    /**
     * 获取随机默认铺砖材质
     */
    public TileDescription getRandomTileDescription() {
        if (defaultTileDescriptionList == null || defaultTileDescriptionList.size() == 0)
            return null;
        int position = (int) (Math.random() * defaultTileDescriptionList.size());
        return defaultTileDescriptionList.get(position);
    }

    /**
     * 绑定三维渲染控件
     *
     * @param designer3DSurfaceView
     */
    public void setDesigner3DSurfaceView(Designer3DSurfaceView designer3DSurfaceView) {
        this.designer3DSurfaceView = designer3DSurfaceView;
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
     * 释放三维加载过的数据
     */
    public void release3DViews() {
        if (designer3DSurfaceView == null)
            return;
        designer3DSurfaceView.getDesigner3DRender().requestRelease();
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
