package com.lejia.mobile.orderking.classes;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lejia.mobile.orderking.R;
import com.lejia.mobile.orderking.bases.HttpsConfig;
import com.lejia.mobile.orderking.bases.OrderKingApplication;
import com.lejia.mobile.orderking.hk3d.HK3DDesignerActivity;
import com.lejia.mobile.orderking.https.OkHttpRequest;
import com.lejia.mobile.orderking.https.ReqCallBack;
import com.lejia.mobile.orderking.httpsResult.ResponseEntity;
import com.lejia.mobile.orderking.httpsResult.classes.User;
import com.lejia.mobile.orderking.utils.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

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

    private OnSignInMsgListener onSignInMsgListener;

    public SignIn(Context context, String account, String password) {
        mContext = context;
        this.account = account;
        this.password = password;
        checkDatas();
    }

    public SignIn(Context context, String account, String password, OnSignInMsgListener onSignInMsgListener) {
        mContext = context;
        this.account = account;
        this.password = password;
        this.onSignInMsgListener = onSignInMsgListener;
        checkDatas();
    }

    // 数据检测
    private void checkDatas() {
        // 数据为空提示填写完整信息
        if (TextUtils.isTextEmpity(account) || TextUtils.isTextEmpity(password)) {
            if (errorMsg())
                return;
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
        request.requestAsyn(HttpsConfig.SIGN_IN, OkHttpRequest.TYPE_POST_JSON, params, new ReqCallBack<Object>() {
            @Override
            public void onReqSuccess(Object result) {
                if (result != null) {
                    ResponseEntity responseEntity = new ResponseEntity(result);
                    // 登入成功
                    if (responseEntity.state == 1) {
                        // 存储用户信息
                        try {
                            String dataJsonStr = responseEntity.getData();
                            JSONObject object = new JSONObject(dataJsonStr);
                            String enterpriseInfo = object.getString("enterpriseInfo"); // 企业信息
                            String token = object.getString("token"); // 唯一验证编码
                            JSONObject userObject = object.getJSONObject("userInfo");
                            User user = new Gson().fromJson(userObject.toString(), User.class);
                            user.setEnterpriseInfo(enterpriseInfo);
                            user.setToken(token);
                            user.setAccount(account);
                            user.setPassowrd(password);
                            ((OrderKingApplication) mContext.getApplicationContext()).setUser(user);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        // 进入首页
                        mContext.startActivity(new Intent(mContext, HK3DDesignerActivity.class));
                    } else {
                        Toast.makeText(mContext, responseEntity.msg, Toast.LENGTH_SHORT).show();
                        errorMsg();
                    }
                }
            }

            @Override
            public void onReqFailed(String errorMsg) {
                Toast.makeText(mContext, R.string.sign_in_error, Toast.LENGTH_SHORT).show();
                errorMsg();
            }
        });
    }

    // 错误提示
    private boolean errorMsg() {
        if (onSignInMsgListener != null)
            onSignInMsgListener.error();
        return onSignInMsgListener != null;
    }

    /**
     * Author by HEKE
     *
     * @time 2018/7/9 15:13
     * TODO: 登录信息监听接口
     */
    public interface OnSignInMsgListener {
        void error();
    }

}
