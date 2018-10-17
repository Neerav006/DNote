package com.codefuelindia.dnote.view;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import com.codefuelindia.dnote.R;

public class ProductItemDetailsActivity extends AppCompatActivity {

    TextView textView_name, textView_rate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_item_details);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        textView_name = findViewById(R.id.productDetails_tv_name);
        textView_rate = findViewById(R.id.productDetails_tv_rate);

        textView_name.setText(getIntent().getStringExtra("name"));
        textView_rate.setText(getIntent().getStringExtra("rate"));

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

}
