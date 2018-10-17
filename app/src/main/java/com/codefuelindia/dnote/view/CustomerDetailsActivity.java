package com.codefuelindia.dnote.view;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import com.codefuelindia.dnote.R;

public class CustomerDetailsActivity extends AppCompatActivity {

    TextView tv_name, tv_number, tv_city, tv_address, tv_totalPayment, tv_collectedPayment, tv_remainingPayment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_details);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        tv_name = findViewById(R.id.customerDetails_tv_name);
        tv_number = findViewById(R.id.customerDetails_tv_number);
        tv_city = findViewById(R.id.customerDetails_tv_city);
        tv_address = findViewById(R.id.customerDetails_tv_address);
        tv_totalPayment = findViewById(R.id.customerDetails_tv_totalPayment);
        tv_collectedPayment = findViewById(R.id.customerDetails_tv_collectedPayment);
        tv_remainingPayment = findViewById(R.id.customerDetails_tv_remainingPayment);


        tv_name.setText(getIntent().getStringExtra("name"));
        tv_number.setText(getIntent().getStringExtra("number"));
        tv_city.setText(getIntent().getStringExtra("city"));
        tv_address.setText(getIntent().getStringExtra("address"));
        tv_totalPayment.setText(getIntent().getStringExtra("totalPayment"));
        tv_collectedPayment.setText(getIntent().getStringExtra("collectedPayment"));
        tv_remainingPayment.setText(getIntent().getStringExtra("remainingPayment"));

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
