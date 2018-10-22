package com.codefuelindia.dnote.view;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.codefuelindia.dnote.Common.RetrofitClient;
import com.codefuelindia.dnote.Model.ResProductReport;
import com.codefuelindia.dnote.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

import java.util.ArrayList;
import java.util.List;

public class ProductReportActivity extends AppCompatActivity {

    private static final String BASE_URL = "http://code-fuel.in/dnote/api/";
    ProductReportAPI productReportAPI;

    TextView tv_name, tv_number, tv_address, tv_city, tv_remainAmt, tv_recViewTitle, tv_noTrans;
    RecyclerView recyclerView_transactions;
    ProgressBar progressBar;

    RecAdapter recAdapter;
    ArrayList<ResProductReport> responseArrayList;

    private String u_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_report);

        productReportAPI = getProductReportAPIService(BASE_URL);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        tv_name = findViewById(R.id.productReport_tv_name);
        tv_number = findViewById(R.id.productReport_tv_number);
        tv_address = findViewById(R.id.productReport_tv_address);
        tv_city = findViewById(R.id.productReport_tv_city);
        tv_remainAmt = findViewById(R.id.productReport_tv_remainAmt);
        tv_recViewTitle = findViewById(R.id.productReport_tv_recViewTitle);
        tv_noTrans = findViewById(R.id.productReport_tv_noTrans);
        recyclerView_transactions = findViewById(R.id.productReport_recView_transactions);
        progressBar = findViewById(R.id.productReport_progressBar);

        u_id = getIntent().getStringExtra("id");
        tv_name.setText(getIntent().getStringExtra("name"));
        tv_number.setText(getIntent().getStringExtra("number"));
        tv_address.setText(getIntent().getStringExtra("address"));
        tv_city.setText(getIntent().getStringExtra("city"));
        tv_remainAmt.setText(getIntent().getStringExtra("remainingPayment") + " /-");

        getAllTransactions();

    }

    private void getAllTransactions() {
        progressBar.setVisibility(View.VISIBLE);

        productReportAPI.login_user(u_id).enqueue(new Callback<List<ResProductReport>>() {
            @Override
            public void onResponse(Call<List<ResProductReport>> call, Response<List<ResProductReport>> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful()) {

                    if (response.body() != null) {

                        responseArrayList = new ArrayList<>();
                        responseArrayList = (ArrayList<ResProductReport>) response.body();

                        recAdapter = new RecAdapter(responseArrayList);

                        recyclerView_transactions.setLayoutManager(new LinearLayoutManager(ProductReportActivity.this, LinearLayoutManager.VERTICAL, false));
                        recyclerView_transactions.addItemDecoration(new DividerItemDecoration(ProductReportActivity.this, DividerItemDecoration.VERTICAL));
                        recyclerView_transactions.setAdapter(recAdapter);

                        recAdapter.notifyDataSetChanged();

                        if (responseArrayList.size() < 0) {
                            tv_recViewTitle.setVisibility(View.INVISIBLE);
                            tv_noTrans.setVisibility(View.VISIBLE);
                        }

                    } else {
                        // response body is null
                    }

                } else {
                    // response not successful
                }

            }

            @Override
            public void onFailure(Call<List<ResProductReport>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
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


//----------------------------------------------- APIs ----------------------------------------------------------//

    ProductReportAPI getProductReportAPIService(String baseUrl) {
        return RetrofitClient.getClient(baseUrl).create(ProductReportAPI.class);
    }

    interface ProductReportAPI {
        @POST("productDetailReport")
        @FormUrlEncoded
        Call<List<ResProductReport>> login_user(@Field("id") String id);
    }

//------------------------------------------------- Adapter Class ----------------------------------------------//

    public class RecAdapter extends RecyclerView.Adapter<RecAdapter.ViewHolder> {

        private ArrayList<ResProductReport> mDataSet;

        RecAdapter(ArrayList<ResProductReport> mDataSet) {
            this.mDataSet = mDataSet;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_list_product_report, viewGroup, false);

            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, final int position) {

            viewHolder.getTextView_date().setText(mDataSet.get(position).getCreate_at());
            viewHolder.getTextView_name().setText(mDataSet.get(position).getProduct_name());
            viewHolder.getTextView_qty().setText(mDataSet.get(position).getQuntity());
            viewHolder.getTextView_amount().setText(mDataSet.get(position).getTotal());

        }

        @Override
        public int getItemCount() {
            return mDataSet.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            private final TextView textView_date, textView_name, textView_qty, textView_amount;

            ViewHolder(View v) {
                super(v);

                textView_date = (TextView) v.findViewById(R.id.row_productReport_tv_date);
                textView_name = (TextView) v.findViewById(R.id.row_productReport_tv_name);
                textView_qty = (TextView) v.findViewById(R.id.row_productReport_tv_qty);
                textView_amount = (TextView) v.findViewById(R.id.row_productReport_tv_amount);

            }

            public TextView getTextView_date() {
                return textView_date;
            }

            public TextView getTextView_name() {
                return textView_name;
            }

            public TextView getTextView_qty() {
                return textView_qty;
            }

            public TextView getTextView_amount() {
                return textView_amount;
            }
        }

    }

}
