package com.lejia.mobile.orderking.classes;

import android.annotation.SuppressLint;
import android.os.AsyncTask;

import com.lejia.mobile.orderking.utils.TextUtils;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Author by HEKE
 *
 * @time 2018/11/17 10:11
 * TODO: 资源节点详细数据列表创建与解析XML文件
 */
public class ResUrlNodeXml {

    private String nodeName;
    private OnResUrlNodeXmlParseListener onResUrlNodeXmlParseListener;

    public ResUrlNodeXml(String xml, String nodeName, OnResUrlNodeXmlParseListener onResUrlNodeXmlParseListener) {
        this.nodeName = nodeName;
        this.onResUrlNodeXmlParseListener = onResUrlNodeXmlParseListener;
        loadXml(xml);
    }

    // 加载
    @SuppressLint("StaticFieldLeak")
    private void loadXml(String xml) {
        new AsyncTask<String, Integer, ArrayList<ResPath>>() {
            @Override
            protected ArrayList<ResPath> doInBackground(String... params) {
                ArrayList<ResPath> urlList = new ArrayList<>();
                try {
                    SAXReader saxReader = new SAXReader();
                    ByteArrayInputStream bais = new ByteArrayInputStream(params[0].getBytes());
                    Document document = saxReader.read(bais);
                    Element rootElement = document.getRootElement();
                    List<Element> list = rootElement.elements();
                    for (Element element : list) {
                        String name = element.getName();
                        if (name.equals("url")) {
                            String path = element.attributeValue("path");
                            ResPath resPath = new ResPath(path);
                            resPath.nodeName = nodeName;
                            urlList.add(resPath);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return urlList;
            }

            @Override
            protected void onPostExecute(ArrayList<ResPath> resPathArrayList) {
                super.onPostExecute(resPathArrayList);
                if (onResUrlNodeXmlParseListener != null)
                    onResUrlNodeXmlParseListener.onParse(resPathArrayList);
            }
        }.execute(xml);
    }

    /**
     * Author by HEKE
     *
     * @time 2018/11/17 10:43
     * TODO: 服务器资源路径对象
     */
    public static class ResPath {

        public String path;
        public String fileName;
        public String name;
        public String nodeName;

        public NormalDatas normalDatas; // 记录对应的瓷砖数据对象

        public ResPath(String path) {
            this.path = path;
            if (path != null) {
                String[] splitor = path.split("[\\\\]");
                this.fileName = splitor[splitor.length - 1];
                this.name = this.fileName.split("[.]")[0];
            }
        }

        /**
         * 静态创建内部路径类
         *
         * @param path
         */
        public static ResPath createResPath(String path) {
            if (TextUtils.isTextEmpty(path))
                return null;
            return new ResPath(path);
        }

        @Override
        public boolean equals(Object obj) {
            return path.equals(obj.toString());
        }

        @Override
        public String toString() {
            return path;
        }
    }

    /**
     * Author by HEKE
     *
     * @time 2018/11/17 10:15
     * TODO: 回调操作结果接口
     */
    public interface OnResUrlNodeXmlParseListener {
        void onParse(ArrayList<ResPath> resPathArrayList);
    }

}
