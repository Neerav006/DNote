package com.codefuelindia.dnote.Model;

import java.util.ArrayList;

public class InserDebitData {

    private String id;
    private ArrayList<DebitModel> debitModelArrayList;

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
