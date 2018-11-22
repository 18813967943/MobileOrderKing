package com.lejia.mobile.orderking.hk3d.gpc;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

import com.lejia.mobile.orderking.hk3d.classes.Point;
import com.lejia.mobile.orderking.hk3d.classes.PointList;
import com.lejia.mobile.orderking.hk3d.classes.RectD;
import com.lejia.mobile.orderking.hk3d.datas_2d.Area3D;
import com.lejia.mobile.orderking.utils.BitmapUtils;

import java.util.ArrayList;

/**
 * Author by HEKE
 *
 * @time 2018/7/21 17:10
 * TODO: 铺砖结果存储对象
 */
@Deprecated
public class TilesResult {

    /**
     * 对应的盒子区域
     */
    private RectD box;

    /**
     * 对应的切割管理对象
     */
    private NSGPCManager gpcManager;
    private float gap;

    /**
     * 所有切割结果区域列表
     */
    private ArrayList<Area3D> area3DSList;

    /**
     * 拼接的完整保存位图
     */
    private Bitmap holeBitmap;
    private Canvas canvas;

    // 偏移数据
    private double transX;
    private double transY;

    // 缝隙颜色画笔
    private Paint gapPaint;

    private void initCanvas() {
        if (box == null)
            return;
        holeBitmap = Bitmap.createBitmap((int) box.width(), (int) box.height(), Bitmap.Config.ARGB_8888);
        canvas = new Canvas(holeBitmap);
        holeBitmap.setHasAlpha(true);
        transX = -box.left;
        transY = -box.top;
        gapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    public TilesResult(RectD rectD, NSGPCManager gpcManager) {
        this.box = rectD;
        this.gpcManager = gpcManager;
        this.area3DSList = new ArrayList<>();
        initCanvas();
    }

    /**
     * 存入单个区域
     *
     * @param area3D
     */
    public void putArea3D(Area3D area3D) {
        if (area3D == null)
            return;
        // 实砖
        if (!area3D.isGap()) {
            saveArea3DBitmap(area3D);
        }
        // 砖缝
        else {
            saveGap(area3D);
        }
        area3DSList.add(area3D);
    }

    /**
     * 绘制砖缝至总画布
     */
    private void saveGap(Area3D area3D) {
        if (area3D == null)
            return;
        int color = gpcManager.getGapsColor();
        if (color == 0) { // 黑色砖缝
            color = 0xFF000000;
        } else if (color == -1) { // 白色砖缝
            color = 0xFFFFFFFF;
        }
        gapPaint.setColor(color);
        gapPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        gapPaint.setStrokeWidth(1f);
        this.gap = this.gpcManager.getBrickGap();
        ArrayList<Point> transList = area3D.translatePointsList(transX, transY);
        Path path = new PointList(transList).getPath(true);
        canvas.drawPath(path, gapPaint);
    }

    /**
     * 保存单个区域的铺砖材质至总画布
     *
     * @param area3D
     */
    @Deprecated
    private void saveArea3DBitmap(Area3D area3D) {
        /*Bitmap codeBitmap = gpcManager.getGround().getTileBitmap(area3D.getMaterialCode(), area3D.getStyleType());
        if (codeBitmap == null || codeBitmap.isRecycled())
            return;
        Bitmap bitmap = codeBitmap.copy(Bitmap.Config.ARGB_8888, true);
        if (area3D.isSkewTile()) {
            bitmap = BitmapUtils.rotateWithCenter(bitmap, -45, area3D.getPointsList());
        }
        if (bitmap == null || bitmap.isRecycled())
            return;
        // 根据区域纹理旋转角度，对资源进行翻转
        if (area3D.getHorizontalAngle() == 180) {
            bitmap = BitmapUtils.mirror(bitmap, 0);
        }
        if (area3D.getVerticalAngle() == 180) {
            bitmap = BitmapUtils.mirror(bitmap, 1);
        }
        ArrayList<Point> transList = area3D.translatePointsList(transX, transY);
        PointList pointList = new PointList(transList);
        RectD box = pointList.getRectBox();
        canvas.drawBitmap(bitmap, (float) box.left, (float) box.top, null);
        bitmap.recycle();*/
    }

    /**
     * 存储切割区域列表
     *
     * @param area3DSList
     */
    public void puArea3DList(ArrayList<Area3D> area3DSList) {
        if (area3DSList == null || area3DSList.size() == 0)
            return;
        for (Area3D area3D : area3DSList) {
            putArea3D(area3D);
        }
    }

    /**
     * 清除数据
     */
    public void clearDatas() {
        area3DSList.clear();
        holeBitmap.recycle();
        holeBitmap = Bitmap.createBitmap((int) box.width(), (int) box.height(), Bitmap.Config.ARGB_8888);
        canvas = new Canvas(holeBitmap);
        holeBitmap.setHasAlpha(true);
    }

    /**
     * 获取整张贴图
     */
    public Bitmap getHoleBitmap() {
        return holeBitmap;
    }

    /**
     * 释放数据
     */
    public void release() {
        if (area3DSList != null)
            area3DSList.clear();
        if (holeBitmap != null) {
            holeBitmap.recycle();
            holeBitmap = null;
            canvas = null;
        }
        System.gc();
    }

}
