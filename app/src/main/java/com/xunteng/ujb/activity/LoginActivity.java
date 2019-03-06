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

public class LoginActivity extends AppCompatActivity {

    private EditText usernameEdit, passwordEdit;
    private Button forgetButton, loginButton, registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameEdit = findViewById(R.id.username);
        passwordEdit = findViewById(R.id.password);

        forgetButton = findViewById(R.id.forgetor_pwd);
        forgetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ResetPwdActivity.class));
            }
        });
        loginButton = findViewById(R.id.login);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(usernameEdit.getText()) || TextUtils.isEmpty(passwordEdit.getText())) {
                    Toast.makeText(LoginActivity.this, "账号密码不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    LoginTask loginTask = new LoginTask(LoginActivity.this);
                    loginTask.execute(usernameEdit.getText().toString(), passwordEdit.getText().toString());
                }
            }
        });
        registerButton = findViewById(R.id.register);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });


    }


    public class LoginTask extends AsyncTask<String, Void, Boolean> {

        private Context context;
        private ProgressDialog progressDialog;
        String username, password;

        public LoginTask(Context context_){
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
            password = strings[1];

            if (username.equals(Preference.getUsername()) && password.equals(Preference.getPassword())) {
                return true;
            }
            // 发送一个网络请求到服务器接口，接受接口返回的登陆用户数据
            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            progressDialog.dismiss();
            if (aBoolean) {
                Preference.setIsLogin(true);
                Toast.makeText(context, "登陆成功", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(context, HomeActivity.class));
            } else {
                Toast.makeText(context, "您输入的账号或密码有误，请重新输入", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            progressDialog.dismiss();
        }
    }

}
