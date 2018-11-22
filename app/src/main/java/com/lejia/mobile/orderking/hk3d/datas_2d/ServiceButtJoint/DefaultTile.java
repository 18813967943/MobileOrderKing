package com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.lejia.mobile.orderking.bases.OrderKingApplication;
import com.lejia.mobile.orderking.classes.FetchLinkUrl;
import com.lejia.mobile.orderking.classes.XInfo;
import com.lejia.mobile.orderking.utils.BitmapUtils;
import com.lejia.mobile.orderking.utils.TextUtils;

/**
 * Author by HEKE
 *
 * @time 2018/11/19 19:00
 * TODO: 默认铺砖
 */
public class DefaultTile {

    private Context mContext;
    private String defaultCode;

    private XInfo xInfo;
    private Bitmap originBitmap;

    private OnDefaultTilesListener onDefaultTilesListener;

    public DefaultTile() {
        super();
    }

    public DefaultTile(Context context, String defaultCode, OnDefaultTilesListener onDefaultTilesListener) {
        this.mContext = context;
        this.defaultCode = defaultCode;
        this.onDefaultTilesListener = onDefaultTilesListener;
        loadDefaultTile();
    }

    /**
     * 加载默认瓷砖
     */
    private void loadDefaultTile() {
        FetchLinkUrl fetchLinkUrl = new FetchLinkUrl(mContext, defaultCode, null, true);
        fetchLinkUrl.setOnFetchLinkUrlListener(new FetchLinkUrl.OnFetchLinkUrlListener() {
            @Override
            public void onFetched(boolean error, XInfo xInfo) {
                DefaultTile.this.xInfo = xInfo;
                if (onDefaultTilesListener != null) {
                    onDefaultTilesListener.compelet(xInfo, null);
                }
            }
        });
        fetchLinkUrl.fetch();
    }

    public String getDefaultCode() {
        return defaultCode;
    }

    public XInfo getxInfo() {
        return xInfo;
    }

    public Bitmap getOriginBitmap() {
        return originBitmap;
    }

    /**
     * 获取瓷砖对应的基础信息
     *
     * @param tileCode                     瓷砖编码
     * @param nodeName                     来自于的节点名称
     * @param onDefaultTilesListener 回调接口
     */
    public void getTilesXInfo(@NonNull String tileCode, String nodeName, final @NonNull OnDefaultTilesListener onDefaultTilesListener) {
        if (TextUtils.isTextEmpty(tileCode) || onDefaultTilesListener == null)
            return;
        FetchLinkUrl fetchLinkUrl = new FetchLinkUrl(OrderKingApplication.getInstant(), tileCode, nodeName, true);
        fetchLinkUrl.setOnFetchLinkUrlListener(new FetchLinkUrl.OnFetchLinkUrlListener() {
            @Override
            public void onFetched(boolean error, XInfo xInfo) {
                if (onDefaultTilesListener != null) {
                    onDefaultTilesListener.compelet(xInfo, null);
                }
            }
        });
        fetchLinkUrl.fetch();
    }

    /**
     * Author by HEKE
     *
     * @time 2018/11/19 19:16
     * TODO: 回调数据并执行接口
     */
    public interface OnDefaultTilesListener {
        void compelet(XInfo xInfo, Bitmap bitmap);
    }
}
