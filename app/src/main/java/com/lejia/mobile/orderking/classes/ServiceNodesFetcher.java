package com.lejia.mobile.orderking.classes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;

import com.lejia.mobile.orderking.https.KosapRequest;
import com.lejia.mobile.orderking.https.OnKosapResponseListener;
import com.lejia.mobile.orderking.httpsResult.classes.LJNodes;
import com.lejia.mobile.orderking.utils.FetchLocal;
import com.lejia.mobile.orderking.utils.FileUtils;
import com.lejia.mobile.orderking.utils.TextUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Author by HEKE
 *
 * @time 2018/11/16 10:43
 * TODO: 服务器资源节点获取对象
 */
public class ServiceNodesFetcher {

    /**
     * 请求服务资源节点根节点名称
     */
    private static final String SERVICE_NAME = "LJ_移动端";

    private Context mContext;
    private String cachePath;
    private String errorLogFilePath;

    /**
     * 根节点，服务器资源起始节点(公司名称)
     */
    private LJNodes rootServiceNode;

    private OnServiceNodesFetchedListener onServiceNodesFetchedListener;

    public ServiceNodesFetcher(Context context, OnServiceNodesFetchedListener onServiceNodesFetchedListener) {
        mContext = context;
        this.onServiceNodesFetchedListener = onServiceNodesFetchedListener;
        cachePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Yi3D/Service/";
        FileUtils.createDirectory(cachePath);
        errorLogFilePath = cachePath + "error.log";
        fetch();
    }

    /**
     * 拉取服务器资源节点信息
     */
    @SuppressLint("StaticFieldLeak")
    private void fetch() {
        final String fileCachePath = cachePath + SERVICE_NAME + ".nodes";
        File file = new File(fileCachePath);
        // 如果本地存在则直接读取本地数据
        if (file.exists()) {
            new FetchLocal(fileCachePath).pullAsync(new FetchLocal.OnAsyncPullListener() {
                @Override
                public void pullCompleted(String contents) {
                    // 解析数据
                    parserNodes(contents);
                }
            });
        } else {
            // 参数
            HashMap<String, String> params = new HashMap<>();
            params.put("companyName", SERVICE_NAME);
            // 请求节点信息
            new KosapRequest(mContext, "DownloadRootNodeXMLByCompanyName", params, new OnKosapResponseListener() {
                @Override
                public void response(String result, boolean error) {
                    if (error) {
                        if (onServiceNodesFetchedListener != null) {
                            final String errorInfo = "服务器资源连接错误，网络波动异常导致!";
                            new FetchLocal(errorLogFilePath).pushAsync(errorInfo, new FetchLocal.OnAsyncPushListener() {
                                @Override
                                public void pushCompleted() {
                                    onServiceNodesFetchedListener.fetchStatues(true, errorInfo);
                                }
                            });
                        }
                    } else {
                        if (!TextUtils.isTextEmpty(result)) {
                            // 存储缓存
                            new FetchLocal(fileCachePath).push(result);
                            // 解析数据
                            parserNodes(result);
                        }
                    }
                }

                @Override
                public void useLocal() {
                    // 从网络获取失败，并且本地亦没有缓存文件，返回加载错误
                    if (onServiceNodesFetchedListener != null) {
                        final String errorInfo = "网络异常，请确保连接的网络可用";
                        new FetchLocal(errorLogFilePath).pushAsync(errorInfo, new FetchLocal.OnAsyncPushListener() {
                            @Override
                            public void pushCompleted() {
                                onServiceNodesFetchedListener.fetchStatues(true, errorInfo);
                            }
                        });
                    }
                }
            }).request();
        }
    }

    /**
     * 解析服务节点
     */
    @SuppressLint("StaticFieldLeak")
    private void parserNodes(String contents) {
        new AsyncTask<String, Integer, String>() {

            @Override
            protected String doInBackground(String... params) {
                //1、创建文档对象工厂实例
                DocumentBuilderFactory mDocumentBuilderFactory = DocumentBuilderFactory.newInstance();
                try {
                    //2、调用DocumentBuilderFactory中的newDocumentBuilder()方法创建文档对象构造器
                    DocumentBuilder mDocumentBuilder = mDocumentBuilderFactory.newDocumentBuilder();
                    //3、将文件流解析成XML文档对象
                    Document mDocument = mDocumentBuilder.parse(new ByteArrayInputStream(params[0].getBytes()));
                    //4、使用mDocument文档对象得到文档根节点
                    Element mElement = mDocument.getDocumentElement();
                    //5、根据名称获取根节点中的子节点列表
                    NodeList mNodeList = mElement.getChildNodes();
                    // 所有有效节点
                    ArrayList<Element> elementsList = new ArrayList<>();
                    // 遍历查询
                    if (mNodeList != null) {
                        for (int i = 0; i < mNodeList.getLength(); i++) {
                            // 获取所有有效节点
                            if (mNodeList.item(i).getNodeType() == Node.ELEMENT_NODE) {
                                Element element = (Element) mNodeList.item(i);
                                if (element.hasAttributes()) {
                                    elementsList.add(element);
                                }
                            }
                        }
                        if (elementsList == null || elementsList.size() == 0) {
                            if (onServiceNodesFetchedListener != null) {
                                final String errorInfo = "该服务业务不支持，请联系客服开通！";
                                new FetchLocal(errorLogFilePath).pushAsync(errorInfo, new FetchLocal.OnAsyncPushListener() {
                                    @Override
                                    public void pushCompleted() {
                                        onServiceNodesFetchedListener.fetchStatues(true, errorInfo);
                                    }
                                });

                            }
                        } else {
                            for (Element element : elementsList) {
                                if (element.hasAttributes()) {
                                    String label = element.getAttribute("label");
                                    String id = element.getAttribute("id");
                                    if (SERVICE_NAME.equals(label)) {
                                        rootServiceNode = new LJNodes(label, id);
                                        rootServiceNode.setElement(element);
                                        break;
                                    }
                                }
                            }
                        }
                        // 加载完成
                        if (rootServiceNode != null) {
                            if (onServiceNodesFetchedListener != null)
                                onServiceNodesFetchedListener.fetchStatues(false, null);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute(contents);
    }

    /**
     * 根据节点名称获取指定父节点下的子节点
     *
     * @param parent    父节点
     * @param childName 子节点名称
     * @return
     */
    public LJNodes getChildNodeByNameFromParentNode(LJNodes parent, String childName) {
        if (parent == null || TextUtils.isTextEmpty(childName))
            return null;
        ArrayList<LJNodes> nodesArrayList = parent.getChildNodesList();
        if (nodesArrayList == null || nodesArrayList.size() == 0)
            return null;
        LJNodes childNode = null;
        for (LJNodes ljNodes : nodesArrayList) {
            if (childName.equals(ljNodes.getLabel())) {
                childNode = ljNodes;
                break;
            }
        }
        return childNode;
    }

    /**
     * 获取空间类型模型详细节点列表
     *
     * @return
     */
    public ArrayList<LJNodes> getModelsTypeNodesList() {
        if (rootServiceNode == null)
            return null;
        ArrayList<LJNodes> ljNodesArrayList = new ArrayList<>();
        try {
            LJNodes modelsRootNode = getChildNodeByNameFromParentNode(rootServiceNode, "产品模型");
            if (modelsRootNode == null)
                return null;
            ljNodesArrayList.add(getChildNodeByNameFromParentNode(modelsRootNode, "客餐厅"));
            ljNodesArrayList.add(getChildNodeByNameFromParentNode(modelsRootNode, "卧室"));
            ljNodesArrayList.add(getChildNodeByNameFromParentNode(modelsRootNode, "厨房"));
            ljNodesArrayList.add(getChildNodeByNameFromParentNode(modelsRootNode, "卫生间"));
            ljNodesArrayList.add(getChildNodeByNameFromParentNode(modelsRootNode, "小孩房"));
            ljNodesArrayList.add(getChildNodeByNameFromParentNode(modelsRootNode, "书房"));
            ljNodesArrayList.add(getChildNodeByNameFromParentNode(modelsRootNode, "其他"));
            ljNodesArrayList.add(getChildNodeByNameFromParentNode(modelsRootNode, "外景"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ljNodesArrayList;
    }

    /**
     * 获取门节点
     *
     * @return
     */
    public LJNodes getDoorsNode() {
        LJNodes modelsRootNode = getChildNodeByNameFromParentNode(rootServiceNode, "产品模型");
        if (modelsRootNode == null)
            return null;
        LJNodes dw = getChildNodeByNameFromParentNode(modelsRootNode, "门窗");
        return getChildNodeByNameFromParentNode(dw, "门");
    }

    /**
     * 获取窗节点
     *
     * @return
     */
    public LJNodes getWindowsNode() {
        LJNodes modelsRootNode = getChildNodeByNameFromParentNode(rootServiceNode, "产品模型");
        if (modelsRootNode == null)
            return null;
        LJNodes dw = getChildNodeByNameFromParentNode(modelsRootNode, "门窗");
        return getChildNodeByNameFromParentNode(dw, "窗");
    }

    /**
     * 获取吊顶节点
     *
     * @return
     */
    public LJNodes getTHNode() {
        LJNodes modelsRootNode = getChildNodeByNameFromParentNode(rootServiceNode, "产品模型");
        if (modelsRootNode == null)
            return null;
        LJNodes parentNode = getChildNodeByNameFromParentNode(modelsRootNode, "吊顶");
        if (parentNode == null)
            return null;
        return getChildNodeByNameFromParentNode(parentNode, "天花");
    }

    /**
     * 获取墙面铺砖节点
     *
     * @return
     */
    public LJNodes getWallTilesNode() {
        LJNodes tileNode = getChildNodeByNameFromParentNode(rootServiceNode, "铺砖");
        if (tileNode == null)
            return null;
        return getChildNodeByNameFromParentNode(tileNode, "墙砖");
    }

    /**
     * 获取地面及顶面铺砖节点
     *
     * @return
     */
    public LJNodes getGroundAndRoofTilesNode() {
        LJNodes tileNode = getChildNodeByNameFromParentNode(rootServiceNode, "铺砖");
        if (tileNode == null)
            return null;
        return getChildNodeByNameFromParentNode(tileNode, "地砖");
    }

    /**
     * 获取基础户型节点
     *
     * @return
     */
    public LJNodes getBaseHouseTypeNode() {
        return getChildNodeByNameFromParentNode(rootServiceNode, "户型");
    }

    /**
     * 获取一键布置方案节点
     *
     * @return
     */
    public LJNodes getOneKeylayoutNode() {
        return getChildNodeByNameFromParentNode(rootServiceNode, "方案");
    }

    /**
     * Author by HEKE
     *
     * @time 2018/11/16 11:22
     * TODO: 回调服务器资源加载进度及结果接口
     */
    public interface OnServiceNodesFetchedListener {
        void fetchStatues(boolean error, String errorInfo);
    }

}
