package com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.classes;

import android.os.Parcel;
import android.os.Parcelable;

import com.lejia.mobile.orderking.hk3d.classes.Point;
import com.lejia.mobile.orderking.utils.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Author by HEKE
 *
 * @time 2018/11/20 10:29
 * TODO: 铺砖计划对象，为波打线、样式、方案铺砖具体子数据对象
 */
public class TilePlan implements Parcelable {

    public String code; // 唯一编码标识
    public String type; // 类型
    public String name; // 方式名称
    public float gap; // 缝隙
    public int locate; // 起铺位置
    public float rotate; // 旋转角度
    public int gapColor; // 砖缝颜色

    // 多层波打线数据
    public String texture = "assets/plan/b1.jpg"; // 默认使用砖路径
    public int openDir = -1;
    public int waveLineOrientation;
    public float waveWidth;
    public int layerCount = 1; // 波打线所属层
    public int gapTileRegion;
    public int waveType; // 波打线类型(斜切或转角)
    public boolean wavelinesCellsLayout; // 多层波打线铺贴标志

    /**
     * 铺砖计划中的运算数值标记集合
     */
    public HashMap<String, Integer> symbolMaps = new HashMap<>();

    /**
     * 物理瓷砖对象
     */
    public Phy phy;

    /**
     * 逻辑砖对象
     */
    public Logtile logtile;

    /**
     * 与整砖起铺偏置方向一
     */
    public DirExp1 dirExp1;

    /**
     * 与整砖起铺偏置方向二
     */
    public DirExp2 dirExp2;

    /**
     * 铺砖起铺方向一
     */
    public Dir1 dir1;

    /**
     * 铺砖起铺方向二
     */
    public Dir2 dir2;

    /**
     * 组合物理砖与逻辑砖数据打包对象列表
     */
    public ArrayList<PhyLogicalPackage> phyLogicalPackageArrayList;

    /**
     * 初始化数据对象
     */
    private void initAttrs() {
        this.phy = new Phy();
        this.logtile = new Logtile();
        this.phyLogicalPackageArrayList = new ArrayList<>();
    }

    public TilePlan() {
        super();
        initAttrs();
    }

    public TilePlan(String code, String type, String name, float gap, int locate, float rotate) {
        this.code = code;
        this.type = type;
        this.name = name;
        this.gap = gap;
        this.locate = locate;
        this.rotate = rotate;
        initAttrs();
    }

    protected TilePlan(Parcel in) {
        code = in.readString();
        type = in.readString();
        name = in.readString();
        gap = in.readFloat();
        locate = in.readInt();
        rotate = in.readFloat();
        int size = in.readInt();
        if (size > 0) {
            symbolMaps = new HashMap<>();
            for (int i = 0; i < size; i++) {
                String key = in.readString();
                Integer val = in.readInt();
                symbolMaps.put(key, val);
            }
        }
        phy = in.readParcelable(Phy.class.getClassLoader());
        logtile = in.readParcelable(Logtile.class.getClassLoader());
        dirExp1 = in.readParcelable(DirExp1.class.getClassLoader());
        dirExp2 = in.readParcelable(DirExp2.class.getClassLoader());
    }

    /**
     * 存入运算数值标记
     *
     * @param key
     * @param value
     */
    public void putSymbol(String key, String value) {
        if (TextUtils.isTextEmpty(key) || TextUtils.isTextEmpty(value))
            return;
        symbolMaps.put(key, Integer.parseInt(value));
    }

    /**
     * 运算打包数据对象
     */
    public void bindingPhyLogicalPackages() {
        phyLogicalPackageArrayList.clear();
        int size = phy.size();
        if (size == 0)
            return;
        for (int i = 0; i < size; i++) {
            Tile tile = phy.getTile(i);
            LogicalTile logicalTile = logtile.getLogicalTile(i);
            PhyLogicalPackage phyLogicalPackage = new PhyLogicalPackage(tile, logicalTile);
            phyLogicalPackageArrayList.add(phyLogicalPackage);
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(code);
        dest.writeString(type);
        dest.writeString(name);
        dest.writeFloat(gap);
        dest.writeInt(locate);
        dest.writeFloat(rotate);
        int size = symbolMaps == null ? 0 : symbolMaps.size();
        dest.writeInt(size);
        if (size > 0) {
            Iterator<Map.Entry<String, Integer>> iterator = symbolMaps.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Integer> entry = iterator.next();
                String key = entry.getKey();
                Integer val = entry.getValue();
                dest.writeString(key);
                dest.writeInt(val);
            }
        }
        dest.writeParcelable(phy, flags);
        dest.writeParcelable(logtile, flags);
        dest.writeParcelable(dirExp1, flags);
        dest.writeParcelable(dirExp2, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TilePlan> CREATOR = new Creator<TilePlan>() {
        @Override
        public TilePlan createFromParcel(Parcel in) {
            return new TilePlan(in);
        }

        @Override
        public TilePlan[] newArray(int size) {
            return new TilePlan[size];
        }
    };

    /**
     * 数据释放
     */
    public void release() {
        try {
            symbolMaps.clear();
            symbolMaps = null;
            if (phyLogicalPackageArrayList != null && phyLogicalPackageArrayList.size() > 0) {
                for (PhyLogicalPackage phyLogicalPackage : phyLogicalPackageArrayList) {
                    phyLogicalPackage.release();
                }
                phyLogicalPackageArrayList.clear();
                phyLogicalPackageArrayList = null;
            }
            phy.tileArrayList.clear();
            phy = null;
            logtile.logicalTileArrayList.clear();
            logtile = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建数据xml
     *
     * @param pointArrayList 区域围点列表
     * @return xml数据
     */
    public String toXml(ArrayList<Point> pointArrayList) {
        String v = "<TilePlan  code=\"" + code + "\" type=\"" + type + "\" texture=\"" + texture + "\" name=\"" + name + "\" gap=\"" + gap + "\" locate=\"" + locate + "\" " + "rotate=\"" + rotate + "\">";
        // 多层波打线
        if (wavelinesCellsLayout) {
            v = "<TilePlan  code=\"" + code + "\" type=\"" + type + "\" texture=\"" + texture + "\" name=\"" + name + "\" gap=\"" + gap + "\" locate=\"" + locate + "\" " + "rotate=\"" + rotate + "\" openDir=\"" + openDir + "\" waveLineOrientation=\"" + waveLineOrientation + "\" waveWidth=\"" + waveWidth + "\" layerCount=\"" + layerCount + "\" gapTileRegion=\"" + gapTileRegion + "\" " + "waveType=\"" + waveType + "\">";
        }
        if (symbolMaps != null && symbolMaps.size() > 0) {
            Iterator<Map.Entry<String, Integer>> iterator = symbolMaps.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Integer> entry = iterator.next();
                String key = entry.getKey();
                Integer val = entry.getValue();
                v += "\n" + "<symbol key=\"" + key + "\" value=\"" + val + "\"/>";
            }
        }
        if (phy != null) {
            v += "\n" + phy.toXml();
        }
        if (logtile != null) {
            v += "\n" + logtile.toXml();
        }
        if (dir1 != null) {
            v += "\n" + dir1.toXml();
        }
        if (dir2 != null) {
            v += "\n" + dir2.toXml();
        }
        if (dirExp1 != null) {
            v += "\n" + dirExp1.toXml();
        }
        if (dirExp2 != null) {
            v += "\n" + dirExp2.toXml();
        }
        if (pointArrayList != null && pointArrayList.size() > 0) {
            v += "\n<tileRegion>";
            for (com.lejia.mobile.orderking.hk3d.classes.Point point : pointArrayList) {
                // 加上30000是因为接单王的原点在(3000,3000)，并放大十倍进行铺砖引起
                v += "\n<TPoint x=\"" + (int) (point.x * -10 + 30000) + "\" y=\"" + (int) (point.y * 10 + 30000) + "\"/>";
            }
            v += "\n</tileRegion>";
        } else {
            v += "\n<tileRegion/>";
        }
        v += "\n</TilePlan>";
        return v;
    }

    @Override
    public String toString() {
        String symbols = "SymbolMaps: ";
        int size = symbolMaps == null ? 0 : symbolMaps.size();
        if (size > 0) {
            Iterator<Map.Entry<String, Integer>> iterator = symbolMaps.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Integer> entry = iterator.next();
                String key = entry.getKey();
                Integer val = entry.getValue();
                symbols += "(Key : " + key + " Val : " + val + "),";
            }
        }
        return "TilePlan: " + code + "," + type + "," + name + "," + gap + "," + locate + "," + rotate + ","
                + "[" + phy + "]," + "[" + logtile + "]," + "[" + dirExp1 + "]," + "[" + dirExp2 + "]," + "[" + symbols + "]";
    }
}
