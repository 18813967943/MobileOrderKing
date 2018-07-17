package com.lejia.mobile.orderking.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.AbsListView;
import android.widget.GridView;

import com.lejia.mobile.orderking.hk3d.classes.Point;

/**
 * Author by HEKE
 *
 * @time 2018/7/12 19:17
 * TODO: 带滚动处理的格子控件
 */
public class ScrollerGridView extends GridView {

    // 最小手指移动处理距离
    private static final int MIN_SCROLL_LENGTH = 50;

    /**
     * 已经下拉到底部标志
     */
    private boolean goToLastBottom;

    // 按下点
    private Point tDown;
    // 弹起点
    private Point tUp;

    // 回调监听接口
    private OnScrollerGridListener onScrollerGridListener;

    private void initAttrs() {
        setOnScrollListener(onScrollListener);
    }

    public ScrollerGridView(Context context) {
        super(context);
        initAttrs();
    }

    public ScrollerGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs();
    }

    public ScrollerGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs();
    }

    public void setOnScrollerGridListener(OnScrollerGridListener onScrollerGridListener) {
        this.onScrollerGridListener = onScrollerGridListener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                float dx = event.getX();
                float dy = event.getY();
                if (tDown == null) {
                    tDown = new Point(dx, dy);
                } else {
                    tDown.x = dx;
                    tDown.y = dy;
                }
                break;
            case MotionEvent.ACTION_UP:
                float ux = event.getX();
                float uy = event.getY();
                if (tUp == null) {
                    tUp = new Point(ux, uy);
                } else {
                    tUp.x = ux;
                    tUp.y = uy;
                }
                double distance = tUp.dist(tDown);
                // 只允许上滑操作
                if (tUp.y < tDown.y && distance >= MIN_SCROLL_LENGTH) {
                    if (goToLastBottom) {
                        goToLastBottom = false;
                        if (onScrollerGridListener != null)
                            onScrollerGridListener.toLastPage();
                    }
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * 滚动监听
     */
    private OnScrollListener onScrollListener = new OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            goToLastBottom = (firstVisibleItem + visibleItemCount == totalItemCount);
        }
    };

    /**
     * Author by HEKE
     *
     * @time 2018/7/12 19:26
     * TODO: 回调滚动事件监听
     */
    public interface OnScrollerGridListener {
        void toLastPage();
    }

}
