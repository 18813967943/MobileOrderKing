package com.lejia.mobile.orderking.activitys;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lejia.mobile.orderking.R;

import butterknife.ButterKnife;

/**
 * @author HEKE 基类界面
 * @version 2016年9月12日
 */
public abstract class BaseActivity extends AppCompatActivity {

    private LinearLayout rootLayout;
    private Toolbar toolbar;

    /**
     * 初始化界面
     */
    protected abstract void initViews();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_base);
        // 声明透明状态栏更有效
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
        }
        initToolbar();
    }

    private void initToolbar() {
        toolbar =findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        transportStatus();
    }

    @Override
    public void setContentView(int layoutId) {
        setContentView(View.inflate(this, layoutId, null));
    }

    /**
     * 设置状态栏背景颜色
     */
    public void setToolBarBackgroundColor(int color) {
        if (toolbar != null)
            toolbar.setBackgroundColor(color);
    }

    @Override
    public void setContentView(View view) {
        rootLayout = findViewById(R.id.root_layout);
        if (rootLayout == null) return;
        rootLayout.addView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        initToolbar();
        ButterKnife.bind(this);
        initViews();
    }

    //让状态栏及底部都变透明的设置 结合上面，如何想让状态栏都变半透明，底部变透明，可以设以下
    public void transportStatus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);   //状态栏透明
        }
    }

    /**
     * 设置顶部标题名称
     *
     * @param id
     */
    protected void setTopBarTitle(int id) {
        View view = super.findViewById(R.id.title);
        if (view != null) {
            ((TextView) view).setText(getString(id));
        }
    }

    /**
     * 设置顶部标题名称
     *
     * @param value
     */
    protected void setTopBarTitle(String value) {
        View view = super.findViewById(R.id.title);
        if (view != null) {
            ((TextView) view).setText(value);
        }
    }

    /**
     * 设置界面是否包含更多按钮
     *
     * @param flag
     */
    protected void setHasMoreButton(boolean flag) {
        View view = super.findViewById(R.id.more);
        if (view != null) {
            ((ImageView) view).setVisibility(flag ? View.VISIBLE : View.GONE);
        }
    }

    /**
     * 设置更多按钮风格
     *
     * @param id
     */
    protected void setMoreStyle(int id) {
        View view = super.findViewById(R.id.more);
        if (view != null) {
            ((ImageView) view).setImageResource(id);
        }
    }

    /**
     * 返回点击
     */
    public void back(View v) {
        super.onBackPressed();
    }

}
