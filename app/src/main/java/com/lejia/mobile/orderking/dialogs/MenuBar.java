package com.lejia.mobile.orderking.dialogs;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.lejia.mobile.orderking.R;
import com.lejia.mobile.orderking.adapters.MenuBarAdapter;

/**
 * @author HEKE 菜单栏
 */
public class MenuBar implements BarInterface {

    public static final int NORMAL = 0x00;
    public static final int HALF_ALPHA = 0x01;
    public static final int BLUE = 0x02;
    public static final int HALF_ALPHA_BLUE = 0x03;
    public static final int LIGHT_GREY = 0x04;

    private Context mContext;
    private String[] items;
    private View view;
    private LinearLayout layout;
    private ListView bar;
    private PopupWindow window;
    private MenuBarAdapter mAdapter;
    private int windowWidth;
    @SuppressWarnings("unused")
    private int windowHeight;

    private OnMenuBarChangedListener onMenuBarChangedListener;

    private boolean isCenterStyle;
    private boolean notNeedBindDragBall;

    public MenuBar(Context context, String[] items, int width, int height, int flag) {
        this.mContext = context;
        this.items = items;
        this.view = View.inflate(mContext, R.layout.menu, null);
        this.layout = view.findViewById(R.id.menuLayout);
        this.bar = view.findViewById(R.id.menuBar);
        this.bar.setOnItemClickListener(itemClickListener);
        if (flag == HALF_ALPHA) {
            this.layout.setBackgroundColor(0x45ffffff);
        } else if (flag == BLUE) {
            this.layout.setBackgroundColor(0xff37c9f2);
        } else if (flag == NORMAL) {
            this.layout.setBackgroundColor(0xffffffff);
        } else if (flag == HALF_ALPHA_BLUE) {
            this.layout.setBackgroundColor(0x9037c9f2);
        } else if (flag == LIGHT_GREY) {
            this.layout.setBackgroundColor(0xbbf0f0f0);
        }
        this.window = new PopupWindow(view);
        if (width == -1) {
            this.window.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
            this.windowWidth = (int) context.getResources().getDimension(R.dimen.menu_width);
        } else {
            this.window.setWidth(width);
            this.windowWidth = width;
        }
        if (height == -1) {
            this.window.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
            this.windowHeight = WindowManager.LayoutParams.WRAP_CONTENT;
        } else if (height == -2) {
            this.window.setHeight(WindowManager.LayoutParams.MATCH_PARENT);
            this.windowHeight = WindowManager.LayoutParams.MATCH_PARENT;
        } else {
            this.window.setHeight(height);
            this.windowHeight = height;
        }
        // 居中样式
        if (width == -1 && height == -2) {
            this.window.setWidth(mContext.getResources().getDisplayMetrics().widthPixels / 2);
            this.window.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
            isCenterStyle = true;
        }
        this.window.setFocusable(true);
        this.window.setTouchable(true);
        this.window.setOutsideTouchable(true);
        this.window.setBackgroundDrawable(new ColorDrawable());
        this.window.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
            }
        });
        this.mAdapter = new MenuBarAdapter(mContext.getApplicationContext(), items);
        this.bar.setAdapter(mAdapter);
    }

    /**
     * 刷新菜单栏内容
     *
     * @param contents
     */
    public void refreshBarContents(String[] contents) {
        this.items = contents;
        this.mAdapter.refreshTitles(this.items);
    }

    public void setNotNeedBindDragBall(boolean notNeedBindDragBall) {
        this.notNeedBindDragBall = notNeedBindDragBall;
    }

    public void setOnMenuBarChangedListener(OnMenuBarChangedListener onMenuBarChangedListener) {
        this.onMenuBarChangedListener = onMenuBarChangedListener;
    }

    @Override
    public void show() {
        if (!isShowing()) {
            if (isCenterStyle) {
                this.window.showAtLocation(view, Gravity.CENTER, 0, 0);
            } else {
                float bottomHeight = mContext.getResources().getDimension(R.dimen.main_bottom_height);
                this.window.showAtLocation(view, Gravity.RIGHT | Gravity.BOTTOM, 8, (int) (8 + bottomHeight));
            }
        }
    }

    /**
     * 底部显示，并可设置X轴上的偏移
     *
     * @param offsetX
     */
    public void showBottomOffsetX(int offsetX) {
        if (!isShowing()) {
            if (isCenterStyle) {
                this.window.showAtLocation(view, Gravity.CENTER, 0, 0);
            } else {
                float bottomHeight = mContext.getResources().getDimension(R.dimen.main_bottom_height);
                this.window.showAtLocation(view, Gravity.LEFT | Gravity.BOTTOM, 8 + offsetX, (int) (8 + bottomHeight));
            }
        }
    }

    public void show(int atY) {
        if (!isShowing()) {
            int screenWidth = mContext.getResources().getDisplayMetrics().widthPixels;
            this.window.showAtLocation(view, Gravity.LEFT | Gravity.TOP, screenWidth - windowWidth, atY);
        }
    }

    @Override
    public void hide() {
        if (isShowing()) {
            this.window.dismiss();
        }
    }


    @Override
    public boolean isShowing() {
        return this.window.isShowing();
    }

    /**
     * 子项点击处理
     */
    private OnItemClickListener itemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            hide();
            if (onMenuBarChangedListener != null) {
                onMenuBarChangedListener.onItemClicked(position, items[position]);
            }
        }
    };

    /**
     * @author HEKE 数据回调接口
     */
    public interface OnMenuBarChangedListener {
        void onItemClicked(int position, String titleValue);
    }

}
