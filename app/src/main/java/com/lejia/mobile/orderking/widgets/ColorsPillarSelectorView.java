package com.lejia.mobile.orderking.widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.lejia.mobile.orderking.R;
import com.lejia.mobile.orderking.hk3d.classes.Point;
import com.lejia.mobile.orderking.hk3d.classes.PointList;
import com.lejia.mobile.orderking.utils.BitmapUtils;

import java.util.ArrayList;

/**
 * Author by HEKE
 *
 * @time 2018/7/30 17:46
 * TODO: 颜色柱状图截取控件
 */
public class ColorsPillarSelectorView extends View {

    private int cpsWidth; // 宽度
    private int cpsHeight; // 高度

    // 标注区域画笔
    private Paint trianglePaint;

    /**
     * 每次截取柱状图的高度，即三角标注位置高度
     */
    private int triangleHeight;

    /**
     * 三角标注所在位置
     */
    private int atY;

    /**
     * 条状图
     */
    private Bitmap pillarBitmap;
    private int pillarWidth;

    // 回调接口
    private OnPillarColorSelectListener onPillarColorSelectListener;

    private void initAttrs() {
        try {
            trianglePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            trianglePaint.setDither(true);
            trianglePaint.setAntiAlias(true);
            trianglePaint.setColor(0xFF6D6D6D);
            triangleHeight = 20;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ColorsPillarSelectorView(Context context) {
        super(context);
        initAttrs();
    }

    public ColorsPillarSelectorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttrs();
    }

    public ColorsPillarSelectorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs();
    }

    public void setOnPillarColorSelectListener(OnPillarColorSelectListener onPillarColorSelectListener) {
        this.onPillarColorSelectListener = onPillarColorSelectListener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        cpsWidth = getDefaultSize(0, widthMeasureSpec);
        cpsHeight = getDefaultSize(0, heightMeasureSpec);
        setMeasuredDimension(cpsWidth, cpsHeight);
        pillarWidth = cpsWidth - 20;
        atY = triangleHeight / 2;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                atY = (int) event.getY();
                invalidate();
                if (onPillarColorSelectListener != null) {
                    onPillarColorSelectListener.colorSelected(getSelectorBitmap());
                }
                return true;
            case MotionEvent.ACTION_MOVE:
                float my = event.getY();
                if (Math.abs(my - atY) > 6) {
                    if (my > cpsHeight - triangleHeight / 2) {
                        my = cpsHeight - triangleHeight / 2;
                    } else if (my < triangleHeight / 2) {
                        my = triangleHeight / 2;
                    }
                    atY = (int) my;
                    invalidate();
                    if (onPillarColorSelectListener != null) {
                        onPillarColorSelectListener.colorSelected(getSelectorBitmap());
                    }
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
        try {
            // 初始化柱状位图
            if (pillarBitmap == null) {
                try {
                    Bitmap res = BitmapFactory.decodeResource(getResources(), R.mipmap.colors_selector_pillar);
                    pillarBitmap = BitmapUtils.toSize(res, pillarWidth, cpsHeight);
                    refreshMirror();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            // 两边的三角形
            if (pillarBitmap != null) {
                // 绘制柱状图
                canvas.drawBitmap(pillarBitmap, 10, 0, null);
                // 获取两端三角形围点列表
                ArrayList<Point> leftPointsList = new ArrayList<>();
                leftPointsList.add(new Point(0, atY - 6));
                leftPointsList.add(new Point(0, atY + 6));
                leftPointsList.add(new Point(8, atY));
                ArrayList<Point> rightPointsList = new ArrayList<>();
                rightPointsList.add(new Point(cpsWidth - 8, atY));
                rightPointsList.add(new Point(cpsWidth, atY - 6));
                rightPointsList.add(new Point(cpsWidth, atY + 6));
                // 绘制三角标注
                Path leftPath = new PointList(leftPointsList).getPath(true);
                Path rightPath = new PointList(rightPointsList).getPath(true);
                canvas.drawPath(leftPath, trianglePaint);
                canvas.drawPath(rightPath, trianglePaint);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取当前选中区域的位图
     */
    private Bitmap getSelectorBitmap() {
        if (pillarBitmap == null)
            return null;
        int y = atY - triangleHeight / 2;
        if (y < 0)
            y = 0;
        return Bitmap.createBitmap(pillarBitmap, 0, y, pillarWidth, triangleHeight);
    }

    /**
     * 刷新镜像内容
     */
    public void refreshMirror() {
        if (onPillarColorSelectListener != null)
            onPillarColorSelectListener.colorSelected(getSelectorBitmap());
    }

    /**
     * Author by HEKE
     *
     * @time 2018/7/31 11:00
     * TODO: 柱状图颜色回调接口
     */
    public interface OnPillarColorSelectListener {
        void colorSelected(Bitmap bitmap);
    }
}
