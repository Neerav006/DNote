package com.codefuelindia.dnote.view;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import com.codefuelindia.dnote.R;

public class ChooseReportTypeActivity extends AppCompatActivity {

    LinearLayout ll_detailReport, ll_productReport, ll_summaryReport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_report_type);

        ll_detailReport = findViewById(R.id.chooseReport_tv_detailsReport);
        ll_productReport = findViewById(R.id.chooseReport_tv_productReport);
        ll_summaryReport = findViewById(R.id.chooseReport_tv_summaryReport);


        ll_detailReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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

                startActivity(new Intent(ChooseReportTypeActivity.this,SummaryReportActivity.class));

            }
        });

    }

}
