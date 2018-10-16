package com.codefuelindia.dnote.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class History {

@SerializedName("id")
@Expose
private String id;
@SerializedName("c_id")
@Expose
private String cId;
@SerializedName("create_at")
@Expose
private String createAt;
@SerializedName("rs")
@Expose
private String rs;

public String getId() {
return id;
}

public void setId(String id) {
this.id = id;
}

public String getCId() {
return cId;
}

public void setCId(String cId) {
this.cId = cId;
}

public String getCreateAt() {
return createAt;
}

public void setCreateAt(String createAt) {
this.createAt = createAt;
}

public String getRs() {
return rs;
}

public void setRs(String rs) {
this.rs = rs;
}

}