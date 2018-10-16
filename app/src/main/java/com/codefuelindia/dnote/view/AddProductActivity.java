package com.codefuelindia.dnote.view;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.codefuelindia.dnote.Common.RetrofitClient;
import com.codefuelindia.dnote.Common.SessionManager;
import com.codefuelindia.dnote.Model.ResCommon;
import com.codefuelindia.dnote.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public class AddProductActivity extends AppCompatActivity {

    private static final String BASE_URL = "http://code-fuel.in/dnote/api/";
    AddProductAPI addProductAPI;
    SessionManager sessionManager;

    EditText editText_name, editText_rate;
    Button button_add, button_cancel;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        addProductAPI = getAddProductAPIService(BASE_URL);
        sessionManager = new SessionManager(this);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        editText_name = findViewById(R.id.addProduct_et_name);
        editText_rate = findViewById(R.id.addProduct_et_rate);
        button_add = findViewById(R.id.addProduct_btn_submit);
        button_cancel = findViewById(R.id.addProduct_btn_cancel);
        progressBar = findViewById(R.id.addProduct_progressBar);


        button_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                methodAddProduct();
            }
        });

        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                methodCancel();
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


    private void methodCancel() {
        finish();
    }

    private void methodAddProduct() {
        // API Call
        progressBar.setVisibility(View.VISIBLE);

        String name, rate;

        name = editText_name.getText().toString();
        rate = editText_rate.getText().toString();


        if (name.equals("")) {
            editText_name.setError("Name is required");
            editText_name.requestFocus();

        } else if (rate.equals("")) {
            editText_rate.setError("Rate is required");
            editText_rate.requestFocus();

        } else {

            addProductAPI.add_product(name, rate).enqueue(new Callback<ResCommon>() {
                @Override
                public void onResponse(Call<ResCommon> call, Response<ResCommon> response) {
                    progressBar.setVisibility(View.GONE);

                    if (response.isSuccessful()) {

                        if (response.body() != null) {

                            if (response.body().getMsg().equals("true")) {

                                clearFields();

                                Toast.makeText(AddProductActivity.this, "Your product is added", Toast.LENGTH_SHORT).show();

                            }

                        } else {
                            // response body is null
                        }


                    } else {
                        // response not successful
                    }

                }

                @Override
                public void onFailure(Call<ResCommon> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                }
            });

        }

    }

    private void clearFields() {
        editText_name.setText("");
        editText_rate.setText("");

        editText_name.requestFocus();

    }


//------------------------------------ APIs --------------------------------------------//

    AddProductAPI getAddProductAPIService(String baseUrl) {
        return RetrofitClient.getClient(baseUrl).create(AddProductAPI.class);
    }

    interface AddProductAPI {
        @POST("addProduct")
        @FormUrlEncoded
        Call<ResCommon> add_product(@Field("name") String name,
                                    @Field("rate") String rate
        );
    }

}
