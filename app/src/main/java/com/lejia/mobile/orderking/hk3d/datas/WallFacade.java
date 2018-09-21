package com.lejia.mobile.orderking.hk3d.datas;

import android.graphics.Bitmap;
import android.opengl.GLES30;

import com.lejia.mobile.orderking.hk3d.RendererState;
import com.lejia.mobile.orderking.hk3d.ViewingShader;
import com.lejia.mobile.orderking.hk3d.classes.LJ3DPoint;
import com.lejia.mobile.orderking.hk3d.classes.Line;
import com.lejia.mobile.orderking.hk3d.classes.Point;
import com.lejia.mobile.orderking.hk3d.classes.PointList;
import com.lejia.mobile.orderking.hk3d.classes.Texture;
import com.lejia.mobile.orderking.hk3d.classes.Trianglulate;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

/**
 * Author by HEKE
 *
 * @time 2018/8/31 9:11
 * TODO: 墙体立面
 */
public class WallFacade extends RendererObject {

    /**
     * 内墙体
     */
    public static final int FLAG_INTER = 0;

    /**
     * 外墙体
     */
    public static final int FLAG_OUTER = 1;

    /**
     * 顶墙体
     */
    public static final int FLAG_TOP_THICKNESS = 2;

    /**
     * 墙体类型
     */
    private int flag;

    /**
     * 房间所属楼层
     */
    private int cell;

    // 法线点
    private LJ3DPoint normal;

    /**
     * 墙体原始围点
     */
    private ArrayList<Point> pointsList;

    /**
     * 核心点
     */
    private Point identifyPoint;

    /**
     * 是否是默认贴图材质
     */
    private boolean isDefaultWallTexture = true;

    /**
     * 所属房间
     */
    private House house;

    private Bitmap textureBitmap; // 贴图
    private boolean needBindTexture;

    /**
     * 切割面
     */
    private boolean havaPunched;
    private ArrayList<PunchFragmentFacade> punchFragmentFacadeArrayList;

    private void initDatas() {
        if (flag == FLAG_INTER || flag == FLAG_OUTER) {
            Line line = new Line(pointsList.get(0), pointsList.get(1));
            identifyPoint = line.getCenter();
        } else if (flag == FLAG_TOP_THICKNESS) {
            identifyPoint = new PointList(pointsList).getInnerValidPoint(false);
        }
        initBuffers();
    }

    public WallFacade(int flag, int cell, ArrayList<Point> pointsList, House house) {
        this.flag = flag;
        this.cell = cell;
        this.pointsList = pointsList;
        this.house = house;
        this.punchFragmentFacadeArrayList = new ArrayList<>();
        initDatas();
    }

    public WallFacade(int flag, int cell, ArrayList<Point> pointsList, String uuid) {
        this.flag = flag;
        this.cell = cell;
        this.pointsList = pointsList;
        this.house = null;
        this.uuid = uuid;
        this.punchFragmentFacadeArrayList = new ArrayList<>();
        initDatas();
    }

    private void initBuffers() {
        float cellBY = (cell - 1) * 280;
        float cellEY = cell * 280;
        lj3DPointsList = new ArrayList<>();
        if (flag == FLAG_INTER || flag == FLAG_OUTER) {
            Point begain = pointsList.get(0);
            Point end = pointsList.get(1);
            lj3DPointsList.add(begain.toLJ3DPoint(cellBY));
            lj3DPointsList.add(end.toLJ3DPoint(cellBY));
            lj3DPointsList.add(end.toLJ3DPoint(cellEY));
            lj3DPointsList.add(begain.toLJ3DPoint(cellEY));
            indices = new short[]{0, 1, 2, 0, 2, 3};
        } else if (flag == FLAG_TOP_THICKNESS) {
            for (Point point : pointsList) {
                lj3DPointsList.add(point.toLJ3DPoint(cellEY));
            }
            indices = new Trianglulate().getTristrip(new PointList(pointsList).toArray());
        }
        int size = indices.length;
        vertexs = new float[3 * size];
        texcoord = new float[]{0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f};
        normals = new float[3 * size];
        normal = LJ3DPoint.spaceNormal(lj3DPointsList.get(indices[0]), lj3DPointsList.get(indices[1])
                , lj3DPointsList.get(indices[2]));
        for (int i = 0; i < size; i++) {
            LJ3DPoint point = lj3DPointsList.get(indices[i]);
            int index = 3 * i;
            vertexs[index] = (float) point.x;
            vertexs[index + 1] = (float) point.y;
            vertexs[index + 2] = (float) point.z;
            normals[index] = (float) normal.x;
            normals[index + 1] = (float) normal.y;
            normals[index + 2] = (float) normal.z;
        }
        vertexsBuffer = ByteBuffer.allocateDirect(4 * vertexs.length).order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexsBuffer.put(vertexs).position(0);
        normalsBuffer = ByteBuffer.allocateDirect(4 * normals.length).order(ByteOrder.nativeOrder()).asFloatBuffer();
        normalsBuffer.put(normals).position(0);
        texcoordBuffer = ByteBuffer.allocateDirect(4 * texcoord.length).order(ByteOrder.nativeOrder()).asFloatBuffer();
        texcoordBuffer.put(texcoord).position(0);
        if (isDefaultWallTexture) {
            //uuid = "wall_texture";
            uuid = "milk_wall";
            //uuid = "cyan_wall";
        } else {
            if (house != null)
                uuid = house.hashCode() + "," + identifyPoint.toString();
            else
                uuid = uuid + "," + identifyPoint.toString();
        }
        Texture texture = getTextureCache(uuid);
        if (texture != null) {
            textureBitmap = texture.bitmap;
            textureId = texture.textureId;
        } else {
            textureBitmap = createTextureWithAssets("textures/" + uuid + ".jpg");
            needBindTexture = true;
        }
    }

    /**
     * 替换材质贴图
     */
    public void setTextureBitmap(Bitmap textureBitmap) {
        this.textureBitmap = textureBitmap;
        this.isDefaultWallTexture = false;
        needBindTexture = true;
        refreshRender();
    }

    // 是否还是默认贴图墙体
    public boolean isDefaultWallTexture() {
        return isDefaultWallTexture;
    }

    // 获取墙体类型
    public int getFlag() {
        return flag;
    }

    /**
     * 检测点是否在面上
     *
     * @param center
     */
    public boolean checkInnerSelf(Point center) {
        if (center == null)
            return false;
        if (flag == FLAG_INTER || flag == FLAG_OUTER) {
            Line line = new Line(pointsList.get(0).copy(), pointsList.get(1).copy());
            return line.getAdsorbPoint(center.x, center.y, 4d) != null;
        } else if (flag == FLAG_TOP_THICKNESS) {
            return PointList.pointRelationToPolygon(pointsList, center) != -1;
        }
        return false;
    }

    /**
     * 切割立面
     *
     * @param line      切割线段
     * @param height    切割高度
     * @param offground 切割离地高
     */
    public void punch(Line line, double height, double offground) {
        if (line == null)
            return;
        new DigHoleTool(cell, 280, line, height, offground, normal.toFloatValues(), pointsList, punchFragmentFacadeArrayList, new DigHoleTool.OnDigHoleListener() {
            @Override
            public void digged(ArrayList<PunchFragmentFacade> punchFragmentFacadeArrayList) {
                WallFacade.this.punchFragmentFacadeArrayList = punchFragmentFacadeArrayList;
                refreshRender();
            }
        });
    }

    @Override
    public void render(int positionAttribute, int normalAttribute, int colorAttribute, boolean onlyPosition) {
        if (needBindTexture) {
            needBindTexture = false;
            textureId = createTextureIdAndCache(uuid, textureBitmap, !isDefaultWallTexture);
            refreshRender();
        }
        if (textureId != -1) {
            /**
             * 渲染切割面
             * */
            if (punchFragmentFacadeArrayList != null && punchFragmentFacadeArrayList.size() > 0) {
                for (int i = 0; i < punchFragmentFacadeArrayList.size(); i++) {
                    if (i < punchFragmentFacadeArrayList.size()) {
                        punchFragmentFacadeArrayList.get(i).render(textureId, positionAttribute, normalAttribute, colorAttribute, onlyPosition);
                    }
                }
                return;
            }
            // 渲染整个立面
            // 顶点
            GLES30.glVertexAttribPointer(positionAttribute, 3, GLES30.GL_FLOAT, false, 12, vertexsBuffer);
            GLES30.glEnableVertexAttribArray(positionAttribute);
            if (!onlyPosition) {
                // 法线
                GLES30.glVertexAttribPointer(normalAttribute, 3, GLES30.GL_FLOAT, false, 12, normalsBuffer);
                GLES30.glEnableVertexAttribArray(normalAttribute);
                // 纹理
                GLES30.glVertexAttribPointer(ViewingShader.scene_uv0, 2, GLES30.GL_FLOAT, false, 8, texcoordBuffer);
                GLES30.glEnableVertexAttribArray(ViewingShader.scene_uv0);
                // 贴图
                GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
                GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureId);
                GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_NEAREST);
                GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
                GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_REPEAT);
                GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_REPEAT);
                GLES30.glUniform1i(ViewingShader.scene_s_baseMap, 0);
                // 着色器使用标志
                GLES30.glUniform1f(ViewingShader.scene_only_color, 0.0f);
                if (RendererState.isNot2D()) {
                    GLES30.glUniform1f(ViewingShader.scene_use_light, 1.0f);
                } else {
                    GLES30.glUniform1f(ViewingShader.scene_use_light, 0.0f);
                }
            }
            GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, indices.length);
        }
    }

}
