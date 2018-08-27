package com.lejia.mobile.orderking.hk3d.datas;

import android.opengl.Matrix;
import android.os.Parcel;
import android.os.Parcelable;

import com.lejia.mobile.orderking.hk3d.ViewingMatrixs;
import com.lejia.mobile.orderking.hk3d.classes.Point;

/**
 * Author by HEKE
 *
 * @time 2018/8/21 9:37
 * TODO: 家具矩阵等数据对象
 */
public class FurnitureMatrixs implements Parcelable {

    /**
     * 用于渲染的矩阵
     */
    public float[] mmvMatrixs = new float[16];

    /**
     * 吸附位置
     */
    public Point point;

    /**
     * 旋转
     */
    public float rotateX = 0.0f;
    public float rotateY = 0.0f;
    public float rotateZ = 0.0f;

    /**
     * 缩放
     */
    public float scaleX = 1.0f;
    public float scaleY = 1.0f;
    public float scaleZ = 1.0f;

    /**
     * 平移
     */
    public float transX = 0.0f;
    public float transY = 0.0f;
    public float transZ = 0.0f;

    public FurnitureMatrixs() {
        super();
    }

    public FurnitureMatrixs(Point point, float rotateX, float rotateY, float rotateZ, float scaleX,
                            float scaleY, float scaleZ, float transX, float transY, float transZ) {
        this.point = point;
        this.rotateX = rotateX;
        this.rotateY = rotateY;
        this.rotateZ = rotateZ;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.scaleZ = scaleZ;
        this.transX = transX;
        this.transY = transY;
        this.transZ = transZ;
        initMatrix();
    }

    protected FurnitureMatrixs(Parcel in) {
        mmvMatrixs = in.createFloatArray();
        point = in.readParcelable(Point.class.getClassLoader());
        rotateX = in.readFloat();
        rotateY = in.readFloat();
        rotateZ = in.readFloat();
        scaleX = in.readFloat();
        scaleY = in.readFloat();
        scaleZ = in.readFloat();
        transX = in.readFloat();
        transY = in.readFloat();
        transZ = in.readFloat();
    }

    /**
     * 初始化矩阵数据
     */
    public void initMatrix() {
        // 设置自身矩阵
        Matrix.setIdentityM(mmvMatrixs, 0);
        Matrix.translateM(mmvMatrixs, 0, transX, transY, transZ);
        Matrix.scaleM(mmvMatrixs, 0, scaleX, scaleY, scaleZ);
        if (rotateX != 0)
            Matrix.rotateM(mmvMatrixs, 0, rotateX, 1.0f, 0.0f, 0.0f);
        if (rotateY != 0)
            Matrix.rotateM(mmvMatrixs, 0, rotateY, 0.0f, 1.0f, 0.0f);
        if (rotateZ != 0)
            Matrix.rotateM(mmvMatrixs, 0, rotateZ, 0.0f, 0.0f, 1.0f);
        // 复制当前矩阵
        float[] rendererModelView = ViewingMatrixs.mModelMatrix.clone();
        // 组合矩阵
        Matrix.multiplyMM(mmvMatrixs, 0, mmvMatrixs, 0, rendererModelView, 0);
    }

    /**
     * 获取模型所用矩阵
     */
    public float[] getMmvMatrixs() {
        return mmvMatrixs;
    }

    /**
     * 获取模型所在三维位置点
     */
    public Point getPoint() {
        return point;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloatArray(mmvMatrixs);
        dest.writeParcelable(point, flags);
        dest.writeFloat(rotateX);
        dest.writeFloat(rotateY);
        dest.writeFloat(rotateZ);
        dest.writeFloat(scaleX);
        dest.writeFloat(scaleY);
        dest.writeFloat(scaleZ);
        dest.writeFloat(transX);
        dest.writeFloat(transY);
        dest.writeFloat(transZ);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<FurnitureMatrixs> CREATOR = new Creator<FurnitureMatrixs>() {
        @Override
        public FurnitureMatrixs createFromParcel(Parcel in) {
            return new FurnitureMatrixs(in);
        }

        @Override
        public FurnitureMatrixs[] newArray(int size) {
            return new FurnitureMatrixs[size];
        }
    };

    @Override
    public String toString() {
        return point + "," + rotateX + "," + rotateY + "," + rotateZ
                + "," + transX + "," + transY + "," + transZ + "," +
                scaleX + "," + scaleY + "," + scaleZ;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || (!(obj instanceof FurnitureMatrixs)))
            return false;
        FurnitureMatrixs furnitureMatrixs = (FurnitureMatrixs) obj;
        return furnitureMatrixs.toString().equals(toString());
    }

}
