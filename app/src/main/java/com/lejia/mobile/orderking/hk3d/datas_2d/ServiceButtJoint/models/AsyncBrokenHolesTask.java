package com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.models;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;

import com.lejia.mobile.orderking.bases.OrderKingApplication;
import com.lejia.mobile.orderking.classes.XInfo;
import com.lejia.mobile.orderking.hk3d.RendererState;
import com.lejia.mobile.orderking.hk3d.classes.LJ3DPoint;
import com.lejia.mobile.orderking.hk3d.classes.Line;
import com.lejia.mobile.orderking.hk3d.classes.Point;
import com.lejia.mobile.orderking.hk3d.classes.PointList;
import com.lejia.mobile.orderking.hk3d.datas_2d.cadwidgets.BaseCad;
import com.lejia.mobile.orderking.hk3d.datas_3d.ShadowsGLSurfaceView;
import com.lejia.mobile.orderking.hk3d.datas_3d.ShadowsRenderer;
import com.lejia.mobile.orderking.hk3d.datas_3d.classes.BuildingFragmentWall;
import com.lejia.mobile.orderking.hk3d.datas_3d.classes.BuildingWall;
import com.lejia.mobile.orderking.hk3d.datas_3d.tools.CellsRecord;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Author by HEKE
 *
 * @time 2018/12/8 15:51
 * TODO: 异步线程墙体开洞操作对象
 */
public class AsyncBrokenHolesTask {

    private Context mContext;
    private ArrayList<BaseCad> innnerwallsList;
    private ShadowsGLSurfaceView shadowsGLSurfaceView;
    private ArrayList<Pierce> pierceArrayList;

    public AsyncBrokenHolesTask(Context context, ArrayList<BaseCad> innnerwallsList) {
        this.mContext = context;
        this.innnerwallsList = innnerwallsList;
        this.pierceArrayList = new ArrayList<>();
        execute();
    }

    /**
     * 执行穿洞操作
     */
    @SuppressLint("StaticFieldLeak")
    private void execute() {
        shadowsGLSurfaceView = ((OrderKingApplication) mContext.getApplicationContext()).getShadowsGLSurfaceView();
        if (shadowsGLSurfaceView == null) {
            return;
        }
        ShadowsRenderer shadowsRenderer = shadowsGLSurfaceView.getRenderer();
        if (shadowsRenderer == null) {
            return;
        }
        ArrayList<BuildingWall> buildingWallArrayList = shadowsRenderer.getTotalBePirecedWallsList();
        if (buildingWallArrayList == null || buildingWallArrayList.size() == 0) {
            return;
        }
        new AsyncTask<ArrayList<BuildingWall>, Integer, String>() {
            @Override
            protected String doInBackground(ArrayList<BuildingWall>... arrayLists) {
                try {
                    // 运算处理每条墙体与穿洞模型的关系
                    pierceArrayList.clear();
                    for (BuildingWall buildingWall : arrayLists[0]) {
                        buildingWall.resetFraments(); // 去除切割面，用于重新切割
                        ArrayList<Point> oringinList = buildingWall.originPointsList;
                        Line line = new Line(oringinList.get(0).copy(), oringinList.get(1).copy());
                        ArrayList<BaseCad> inCadArrayList = new ArrayList<>();
                        ArrayList<Point> adsorbList = new ArrayList<>();
                        for (BaseCad baseCad : innnerwallsList) {
                            Point point = baseCad.topView.adi.point;
                            Point adsorb = line.getAdsorbPoint(point.x, point.y, 50);
                            boolean inthiswall = (adsorb != null);
                            if (inthiswall) {
                                adsorbList.add(adsorb);
                                inCadArrayList.add(baseCad);
                            }
                        }
                        if (inCadArrayList.size() > 0)
                            pierceArrayList.add(new Pierce(buildingWall, inCadArrayList, adsorbList));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                refreshRender();
            }
        }.execute(buildingWallArrayList);
    }

    /**
     * Author by HEKE
     *
     * @time 2018/12/8 16:47
     * TODO: 穿洞数据捆绑对象
     */
    private class Pierce {

        public BuildingWall buildingWall; // 开动墙体
        public ArrayList<BaseCad> innerCadList; // 此墙体内需要开洞的模型
        public ArrayList<Point> adsorbList; // 对应模型吸附墙体内的点列表

        public Pierce(BuildingWall buildingWall, ArrayList<BaseCad> innerCadList, ArrayList<Point> adsorbList) {
            this.buildingWall = buildingWall;
            this.innerCadList = innerCadList;
            this.adsorbList = adsorbList;
            pierce();
        }

        // 执行操作
        private void pierce() {
            try {
                ArrayList<BuildingFragmentWall> buildingFragmentWallArrayList = new ArrayList<>();
                // 转化墙体点至平面
                ArrayList<Point> oringinList = buildingWall.originPointsList;
                Line line = new Line(oringinList.get(0).copy(), oringinList.get(1).copy());
                final Point wallBegain = line.down.copy();
                final Point wallUp = line.up.copy();
                float lineLength = (float) line.getLength();
                float cellHeight = CellsRecord.get(1).cellHeight;
                // 根据起始点进行切割点分段排序
                ArrayList<Point> splitPointsList = new ArrayList<>();
                ArrayList<Anchor> anchorsList = new ArrayList<>();
                for (int i = 0; i < innerCadList.size(); i++) {
                    BaseCad baseCad = innerCadList.get(i);
                    Point adsorb = adsorbList.get(i); // 对应墙体上的点
                    TopView topView = baseCad.topView;
                    XInfo xInfo = topView.xInfo; // 模型信息
                    ADI adi = topView.adi; // 矩阵相关信息
                    ArrayList<Point> lepsList = PointList.getRotateLEPS(line.getAngle(), xInfo.X * 0.1d, adsorb);
                    splitPointsList.addAll(lepsList);
                    // 添加标记锚
                    anchorsList.add(new Anchor(xInfo.offGround / 10, xInfo.Z / 10, lepsList));
                }
                Collections.sort(splitPointsList, new Comparator<Point>() {
                    @Override
                    public int compare(Point o1, Point o2) {
                        double disto1 = o1.dist(wallBegain);
                        double disto2 = o2.dist(wallBegain);
                        int ret = Double.compare(disto1, disto2); // 排序规则为每个点与墙体起始点的距离大小，越小越靠近优先排列
                        if (ret != 0)
                            return ret;
                        return 0;
                    }
                });
                // 执行切割
                ArrayList<Point> sectionsList = new ArrayList<>();
                sectionsList.add(wallBegain.copy());
                sectionsList.addAll(splitPointsList);
                sectionsList.add(line.up.copy());
                int size = sectionsList.size() - 1;
                for (int i = 0; i < size; i++) {
                    Point begain = sectionsList.get(i);
                    Point end = sectionsList.get(i + 1);
                    ArrayList<Point> pointsList = new ArrayList<>();
                    pointsList.add(begain.copy());
                    pointsList.add(end.copy());
                    Anchor anchor = null;
                    for (Anchor a : anchorsList) {
                        if (a.equals(pointsList)) {
                            anchor = a;
                            break;
                        }
                    }
                    // 水平纹理
                    float bu = (float) (begain.dist(wallBegain) / lineLength);
                    float eu = (float) (end.dist(wallBegain) / lineLength);
                    // 非切割区域
                    if (anchor == null) {
                        ArrayList<LJ3DPoint> lj3DPointArrayList = new ArrayList<>();
                        lj3DPointArrayList.add(new LJ3DPoint(begain.x, begain.y, 0));
                        lj3DPointArrayList.add(new LJ3DPoint(end.x, end.y, 0));
                        lj3DPointArrayList.add(new LJ3DPoint(end.x, end.y, cellHeight));
                        lj3DPointArrayList.add(new LJ3DPoint(begain.x, begain.y, cellHeight));
                        // 纹理
                        float[] texcoords = new float[]{bu, 0, eu, 0, eu, 1.0f, bu, 0, eu, 1.0f, bu, 1.0f};
                        // 创建添加
                        BuildingFragmentWall buildingFragmentWall = new BuildingFragmentWall(lj3DPointArrayList, texcoords);
                        buildingFragmentWallArrayList.add(buildingFragmentWall);
                    }
                    // 切割区域
                    else {
                        // 离地高大于0
                        if (anchor.offGround > 0) {
                            // 创建离地高区域
                            ArrayList<LJ3DPoint> offGroundlj3DPointArrayList = new ArrayList<>();
                            offGroundlj3DPointArrayList.add(new LJ3DPoint(begain.x, begain.y, 0));
                            offGroundlj3DPointArrayList.add(new LJ3DPoint(end.x, end.y, 0));
                            offGroundlj3DPointArrayList.add(new LJ3DPoint(end.x, end.y, anchor.offGround));
                            offGroundlj3DPointArrayList.add(new LJ3DPoint(begain.x, begain.y, anchor.offGround));
                            // 纹理
                            float bv = anchor.offGround / cellHeight;
                            float[] texcoords = new float[]{bu, 0, eu, 0, eu, bv, bu, 0, eu, bv, bu, bv};
                            BuildingFragmentWall offGroundBuildingFragmentWall = new BuildingFragmentWall(offGroundlj3DPointArrayList, texcoords);
                            buildingFragmentWallArrayList.add(offGroundBuildingFragmentWall);
                            // 顶面切割区域创建
                            float bz = (anchor.offGround + anchor.modelsHeight > cellHeight ? cellHeight : anchor.offGround + anchor.modelsHeight);
                            if (bz != cellHeight) {
                                // 垂直纹理
                                bv = bz / cellHeight;
                                ArrayList<LJ3DPoint> lj3DPointArrayList = new ArrayList<>();
                                lj3DPointArrayList.add(new LJ3DPoint(begain.x, begain.y, bz));
                                lj3DPointArrayList.add(new LJ3DPoint(end.x, end.y, bz));
                                lj3DPointArrayList.add(new LJ3DPoint(end.x, end.y, cellHeight));
                                lj3DPointArrayList.add(new LJ3DPoint(begain.x, begain.y, cellHeight));
                                // 纹理
                                texcoords = new float[]{bu, bv, eu, bv, eu, 1.0f, bu, bv, eu, 1.0f, bu, 1.0f};
                                // 创建添加
                                BuildingFragmentWall buildingFragmentWall = new BuildingFragmentWall(lj3DPointArrayList, texcoords);
                                buildingFragmentWallArrayList.add(buildingFragmentWall);
                            }
                        }
                        // 接地模型
                        else {
                            float bz = (anchor.modelsHeight > cellHeight ? cellHeight : anchor.modelsHeight);
                            if (bz != cellHeight) { // 没占满空间，创建上部切割
                                // 垂直纹理
                                float bv = bz / cellHeight;
                                ArrayList<LJ3DPoint> lj3DPointArrayList = new ArrayList<>();
                                lj3DPointArrayList.add(new LJ3DPoint(begain.x, begain.y, bz));
                                lj3DPointArrayList.add(new LJ3DPoint(end.x, end.y, bz));
                                lj3DPointArrayList.add(new LJ3DPoint(end.x, end.y, cellHeight));
                                lj3DPointArrayList.add(new LJ3DPoint(begain.x, begain.y, cellHeight));
                                // 纹理
                                float[] texcoords = new float[]{bu, bv, eu, bv, eu, 1.0f, bu, bv, eu, 1.0f, bu, 1.0f};
                                // 创建添加
                                BuildingFragmentWall buildingFragmentWall = new BuildingFragmentWall(lj3DPointArrayList, texcoords);
                                buildingFragmentWallArrayList.add(buildingFragmentWall);
                            }
                        }
                    }
                }
                buildingWall.setBuildingFragmentWallArrayList(buildingFragmentWallArrayList);
            } catch (Exception e) {
                e.printStackTrace();
            }
            refreshRender();
        }

        /**
         * Author by HEKE
         *
         * @time 2018/12/11 16:38
         * TODO: 记录需要切割部位的信息
         */
        private class Anchor {

            public float offGround;
            public float modelsHeight;
            public ArrayList<Point> lepsList;

            public Anchor(float offGround, float modelsHeight, ArrayList<Point> lepsList) {
                this.offGround = offGround;
                this.modelsHeight = modelsHeight;
                this.lepsList = lepsList;
            }

            /**
             * 判断是否相同区域
             *
             * @param pointArrayList
             */
            public boolean equals(ArrayList<Point> pointArrayList) {
                if (pointArrayList == null || pointArrayList.size() == 0 || lepsList == null)
                    return false;
                PointList p1List = new PointList(pointArrayList);
                PointList p2List = new PointList(lepsList);
                return p1List.equals(p2List);
            }

        }

    }

    /**
     * 刷新操作
     */
    private void refreshRender() {
        if (shadowsGLSurfaceView == null)
            return;
        if (RendererState.isNot2D()) {
            shadowsGLSurfaceView.requestRender();
        }
    }
}
