package com.lejia.mobile.orderking.hk3d.datas;

import com.lejia.mobile.orderking.hk3d.classes.LJ3DPoint;
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

    private boolean isWallTile; // 是否墙砖
    private String materialCode; // 瓷砖编码
    private ArrayList<Point> pointsList; // 围点

    private int horizontalAngle; // 水平旋转角度
    private int verticalAngle; // 垂直旋转角度

    private void initDatas() {
        PointList pointList = new PointList(pointsList);
        if (pointList.invalid())
            return;
        pointList.setPointsList(pointList.antiClockwise());
        pointsList = pointList.getPointsList();
        lj3DPointsList = pointList.to3dList();
        RectD box = pointList.getRectBox();
        indices = new Trianglulate().getTristrip(pointList.toArray());
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

    public Area3D(boolean isWallTile, int textureId, String materialCode, ArrayList<Point> pointsList) {
        this.isWallTile = isWallTile;
        this.textureId = textureId;
        this.materialCode = materialCode;
        this.pointsList = pointsList;
        initDatas();
    }

    public boolean isWallTile() {
        return isWallTile;
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

    @Override
    public void render(int positionAttribute, int normalAttribute, int colorAttribute, boolean onlyPosition) {
    }

}
