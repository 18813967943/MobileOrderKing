package com.lejia.mobile.orderking.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Author by HEKE
 *
 * @time 2018/11/30 10:25
 * TODO: 时间处理对象
 */
public class TimeUtils {

    /**
     * 年月日时分秒
     */
    public static String timeNumber() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
        return sdf.format(date);
    }

}
