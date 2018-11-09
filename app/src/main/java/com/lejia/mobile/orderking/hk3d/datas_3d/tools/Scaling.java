package com.lejia.mobile.orderking.hk3d.datas_3d.tools;

import android.opengl.Matrix;

import com.lejia.mobile.orderking.hk3d.classes.Point;

import java.util.ArrayList;

/**
 * Author by HEKE
 *
 * @time 2018/11/6 9:48
 * TODO: 缩放比例对象
 */
public class Scaling {

    /**
     * 当前所有数据缩放比例
     */
    public static float scale = 0.01f;

    /**
     * 按照当前比例缩放围点列表
     *
     * @param pointArrayList
     * @return
     */
    public static ArrayList<Point> scalePointList(ArrayList<Point> pointArrayList) {
        if (pointArrayList == null || pointArrayList.size() == 0)
            return null;
        float[] scaleMatrix = new float[16];
        Matrix.setIdentityM(scaleMatrix, 0);
        Matrix.scaleM(scaleMatrix, 0, scale, scale, 1.0f);
        ArrayList<Point> scaledPointsList = new ArrayList<>();
        for (Point point : pointArrayList) {
            float[] pm = new float[]{(float) point.x, (float) point.y, 0.0f, 1.0f};
            float[] rm = new float[4];
            Matrix.multiplyMV(rm, 0, scaleMatrix, 0, pm, 0);
            scaledPointsList.add(new Point(rm[0], rm[1]));
        }
        return scaledPointsList;
    }

    /**
     * 缩放常态数值
     *
     * @param value
     * @return
     */
    public static float scaleSimpleValue(float value) {
        return value * scale;
    }

}

