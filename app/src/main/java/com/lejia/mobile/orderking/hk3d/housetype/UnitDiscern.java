package com.lejia.mobile.orderking.hk3d.housetype;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.opengl.Matrix;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Base64;

import com.lejia.mobile.orderking.bases.OrderKingApplication;
import com.lejia.mobile.orderking.hk3d.Designer3DSurfaceView;
import com.lejia.mobile.orderking.hk3d.classes.L3DMatrix;
import com.lejia.mobile.orderking.hk3d.classes.Line;
import com.lejia.mobile.orderking.hk3d.classes.Point;
import com.lejia.mobile.orderking.hk3d.classes.PointList;
import com.lejia.mobile.orderking.hk3d.classes.RectD;
import com.lejia.mobile.orderking.hk3d.datas_2d.Furniture;
import com.lejia.mobile.orderking.hk3d.datas_2d.FurnitureMatrixs;
import com.lejia.mobile.orderking.hk3d.datas_2d.HouseDatasManager;
import com.lejia.mobile.orderking.hk3d.datas_2d.NormalHouse;
import com.lejia.mobile.orderking.hk3d.datas_2d.cadwidgets.FurTypes;
import com.lejia.mobile.orderking.hk3d.datas_2d.cadwidgets.SimpleWindow;
import com.lejia.mobile.orderking.hk3d.datas_2d.cadwidgets.SingleDoor;
import com.lejia.mobile.orderking.utils.BitmapUtils;
import com.lejia.mobile.orderking.utils.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Author by HEKE
 *
 * @time 2018/10/16 11:52
 * TODO: 户型识别
 */
public class UnitDiscern {

    private static final String TO_URL = "http://192.168.1.97:5000/api/v1.0/sizechart/";
    private Context mContext;
    private File postFile; // 发送本地源文件

    public UnitDiscern(Context context, @NonNull Intent data) {
        this.mContext = context;
        Uri selectedImage = data.getData();
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(selectedImage,
                filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        final String picturePath = cursor.getString(columnIndex);
        cursor.close();
        postFile = new File(picturePath);
        preparePost();
    }

    public UnitDiscern(Context context, @NonNull File file) {
        this.mContext = context;
        postFile = file;
        preparePost();
    }

    /**
     * 执行请求
     */
    @SuppressLint("StaticFieldLeak")
    private void preparePost() {
        if (postFile == null || !postFile.exists())
            return;
        new AsyncTask<String, Integer, String>() {
            @Override
            protected String doInBackground(String... strings) {
                try {
                    // 压缩源文件位图
                    FileInputStream fileInputStream = new FileInputStream(postFile);
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    options.inSampleSize = 3;
                    options.inJustDecodeBounds = false;
                    Bitmap oringin = BitmapFactory.decodeStream(fileInputStream, null, options);
                    fileInputStream.close();
                    // 转化文件格式
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    oringin.compress(Bitmap.CompressFormat.JPEG, 90, baos);
                    byte[] buffer = baos.toByteArray();
                    Bitmap jpgBitmap = BitmapFactory.decodeByteArray(buffer, 0, buffer.length);
                    oringin.recycle();
                    baos.close();
                    // 转化大小
                    Bitmap toSizeBitmap = BitmapUtils.toSize(jpgBitmap, 480, 360);
                    // 转化为json字符串
                    ByteArrayOutputStream toJson = new ByteArrayOutputStream();
                    toSizeBitmap.compress(Bitmap.CompressFormat.JPEG, 100, toJson);
                    String base64Bitmap = Base64.encodeToString(toJson.toByteArray(), Base64.DEFAULT);
                    String jsonResult = "{\"" + "file" + "\":\"" + base64Bitmap + "\"}";
                    toSizeBitmap.recycle();
                    toJson.close();
                    // 执行请求
                    post(jsonResult);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }

    /**
     * 发送请求
     *
     * @param params 数据
     */
    private void post(String params) throws JSONException {
        try {
            //创建连接
            URL url = new URL(TO_URL);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestProperty("Content-Type",
                    "application/json");
            connection.connect();
            //POST请求
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            JSONObject json = new JSONObject(params);
            String jsonString = json.toString();
            byte[] jsonByte = jsonString.getBytes();
            out.write(jsonByte);
            out.flush();
            out.close();
            //读取响应
            DataInputStream inputStream = null;
            String strInputStream = "";
            inputStream = new DataInputStream(new BufferedInputStream(connection.getInputStream()));
            int size = 1024;
            byte[] by = new byte[size];
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int nbyte;
            while ((nbyte = inputStream.read(by)) != -1) {
                baos.write(by, 0, nbyte);
            }
            strInputStream = new String(baos.toByteArray());
            // 断开连接
            connection.disconnect();
            baos.close();
            inputStream.close();
            // 数据内容读取
            discern(strInputStream);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /********************************** 解析数据集 ************************************/

    private ArrayList<SingleDoor> singleDoorArrayList; // 门列表
    private ArrayList<SimpleWindow> simpleWindowArrayList; // 窗列表
    private ArrayList<PointList> pointListArrayList; // 房间有效点列表
    private ArrayList<NormalHouse> normalHouseArrayList; // 房间列表
    private Furniture doorsFurniture; // 默认门
    private Furniture windowFurniture; // 默认窗

    private int scaleSize = 3;

    /**
     * 解析识别数据内容
     *
     * @param resultJson
     */
    private void discern(String resultJson) {
        if (TextUtils.isTextEmpity(resultJson))
            return;
        try {
            JSONObject object = new JSONObject(resultJson);
            int size;
            // 门
            if (object.has("doors")) {
                Object o = object.get("doors");
                if (o != null && ("" + o).length() > 0) {
                    JSONArray doorsArray = object.getJSONArray("doors");
                    size = doorsArray.length();
                    if (size > 0) {
                        singleDoorArrayList = new ArrayList<>();
                        // 默认使用门
                        doorsFurniture = new Furniture();
                        doorsFurniture.materialCode = "MEN062-WZ0416";
                        doorsFurniture.width = 215;
                        doorsFurniture.xLong = 992;
                        doorsFurniture.height = 2250;
                        doorsFurniture.enterpriseID = "e379332e-5a44-4b27-8056-fcc2a91a8818";
                        doorsFurniture.creatorID = "2b1a1b18-a0be-44f0-a202-6df41c762c74";
                        doorsFurniture.materialGID = "f535b45f-1870-46e4-b1ed-445199129bdb";
                        doorsFurniture.preview = "http://192.168.1.100:8070/Content/LejiaImg/5d4189e0-b5be-44ee-b9ee-4b3cdb853b13.jpg";
                        doorsFurniture.materialSubsetsJsonURL = "http://192.168.1.100:8070/Content/MaterialAttachBufferFile/MEN062-WZ0416.bf";
                        doorsFurniture.materialURL = "http://192.168.1.100:8070/Content/ModelMaterialFile/MEN062-WZ0416.L3D";
                        doorsFurniture.modelMaterialTypeID = 2;
                        doorsFurniture.modelMaterialRoomTypeID = 0;
                        doorsFurniture.renderingPath = "LIBRARY\\JD20180730165009589288\\MEN062-WZ0416.L3D";
                        doorsFurniture.topView = "http://192.168.1.100:8070/Content/LejiaImg/0ebc6728-b348-43a8-af13-9acebd52381b.png";
                        // 遍历创建门
                        for (int i = 0; i < size; i++) {
                            JSONObject doorObject = doorsArray.getJSONObject(i);
                            Point begain = new Point(doorObject.getInt("x1"), doorObject.getInt("y1"));
                            Point end = new Point(doorObject.getInt("x2"), doorObject.getInt("y2"));
                            Line line = new Line(begain, end);
                            // 门的长度、吸附点
                            double xlength = Point.percision((line.getLength() / 27d) * 992d, 0);
                            Point center = scalePoint(line.getCenter(), scaleSize);
                            // 创建门
                            SingleDoor singleDoor = new SingleDoor(line.getAngle(), 24, xlength / 10, center, FurTypes.SINGLE_DOOR, doorsFurniture);
                            doorsFurniture.put(new FurnitureMatrixs(center, 0, 0, (float) line.getAngle(),
                                    240f / doorsFurniture.width * 1.0f, 1.0f, 1.0f,
                                    (float) center.x, (float) center.y, 0.0f, false));
                            singleDoorArrayList.add(singleDoor);
                        }
                    }
                }
            }
            // 窗
            if (object.has("windows")) {
                Object o = object.get("windows");
                if (o != null && ("" + o).length() > 0) {
                    JSONArray windosArray = object.getJSONArray("windows");
                    size = windosArray.length();
                    if (size > 0) {
                        simpleWindowArrayList = new ArrayList<>();
                        // 默认使用窗体
                        windowFurniture = new Furniture();
                        windowFurniture.materialCode = "C-02";
                        windowFurniture.width = 113;
                        windowFurniture.xLong = 1183;
                        windowFurniture.height = 1183;
                        windowFurniture.groundHeight = 600;
                        windowFurniture.enterpriseID = "e379332e-5a44-4b27-8056-fcc2a91a8818";
                        windowFurniture.creatorID = "2b1a1b18-a0be-44f0-a202-6df41c762c74";
                        windowFurniture.materialGID = "003a7955-8809-4163-912a-d8978617c2fa";
                        windowFurniture.preview = "http://192.168.1.100:8070/Content/LejiaImg/e2c7553b-d83b-4c47-8821-1b0ae7055513.jpg";
                        windowFurniture.materialSubsetsJsonURL = "http://192.168.1.100:8070/Content/MaterialAttachBufferFile/C-02.bf";
                        windowFurniture.materialURL = "http://192.168.1.100:8070/Content/ModelMaterialFile/C-02.L3D";
                        windowFurniture.modelMaterialTypeID = 3;
                        windowFurniture.modelMaterialRoomTypeID = 0;
                        windowFurniture.renderingPath = "LIBRARY\\JD20180730170045301121\\C-02.L3D";
                        windowFurniture.topView = "http://192.168.1.100:8070/Content/LejiaImg/e96b6722-ca31-471f-9c0d-e1db0f0677ee.png";
                        // 遍历创建窗
                        for (int i = 0; i < size; i++) {
                            JSONObject windowObject = windosArray.getJSONObject(i);
                            Point begain = new Point(windowObject.getInt("x1"), windowObject.getInt("y1"));
                            Point end = new Point(windowObject.getInt("x2"), windowObject.getInt("y2"));
                            Line line = new Line(begain, end);
                            // 窗的长度、吸附点
                            Point center = scalePoint(line.getCenter(), scaleSize);
                            SimpleWindow simpleWindow = new SimpleWindow(line.getAngle(), 24, windowFurniture.xLong / 10, center,
                                    FurTypes.SINGLE_DOOR, windowFurniture);
                            windowFurniture.put(new FurnitureMatrixs(center, 0, 0, (float) line.getAngle(),
                                    240f / windowFurniture.width * 1.0f, 1.0f, 1.0f,
                                    (float) center.x, (float) center.y, 0.0f, false));
                            simpleWindowArrayList.add(simpleWindow);
                        }
                    }
                }
            }
            // 闭合房间地面
            RectD box = null;
            if (object.has("floor")) {
                Object o = object.get("floor");
                if (o != null && ("" + o).length() > 0) {
                    JSONArray floorsArray = object.getJSONArray("floor");
                    normalHouseArrayList = new ArrayList<>();
                    pointListArrayList = new ArrayList<>();
                    size = floorsArray.length();
                    if (size > 0) {
                        ArrayList<Point> allPointArrayList = new ArrayList<>();
                        for (int i = 0; i < size; i++) {
                            JSONArray floor = floorsArray.getJSONArray(i);
                            int psize = floor.length();
                            ArrayList<Point> pointArrayList = new ArrayList<>();
                            for (int j = 0; j < psize; j++) {
                                JSONObject child = floor.getJSONObject(j);
                                Point point = new Point(child.getInt("x"), child.getInt("y"));
                                if (pointArrayList.size() == 0) {
                                    pointArrayList.add(point);
                                } else {
                                    boolean existed = false;
                                    for (int k = 0; k < pointArrayList.size(); k++) {
                                        Point point1 = pointArrayList.get(k);
                                        if ((point.x == point1.x) && (point.y == point1.y)) {
                                            existed = true;
                                            break;
                                        }
                                    }
                                    if (!existed)
                                        pointArrayList.add(point);
                                }
                            }
                            PointList pointList = new PointList(wipeNotNeedSkewWallThenScale(pointArrayList));
                            allPointArrayList.addAll(pointList.getPointsList());
                            if (pointList.size() >= 3 && pointList.area() >= 1.0d) {
                                pointListArrayList.add(pointList);
                            }
                        }
                        box = new PointList(allPointArrayList).getRectBox();
                    }
                }
            }
            // 盒子不为空，进行所有点的偏置
            if (box != null) {
                // 偏移量
                double transX = -Point.percision(box.centerX(), Point.defaultDecimalPlaces);
                double transY = -Point.percision(box.centerY(), Point.defaultDecimalPlaces);
                // 房间偏置
                if (pointListArrayList != null) {
                    for (int i = 0; i < pointListArrayList.size(); i++) {
                        PointList pointList = pointListArrayList.get(i);
                        ArrayList<Point> translateList = L3DMatrix.translate(pointList.getPointsList(), transX, transY, 0.0d);
                        // 创建房间
                        PointList houseList = new PointList(translateList);
                        NormalHouse normalHouse = new NormalHouse(mContext, houseList, 24);
                        normalHouseArrayList.add(normalHouse);
                    }
                }
                // 门偏置
                if (singleDoorArrayList != null) {
                    for (SingleDoor singleDoor : singleDoorArrayList) {
                        Point p = singleDoor.getPoint();
                        singleDoor.setPoint(new Point(p.x + transX, p.y + transY));
                    }
                }
                // 窗偏置
                if (simpleWindowArrayList != null) {
                    for (SimpleWindow simpleWindow : simpleWindowArrayList) {
                        Point p = simpleWindow.getPoint();
                        simpleWindow.setPoint(new Point(p.x + transX, p.y + transY));
                    }
                }
            }
            done();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 去除双转角墙体
     *
     * @param pointArrayList
     * @return
     */
    private ArrayList<Point> wipeNotNeedSkewWallThenScale(ArrayList<Point> pointArrayList) {
        if (pointArrayList == null || pointArrayList.size() == 0)
            return null;
        ArrayList<Point> retList = new ArrayList<>();
        try {
            PointList pointList = new PointList(pointArrayList);
            ArrayList<Line> lineArrayList = pointList.toLineList();
            // 去除所有倾斜线段
            ArrayList<Line> noSkewLinesList = new ArrayList<>();
            for (int i = 0; i < lineArrayList.size(); i++) {
                Line now = lineArrayList.get(i);
                double dist = now.getLength();
                if (dist >= 5) {
                    noSkewLinesList.add(now);
                }
            }
            // 获取连接围点
            for (int i = 0; i < noSkewLinesList.size(); i++) {
                Line now = noSkewLinesList.get(i);
                Line next = null;
                if (i == noSkewLinesList.size() - 1) {
                    next = noSkewLinesList.get(0);
                } else {
                    next = noSkewLinesList.get(i + 1);
                }
                // 做两条线段的延长线
                Line nowExtend = now.toExtendLine(5);
                Line nextExtend = next.toExtendLine(5);
                Point interPoint = nowExtend.getLineIntersectedPoint(nextExtend);
                // 交点不为空时直接加入
                if (interPoint != null) {
                    retList.add(interPoint);
                }
                // 无交点时，增加当前线段的结尾点与下一条线段的起始点
                else {
                    retList.add(now.up.copy());
                    retList.add(next.down.copy());
                }
            }
            // 进行缩放
            if (retList != null && retList.size() > 0) {
                ArrayList<Point> scaleList = new ArrayList<>();
                for (Point point : retList) {
                    Point p = scalePoint(point, scaleSize);
                    scaleList.add(p);
                }
                retList = scaleList;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retList;
    }

    /**
     * 围点统一标准缩放
     *
     * @param point
     * @param scaleSize 缩放数值
     * @return
     */
    private Point scalePoint(Point point, int scaleSize) {
        float[] matrixs = new float[16];
        Matrix.setIdentityM(matrixs, 0);
        Matrix.scaleM(matrixs, 0, scaleSize, scaleSize, 1);
        float[] ps = new float[]{(float) point.x, (float) point.y, 0, 1};
        float[] ret = new float[4];
        Matrix.multiplyMV(ret, 0, matrixs, 0, ps, 0);
        return new Point(-ret[0], ret[1]);
    }

    /**
     * 增加显示控件
     */
    private void done() {
        try {
            // 获取房间数据管理对象
            Designer3DSurfaceView designer3DSurfaceView = ((OrderKingApplication) mContext.getApplicationContext()).getDesigner3DSurfaceView();
            HouseDatasManager houseDatasManager = designer3DSurfaceView.getDesigner3DRender().getHouseDatasManager();
            // 增加房间
            if (normalHouseArrayList != null) {
                for (NormalHouse normalHouse : normalHouseArrayList) {
                    houseDatasManager.add(normalHouse);
                    houseDatasManager.gpcClosedCheck(normalHouse);
                }
            }
            // 增加门窗
            if (singleDoorArrayList != null) {
                for (SingleDoor singleDoor : singleDoorArrayList) {
                    houseDatasManager.addFurniture(singleDoor);
                }
            }
            if (simpleWindowArrayList != null) {
                for (SimpleWindow simpleWindow : simpleWindowArrayList) {
                    houseDatasManager.addFurniture(simpleWindow);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
