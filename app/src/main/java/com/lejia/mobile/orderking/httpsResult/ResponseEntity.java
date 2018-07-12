package com.lejia.mobile.orderking.httpsResult;

import android.os.Parcel;
import android.os.Parcelable;

import com.lejia.mobile.orderking.utils.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Author by HEKE
 *
 * @time 2018/7/6 18:35
 * TODO:
 */
public class ResponseEntity implements Parcelable {

    public String msg; // 信息
    public int state; // 状态
    public String data; // 数据

    public ResponseEntity(Object result) {
        JSONObject object = null;
        try {
            object = new JSONObject(result.toString());
            msg = object.getString("msg");
            state = object.getInt("state");
            data = object.getString("data");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public ResponseEntity(String result) {
        JSONObject object = null;
        try {
            object = new JSONObject(result);
            msg = object.getString("msg");
            state = object.getInt("state");
            data = object.getString("data");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected ResponseEntity(Parcel in) {
        msg = in.readString();
        state = in.readInt();
        data = in.readString();
    }

    public String getData() {
        if (TextUtils.isTextEmpity(data))
            return null;
        String json = null;
        try {
            json = new JSONObject(data).toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    /**
     * 获取数据对象内容
     *
     * @param key
     */
    public String getJSonObject(String key) {
        if (TextUtils.isTextEmpity(data))
            return null;
        String json = null;
        try {
            json = new JSONObject(data).getJSONObject(key).toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    /**
     * 获取数据集合内容
     *
     * @param key
     */
    public JSONArray getJSonArray(String key) {
        if (TextUtils.isTextEmpity(data))
            return null;
        JSONArray array = null;
        try {
            array = new JSONObject(data).getJSONArray(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return array;
    }

    /**
     * 获取数据集合内容
     *
     * @param position
     */
    public String getJSonArray(int position) {
        if (TextUtils.isTextEmpity(data))
            return null;
        String json = null;
        try {
            json = new JSONArray(data).getJSONObject(position).toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(msg);
        dest.writeInt(state);
        dest.writeString(data);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ResponseEntity> CREATOR = new Creator<ResponseEntity>() {
        @Override
        public ResponseEntity createFromParcel(Parcel in) {
            return new ResponseEntity(in);
        }

        @Override
        public ResponseEntity[] newArray(int size) {
            return new ResponseEntity[size];
        }
    };

    @Override
    public String toString() {
        return msg + "|" + state + "|" + data;
    }

}
