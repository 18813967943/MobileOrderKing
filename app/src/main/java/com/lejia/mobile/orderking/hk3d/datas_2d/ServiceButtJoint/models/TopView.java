package com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.models;

import android.graphics.Bitmap;

import com.lejia.mobile.orderking.classes.ResUrlNodeXml;
import com.lejia.mobile.orderking.classes.XInfo;
import com.lejia.mobile.orderking.utils.BitmapUtils;
import com.lejia.mobile.orderking.utils.TextUtils;

/**
 * Author by HEKE
 *
 * @time 2018/12/1 16:15
 * TODO: 顶视图数据对象
 */
public class TopView {

    public int type; //大类分类
    public ResUrlNodeXml.ResPath resPath; // 资源路径对象
    public XInfo xInfo; // 模型数据信息对象
    public Bitmap topviewBitmap; // 顶视图位图
    public ADI adi; // 附属信息
    public boolean mirror; // 是否镜像

    public TopView() {
    }

    public TopView(int type, ResUrlNodeXml.ResPath resPath, XInfo xInfo) {
        this.type = type;
        this.resPath = resPath;
        this.xInfo = xInfo;
        this.topviewBitmap = BitmapUtils.createTopViewBitmapByXInfo(this.xInfo, xInfo.X, xInfo.Y);
    }

    /**
     * 复制
     */
    public TopView copy() {
        TopView topView = new TopView();
        topView.type = type;
        topView.xInfo = xInfo.copy();
        topView.adi = adi.copy();
        topView.topviewBitmap = topviewBitmap;
        topView.resPath = resPath;
        topView.mirror = mirror;
        return topView;
    }

    /**
     * 数据资源编码比对
     *
     * @param code 模型唯一编码
     * @return
     */
    public boolean codeEquals(String code) {
        if (TextUtils.isTextEmpty(code) || xInfo == null)
            return false;
        return code.equals(xInfo.materialCode);
    }

    /**
     * 释放数据
     */
    public void release() {
        if (xInfo != null) {
            xInfo.topViewBuffer = null;
            xInfo.previewBuffer = null;
            xInfo = null;
        }
        if (topviewBitmap != null && !topviewBitmap.isRecycled()) {
            topviewBitmap.recycle();
            topviewBitmap = null;
        }
        adi = null;
    }

}
