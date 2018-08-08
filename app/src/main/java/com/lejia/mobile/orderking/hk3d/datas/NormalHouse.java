package com.lejia.mobile.orderking.hk3d.datas;

import android.content.Context;

import com.lejia.mobile.orderking.hk3d.classes.AuxiliaryLine;
import com.lejia.mobile.orderking.hk3d.classes.Line;
import com.lejia.mobile.orderking.hk3d.classes.Point;
import com.lejia.mobile.orderking.hk3d.classes.PointList;

import java.util.ArrayList;

/**
 * Author by HEKE
 *
 * @time 2018/7/18 16:11
 * TODO: 普通房间
 */
public class NormalHouse extends House {

    private Point down; // 当前按下点
    private Point up; // 当前实时移动点，或弹起点
    private Line currentLine; // 当前绘制的线段

    public NormalHouse(Context context) {
        super(context);
    }

    public NormalHouse(Context context, PointList center_pointList, int thickness) {
        super(context, center_pointList, thickness);
    }

    public void setDown(Point down) {
        this.down = down;
    }

    public void setUp(Point up) {
        this.up = up;
        // 两点最小距离
        double dist = this.up.dist(down.x, down.y);
        if (dist < 50) {
            return;
        }
        // 检测倾斜矫正
        double poorX = Math.abs(this.up.x - down.x);
        double poorY = Math.abs(this.up.y - down.y);
        if (poorX < 100) {
            this.up.x = down.x;
        }
        if (poorY < 100) {
            this.up.y = down.y;
        }
        currentLine = new Line(down, this.up);
        currentLine.loadAuxiliaryArray();
        ArrayList<AuxiliaryLine> auxiliaryLinesList = currentLine.getAuxiliaryLineList();
        if (auxiliaryLinesList != null && auxiliaryLinesList.size() > 0) {
            centerPointList = new PointList(currentLine.toPointList());
            innerPointList = new PointList(auxiliaryLinesList.get(0).getPointList());
            outerPointList = new PointList(auxiliaryLinesList.get(1).getPointList());
            createRenderer();
            refreshRenderer();
        }
    }

    /**
     * TODO 检测线段画墙体与其他墙体房间的关系
     */
    public void checkUp() {

    }

}
