package com.lejia.mobile.orderking.activitys;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.lejia.mobile.orderking.bases.OrderKingApplication;
import com.lejia.mobile.orderking.classes.SignIn;
import com.lejia.mobile.orderking.httpsResult.classes.User;

/**
 * Author by HEKE
 *
 * @time 2018/7/9 10:56
 * TODO: 自动登入检测界面
 */
public class AutoLoginCheckActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        check();
    }

    /**
     * 检测
     */
    private void check() {
        User user = ((OrderKingApplication) getApplicationContext()).mUser;
        // 未含有登入信息，跳转至登录界面
        if (user == null) {
            startActivity(new Intent(this, LoginActivity.class));
        }
        // 已登录过，再次自动登录
        else {
            new SignIn(this, user.getAccount(), user.getPassowrd(), new SignIn.OnSignInMsgListener() {
                @Override
                public void error() {
                    startActivity(new Intent(AutoLoginCheckActivity.this, LoginActivity.class));
                }
            });
        }
    }

}
