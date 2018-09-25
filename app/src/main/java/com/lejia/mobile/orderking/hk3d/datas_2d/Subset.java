package com.lejia.mobile.orderking.hk3d.datas_2d;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Environment;

import com.lejia.mobile.orderking.bases.OrderKingApplication;
import com.lejia.mobile.orderking.hk3d.classes.NetByteArrayIntputStream;
import com.lejia.mobile.orderking.https.OkHttpRequest;
import com.lejia.mobile.orderking.https.ReqCallBack;
import com.lejia.mobile.orderking.utils.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

/**
 * Author by HEKE
 *
 * @time 2018/8/20 16:00
 * TODO: 模型渲染子件
 */
public class Subset {

    /**
     * 子件缓存根路径
     */
    public static final String CACHE_ROOT_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/LeJia2/";

    private String materialCode; // 模型编码
    private String fileUrl; // 文件网络路径

    /**
     * 指向对应缓存路径的文件
     */
    private File datasFile;

    /**
     * 子件数据对象列表
     */
    private ArrayList<SubsetView> subsetViewsList;

    public Subset(String materialCode, String fileUrl) {
        this.materialCode = materialCode;
        this.fileUrl = fileUrl;
        this.subsetViewsList = new ArrayList<>();
        load();
    }

    /**
     * 当前模型子件的编码
     */
    public String getMaterialCode() {
        return materialCode;
    }

    // 初步加载
    @SuppressLint("StaticFieldLeak")
    private void load() {
        if (TextUtils.isTextEmpity(fileUrl)) {
            return;
        }
        String dirPath = CACHE_ROOT_PATH + "subsets/";
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.setReadable(true);
            dir.setWritable(true);
            dir.mkdirs();
        }
        String cachePath = dirPath + "" + materialCode + ".l3d";
        File file = new File(cachePath);
        // 本地读取
        if (file.exists()) {
            datasFile = file;
            parseFile(datasFile);
        }
        // 网络拉取
        else {
            OkHttpRequest request = OkHttpRequest.getInstance(OrderKingApplication.getInstant());
            request.downLoadFile(fileUrl, cachePath, new ReqCallBack<File>() {
                @Override
                public void onReqSuccess(File result) {
                    datasFile = result;
                    parseFile(datasFile);
                }

                @Override
                public void onReqFailed(String errorMsg) {
                }
            });
        }
    }

    public File getDatasFile() {
        return datasFile;
    }

    // TODO 解析文件
    @SuppressLint("StaticFieldLeak")
    public void parseFile(final File file) {
        if (file == null)
            return;
        new AsyncTask<File, Integer, ArrayList<SubsetView>>() {
            @Override
            protected ArrayList<SubsetView> doInBackground(File... files) {
                if (files == null)
                    return null;
                subsetViewsList.clear();
                ArrayList<SubsetView> subsetViews = new ArrayList<>();
                try {
                    FileInputStream fis = new FileInputStream(files[0]);
                    byte[] buffer = new byte[fis.available()];
                    fis.read(buffer, 0, buffer.length);
                    NetByteArrayIntputStream nbis = new NetByteArrayIntputStream(buffer);
                    fis.close();
                    // 读取子件数量
                    int itemCount = nbis.ReadInt32();
                    // 循环读取子件数据
                    if (itemCount > 0) {
                        for (int i = 0; i < itemCount; i++) {
                            // 子件编号
                            int id = nbis.ReadInt32();
                            // 材质编号类型
                            int type = nbis.ReadInt32();
                            // 顶点
                            String vertexs = nbis.ReadString();
                            // UV0纹理
                            String uv0 = nbis.ReadString();
                            // UV1纹理
                            String uv1 = nbis.ReadString();
                            // 光照贴图
                            String texture0 = nbis.ReadString();
                            // 法线贴图
                            String texture1 = nbis.ReadString();
                            // 法线
                            String normals = nbis.ReadString();
                            // 索引
                            String indices = nbis.ReadString();
                            // 创建子件对象
                            subsetViews.add(new SubsetView(id, type, vertexs, uv0, uv1, texture0,
                                    texture1, normals, indices, (materialCode + "Item" + i)));
                        }
                    }
                    nbis.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return subsetViews;
            }

            @Override
            protected void onPostExecute(ArrayList<SubsetView> ret) {
                super.onPostExecute(ret);
                // 加载材质贴图
                if (ret != null && ret.size() > 0) {
                    subsetViewsList.addAll(ret);
                }
                if (subsetViewsList != null && subsetViewsList.size() > 0) {
                    for (SubsetView subsetView : subsetViewsList) {
                        subsetView.loadTexture();
                    }
                }
            }
        }.execute(file);
    }

    /**
     * 获取详细子件核心数据对象列表
     */
    public ArrayList<SubsetView> getSubsetViewsList() {
        return subsetViewsList;
    }

    /**
     * 渲染
     *
     * @param furnitureMatrixs 模型对应矩阵
     */
    public void render(FurnitureMatrixs furnitureMatrixs, int positionAttribute, int normalAttribute, int colorAttribute, boolean onlyPosition) {
        if (subsetViewsList != null && subsetViewsList.size() > 0) {
            for (int i = 0; i < subsetViewsList.size(); i++) {
                if (i < subsetViewsList.size()) {
                    SubsetView subsetView = subsetViewsList.get(i);
                    subsetView.render(furnitureMatrixs, positionAttribute, normalAttribute, colorAttribute, onlyPosition);
                }
            }
        }
    }

}
