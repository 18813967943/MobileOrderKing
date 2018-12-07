package com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.models;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Base64;

import com.lejia.mobile.orderking.bases.OrderKingApplication;
import com.lejia.mobile.orderking.hk3d.classes.NetByteArrayIntputStream;
import com.lejia.mobile.orderking.hk3d.datas_3d.tools.Scaling;
import com.lejia.mobile.orderking.https.KosapRequest;
import com.lejia.mobile.orderking.https.OnKosapResponseListener;
import com.lejia.mobile.orderking.utils.FetchLocal;
import com.lejia.mobile.orderking.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import geom.utils.TextUtils;

/**
 * Author by HEKE
 *
 * @time 2018/12/4 18:37
 * TODO: 三维模型数据对象
 */
public class L3DFile {

    private String code;

    /**
     * 长
     */
    public int xlong;

    /**
     * 宽
     */
    public int width;

    /**
     * 高
     */
    public int height;

    /**
     * 离地高
     */
    public int highGround;

    /**
     * 名称
     */
    public String l3dName;

    /**
     * 编号
     */
    public String serialNumber;

    /**
     * 子件数量
     */
    public int childNumber;

    public int offBoard;

    /**
     * 子件
     */
    private ArrayList<L3DItemInfo> l3DItemInfoArrayList;
    private boolean loadCompleted;

    public L3DFile(String code) {
        this.code = code;
        this.l3DItemInfoArrayList = new ArrayList<>();
        loadDatas();
    }

    // 网络请求数据
    @SuppressLint("StaticFieldLeak")
    private void loadDatas() {
        final String cachePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Yi3D/models/";
        FileUtils.createDirectory(cachePath);
        final String fileCachePath = cachePath + code + ".l3d";
        File file = new File(fileCachePath);
        if (file.exists()) {
            new FetchLocal(fileCachePath).pullAsync(new FetchLocal.OnAsyncPullListener() {
                @Override
                public void pullCompleted(String contents) {
                    parsel3dFile(contents);
                }
            });
        } else {
            HashMap<String, String> params = new HashMap<>();
            params.put("code", code);
            KosapRequest request = new KosapRequest(OrderKingApplication.getInstant(), "DownloadMobileDataBufferFromCode", params, new OnKosapResponseListener() {
                @Override
                public void response(String result, boolean error) {
                    if (result != null) {
                        new FetchLocal(fileCachePath).push(result);
                        parsel3dFile(result);
                    }
                }

                @Override
                public void useLocal() {
                }
            });
            request.request();
        }
    }

    /**
     * 解析l3d文件
     *
     * @param content
     */
    @SuppressLint("StaticFieldLeak")
    private void parsel3dFile(String content) {
        new AsyncTask<String, Integer, String>() {
            @Override
            protected String doInBackground(String... params) {
                try {
                    // 解析
                    byte[] buffer = Base64.decode(params[0], Base64.DEFAULT);
                    NetByteArrayIntputStream netBais = new NetByteArrayIntputStream(buffer);
                    // 模式
                    int mode = netBais.ReadNormalInt32();
                    switch (mode) {
                        case 0:
                            // 图片
                            int imgBufferLength = netBais.ReadNormalInt32();
                            if (imgBufferLength > 0)
                                netBais.ReadBytes(0, imgBufferLength);
                        case 1:
                            // 详细信息
                            // 长、宽、高、离地高、场外参数
                            xlong = netBais.ReadNormalInt32();
                            width = netBais.ReadNormalInt32();
                            height = netBais.ReadNormalInt32();
                            highGround = netBais.ReadNormalInt32();
                            offBoard = netBais.ReadNormalInt32();
                            l3dName = netBais.ReadString();
                            serialNumber = netBais.ReadString();
                            // 子件数量
                            childNumber = netBais.ReadNormalInt32();
                            for (int i = 0; i < childNumber; i++) {
                                // 读取子件名称
                                String subsetName = netBais.ReadString();
                                if (!TextUtils.isTextEmpity(subsetName)) {
                                    L3DItemInfo item = new L3DItemInfo();
                                    item.textureName = subsetName;
                                    // 读取顶点
                                    int vertexCount = netBais.ReadNormalInt32();
                                    if (vertexCount > 0) {
                                        item.vertexs = new float[vertexCount];
                                        int lengthSize = item.vertexs.length / 3;
                                        for (int j = 0; j < lengthSize; j++) {
                                            int index = 3 * j;
                                            float x = netBais.ReadNormalSingle() * 0.1f;
                                            float y = netBais.ReadNormalSingle() * 0.1f;
                                            float z = netBais.ReadNormalSingle() * 0.1f;
                                            item.vertexs[index] = Scaling.scaleSimpleValue(x);
                                            item.vertexs[index + 1] = Scaling.scaleSimpleValue(z);
                                            item.vertexs[index + 2] = Scaling.scaleSimpleValue(y);
                                        }
                                    }
                                    // 读取法线
                                    int normalCount = netBais.ReadNormalInt32();
                                    if (normalCount > 0) {
                                        item.normals = new float[normalCount];
                                        for (int j = 0; j < normalCount; j++) {
                                            item.normals[j] = netBais.ReadNormalSingle();
                                        }
                                    }
                                    // 读取第一层贴图
                                    int uv0Count = netBais.ReadNormalInt32();
                                    if (uv0Count > 0) {
                                        item.texcoord = new float[uv0Count];
                                        for (int j = 0; j < uv0Count; j++) {
                                            item.texcoord[j] = netBais.ReadNormalSingle();
                                        }
                                    }
                                    // 读取第二层贴图
                                    int uv1Count = netBais.ReadNormalInt32();
                                    if (uv1Count > 0) {
                                        for (int j = 0; j < uv1Count; j++) {
                                            netBais.ReadNormalSingle();
                                        }
                                    }
                                    // 读取索引
                                    int indicesCount = netBais.ReadNormalInt32();
                                    if (indicesCount > 0) {
                                        item.indices = new short[indicesCount];
                                        for (int j = 0; j < indicesCount; j++) {
                                            item.indices[j] = (short) netBais.ReadNormalInt32();
                                        }
                                    }
                                    // 读取透明度
                                    netBais.ReadNormalSingle();
                                    // 读取光照图
                                    int diffuseCount = netBais.ReadNormalInt32();
                                    if (diffuseCount > 0) {
                                        byte[] dbuffer = netBais.ReadBytes(0, diffuseCount);
                                        Bitmap diffuseBitmap = BitmapFactory.decodeByteArray(dbuffer, 0, dbuffer.length);
                                        item.diffuseBitmap = diffuseBitmap;
                                    }
                                    // 其他贴图
                                    int emissionCount = netBais.ReadNormalInt32();
                                    if (emissionCount > 0) {
                                        netBais.ReadBytes(0, emissionCount);
                                    }
                                    // 子件对应的编码和子件编码
                                    item.uuid = code;
                                    item.loadBuffer();
                                    l3DItemInfoArrayList.add(item);
                                }
                            }
                            childNumber = l3DItemInfoArrayList.size();
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loadCompleted = l3DItemInfoArrayList.size() > 0;
            }
        }.execute(content);
    }

    // 获取所有子件对象
    public ArrayList<L3DItemInfo> getL3DItemInfoArrayList() {
        return l3DItemInfoArrayList;
    }

    /**
     * 渲染详细数据
     *
     * @param correspondingMatrix
     * @param positionAttribute
     * @param normalAttribute
     * @param colorAttribute
     * @param onlyPosition
     */
    public void render(CorrespondingMatrix correspondingMatrix, int positionAttribute, int normalAttribute, int colorAttribute, boolean onlyPosition) {
        if (correspondingMatrix == null || !loadCompleted)
            return;
        correspondingMatrix.renderSetMatrixs(l3DItemInfoArrayList.get(0).mRenderer, onlyPosition);
        for (L3DItemInfo l3DItemInfo : l3DItemInfoArrayList) {
            l3DItemInfo.render(positionAttribute, normalAttribute, colorAttribute, onlyPosition);
        }
    }

}
