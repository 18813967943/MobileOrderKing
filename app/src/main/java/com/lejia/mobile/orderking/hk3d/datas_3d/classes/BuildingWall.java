package com.lejia.mobile.orderking.hk3d.datas_3d.classes;

import android.graphics.Bitmap;
import android.opengl.GLES30;

import com.lejia.mobile.orderking.hk3d.RendererState;
import com.lejia.mobile.orderking.hk3d.classes.LJ3DPoint;
import com.lejia.mobile.orderking.hk3d.classes.Point;
import com.lejia.mobile.orderking.hk3d.classes.PointList;
import com.lejia.mobile.orderking.hk3d.classes.RectD;
import com.lejia.mobile.orderking.hk3d.classes.Texture;
import com.lejia.mobile.orderking.hk3d.classes.Trianglulate;
import com.lejia.mobile.orderking.hk3d.datas_2d.TexturesCache;
import com.lejia.mobile.orderking.hk3d.datas_3d.common.RenderConstants;
import com.lejia.mobile.orderking.hk3d.datas_3d.tools.Cell;
import com.lejia.mobile.orderking.hk3d.datas_3d.tools.Scaling;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

/**
 * Author by HEKE
 *
 * @time 2018/11/7 9:47
 * TODO: 墙体数据对象
 */
public class BuildingWall extends Render3DObject {

    public Cell cell; // 楼层信息
    public int type; // 墙体类型
    public ArrayList<Point> originPointsList; // 对应平面围点列表
    public ArrayList<Point> scalingPointsList; // 对应比例缩放围点列表
    public boolean invalid; // 无效的
    public Bitmap textureBitmap; // 贴图材质

    public BuildingWall(Cell cell, int type, ArrayList<Point> originPointsList) {
        this.cell = cell;
        this.type = type;
        this.originPointsList = originPointsList;
        initDatas();
    }

    private void initDatas() {
        invalid = (originPointsList == null || originPointsList.size() == 0);
        if (invalid)
            return;
        PointList originPointList = new PointList(originPointsList);
        originPointList.setPointsList(originPointList.antiClockwise());
        scalingPointsList = Scaling.scalePointList(originPointList.getPointsList());
        PointList pointList = new PointList(scalingPointsList);
        float cellBegainY = (int) Scaling.scaleSimpleValue(cell.cellBegainHeight);
        float cellEndY = (int) Scaling.scaleSimpleValue(cellBegainY + cell.cellHeight);
        lj3DPointsList = new ArrayList<>();
        Point b = pointList.get(0);
        Point e = pointList.get(1);
        // 索引、UV、顶点
        switch (type) {
            case Type.TOPSIDE:
                indices = new short[]{0, 1, 2, 0, 2, 3};
                texcoord = new float[]{0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f};
                for (int i = 0; i < pointList.size(); i++) {
                    Point point = pointList.getIndexAt(i);
                    lj3DPointsList.add(new LJ3DPoint(point.x, cellEndY, point.y));
                }
                textureName = "flat_wall_texture";
                break;
            case Type.INSIDE:
                indices = new short[]{0, 1, 2, 0, 2, 3};
                texcoord = new float[]{0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f};
                lj3DPointsList.add(new LJ3DPoint(b.x, cellBegainY, b.y));
                lj3DPointsList.add(new LJ3DPoint(e.x, cellBegainY, e.y));
                lj3DPointsList.add(new LJ3DPoint(e.x, cellEndY, e.y));
                lj3DPointsList.add(new LJ3DPoint(b.x, cellEndY, b.y));
                textureName = "wall_texture";
                break;
            case Type.OUTSIDE:
                indices = new short[]{3, 2, 0, 2, 1, 0};
                texcoord = new float[]{0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f};
                lj3DPointsList.add(new LJ3DPoint(b.x, cellBegainY, b.y));
                lj3DPointsList.add(new LJ3DPoint(e.x, cellBegainY, e.y));
                lj3DPointsList.add(new LJ3DPoint(e.x, cellEndY, e.y));
                lj3DPointsList.add(new LJ3DPoint(b.x, cellEndY, b.y));
                textureName = "wall_texture";
                break;
            case Type.ROOF:
                RectD box = pointList.getRectBox();
                indices = new Trianglulate().getTristrip(originPointList.toArray());
                for (int i = 0; i < pointList.size(); i++) {
                    Point point = pointList.getIndexAt(i);
                    lj3DPointsList.add(new LJ3DPoint(point.x, cellEndY, point.y));
                }
                texcoord = new float[2 * indices.length];
                for (int i = 0; i < indices.length; i++) {
                    LJ3DPoint point = lj3DPointsList.get(indices[i]);
                    int uvIndex = 2 * i;
                    texcoord[uvIndex] = (float) (Math.abs(point.x - box.left) / box.width());
                    texcoord[uvIndex + 1] = 1.0f - (float) (Math.abs(point.z - box.bottom) / box.height());
                }
                textureName = "roof_wall_texture";
                break;
        }
        // 顶点数组、法线
        LJ3DPoint normal = LJ3DPoint.spaceNormal(lj3DPointsList.get(indices[0]), lj3DPointsList.get(indices[1])
                , lj3DPointsList.get(indices[2]));
        if (type != Type.TOPSIDE) {
            normal = normal.reverser();
        }
        vertexs = new float[3 * indices.length];
        normals = new float[3 * indices.length];
        for (int i = 0; i < indices.length; i++) {
            LJ3DPoint point = lj3DPointsList.get(indices[i]);
            int index = 3 * i;
            vertexs[index] = (float) point.x;
            vertexs[index + 1] = (float) point.y;
            vertexs[index + 2] = (float) point.z;
            normals[index] = (float) normal.x;
            normals[index + 1] = (float) normal.y;
            normals[index + 2] = (float) normal.z;
        }
        // 字节缓存
        vertexsBuffer = ByteBuffer.allocateDirect(RenderConstants.FLOAT_SIZE_IN_BYTES * vertexs.length).order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexsBuffer.put(vertexs).position(0);
        texcoordBuffer = ByteBuffer.allocateDirect(RenderConstants.FLOAT_SIZE_IN_BYTES * texcoord.length).order(ByteOrder.nativeOrder()).asFloatBuffer();
        texcoordBuffer.put(texcoord).position(0);
        normalsBuffer = ByteBuffer.allocateDirect(RenderConstants.FLOAT_SIZE_IN_BYTES * normals.length).order(ByteOrder.nativeOrder()).asFloatBuffer();
        normalsBuffer.put(normals).position(0);
    }

    /**
     * 加载材质
     */
    private void loadDefaultTexture() {
        Texture texture = TexturesCache.get(textureName);
        if (texture != null) {
            textureId = texture.textureId;
            textureBitmap = texture.bitmap;
        } else {
            String path = "use_textures/" + textureName + ".jpg";
            textureBitmap = createTextureWithAssets(path);
            textureId = createTextureIdAndCache(textureName, textureBitmap, false);
            refreshRender();
        }
    }

    @Override
    public void render(int positionAttribute, int normalAttribute, int colorAttribute, boolean onlyPosition) {
        // Pass position information to shader
        vertexsBuffer.position(0);
        GLES30.glVertexAttribPointer(positionAttribute, 3, GLES30.GL_FLOAT, false,
                0, vertexsBuffer);
        GLES30.glEnableVertexAttribArray(positionAttribute);
        if (!onlyPosition) {
            // Pass normal information to shader
            normalsBuffer.position(0);
            GLES30.glVertexAttribPointer(normalAttribute, 3, GLES30.GL_FLOAT, false,
                    0, normalsBuffer);
            GLES30.glEnableVertexAttribArray(normalAttribute);
            if (textureId != -1) {
                // texcoord
                texcoordBuffer.position(0);
                GLES30.glVertexAttribPointer(mRenderer.scene_texcoordAttribute, 2, GLES30.GL_FLOAT, false,
                        0, texcoordBuffer);
                GLES30.glEnableVertexAttribArray(mRenderer.scene_texcoordAttribute);
                // map
                GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
                GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureId);
                GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_NEAREST);
                GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
                GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_REPEAT);
                GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_REPEAT);
                GLES30.glUniform1i(mRenderer.scene_SbaseMapUniform, 0);
                GLES30.glUniform1f(mRenderer.scene_useSkinTexcoord_flag, 1.0f);
                if (RendererState.isNot3D()) {
                    GLES30.glUniform1f(mRenderer.scene_uSpecular, 0.5f);
                } else {
                    GLES30.glUniform1f(mRenderer.scene_uSpecular, 0.65f);
                }
            } else {
                // 加载材质
                loadDefaultTexture();
            }
        }
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, indices.length);
    }

    // 判断是否无效墙体
    public boolean isInvalid() {
        return invalid;
    }

    /**
     * Author by HEKE
     *
     * @time 2018/11/7 9:50
     * TODO: 内部类型对象
     */
    public class Type {

        /**
         * 内墙体
         */
        public static final int INSIDE = 0x00;

        /**
         * 外墙体
         */
        public static final int OUTSIDE = 0x01;

        /**
         * 顶边墙体(厚度面)
         */
        public static final int TOPSIDE = 0x02;

        /**
         * 内部顶面墙体(房顶)
         */
        public static final int ROOF = 0x03;

    }

}
