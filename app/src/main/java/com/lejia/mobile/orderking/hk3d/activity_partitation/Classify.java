package com.lejia.mobile.orderking.hk3d.activity_partitation;

/**
 * Author by HEKE
 *
 * @time 2018/7/27 17:12
 * TODO: 数字分类
 */
@Deprecated
public class Classify {

    /********************************************
     * 铺砖窗口右下角区分
     * ******************************************/

    // 区域铺砖
    public static final int FLAG_AREAS = 0;
    // 砖缝设置
    public static final int FLAG_GAPS = 1;
    // 起铺方向
    public static final int FLAG_DIRECTION = 2;
    // 倾斜铺砖
    public static final int FLAG_SKEW_TILE = 3;

    /********************************************
     * 铺砖窗口右上角区分
     * ******************************************/

    // 样式
    public static final int FLAG_STYLES = 0;
    // 换砖
    public static final int FLAG_CHANGE_TILES = 1;
    // 方案
    public static final int FLAG_SCHEMES = 2;
    // 自定义
    public static final int FLAG_CUSTOM = 3;

}
