package com.lejia.mobile.orderking.bases;

import com.lejia.mobile.orderking.httpsResult.classes.LJNodes;
import com.lejia.mobile.orderking.httpsResult.classes.MaterialTypeList;

import java.util.ArrayList;

/**
 * Author by HEKE
 *
 * @time 2018/8/23 15:41
 * TODO: 大类数据区分
 */
@Deprecated
public class CatlogChecker {

    /**
     * 当前用户企业信息大类数据节点列表
     */
    private static MaterialTypeList furnitureCatlogList;

    // 绑定大类数据资源
    public static void setFurnitureCatlogList(MaterialTypeList furnitureCatlogList) {
        CatlogChecker.furnitureCatlogList = furnitureCatlogList;
    }

    /**
     * 检测是否是门窗
     *
     * @param materialTypeId 模型大类编号
     * @return -1为非门窗，0为门，1为窗
     */
    public static int checkDoorOrWindow(int materialTypeId) {
        if (materialTypeId < 0 || CatlogChecker.furnitureCatlogList == null)
            return -1;
        /*ArrayList<LJNodes> nodesList = CatlogChecker.furnitureCatlogList.getMaterialTypeList();
        for (LJNodes nodes : nodesList) {
            if (nodes.getId() == materialTypeId) {
                String name = nodes.getName();
                if (name.equals("门"))
                    return 0;
                else if (name.equals("窗")) {
                    return 1;
                }
            }
        }*/
        return -1;
    }

}
