package com.lejia.mobile.orderking.httpsResult.classes;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.lejia.mobile.orderking.httpsResult.ResponseEntity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Author by HEKE
 *
 * @time 2018/7/12 10:31
 * TODO: 企业对应材质节点数据列表对象
 */
public class MaterialTypeList implements Parcelable {

    public ArrayList<LJNodes> materialTypeList;

    public MaterialTypeList() {
        this.materialTypeList = new ArrayList<>();
    }

    public MaterialTypeList(String json) {
        this.materialTypeList = new ArrayList<>();
        try {
            ResponseEntity entity = new ResponseEntity(json);
            JSONArray array = entity.getJSonArray("materialTypeList");
            if (array != null && array.length() > 0) {
                for (int i = 0; i < array.length(); i++) {
                    JSONObject object = array.getJSONObject(i);
                    LJNodes nodes = new Gson().fromJson(object.toString(), LJNodes.class);
                    add(nodes);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected MaterialTypeList(Parcel in) {
        materialTypeList = in.createTypedArrayList(LJNodes.CREATOR);
    }

    public ArrayList<LJNodes> getMaterialTypeList() {
        return materialTypeList;
    }

    /**
     * 新增
     *
     * @param nodes
     */
    public void add(LJNodes nodes) {
        materialTypeList.add(nodes);
    }

    /**
     * 增加列表
     *
     * @param materialTypeList
     */
    public void addAll(ArrayList<LJNodes> materialTypeList) {
        this.materialTypeList.addAll(materialTypeList);
    }

    /**
     * 重置
     */
    public void clear() {
        materialTypeList.clear();
    }

    /**
     * 获取指定位置的节点详细节点数据
     *
     * @param position
     * @return 返回指定节点的详细节点数据列表
     */
    public ArrayList<LJNodes> getChildDetailsList(int position) {
        if (materialTypeList == null || materialTypeList.size() == 0)
            return null;
        if (position < 0 || position >= materialTypeList.size())
            return null;
        return materialTypeList.get(position).getChildrenList();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(materialTypeList);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MaterialTypeList> CREATOR = new Creator<MaterialTypeList>() {
        @Override
        public MaterialTypeList createFromParcel(Parcel in) {
            return new MaterialTypeList(in);
        }

        @Override
        public MaterialTypeList[] newArray(int size) {
            return new MaterialTypeList[size];
        }
    };

    @Override
    public String toString() {
        return "" + materialTypeList;
    }

}
