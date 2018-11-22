package com.lejia.mobile.orderking.classes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Base64;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.lejia.mobile.orderking.R;
import com.lejia.mobile.orderking.bases.OrderKingApplication;
import com.lejia.mobile.orderking.hk3d.classes.NetByteArrayIntputStream;
import com.lejia.mobile.orderking.https.KosapRequest;
import com.lejia.mobile.orderking.https.OnKosapResponseListener;
import com.lejia.mobile.orderking.utils.FetchLocal;
import com.lejia.mobile.orderking.utils.FileUtils;
import com.lejia.mobile.orderking.utils.TextUtils;

import java.io.File;
import java.util.HashMap;

/**
 * Author by HEKE
 *
 * @time 2017/3/16 16:09
 * TODO: 拉取材质或模型的贴图等信息
 */
public class FetchLinkUrl implements OnKosapResponseListener {

    public static String FETCH_DEFAULT_ROOT = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Yi3D/Nodes/DefaultXinfos/";
    private static final String REAQUEST_METHOD = "GetMobileDetailBufferFromCode";

    private Context mContext;
    private String code; // 材质或模型编码
    private String localPath; // 本地缓存路径
    private boolean needViews; // 是否需要图片信息

    private OnFetchLinkUrlListener onFetchLinkUrlListener;

    /**
     * 预览图
     */
    private ImageView preview;
    public XInfo xinfo;

    private boolean showSrc;

    /**
     * 外部回调接口
     */
    private OnRGeneralL3DCacheListener onRGeneralL3DCacheListener;

    /**
     * 个人材质
     */
    private boolean isMineTileResource;

    public FetchLinkUrl(Context context, String code, String nodeName, boolean needViews) {
        this.mContext = context;
        this.code = code;
        this.needViews = needViews;
        if (TextUtils.isTextEmpty(nodeName)) {
            this.localPath = FETCH_DEFAULT_ROOT + this.code + ".xinf";
        } else {
            this.localPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Yi3D/Nodes/" + nodeName + "/" + this.code + ".xinf";
        }
        FileUtils.createDirectory(FETCH_DEFAULT_ROOT);
    }

    public FetchLinkUrl(Context context, String code, String nodeName, boolean needViews, boolean isMineTileResource) {
        this.mContext = context;
        this.code = code;
        this.needViews = needViews;
        this.isMineTileResource = isMineTileResource;
        if (TextUtils.isTextEmpty(nodeName)) {
            this.localPath = FETCH_DEFAULT_ROOT + this.code + ".xinf";
        } else {
            this.localPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Yi3D/Nodes/" + nodeName + "/" + this.code + ".xinf";
        }
        FileUtils.createDirectory(FETCH_DEFAULT_ROOT);
    }

    /**
     * 绑定获取结果接口
     */
    public void setOnFetchLinkUrlListener(OnFetchLinkUrlListener onFetchLinkUrlListener) {
        this.onFetchLinkUrlListener = onFetchLinkUrlListener;
    }

    /**
     * 拉取信息
     */
    public void fetch() {
        // 检测本地缓存
        File file = new File(localPath);
        if (file.exists()) {
            loadLocal(localPath, "1");
        } else {
            // 我的材质
            if (isMineTileResource) {
                HashMap<String, String> params = new HashMap<>();
                params.put("userCode", ((OrderKingApplication) mContext.getApplicationContext()).mUser.getToken());
                params.put("code", code);
                new KosapRequest(mContext, "GetUserSpaceMobileDetailBufferFromCode", params, this).request();
            }
            // 普通材质
            else {
                HashMap<String, String> params = new HashMap<>();
                params.put("code", code);
                new KosapRequest(mContext, REAQUEST_METHOD, params, this).request();
            }
        }
    }

    /**
     * 拉取图片显示
     *
     * @param preview
     * @param showSrc
     */
    public void fetch(ImageView preview, boolean showSrc) {
        this.showSrc = showSrc;
        fetch(preview);
    }

    /**
     * 拉取信息
     *
     * @param preview
     */
    public void fetch(ImageView preview) {
        this.preview = preview;
        // 检测本地缓存
        File file = new File(localPath);
        if (file.exists()) {
            loadLocal(localPath, "1");
        } else {
            // 我的材质
            if (isMineTileResource) {
                HashMap<String, String> params = new HashMap<>();
                params.put("userCode", ((OrderKingApplication) mContext.getApplicationContext()).mUser.getToken());
                params.put("code", code);
                new KosapRequest(mContext, "GetUserSpaceMobileDetailBufferFromCode", params, this).request();
            }
            // 普通材质
            else {
                HashMap<String, String> params = new HashMap<>();
                params.put("code", code);
                new KosapRequest(mContext, REAQUEST_METHOD, params, this).request();
            }
        }
    }

    /**
     * 拉取数据并回调至外部
     *
     * @param onRGeneralL3DCacheListener
     */
    public void fetch(OnRGeneralL3DCacheListener onRGeneralL3DCacheListener) {
        this.onRGeneralL3DCacheListener = onRGeneralL3DCacheListener;
        fetch();
    }

    @Override
    public void response(String result, boolean error) {
        if (!error) {
            // 写入缓存
            new FetchLocal(localPath).push(result);
            // 解析
            loadLocal(result, "0");
        } else {
            if (onFetchLinkUrlListener != null)
                onFetchLinkUrlListener.onFetched(true, null);
            // 预览图列表
            if (needViews)
                fetchFailured();
        }
    }

    @Override
    public void useLocal() {
        try {
            // 检测本地缓存
            File file = new File(localPath);
            if (file.exists()) {
                loadLocal(localPath, "1");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 加载本地资源
     *
     * @param localPath 路径
     * @param tag       标志
     */
    @SuppressLint("StaticFieldLeak")
    private void loadLocal(String localPath, String tag) {
        new AsyncTask<String, Integer, XInfo>() {
            @Override
            protected XInfo doInBackground(String... params) {
                if (params[0] == null)
                    return null;
                int flag = Integer.parseInt(params[1]);
                String result = null;
                if (flag == 1) {
                    result = new FetchLocal(params[0]).pull();
                } else {
                    result = params[0];
                }
                //  解析
                XInfo xinfo = new XInfo();
                try {
                    byte[] buffer = Base64.decode(result, Base64.DEFAULT);
                    NetByteArrayIntputStream netBais = new NetByteArrayIntputStream(buffer);
                    xinfo.materialCode = netBais.ReadString();
                    xinfo.materialName = netBais.ReadString();
                    xinfo.type = netBais.ReadInt32();
                    xinfo.catalog = netBais.ReadInt32();
                    xinfo.X = netBais.ReadInt32();
                    xinfo.Y = netBais.ReadInt32();
                    xinfo.Z = netBais.ReadInt32();
                    xinfo.linkUrl = netBais.ReadString();
                    // 需要图片信息，获取图片信息
                    if (needViews) {
                        int previewCount = netBais.ReadInt32();
                        if (previewCount > 0)
                            xinfo.previewBuffer = netBais.ReadBytes(0, previewCount);
                        int topviewCount = netBais.ReadInt32();
                        if (topviewCount > 0)
                            xinfo.topViewBuffer = netBais.ReadBytes(0, topviewCount);
                        // 离地高
                        xinfo.offGround = netBais.ReadInt32();
                        // 读取材质分类
                        xinfo.mode = "" + netBais.ReadInt32();
                    }
                    // 无需图片，但取出数据
                    else {
                        int previewCount = netBais.ReadInt32();
                        if (previewCount > 0)
                            netBais.ReadBytes(0, previewCount);
                        int topviewCount = netBais.ReadInt32();
                        if (topviewCount > 0)
                            netBais.ReadBytes(0, topviewCount);
                        // 离地高
                        xinfo.offGround = netBais.ReadInt32();
                        // 读取材质分类
                        xinfo.mode = "" + netBais.ReadInt32();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return xinfo;
            }

            @Override
            protected void onPostExecute(XInfo xInfo) {
                super.onPostExecute(xInfo);
                // 调用至外部处理数据时
                if (onRGeneralL3DCacheListener != null) {
                    // 调用生成图片异步线程
                    xinfo = xInfo;
                    fetchSuccessed();
                } else {
                    // 模型预览列表
                    if (needViews) {
                        xinfo = xInfo;
                        fetchSuccessed();
                    }
                    // 返回接口数据
                    if (onFetchLinkUrlListener != null)
                        onFetchLinkUrlListener.onFetched(false, xInfo);
                }
            }
        }.execute(localPath, tag);
    }

    /**
     * 拉取材质或模型反馈结果接口
     */
    public interface OnFetchLinkUrlListener {
        void onFetched(boolean error, XInfo xInfo);
    }

    /**
     * 拉取成功
     */
    private void fetchSuccessed() {
        // 当外部回调接口为空时，预览图控件为空返回
        byte[] buffer = null;
        if (onRGeneralL3DCacheListener == null) {
            if (preview == null)
                return;
            buffer = xinfo.previewBuffer;
        } else {
            buffer = xinfo.topViewBuffer;
        }
        // 加载数据
        Glide.with(mContext).asBitmap().load(buffer).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> transition) {
                // 回调至外部操作
                if (onRGeneralL3DCacheListener != null) {
                    onRGeneralL3DCacheListener.onCacheView(bitmap);
                } else {
                    if (preview != null && bitmap != null) {
                        // 设置内部显示
                        if (showSrc) {
                            preview.setImageBitmap(bitmap);
                        }
                        // 设置背景
                        else {
                            preview.setBackground(new BitmapDrawable(mContext.getResources(), bitmap));
                        }
                    }
                }
            }
        });
    }

    /**
     * 拉取失败
     */
    private void fetchFailured() {
        if (preview == null)
            return;
        Glide.with(mContext).load(R.mipmap.unkonw_pic).into(preview);
    }

    public String getCode() {
        return code;
    }

}
