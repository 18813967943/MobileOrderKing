package com.lejia.mobile.orderking.activitys;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.lejia.mobile.orderking.R;
import com.lejia.mobile.orderking.widgets.AuthcodeView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Author by HEKE
 *
 * @time 2018/7/6 14:23
 * TODO: 注册界面
 */
public class RegisterActivity extends BaseActivity {

    @BindView(R.id.account)
    EditText account;
    @BindView(R.id.password)
    EditText password;
    @BindView(R.id.repeat_password)
    EditText repeatPassword;
    @BindView(R.id.passcode)
    EditText passcode;
    @BindView(R.id.authCode)
    AuthcodeView authCode;
    @BindView(R.id.register)
    Button register;

    @Override
    protected void initViews() {
        System.out.println("###### authCode : " + (authCode == null));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.account, R.id.password, R.id.repeat_password, R.id.passcode, R.id.authCode, R.id.register})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.account:
                break;
            case R.id.password:
                break;
            case R.id.repeat_password:
                break;
            case R.id.passcode:
                break;
            case R.id.authCode:
                authCode.loadCodes();
                break;
            case R.id.register:
                break;
        }
    }

}
