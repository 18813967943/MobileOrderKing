package com.lejia.mobile.orderking.classes;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.lejia.mobile.orderking.R;
import com.lejia.mobile.orderking.bases.HttpsConfig;
import com.lejia.mobile.orderking.https.OkHttpRequest;
import com.lejia.mobile.orderking.https.ReqCallBack;
import com.lejia.mobile.orderking.httpsResult.ResponseEntity;
import com.lejia.mobile.orderking.httpsResult.classes.ValidateCode;
import com.lejia.mobile.orderking.utils.TextUtils;

import java.util.HashMap;

/**
 * Author by HEKE
 *
 * @time 2018/7/6 19:42
 * TODO: 注册
 */
public class SignUp {

    private Context mContext;
    private String account;
    private String password;
    private String repeatPassword;
    private String inputCode;
    private ValidateCode validateCode;

    public SignUp(Context context, String account, String password, String repeatPassword,
                  String inputCode, ValidateCode validateCode) {
        mContext = context;
        this.account = account;
        this.password = password;
        this.repeatPassword = repeatPassword;
        this.inputCode = inputCode;
        this.validateCode = validateCode;
        handle();
    }

    /**
     * 数据处理
     */
    private void handle() {
        // 数据为空提示填写完整信息
        if (TextUtils.isTextEmpty(account) || TextUtils.isTextEmpty(password) || TextUtils.isTextEmpty(repeatPassword) || TextUtils.isTextEmpty(inputCode)) {
            Toast.makeText(mContext, R.string.please_input_hole_datas, Toast.LENGTH_SHORT).show();
            return;
        }
        // 密码重复输入不匹配
        if (!(password.equals(repeatPassword))) {
            Toast.makeText(mContext, R.string.input_twice_error, Toast.LENGTH_SHORT).show();
            return;
        }
        signUp();
    }

    /**
     * 注册
     */
    private void signUp() {
        HashMap<String, String> params = new HashMap<>();
        params.put("cellPhone", account);
        params.put("email", "");
        params.put("password", password);
        params.put("verificationCodeID", validateCode.id);
        params.put("verificationCode", inputCode);
        OkHttpRequest request = OkHttpRequest.getInstance(mContext);
        request.requestAsyn(HttpsConfig.SIGN_UP, OkHttpRequest.TYPE_POST_JSON, params, new ReqCallBack<Object>() {
            @Override
            public void onReqSuccess(Object result) {
                if (result != null) {
                    ResponseEntity responseEntity = new ResponseEntity(result);
                    Toast.makeText(mContext, responseEntity.msg, Toast.LENGTH_SHORT).show();
                    finishActivity();
                }
            }

            @Override
            public void onReqFailed(String errorMsg) {
                Toast.makeText(mContext, R.string.sign_up_error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 退出界面
    private void finishActivity() {
        if (mContext instanceof Activity) {
            ((Activity) mContext).finish();
        }
    }

}
