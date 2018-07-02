package com.lejia.mobile.orderking.hk3d.classes;

import android.os.Parcel;
import android.os.Parcelable;

import com.seisw.util.geom.Point2D;

import java.math.BigDecimal;

/**
 * Author by HEKE
 *
 * @time 2018/6/22 15:55
 * TODO: 点对象
 */
public class Point implements Parcelable {

    public static final int defaultDecimalPlaces = 5;

    public double x;
    public double y;

    /**
     * 常规构造函数，不进行任何数据处理
     */
    public Point() {
        super();
    }

    /**
     * 使用默认保留小数位长度的构造函数，默认小数位数为5
     */
    public Point(double x, double y) {
        this.x = percision(x, defaultDecimalPlaces);
        this.y = percision(y, defaultDecimalPlaces);
    }

    /**
     * 指定小数位长度的构造函数
     */
    public Point(double x, double y, int decimalPlaces) {
        this.x = percision(x, decimalPlaces);
        this.y = percision(y, decimalPlaces);
    }

    protected Point(Parcel in) {
        x = in.readDouble();
        y = in.readDouble();
    }

    // 转gpc点
    public Point2D toPoint2D() {
        return new Point2D(x, y);
    }

    // 复制点
    public Point copy() {
        Point copy = new Point();
        copy.x = x;
        copy.y = y;
        return copy;
    }

    // 获取两点之间的距离
    public double dist(Point target) {
        if (target == null)
            return Double.NaN;
        double poorX = Math.abs(target.x - x);
        double poorY = Math.abs(target.y - y);
        double result = Math.sqrt(poorX * poorX + poorY * poorY);
        return percision(result, 8);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(x);
        dest.writeDouble(y);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Point> CREATOR = new Creator<Point>() {
        @Override
        public Point createFromParcel(Parcel in) {
            return new Point(in);
        }

        @Override
        public Point[] newArray(int size) {
            return new Point[size];
        }
    };

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Point)) {
            return false;
        }
        Point other = (Point) obj;
        if (other.x == x && other.y == y) {
            return true;
        } else {
            double dist = dist(other);
            if (dist <= 0.1d)
                return true;
        }
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return x + "," + y;
    }

    /**
     * 调整浮点数类型精度
     *
     * @param evadeValue    浮点数值
     * @param decimalPlaces 精度(保留小数点位数)
     */
    public static double percision(double evadeValue, int decimalPlaces) {
        if (evadeValue == Double.NaN)
            return 0.0d;
        double evadeResult = 0.0d;
        try {
            // 计算保留位数后一位小数值
            double decimal = 1.0d;
            for (int i = 0; i < (decimalPlaces + 1); i++) {
                decimal *= 10.0d;
            }
            decimal = 1.0d / decimal;
            // 增加规避数值
            evadeValue += decimal;
            // 根据四舍五入保留指定decimalPlaces位数的小数位
            BigDecimal bigDecimal = new BigDecimal(evadeValue);
            BigDecimal result = bigDecimal.setScale(decimalPlaces, BigDecimal.ROUND_HALF_UP);
            evadeResult = result.doubleValue();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return evadeResult;
    }

}
