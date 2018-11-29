package com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.classes;

import android.os.Parcel;
import android.os.Parcelable;

import com.lejia.mobile.orderking.utils.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Author by HEKE
 *
 * @time 2018/11/20 15:20
 * TODO: 逻辑砖偏置函数详细数据对象
 */
public class SymbolVector3D implements Parcelable {

    public float u; // 水平方向偏移量
    public float v; // 垂直方向偏移量
    public float w; // 权重值

    private String su;
    private String sv;

    public SymbolVector3D() {
        super();
    }

    public SymbolVector3D(float u, float v, float w) {
        this.u = u;
        this.v = v;
        this.w = w;
    }

    protected SymbolVector3D(Parcel in) {
        u = in.readFloat();
        v = in.readFloat();
        w = in.readFloat();
        su = in.readString();
        sv = in.readString();
    }

    /**
     * 判断字符串是否匹配单数值
     *
     * @param v
     * @return
     */
    private boolean isSingleLengthIntegerValue(String v) {
        if (TextUtils.isTextEmpty(v))
            return false;
        int length = v.trim().length();
        if (length != 1)
            return false;
        return v.matches("[\\d]{1}");
    }

    /**
     * Author by HEKE
     *
     * @time 2018/11/20 20:30
     * TODO: 用于区分分割字符串的数值标记对象
     */
    private class V {

        public String val; // 具体数值或字符
        public boolean used; // 是否已使用
        public int position; // 所在集合位置

        public float replaceVal; // 替换数值

        public V(String val, boolean used, int position) {
            this.val = val;
            this.used = used;
            this.position = position;
        }

        /**
         * 初始化数值
         *
         * @param symbolsMap
         */
        private void initVal(HashMap<String, Integer> symbolsMap) {
            Integer integer = symbolsMap.get(val);
            if (integer != null) {
                val = String.valueOf(integer.intValue());
            }
        }
    }

    /**
     * 根据标记及对应数值运算数值
     *
     * @param value
     * @param symbolsMap
     * @return
     */
    private float calculate(String value, HashMap<String, Integer> symbolsMap) {
        if (TextUtils.isTextEmpty(value) || symbolsMap == null || symbolsMap.size() == 0)
            return -1;
        float ret = 0;
        // 分割运算符得出数值
        String[] vs = value.split("[\\/*+-]{1}");
        V[] svs = new V[vs.length];
        for (int i = 0; i < vs.length; i++) {
            svs[i] = new V(vs[i], false, i);
            svs[i].initVal(symbolsMap);
        }
        // 分割出运算符号
        String operator = "";
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (("" + c).matches("[\\/*+-]{1}")) {
                operator += c;
            }
        }
        // 优先运算乘除
        String residuce = "";
        ArrayList<Integer> residuceIndexList = new ArrayList<>();
        for (int i = 0; i < operator.length(); i++) {
            String c = String.valueOf(operator.charAt(i));
            if (c.matches("[\\/*]{1}")) {
                V v1 = svs[i];
                V v2 = svs[i + 1];
                v1.used = true;
                v2.used = true;
                float rv = 0f;
                if (c.equals("*")) {
                    rv = Float.parseFloat(v1.val) * Float.parseFloat(v2.val);
                } else if (c.equals("/")) {
                    rv = Float.parseFloat(v1.val) / Float.parseFloat(v2.val);
                }
                v1.replaceVal = rv;
                v2.replaceVal = rv;
            } else {
                residuce += c;
                residuceIndexList.add(i);
            }
        }
        // 在运算加减
        for (int i = 0; i < residuce.length(); i++) {
            String resi = String.valueOf(residuce.charAt(i));
            int index = residuceIndexList.get(i);
            V vi = svs[index];
            V vn = svs[index + 1];
            float rv = 0f;
            if (resi.equals("+")) {
                rv = (vi.used ? vi.replaceVal : Float.parseFloat(vi.val)) + (vn.used ? vn.replaceVal : Float.parseFloat(vn.val));
            } else if (resi.equals("-")) {
                rv = (vi.used ? vi.replaceVal : Float.parseFloat(vi.val)) - (vn.used ? vn.replaceVal : Float.parseFloat(vn.val));
            }
            vi.used = true;
            vi.replaceVal = rv;
            vn.used = true;
            vn.replaceVal = rv;
            // 运算结束，赋值
            if (i == residuce.length() - 1) {
                ret = rv;
            }
        }
        return ret;
    }

    /**
     * 运算数值
     *
     * @param ustring    水平运算字符串
     * @param vstring    垂直运算字符串
     * @param symbolsMap 所有数值标记集合
     */
    public void calculateValues(String ustring, String vstring, HashMap<String, Integer> symbolsMap) {
        if (symbolsMap == null || symbolsMap.size() == 0 || TextUtils.isTextEmpty(ustring) || TextUtils.isTextEmpty(vstring))
            return;
        su = ustring;
        sv = vstring;
        if (isSingleLengthIntegerValue(ustring))
            u = Integer.parseInt(ustring);
        else
            u = calculate(ustring, symbolsMap);
        if (isSingleLengthIntegerValue(vstring))
            v = Integer.parseInt(vstring);
        else
            v = calculate(vstring, symbolsMap);
        w = 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(u);
        dest.writeFloat(v);
        dest.writeFloat(w);
        dest.writeString(su);
        dest.writeString(sv);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SymbolVector3D> CREATOR = new Creator<SymbolVector3D>() {
        @Override
        public SymbolVector3D createFromParcel(Parcel in) {
            return new SymbolVector3D(in);
        }

        @Override
        public SymbolVector3D[] newArray(int size) {
            return new SymbolVector3D[size];
        }
    };

    public String toXml() {
        return "<SymbolVector3D u=\"" + su + "\" v=\"" + sv + "\" w=\"0\"/>";
    }

    @Override
    public String toString() {
        return u + "," + v + "," + w;
    }

}
