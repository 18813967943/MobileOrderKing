package com.lejia.mobile.orderking.hk3d.datas_2d;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES30;
import android.opengl.GLException;
import android.opengl.GLUtils;

import com.lejia.mobile.orderking.bases.OrderKingApplication;
import com.lejia.mobile.orderking.hk3d.classes.LJ3DPoint;
import com.lejia.mobile.orderking.hk3d.classes.Texture;
import com.lejia.mobile.orderking.utils.TextUtils;

import java.io.InputStream;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;

/**
 * Author by HEKE
 *
 * @time 2018/7/16 10:32
 * TODO: 渲染对象父类
 */
public abstract class RendererObject {

    public float[] vertexs; // 顶点
    public FloatBuffer vertexsBuffer; // 顶点字节缓存

    public float[] texcoord; // 纹理uv
    public FloatBuffer texcoordBuffer; // 纹理uv字节缓存

    public float[] normalTexcoord; // 法线纹理uv
    public FloatBuffer normalTexcoordBuffer; // 法线纹理uv字节缓存

    public float[] colors; // 颜色
    public FloatBuffer colorsBuffer; // 颜色字节缓存

    public short[] indices; // 索引
    public ShortBuffer indicesBuffer; // 索引字节缓存
    public int[] bigIndices; // 大位数索引
    public IntBuffer bigIndicesBuffer; // 大位数索引字节缓存

    public float[] normals; // 法线
    public FloatBuffer normalsBuffer; // 法线字节缓存

    public int textureId = -1; // 纹理对应编号

    public int normalTextureId = -1; // 法线纹理对应编号

    public String textureName; // 纹理名称

    public String normalTextureName; // 法线纹理名称

    public ArrayList<LJ3DPoint> lj3DPointsList; // 三维围点列表

    public String uuid; // 唯一编码

    public Selector selector; // 选中对象
    public boolean selected; // 是否选中

    // 判断模型是否选中
    public boolean isSelected() {
        return selected;
    }

    // 设置模型选中状态
    public void setSelected(boolean selected) {
        this.selected = selected;
        refreshRender();
    }

    /**
     * 基础渲染
     *
     * @param positionAttribute 位置编号
     * @param normalAttribute   法线编号
     * @param colorAttribute    颜色编号
     * @param onlyPosition      是否阴影仅顶点着色
     */
    public abstract void render(int positionAttribute, int normalAttribute, int colorAttribute, boolean onlyPosition);

    /**
     * 获取材质缓存
     *
     * @param key 对象唯一编码(UUID或模型材质编码)
     * @return 返回缓存对象
     */
    public Texture getTextureCache(String key) {
        return TexturesCache.get(key);
    }

    /**
     * 创建材质纹理对应编号及缓存
     *
     * @param key              唯一身份证
     * @param bitmap           对应贴图
     * @param fromReplaceTiles 是否来自于材质替换
     * @return 返回材质纹理对应渲染编号
     */
    public int createTextureIdAndCache(String key, Bitmap bitmap, boolean fromReplaceTiles) {
        if (TextUtils.isTextEmpty(key) || bitmap == null || bitmap.isRecycled())
            return -1;
        int[] textureId = new int[1];
        try {
            Texture texture = TexturesCache.get(key);
            if (texture != null) {
                // 无替换铺砖操作
                if (!fromReplaceTiles) {
                    return texture.textureId;
                }
                // 刷新铺砖材质，先删除当前铺砖材质
                else {
                    GLES30.glDeleteTextures(1, new int[]{texture.textureId}, 0);
                }
            }
            GLES30.glGenTextures(1, textureId, 0);
            GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureId[0]);
            GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, GLES30.GL_RGBA, bitmap, 0);
            GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_NEAREST);
            GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
            GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_REPEAT);
            GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_REPEAT);
            // 刷新缓存
            TexturesCache.put(key, textureId[0], bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return textureId[0];
    }

    /**
     * 创建指定区域的位图
     *
     * @param x 起始点x
     * @param y 起始点y
     * @param w 宽度
     * @param h 高度
     * @return 返回截取的位图
     */
    public Bitmap createBitmapFromGLSurface(int x, int y, int w, int h) {
        int bitmapBuffer[] = new int[w * h];
        int bitmapSource[] = new int[w * h];
        IntBuffer intBuffer = IntBuffer.wrap(bitmapBuffer);
        intBuffer.position(0);
        Bitmap overlapBitmap = null;
        try {
            GLES30.glReadPixels(x, y, w, h, GLES30.GL_RGBA, GLES30.GL_UNSIGNED_BYTE, intBuffer);
            int offset1, offset2;
            for (int i = 0; i < h; i++) {
                offset1 = i * w;
                offset2 = (h - i - 1) * w;
                for (int j = 0; j < w; j++) {
                    int texturePixel = bitmapBuffer[offset1 + j];
                    int blue = (texturePixel >> 16) & 0xff;
                    int red = (texturePixel << 16) & 0x00ff0000;
                    int pixel = (texturePixel & 0xff00ff00) | red | blue;
                    bitmapSource[offset2 + j] = pixel;
                }
            }
            overlapBitmap = Bitmap.createBitmap(bitmapSource, w, h, Bitmap.Config.ARGB_8888);
        } catch (GLException e) {
            return null;
        }
        return overlapBitmap;
    }

    /**
     * 从资源中获取贴图
     *
     * @param path
     * @return
     */
    public Bitmap createTextureWithAssets(String path) {
        if (path == null)
            return null;
        Bitmap bitmap = null;
        try {
            InputStream fis = getContext().getAssets().open(path);
            bitmap = BitmapFactory.decodeStream(fis);
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 从资源中获取贴图
     *
     * @param id
     * @return
     */
    public Bitmap createTextureWithRaw(int id) {
        if (id <= 0)
            return null;
        Bitmap bitmap = null;
        try {
            InputStream fis = getContext().getResources().openRawResource(id);
            bitmap = BitmapFactory.decodeStream(fis);
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 刷新画布
     */
    public void refreshRender() {
        ((OrderKingApplication) OrderKingApplication.getInstant()).render();
    }

    /**
     * 获取渲染数据管理对象
     */
    public HouseDatasManager getHouseDatasManager() {
        return ((OrderKingApplication) OrderKingApplication.getInstant()).getDesigner3DSurfaceView().getDesigner3DRender().getHouseDatasManager();
    }

    /**
     * 获取上下文
     */
    public Context getContext() {
        return OrderKingApplication.getInstant();
    }

    /**
     * 释放数据
     */
    public void release() {
        GLES30.glDeleteTextures(2, new int[]{textureId, normalTextureId}, 0);
    }

}
