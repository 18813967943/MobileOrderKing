package com.lejia.mobile.orderking.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.lejia.mobile.orderking.R;
import com.lejia.mobile.orderking.utils.TextUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Author by HEKE
 *
 * @time 2018/7/30 15:15
 * TODO: 砖缝设置窗口
 */
public class TileGapsSettingDialog extends Dialog {

    @BindView(R.id.gapSize)
    EditText gapSize;
    @BindView(R.id.gapSizeConfirm)
    TextView gapSizeConfirm;
    @BindView(R.id.gapBalck)
    ImageView gapBalck;
    @BindView(R.id.gapWhite)
    ImageView gapWhite;
    @BindView(R.id.gapColors)
    ImageView gapColors;
    @BindView(R.id.calBrickCounts)
    Button calBrickCounts;

    private OnTileGapSettingListener onTileGapSettingListener;

    // 缝隙宽与颜色
    private int size;
    private int color;

    public TileGapsSettingDialog(@NonNull Context context, OnTileGapSettingListener onTileGapSettingListener) {
        super(context, R.style.transparentDiag);
        this.onTileGapSettingListener = onTileGapSettingListener;
    }

    private void initWindowAttrs() {
        gapSize.setOnEditorActionListener(onEditorActionListener);
        int size = (int) getContext().getResources().getDimension(R.dimen.d200dp);
        int x = (int) getContext().getResources().getDimension(R.dimen.d45dp);
        int y = 2 * (int) getContext().getResources().getDimension(R.dimen.d55dp);
        Window window = getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.gravity = Gravity.RIGHT | Gravity.BOTTOM;
        layoutParams.width = size;
        layoutParams.height = size - x;
        layoutParams.x = x;
        layoutParams.y = y;
        window.setAttributes(layoutParams);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_gap_setting);
        ButterKnife.bind(this);
        initWindowAttrs();
    }

    /**
     * 自动显示或隐藏
     */
    public void autoShowOrHide() {
        if (isShowing()) {
            dismiss();
        } else {
            show();
        }
    }

    /**
     * 设置缝隙宽度
     */
    public void setGapsSize(int size) {
        if (gapSize != null) {
            this.size = size;
            gapSize.setText("" + size);
        }
    }

    /**
     * 设置砖缝颜色
     */
    public void setGapsColor(int color) {
        if (gapColors != null) {
            this.color = color;
            // 黑色
            if (color == 0xFF000000) {
                gapBalck.setBackgroundResource(R.mipmap.huise_chosen);
                gapWhite.setBackgroundResource(R.mipmap.baise);
                gapColors.setBackgroundResource(R.mipmap.caise);
            }
            // 白色
            else if (color == 0xFFFFFFFF) {
                gapBalck.setBackgroundResource(R.mipmap.huise);
                gapWhite.setBackgroundResource(R.mipmap.baise_chosen);
                gapColors.setBackgroundResource(R.mipmap.caise);
            }
            // 其他
            else {
                gapBalck.setBackgroundResource(R.mipmap.huise);
                gapWhite.setBackgroundResource(R.mipmap.baise);
                gapColors.setBackgroundResource(R.mipmap.caise_chosen);
            }
        }
    }

    // 输入法确定执行回调
    private TextView.OnEditorActionListener onEditorActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == KeyEvent.ACTION_DOWN || actionId == EditorInfo.IME_ACTION_DONE) {
                if (onTileGapSettingListener != null) {
                    String size = gapSize.getText().toString();
                    if (TextUtils.isTextEmpty(size))
                        size = "0";
                    onTileGapSettingListener.setGapSize(Integer.parseInt(size));
                    dismiss();
                }
            }
            return false;
        }
    };

    @OnClick({R.id.gapSizeConfirm, R.id.gapBalck, R.id.gapWhite, R.id.gapColors, R.id.calBrickCounts})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.gapSizeConfirm:
                // 设置砖缝厚度
                if (onTileGapSettingListener != null) {
                    String size = gapSize.getText().toString();
                    if (TextUtils.isTextEmpty(size))
                        size = "0";
                    onTileGapSettingListener.setGapSize(Integer.parseInt(size));
                    dismiss();
                }
                break;
            case R.id.gapBalck:
                // 设置黑色砖缝
                if (onTileGapSettingListener != null) {
                    onTileGapSettingListener.setGapColor(0);
                    dismiss();
                }
                break;
            case R.id.gapWhite:
                // 设置白色砖缝
                if (onTileGapSettingListener != null) {
                    onTileGapSettingListener.setGapColor(-1);
                    dismiss();
                }
                break;
            case R.id.gapColors:
                // 跳转至颜色选择窗口
                new ColorSelectorDialog(getContext(), onTileGapSettingListener).show();
                break;
            case R.id.calBrickCounts:
                if (onTileGapSettingListener != null) {
                    onTileGapSettingListener.calBrickCounts();
                    dismiss();
                }
                break;
        }
    }

    /**
     * Author by HEKE
     *
     * @time 2018/7/30 15:33
     * TODO: 砖缝设置回调接口
     */
    public interface OnTileGapSettingListener {
        void setGapSize(int size);

        void setGapColor(int color);

        void calBrickCounts();
    }
}
