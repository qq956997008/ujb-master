package com.xunteng.ujb.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.xunteng.ujb.R;
import com.xunteng.ujb.entity.VersionInfo;
import com.xunteng.ujb.sp.Preference;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private ImageView imageView;

    private final int UPDATA_NONEED = 0;
    private final int UPDATA_CLIENT = 1;
    private final int GET_UNDATAINFO_ERROR = 2;
    private final int SDCARD_NOMOUNTED = 3;
    private final int DOWN_ERROR = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.image_view);

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.welcome_alpha);
        imageView.startAnimation(animation);

        new CheckVersionInfoTask(this).execute();

    }

    public class CheckVersionInfoTask extends AsyncTask<Void, Void, String> {

        private Context context;
        private ProgressDialog progressDialog;

        private CheckVersionInfoTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(context);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Void... voids) {
            return getData();
        }

        @Override
        protected void onPostExecute(String s) {
            progressDialog.dismiss();

            Gson gson = new Gson();
            try {
                JSONObject jsonObject = new JSONObject(s);
                VersionInfo versionInfo = gson.fromJson(jsonObject.getString("data"), VersionInfo.class);
                Log.d("version_info", versionInfo.toString());

                if (versionInfo.getVersionCode() > getVersionCode()) {
                    showUpdataDialog(versionInfo);
                } else {
                    if (Preference.getIsFristOpen()) {
                        startActivity(new Intent(MainActivity.this, GuideActivity.class));
                    } else {
                        if (Preference.getIsLogin()) {
                            startActivity(new Intent(MainActivity.this, HomeActivity.class));
                        } else {
                            startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onCancelled() {
            progressDialog.dismiss();
        }

    }

    public class DownloadApkTask extends AsyncTask<Void, Void, File> {

        private Context context;
        private VersionInfo versionInfo;
        private ProgressDialog progressDialog;

        private DownloadApkTask(Context context, VersionInfo versionInfo) {
            this.context = context;
            this.versionInfo = versionInfo;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(context);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setMessage("正在下载更新");
            progressDialog.show();
        }

        @Override
        protected File doInBackground(Void... voids) {
            try {
                return getFileFromServer(versionInfo.getUrl(), progressDialog);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(File file) {
            progressDialog.dismiss();
            installApk(file);
        }

        @Override
        protected void onCancelled() {
            progressDialog.dismiss();
        }
    }

    protected void showUpdataDialog(final VersionInfo versionInfo) {
        AlertDialog.Builder builer = new AlertDialog.Builder(this);
        builer.setTitle("当前版本不是最新版本，确定下载新版本？");
        builer.setIcon(R.drawable.updateicon);
        builer.setMessage(versionInfo.getRemarks());
        // 当点确定按钮时从服务器上下载 新的apk 然后安装
        builer.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    Toast.makeText(getApplicationContext(), "SD卡不可用", Toast.LENGTH_SHORT).show();
                } else {
                    DownloadApkTask downloadApkTask = new DownloadApkTask(MainActivity.this, versionInfo);
                    downloadApkTask.execute();
                }
            }
        });
        // 当点取消按钮时进行登录
        builer.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (Preference.getIsFristOpen()) {
                    startActivity(new Intent(MainActivity.this, GuideActivity.class));
                } else {
                    if (Preference.getIsLogin()) {
                        startActivity(new Intent(MainActivity.this, HomeActivity.class));
                    } else {
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    }
                }
                finish();
            }
        });
        AlertDialog dialog = builer.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    /**
     * 安装 apk 文件
     *
     * @param apkFile
     */
    public void installApk(File apkFile) {
        Intent installApkIntent = new Intent();
        installApkIntent.setAction(Intent.ACTION_VIEW);
        installApkIntent.addCategory(Intent.CATEGORY_DEFAULT);
        installApkIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            installApkIntent.setDataAndType(FileProvider.getUriForFile(getApplicationContext(), "com.xunteng.ujb.file_provider", apkFile), "application/vnd.android.package-archive");
            installApkIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            installApkIntent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
        }

//        if (getPackageManager().queryIntentActivities(installApkIntent, 0).size() > 0) {
//            startActivity(installApkIntent);
//        }
        startActivity(installApkIntent);
    }

    private int getVersionCode() {
        try {
            PackageManager packageManager = getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public String getData() {
        try {
            String path = "https://api.teq6.com/dz/app/version/com.hezy.guide.phone/android/GA/latest?versionCode=1";
            URL url = new URL(path);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(5000); //连接超时时间
            connection.setReadTimeout(5000); //读取超时时间
            connection.setRequestMethod("GET"); // 设置请求的方法是Post

            if (connection.getResponseCode() == 200) {
                InputStream inputStream = connection.getInputStream();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                int len = 0;
                byte buffer[] = new byte[1024];
                while ((len = inputStream.read(buffer)) != -1) {
                    baos.write(buffer, 0, len);
                }
                inputStream.close();
                baos.close();
                String result = new String(baos.toByteArray());
                Log.v("return_data", result);
                return result;
            } else {
                Log.v("return_code", "" + connection.getResponseCode());
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String postData() {
        try {
            String path = "https://api.teq6.com/dz/app/device";
            URL url = new URL(path);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(5000); //连接超时时间
            connection.setReadTimeout(5000); //读取超时时间
            connection.setDoInput(true); // 设置请求允许输入
            connection.setDoOutput(true); // 设置请求允许输出
            connection.setRequestProperty("Content-type", "application/json"); // 配置请求的内容类型是json
            connection.setRequestMethod("POST"); // 设置请求的方法是Post

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("uuid", "123456");
            jsonObject.put("name", "admin");
            jsonObject.put("version", "111");
            String postData = jsonObject.toString();
            Log.v("post_data", postData); // {"uuid":"123456","name":"admin","version":"111"}

            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(postData.getBytes());
            outputStream.flush();

            if (connection.getResponseCode() == 200) {
                InputStream inputStream = connection.getInputStream();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                int len = 0;
                byte buffer[] = new byte[1024];
                while ((len = inputStream.read(buffer)) != -1) {
                    baos.write(buffer, 0, len);
                }
                inputStream.close();
                baos.close();
                String result = new String(baos.toByteArray());
                Log.v("return_data", result);
                return result;
            } else {
                Log.v("return_code", "" + connection.getResponseCode());
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 从服务器下载apk
     *
     * @param path
     * @param progressDialog
     * @return File
     */
    public File getFileFromServer(String path, ProgressDialog progressDialog) throws IOException {
        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(5000);
        progressDialog.setMax(conn.getContentLength()); // 获取到文件的大小
        InputStream is = conn.getInputStream();
        File file = new File(Environment.getExternalStorageDirectory(),"update.apk");
        if (!file.exists()) {
            boolean result = file.createNewFile();
            if (result) {
                FileOutputStream fos = new FileOutputStream(file);
                BufferedInputStream bis = new BufferedInputStream(is);
                byte[] buffer = new byte[1024];
                int len;
                int total = 0;
                while ((len = bis.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                    total += len;
                    progressDialog.setProgress(total); // 获取当前下载量
                }
                fos.close();
                bis.close();
                is.close();
                return file;
            } else {
                return null;
            }
        } else {
            FileOutputStream fos = new FileOutputStream(file);
            BufferedInputStream bis = new BufferedInputStream(is);
            byte[] buffer = new byte[1024];
            int len;
            int total = 0;
            while ((len = bis.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
                total += len;
                progressDialog.setProgress(total); // 获取当前下载量
            }
            fos.close();
            bis.close();
            is.close();
            return file;
        }

    }

}
