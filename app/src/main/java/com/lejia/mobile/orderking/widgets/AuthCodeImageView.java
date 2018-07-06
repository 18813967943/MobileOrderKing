package com.lejia.mobile.orderking.widgets;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.lejia.mobile.orderking.bases.HttpsConfig;
import com.lejia.mobile.orderking.https.OkHttpRequest;
import com.lejia.mobile.orderking.https.ReqCallBack;
import com.lejia.mobile.orderking.httpsResult.ResponseEntity;
import com.lejia.mobile.orderking.httpsResult.classes.ValidateCode;

import java.util.HashMap;

/**
 * Author by HEKE
 *
 * @time 2018/7/6 17:24
 * TODO: 验证码图片控件
 */
@SuppressLint("AppCompatCustomView")
public class AuthCodeImageView extends ImageView {

    /**
     * TODO: 刷新状态
     */
    private boolean isRefreshing;

    /**
     * 验证码数据对象
     */
    private ValidateCode validateCode;

    private void initAttrs() {
        setOnClickListener(onClickListener);
        performClick();
    }

    public AuthCodeImageView(Context context) {
        super(context);
        initAttrs();
    }

    public AuthCodeImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttrs();
    }

    public AuthCodeImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs();
    }

    // 点击事件
    private OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (isRefreshing) {
                return;
            }
            isRefreshing = true;
            fetchValidateCode();
        }
    };

    // 请求数据
    private void fetchValidateCode() {
        // 参数
        HashMap<String, String> params = new HashMap<>();
        params.put("appsecret", "LeJiatbBLpR64lkz3EYPQPj3qObRiG8WZPxvW");
        // 请求处理数据
        OkHttpRequest request = OkHttpRequest.getInstance(getContext());
        request.requestAsyn(HttpsConfig.GetValidateCode, OkHttpRequest.TYPE_POST_JSON, params, new ReqCallBack<Object>() {
            @Override
            public void onReqSuccess(Object result) {
                if (result != null) {
                    try {
                        ResponseEntity entity = new ResponseEntity(result);
                        Gson gson = new Gson();
                        validateCode = gson.fromJson(entity.getJSonObject("validateCode"), ValidateCode.class);
                        if (validateCode != null && validateCode.isValid()) {
                            Glide.with(getContext()).load(validateCode.codeImg).into(AuthCodeImageView.this);
                        }
                        isRefreshing = false;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onReqFailed(String errorMsg) {
            }
        });
    }

    // 获取验证码数据对象
    public ValidateCode getValidateCode() {
        return validateCode;
    }

}
