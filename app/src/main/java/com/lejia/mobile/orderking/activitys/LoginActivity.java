package com.lejia.mobile.orderking.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.lejia.mobile.orderking.R;
import com.lejia.mobile.orderking.classes.SignIn;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Author by HEKE
 *
 * @time 2018/7/6 10:24
 * TODO: 登入界面
 */
public class LoginActivity extends BaseActivity {

    @BindView(R.id.account)
    EditText account;
    @BindView(R.id.password)
    EditText password;
    @BindView(R.id.forgetPassword)
    TextView forgetPassword;
    @BindView(R.id.login)
    Button login;
    @BindView(R.id.register)
    Button register;

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

    @OnClick({R.id.forgetPassword, R.id.login, R.id.register})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.forgetPassword:
                break;
            case R.id.login:
                new SignIn(LoginActivity.this, account.getText().toString(), password.getText().toString());
                break; 
            case R.id.register:
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                break;
        }
    }

}
