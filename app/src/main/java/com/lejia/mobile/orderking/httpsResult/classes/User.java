package com.lejia.mobile.orderking.httpsResult.classes;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.lejia.mobile.orderking.utils.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Author by HEKE
 *
 * @time 2018/7/7 10:10
 * TODO: 用户
 */
public class User implements Parcelable {

    public long id; // 唯一编号
    public String accountID; // 用户唯一编码
    public String fullName; // 全名
    public int gender; // 性别
    public String cellPhone; // 手机号
    public String email; // 邮箱
    public String userName; // 用户名
    public int provinceID; // 省份编号
    public String provinceName; // 省份名称
    public int cityID; // 城市编号
    public String cityName; // 城市名称
    public String address; // 地址
    public String remark; // 标记
    public String leJiaProjectID; // 项目版本号
    public String leJiaProjectName; // 项目名称
    public int isAdministrators; // 最高管理权限
    public int isLeJiaAccount; // 是否乐家账号
    public String createrID; // 创建人编号
    public String createrName; // 创建人名称
    public boolean isEnabled; // 是否开放账号
    public String createTime; // 创建时间

    // 其他信息
    public String enterpriseInfo; // 企业信息
    public String token; // 有效唯一编码

    // 主体信息
    private String account;
    private String passowrd;

    /**
     * 所在企业信息
     */
    public Enterprise enterprise;

    public User() {
        super();
    }

    public User(String user) {
        if (!TextUtils.isTextEmpty(user)) {
            String[] vs = user.split("[|]");
            if (vs.length >= 25) {
                id = Long.parseLong(vs[0]);
                accountID = vs[1];
                fullName = vs[2];
                gender = Integer.parseInt(vs[3]);
                cellPhone = vs[4];
                email = vs[5];
                userName = vs[6];
                provinceID = Integer.parseInt(vs[7]);
                provinceName = vs[8];
                cityID = Integer.parseInt(vs[9]);
                cityName = vs[10];
                address = vs[11];
                remark = vs[12];
                leJiaProjectID = vs[13];
                leJiaProjectName = vs[14];
                isAdministrators = Integer.parseInt(vs[15]);
                isLeJiaAccount = Integer.parseInt(vs[16]);
                createrID = vs[17];
                createrName = vs[18];
                isEnabled = "true".equals(vs[19]);
                createTime = vs[20];
                setEnterpriseInfo(vs[21]);
                token = vs[22];
                account = vs[23];
                passowrd = vs[24];
            }
        }
    }

    protected User(Parcel in) {
        id = in.readLong();
        accountID = in.readString();
        fullName = in.readString();
        gender = in.readInt();
        cellPhone = in.readString();
        email = in.readString();
        userName = in.readString();
        provinceID = in.readInt();
        provinceName = in.readString();
        cityID = in.readInt();
        cityName = in.readString();
        address = in.readString();
        remark = in.readString();
        leJiaProjectID = in.readString();
        leJiaProjectName = in.readString();
        isAdministrators = in.readInt();
        isLeJiaAccount = in.readInt();
        createrID = in.readString();
        createrName = in.readString();
        isEnabled = in.readByte() != 0;
        createTime = in.readString();
        enterpriseInfo = in.readString();
        token = in.readString();
        account = in.readString();
        passowrd = in.readString();
        enterprise = in.readParcelable(Enterprise.class.getClassLoader());
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAccountID() {
        return accountID;
    }

    public void setAccountID(String accountID) {
        this.accountID = accountID;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getCellPhone() {
        return cellPhone;
    }

    public void setCellPhone(String cellPhone) {
        this.cellPhone = cellPhone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getProvinceID() {
        return provinceID;
    }

    public void setProvinceID(int provinceID) {
        this.provinceID = provinceID;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public int getCityID() {
        return cityID;
    }

    public void setCityID(int cityID) {
        this.cityID = cityID;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getLeJiaProjectID() {
        return leJiaProjectID;
    }

    public void setLeJiaProjectID(String leJiaProjectID) {
        this.leJiaProjectID = leJiaProjectID;
    }

    public String getLeJiaProjectName() {
        return leJiaProjectName;
    }

    public void setLeJiaProjectName(String leJiaProjectName) {
        this.leJiaProjectName = leJiaProjectName;
    }

    public int getIsAdministrators() {
        return isAdministrators;
    }

    public void setIsAdministrators(int isAdministrators) {
        this.isAdministrators = isAdministrators;
    }

    public int getIsLeJiaAccount() {
        return isLeJiaAccount;
    }

    public void setIsLeJiaAccount(int isLeJiaAccount) {
        this.isLeJiaAccount = isLeJiaAccount;
    }

    public String getCreaterID() {
        return createrID;
    }

    public void setCreaterID(String createrID) {
        this.createrID = createrID;
    }

    public String getCreaterName() {
        return createrName;
    }

    public void setCreaterName(String createrName) {
        this.createrName = createrName;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getEnterpriseInfo() {
        return enterpriseInfo;
    }

    @Deprecated
    public void setEnterpriseInfo(String enterpriseInfo) {
        this.enterpriseInfo = enterpriseInfo;
        // 解析对应的企业信息
        try {
            if (!TextUtils.isTextEmpty(enterpriseInfo) && !enterpriseInfo.equals("乐家")) {
                JSONObject object = new JSONObject(enterpriseInfo);
                enterprise = new Gson().fromJson(object.toString(), Enterprise.class);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassowrd() {
        return passowrd;
    }

    public void setPassowrd(String passowrd) {
        this.passowrd = passowrd;
    }

    public Enterprise getEnterprise() {
        return enterprise;
    }

    public void setEnterprise(Enterprise enterprise) {
        this.enterprise = enterprise;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(accountID);
        dest.writeString(fullName);
        dest.writeInt(gender);
        dest.writeString(cellPhone);
        dest.writeString(email);
        dest.writeString(userName);
        dest.writeInt(provinceID);
        dest.writeString(provinceName);
        dest.writeInt(cityID);
        dest.writeString(cityName);
        dest.writeString(address);
        dest.writeString(remark);
        dest.writeString(leJiaProjectID);
        dest.writeString(leJiaProjectName);
        dest.writeInt(isAdministrators);
        dest.writeInt(isLeJiaAccount);
        dest.writeString(createrID);
        dest.writeString(createrName);
        dest.writeByte((byte) (isEnabled ? 1 : 0));
        dest.writeString(createTime);
        dest.writeString(enterpriseInfo);
        dest.writeString(token);
        dest.writeString(account);
        dest.writeString(passowrd);
        dest.writeParcelable(enterprise, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public String toString() {
        return id + "|" + accountID + "|" + fullName + "|" + gender + "|" + cellPhone + "|" + email + "|" + userName +
                "|" + provinceID + "|" + provinceName + "|" + cityID + "|" + cityName + "|" + address + "|" + remark + "|" + leJiaProjectID
                + "|" + leJiaProjectName + "|" + isAdministrators + "|" + isLeJiaAccount + "|" + createrID + "|" + createrName + "|"
                + isEnabled + "|" + createTime + "|" + enterpriseInfo + "|" + token + "|" + account + "|" + passowrd;
    }

}
