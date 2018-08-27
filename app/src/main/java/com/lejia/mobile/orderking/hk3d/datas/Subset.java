package com.lejia.mobile.orderking.hk3d.datas;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Environment;

import com.lejia.mobile.orderking.bases.OrderKingApplication;
import com.lejia.mobile.orderking.https.OkHttpRequest;
import com.lejia.mobile.orderking.https.ReqCallBack;
import com.lejia.mobile.orderking.utils.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

/**
 * Author by HEKE
 *
 * @time 2018/8/20 16:00
 * TODO: 模型渲染子件
 */
public class Subset extends RendererObject {

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

    public Subset(String materialCode, String fileUrl) {
        this.materialCode = materialCode;
        this.fileUrl = fileUrl;
        load();
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
        }
        // 网络拉取
        else {
            new AsyncTask<String, Integer, String>() {
                @Override
                protected String doInBackground(String... strings) {
                    OkHttpRequest request = OkHttpRequest.getInstance(OrderKingApplication.getInstant());
                    request.downLoadFile(fileUrl, strings[0], new ReqCallBack<File>() {
                        @Override
                        public void onReqSuccess(File result) {
                            datasFile = result;
                        }

                        @Override
                        public void onReqFailed(String errorMsg) {
                        }
                    });
                    return null;
                }
            }.execute(cachePath);
        }
    }

    public File getDatasFile() {
        return datasFile;
    }

    // TODO 解析文件
    @SuppressLint("StaticFieldLeak")
    public void parseFile(File file) {
        if (file == null)
            return;
        new AsyncTask<File, Integer, String>() {
            @Override
            protected String doInBackground(File... files) {
                if (files == null)
                    return null;
                try {
                    FileInputStream fis = new FileInputStream(files[0]);
                    InputStreamReader isr = new InputStreamReader(fis);
                    StringBuilder sb = new StringBuilder();
                    int size = 4096;
                    char[] buffer = null;

                    isr.close();
                    fis.close();
                    return null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute(file);
    }

    @Override
    public void render(int positionAttribute, int normalAttribute, int colorAttribute, boolean onlyPosition) {

    }

}
