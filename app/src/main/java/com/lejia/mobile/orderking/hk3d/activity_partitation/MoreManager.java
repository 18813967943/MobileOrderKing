package com.lejia.mobile.orderking.hk3d.activity_partitation;

import android.content.Context;

import com.lejia.mobile.orderking.R;
import com.lejia.mobile.orderking.dialogs.MenuBar;
import com.lejia.mobile.orderking.hk3d.datas_2d.HouseDatasManager;
import com.lejia.mobile.orderking.utils.TextUtils;

/**
 * Author by HEKE
 *
 * @time 2018/7/17 15:11
 * TODO: 更多选项操作管理对象
 */
public class MoreManager {

    private Context mContext;
    private TilesManager tilesManager;
    private Designer3DManager designer3DManager;
    private HouseDatasManager houseDatasManager;
    private MenuBar menuBar;
    private int menuWidth;

    public MoreManager(Context context, TilesManager tilesManager, Designer3DManager designer3DManager) {
        this.mContext = context;
        this.tilesManager = tilesManager;
        this.designer3DManager = designer3DManager;
        this.houseDatasManager = this.designer3DManager.getDesigner3DRender().getHouseDatasManager();
        this.menuWidth = TextUtils.dip2px(mContext, 80);
        this.menuBar = new MenuBar(mContext, mContext.getResources().getStringArray(R.array.menu_titles)
                , menuWidth, -1, MenuBar.NORMAL);
        this.menuBar.setOnMenuBarChangedListener(onMenuBarChangedListener);
    }

    // 自动切换显示或隐藏
    public void autoShowOrHide() {
        if (!menuBar.isShowing()) {
            int offsetX = mContext.getResources().getDisplayMetrics().widthPixels - menuWidth - 16;
            menuBar.showBottomOffsetX(offsetX);
        } else {
            menuBar.hide();
        }
    }

    // 点击事件接口
    private MenuBar.OnMenuBarChangedListener onMenuBarChangedListener = new MenuBar.OnMenuBarChangedListener() {
        @Override
        public void onItemClicked(int position, String titleValue) {
            switch (position) {
                case 0:
                    // 新建
                    houseDatasManager.clear();
                    tilesManager.setTilesMenuLayoutShowFromNewCreate(false);
                    break;
                case 1:
                    // 打开
                    break;
                case 2:
                    // 保存
                    break;
                case 3:
                    // 另存为
                    break;
                case 4:
                    // 量尺
                    break;
            }
        }
    };

}
