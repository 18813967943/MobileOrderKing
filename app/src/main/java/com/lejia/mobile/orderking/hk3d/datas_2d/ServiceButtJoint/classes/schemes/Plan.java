package com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.classes.schemes;

import android.os.Parcel;
import android.os.Parcelable;

import com.lejia.mobile.orderking.hk3d.classes.Point;
import com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.classes.TilePlan;

import java.util.ArrayList;

/**
 * Author by HEKE
 *
 * @time 2018/11/28 10:51
 * TODO: 中心转铺砖计划外部包裹对象
 */
public class Plan implements Parcelable {

    public ArrayList<TilePlan> tilePlanArrayList;
    public ArrayList<ArrayList<Point>> tileplanPointsList;

    public Plan() {
        tilePlanArrayList = new ArrayList<>();
        tileplanPointsList = new ArrayList<>();
    }

    protected Plan(Parcel in) {
        tilePlanArrayList = in.createTypedArrayList(TilePlan.CREATOR);
        int size = in.readInt();
        if (size > 0) {
            tileplanPointsList = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                ArrayList<Point> pointsList = in.createTypedArrayList(Point.CREATOR);
                tileplanPointsList.add(pointsList);
            }
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(tilePlanArrayList);
        dest.writeInt((tileplanPointsList == null) ? 0 : tileplanPointsList.size());
        if (tileplanPointsList != null && tileplanPointsList.size() > 0) {
            for (ArrayList<Point> pointsList : tileplanPointsList) {
                dest.writeTypedList(pointsList);
            }
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Plan> CREATOR = new Creator<Plan>() {
        @Override
        public Plan createFromParcel(Parcel in) {
            return new Plan(in);
        }

        @Override
        public Plan[] newArray(int size) {
            return new Plan[size];
        }
    };

    public void add(TilePlan tilePlan, ArrayList<Point> pointArrayList) {
        if (tilePlan == null)
            return;
        tilePlanArrayList.add(tilePlan);
        tileplanPointsList.add((pointArrayList == null) ? new ArrayList<Point>() : pointArrayList);
    }

    public String toXml() {
        String v = "<plan>";
        if (tilePlanArrayList != null && tilePlanArrayList.size() > 0) {
            for (int i = 0; i < tilePlanArrayList.size(); i++) {
                TilePlan tilePlan = tilePlanArrayList.get(i);
                ArrayList<Point> pointArrayList = tileplanPointsList.get(i);
                v += "\n" + tilePlan.toXml(pointArrayList);
            }
        }
        v += "\n</plan>";
        return v;
    }

    @Override
    public String toString() {
        return "Plan : " + tilePlanArrayList;
    }
}
