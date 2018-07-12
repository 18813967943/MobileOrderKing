package com.lejia.mobile.orderking.bases;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.lejia.mobile.orderking.hk3d.Designer3DSurfaceView;
import com.lejia.mobile.orderking.httpsResult.classes.MaterialTypeList;
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

    /**
     * 当前用户企业信息数据节点列表
     */
    public MaterialTypeList materialTypeList;

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

    @Override
    public void onCreate() {
        super.onCreate();
        // 设置单例程序上下文
        context = getApplicationContext();
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

}
