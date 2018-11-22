package com.lejia.mobile.orderking.classes;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Base64;

import com.lejia.mobile.orderking.utils.TextUtils;

/**
 * Author by HEKE
 *
 * @time 2017/3/16 16:13
 * TODO: 材质或模型的详细信息
 */
public class XInfo implements Parcelable {

    public String materialCode; // 编码
    public String materialName; // 名称
    public int type; // 属于模型或材质,1为模型,0为材质
    public int catalog; // 分类
    public int X; // 长
    public int Y; // 宽
    public int Z; // 高
    public String linkUrl; // 相对路径
    public byte[] previewBuffer; // 预览图信息
    public byte[] topViewBuffer; // 顶视图信息

    public boolean needViews; // 是否需要缓冲图片

    public int offGround; // 离地高

    public String mode; // 材质类型

    public XInfo() {
        super();
    }

    public XInfo(boolean needViews) {
        super();
        this.needViews = needViews;
    }

    public XInfo(String contens) {
        String[] values = contens.split("[|]");
        materialCode = values[0];
        materialName = values[1];
        type = Integer.parseInt(values[2]);
        catalog = Integer.parseInt(values[3]);
        X = Integer.parseInt(values[4]);
        Y = Integer.parseInt(values[5]);
        Z = Integer.parseInt(values[6]);
        linkUrl = values[7];
        offGround = Integer.parseInt(values[8]);
        mode = values[9];
        if (values.length > 10) {
            if (!TextUtils.isTextEmpty(values[10])) {
                previewBuffer = Base64.decode(values[10], Base64.DEFAULT);
            }
        }
        if (values.length > 11) {
            if (!TextUtils.isTextEmpty(values[11])) {
                topViewBuffer = Base64.decode(values[11], Base64.DEFAULT);
            }
        }
    }

    public XInfo(Parcel source) {
        X = source.readInt();
        Y = source.readInt();
        Z = source.readInt();
        type = source.readInt();
        catalog = source.readInt();
        needViews = source.readInt() == 1;
        materialCode = source.readString();
        materialName = source.readString();
        linkUrl = source.readString();
        offGround = source.readInt();
        mode = source.readString();
    }

    @Override
    public String toString() {
        return materialCode + "|" + materialName + "|" + type + "|" + catalog + "|" + X + "|" + Y
                + "|" + Z + "|" + linkUrl + "|" + offGround + "|" + mode + "|" + (previewBuffer == null ? "" : Base64.encodeToString(previewBuffer, Base64.DEFAULT))
                + "|" + (topViewBuffer == null ? "" : Base64.encodeToString(topViewBuffer, Base64.DEFAULT));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(X);
        dest.writeInt(Y);
        dest.writeInt(Z);
        dest.writeInt(type);
        dest.writeInt(catalog);
        dest.writeInt(needViews ? 1 : 0);
        dest.writeString(materialCode);
        dest.writeString(materialName);
        dest.writeString(linkUrl);
        dest.writeInt(offGround);
        dest.writeString(mode);
    }

    /**
     * 复制信息
     */
    public XInfo copy() {
        XInfo copy = new XInfo();
        copy.X = X;
        copy.Y = Y;
        copy.Z = Z;
        copy.materialCode = materialCode;
        copy.type = type;
        copy.catalog = catalog;
        copy.needViews = needViews;
        copy.materialName = materialName;
        copy.linkUrl = linkUrl;
        copy.offGround = offGround;
        copy.mode = mode;
        copy.previewBuffer = previewBuffer;
        copy.topViewBuffer = topViewBuffer;
        return copy;
    }

    /**
     * 构造器
     */
    public static final Creator<XInfo> CREATOR = new Creator<XInfo>() {

        @Override
        public XInfo createFromParcel(Parcel source) {
            return new XInfo(source);
        }

        @Override
        public XInfo[] newArray(int size) {
            return new XInfo[size];
        }
    };

}
