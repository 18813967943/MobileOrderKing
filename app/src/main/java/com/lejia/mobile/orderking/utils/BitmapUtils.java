package com.lejia.mobile.orderking.utils;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author HEKE 位图处理
 * @version 2016年9月27日
 */
public class BitmapUtils {

    /**
     * 将图片位图裁剪为指定宽高的位图
     *
     * @param width
     * @param height
     * @param res
     * @return
     */
    public static Bitmap tailorBitmap(int width, int height, Bitmap res) {
        if (res == null || width < 0 || height < 0)
            return null;
        Bitmap bmp = null;
        try {
            // 新建指定宽高空位图
            bmp = Bitmap.createBitmap(width, height, Config.ARGB_8888);
            Canvas canvas = new Canvas(res);
            canvas.drawBitmap(bmp, 0, 0, new Paint());
            // 释放之前的位图
            res.recycle();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bmp;
    }

    /**
     * 获取指定亮度调节值的图片
     *
     * @param res        需要被调节的图片
     * @param brightness 范围-100~100
     */
    public static Bitmap getBrightnessBitmap(Bitmap res, int brightness) {
        Bitmap darkTexture = null;
        try {
            ColorMatrix cMatrix = new ColorMatrix();
            cMatrix.set(new float[]{1, 0, 0, 0, brightness, 0, 1, 0, 0, brightness, 0, 0, 1, 0, brightness, 0, 0, 0,
                    1, 0});
            Paint paint = new Paint();
            paint.setDither(true);
            paint.setColorFilter(new ColorMatrixColorFilter(cMatrix));
            darkTexture = Bitmap.createBitmap(res.getWidth(), res.getHeight(), Config.ARGB_8888);
            Canvas canvas = new Canvas(darkTexture);
            canvas.drawBitmap(res, 0, 0, paint);
            // 释放之前的位图
            res.recycle();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return darkTexture;
    }

    /**
     * bitmap转为base64
     *
     * @param bitmap
     * @return
     */
    public static String bitmapToBase64(Bitmap bitmap) {
        String result = null;
        ByteArrayOutputStream baos = null;
        try {
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                baos.flush();
                baos.close();
                byte[] bitmapBytes = baos.toByteArray();
                result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.flush();
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * base64转为bitmap
     *
     * @param base64Data
     * @return
     */
    public static Bitmap base64ToBitmap(String base64Data) {
        byte[] bytes = Base64.decode(base64Data, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }


    /**
     * 转化图片为二进制数据
     *
     * @param bitmap
     */
    public static byte[] getBitmapByte(Bitmap bitmap) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        byte[] buffer = out.toByteArray();
        try {
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer;
    }

    /**
     * 质量压缩方法
     *
     * @param originBitmap
     * @param targetSize   位图保存大小，单位kb
     * @param percent      百分比
     * @return
     */
    public static Bitmap compressImage(Bitmap originBitmap, int targetSize, float percent) {
        Bitmap bitmap = null;
        try {
            // 原图
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            originBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            int quality = 90;
            // 使用百分比压缩
            if (percent != 1.0f) {
                targetSize = (int) ((baos.toByteArray().length / 1024) * percent);
            }
            // 压缩
            while (baos.toByteArray().length / 1024 > targetSize) {
                baos.reset();
                originBitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
                quality -= 10;
            }
            ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
            bitmap = BitmapFactory.decodeStream(isBm, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 镜像
     *
     * @param bitmap
     * @param flag   0为x轴对调，1为y轴对调
     * @return 返回镜像的位图
     */
    public static Bitmap mirror(Bitmap bitmap, int flag) {
        if (bitmap == null || bitmap.isRecycled())
            return null;
        Bitmap mirror = null;
        try {
            Bitmap bitmap1 = bitmap;
            Bitmap bitmap2 = Bitmap.createBitmap(bitmap1.getWidth(), bitmap1.getHeight(), Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap2);
            Matrix orig = canvas.getMatrix();
            //翻转X
            if (flag == 0) {
                orig.setScale(-1, 1);
                orig.postTranslate(bitmap1.getWidth(), 0);//平移
            }
            // 翻转Y
            else if (flag == 1) {
                orig.setScale(1, -1);
                orig.postTranslate(0, bitmap1.getHeight());//平移
            }
            canvas.drawBitmap(bitmap1, orig, null);
            mirror = bitmap2;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mirror;
    }

    /**
     * 将图片位图指定大小
     *
     * @param res    被修改大小的位图
     * @param width  修改后的宽度
     * @param height 修改前的高度
     * @return 改变大小后的位图
     */
    public static Bitmap toSize(Bitmap res, int width, int height) {
        if (res == null || res.isRecycled())
            return null;
        if (width <= 1 || height <= 1)
            return null;
        int bmpWidth = res.getWidth();
        int bmpHeight = res.getHeight();
        float scaleWidth = width * 1f / bmpWidth;
        float scaleHeight = height * 1f / bmpHeight;
        Matrix matrix = new Matrix();
        matrix.setScale(scaleWidth, scaleHeight);
        Bitmap ret = Bitmap.createBitmap(res, 0, 0, bmpWidth, bmpHeight, matrix, true);
        res.recycle();
        return ret;
    }

}
