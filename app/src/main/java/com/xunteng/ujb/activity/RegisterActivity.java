package com.xunteng.ujb.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.xunteng.ujb.R;
import com.xunteng.ujb.sp.Preference;

public class RegisterActivity extends AppCompatActivity {

    private EditText usernameEdit, mobileEdit, passwordEdit;
    private Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        usernameEdit = findViewById(R.id.username);
        mobileEdit = findViewById(R.id.mobile);
        passwordEdit = findViewById(R.id.password);

        registerButton = findViewById(R.id.register);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(usernameEdit.getText()) || TextUtils.isEmpty(mobileEdit.getText()) || TextUtils.isEmpty(passwordEdit.getText())) {
                    Toast.makeText(RegisterActivity.this, "用户名手机号密码均不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    RegisterTask registerTask = new RegisterTask(RegisterActivity.this);
                    registerTask.execute(usernameEdit.getText().toString(), mobileEdit.getText().toString(), passwordEdit.getText().toString());
                }
            }
        });

    }

    public class RegisterTask extends AsyncTask<String, Void, Boolean>{

        private Context context;
        private ProgressDialog progressDialog;
        String username, mobile, password;

        public RegisterTask(Context context_){
            this.context = context_;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(context);
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            if (isCancelled()) {
                return false;
            }
            username = strings[0];
            mobile = strings[1];
            password = strings[2];

            // 发送一个网络请求到服务器接口，接受接口返回的注册数据
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            progressDialog.dismiss();
            if (aBoolean) {
                Preference.setUsername(username);
                Preference.setPassword(password);
                Preference.setMobile(mobile);

                Toast.makeText(context, "注册成功，请登陆", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(context, LoginActivity.class));
            } else {

            }
        }

        @Override
        protected void onCancelled() {
            progressDialog.dismiss();

        }
    }

}
