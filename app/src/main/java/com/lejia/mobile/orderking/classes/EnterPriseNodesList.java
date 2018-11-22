package com.lejia.mobile.orderking.classes;

import android.content.Context;

import com.google.gson.Gson;
import com.lejia.mobile.orderking.bases.HttpsConfig;
import com.lejia.mobile.orderking.https.OkHttpRequest;
import com.lejia.mobile.orderking.https.ReqCallBack;
import com.lejia.mobile.orderking.httpsResult.ResponseEntity;
import com.lejia.mobile.orderking.httpsResult.classes.LJNodes;
import com.lejia.mobile.orderking.httpsResult.classes.MaterialTypeList;
import com.lejia.mobile.orderking.httpsResult.classes.User;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Author by HEKE
 *
 * @time 2018/7/12 8:54
 * TODO: 获取企业信息对应的服务器节点列表
 * 20181116 去除使用
 */
@Deprecated
public class EnterPriseNodesList {

    private Context mContext;
    private User user;
    private OnEnterpriseNodesListCompeletedListener onEnterpriseNodesListCompeletedListener;

    public EnterPriseNodesList(Context context, User user, OnEnterpriseNodesListCompeletedListener onEnterpriseNodesListCompeletedListener) {
        this.mContext = context;
        this.user = user;
        this.onEnterpriseNodesListCompeletedListener = onEnterpriseNodesListCompeletedListener;
        nodesList();
    }

    /**
     * 请求列表数据
     */
    private void nodesList() {
        HashMap<String, String> params = new HashMap<>();
        params.put("enterpriseID", user.getEnterprise().getId());
        params.put("token", user.getToken());
        OkHttpRequest request = OkHttpRequest.getInstance(mContext);
        request.requestAsyn(HttpsConfig.GET_ENTERPRISE_NODESLIST, OkHttpRequest.TYPE_POST_JSON, params, new ReqCallBack<Object>() {
            @Override
            public void onReqSuccess(Object result) {
                if (result != null) {
                    try {
                        ResponseEntity entity = new ResponseEntity(result);
                        JSONArray array = entity.getJSonArray("materialTypeList");
                        if (array != null && array.length() > 0) {
                            MaterialTypeList materialTypeList = new MaterialTypeList();
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject object = array.getJSONObject(i);
                                LJNodes nodes = new Gson().fromJson(object.toString(), LJNodes.class);
                                materialTypeList.add(nodes);
                            }
                            // 铺砖材质节点列表
                            //((OrderKingApplication) mContext.getApplicationContext()).setMaterialTypeList(materialTypeList, result.toString());
                            // 获取模型节点列表
                            fetchFuritureNodes();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onReqFailed(String errorMsg) {
            }
        });
    }

    /**
     * 请求拉取模型节点列表
     */
    private void fetchFuritureNodes() {
        HashMap<String, String> params = new HashMap<>();
        OkHttpRequest request = OkHttpRequest.getInstance(mContext);
        request.requestAsyn(HttpsConfig.GET_FURNITURE_TYPENODE_DATAS_LIST, OkHttpRequest.TYPE_POST_JSON, params, new ReqCallBack<Object>() {
            @Override
            public void onReqSuccess(Object result) {
                if (result != null) {
                    try {
                        ResponseEntity entity = new ResponseEntity(result);
                        JSONArray array = entity.getJSonArray("modelMaterialRoomTypeList");
                        if (array != null && array.length() > 0) {
                            MaterialTypeList materialTypeList = new MaterialTypeList();
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject object = array.getJSONObject(i);
                                LJNodes nodes = new Gson().fromJson(object.toString(), LJNodes.class);
                                materialTypeList.add(nodes);
                            }
                            // 存入模型节点列表
                            //((OrderKingApplication) mContext.getApplicationContext()).setFurnitureMaterialTypeList(materialTypeList, result.toString());
                            // 拉取大类节点列表
                            fetchCatlogList();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onReqFailed(String errorMsg) {
            }
        });
    }

    /**
     * 拉取大类节点列表
     */
    private void fetchCatlogList() {
        HashMap<String, String> params = new HashMap<>();
        params.put("enterpriseID", user.getEnterprise().getId());
        params.put("token", user.getToken());
        OkHttpRequest request = OkHttpRequest.getInstance(mContext);
        request.requestAsyn(HttpsConfig.GET_FURNITURE_CATLOG_LIST, OkHttpRequest.TYPE_POST_JSON, params, new ReqCallBack<Object>() {
            @Override
            public void onReqSuccess(Object result) {
                try {
                    ResponseEntity entity = new ResponseEntity(result);
                    JSONArray array = entity.getJSonArray("materialTypeList");
                    if (array != null && array.length() > 0) {
                        MaterialTypeList materialTypeList = new MaterialTypeList();
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject object = array.getJSONObject(i);
                            LJNodes nodes = new Gson().fromJson(object.toString(), LJNodes.class);
                            materialTypeList.add(nodes);
                        }
                        // 存储大类节点列表
                        //((OrderKingApplication) mContext.getApplicationContext()).setFurnitureCatlogList(materialTypeList, result.toString());
                        // 回调完成
                        if (onEnterpriseNodesListCompeletedListener != null)
                            onEnterpriseNodesListCompeletedListener.done();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onReqFailed(String errorMsg) {
            }
        });
    }

    /**
     * Author by HEKE
     *
     * @time 2018/7/13 9:22
     * TODO: 回调节点完成状态接口
     */
    public interface OnEnterpriseNodesListCompeletedListener {
        void done();
    }

}
