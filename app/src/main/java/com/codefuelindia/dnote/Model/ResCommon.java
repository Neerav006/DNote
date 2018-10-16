package com.codefuelindia.dnote.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResCommon {

    @SerializedName("msg")
    @Expose
    private String msg;

    @SerializedName("u_id")
    @Expose
    private String u_id;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("number")
    @Expose
    private String number;


    public String getMsg() {
        return msg;
    }

    public void setMessage(String msg) {
        this.msg = msg;
    }

    public String getU_id() {
        return u_id;
    }

    public void setU_id(String u_id) {
        this.u_id = u_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
