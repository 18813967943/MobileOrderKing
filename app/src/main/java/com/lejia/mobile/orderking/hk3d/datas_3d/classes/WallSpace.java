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
import com.lejia.mobile.orderking.hk3d.datas_2d.RendererObject;
import com.lejia.mobile.orderking.hk3d.datas_3d.ShadowViewingShader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

/**
 * Author by HEKE
 *
 * @time 2018/9/26 15:45
 * TODO: 墙体面
 */
public class WallSpace extends RendererObject {

    // 内墙面
    public static final int FLAG_INNER = 0;
    // 外墙面
    public static final int FLAG_OUTER = 1;
    // 房顶面
    public static final int FLAG_ROOF = 2;
    // 墙厚面
    public static final int FLAG_PLY = 3;

    // 所属类型
    private int flag;
    // 楼层
    private int cell;
    // 层高
    private int cellHeight;
    // 围点
    private ArrayList<Point> pointsList;
    // 所属房间的hash编码
    private int belongHouseHashCode;
    // 属于房间的唯一数字编号
    private int index;

    // 是否使用默认墙体材质
    private boolean isUseDefaultTexture;

    // 材质贴图
    private Bitmap textureBitmap;
    private boolean needBindTexture;

    /**
     * 当前使用法线
     **/
    private float[] spaceNormals;

    private void initBuffers() {
        boolean isCellSpace = flag >= FLAG_ROOF;
        float cellBY = (cell - 1) * 280;
        float cellEY = cell * 280;
        lj3DPointsList = new ArrayList<>();
        PointList pointList = new PointList(pointsList);
        RectD box = null;
        if (isCellSpace) {
            box = pointList.getRectBox();
            indices = new Trianglulate().getTristrip(pointList.toArray());
            for (int i = 0; i < pointList.size(); i++) {
                Point point = pointList.getIndexAt(i);
                lj3DPointsList.add(new LJ3DPoint(point.x, cellEY, point.y));
            }
        } else {
            indices = new short[]{0, 1, 2, 0, 2, 3};
            texcoord = new float[]{0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f};
            Point begain = pointsList.get(0);
            Point end = pointsList.get(1);
            lj3DPointsList.add(new LJ3DPoint(begain.x, cellBY, begain.y));
            lj3DPointsList.add(new LJ3DPoint(end.x, cellBY, end.y));
            lj3DPointsList.add(new LJ3DPoint(end.x, cellEY, end.y));
            lj3DPointsList.add(new LJ3DPoint(begain.x, cellEY, begain.y));
        }
        int size = indices.length;
        vertexs = new float[3 * size];
        if (isCellSpace) {
            texcoord = new float[2 * size];
        }
        colors = new float[4 * size];
        normals = new float[3 * size];
        LJ3DPoint normal = LJ3DPoint.spaceNormal(lj3DPointsList.get(indices[0]), lj3DPointsList.get(indices[1])
                , lj3DPointsList.get(indices[2]));
        // 调整法线方向
        boolean minus = isCellSpace || flag == FLAG_OUTER;
        float[] normalValues = minus ? normal.toMinusFloatValues() : normal.toFloatValues();
        spaceNormals = normalValues;
        for (int i = 0; i < size; i++) {
            LJ3DPoint point = lj3DPointsList.get(indices[i]);
            float[] values = point.toFloatValues();
            int index = 3 * i;
            vertexs[index] = values[0];
            vertexs[index + 1] = values[1];
            vertexs[index + 2] = values[2];
            normals[index] = normalValues[0];
            normals[index + 1] = normalValues[1];
            normals[index + 2] = normalValues[2];
            if (isCellSpace) {
                int uvIndex = 2 * i;
                texcoord[uvIndex] = (float) (Math.abs(point.x - box.left) / box.width());
                texcoord[uvIndex + 1] = 1.0f - (float) (Math.abs(point.z - box.bottom) / box.height());
            }
            int colorIndex = 4 * i;
            colors[colorIndex] = 1.0f;
            colors[colorIndex + 1] = 0.0f;
            colors[colorIndex + 2] = 0.0f;
            colors[colorIndex + 3] = 1.0f;
        }
        vertexsBuffer = ByteBuffer.allocateDirect(4 * vertexs.length).order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexsBuffer.put(vertexs).position(0);
        normalsBuffer = ByteBuffer.allocateDirect(4 * normals.length).order(ByteOrder.nativeOrder()).asFloatBuffer();
        normalsBuffer.put(normals).position(0);
        texcoordBuffer = ByteBuffer.allocateDirect(4 * texcoord.length).order(ByteOrder.nativeOrder()).asFloatBuffer();
        texcoordBuffer.put(texcoord).position(0);
        colorsBuffer = ByteBuffer.allocateDirect(4 * colors.length).order(ByteOrder.nativeOrder()).asFloatBuffer();
        colorsBuffer.put(colors).position(0);
        initTextures();
    }

    public WallSpace(int flag, int cell, int cellHeight, ArrayList<Point> pointsList, int belongHouseHashCode, int index) {
        this.flag = flag;
        this.cell = cell;
        this.cellHeight = cellHeight;
        this.pointsList = pointsList;
        this.belongHouseHashCode = belongHouseHashCode;
        this.isUseDefaultTexture = true;
        this.index = index;
        initBuffers();
    }

    private void initTextures() {
        // 默认材质
        if (isUseDefaultTexture) {
            if (flag == FLAG_INNER || flag == FLAG_OUTER) {
                uuid = "wall_texture";
            } else if (flag == FLAG_ROOF) {
                uuid = "roof_wall_texture";
            } else {
                uuid = "flat_wall_texture";
            }
        } else {
            uuid = belongHouseHashCode + "?" + index;
        }
        needBindTexture = true; // 打开需要加载贴图操作
    }

    public int getFlag() {
        return flag;
    }

    /**
     * 法线方向取反
     *
     * @param inTheHouse
     */
    private void minusNormalDir(boolean inTheHouse) {
        float[] useNormal = null;
        if (inTheHouse) {
            useNormal = new float[]{-spaceNormals[0], -spaceNormals[1], -spaceNormals[2]};
        } else {
            useNormal = spaceNormals.clone();
        }
        for (int i = 0; i < indices.length; i++) {
            int index = 3 * i;
            normals[index] = useNormal[0];
            normals[index + 1] = useNormal[1];
            normals[index + 2] = useNormal[2];
        }
        normalsBuffer.put(normals).position(0);
    }

    @Override
    public void render(int positionAttribute, int normalAttribute, int colorAttribute, boolean onlyPosition) {
        if (needBindTexture) {
            needBindTexture = false;
            // 材质加载
            Texture texture = getTexture3DCache(uuid);
            if (texture != null) {
                textureBitmap = texture.bitmap;
                textureId = texture.textureId;
            } else {
                // 绑定纹理
                textureBitmap = createTextureWithAssets("use_textures/" + uuid + ".jpg");
                textureId = createTexture3DIdAndCache(uuid, textureBitmap, false);
            }
            refreshShadowRender();
        } else {
            // 无效返回
            if (textureId == -1)
                return;
            // 轴侧顶面不显示
            if (!RendererState.isNot25D()) {
                if (flag == FLAG_ROOF) {
                    return;
                }
            }
            // 内墙立面操作
            if (flag == FLAG_INNER || flag == FLAG_OUTER) {
                minusNormalDir(!RendererState.isNot3D());
            }
            // Pass position information to shader
            GLES30.glVertexAttribPointer(positionAttribute, 3, GLES30.GL_FLOAT, false,
                    0, vertexsBuffer);
            GLES30.glEnableVertexAttribArray(positionAttribute);
            if (!onlyPosition) {
                // uv
                GLES30.glVertexAttribPointer(ShadowViewingShader.scene_uv0, 2, GLES30.GL_FLOAT, false, 8, texcoordBuffer);
                GLES30.glEnableVertexAttribArray(ShadowViewingShader.scene_uv0);
                // Pass normal information to shader
                GLES30.glVertexAttribPointer(normalAttribute, 3, GLES30.GL_FLOAT, false,
                        0, normalsBuffer);
                GLES30.glEnableVertexAttribArray(normalAttribute);
                // Pass color information to shader
                GLES30.glVertexAttribPointer(colorAttribute, 4, GLES30.GL_FLOAT, false,
                        0, colorsBuffer);
                GLES30.glEnableVertexAttribArray(colorAttribute);
                // 贴图
                GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
                GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureId);
                GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_NEAREST);
                GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
                GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_REPEAT);
                GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_REPEAT);
                GLES30.glUniform1i(ShadowViewingShader.scene_baseMap, 0);
                GLES30.glUniform1f(ShadowViewingShader.scene_texture_flags, 1.0f);
            }
            GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, indices.length);
        }
    }

}
