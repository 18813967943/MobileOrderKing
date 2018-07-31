package com.lejia.mobile.orderking.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.lejia.mobile.orderking.R;
import com.lejia.mobile.orderking.widgets.ColorSelectorImageView;
import com.lejia.mobile.orderking.widgets.ColorsPillarSelectorView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Author by HEKE
 *
 * @time 2018/7/30 16:43
 * TODO: 颜色选择窗口
 */
public class ColorSelectorDialog extends Dialog {

    @BindView(R.id.colorMirror)
    ColorSelectorImageView colorMirror;
    @BindView(R.id.confirm)
    Button confirm;
    @BindView(R.id.cpsView)
    ColorsPillarSelectorView cpsView;

    private TileGapsSettingDialog.OnTileGapSettingListener onTileGapSettingListener;

    public ColorSelectorDialog(@NonNull Context context, TileGapsSettingDialog.OnTileGapSettingListener onTileGapSettingListener) {
        super(context, R.style.transparentDiag);
        this.onTileGapSettingListener = onTileGapSettingListener;
    }

    private void initAttrs() {
        // 绑定柱状图与预览图
        cpsView.setOnPillarColorSelectListener(colorMirror.getOnPillarColorSelectListener());
        // 设置窗口属性
        Window window = getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.width = getContext().getResources().getDisplayMetrics().widthPixels;
        layoutParams.height = getContext().getResources().getDisplayMetrics().heightPixels;
        window.setAttributes(layoutParams);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.dialog_color_selector);
        ButterKnife.bind(this);
        initAttrs();
    }

    @OnClick(R.id.confirm)
    public void onViewClicked() {
        if (colorMirror != null) {
            int color = colorMirror.getSelectColor();
            if (onTileGapSettingListener != null)
                onTileGapSettingListener.setGapColor(color);
        }
        dismiss();
    }

}
