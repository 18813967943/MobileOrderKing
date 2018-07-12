package com.lejia.mobile.orderking.hk3d.classes;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.lejia.mobile.orderking.bases.OrderKingApplication;
import com.lejia.mobile.orderking.utils.BitmapUtils;

/**
 * Author by HEKE
 *
 * @time 2018/7/10 16:11
 * TODO: 瓷砖
 */
public class Tile implements Parcelable {

    public int id; // 数据库编号
    public int materialTypeID; // 材质类型
    public String materialCode; // 材质编码
    public String materialName; // 材质名称
    public int materialWidth; // 材质宽度
    public int materialHeight; // 材质高度
    public String imageURL; // 对应图片链接
    public String renderingPath; // 材质对应渲染路径
    public String creatorID; // 创建者编号
    public String enterpriseID; // 企业编号
    public boolean isPublic; // 公开的
    public boolean isEnable; // 是否开放的，可使用的
    public String createTime; // 创建时间

    /**
     * 材质位图
     */
    private Bitmap bitmap;

    public Tile() {
        super();
    }

    protected Tile(Parcel in) {
        id = in.readInt();
        materialTypeID = in.readInt();
        materialCode = in.readString();
        materialName = in.readString();
        materialWidth = in.readInt();
        materialHeight = in.readInt();
        imageURL = in.readString();
        renderingPath = in.readString();
        creatorID = in.readString();
        enterpriseID = in.readString();
        isPublic = in.readByte() != 0;
        isEnable = in.readByte() != 0;
        createTime = in.readString();
        int bmpSize = in.readInt();
        if (bmpSize > 0)
            bitmap = in.readParcelable(Bitmap.class.getClassLoader());
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMaterialTypeID() {
        return materialTypeID;
    }

    public void setMaterialTypeID(int materialTypeID) {
        this.materialTypeID = materialTypeID;
    }

    public String getMaterialCode() {
        return materialCode;
    }

    public void setMaterialCode(String materialCode) {
        this.materialCode = materialCode;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public int getMaterialWidth() {
        return materialWidth;
    }

    public void setMaterialWidth(int materialWidth) {
        this.materialWidth = materialWidth;
    }

    public int getMaterialHeight() {
        return materialHeight;
    }

    public void setMaterialHeight(int materialHeight) {
        this.materialHeight = materialHeight;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getRenderingPath() {
        return renderingPath;
    }

    public void setRenderingPath(String renderingPath) {
        this.renderingPath = renderingPath;
    }

    public String getCreatorID() {
        return creatorID;
    }

    public void setCreatorID(String creatorID) {
        this.creatorID = creatorID;
    }

    public String getEnterpriseID() {
        return enterpriseID;
    }

    public void setEnterpriseID(String enterpriseID) {
        this.enterpriseID = enterpriseID;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public boolean isEnable() {
        return isEnable;
    }

    public void setEnable(boolean enable) {
        isEnable = enable;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    /**
     * 获取材质的位图
     *
     * @param onTileBitmapListener 用于数据返回接口
     */
    public void getBitmap(@NonNull final OnTileBitmapListener onTileBitmapListener) {
        if (onTileBitmapListener == null)
            return;
        if (bitmap != null)
            onTileBitmapListener.onTileBitmapThenDoSomething(bitmap);
        else {
            Glide.with(OrderKingApplication.getInstant()).asBitmap().load(imageURL).into(new SimpleTarget<Bitmap>() {
                @SuppressLint("StaticFieldLeak")
                @Override
                public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> transition) {
                    // 异步线程执行
                    new AsyncTask<Bitmap, Integer, Bitmap>() {
                        @Override
                        protected Bitmap doInBackground(Bitmap... bitmaps) {
                            return BitmapUtils.toSize(bitmaps[0], materialWidth / 10, materialHeight / 10);
                        }

                        @Override
                        protected void onPostExecute(Bitmap bitmap) {
                            super.onPostExecute(bitmap);
                            // 赋值位图
                            Tile.this.bitmap = bitmap;
                            // 回调位图结果
                            onTileBitmapListener.onTileBitmapThenDoSomething(Tile.this.bitmap);
                        }
                    }.execute(bitmap);
                }
            });
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(materialTypeID);
        dest.writeString(materialCode);
        dest.writeString(materialName);
        dest.writeInt(materialWidth);
        dest.writeInt(materialHeight);
        dest.writeString(imageURL);
        dest.writeString(renderingPath);
        dest.writeString(creatorID);
        dest.writeString(enterpriseID);
        dest.writeByte((byte) (isPublic ? 1 : 0));
        dest.writeByte((byte) (isEnable ? 1 : 0));
        dest.writeString(createTime);
        if (bitmap != null) {
            dest.writeInt(1);
            dest.writeParcelable(bitmap, flags);
        } else
            dest.writeInt(0);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Tile> CREATOR = new Creator<Tile>() {
        @Override
        public Tile createFromParcel(Parcel in) {
            return new Tile(in);
        }

        @Override
        public Tile[] newArray(int size) {
            return new Tile[size];
        }
    };

    /**
     * Author by HEKE
     *
     * @time 2018/7/10 16:51
     * TODO: 材质位图获取监听
     */
    public interface OnTileBitmapListener {
        void onTileBitmapThenDoSomething(Bitmap bitmap);
    }

}
