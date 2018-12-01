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

    public TopView() {
    }

    public TopView(int type, ResUrlNodeXml.ResPath resPath, XInfo xInfo) {
        this.type = type;
        this.resPath = resPath;
        this.xInfo = xInfo;
        this.topviewBitmap = BitmapUtils.createBitmapByXInfo(this.xInfo, xInfo.X / 10, xInfo.Y / 10);
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

}
