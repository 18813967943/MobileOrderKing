package com.lejia.mobile.orderking.classes;

import android.content.Context;

import com.google.gson.Gson;
import com.lejia.mobile.orderking.bases.HttpsConfig;
import com.lejia.mobile.orderking.bases.OrderKingApplication;
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
 */
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
                            ((OrderKingApplication) mContext.getApplicationContext()).setMaterialTypeList(materialTypeList, result.toString());
                            if (onEnterpriseNodesListCompeletedListener != null)
                                onEnterpriseNodesListCompeletedListener.done();
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
     * Author by HEKE
     *
     * @time 2018/7/13 9:22
     * TODO: 回调节点完成状态接口
     */
    public interface OnEnterpriseNodesListCompeletedListener {
        void done();
    }

}
