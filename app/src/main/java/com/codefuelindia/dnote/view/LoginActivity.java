package com.codefuelindia.dnote.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.codefuelindia.dnote.Common.SessionManager;
import com.codefuelindia.dnote.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    //LoginAPI loginAPI;
    private static final String BASE_URL = "http://code-fuel.in/healthbotics/api/auth/";
    //http://192.168.0.103/laravel/public/api/auth/login     local URL

    SessionManager sessionManager;

    EditText editText_email, editText_password;
    Button button_login;
    TextView textView_forget_pwd, textView_signUp_here;

    String email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //loginAPI = getLoginAPIService(BASE_URL);

        sessionManager = new SessionManager(this);

        if (sessionManager.checkLogin()) {
            Intent i = new Intent(this, DashNavigationActivity.class);

            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            startActivity(i);
            finish();
        }

        editText_email = findViewById(R.id.login_et_email);
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

    private void fake_login() {
        sessionManager.createLoginSession("name", "token", "u_id");
        startActivity(new Intent(LoginActivity.this, DashNavigationActivity.class));
        finish();
    }

    private void method_login() {

        email = editText_email.getText().toString();
        password = editText_password.getText().toString();

        if (email.equals("")) {
            editText_email.setError("E-mail is required");
            editText_email.requestFocus();

        } else if (!isValidEmail(email)) {
            editText_email.setError("Enter a valid e-mail address");
            editText_email.requestFocus();

        } else if (password.equals("")) {
            editText_password.setError("Password is required");
            editText_password.requestFocus();

        } else if (password.length() < 8) {
            editText_password.setError("Length of password must be >=8 characters");
            editText_password.requestFocus();

        } else {

            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Loading");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

        //    BodyLogin bodyLogin = new BodyLogin(email, password, true);

            /*loginAPI.login_user(bodyLogin).enqueue(new Callback<ResCommon>() {
                @Override
                public void onResponse(Call<ResCommon> call, Response<ResCommon> response) {

                    progressDialog.dismiss();


                    if (response.isSuccessful()) {

                        if (response.body() != null) {

                            switch (response.body().getMsg()) {

                                case "true":
                                    String name, token, u_id;
                                    name = response.body().getName();
                                    token = response.body().getAccess_token();
                                    u_id = response.body().getU_id();

                                    finish();
                                    sessionManager.createLoginSession(name, token, u_id);

                                    Intent i = new Intent(LoginActivity.this, ChooseDashActivity.class);
                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(i);
                                    break;

                                case "false":
                                    Toast.makeText(getApplicationContext(), "Your login credentials are not valid", Toast.LENGTH_SHORT).show();
                                    break;

                                case "block":
                                    Toast.makeText(getApplicationContext(), "Your account is currently blocked \nPlease contact administrative.", Toast.LENGTH_LONG).show();
                                    break;

                                case "xxx":
                                    Toast.makeText(getApplicationContext(), "You are not a registered user. \nPlease register first.", Toast.LENGTH_LONG).show();
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
            });*/

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

    /*LoginAPI getLoginAPIService(String baseUrl) {
        return RetrofitClient.getClient(baseUrl).create(LoginAPI.class);
    }

    interface LoginAPI {
        @Headers("X-Requested-With:XMLHttpRequest")
        @POST("login")
        Call<ResCommon> login_user(@Body BodyLogin bodyLogin);
    }*/

}
