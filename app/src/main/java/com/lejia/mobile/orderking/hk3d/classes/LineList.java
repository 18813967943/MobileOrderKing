package com.lejia.mobile.orderking.hk3d.classes;

import java.util.ArrayList;

/**
 * Author by HEKE
 *
 * @time 2018/7/2 15:28
 * TODO: 线段列表集合处理对象
 */
public class LineList {

    /**
     * 有效的连续性的线段组合
     */
    private ArrayList<Line> linesList;

    public LineList(ArrayList<Line> linesList) {
        this.linesList = linesList;
    }

    /**
     * 获取线段
     */
    public ArrayList<Line> getLinesList() {
        return linesList;
    }

    /**
     * 设置线段内容
     *
     * @param linesList
     */
    public void setLinesList(ArrayList<Line> linesList) {
        this.linesList = linesList;
    }

    /**
     * 判断是否无效
     */
    public boolean invalid() {
        return linesList == null || linesList.size() == 0;
    }

    /**
     * 转化为有效的点集合列表
     */
    public ArrayList<Point> toList() {
        if (invalid())
            return null;
        ArrayList<Point> list = new ArrayList<>();
        int count = 0;
        for (Line line : linesList) {
            if (count == 0) {
                list.add(line.down.copy());
                list.add(line.up.copy());
            } else {
                list.add(line.up.copy());
            }
            count++;
        }
        return list;
    }

    /**
     * 复制集合
     */
    public ArrayList<Line> copy() {
        if (invalid())
            return null;
        ArrayList<Line> copyList = new ArrayList<>();
        for (Line line : linesList) {
            copyList.add(line.copy());
        }
        return copyList;
    }

}
