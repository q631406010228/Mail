package com.example.mail;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by 秦 on 2016/11/15.
 */

public class SplashActivity extends AppCompatActivity {
    private Context context;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        context=SplashActivity.this;
        Handler x = new Handler();
        x.postDelayed(new splashhandler(), 2000);
    }

    class splashhandler implements Runnable{
        public void run() {
            UserRepo repo=new UserRepo(context);
            ArrayList<HashMap<String, String>> userList=repo.getUserList();
            if(userList.size()!=0){
                startActivity(new Intent(getApplication(),MainActivity.class));
                SplashActivity.this.finish();
            }else{
                startActivity(new Intent(getApplication(),User_login.class));
                SplashActivity.this.finish();
            }
        }
    }
}
