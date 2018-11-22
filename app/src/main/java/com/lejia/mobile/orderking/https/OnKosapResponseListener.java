package com.lejia.mobile.orderking.https;

/**
 * @auther HEKE
 * create at 2017/1/20 , 10:34
 * todo : 回调WebService请求结果
 */
public interface OnKosapResponseListener {
    void response(String result, boolean error);
    void useLocal();
}
