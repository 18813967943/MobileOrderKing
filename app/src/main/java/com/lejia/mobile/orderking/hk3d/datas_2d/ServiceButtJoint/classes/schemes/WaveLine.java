package com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.classes.schemes;

import android.os.Parcel;
import android.os.Parcelable;

import com.lejia.mobile.orderking.hk3d.classes.Point;
import com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.classes.TilePlan;

import java.util.ArrayList;

/**
 * Author by HEKE
 *
 * @time 2018/11/28 11:17
 * TODO: 波打线最外层数据对象
 */
public class WaveLine implements Parcelable {

    public int planType;
    public int openDir;
    public int parentGuid;
    public ArrayList<TilePlan> tilePlanArrayList;
    public ArrayList<ArrayList<Point>> tilePlanPointArrayList;

    public WaveLine() {
        this.tilePlanArrayList = new ArrayList<>();
        this.tilePlanPointArrayList = new ArrayList<>();
    }

    protected WaveLine(Parcel in) {
        planType = in.readInt();
        openDir = in.readInt();
        parentGuid = in.readInt();
        tilePlanArrayList = in.createTypedArrayList(TilePlan.CREATOR);
        int size = in.readInt();
        if (size > 0) {
            tilePlanPointArrayList = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                ArrayList<Point> pointsList = in.createTypedArrayList(Point.CREATOR);
                tilePlanPointArrayList.add(pointsList);
            }
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(planType);
        dest.writeInt(openDir);
        dest.writeInt(parentGuid);
        dest.writeTypedList(tilePlanArrayList);
        int size = tilePlanPointArrayList.size();
        dest.writeInt(size);
        if (size > 0) {
            for (ArrayList<Point> pointsList : tilePlanPointArrayList) {
                dest.writeTypedList(pointsList);
            }
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<WaveLine> CREATOR = new Creator<WaveLine>() {
        @Override
        public WaveLine createFromParcel(Parcel in) {
            return new WaveLine(in);
        }

        @Override
        public WaveLine[] newArray(int size) {
            return new WaveLine[size];
        }
    };

    public void add(TilePlan tilePlan, ArrayList<Point> pointsList) {
        if (tilePlan == null)
            return;
        tilePlanArrayList.add(tilePlan);
        tilePlanPointArrayList.add((pointsList == null) ? new ArrayList<Point>() : pointsList);
    }

    public String toXml() {
        String v = "<waveLine planType=\"" + planType + "\" openDir=\"" + openDir + "\" parentGuid=\"" + parentGuid + "\">";
        if (tilePlanArrayList != null && tilePlanArrayList.size() > 0) {
            for (int i = 0; i < tilePlanArrayList.size(); i++) {
                TilePlan tilePlan = tilePlanArrayList.get(i);
                ArrayList<Point> pointsList = tilePlanPointArrayList.get(i);
                v += "\n" + tilePlan.toXml(pointsList);
            }
        }
        v += "\n</waveLine>";
        return v;
    }

    @Override
    public String toString() {
        return planType + "," + openDir + "," + parentGuid + "," + tilePlanArrayList + "," + tilePlanPointArrayList;
    }
}
