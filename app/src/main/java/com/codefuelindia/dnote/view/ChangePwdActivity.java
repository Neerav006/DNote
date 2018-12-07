package com.codefuelindia.dnote.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.codefuelindia.dnote.Common.RetrofitClient;
import com.codefuelindia.dnote.Common.SessionManager;
import com.codefuelindia.dnote.Model.ResCommon;
import com.codefuelindia.dnote.R;
import com.codefuelindia.dnote.constants.MyConstants;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public class ChangePwdActivity extends AppCompatActivity {

    private static final String BASE_URL = "http://code-fuel.in/dnote/api/";
    ChangePasswordAPI changePasswordAPI;

    EditText editText_pwd1, editText_pwd2, editText_OldPwd;
    Button button_submit;
    private String u_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pwd);

        if (getIntent() != null) {
            u_id = getIntent().getStringExtra("u_id");
        }

        changePasswordAPI = getChangePasswordAPIService(BASE_URL);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        editText_pwd1 = findViewById(R.id.changePwd_et_password1);
        editText_pwd2 = findViewById(R.id.changePwd_et_password2);
        editText_OldPwd = findViewById(R.id.changePwd_et_oldPwd);
        button_submit = findViewById(R.id.changePwd_btn_submit);

        button_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                methodSubmit();
            }
        });

    }

    private void methodSubmit() {

        String pwd1, pwd2, oldPwd;

        pwd1 = editText_pwd1.getText().toString();
        pwd2 = editText_pwd2.getText().toString();
        oldPwd = editText_OldPwd.getText().toString();

        if (oldPwd.trim().isEmpty()) {
            editText_OldPwd.setError("Old Password Required.");
            editText_OldPwd.requestFocus();
        } else if (pwd1.equals("")) {
            editText_pwd1.setError("Password is required");
            editText_pwd1.requestFocus();

        } else if (pwd2.equals("")) {
            editText_pwd2.setError("Confirm password is required");
            editText_pwd2.requestFocus();

        } else if (!pwd1.equals(pwd2)) {
            editText_pwd2.setError("Both password must be same");
            editText_pwd2.requestFocus();

        } else {
            // API Call
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Logging you in...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            changePasswordAPI.changePassword(u_id, oldPwd, pwd2).enqueue(new Callback<ResCommon>() {
                @Override
                public void onResponse(Call<ResCommon> call, Response<ResCommon> response) {
                    progressDialog.dismiss();

                    if (response.isSuccessful()) {
                        if (response.body().getMsg().equalsIgnoreCase("true")) {
                            MyConstants.Companion.showToast(ChangePwdActivity.this, "Successfully Changed!");

                            finishAffinity();
                            startActivity(new Intent(ChangePwdActivity.this, LoginActivity.class));

                        } else {
                            MyConstants.Companion.showToast(ChangePwdActivity.this, "Password Not match!");

                        }

                    } else {
                        MyConstants.Companion.showToast(ChangePwdActivity.this, "Connection error");
                    }
                }

                @Override
                public void onFailure(Call<ResCommon> call, Throwable t) {
                    progressDialog.dismiss();
                    MyConstants.Companion.showToast(ChangePwdActivity.this, "Connection error");

                }
            });

        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

//-------------------------------------------- APIs ---------------------------------------------------------//

    ChangePasswordAPI getChangePasswordAPIService(String baseUrl) {
        return RetrofitClient.getClient(baseUrl).create(ChangePasswordAPI.class);
    }

    interface ChangePasswordAPI {
        @POST("changePassword")
        @FormUrlEncoded
        Call<ResCommon> changePassword(@Field("u_id") String u_id,
                                       @Field("old_pwd") String old_pwd,
                                       @Field("password") String password
        );
    }

}
