package com.codefuelindia.dnote.view;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.MenuItem;
import android.view.View;
import com.codefuelindia.dnote.R;

public class ChooseReportTypeActivity extends AppCompatActivity {

    CardView card_detailReport, card_productReport, card_summaryReport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_report_type);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        card_detailReport = findViewById(R.id.chooseReport_card_detailsReport);
        card_productReport = findViewById(R.id.chooseReport_card_productReport);
        card_summaryReport = findViewById(R.id.chooseReport_card_summaryReport);


        card_detailReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(ChooseReportTypeActivity.this, SummaryReportActivity.class);
                intent.putExtra("from", "detail");
                startActivity(intent);


            }
        });

        card_productReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        card_summaryReport.setOnClickListener(new View.OnClickListener() {
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
