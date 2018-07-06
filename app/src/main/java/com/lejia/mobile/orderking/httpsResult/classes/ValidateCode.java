package com.lejia.mobile.orderking.httpsResult.classes;

import android.os.Parcel;
import android.os.Parcelable;

import com.lejia.mobile.orderking.utils.TextUtils;

/**
 * Author by HEKE
 *
 * @time 2018/7/6 18:45
 * TODO: 验证码对象
 */
public class ValidateCode implements Parcelable {

    public String id; // 验证码对应唯一编号
    public String code;
    public String codeImg; // 验证码图片
    public String addTime; // 创建时间

    protected ValidateCode(Parcel in) {
        id = in.readString();
        code = in.readString();
        codeImg = in.readString();
        addTime = in.readString();
    }

    public boolean isValid() {
        return !TextUtils.isTextEmpity(id);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCodeImg() {
        return codeImg;
    }

    public void setCodeImg(String codeImg) {
        this.codeImg = codeImg;
    }

    public String getAddTime() {
        return addTime;
    }

    public void setAddTime(String addTime) {
        this.addTime = addTime;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(code);
        dest.writeString(codeImg);
        dest.writeString(addTime);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ValidateCode> CREATOR = new Creator<ValidateCode>() {
        @Override
        public ValidateCode createFromParcel(Parcel in) {
            return new ValidateCode(in);
        }

        @Override
        public ValidateCode[] newArray(int size) {
            return new ValidateCode[size];
        }
    };

    @Override
    public String toString() {
        return id + "|" + code + "|" + codeImg + "|" + addTime;
    }
}
