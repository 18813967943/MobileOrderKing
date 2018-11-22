package com.lejia.mobile.orderking.integrationActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.lejia.mobile.orderking.R;
import com.lejia.mobile.orderking.activitys.BaseActivity;
import com.lejia.mobile.orderking.bases.OrderKingApplication;
import com.lejia.mobile.orderking.hk3d.HK3DDesignerActivity;
import com.lejia.mobile.orderking.https.KosapRequest;
import com.lejia.mobile.orderking.https.OnKosapResponseListener;
import com.lejia.mobile.orderking.httpsResult.classes.User;
import com.lejia.mobile.orderking.utils.TextUtils;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Author by HEKE
 *
 * @time 2018/11/15 16:48
 * TODO: 登入界面
 */
public class LoginActivity extends BaseActivity {

    @BindView(R.id.enter_username)
    EditText enterUsername;
    @BindView(R.id.enter_password)
    EditText enterPassword;
    @BindView(R.id.loginBut)
    Button loginBut;

    @Override
    protected void initViews() {
        setTopBarTitle(R.string.login);
        setHasBackButton(false);
        // 检测是否存在登入缓存信息
        User user = ((OrderKingApplication) getApplicationContext()).mUser;
        if (user != null) {
            if (!TextUtils.isTextEmpty(user.getAccount())) {
                // 进入主界面
                startActivity(new Intent(LoginActivity.this, HK3DDesignerActivity.class));
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_integration_login);
    }

    @OnClick(R.id.loginBut)
    public void onViewClicked() {
        final String account = enterUsername.getText().toString();
        final String passowrd = enterPassword.getText().toString();
        if (TextUtils.isTextEmpty(account) || TextUtils.isTextEmpty(passowrd)) {
            Toast.makeText(this, R.string.please_input_hole_datas, Toast.LENGTH_SHORT).show();
            return;
        }
        // 登入参数
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("name", account);
        params.put("password", passowrd);
        // 请求登入,过时LoginMobileUser
        KosapRequest login = new KosapRequest(this, "LeJiaLogin", params, new OnKosapResponseListener() {
            @Override
            public void response(String result, boolean error) {
                // 返回结果
                if (!TextUtils.isTextEmpty(result)) {
                    if (result.contains("登录成功")) {
                        // 拉取用户详细信息
                        // 参数
                        HashMap<String, String> params = new HashMap<>();
                        params.put("userName", account);
                        // 请求信息
                        KosapRequest pullUserInfo = new KosapRequest(LoginActivity.this, "GetMobileUserInfo", params, new OnKosapResponseListener() {
                            @Override
                            public void response(String result, boolean error) {
                                // 拉取数据不为空
                                if (!TextUtils.isTextEmpty(result)) {
                                    String[] infos = result.split("[|]");
                                    User user = new User();
                                    user.token = infos[0];
                                    user.enterpriseInfo = infos[infos.length - 1];
                                    user.setAccount(infos[1]);
                                    user.userName = infos[2];
                                    user.setPassowrd(passowrd);
                                    user.setRemark(infos[infos.length - 2]);
                                    ((OrderKingApplication) getApplicationContext()).setUser(user);
                                    // 进入主界面
                                    startActivity(new Intent(LoginActivity.this, HK3DDesignerActivity.class));
                                }
                            }

                            @Override
                            public void useLocal() {
                            }
                        });
                        pullUserInfo.request();
                    } else {
                        // 提示登入失败
                        Toast.makeText(LoginActivity.this, "账号或密码错误!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void useLocal() {
            }
        });
        login.request();
    }

}
