package com.codefuelindia.dnote.view;

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
import android.widget.ProgressBar;
import android.widget.TextView;
import com.codefuelindia.dnote.Common.RetrofitClient;
import com.codefuelindia.dnote.Model.User;
import com.codefuelindia.dnote.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.POST;

import java.util.ArrayList;
import java.util.List;

public class ManageCustomersActivity extends AppCompatActivity {

    private static final String BASE_URL = "http://code-fuel.in/dnote/api/";
    CustomerListAPI customerListAPI;

    RecyclerView recyclerView_customerList;
    TextView textView_noCustomers;
    ProgressBar progressBar;


    RecAdapter recAdapter;
    private ArrayList<User> resCustomerArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_customers);

        customerListAPI = getCustomerListAPIService(BASE_URL);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        recyclerView_customerList = findViewById(R.id.manageCustomers_recView);
        textView_noCustomers = findViewById(R.id.manageCustomers_tv_noCustomers);
        progressBar = findViewById(R.id.manageCustomers_progressBar);


        getAllCustomers();

    }

    private void getAllCustomers() {
        progressBar.setVisibility(View.VISIBLE);

        customerListAPI.getCustomerList().enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful()) {

                    if (response.body() != null) {

                        resCustomerArrayList = new ArrayList<>();
                        resCustomerArrayList = (ArrayList<User>) response.body();

                        recAdapter = new RecAdapter(resCustomerArrayList);

                        recyclerView_customerList.setLayoutManager(new LinearLayoutManager(ManageCustomersActivity.this, LinearLayoutManager.VERTICAL, false));
                        recyclerView_customerList.addItemDecoration(new DividerItemDecoration(ManageCustomersActivity.this, DividerItemDecoration.VERTICAL));
                        recyclerView_customerList.setAdapter(recAdapter);

                        recAdapter.notifyDataSetChanged();


                    } else {
                        // response body null
                    }

                } else {
                    // response not successful
                }

            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
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


//---------------------------------------- APIs ------------------------------------------------//

    CustomerListAPI getCustomerListAPIService(String baseUrl) {
        return RetrofitClient.getClient(baseUrl).create(CustomerListAPI.class);
    }

    interface CustomerListAPI {
        @POST("getUsers")
        Call<List<User>> getCustomerList();
    }


//----------------------------------- Adapter Class ---------------------------------------------//

    public class RecAdapter extends RecyclerView.Adapter<RecAdapter.ViewHolder> {

        private ArrayList<User> mDataSet;

        RecAdapter(ArrayList<User> mDataSet) {
            this.mDataSet = mDataSet;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_list_customers, viewGroup, false);

            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, final int position) {

            viewHolder.getTextView_index().setText(String.valueOf(position + 1));
            viewHolder.getTextView_name().setText(mDataSet.get(position).getName());

        }

        @Override
        public int getItemCount() {
            return mDataSet.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            private final TextView textView_index, textView_name;

            ViewHolder(View v) {
                super(v);

                textView_index = (TextView) v.findViewById(R.id.row_customerList_tv_index);
                textView_name = (TextView) v.findViewById(R.id.row_customerList_tv_name);

            }

            TextView getTextView_name() {
                return textView_name;
            }

            TextView getTextView_index() {
                return textView_index;
            }

        }

    }

}
