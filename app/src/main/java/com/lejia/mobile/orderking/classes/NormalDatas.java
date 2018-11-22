package com.lejia.mobile.orderking.classes;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Base64;

import com.lejia.mobile.orderking.hk3d.classes.NetByteArrayIntputStream;
import com.lejia.mobile.orderking.utils.TextUtils;

/**
 * Author by HEKE
 *
 * @time 2017/7/3 11:26
 * TODO: 解析材质信息对象
 */
public class NormalDatas implements Parcelable {

    public String code; // 编码
    public String name; // 名称
    public String classCode; // 类编码
    public String className; // 类名
    public int type; // 类型
    public float price; // 价格
    public float cost; // 花费
    public int catalog; // 大类
    public String spec;
    public String remark; // 标签
    public String description; // 详情描述
    public float offGround; // 离地高
    public String url;
    public String combo;
    public String brand;
    public String mode; // 瓷砖分类
    public String style;
    public String series;
    public String subSeries;
    public String linkedDataUrl; // 方案对应xml路径
    public String family;
    public int vrmMode;
    public boolean isPolyMode;
    public float offBoard;
    public String orgCode;
    public String orgName;
    public String linkVRDataUrl;
    public String parentCode;
    private Bitmap previewBmp;

    public String cachePath; // 缓存路径

    private OnNormalDatasListener onNormalDatasListener;

    /**
     * 避免内存泄漏
     */
    private ParseInfoTask mTask;
    private boolean isParsing;

    // 加载数据对应的位置
    private int loadPosition = -1;

    public NormalDatas() {
        super();
    }

    protected NormalDatas(Parcel in) {
        code = in.readString();
        name = in.readString();
        classCode = in.readString();
        className = in.readString();
        type = in.readInt();
        price = in.readFloat();
        cost = in.readFloat();
        catalog = in.readInt();
        spec = in.readString();
        remark = in.readString();
        description = in.readString();
        offGround = in.readFloat();
        url = in.readString();
        combo = in.readString();
        brand = in.readString();
        mode = in.readString();
        style = in.readString();
        series = in.readString();
        subSeries = in.readString();
        linkedDataUrl = in.readString();
        family = in.readString();
        vrmMode = in.readInt();
        isPolyMode = in.readByte() != 0;
        offBoard = in.readFloat();
        orgCode = in.readString();
        orgName = in.readString();
        linkVRDataUrl = in.readString();
        parentCode = in.readString();
        previewBmp = in.readParcelable(Bitmap.class.getClassLoader());
        isParsing = in.readByte() != 0;
        cachePath = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(code);
        dest.writeString(name);
        dest.writeString(classCode);
        dest.writeString(className);
        dest.writeInt(type);
        dest.writeFloat(price);
        dest.writeFloat(cost);
        dest.writeInt(catalog);
        dest.writeString(spec);
        dest.writeString(remark);
        dest.writeString(description);
        dest.writeFloat(offGround);
        dest.writeString(url);
        dest.writeString(combo);
        dest.writeString(brand);
        dest.writeString(mode);
        dest.writeString(style);
        dest.writeString(series);
        dest.writeString(subSeries);
        dest.writeString(linkedDataUrl);
        dest.writeString(family);
        dest.writeInt(vrmMode);
        dest.writeByte((byte) (isPolyMode ? 1 : 0));
        dest.writeFloat(offBoard);
        dest.writeString(orgCode);
        dest.writeString(orgName);
        dest.writeString(linkVRDataUrl);
        dest.writeString(parentCode);
        dest.writeParcelable(previewBmp, flags);
        dest.writeByte((byte) (isParsing ? 1 : 0));
        dest.writeString(cachePath);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<NormalDatas> CREATOR = new Creator<NormalDatas>() {
        @Override
        public NormalDatas createFromParcel(Parcel in) {
            return new NormalDatas(in);
        }

        @Override
        public NormalDatas[] newArray(int size) {
            return new NormalDatas[size];
        }
    };

    public void load(String contents, int position, OnNormalDatasListener onNormalDatasListener) {
        if (TextUtils.isTextEmpty(contents))
            return;
        this.loadPosition = position;
        this.onNormalDatasListener = onNormalDatasListener;
        // 解析
        loadPreviewBitmap(contents);
    }

    /**
     * 加载预览图
     *
     * @param contents
     */
    private void loadPreviewBitmap(String contents) {
        if (TextUtils.isTextEmpty(contents))
            return;
        if (!isParsing) {
            isParsing = true;
            if (mTask != null) {
                mTask.cancel(true);
                mTask = null;
                System.gc();
            }
            mTask = new ParseInfoTask();
            mTask.execute(contents);
        }
    }

    public Bitmap getPreviewBmp() {
        return previewBmp;
    }

    /**
     * Author by HEKE
     *
     * @time 2017/7/3 16:06
     * TODO: 拉取模型信息异步线程
     */
    private class ParseInfoTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                // 解析
                byte[] buffer = Base64.decode(params[0], Base64.DEFAULT);
                // 创建解析数据流
                NetByteArrayIntputStream nbais = new NetByteArrayIntputStream(buffer);
                // 读取资料信息
                code = nbais.ReadString();
                name = nbais.ReadString();
                classCode = nbais.ReadString();
                className = nbais.ReadString();
                type = nbais.ReadInt32();
                price = nbais.ReadSingle();
                cost = nbais.ReadSingle();
                catalog = nbais.ReadInt32();
                spec = nbais.ReadString();
                remark = nbais.ReadString();
                description = nbais.ReadString();
                // 读取预览图
                int previewLength = nbais.ReadInt32();
                if (previewLength > 0) {
                    byte[] previewBuffer = nbais.ReadBytes(0, previewLength);
                    previewBmp = BitmapFactory.decodeByteArray(previewBuffer, 0, previewBuffer.length);
                }
                // 判断后续是否还有数据，有继续读取
                if (nbais.available() > 0) {
                    offGround = nbais.ReadSingle();
                    if (nbais.available() > 0) {
                        url = nbais.ReadString();
                        if (nbais.available() > 0) {
                            combo = nbais.ReadString();
                            if (nbais.available() > 0) {
                                brand = nbais.ReadString();
                                mode = nbais.ReadString();
                                style = nbais.ReadString();
                                series = nbais.ReadString();
                                subSeries = nbais.ReadString();
                                if (nbais.available() > 0) {
                                    linkedDataUrl = nbais.ReadString();
                                    if (nbais.available() > 0) {
                                        family = nbais.ReadString();
                                        if (nbais.available() > 0) {
                                            vrmMode = nbais.ReadInt32();
                                            if (nbais.available() > 0) {
                                                isPolyMode = nbais.ReadInt32() > 0;
                                                if (nbais.available() > 0) {
                                                    offBoard = nbais.ReadSingle();
                                                    if (nbais.available() > 0) {
                                                        orgCode = nbais.ReadString();
                                                        orgName = nbais.ReadString();
                                                        if (nbais.available() > 0) {
                                                            linkVRDataUrl = nbais.ReadString();
                                                            if (nbais.available() > 0) {
                                                                parentCode = nbais.ReadString();
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                nbais.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return linkedDataUrl;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (onNormalDatasListener != null)
                onNormalDatasListener.onCompeleted(NormalDatas.this, previewBmp, loadPosition);
            // 释放
            isParsing = false;
            if (mTask != null) {
                mTask.cancel(true);
                mTask = null;
                System.gc();
            }
        }
    }

    @Override
    public String toString() {
        return code + "|" + type + "|" + name + "|" + linkedDataUrl + "|" + cachePath + "|" + catalog
                + "|" + spec + "|" + remark + "|" + description + "|" + series + "|" + style;
    }

    /**
     * 释放数据
     */
    public void release() {
        try {
            if (previewBmp != null) {
                previewBmp.recycle();
                previewBmp = null;
            }
            if (mTask != null) {
                mTask.cancel(true);
                mTask = null;
            }
            System.gc();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Author by HEKE
     *
     * @time 2017/7/3 11:30
     * TODO: 回调数据接口
     */
    public interface OnNormalDatasListener {
        void onCompeleted(NormalDatas nd, Bitmap preview, int position);
    }

}
