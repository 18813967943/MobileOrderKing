package com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint;

import android.graphics.Bitmap;

import com.lejia.mobile.orderking.classes.XInfo;
import com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.classes.PhyLogicalPackage;
import com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.classes.TilePlan;

import java.util.ArrayList;

/**
 * Author by HEKE
 *
 * @time 2018/11/21 14:24
 * TODO: 波打线、样式、方案砖等复杂铺砖数据资源加载器线程对象
 */
public class ResourceLoader extends Thread {

    /**
     * 需要加载资源的铺砖计划对象列表
     */
    private ArrayList<TilePlan> tilePlanArrayList;

    /**
     * 用于运算的数据包列表
     */
    private ArrayList<PhyLogicalPackage> phyLogicalPackageArrayList;

    private String nodeName; // 需要指定资源缓存目录的文件夹名称
    private OnResourceLoaderCompletedListener onResourceLoaderCompletedListener;

    /**
     * 数据加载及技术
     */
    private int size;
    private int count;

    /**
     * 强制中断操作
     */
    protected boolean requestForceInterrupt;

    private boolean running; // 是否启动加载程序
    private boolean completed; // 是否完成操作

    private void init() {
        if (tilePlanArrayList == null || tilePlanArrayList.size() == 0)
            return;
        phyLogicalPackageArrayList = new ArrayList<>();
        for (TilePlan tilePlan : tilePlanArrayList) {
            if (tilePlan.phyLogicalPackageArrayList != null && tilePlan.phyLogicalPackageArrayList.size() > 0) {
                phyLogicalPackageArrayList.addAll(tilePlan.phyLogicalPackageArrayList);
            }
        }
        size = phyLogicalPackageArrayList.size();
        count = 0;
    }

    public ResourceLoader(ArrayList<TilePlan> tilePlanArrayList, String nodeName, OnResourceLoaderCompletedListener onResourceLoaderCompletedListener) {
        this.tilePlanArrayList = tilePlanArrayList;
        this.nodeName = nodeName;
        this.onResourceLoaderCompletedListener = onResourceLoaderCompletedListener;
        init();
    }

    // 执行中断操作
    public void RequestForceInterrupt() {
        this.requestForceInterrupt = true;
    }

    @Override
    public void run() {
        super.run();
        try {
            while (true) {
                // 延迟20毫秒检测一次
                Thread.sleep(20);
                // 强制中断程序
                if (requestForceInterrupt) {
                    requestForceInterrupt = false;
                    // 回调完成
                    if (onResourceLoaderCompletedListener != null) {
                        onResourceLoaderCompletedListener.compeleted(!completed);
                    }
                    break;
                }
                // 遍历循环加载
                if (!running) {
                    running = true;
                    final PhyLogicalPackage phyLogicalPackage = phyLogicalPackageArrayList.get(count);
                    new DefaultTile().getTilesXInfo(phyLogicalPackage.tile.codeNum, nodeName, new DefaultTile.OnDefaultTilesListener() {
                        @Override
                        public void compelet(XInfo xInfo, Bitmap bitmap) {
                            // 存入返回数据列表
                            phyLogicalPackage.xInfo = xInfo;
                            // 打开执行标签，记录下移
                            count++;
                            if (count >= size) {
                                completed = true;
                                RequestForceInterrupt();
                            } else {
                                running = false;
                            }
                        }
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Author by HEKE
     *
     * @time 2018/11/21 15:26
     * TODO: 资源加载完成状态监听接口
     */
    public interface OnResourceLoaderCompletedListener {
        void compeleted(boolean isInterrupted);
    }

}
