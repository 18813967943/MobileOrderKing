package com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.bridge;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Handler;
import android.widget.Toast;

import com.lejia.mobile.orderking.dialogs.WaitBar;
import com.lejia.mobile.orderking.hk3d.activity_partitation.Designer3DManager;
import com.lejia.mobile.orderking.hk3d.datas_2d.House;
import com.lejia.mobile.orderking.hk3d.datas_2d.HouseDatasManager;
import com.lejia.mobile.orderking.utils.BitmapUtils;

import java.util.ArrayList;

/**
 * Author by HEKE
 *
 * @time 2018/11/24 11:01
 * TODO: 保存操作对象
 */
public class SaveDatasToService {

    private Context mContext;
    private Designer3DManager designer3DManager;
    private HouseDatasManager houseDatasManager;

    private Bitmap previewBmp; // 预览图

    /**
     * 创建提示窗口
     */
    private WaitBar waitBar;

    /**
     * 所有房间列表
     */
    ArrayList<House> houseArrayList;

    public SaveDatasToService(Context context, Designer3DManager designer3DManager, HouseDatasManager houseDatasManager) {
        this.mContext = context;
        this.designer3DManager = designer3DManager;
        this.houseDatasManager = houseDatasManager;
        this.waitBar = new WaitBar(context);
        this.waitBar.setWindow(true);
        preview();
    }

    /**
     * 创建上传数据
     */
    @SuppressLint("StaticFieldLeak")
    private void createUpDatas() {
        // 创建xml数据
        new AsyncTask<String, Integer, Integer>() {
            @Override
            protected Integer doInBackground(String... params) {
                try {

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Integer r) {
                super.onPostExecute(r);
                waitBar.setText("创建完成，请输入命名后提交保存!");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        waitBar.hide();
                        // 弹出输入命名窗口

                    }
                }, 500);
            }
        }.execute();
    }

    /**
     * 预览图
     */
    private void preview() {
        try {
            // 获取房间数据
            houseArrayList = houseDatasManager.getHousesList();
            // 无绘制方案，不进行操作
            if (houseArrayList == null || houseArrayList.size() == 0) {
                Toast.makeText(mContext, "请先设计方案！", Toast.LENGTH_SHORT).show();
                return;
            }
            this.waitBar.show();
            waitBar.setText("创建预览图中...");
            // 三维截图
            designer3DManager.getDesigner3DRender().readPixs(new OnReadPixsListener() {

                @SuppressLint("StaticFieldLeak")
                @Override
                public void complelted(Bitmap bitmap) {
                    new AsyncTask<Bitmap, Integer, Bitmap>() {
                        @Override
                        protected Bitmap doInBackground(Bitmap... bitmaps) {
                            Bitmap ret = BitmapUtils.toSize(bitmaps[0], -1, 512);
                            return ret;
                        }

                        @Override
                        protected void onPostExecute(Bitmap bitmap) {
                            super.onPostExecute(bitmap);
                            previewBmp = bitmap;
                            waitBar.setText("创建房间数据中...");
                            createUpDatas();
                        }
                    }.execute(bitmap);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
