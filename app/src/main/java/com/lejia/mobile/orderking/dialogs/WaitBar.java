package com.lejia.mobile.orderking.dialogs;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.lejia.mobile.orderking.R;

/**
 * @author HEKE 等待信息窗口
 */
public class WaitBar implements BarInterface {

    private View view; // 界面
    private TextView txt; // 文本内容
    private PopupWindow window; // 窗口

    public WaitBar(Context context) {
        // 初始化界面
        this.view = View.inflate(context, R.layout.wait_bar, null);
        this.txt = this.view.findViewById(R.id.textMsg);
        // 初始化窗口
        window = new PopupWindow(view, WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        window.setFocusable(false);
        window.setTouchable(true);
        window.setOutsideTouchable(true);
        window.setBackgroundDrawable(new ColorDrawable());
    }

    /**
     * 设置窗口是否可操作消失
     *
     * @param forbid
     */
    public void setWindow(boolean forbid) {
        if (forbid) {
            window.setTouchable(false);
            window.setFocusable(true);
            window.setOutsideTouchable(false);
            window.setBackgroundDrawable(new ColorDrawable());
        }
    }

    @Override
    public void show() {
        if (!isShowing())
            window.showAtLocation(view, Gravity.CENTER, 0, 0);
    }

    @Override
    public void hide() {
        window.dismiss();
    }

    @Override
    public boolean isShowing() {
        return window.isShowing();
    }

    /**
     * 设置文本是否显示
     *
     * @param flag
     */
    public void setTextVisibility(boolean flag) {
        this.txt.setVisibility(flag ? View.VISIBLE : View.GONE);
    }

    /**
     * 设置要显示的内容
     *
     * @param message
     */
    public void setText(String message) {
        this.txt.setText(message);
    }

}
