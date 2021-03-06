package com.codefuelindia.dnote.Model;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User implements Parcelable {

@SerializedName("id")
@Expose
private String id;

@SerializedName("date_time")
@Expose
private String dateTime;

@SerializedName("name")
@Expose
private String name;

@SerializedName("mobile")
@Expose
private String mobile;

@SerializedName("password")
@Expose
private String password;
@SerializedName("address")
@Expose
private String address;

@SerializedName("city")
@Expose
private String city;

@SerializedName("total_payment")
@Expose
private String totalPayment;

@SerializedName("collected_payment")
@Expose
private String collectedPayment;

@SerializedName("remaining_payment")
@Expose
private String remainingPayment;

@SerializedName("status")
@Expose
private String status;

@SerializedName("role")
@Expose
private String role;

public String getId() {
return id;
}

public void setId(String id) {
this.id = id;
}

public String getDateTime() {
return dateTime;
}

public void setDateTime(String dateTime) {
this.dateTime = dateTime;
}

public String getName() {
return name;
}

public void setName(String name) {
this.name = name;
}

public String getMobile() {
return mobile;
}

public void setMobile(String mobile) {
this.mobile = mobile;
}

public String getPassword() {
return password;
}

public void setPassword(String password) {
this.password = password;
}

public String getAddress() {
return address;
}

public void setAddress(String address) {
this.address = address;
}

public String getCity() {
return city;
}

public void setCity(String city) {
this.city = city;
}

public String getTotalPayment() {
return totalPayment;
}

public void setTotalPayment(String totalPayment) {
this.totalPayment = totalPayment;
}

public String getCollectedPayment() {
return collectedPayment;
}

public void setCollectedPayment(String collectedPayment) {
this.collectedPayment = collectedPayment;
}

public String getRemainingPayment() {
return remainingPayment;
}

public void setRemainingPayment(String remainingPayment) {
this.remainingPayment = remainingPayment;
}

public String getStatus() {
return status;
}

public void setStatus(String status) {
this.status = status;
}

public String getRole() {
return role;
}

public void setRole(String role) {
this.role = role;
}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.dateTime);
        dest.writeString(this.name);
        dest.writeString(this.mobile);
        dest.writeString(this.password);
        dest.writeString(this.address);
        dest.writeString(this.city);
        dest.writeString(this.totalPayment);
        dest.writeString(this.collectedPayment);
        dest.writeString(this.remainingPayment);
        dest.writeString(this.status);
        dest.writeString(this.role);
    }

    public User() {
    }

    protected User(Parcel in) {
        this.id = in.readString();
        this.dateTime = in.readString();
        this.name = in.readString();
        this.mobile = in.readString();
        this.password = in.readString();
        this.address = in.readString();
        this.city = in.readString();
        this.totalPayment = in.readString();
        this.collectedPayment = in.readString();
        this.remainingPayment = in.readString();
        this.status = in.readString();
        this.role = in.readString();
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}