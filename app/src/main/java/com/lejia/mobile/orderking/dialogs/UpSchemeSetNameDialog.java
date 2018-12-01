package com.lejia.mobile.orderking.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.lejia.mobile.orderking.R;
import com.lejia.mobile.orderking.utils.TextUtils;
import com.lejia.mobile.orderking.utils.TimeUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Author by HEKE
 *
 * @time 2018/11/30 12:15
 * TODO: 上传铺砖方案命名窗口
 */
public class UpSchemeSetNameDialog extends Dialog {

    @BindView(R.id.nameEditor)
    EditText nameEditor;
    @BindView(R.id.confirm)
    Button confirm;
    @BindView(R.id.cancle)
    Button cancle;
    private OnSchemeNameSetListener onSchemeNameSetListener;

    public UpSchemeSetNameDialog(@NonNull Context context, OnSchemeNameSetListener onSchemeNameSetListener) {
        super(context, R.style.transparentDiag);
        this.onSchemeNameSetListener = onSchemeNameSetListener;
    }

    private void init() {
        nameEditor.setText("新建方案" + TimeUtils.timeNumber());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.dialog_up_scheme);
        ButterKnife.bind(this);
        init();
    }

    @OnClick({R.id.confirm, R.id.cancle})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.confirm:
                String value = nameEditor.getText().toString();
                if (TextUtils.isTextEmpty(value)) {
                    Toast.makeText(getContext(), "请输入方案名称！", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (onSchemeNameSetListener != null)
                    onSchemeNameSetListener.setName(value, false);
                dismiss();
                break;
            case R.id.cancle:
                if (onSchemeNameSetListener != null)
                    onSchemeNameSetListener.setName(null, true);
                dismiss();
                break;
        }
    }

    /**
     * Author by HEKE
     *
     * @time 2018/11/30 12:23
     * TODO: 设置保存方案名称回调接口
     */
    public interface OnSchemeNameSetListener {
        void setName(String name, boolean cancle);
    }
}
