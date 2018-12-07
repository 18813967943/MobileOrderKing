package com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.models;

import android.content.Context;

import com.lejia.mobile.orderking.hk3d.Designer3DRender;
import com.lejia.mobile.orderking.hk3d.datas_2d.cadwidgets.BaseCad;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Author by HEKE
 *
 * @time 2018/12/4 16:47
 * TODO: 模型中间数据监听观察者对象
 */
public class InterObserver {

    private Context mContext;
    private Designer3DRender designer3DRender;

    /**
     * 所有模型对应的坐标等信息集合
     */
    private HashMap<String, ArrayList<CorrespondingMatrix>> modelsCorrespondingMatrixMap;

    /**
     * 模型库
     */
    private ModelsLibrary modelsLibrary;

    public InterObserver(Context context) {
        this.mContext = context;
        this.modelsCorrespondingMatrixMap = new HashMap<>();
        this.modelsLibrary = new ModelsLibrary();
    }

    public void setDesigner3DRender(Designer3DRender designer3DRender) {
        this.designer3DRender = designer3DRender;
    }

    /**
     * 刷新数据
     */
    public void notification() {
        modelsCorrespondingMatrixMap.clear();
        FurnitureController furnitureController = designer3DRender.getFurnitureController();
        try {
            ArrayList<BaseCad> baseCadArrayList = furnitureController.getBaseCadsList();
            if (baseCadArrayList == null || baseCadArrayList.size() == 0) {
                return;
            }
            for (BaseCad baseCad : baseCadArrayList) {
                String hashCode = "" + baseCad.hashCode();
                TopView topView = baseCad.topView;
                String code = baseCad.topView.xInfo.materialCode;
                int size = modelsCorrespondingMatrixMap.size();
                if (size == 0) {
                    ArrayList<CorrespondingMatrix> correspondingMatrixArrayList = new ArrayList<>();
                    correspondingMatrixArrayList.add(new CorrespondingMatrix(hashCode, topView));
                    modelsCorrespondingMatrixMap.put(code, correspondingMatrixArrayList);
                    modelsLibrary.put(code);
                } else {
                    ArrayList<CorrespondingMatrix> correspondingMatrixArrayList = modelsCorrespondingMatrixMap.get(code);
                    if (correspondingMatrixArrayList == null) {
                        ArrayList<CorrespondingMatrix> correspondingMatrixArrayList1 = new ArrayList<>();
                        correspondingMatrixArrayList1.add(new CorrespondingMatrix(hashCode, topView));
                        modelsCorrespondingMatrixMap.put(code, correspondingMatrixArrayList1);
                        modelsLibrary.put(code);
                    } else {
                        correspondingMatrixArrayList.add(new CorrespondingMatrix(hashCode, topView));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 渲染模型
     *
     * @param positionAttribute
     * @param normalAttribute
     * @param colorAttribute
     * @param onlyPosition
     */
    public void render(int positionAttribute, int normalAttribute, int colorAttribute, boolean onlyPosition) {
        int size = modelsLibrary.size();
        if (size > 0) {
            HashMap<String, L3DFile> l3dFileHashMap = modelsLibrary.getL3dFileHashMap();
            Iterator<Map.Entry<String, L3DFile>> iterator = l3dFileHashMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, L3DFile> entry = iterator.next();
                String code = entry.getKey();
                L3DFile l3dFile = entry.getValue();
                ArrayList<CorrespondingMatrix> correspondingMatrixArrayList = modelsCorrespondingMatrixMap.get(code);
                if (correspondingMatrixArrayList != null && correspondingMatrixArrayList.size() > 0) {
                    for (CorrespondingMatrix correspondingMatrix : correspondingMatrixArrayList) {
                        l3dFile.render(correspondingMatrix, positionAttribute, normalAttribute, colorAttribute, onlyPosition);
                    }
                }
            }
        }
    }

    /**
     * 数据释放
     */
    public void release() {
        int size = modelsLibrary.size();
        if (size > 0) {
            HashMap<String, L3DFile> l3dFileHashMap = modelsLibrary.getL3dFileHashMap();
            Iterator<Map.Entry<String, L3DFile>> iterator = l3dFileHashMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, L3DFile> entry = iterator.next();
                L3DFile l3dFile = entry.getValue();
                ArrayList<L3DItemInfo> l3DItemInfoArrayList = l3dFile.getL3DItemInfoArrayList();
                if (l3DItemInfoArrayList != null) {
                    for (L3DItemInfo l3DItemInfo : l3DItemInfoArrayList) {
                        l3DItemInfo.release();
                        if (l3DItemInfo.diffuseBitmap != null && !l3DItemInfo.diffuseBitmap.isRecycled()) {
                            l3DItemInfo.diffuseBitmap.recycle();
                            l3DItemInfo.diffuseBitmap = null;
                        }
                    }
                }
            }
        }
        modelsCorrespondingMatrixMap.clear();
    }

}
