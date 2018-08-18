package com.lejia.mobile.orderking.hk3d.factory;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.lejia.mobile.orderking.bases.OrderKingApplication;
import com.lejia.mobile.orderking.hk3d.classes.L3DMatrix;
import com.lejia.mobile.orderking.hk3d.classes.NameData;
import com.lejia.mobile.orderking.hk3d.classes.Point;
import com.lejia.mobile.orderking.hk3d.classes.PointList;
import com.lejia.mobile.orderking.hk3d.classes.PolyE;
import com.lejia.mobile.orderking.hk3d.classes.RectD;
import com.lejia.mobile.orderking.utils.TextUtils;
import com.seisw.util.geom.Poly;

import java.util.ArrayList;

/**
 * Author by HEKE
 *
 * @time 2018/8/1 16:46
 * TODO: 房间名称位图创建对象
 */
public class RoomNameBitmapFactory {

    /**
     * 根据房间面积生成房间名称位图
     *
     * @param roomName      手动设置房间名称,null时为自动根据面积生成名称
     * @param pointList     房间围点列表对象
     * @param overlapBitmap 覆盖住的地面贴图
     * @return 返回房间名称贴图
     */
    public static NameData createRoomNameBitmap(String roomName, PointList pointList, Bitmap overlapBitmap) {
        if (pointList == null || pointList.invalid())
            return null;
        NameData nameData = null;
        Bitmap roomNameBitmap = null;
        try {
            // 面积
            double area = pointList.area();
            // 内部一点
            Point roomNameAtPoint = pointList.getInnerValidPoint(false);
            RectD houseInnerBox = pointList.getRectBox();
            // 房间名称为null，自动根据面积选择
            if (TextUtils.isTextEmpity(roomName)) {
                // 客餐厅
                if (area > 22.0d) {
                    roomName = "客餐厅";
                }
                // 主卧
                else if (area <= 22.0d && area >= 16.0d) {
                    roomName = "主卧";
                }
                // 客房、次卧
                else if (area < 16.0d && area >= 13.0d) {
                    boolean evenNumber = (int) (Math.random() * 10) % 2 == 0;
                    roomName = evenNumber ? "次卧" : "客房";
                }
                // 小孩房、书房
                else if (area < 13.0d && area > 10.0d) {
                    boolean evenNumber = (int) (Math.random() * 10) % 2 == 0;
                    roomName = evenNumber ? "小孩房" : "书房";
                }
                // 厨房、卫生间
                else if (area <= 10.0d && area >= 4.0d) {
                    boolean evenNumber = (int) (Math.random() * 10) % 2 == 0;
                    roomName = evenNumber ? "厨房" : "卫生间";
                }
                // 小卫生间、次卫
                else if (area < 4.0d && area >= 1.5d) {
                    roomName = "次卫";
                }
                // 其他面积视为无效区域，不需要名称命名
                else {
                    return null;
                }
                // 创建画笔
                Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                paint.setDither(true);
                paint.setAntiAlias(true);
                paint.setTextSize(TextUtils.sp2px(OrderKingApplication.getInstant(), 16));
                // 1、获取长宽
                String drawValue = roomName + " " + area + "m²";
                String blackValue = roomName + " ";
                float width = 6 + paint.measureText(drawValue);
                if (width > houseInnerBox.width()) { // 房间宽度过窄，使用小字体
                    paint.setTextSize(TextUtils.sp2px(OrderKingApplication.getInstant(), 9));
                    width = 6 + paint.measureText(drawValue);
                }
                float blackWidth = paint.measureText(blackValue);
                Paint.FontMetrics fm = paint.getFontMetrics();
                float height = 6 + fm.bottom - fm.top;
                // 2、围点检测生成
                ArrayList<Point> pointsList = PointList.getRotateVertexs(0, height, width, roomNameAtPoint);
                Poly innerPoly = PolyE.toPolyDefault(pointList);
                Poly checkPoly = PolyE.toPolyDefault(pointsList);
                Poly interPoly = checkPoly.intersection(innerPoly);
                if (interPoly != null && !interPoly.isEmpty()) {
                    PointList retList = PolyE.toPointList(interPoly);
                    // 有相交区域，与检测区域不同，变更房间位置信息
                    if (!retList.equals(new PointList(pointsList))) {
                        roomNameAtPoint = pointList.getInnerValidPoint(true);
                        pointsList = PointList.getRotateVertexs(0, height, width, roomNameAtPoint);
                    }
                }
                // 3、画笔创建画布
                PointList namePointList = new PointList(pointsList);
                RectD box = namePointList.getRectBox();
                Bitmap bitmap = Bitmap.createBitmap((int) box.width(), (int) box.height(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                bitmap.setHasAlpha(true);
                // 绘制覆盖区域位图
                if (overlapBitmap != null) {
                    RectD innerBox = pointList.getRectBox();
                    double transX = -innerBox.left;
                    double transY = -innerBox.top;
                    ArrayList<Point> transList = L3DMatrix.translate(pointsList, transX, transY, 0);
                    PointList transPointList = new PointList(transList);
                    RectD transBox = transPointList.getRectBox();
                    Bitmap overlapAreaBitmap = Bitmap.createBitmap(overlapBitmap, (int) transBox.left,
                            (int) transBox.top, (int) (transBox.width() + 1.0d), (int) transBox.height());
                    canvas.drawBitmap(overlapAreaBitmap, 0, 0, null);
                    overlapAreaBitmap.recycle();
                }
                // 绘制内容
                float y = 3 + (float) ((box.height() + height / 2) / 2);
                paint.setColor(0xFF000000);
                canvas.drawText(roomName, 3, y, paint);
                paint.setColor(0xFFFF0000);
                canvas.drawText(area + "m²", 3 + blackWidth, y, paint);
                // 创建数据对象
                nameData = new NameData(roomName, "" + area, roomNameAtPoint, bitmap, namePointList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return nameData;
    }

}
