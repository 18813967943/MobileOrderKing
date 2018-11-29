package com.lejia.mobile.orderking.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.lejia.mobile.orderking.R;
import com.lejia.mobile.orderking.widgets.TileDirectionSelectorView;

import butterknife.BindView;
import butterknife.ButterKnife;
import geom.gpc.GPCConfig;

/**
 * Author by HEKE
 *
 * @time 2018/7/30 9:34
 * TODO: 铺装设置起铺方向的窗口
 */
public class TileDirectionDialog extends Dialog {

    @BindView(R.id.tileDirectionSelector)
    TileDirectionSelectorView tileDirectionSelector;

    private TileDirectionSelectorView.OnTileDirectionsSelectedListener onTileDirectionsSelectedListener;

    private int direction = GPCConfig.FROM_RIGHT_TOP; // 铺砖方向

    public TileDirectionDialog(@NonNull Context context, TileDirectionSelectorView.OnTileDirectionsSelectedListener onTileDirectionsSelectedListener) {
        super(context, R.style.transparentDiag);
        this.onTileDirectionsSelectedListener = onTileDirectionsSelectedListener;
    }

    private void initViews() {
        tileDirectionSelector.setOnTileDirectionsSelectedListener(onTileDirectionsSelectedListener);
        // 设置窗口属性
        int size = (int) getContext().getResources().getDimension(R.dimen.d150dp);
        int x = (int) getContext().getResources().getDimension(R.dimen.d45dp);
        int y = (int) getContext().getResources().getDimension(R.dimen.d55dp);
        Window window = getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.gravity = Gravity.RIGHT | Gravity.BOTTOM;
        layoutParams.width = size;
        layoutParams.height = size;
        layoutParams.x = x;
        layoutParams.y = y;
        window.setAttributes(layoutParams);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.dialog_tile_direction);
        ButterKnife.bind(this);
        initViews();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            tileDirectionSelector.setDirection(direction);
        }
    }

    /**
     * 设置窗口显示时的起铺方向
     *
     * @param direction
     */
    public void setDirection(int direction) {
        if (tileDirectionSelector == null)
            return;
        this.direction = direction;
    }

    public int getDirection() {
        if (tileDirectionSelector == null)
            return -1;
        return tileDirectionSelector.getDirection();
    }

    /**
     * 自动显示与隐藏
     */
    public void autoShowOrHide() {
        if (isShowing()) {
            dismiss();
        } else {
            show();
        }
    }

}
