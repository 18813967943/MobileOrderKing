package com.lejia.mobile.orderking.bases;

/**
 * Author by HEKE
 *
 * @time 2018/7/6 16:35
 * TODO: 服务器请求配置
 */
public class HttpsConfig {

    /**
     * 网络请求服务器根目录
     */
    private static final String HTTPS_SERVICE_URL = "http://192.168.1.100:8020/";

    /**
     * 文件材质请求服务器根目录
     */
    private static final String FILES_SERVICE_URL = "http://192.168.1.100:8070/";

    /**
     * 获取验证码,6位随机数
     */
    public static final String GetValidateCode = HTTPS_SERVICE_URL + "api/User/GetValidateCode";

    /**
     * 注册
     */
    public static final String SIGN_UP = HTTPS_SERVICE_URL + "api/User/SignUp";

    /**
     * 登入
     */
    public static final String SIGN_IN = HTTPS_SERVICE_URL + "api/User/UserLogin";

    /**
     * 拉取企业数据列表
     */
    public static final String GET_ENTERPRISE_NODESLIST = FILES_SERVICE_URL + "api/Material/GetMaterialTypeList";

    /**
     * 拉取指定节点数据列表
     */
    public static final String GET_DETAILE_NODE_DATAS = FILES_SERVICE_URL + "api/Material/GetMaterialList";

}
