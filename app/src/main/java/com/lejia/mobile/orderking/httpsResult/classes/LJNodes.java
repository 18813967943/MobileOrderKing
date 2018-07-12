package com.lejia.mobile.orderking.httpsResult.classes;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Author by HEKE
 *
 * @time 2018/7/12 10:10
 * TODO: 数据节点
 */
public class LJNodes implements Parcelable {

    public int id; // 编号
    public String name; // 节点名称
    public int parentId; // 父节点编号
    public ArrayList<LJNodes> childrenList; // 子节点列表

    protected LJNodes(Parcel in) {
        id = in.readInt();
        name = in.readString();
        parentId = in.readInt();
        childrenList = in.createTypedArrayList(LJNodes.CREATOR);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public ArrayList<LJNodes> getChildrenList() {
        return childrenList;
    }

    public void setChildrenList(ArrayList<LJNodes> childrenList) {
        this.childrenList = childrenList;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeInt(parentId);
        dest.writeTypedList(childrenList);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<LJNodes> CREATOR = new Creator<LJNodes>() {
        @Override
        public LJNodes createFromParcel(Parcel in) {
            return new LJNodes(in);
        }

        @Override
        public LJNodes[] newArray(int size) {
            return new LJNodes[size];
        }
    };

    @Override
    public String toString() {
        return id + "|" + name + "|" + parentId + "|" + childrenList;
    }
}
