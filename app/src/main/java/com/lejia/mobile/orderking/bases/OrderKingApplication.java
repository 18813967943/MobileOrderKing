package com.lejia.mobile.orderking.bases;

import android.app.Application;

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

    @Override
    public void onCreate() {
        super.onCreate();
    }

}
