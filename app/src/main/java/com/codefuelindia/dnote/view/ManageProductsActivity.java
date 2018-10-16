package com.codefuelindia.dnote.view;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.codefuelindia.dnote.R;

public class ManageProductsActivity extends AppCompatActivity {

    RecyclerView recyclerView_productList;
    TextView textView_noproducts;
    ProgressBar progressBar;
    FloatingActionButton fab_add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_products);

        recyclerView_productList = findViewById(R.id.manageProducts_recView);
        textView_noproducts = findViewById(R.id.manageProducts_tv_noProducts);
        progressBar = findViewById(R.id.manageProducts_progressBar);
        fab_add = findViewById(R.id.manageProducts_fab_addProduct);


        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ManageProductsActivity.this, AddProductActivity.class));
            }
        });

    }

}
