package com.codefuelindia.dnote.view;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import com.codefuelindia.dnote.R;

public class ChooseReportTypeActivity extends AppCompatActivity {

    LinearLayout ll_detailReport, ll_productReport, ll_summaryReport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_report_type);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        ll_detailReport = findViewById(R.id.chooseReport_card_detailsReport);
        ll_productReport = findViewById(R.id.chooseReport_card_productReport);
        ll_summaryReport = findViewById(R.id.chooseReport_card_summaryReport);


        ll_detailReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(ChooseReportTypeActivity.this,SummaryReportActivity.class);
                intent.putExtra("from","detail");
                startActivity(intent);


            }
        });

        ll_productReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        ll_summaryReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(ChooseReportTypeActivity.this, SummaryReportActivity.class));

            }
        });

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
