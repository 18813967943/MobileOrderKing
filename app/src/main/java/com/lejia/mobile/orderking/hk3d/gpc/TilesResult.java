package com.lejia.mobile.orderking.hk3d.gpc;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.lejia.mobile.orderking.hk3d.classes.Point;
import com.lejia.mobile.orderking.hk3d.classes.PointList;
import com.lejia.mobile.orderking.hk3d.classes.RectD;
import com.lejia.mobile.orderking.hk3d.datas.Area3D;
import com.lejia.mobile.orderking.utils.BitmapUtils;

import java.util.ArrayList;

/**
 * Author by HEKE
 *
 * @time 2018/7/21 17:10
 * TODO: 铺砖结果存储对象
 */
public class TilesResult {

    /**
     * 对应的盒子区域
     */
    private RectD box;

    /**
     * 对应的切割管理对象
     */
    private GPCManager gpcManager;

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

    private void initCanvas() {
        if (box == null)
            return;
        holeBitmap = Bitmap.createBitmap((int) box.width(), (int) box.height(), Bitmap.Config.ARGB_8888);
        canvas = new Canvas(holeBitmap);
        canvas.drawColor(gpcManager.getGapsColor());
        transX = -box.left;
        transY = -box.top;
    }

    public TilesResult(RectD rectD, GPCManager gpcManager) {
        this.box = rectD;
        this.gpcManager = gpcManager;
        this.area3DSList = new ArrayList<>();
        initCanvas();
    }

    /**
     * 刷新背景色(砖缝颜色，渲染时刻使用整张图像进行渲染)
     */
    public void refreshGapsColor() {
        canvas.drawColor(gpcManager.getGapsColor());

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
            Bitmap bitmap = gpcManager.getGround().getTileBitmap(area3D.getMaterialCode()).copy(Bitmap.Config.ARGB_8888, true);
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
            bitmap.recycle();
        }
        area3DSList.add(area3D);
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
