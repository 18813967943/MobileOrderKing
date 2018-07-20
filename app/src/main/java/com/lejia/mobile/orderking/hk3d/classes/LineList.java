package com.lejia.mobile.orderking.hk3d.classes;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Author by HEKE
 *
 * @time 2018/7/2 15:28
 * TODO: 线段列表集合处理对象
 */
public class LineList implements Parcelable {

    /**
     * 有效的连续性的线段组合
     */
    private ArrayList<Line> linesList;

    public LineList(ArrayList<Line> linesList) {
        this.linesList = linesList;
    }

    protected LineList(Parcel in) {
        int size = in.readInt();
        if (size > 0) {
            Line[] array = new Line[size];
            Parcelable[] parcelables = in.readParcelableArray(Line.class.getClassLoader());
            int index = 0;
            for (Parcelable parcelable : parcelables) {
                array[index] = (Line) parcelable;
                index++;
            }
            this.linesList = toLineList(array);
        }
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
     * 内容长度大小
     */
    public int size() {
        if (invalid())
            return 0;
        return linesList.size();
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
     * 列表转集合
     */
    public Line[] toArray() {
        if (invalid())
            return null;
        Line[] array = new Line[linesList.size()];
        int index = 0;
        for (Line line : linesList) {
            array[index] = line;
            index++;
        }
        return array;
    }

    /**
     * 集合转列表
     *
     * @param array
     * @return
     */
    public ArrayList<Line> toLineList(Line[] array) {
        if (array == null || array.length == 0)
            return null;
        ArrayList<Line> linesList = new ArrayList<>();
        for (Line line : array) {
            linesList.add(line);
        }
        return linesList;
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

    /**
     * 点吸附矫正
     *
     * @param point
     * @return
     */
    public Point correctNearlyPoint(LJ3DPoint point) {
        if (point == null || invalid())
            return null;
        Point checkPoint = point.off();
        Point ret = null;
        for (Line line : linesList) {
            ret = line.getAdsorbPoint(checkPoint.x, checkPoint.y, 50);
            if (ret != null)
                break;
        }
        return ret;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        boolean invalid = invalid();
        dest.writeInt(invalid ? 0 : linesList.size());
        if (!invalid)
            dest.writeParcelableArray(toArray(), flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<LineList> CREATOR = new Creator<LineList>() {
        @Override
        public LineList createFromParcel(Parcel in) {
            return new LineList(in);
        }

        @Override
        public LineList[] newArray(int size) {
            return new LineList[size];
        }
    };

    @Override
    public String toString() {
        return "" + linesList;
    }

}
