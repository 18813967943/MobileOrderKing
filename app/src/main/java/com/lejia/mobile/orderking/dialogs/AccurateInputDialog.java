package com.lejia.mobile.orderking.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.lejia.mobile.orderking.R;
import com.lejia.mobile.orderking.hk3d.datas_2d.House;
import com.lejia.mobile.orderking.hk3d.datas_2d.NormalHouse;
import com.lejia.mobile.orderking.hk3d.datas_2d.RectHouse;
import com.lejia.mobile.orderking.utils.TextUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Author by HEKE
 *
 * @time 2018/9/19 15:08
 * TODO: 精准输入模式
 */
public class AccurateInputDialog extends Dialog {

    public static final int USUALLY = 0; // 断墙画法分类
    public static final int RECT = 1; // 矩形

    @BindView(R.id.xlong)
    EditText xlong;
    @BindView(R.id.width)
    EditText width;
    @BindView(R.id.height)
    EditText height;
    @BindView(R.id.cancel)
    Button cancel;
    @BindView(R.id.confirm)
    Button confirm;
    @BindView(R.id.longLayout)
    LinearLayout longLayout;
    @BindView(R.id.widthLayout)
    LinearLayout widthLayout;
    @BindView(R.id.heightLayout)
    LinearLayout heightLayout;

    private int flag = USUALLY;
    private House house;
    private OnAccurateInputListener onAccurateInputListener;

    public AccurateInputDialog(@NonNull Context context, House house, int flag, OnAccurateInputListener onAccurateInputListener) {
        super(context, R.style.transparentDiag);
        this.flag = flag;
        this.house = house;
        this.onAccurateInputListener = onAccurateInputListener;
    }

    private void init() {
        Window window = getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        int width = TextUtils.dip2px(getContext(), 260);
        boolean isRect = (flag == RECT);
        int height = (isRect ? TextUtils.dip2px(getContext(), 200) : TextUtils.dip2px(getContext(), 180));
        layoutParams.gravity = Gravity.CENTER;
        layoutParams.width = width;
        layoutParams.height = height;
        window.setAttributes(layoutParams);
        // 显示类型处理、数据显示处理
        if (isRect) {
            longLayout.setVisibility(View.GONE);
            widthLayout.setVisibility(View.VISIBLE);
            heightLayout.setVisibility(View.VISIBLE);
            RectHouse rectHouse = (RectHouse) house;
            this.width.setText("" + (int) (10 * rectHouse.width - 24));
            this.height.setText("" + (int) (10 * rectHouse.height - 24));
        } else {
            longLayout.setVisibility(View.VISIBLE);
            widthLayout.setVisibility(View.GONE);
            heightLayout.setVisibility(View.GONE);
            NormalHouse normalHouse = (NormalHouse) house;
            this.xlong.setText("" + (int) (10 * normalHouse.getCurrentWallLength()));
        }
        // 外部点击消失处理
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (onAccurateInputListener != null)
                    onAccurateInputListener.cancel();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.dialog_accurate_input);
        ButterKnife.bind(this);
        init();
    }

    @OnClick({R.id.cancel, R.id.confirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.cancel:
                // 回调取消
                if (onAccurateInputListener != null)
                    onAccurateInputListener.cancel();
                break;
            case R.id.confirm:
                // 回调数据
                String lonStr = xlong.getText().toString();
                String widStr = width.getText().toString();
                String heiStr = height.getText().toString();
                int lonVal = TextUtils.isTextEmpity(lonStr) ? 0 : Integer.parseInt(lonStr);
                int widVal = TextUtils.isTextEmpity(widStr) ? 0 : Integer.parseInt(widStr);
                int heiVal = TextUtils.isTextEmpity(heiStr) ? 0 : Integer.parseInt(heiStr);
                if (onAccurateInputListener != null) {
                    onAccurateInputListener.onInputed(lonVal, widVal, heiVal, flag);
                }
                break;
        }
        dismiss();
    }

    /**
     * Author by HEKE
     *
     * @time 2018/9/19 18:12
     * TODO: 回调设置数据接口
     */
    public interface OnAccurateInputListener {
        void onInputed(int xlong, int width, int height, int flag);

        void cancel();
    }
}
