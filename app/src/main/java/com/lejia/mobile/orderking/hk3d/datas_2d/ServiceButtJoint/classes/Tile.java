package com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.classes;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Author by HEKE
 *
 * @time 2018/11/20 14:47
 * TODO: 物理瓷砖信息详细描述对象
 */
public class Tile implements Parcelable {

    public String code; // 瓷砖主序编号
    public String codeNum; // 瓷砖编码
    public float length; // 瓷砖长度
    public float width; // 瓷砖宽度
    public String url; // 瓷砖对应渲染链接

    public Tile() {
        super();
    }

    public Tile(String code, String codeNum, float length, float width, String url) {
        this.code = code;
        this.codeNum = codeNum;
        this.length = length;
        this.width = width;
        this.url = url;
    }

    protected Tile(Parcel in) {
        code = in.readString();
        codeNum = in.readString();
        length = in.readFloat();
        width = in.readFloat();
        url = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(code);
        dest.writeString(codeNum);
        dest.writeFloat(length);
        dest.writeFloat(width);
        dest.writeString(url);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Tile> CREATOR = new Creator<Tile>() {
        @Override
        public Tile createFromParcel(Parcel in) {
            return new Tile(in);
        }

        @Override
        public Tile[] newArray(int size) {
            return new Tile[size];
        }
    };

    @Override
    public String toString() {
        return code + "," + codeNum + "," + length + "," + width + "," + url;
    }
}
