package com.lejia.mobile.orderking.activitys;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.lejia.mobile.orderking.R;
import com.lejia.mobile.orderking.classes.SignUp;
import com.lejia.mobile.orderking.widgets.AuthCodeImageView;

import butterknife.BindView;
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
    AuthCodeImageView authCode;
    @BindView(R.id.register)
    Button register;

    @Override
    protected void initViews() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_register);
    }

    @OnClick({R.id.register})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.register:
                new SignUp(RegisterActivity.this, account.getText().toString()
                        , password.getText().toString(), repeatPassword.getText().toString()
                        , passcode.getText().toString(), authCode.getValidateCode());
                break;
        }
    }


}
