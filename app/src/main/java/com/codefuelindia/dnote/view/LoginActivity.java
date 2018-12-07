package com.codefuelindia.dnote.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String BASE_URL = "http://code-fuel.in/dnote/api/";
    LoginAPI loginAPI;
    SessionManager sessionManager;
    EditText editText_number, editText_password;
    Button button_login;
    TextView textView_forget_pwd;
    String number, password;
    private CheckSubscription checkSubscription;
    private TextView tvServerError;
    private TextView tvNoInternet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginAPI = getLoginAPIService(BASE_URL);

        sessionManager = new SessionManager(this);
        checkSubscription = RetrofitClient.getClient(BASE_URL).create(CheckSubscription.class);
        tvNoInternet = findViewById(R.id.tvNoInterNet);
        tvServerError = findViewById(R.id.tvServerError);


        if (sessionManager.checkLogin()) {

            //---------------------- verify subscription --------------------//

            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Verifying User.....");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            final String uid = sessionManager.getKeyUId();

            checkSubscription.checkLicence(uid).enqueue(new Callback<ResCommon>() {
                @Override
                public void onResponse(Call<ResCommon> call, Response<ResCommon> response) {
                    if (progressDialog.isShowing() && LoginActivity.this != null) {
                        progressDialog.dismiss();
                    }
                    if (response.isSuccessful()) {

                        if (response.body().getMsg().equalsIgnoreCase("2x")) {
                            // ok valid subscription


                            Intent i = new Intent(LoginActivity.this, DashNavigationActivity.class);

                            // Closing all the Activities
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                            // Add new Flag to start new Activity
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                            startActivity(i);
                            finish();


                        } else {
                            // not ok expired..
                            Toast.makeText(LoginActivity.this, "Your subscription is expired.Please contact admin", Toast.LENGTH_LONG).show();

                        }


                    } else {

                        // some internal server error
                        Toast.makeText(LoginActivity.this, "Internal Server error, Try again", Toast.LENGTH_LONG).show();


                    }

                }

                @Override
                public void onFailure(Call<ResCommon> call, Throwable t) {
                    // some server error
                    if (progressDialog.isShowing() && LoginActivity.this != null) {
                        progressDialog.dismiss();
                    }
                    Toast.makeText(LoginActivity.this, "Internal Server error,Try again", Toast.LENGTH_LONG).show();

                }
            });


        }

        editText_number = findViewById(R.id.login_et_number);
        editText_password = findViewById(R.id.login_et_password);
        button_login = findViewById(R.id.login_btn_login);
        textView_forget_pwd = findViewById(R.id.login_tv_forgot_pwd);

        button_login.setOnClickListener(this);
        textView_forget_pwd.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.login_btn_login:
                method_login();
                //fake_login();
                break;

            case R.id.login_tv_forgot_pwd:
                method_forgot_pwd();
                break;

        }

    }


    private void method_login() {

        number = editText_number.getText().toString();
        password = editText_password.getText().toString();

        if (number.equals("")) {
            editText_number.setError("Number is required");
            editText_number.requestFocus();

        } else if (!isValidNumber(number)) {
            editText_number.setError("Enter a valid number");
            editText_number.requestFocus();

        } else if (password.equals("")) {
            editText_password.setError("Password is required");
            editText_password.requestFocus();

        } else if (password.length() < 8) {
            editText_password.setError("Length of password must be >=8 characters");
            editText_password.requestFocus();

        } else {

            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Logging you in...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();


            loginAPI.login_user(number, password).enqueue(new Callback<ResCommon>() {
                @Override
                public void onResponse(Call<ResCommon> call, Response<ResCommon> response) {

                    progressDialog.dismiss();

                    if (response.isSuccessful()) {

                        if (response.body() != null) {

                            switch (response.body().getMsg()) {

                                case "true":
                                    String name, number, u_id, addr, mobile;
                                    name = response.body().getName();
                                    number = response.body().getNumber();
                                    u_id = response.body().getId();
                                    addr = response.body().getAddress();
                                    mobile = response.body().getMobile();

                                    finish();
                                    sessionManager.createLoginSession(name, number, u_id, mobile, addr);

                                    if (number.equals(password)) {
                                        Intent i = new Intent(LoginActivity.this, ChangePwdActivity.class);
                                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(i);
                                    } else {
                                        Intent i = new Intent(LoginActivity.this, DashNavigationActivity.class);
                                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(i);
                                    }


                                    break;

                                case "false":
                                    Toast.makeText(getApplicationContext(), "You are not a registered user", Toast.LENGTH_SHORT).show();
                                    break;

                                case "2x":
                                    // valid subscription

                                    String name1, number1, u_id1, addr1, mobile1;
                                    name1 = response.body().getName();
                                    number1 = response.body().getNumber();
                                    u_id1 = response.body().getId();
                                    addr1 = response.body().getAddress();
                                    mobile1 = response.body().getMobile();

                                    finish();
                                    sessionManager.createLoginSession(name1, number1, u_id1, mobile1, addr1);

                                    if (number1.equals(password)) {
                                        Intent i1 = new Intent(LoginActivity.this, ChangePwdActivity.class);
                                        i1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        i1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(i1);

                                    } else {
                                        Intent i1 = new Intent(LoginActivity.this, DashNavigationActivity.class);
                                        i1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        i1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(i1);
                                    }


                                    break;

                                case "1x":
                                    // expired
                                    Toast.makeText(getApplicationContext(), "Your subscription Expired..Contact Admin", Toast.LENGTH_SHORT).show();


                                    break;


                                default:
                                    Toast.makeText(getApplicationContext(), "Some error occurred while signing you up \nPlease try after sometime", Toast.LENGTH_SHORT).show();
                                    break;

                            }

                        } else {
                            Toast.makeText(getApplicationContext(), "Some error occurred while logging you in \nPlease try after sometime", Toast.LENGTH_SHORT).show();
                        }

                    } else if (response.code() == 401) {
                        Toast.makeText(getApplicationContext(), "You are not a registered user", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(getApplicationContext(), "Error in getting response", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onFailure(Call<ResCommon> call, Throwable t) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Connection error", Toast.LENGTH_SHORT).show();
                }
            });

        }

    }

    private void method_forgot_pwd() {
        Toast.makeText(this, "Forgot Password!", Toast.LENGTH_SHORT).show();
    }

    private boolean isValidNumber(String number) {
        Pattern p;
        Matcher m;
        String NUMBER_STRING = "^[6-9]\\d{9}$";
        p = Pattern.compile(NUMBER_STRING);
        m = p.matcher(number);
        return m.matches();
    }

    private boolean isValidEmail(String email) {
        Pattern p;
        Matcher m;

        String EMAIL_STRING = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        p = Pattern.compile(EMAIL_STRING);
        m = p.matcher(email);

        return m.matches();
    }

//---------------------------------------- APIs -----------------------------------------------//

    LoginAPI getLoginAPIService(String baseUrl) {
        return RetrofitClient.getClient(baseUrl).create(LoginAPI.class);
    }

    interface LoginAPI {
        @POST("adminLogin")
        @FormUrlEncoded
        Call<ResCommon> login_user(@Field("mobile") String mobile,
                                   @Field("pass") String pass
        );
    }

    interface CheckSubscription {
        @POST("checkvalidityapi")
        @FormUrlEncoded
        Call<ResCommon> checkLicence(@Field("id") String id);
    }


}
