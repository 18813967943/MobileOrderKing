package com.lejia.mobile.orderking.httpsResult.classes;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Author by HEKE
 *
 * @time 2018/7/11 17:27
 * TODO: 企业对象
 */
public class Enterprise implements Parcelable {

    public String id;
    public String companyName;
    public int companyType;
    public String companyTypeTitle;
    public int authorizedSubAccountQuantity;
    public String contacts;
    public String contactWay;
    public int subAccountLimit;
    public int subAccountAmount;
    public String createrID;
    public String createrName;
    public int isSystem;
    public String created;

    protected Enterprise(Parcel in) {
        id = in.readString();
        companyName = in.readString();
        companyType = in.readInt();
        companyTypeTitle = in.readString();
        authorizedSubAccountQuantity = in.readInt();
        contacts = in.readString();
        contactWay = in.readString();
        subAccountLimit = in.readInt();
        subAccountAmount = in.readInt();
        createrID = in.readString();
        createrName = in.readString();
        isSystem = in.readInt();
        created = in.readString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public int getCompanyType() {
        return companyType;
    }

    public void setCompanyType(int companyType) {
        this.companyType = companyType;
    }

    public String getCompanyTypeTitle() {
        return companyTypeTitle;
    }

    public void setCompanyTypeTitle(String companyTypeTitle) {
        this.companyTypeTitle = companyTypeTitle;
    }

    public int getAuthorizedSubAccountQuantity() {
        return authorizedSubAccountQuantity;
    }

    public void setAuthorizedSubAccountQuantity(int authorizedSubAccountQuantity) {
        this.authorizedSubAccountQuantity = authorizedSubAccountQuantity;
    }

    public String getContacts() {
        return contacts;
    }

    public void setContacts(String contacts) {
        this.contacts = contacts;
    }

    public String getContactWay() {
        return contactWay;
    }

    public void setContactWay(String contactWay) {
        this.contactWay = contactWay;
    }

    public int getSubAccountLimit() {
        return subAccountLimit;
    }

    public void setSubAccountLimit(int subAccountLimit) {
        this.subAccountLimit = subAccountLimit;
    }

    public int getSubAccountAmount() {
        return subAccountAmount;
    }

    public void setSubAccountAmount(int subAccountAmount) {
        this.subAccountAmount = subAccountAmount;
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

    public int getIsSystem() {
        return isSystem;
    }

    public void setIsSystem(int isSystem) {
        this.isSystem = isSystem;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(companyName);
        dest.writeInt(companyType);
        dest.writeString(companyTypeTitle);
        dest.writeInt(authorizedSubAccountQuantity);
        dest.writeString(contacts);
        dest.writeString(contactWay);
        dest.writeInt(subAccountLimit);
        dest.writeInt(subAccountAmount);
        dest.writeString(createrID);
        dest.writeString(createrName);
        dest.writeInt(isSystem);
        dest.writeString(created);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Enterprise> CREATOR = new Creator<Enterprise>() {
        @Override
        public Enterprise createFromParcel(Parcel in) {
            return new Enterprise(in);
        }

        @Override
        public Enterprise[] newArray(int size) {
            return new Enterprise[size];
        }
    };

    @Override
    public String toString() {
        return id + "|" + companyName + "|" + companyType + "|" + companyTypeTitle + "|" + authorizedSubAccountQuantity + "|"
                + contacts + "|" + contactWay + "|" + subAccountLimit + "|" + subAccountAmount + "|" + createrID + "|"
                + createrName + "|" + isSystem + "|" + created;
    }

}
