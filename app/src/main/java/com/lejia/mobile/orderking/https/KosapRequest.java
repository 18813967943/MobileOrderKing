package com.lejia.mobile.orderking.https;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.lejia.mobile.orderking.utils.NetworkUtils;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @auther HEKE
 * create at 2017/1/20 , 10:30
 * todo : 封装WebService调用请求
 */
public class KosapRequest {

    /**
     * 访问.NET 服务器的命名空间
     */
    public static final String NAMESPACE = "http://tempuri.org/";

    /**
     * 本地WebService服务调用地址
     */
    // public static String SOAP_URL = "http://192.168.1.226:3820/LejiaWebService.asmx?wsdl";

    /**
     * 网络WebService服务调用地址
     */
    public static String SOAP_URL = "http://www.lejia3d.com:3820/LejiaWebService.asmx?wsdl";

    // public static String SOAP_URL = "http://125.88.152.119:3820/LejiaWebService.asmx?wsdl";

    /**
     * 超时时间，默认五分钟,单位ms
     */
    public static int TIME_OUT = 150000;

    private Context mContext;
    private String requestMethod;
    private HashMap<String, String> params;
    private OnKosapResponseListener onKosapResponseListener;
    private boolean error;
    private boolean requestCompleted; // 请求结束

    /**
     * 带参构造函数
     *
     * @param context
     * @param requestMethod           请求方法名
     * @param params                  参数
     * @param onKosapResponseListener 结果接口
     */
    public KosapRequest(Context context, String requestMethod, HashMap<String, String> params, OnKosapResponseListener onKosapResponseListener) {
        this.mContext = context;
        this.requestMethod = requestMethod;
        this.params = params;
        this.onKosapResponseListener = onKosapResponseListener;
    }

    /**
     * 使用本地服务器，默认为外网服务器
     */
    public KosapRequest useLocalServer() {
        SOAP_URL = "http://192.168.1.220:3820/LejiaWebService.asmx?wsdl";
        return this;
    }

    /**
     * 开启请求,方法名不能为空，回调接口不能为空，否则无效处理
     */
    @SuppressLint("StaticFieldLeak")
    public void request() {
        if (requestMethod == null || onKosapResponseListener == null)
            return;
        // 无法上网情况，直接加载本地缓存
        if (!NetworkUtils.isNetworkConnected(mContext)) {
            if (onKosapResponseListener != null)
                onKosapResponseListener.useLocal();
            return;
        }
        new AsyncTask<String, Integer, String>() {
            @Override
            protected String doInBackground(String... strings) {
                String result = CallWebService(requestMethod, KosapRequest.this.params);
                return result;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if (s != null) {
                    if (onKosapResponseListener != null)
                        onKosapResponseListener.response(s, false);
                }
            }
        }.execute();
    }

    /**
     * 调用WebService
     *
     * @return WebService的返回值
     */
    @SuppressWarnings("rawtypes")
    private String CallWebService(String MethodName, Map<String, String> Params) {
        try {
            // 1、指定webservice的命名空间和调用的方法名
            SoapObject request = new SoapObject(NAMESPACE, MethodName);
            // 2、设置调用方法的参数值，如果没有参数，可以省略，
            if (Params != null) {
                Iterator iter = Params.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry entry = (Map.Entry) iter.next();
                    request.addProperty((String) entry.getKey(), (String) entry.getValue());
                }
            }
            // 3、生成调用Webservice方法的SOAP请求信息。该信息由SoapSerializationEnvelope对象描述
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
            envelope.bodyOut = request;
            // c#写的应用程序必须加上这句
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);
            HttpTransportSE ht = new HttpTransportSE(SOAP_URL, TIME_OUT);
            // 使用call方法调用WebService方法
            try {
                ht.call(NAMESPACE + requestMethod, envelope);
            } catch (Exception e) {
                e.printStackTrace();
                requestCompleted = true;
                error();
            }
            try {
                if (envelope != null) {
                    Object o = envelope.getResponse();
                    String result = null;
                    if (o != null)
                        result = o.toString();
                    // 断开连接
                    ht.getServiceConnection().disconnect();
                    // 判断结果
                    if (result != null) {
                        requestCompleted = true;
                        return result;
                    }
                }
            } catch (Exception e) {
                Log.e("----发生错误---", e.getMessage());
                e.printStackTrace();
                requestCompleted = true;
                error();
            }
        } catch (Exception e) {
            e.printStackTrace();
            requestCompleted = true;
            error();
        }
        return null;
    }

    /**
     * 调用错误
     */
    private void error() {
        // 网络出错，调用本地数据
        if (onKosapResponseListener != null)
            onKosapResponseListener.useLocal();
    }

    /**
     * 判断是否请求执行结束
     */
    public boolean isRequestCompleted() {
        return requestCompleted;
    }

}
