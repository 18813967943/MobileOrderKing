package com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.classes.schemes;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Author by HEKE
 *
 * @time 2018/11/28 9:39
 * TODO: 场景编号信息对象
 */
public class SceneID implements Parcelable {

    public String id; // 编码
    public String numID; // 数值编号

    public SceneID() {
    }

    public SceneID(String id, String numID) {
        this.id = id;
        this.numID = numID;
    }

    protected SceneID(Parcel in) {
        id = in.readString();
        numID = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(numID);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SceneID> CREATOR = new Creator<SceneID>() {
        @Override
        public SceneID createFromParcel(Parcel in) {
            return new SceneID(in);
        }

        @Override
        public SceneID[] newArray(int size) {
            return new SceneID[size];
        }
    };

    public String toXml() {
        return "<SceneID id=\"" + id + "\" numID=\"" + numID + "\"/>";
    }

    @Override
    public String toString() {
        return toXml();
    }
}
