package com.graduation.fuyu.ilive;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.dfqin.grantor.PermissionListener;
import com.github.dfqin.grantor.PermissionsUtil;
import com.graduation.fuyu.ilive.connection.HClient;
import com.graduation.fuyu.ilive.pojo.User;
import com.graduation.fuyu.ilive.service.UserPostService;
import com.graduation.fuyu.ilive.widget.CircleImageView;
import com.graduation.fuyu.ilive.widget.Constants;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class SetHeadActivity extends AppCompatActivity {
    private CircleImageView front_cover;
    private Button gallery;
    private Button camera;
    private Toolbar toolbar;
    private String username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initWindow();
        setContentView(R.layout.activity_set_head);
        front_cover=findViewById(R.id.activity_set_head_im);
        gallery=findViewById(R.id.activity_set_head_btn_gallery);
        camera=findViewById(R.id.activity_set_head_btn_camera);
        toolbar=findViewById(R.id.activity_set_head_toolbar);
        User user=new User();
        Glide.with(SetHeadActivity.this).load("http://139.196.124.195:8080/LoginServer/head/"+ user.getHead()).dontAnimate().placeholder(R.mipmap.header).error(R.mipmap.header).into(front_cover);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestPermission();
            }
        });
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestPermissions();
            }
        });

        @SuppressLint("WrongConstant")
        SharedPreferences sharedPreferences=getSharedPreferences("ilive", Context.MODE_APPEND);
        username=sharedPreferences.getString("username","匿名");
    }
    private void initWindow() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
    }
    private void requestPermission() {
        PermissionsUtil.requestPermission(getApplication(), new PermissionListener() {
            @Override
            public void permissionGranted(@NonNull String[] permissions) {
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, Constants.IMAGE_UNSPECIFIED);
                startActivityForResult(intent, Constants.ALBUM_REQUEST_CODE);
            }

            @Override
            public void permissionDenied(@NonNull String[] permissions) {
                Toast.makeText(SetHeadActivity.this, "请求权限失败", Toast.LENGTH_LONG).show();
            }
        }, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},false,null);
    }

    private void requestPermissions() {
        PermissionsUtil.requestPermission(getApplication(), new PermissionListener() {
            @Override
            public void permissionGranted(@NonNull String[] permissions) {


                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Environment.
                        getExternalStorageDirectory(), "temp.jpg")));
                startActivityForResult(intent, Constants.CAMERA_REQUEST_CODE);
            }

            @Override
            public void permissionDenied(@NonNull String[] permissions) {
                Toast.makeText(SetHeadActivity.this, "请求权限失败", Toast.LENGTH_LONG).show();
            }
        }, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.CAMERA},false,null);
    }
    Bitmap photo;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            //相册返回
            case Constants.ALBUM_REQUEST_CODE:
                if (data == null) {
                    return;
                }
                startCrop(data.getData());
                break;
            //相机返回
            case Constants.CAMERA_REQUEST_CODE:
                File picture = new File(Constants.ImageTempPath);
                startCrop(Uri.fromFile(picture));
                break;
            //裁剪
            case Constants.CROP_REQUEST_CODE:
                photo = getLoacalBitmap(Constants.ImageTempPath);
//                Log.e("crop1", Constants.ImageTempPath);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                photo.compress(Bitmap.CompressFormat.JPEG, 75, stream);// (0-100)压缩文件
                front_cover.setImageBitmap(photo);
                new UpLoadImage().execute(Constants.ImageTempPath);
                break;
            default:
                break;
        }
    }

    class UpLoadImage extends AsyncTask<String,Void,Boolean> {

        HClient hClient=new HClient();
        @Override
        protected Boolean doInBackground(String... strings) {
            String url=strings[0];
            if(!isGrantExternalRW(SetHeadActivity.this)){
                return null;
            }
            return hClient.UpLoadHead(url,username);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (aBoolean){
                new getHeadByName().execute();
            }else {
                Toast.makeText(SetHeadActivity.this,"设置失败",Toast.LENGTH_SHORT).show();
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    class getHeadByName extends AsyncTask<Void,Void,String>{

        @Override
        protected String doInBackground(Void... voids) {
            List<NameValuePair> params=new ArrayList<>();
            params.add(new BasicNameValuePair("username", username));
            return UserPostService.getHeadByName(params);
        }

        @Override
        protected void onPostExecute(String s) {
            User.head=s;
            Glide.with(SetHeadActivity.this).load("http://139.196.124.195:8080/LoginServer/head/"+ s).dontAnimate().placeholder(R.mipmap.header).error(R.mipmap.header).into(front_cover);
            Toast.makeText(SetHeadActivity.this,"设置成功",Toast.LENGTH_SHORT).show();
        }
    }
    /**
     * 开始裁剪
     *
     * @param uri
     */
    private void startCrop(Uri uri) {
        //调用Android系统自带的一个图片剪裁页面,
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, Constants.IMAGE_UNSPECIFIED);
        intent.putExtra("crop", "true");//进行修剪
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 64);
        intent.putExtra("outputY", 64);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Environment.
                getExternalStorageDirectory(), "temp.jpg")));
        startActivityForResult(intent, Constants.CROP_REQUEST_CODE);
    }

    public static Bitmap getLoacalBitmap(String url) {
        try {
            FileInputStream fis = new FileInputStream(url);
            return BitmapFactory.decodeStream(fis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean isGrantExternalRW(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && activity.checkSelfPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            activity.requestPermissions(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 1);

            return false;
        }

        return true;
    }
    int PERMISSIONS_CODE =1;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSIONS_CODE) {
            for (int i = 0; i < permissions.length; i++) {
                String permission = permissions[i];
                int grantResult = grantResults[i];

                if (permission.equals(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    if (grantResult == PackageManager.PERMISSION_GRANTED) {
                        //授权成功后的逻辑
                    } else {
                        SetHeadActivity.this.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_CODE);
                    }
                }
            }
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
