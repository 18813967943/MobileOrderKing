package com.lejia.mobile.orderking.widgets;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.lejia.mobile.orderking.hk3d.classes.Point;

/**
 * Author by HEKE
 *
 * @time 2018/7/30 19:00
 * TODO: 手指选中位置取色图片控件
 */
@SuppressLint("AppCompatCustomView")
public class ColorSelectorImageView extends ImageView {

    private Paint selectPaint;
    private int radius;

    private Point touchAt;

    private Bitmap selector;

    private void initAttrs() {
        selectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        selectPaint.setDither(true);
        selectPaint.setAntiAlias(true);
        selectPaint.setStrokeWidth(3f);
        selectPaint.setStyle(Paint.Style.STROKE);
        selectPaint.setColor(0xFFFFFFFF);
        radius = 8;
    }

    public ColorSelectorImageView(Context context) {
        super(context);
        initAttrs();
    }

    public ColorSelectorImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttrs();
    }

    public ColorSelectorImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (touchAt == null)
                    touchAt = new Point(event.getX(), event.getY());
                else {
                    touchAt.x = event.getX();
                    touchAt.y = event.getY();
                }
                invalidate();
                return true;
            case MotionEvent.ACTION_MOVE:
                float mx = event.getX();
                float my = event.getY();
                double dist = touchAt.dist(mx, my);
                if (dist >= 16) {
                    touchAt.setXY(mx, my);
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (touchAt != null) {
            canvas.drawCircle((float) touchAt.x, (float) touchAt.y, radius, selectPaint);
        }
    }

    /**
     * 获取选中颜色
     */
    public int getSelectColor() {
        if (touchAt == null)
            touchAt = new Point(10, 10);
        setDrawingCacheEnabled(true);
        buildDrawingCache();
        Bitmap bitmap = getDrawingCache().copy(Bitmap.Config.ARGB_8888, true);
        setDrawingCacheEnabled(false);
        int color = bitmap.getPixel((int) touchAt.x, (int) touchAt.y);
        bitmap.recycle();
        return color;
    }

    // 获取颜色柱状图回调接口
    public ColorsPillarSelectorView.OnPillarColorSelectListener getOnPillarColorSelectListener() {
        return onPillarColorSelectListener;
    }

    /**
     * 颜色选中区域回调接口
     */
    private ColorsPillarSelectorView.OnPillarColorSelectListener onPillarColorSelectListener = new ColorsPillarSelectorView.OnPillarColorSelectListener() {
        @Override
        public void colorSelected(Bitmap bitmap) {
            if (selector != null) {
                selector.recycle();
            }
            selector = bitmap;
            setBackground(new BitmapDrawable(getResources(), selector));
        }
    };
}
