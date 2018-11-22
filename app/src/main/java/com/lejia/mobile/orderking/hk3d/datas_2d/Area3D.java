package com.lejia.mobile.orderking.hk3d.datas_2d;

import com.lejia.mobile.orderking.hk3d.classes.L3DMatrix;
import com.lejia.mobile.orderking.hk3d.classes.LJ3DPoint;
import com.lejia.mobile.orderking.hk3d.classes.Line;
import com.lejia.mobile.orderking.hk3d.classes.Point;
import com.lejia.mobile.orderking.hk3d.classes.PointList;
import com.lejia.mobile.orderking.hk3d.classes.RectD;
import com.lejia.mobile.orderking.hk3d.classes.Trianglulate;

import java.util.ArrayList;

/**
 * Author by HEKE
 *
 * @time 2018/7/21 11:57
 * TODO: 地砖细化面
 */
public class Area3D extends RendererObject {

    private boolean isGap; // 是否是砖缝
    private String materialCode; // 瓷砖编码
    private ArrayList<Point> pointsList; // 自身切割后的围点(实际显示的)
    private ArrayList<Point> originList; // 自身完整区域的围点

    private int horizontalAngle; // 水平旋转角度
    private int verticalAngle; // 垂直旋转角度

    private float waveangle; // 角度值

    /**
     * 铺砖类型，默认为普通铺砖
     */
    private int styleType = 1;

    /**
     * 是否斜铺
     */
    private boolean isSkewTile;

    private void initDatas() {
        PointList pointList = new PointList(pointsList);
        PointList mOriginList = new PointList(originList);
        if (pointList.invalid() || invalid())
            return;
        pointList.setPointsList(pointList.antiClockwise());
        pointsList = pointList.getPointsList();
        lj3DPointsList = pointList.to3dList();
        RectD box = mOriginList.getRectBox();
        indices = new Trianglulate().getTristrip(pointList.toArray());
        if (indices == null) {
            return;
        }
        vertexs = new float[3 * indices.length];
        texcoord = new float[2 * indices.length];
        for (int i = 0; i < indices.length; i++) {
            LJ3DPoint point = lj3DPointsList.get(indices[i]);
            // 顶点
            int index = 3 * i;
            vertexs[index] = (float) point.x;
            vertexs[index + 1] = (float) point.y;
            vertexs[index + 2] = (float) point.z;
            // 随机uv方向
            int random = (int) (Math.random() * 10 + 1);
            int randomD = (int) (Math.random() * 20 + 1);
            boolean evenNumber = random % 2 == 0;
            boolean reverser = randomD % 2 == 1;
            // 纹理，偶数正常、奇数u颠倒，反转false为正常,true为v颠倒
            int uvIndex = 2 * i;
            if (evenNumber) {
                texcoord[uvIndex] = (float) (Math.abs(point.x - box.left) / box.width());
                horizontalAngle = 0;
            } else {
                texcoord[uvIndex] = 1.0f - (float) (Math.abs(point.x - box.left) / box.width());
                horizontalAngle = 180;
            }
            if (reverser) {
                texcoord[uvIndex + 1] = (float) (Math.abs(point.y - box.bottom) / box.height());
                verticalAngle = 0;
            } else {
                texcoord[uvIndex + 1] = 1.0f - (float) (Math.abs(box.bottom - point.y) / box.height());
                verticalAngle = 180;
            }
        }
    }

    public Area3D(boolean isGap, String materialCode, ArrayList<Point> pointsList, ArrayList<Point> originList) {
        this.isGap = isGap;
        this.materialCode = materialCode;
        this.pointsList = pointsList;
        this.originList = originList;
        initDatas();
    }

    public int getHorizontalAngle() {
        return horizontalAngle;
    }

    public int getVerticalAngle() {
        return verticalAngle;
    }

    public String getMaterialCode() {
        return materialCode;
    }

    public boolean isGap() {
        return isGap;
    }

    public int getStyleType() {
        return styleType;
    }

    public void setStyleType(int styleType) {
        this.styleType = styleType;
    }

    public ArrayList<Point> getPointsList() {
        return pointsList;
    }

    public boolean isSkewTile() {
        return isSkewTile;
    }

    public void setSkewTile(boolean skewTile) {
        isSkewTile = skewTile;
    }

    public boolean invalid() {
        return pointsList != null && pointsList.size() < 3;
    }

    // 波打线角度
    public float getWaveangle() {
        return waveangle;
    }

    /**
     * 瓷砖角度，用于波打线设置
     *
     * @param pointList 所在布置房间的围点列表对象
     */
    public void setWaveAngle(PointList pointList) {
        if (pointsList == null || pointsList.size() == 0 || pointList == null)
            return;
        try {
            // 获取自身区域最长的线段
            ArrayList<Line> linesList = new PointList(pointsList).toLineList();
            Line maxLengthLine = linesList.get(0);
            for (Line line : linesList) {
                if (line.getLength() >= maxLengthLine.getLength()) {
                    maxLengthLine = line;
                }
            }
            RectD box = pointList.getRectBox();
            double boxMaxLength = box.width() >= box.height() ? box.width() : box.height();
            // 取最长线段的中点，判断与铺设房间围点形成的线段哪条距离最近，则为吸附墙体，角度为墙体角度
            Point point = maxLengthLine.getCenter();
            ArrayList<Line> houseLinesList = pointList.toLineList();
            double minDist = Float.MAX_VALUE;
            Line adsorbLine = null;
            for (Line line : houseLinesList) {
                ArrayList<Point> lepsList = PointList.getRotateLEPS(line.getAngle() + 90.0d, 5 * boxMaxLength, point);
                Line lepsLine = new Line(lepsList.get(1), lepsList.get(0));
                Point interPoint = lepsLine.getLineIntersectedPoint(line);
                if (interPoint != null) { // 交点为空时，可判定为不接近的墙体直接忽略
                    // 求出最小距离选项
                    double dist = point.dist(interPoint);
                    if (dist <= minDist) {
                        minDist = dist;
                        adsorbLine = line;
                    }
                }
            }
            // 赋值角度
            if (adsorbLine != null) {
                waveangle = (float) adsorbLine.getAngle();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 平移区域围点
     *
     * @param transX
     * @param transY
     */
    public ArrayList<Point> translatePointsList(double transX, double transY) {
        if (originList == null || originList.size() == 0)
            return null;
        return L3DMatrix.translate(originList, transX, transY, 0);
    }

    @Override
    public void render(int positionAttribute, int normalAttribute, int colorAttribute, boolean onlyPosition) {
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Area3D))
            return false;
        Area3D other = (Area3D) obj;
        return new PointList(originList).equals(new PointList(other.originList));
    }

}
