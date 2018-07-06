package com.lejia.mobile.orderking.activitys;

import android.os.Bundle;

import com.lejia.mobile.orderking.R;

/**
 * Author by HEKE
 *
 * @time 2018/7/6 10:24
 * TODO: 登入界面
 */
public class LoginActivity extends BaseActivity {


    @Override
    protected void initViews() {
        setTopBarTitle(R.string.login);
        setHasMoreButton(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_login);
    }

}
