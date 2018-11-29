package com.lejia.mobile.orderking.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.lejia.mobile.orderking.hk3d.classes.Point;
import com.lejia.mobile.orderking.hk3d.classes.PointList;
import com.lejia.mobile.orderking.hk3d.gpc.GPCArea;

import java.util.ArrayList;

import geom.gpc.GPCConfig;

/**
 * Author by HEKE
 *
 * @time 2018/7/28 11:05
 * TODO: 铺砖起铺方向选择控件
 */
public class TileDirectionSelectorView extends View {

    private int tdsWidth;
    private int tdsHeight;

    private int size;
    private int margin;

    /**
     * 起铺点列表
     */
    private ArrayList<Point> directionPointList;

    /**
     * 每个起铺点的大小区域围点列表
     */
    private ArrayList<GPCArea> directionSizeList;

    // 画笔
    private Paint paint;

    // 选中位置(即选中房间的铺砖方向)
    private int direction = GPCConfig.FROM_LEFT_TOP;

    // 按下点
    private Point down;

    // 回调接口
    private OnTileDirectionsSelectedListener onTileDirectionsSelectedListener;

    private void defaultInit() {
        directionPointList = new ArrayList<>();
        directionSizeList = new ArrayList<>();
        size = 20;
        margin = 16;
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setDither(true);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
    }

    public TileDirectionSelectorView(Context context) {
        super(context);
        defaultInit();
    }

    public TileDirectionSelectorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        defaultInit();
    }

    public TileDirectionSelectorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        defaultInit();
    }

    public void setOnTileDirectionsSelectedListener(OnTileDirectionsSelectedListener onTileDirectionsSelectedListener) {
        this.onTileDirectionsSelectedListener = onTileDirectionsSelectedListener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        tdsWidth = getDefaultSize(0, widthMeasureSpec);
        tdsHeight = getDefaultSize(0, heightMeasureSpec);
        setMeasuredDimension(tdsWidth, tdsHeight);
        // 运算围点
        directionPointList.clear();
        directionSizeList.clear();
        directionPointList.add(new Point(margin + size / 2, margin + size / 2)); // 点顺序不能变
        directionPointList.add(new Point(tdsWidth / 2, margin + size / 2));
        directionPointList.add(new Point(tdsWidth - margin - size / 2, margin + size / 2));
        directionPointList.add(new Point(tdsWidth - margin - size / 2, tdsHeight / 2));
        directionPointList.add(new Point(tdsWidth - margin - size / 2, tdsHeight - margin - size / 2));
        directionPointList.add(new Point(tdsWidth / 2, tdsHeight - margin - size / 2));
        directionPointList.add(new Point(margin + size / 2, tdsHeight - margin - size / 2));
        directionPointList.add(new Point(margin + size / 2, tdsHeight / 2));
        directionPointList.add(new Point(tdsWidth / 2, tdsHeight / 2));
        int areaWidth = (tdsWidth - 2 * margin) / 3;
        int areaHeight = (tdsHeight - 2 * margin) / 3;
        int index = 0;
        for (Point point : directionPointList) {
            ArrayList<Point> pointsList = PointList.getRotateVertexs(0, areaWidth, areaHeight, point);
            GPCArea gpcArea = new GPCArea();
            gpcArea.pointsList = pointsList;
            gpcArea.position = index;
            switch (index) {
                case 0:
                    gpcArea.direction = GPCConfig.FROM_RIGHT_TOP;
                    break;
                case 1:
                    gpcArea.direction = GPCConfig.FROM_MIDDLE_TOP;
                    break;
                case 2:
                    gpcArea.direction = GPCConfig.FROM_LEFT_TOP;
                    break;
                case 3:
                    gpcArea.direction = GPCConfig.FROM_MIDDLE_LEFT;
                    break;
                case 4:
                    gpcArea.direction = GPCConfig.FROM_LEFT_BOTTOM;
                    break;
                case 5:
                    gpcArea.direction = GPCConfig.FROM_MIDDLE_BOTTOM;
                    break;
                case 6:
                    gpcArea.direction = GPCConfig.FROM_RIGHT_BOTTOM;
                    break;
                case 7:
                    gpcArea.direction = GPCConfig.FROM_MIDDLE_RIGHT;
                    break;
                case 8:
                    gpcArea.direction = GPCConfig.FROM_MIDDLE;
                    break;
            }
            directionSizeList.add(gpcArea);
            index++;
        }
    }

    /**
     * 设置球体大小
     *
     * @param size
     */
    public void setSize(int size) {
        this.size = size;
        invalidate();
    }

    /**
     * 设置外间距
     */
    public void setMargin(int margin) {
        this.margin = margin;
        invalidate();
    }

    /**
     * 设置选中位置
     */
    public void setDirection(int direction) {
        for (GPCArea gpcArea : directionSizeList) {
            if (gpcArea.direction == direction) {
                this.direction = gpcArea.position;
                break;
            }
        }
        invalidate();
    }

    public int getDirection() {
        return direction;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (directionPointList.size() > 0) {
            int index = 0;
            for (Point point : directionPointList) {
                if (index == direction) {
                    paint.setColor(0xFF15D4E0);
                } else {
                    paint.setColor(0xFFDDDDDD);
                }
                canvas.drawCircle((float) point.x, (float) point.y, size, paint);
                index++;
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (down == null)
                    down = new Point(event.getX(), event.getY());
                else {
                    down.x = event.getX();
                    down.y = event.getY();
                }
                int index = 0;
                for (GPCArea gpcArea : directionSizeList) {
                    if (PointList.pointRelationToPolygon(gpcArea.pointsList, down) != -1) {
                        setDirection(gpcArea.direction);
                        if (onTileDirectionsSelectedListener != null) {
                            onTileDirectionsSelectedListener.selected(gpcArea.direction);
                        }
                        break;
                    }
                    index++;
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * Author by HEKE
     *
     * @time 2018/7/28 17:11
     * TODO: 铺砖起铺方向控件选中回调接口
     */
    public interface OnTileDirectionsSelectedListener {
        void selected(int direction);
    }

}
