package com.codefuelindia.dnote.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class InserDebitData {

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("debit_list")
    private ArrayList<DebitModel> debitModelArrayList;
    @SerializedName("addr")
    @Expose
    private String addr;
    @SerializedName("mobile")
    @Expose
    private String mobile;
    @SerializedName("city")
    @Expose
    private String city;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @SerializedName("name")
    @Expose
    private String name;

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    @SerializedName("total")
    @Expose
    private String total;

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @SerializedName("remark")
    @Expose
    private String remark;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<DebitModel> getDebitModelArrayList() {
        return debitModelArrayList;
    }

    public void setDebitModelArrayList(ArrayList<DebitModel> debitModelArrayList) {
        this.debitModelArrayList = debitModelArrayList;
    }
}
