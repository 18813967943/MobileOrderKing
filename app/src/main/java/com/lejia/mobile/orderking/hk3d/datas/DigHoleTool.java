package com.lejia.mobile.orderking.hk3d.datas;

import android.annotation.SuppressLint;
import android.os.AsyncTask;

import com.lejia.mobile.orderking.hk3d.classes.LJ3DPoint;
import com.lejia.mobile.orderking.hk3d.classes.Line;
import com.lejia.mobile.orderking.hk3d.classes.Point;
import com.lejia.mobile.orderking.hk3d.classes.PointList;

import java.util.ArrayList;

/**
 * Author by HEKE
 *
 * @time 2018/9/5 14:51
 * TODO: 开洞工具类
 */
public class DigHoleTool {

    private int cell;
    private double cellHeight;
    private Line line;
    private double height;
    private double offground;
    private float[] normal; // 法线
    private ArrayList<Point> facadeList;
    private ArrayList<PunchFragmentFacade> punchFragmentFacadeArrayList;

    private OnDigHoleListener onDigHoleListener;


    /**
     * 构造函数
     *
     * @param cell       第几层楼
     * @param cellHeight 层高
     * @param line       开洞线段
     * @param height     开洞高度
     * @param offground  开洞的离地高
     * @param facadeList 被开洞的立面
     */
    public DigHoleTool(int cell, double cellHeight, Line line, double height, double offground, float[] normal,
                       ArrayList<Point> facadeList, ArrayList<PunchFragmentFacade> punchFragmentFacadeArrayList,
                       OnDigHoleListener onDigHoleListener) {
        this.cell = cell;
        this.cellHeight = cellHeight;
        this.line = line;
        this.height = height;
        this.offground = offground;
        this.normal = normal;
        this.facadeList = facadeList;
        this.punchFragmentFacadeArrayList = punchFragmentFacadeArrayList;
        this.onDigHoleListener = onDigHoleListener;
        doPunch();
    }

    /**
     * 执行
     */
    @SuppressLint("StaticFieldLeak")
    private void doPunch() {
        new AsyncTask<String, Integer, ArrayList<PunchFragmentFacade>>() {
            @Override
            protected ArrayList<PunchFragmentFacade> doInBackground(String... strings) {
                ArrayList<PunchFragmentFacade> ret = new ArrayList<>();
                try {
                    // 将面的点与切割面的点进行排序
                    ArrayList<Point> pointsList = new ArrayList<>();
                    Line facadeLine = new Line(facadeList.get(0).copy(), facadeList.get(1).copy());
                    Point facadeBegain = facadeList.get(0);
                    pointsList.add(facadeBegain.copy());
                    Point lineBegain = line.down;
                    Point lineEnd = line.up;
                    if (facadeBegain.dist(lineBegain) < facadeBegain.dist(lineEnd)) {
                        pointsList.add(lineBegain.copy());
                        pointsList.add(lineEnd.copy());
                    } else {
                        pointsList.add(lineEnd.copy());
                        pointsList.add(lineBegain.copy());
                    }
                    pointsList.add(facadeList.get(1).copy());
                    // 基础信息
                    Point center = line.getCenter();
                    double digBegainZ = (float) offground;
                    double digEnd = (float) (digBegainZ + height);
                    // 相对起点开始的围点列表
                    ArrayList<Point> releativeList = new ArrayList<>();
                    releativeList.add(new Point(0, cellHeight));
                    releativeList.add(new Point(facadeLine.getLength(), cellHeight));
                    releativeList.add(new Point(facadeLine.getLength(), 0));
                    releativeList.add(new Point(0, 0));
                    // 不存在切割面
                    int size = punchFragmentFacadeArrayList == null ? 0 : punchFragmentFacadeArrayList.size();
                    if (size == 0) {
                        ArrayList<Line> punchLinesList = new PointList(pointsList).toNotClosedLineList();
                        for (int i = 0; i < punchLinesList.size(); i++) {
                            Line punchLine = punchLinesList.get(i);
                            // 开洞区域
                            if (center.equals(punchLine.getCenter())) {
                                ArrayList<Point> selfReleativeList = new ArrayList<>();
                                ArrayList<LJ3DPoint> lj3DPointArrayList = new ArrayList<>();
                                // 接地
                                if (offground == 0) {
                                    double begainDist = facadeBegain.dist(punchLine.down);
                                    double endDist = facadeBegain.dist(punchLine.up);
                                    selfReleativeList.add(new Point(begainDist, cellHeight - height));
                                    selfReleativeList.add(new Point(endDist, cellHeight - height));
                                    selfReleativeList.add(new Point(endDist, 0));
                                    selfReleativeList.add(new Point(begainDist, 0));
                                    lj3DPointArrayList.add(new LJ3DPoint(punchLine.down.x, punchLine.down.y, (cell - 1) * cellHeight + height));
                                    lj3DPointArrayList.add(new LJ3DPoint(punchLine.up.x, punchLine.up.y, (cell - 1) * cellHeight + height));
                                    lj3DPointArrayList.add(new LJ3DPoint(punchLine.up.x, punchLine.up.y, cell * cellHeight));
                                    lj3DPointArrayList.add(new LJ3DPoint(punchLine.down.x, punchLine.down.y, cell * cellHeight));
                                    ret.add(new PunchFragmentFacade(punchLine, selfReleativeList, releativeList,
                                            lj3DPointArrayList, cell, (int) cellHeight, normal));
                                }
                                // 非接地
                                else {
                                    double begainDist = facadeBegain.dist(punchLine.down);
                                    double endDist = facadeBegain.dist(punchLine.up);
                                    ArrayList<Point> selfOffgroundReleativeList = new ArrayList<>();
                                    ArrayList<LJ3DPoint> selfOffgroundList = new ArrayList<>();
                                    selfOffgroundReleativeList.add(new Point(begainDist, cellHeight));
                                    selfOffgroundReleativeList.add(new Point(endDist, cellHeight));
                                    selfOffgroundReleativeList.add(new Point(endDist, cellHeight - offground));
                                    selfOffgroundReleativeList.add(new Point(begainDist, cellHeight - offground));
                                    double sogbZ = (cell - 1) * cellHeight;
                                    selfOffgroundList.add(new LJ3DPoint(punchLine.down.x, punchLine.down.y, sogbZ));
                                    selfOffgroundList.add(new LJ3DPoint(punchLine.up.x, punchLine.up.y, sogbZ));
                                    selfOffgroundList.add(new LJ3DPoint(punchLine.up.x, punchLine.up.y, sogbZ + offground));
                                    selfOffgroundList.add(new LJ3DPoint(punchLine.down.x, punchLine.down.y, sogbZ + offground));
                                    if (offground + height < cellHeight) {
                                        double poor = cellHeight - offground - height;
                                        selfReleativeList.add(new Point(begainDist, poor));
                                        selfReleativeList.add(new Point(endDist, poor));
                                        selfReleativeList.add(new Point(endDist, 0));
                                        selfReleativeList.add(new Point(begainDist, 0));
                                        double selfZ = sogbZ + offground + height;
                                        lj3DPointArrayList.add(new LJ3DPoint(punchLine.down.x, punchLine.down.y, selfZ));
                                        lj3DPointArrayList.add(new LJ3DPoint(punchLine.up.x, punchLine.up.y, selfZ));
                                        lj3DPointArrayList.add(new LJ3DPoint(punchLine.up.x, punchLine.up.y, cell * cellHeight));
                                        lj3DPointArrayList.add(new LJ3DPoint(punchLine.down.x, punchLine.down.y, cell * cellHeight));
                                        // 顶部
                                        ret.add(new PunchFragmentFacade(punchLine, selfReleativeList, releativeList, lj3DPointArrayList,
                                                cell, (int) cellHeight, normal));
                                    }
                                    // 底部
                                    ret.add(new PunchFragmentFacade(punchLine, selfOffgroundReleativeList, releativeList, selfOffgroundList,
                                            cell, (int) cellHeight, normal));
                                }
                            }
                            // 无需开洞区域
                            else {
                                add(facadeBegain, punchLine, releativeList, ret);
                            }
                        }
                    }
                    // 已有切割面
                    else {

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (ret.size() == 0)
                    ret = null;
                return ret;
            }

            @Override
            protected void onPostExecute(ArrayList<PunchFragmentFacade> punchFragmentFacadeArrayList) {
                super.onPostExecute(punchFragmentFacadeArrayList);
                if (onDigHoleListener != null)
                    onDigHoleListener.digged(punchFragmentFacadeArrayList);
            }
        }.execute();
    }

    /**
     * 新增切割的普通面
     *
     * @param facadeBegain                 三维立面起始点
     * @param punchLine                    切割出的面对应线段
     * @param releativeList                三维立面映射平面上的围点列表
     * @param punchFragmentFacadeArrayList 创建结果存储列表
     */
    private void add(Point facadeBegain, Line punchLine, ArrayList<Point> releativeList, ArrayList<PunchFragmentFacade> punchFragmentFacadeArrayList) {
        // 切割后的相对区域、三维对应围点
        ArrayList<Point> selfReleativeList = new ArrayList<>();
        double begainDist = facadeBegain.dist(punchLine.down);
        double endDist = facadeBegain.dist(punchLine.up);
        selfReleativeList.add(new Point(begainDist, cellHeight));
        selfReleativeList.add(new Point(endDist, cellHeight));
        selfReleativeList.add(new Point(endDist, 0));
        selfReleativeList.add(new Point(begainDist, 0));
        ArrayList<LJ3DPoint> lj3DPointArrayList = new ArrayList<>();
        lj3DPointArrayList.add(new LJ3DPoint(punchLine.down.x, punchLine.down.y, (cell - 1) * cellHeight));
        lj3DPointArrayList.add(new LJ3DPoint(punchLine.up.x, punchLine.up.y, (cell - 1) * cellHeight));
        lj3DPointArrayList.add(new LJ3DPoint(punchLine.up.x, punchLine.up.y, cell * cellHeight));
        lj3DPointArrayList.add(new LJ3DPoint(punchLine.down.x, punchLine.down.y, cell * cellHeight));
        // 新建并添加切割面
        punchFragmentFacadeArrayList.add(new PunchFragmentFacade(punchLine, selfReleativeList, releativeList, lj3DPointArrayList,
                cell, (int) cellHeight, normal));
    }

    /**
     * Author by HEKE
     *
     * @time 2018/9/5 14:58
     * TODO: 切割回调数据接口
     */
    public interface OnDigHoleListener {
        void digged(ArrayList<PunchFragmentFacade> punchFragmentFacadeArrayList);
    }

}
