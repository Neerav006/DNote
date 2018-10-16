package com.codefuelindia.dnote.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Product {

@SerializedName("id")
@Expose
private String id;
@SerializedName("time")
@Expose
private String time;
@SerializedName("name")
@Expose
private String name;
@SerializedName("rate")
@Expose
private String rate;
@SerializedName("status")
@Expose
private String status;

public String getId() {
return id;
}

public void setId(String id) {
this.id = id;
}

public String getTime() {
return time;
}

public void setTime(String time) {
this.time = time;
}

public String getName() {
return name;
}

public void setName(String name) {
this.name = name;
}

public String getRate() {
return rate;
}

public void setRate(String rate) {
this.rate = rate;
}

public String getStatus() {
return status;
}

public void setStatus(String status) {
this.status = status;
}

}