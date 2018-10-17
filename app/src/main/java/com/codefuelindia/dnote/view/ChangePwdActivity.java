package com.codefuelindia.dnote.view;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.codefuelindia.dnote.Common.RetrofitClient;
import com.codefuelindia.dnote.Model.ResCommon;
import com.codefuelindia.dnote.R;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public class ChangePwdActivity extends AppCompatActivity {

    private static final String BASE_URL = "http://code-fuel.in/dnote/api/";
    ChangePasswordAPI changePasswordAPI;

    EditText editText_pwd1, editText_pwd2;
    Button button_submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pwd);

        changePasswordAPI = getChangePasswordAPIService(BASE_URL);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        editText_pwd1 = findViewById(R.id.changePwd_et_password1);
        editText_pwd2 = findViewById(R.id.changePwd_et_password2);
        button_submit = findViewById(R.id.changePwd_btn_submit);

        button_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                methodSubmit();
            }
        });

    }

    private void methodSubmit() {

        String pwd1, pwd2;

        pwd1 = editText_pwd1.getText().toString();
        pwd2 = editText_pwd2.getText().toString();

        if (pwd1.equals("")) {
            editText_pwd1.setError("Password is required");
            editText_pwd1.requestFocus();

        } else if (pwd1.length() < 8) {
            editText_pwd1.setError("Length of password must be >=8 characters");
            editText_pwd1.requestFocus();

        } else if (pwd2.equals("")) {
            editText_pwd2.setError("Confirm password is required");
            editText_pwd2.requestFocus();

        } else if (pwd2.length() < 8) {
            editText_pwd2.setError("Length of password must be >=8 characters");
            editText_pwd2.requestFocus();

        } else if (!pwd1.equals(pwd2)) {
            editText_pwd2.setError("Both password must be same");
            editText_pwd2.requestFocus();

        } else {
            // API Call
            Toast.makeText(this, "DONE", Toast.LENGTH_SHORT).show();
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
                                       @Field("password") String password
        );
    }

}
