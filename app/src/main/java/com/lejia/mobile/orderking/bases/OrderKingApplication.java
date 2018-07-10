package com.lejia.mobile.orderking.bases;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.lejia.mobile.orderking.hk3d.Designer3DSurfaceView;
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
     * 三维渲染控件
     */
    private Designer3DSurfaceView designer3DSurfaceView;

    @Override
    public void onCreate() {
        super.onCreate();
        // 自动读取用户登入缓存信息
        sp = getSharedPreferences("USER_CACHE", Context.MODE_PRIVATE);
        String vs = sp.getString("USER", null);
        if (vs != null) {
            mUser = new User(vs);
        }
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

    /**
     * 渲染刷新渲染控件
     */
    public void render() {
        if (designer3DSurfaceView == null)
            return;
        designer3DSurfaceView.requestRender();
    }

}
