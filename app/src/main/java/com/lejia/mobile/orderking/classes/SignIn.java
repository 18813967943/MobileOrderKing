package com.lejia.mobile.orderking.classes;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.lejia.mobile.orderking.R;
import com.lejia.mobile.orderking.bases.HttpsConfig;
import com.lejia.mobile.orderking.hk3d.HK3DDesignerActivity;
import com.lejia.mobile.orderking.https.OkHttpRequest;
import com.lejia.mobile.orderking.https.ReqCallBack;
import com.lejia.mobile.orderking.httpsResult.ResponseEntity;
import com.lejia.mobile.orderking.utils.TextUtils;

import java.util.HashMap;

/**
 * Author by HEKE
 *
 * @time 2018/7/6 20:32
 * TODO: 登入
 */
public class SignIn {

    private Context mContext;
    private String account;
    private String password;

    public SignIn(Context context, String account, String password) {
        mContext = context;
        this.account = account;
        this.password = password;
        checkDatas();
    }

    // 数据检测
    private void checkDatas() {
        // 数据为空提示填写完整信息
        if (TextUtils.isTextEmpity(account) || TextUtils.isTextEmpity(password)) {
            Toast.makeText(mContext, R.string.please_input_hole_datas, Toast.LENGTH_SHORT).show();
            return;
        }
        login();
    }

    /**
     * 登入操作
     */
    private void login() {
        HashMap<String, String> params = new HashMap<>();
        params.put("userName", account);
        params.put("password", password);
        OkHttpRequest request = OkHttpRequest.getInstance(mContext);
        request.requestAsyn(HttpsConfig.SIGN_UP, OkHttpRequest.TYPE_POST_JSON, params, new ReqCallBack<Object>() {
            @Override
            public void onReqSuccess(Object result) {
                if (result != null) {
                    ResponseEntity responseEntity = new ResponseEntity(result);
                    Toast.makeText(mContext, responseEntity.msg, Toast.LENGTH_SHORT).show();
                    // 登入成功
                    //if (responseEntity.state == 1) {
                        // 进入首页
                        mContext.startActivity(new Intent(mContext, HK3DDesignerActivity.class));
                   // }
                }
            }

            @Override
            public void onReqFailed(String errorMsg) {
                Toast.makeText(mContext, R.string.sign_up_error, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
