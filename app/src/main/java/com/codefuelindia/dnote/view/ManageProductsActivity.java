package com.codefuelindia.dnote.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.codefuelindia.dnote.R;

public class ManageProductsActivity extends AppCompatActivity {

    RecyclerView recyclerView_productList;
    TextView textView_noproducts;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_products);

        recyclerView_productList = findViewById(R.id.manageProducts_recView);
        textView_noproducts = findViewById(R.id.manageProducts_tv_noProducts);
        progressBar = findViewById(R.id.manageProducts_progressBar);


    }

}
