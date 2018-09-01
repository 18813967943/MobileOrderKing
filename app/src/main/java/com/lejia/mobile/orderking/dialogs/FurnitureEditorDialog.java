package com.lejia.mobile.orderking.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.lejia.mobile.orderking.R;
import com.lejia.mobile.orderking.hk3d.activity_partitation.TilesManager;
import com.lejia.mobile.orderking.hk3d.classes.Point;
import com.lejia.mobile.orderking.hk3d.datas.Furniture;
import com.lejia.mobile.orderking.hk3d.datas.HouseDatasManager;
import com.lejia.mobile.orderking.hk3d.datas.cadwidgets.BaseCad;
import com.lejia.mobile.orderking.utils.TextUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Author by HEKE
 *
 * @time 2018/8/28 17:46
 * TODO: 家具模型编辑窗口
 */
public class FurnitureEditorDialog extends Dialog {

    @BindView(R.id.copy)
    ImageView copy;
    @BindView(R.id.mirror)
    ImageView mirror;
    @BindView(R.id.replace)
    ImageView replace;
    @BindView(R.id.delete)
    ImageView delete;
    @BindView(R.id.singleProduct)
    ImageView singleProduct;

    private Window window;
    private WindowManager.LayoutParams layoutParams;
    private int windowWidth;
    private int windowHeight;
    private Point atPoint;
    private BaseCad selector;

    private HouseDatasManager houseDatasManager;
    private TilesManager tilesManager;

    public FurnitureEditorDialog(@NonNull Context context, HouseDatasManager houseDatasManager, TilesManager tilesManager) {
        super(context, R.style.transparentDiag);
        this.houseDatasManager = houseDatasManager;
        this.tilesManager = tilesManager;
    }

    private void initWindow() {
        window = getWindow();
        layoutParams = window.getAttributes();
        layoutParams.gravity = Gravity.LEFT | Gravity.TOP;
        layoutParams.width = TextUtils.dip2px(getContext(), 220);
        layoutParams.height = TextUtils.dip2px(getContext(), 40);
        layoutParams.x = (int) atPoint.x;
        layoutParams.y = (int) atPoint.y;
        window.setAttributes(layoutParams);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.dialog_furnitur_editor);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.copy, R.id.mirror, R.id.replace, R.id.delete, R.id.singleProduct})
    public void onViewClicked(View view) {
        if (selector == null)
            return;
        switch (view.getId()) {
            case R.id.copy:
                houseDatasManager.copy(selector);
                break;
            case R.id.mirror:
                houseDatasManager.mirror(selector);
                break;
            case R.id.replace:
                tilesManager.setFromReplace(true, new TilesManager.OnReplaceFurnitureListener() {
                    @Override
                    public void replaced(Furniture furniture) {
                        houseDatasManager.relace(selector, furniture);
                    }
                });
                tilesManager.setFlag(TilesManager.FLAG_LAYOUTS);
                tilesManager.setTilesMenuLayoutShowFromNewCreate(true);
                break;
            case R.id.delete:
                houseDatasManager.delete(selector);
                break;
            case R.id.singleProduct:
                Toast.makeText(getContext(), "敬请期待，暂未开放!", Toast.LENGTH_SHORT).show();
                break;
        }
        dismiss();
    }

    /**
     * 显示在指定的某个起始位置
     *
     * @param point
     * @param selector
     */
    public void show(Point point, BaseCad selector) {
        if (point == null || selector == null)
            return;
        this.atPoint = point;
        this.selector = selector;
        initWindow();
        show();
    }

}
