package com.lejia.mobile.orderking.hk3d.classes;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.lejia.mobile.orderking.bases.OrderKingApplication;

/**
 * Author by HEKE
 *
 * @time 2018/7/10 16:11
 * TODO: 瓷砖
 */
public class Tile implements Parcelable {

    public int id; // 数据库编号
    public int materialTypeId; // 材质类型
    public String materialCode; // 材质编码
    public String materialName; // 材质名称
    public int materialWidth; // 材质宽度
    public int materialHeight; // 材质高度
    public String imageUrl; // 对应图片链接
    public String renderingPath; // 材质对应渲染路径
    public String creatorId; // 创建者编号
    public String enterpriseId; // 企业编号
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
        materialTypeId = in.readInt();
        materialCode = in.readString();
        materialName = in.readString();
        materialWidth = in.readInt();
        materialHeight = in.readInt();
        imageUrl = in.readString();
        renderingPath = in.readString();
        creatorId = in.readString();
        enterpriseId = in.readString();
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

    public int getMaterialTypeId() {
        return materialTypeId;
    }

    public void setMaterialTypeId(int materialTypeId) {
        this.materialTypeId = materialTypeId;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getRenderingPath() {
        return renderingPath;
    }

    public void setRenderingPath(String renderingPath) {
        this.renderingPath = renderingPath;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public String getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(String enterpriseId) {
        this.enterpriseId = enterpriseId;
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
            Glide.with(OrderKingApplication.getInstant()).asBitmap().load(imageUrl).into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> transition) {
                    // 赋值位图
                    Tile.this.bitmap = bitmap;
                    // 回调位图结果
                    onTileBitmapListener.onTileBitmapThenDoSomething(bitmap);
                }
            });
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(materialTypeId);
        dest.writeString(materialCode);
        dest.writeString(materialName);
        dest.writeInt(materialWidth);
        dest.writeInt(materialHeight);
        dest.writeString(imageUrl);
        dest.writeString(renderingPath);
        dest.writeString(creatorId);
        dest.writeString(enterpriseId);
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
