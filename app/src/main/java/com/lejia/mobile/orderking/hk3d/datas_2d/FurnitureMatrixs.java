package com.lejia.mobile.orderking.hk3d.datas_2d;

import android.opengl.GLES30;
import android.opengl.Matrix;
import android.os.Parcel;
import android.os.Parcelable;

import com.lejia.mobile.orderking.hk3d.LightMatrixs;
import com.lejia.mobile.orderking.hk3d.ViewingMatrixs;
import com.lejia.mobile.orderking.hk3d.ViewingShader;
import com.lejia.mobile.orderking.hk3d.classes.LJ3DPoint;
import com.lejia.mobile.orderking.hk3d.classes.Point;
import com.lejia.mobile.orderking.hk3d.classes.PointList;

import java.util.ArrayList;

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
    private float[] myModelMatrixs = new float[16]; // TODO 模型自身的缩放、旋转、平移矩阵,用于同模型数据不同位置渲染，减少同模型内存占用
    private float[] mViewMatrix = new float[16]; // 视图矩阵
    private float[] mMVMatrix = new float[16]; // 视图及模型矩阵
    private float[] mNormalMatrix = new float[16]; // 转至法线矩阵
    private float[] mProjectionMatrix = new float[16]; // 投影矩阵
    private float[] mMVPMatrix = new float[16]; // 总矩阵

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

    /**
     * 镜像
     */
    public boolean mirror;

    /**
     * 选中对象
     */
    public ArrayList<Selector> selectorArrayList;

    public FurnitureMatrixs() {
        super();
    }

    public FurnitureMatrixs(Point point, float rotateX, float rotateY, float rotateZ, float scaleX,
                            float scaleY, float scaleZ, float transX, float transY, float transZ, boolean mirror) {
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
        this.mirror = mirror;
        this.selectorArrayList = new ArrayList<>();
    }

    protected FurnitureMatrixs(Parcel in) {
        myModelMatrixs = in.createFloatArray();
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
        mirror = in.readInt() == 1;
    }

    public void setMirror(boolean mirror) {
        this.mirror = mirror;
    }

    /**
     * 初始化矩阵数据
     *
     * @param shadow          是否阴影渲染
     * @param renderTextureId 渲染阴影材质编号
     */
    public void initMatrix(boolean shadow, int renderTextureId) {
        // 设置自身矩阵，优先平移、旋转、缩放，非动画操作
        myModelMatrixs = ViewingMatrixs.mModelMatrix.clone();
        Matrix.translateM(myModelMatrixs, 0, transX, transY, transZ);
        Matrix.rotateM(myModelMatrixs, 0, rotateX, 1.0f, 0.0f, 0.0f);
        Matrix.rotateM(myModelMatrixs, 0, rotateY, 0.0f, 1.0f, 0.0f);
        Matrix.rotateM(myModelMatrixs, 0, rotateZ, 0.0f, 0.0f, 1.0f);
        Matrix.scaleM(myModelMatrixs, 0, mirror ? -1.0f * scaleX : scaleX, scaleY, scaleZ);
        // 阴影渲染参数
        if (shadow) {
            float[] tempResultMatrix = new float[16];
            // Calculate matrices for standing objects
            // View matrix * Model matrix value is stored
            Matrix.multiplyMM(LightMatrixs.mLightMvpMatrix_staticShapes, 0, LightMatrixs.mLightViewMatrix
                    , 0, myModelMatrixs, 0);

            // Model * view * projection matrix stored and copied for use at rendering from camera point of view
            Matrix.multiplyMM(tempResultMatrix, 0, LightMatrixs.mLightProjectionMatrix, 0,
                    LightMatrixs.mLightMvpMatrix_staticShapes, 0);
            System.arraycopy(tempResultMatrix, 0, LightMatrixs.mLightMvpMatrix_staticShapes, 0, 16);
            // Pass in the combined matrix.
            GLES30.glUniformMatrix4fv(ViewingShader.shadow_mvpMatrixUniform, 1, false, LightMatrixs.mLightMvpMatrix_staticShapes, 0);
        }
        // 常态渲染参数
        else {
            // 矩阵换算
            float[] tempResultMatrix = new float[16];
            float bias[] = new float[]{
                    0.5f, 0.0f, 0.0f, 0.0f,
                    0.0f, 0.5f, 0.0f, 0.0f,
                    0.0f, 0.0f, 0.5f, 0.0f,
                    0.5f, 0.5f, 0.5f, 1.0f};

            float[] depthBiasMVP = new float[16];
            //calculate MV matrix
            mViewMatrix = ViewingMatrixs.mViewMatrix.clone();
            Matrix.multiplyMM(tempResultMatrix, 0, mViewMatrix, 0, myModelMatrixs, 0);
            System.arraycopy(tempResultMatrix, 0, mMVMatrix, 0, 16);

            //pass in MV Matrix as uniform
            GLES30.glUniformMatrix4fv(ViewingShader.scene_mvMatrixUniform, 1, false, mMVMatrix, 0);

            //calculate Normal Matrix as uniform (invert transpose MV)
            Matrix.invertM(tempResultMatrix, 0, mMVMatrix, 0);
            Matrix.transposeM(mNormalMatrix, 0, tempResultMatrix, 0);

            //pass in Normal Matrix as uniform
            GLES30.glUniformMatrix4fv(ViewingShader.scene_normalMatrixUniform, 1, false, mNormalMatrix, 0);

            //calculate MVP matrix
            mProjectionMatrix = ViewingMatrixs.mProjectionMatrix.clone();
            Matrix.multiplyMM(tempResultMatrix, 0, mProjectionMatrix, 0, mMVMatrix, 0);
            System.arraycopy(tempResultMatrix, 0, mMVPMatrix, 0, 16);
            //pass in MVP Matrix as uniform
            GLES30.glUniformMatrix4fv(ViewingShader.scene_mvpMatrixUniform, 1, false, mMVPMatrix, 0);

            Matrix.multiplyMV(LightMatrixs.mLightPosInEyeSpace, 0, mViewMatrix,
                    0, LightMatrixs.mActualLightPosition, 0);

            //pass in light source position
            GLES30.glUniform3f(ViewingShader.scene_lightPosUniform, LightMatrixs.mLightPosInEyeSpace[0],
                    LightMatrixs.mLightPosInEyeSpace[1], LightMatrixs.mLightPosInEyeSpace[2]);
            Matrix.multiplyMM(depthBiasMVP, 0, bias, 0, LightMatrixs.mLightMvpMatrix_staticShapes, 0);
            System.arraycopy(depthBiasMVP, 0, LightMatrixs.mLightMvpMatrix_staticShapes, 0, 16);

            //MVP matrix that was used during depth map render
            GLES30.glUniformMatrix4fv(ViewingShader.scene_schadowProjMatrixUniform, 1, false,
                    LightMatrixs.mLightMvpMatrix_staticShapes, 0);
            //pass in texture where depth map is stored
            GLES30.glActiveTexture(GLES30.GL_TEXTURE1);
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, renderTextureId);
            GLES30.glUniform1i(ViewingShader.scene_textureUniform, 1);
        }
    }

    /**
     * 刷新模型吸附信息
     *
     * @param angle
     * @param point
     */
    public void refreshModel(double angle, Point point) {
        if (point == null)
            return;
        this.point = point;
        this.transX = (float) this.point.x;
        this.transY = (float) this.point.y;
        this.rotateZ = (float) angle;
    }

    /**
     * 获取模型所在三维位置点
     */
    public Point getPoint() {
        return point;
    }

    /**
     * 获取模型选中对象数据列表
     */
    public ArrayList<Selector> getSelectorArrayList() {
        return selectorArrayList;
    }

    /**
     * 初始化创建选中对象
     *
     * @param xlong     长度
     * @param width     宽度
     * @param height    高度
     * @param offground 离地高
     */
    public void initSelector(int xlong, int width, int height, int offground) {
        if (point == null)
            return;
        if (selectorArrayList.size() > 0) {
            for (Selector selector : selectorArrayList) {
                selector.releaseDatas();
            }
        }
        selectorArrayList.clear();
        // 创建三维模型盒子围点
        ArrayList<Point> pointsList = PointList.getRotateVertexs(rotateZ, width / 10, xlong / 10, point.copy());
        int begainZ = offground / 10;
        int endZ = begainZ + height / 10;
        for (int i = 0; i < pointsList.size(); i++) {
            Point now = pointsList.get(i);
            Point next = null;
            if (i == pointsList.size() - 1) {
                next = pointsList.get(0);
            } else {
                next = pointsList.get(i + 1);
            }
            ArrayList<LJ3DPoint> lj3DPointArrayList = new ArrayList<>();
            lj3DPointArrayList.add(new LJ3DPoint(now.x, now.y, begainZ));
            lj3DPointArrayList.add(new LJ3DPoint(next.x, next.y, begainZ));
            lj3DPointArrayList.add(new LJ3DPoint(next.x, next.y, endZ));
            lj3DPointArrayList.add(new LJ3DPoint(now.x, now.y, endZ));
            lj3DPointArrayList.add(new LJ3DPoint(now.x, now.y, begainZ));
            selectorArrayList.add(new Selector(lj3DPointArrayList));
        }
        // 增加一个顶面
        ArrayList<LJ3DPoint> topLj3DPointArrayList = new ArrayList<>();
        for (int i = 0; i < pointsList.size(); i++) {
            Point point = pointsList.get(i);
            topLj3DPointArrayList.add(new LJ3DPoint(point.x, point.y, endZ));
        }
        Point end = pointsList.get(pointsList.size() - 1);
        topLj3DPointArrayList.add(new LJ3DPoint(end.x, end.y, endZ));
        selectorArrayList.add(new Selector(topLj3DPointArrayList));
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloatArray(myModelMatrixs);
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
        dest.writeInt(mirror ? 1 : 0);
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
                scaleX + "," + scaleY + "," + scaleZ + "," + mirror;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || (!(obj instanceof FurnitureMatrixs)))
            return false;
        FurnitureMatrixs furnitureMatrixs = (FurnitureMatrixs) obj;
        return furnitureMatrixs.toString().equals(toString());
    }

}
