package com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.models;

import com.lejia.mobile.orderking.classes.XInfo;
import com.lejia.mobile.orderking.hk3d.datas_2d.cadwidgets.BaseCad;
import com.lejia.mobile.orderking.hk3d.datas_3d.tools.CellsRecord;

/**
 * Author by HEKE
 *
 * @time 2018/12/11 18:34
 * TODO: 模型文件的离地高等属性检测(离地高设置)
 */
public class L3DOffGroundChecker {

    private BaseCad baseCad;

    public L3DOffGroundChecker(BaseCad baseCad) {
        this.baseCad = baseCad;
        check();
    }

    private void check() {
        TopView topView = baseCad.topView;
        if (topView == null)
            return;
        // 窗户
        if (topView.type == 1) {
            XInfo xInfo = topView.xInfo;
            float cellHeight = CellsRecord.get(CellsRecord.current_edit_cell).cellHeight;
            float modelsEY = (xInfo.offGround + xInfo.Z) / 10;
            if (modelsEY > cellHeight) {
                xInfo.offGround = (int) (10 * ((cellHeight - xInfo.Z / 10) / 2));
            }
        }
        // 其他
        else {
            float cellHeight = CellsRecord.get(CellsRecord.current_edit_cell).cellHeight;
            String code = topView.xInfo.materialCode.toLowerCase();
            // 天花吊顶
            if (code.startsWith("th")) {
                topView.xInfo.offGround = (int) (cellHeight * 10);
            }
            // 吊灯
            else if (code.startsWith("dd")) {
                topView.xInfo.offGround = (int) (cellHeight * 10);
            }
        }
    }

}
