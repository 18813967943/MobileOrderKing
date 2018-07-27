package com.lejia.mobile.orderking.utils;

import android.annotation.SuppressLint;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

/**
 * Author by HEKE
 *
 * @time 2017/3/11 14:51
 * TODO: 数据文件处理
 */
public class FetchLocal {

    private String path;

    private boolean isPulling; // 是否正在拉取数据

    private int error; // 错误标志,0为正常，-1为文件找不到(无读取权限)

    /**
     * 避免造成内存泄漏问题
     */
    private OnAsyncPullListener onAsyncPullListener; // 异步拉取回调数据接口

    public FetchLocal(String path) {
        this.path = path;

    }

    /**
     * 写入文件内容
     *
     * @param content
     */
    @SuppressLint("StaticFieldLeak")
    public void push(String content) {
        new AsyncTask<String, Integer, String>() {
            @Override
            protected String doInBackground(String... params) {
                try {
                    // 先获取文件数据流
                    File file = new File(path);
                    if (!file.exists()) {
                        if (file.isDirectory()) {
                            file.setExecutable(true);
                            file.setReadable(true);
                            file.setWritable(true);
                            file.mkdirs();
                        } else {
                            String[] splitorValues = file.getAbsolutePath().split("[//]");
                            String rootPath = "";
                            for (int i = 0; i < splitorValues.length; i++) {
                                int lastIndex = splitorValues.length - 2;
                                if (i == lastIndex) {
                                    rootPath += ("/" + splitorValues[i] + "/");
                                } else if (i < lastIndex) {
                                    if (!TextUtils.isTextEmpity(splitorValues[i]))
                                        rootPath += ("/" + splitorValues[i]);
                                }
                            }
                            File dir = new File(rootPath);
                            if (!dir.exists()) {
                                dir.setExecutable(true);
                                dir.setReadable(true);
                                dir.setWritable(true);
                                dir.mkdirs();
                            }
                            file.setExecutable(true);
                            file.setReadable(true);
                            file.setWritable(true);
                            file.createNewFile();
                        }
                    } else {
                        return null;
                    }
                    // 获取文件数据流
                    FileOutputStream fos = new FileOutputStream(file);
                    fos.write(params[0].getBytes());
                    fos.flush();
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute(content);
    }

    /**
     * 拉取文件内容
     */
    public String pull() {
        String result = "";
        try {
            File file = new File(path);
            if (!file.exists())
                return null;
            FileInputStream fis = new FileInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis, Charset.forName("UTF-8")));
            String lineText = "";
            while ((lineText = reader.readLine()) != null) {
                result += lineText;
            }
            fis.close();
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (result.trim().length() == 0)
                return null;
        }
        return result;
    }

    /**
     * 异步拉取文件内容
     */
    @SuppressLint("StaticFieldLeak")
    public void pullAsync(OnAsyncPullListener onAsyncPullListener) {
        // 文件不存在，返回
        File file = new File(path);
        if (!file.exists())
            return;
        if (!isPulling) {
            this.onAsyncPullListener = onAsyncPullListener;
            isPulling = true;
            new AsyncTask<String, Integer, String>() {
                @Override
                protected String doInBackground(String... strings) {
                    String contents = "";
                    try {
                        File file = new File(path);
                        if (!file.exists())
                            return null;
                        FileInputStream fis = new FileInputStream(file);
                        StringBuffer sBuffer = new StringBuffer();
                        BufferedReader br = new BufferedReader(new InputStreamReader(fis, Charset.forName("UTF-8")));
                        String strLine = null;
                        while ((strLine = br.readLine()) != null) {
                            sBuffer.append(strLine + "\n");
                        }
                        br.close();
                        fis.close();
                        contents = sBuffer.toString();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return contents;
                }

                @Override
                protected void onPostExecute(String s) {
                    super.onPostExecute(s);
                    if (FetchLocal.this.onAsyncPullListener != null)
                        FetchLocal.this.onAsyncPullListener.pullCompleted(s);
                    // 释放
                    isPulling = false;
                }
            }.execute();
        }
    }

    /**
     * 是否正在执行拉取数据任务
     */
    public boolean isPulling() {
        return isPulling;
    }

    /**
     * 异步拉取文件监听接口
     */
    public interface OnAsyncPullListener {
        void pullCompleted(String contents);
    }

}
