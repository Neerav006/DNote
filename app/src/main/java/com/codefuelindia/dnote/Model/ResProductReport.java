package com.codefuelindia.dnote.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResProductReport {

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("c_id")
    @Expose
    private String c_id;

    @SerializedName("product_name")
    @Expose
    private String product_name;

    @SerializedName("rate")
    @Expose
    private String rate;

    @SerializedName("quntity")
    @Expose
    private String quntity;

    @SerializedName("total")
    @Expose
    private String total;

    @SerializedName("remark")
    @Expose
    private String remark;

    @SerializedName("create_at")
    @Expose
    private String create_at;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getC_id() {
        return c_id;
    }

    public void setC_id(String c_id) {
        this.c_id = c_id;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getQuntity() {
        return quntity;
    }

    public void setQuntity(String quntity) {
        this.quntity = quntity;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getCreate_at() {
        return create_at;
    }

    public void setCreate_at(String create_at) {
        this.create_at = create_at;
    }
}
