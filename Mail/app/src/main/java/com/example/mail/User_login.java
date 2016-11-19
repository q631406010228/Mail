package com.example.mail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import static java.sql.Types.NULL;

/**
 * Created by 秦 on 2016/11/15.
 */

public class User_login extends AppCompatActivity{
    EditText e1;
    EditText e2;
    ImageView m1, m2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.user_login);
        init();
    }

    private void init() {
        // TODO Auto-generated method stub
        e1= (EditText) findViewById(R.id.e_mail);
        e2= (EditText) findViewById(R.id.e_password);
        m1 = (ImageView) findViewById(R.id.i_del_mail);
        m2 = (ImageView) findViewById(R.id.i_del_password);
        // 添加清楚监听器大气
        EditTextClearTools.addclerListener(e1, m1);
        EditTextClearTools.addclerListener(e2, m2);
    }

    public void Login(View view) {
        e1= (EditText) findViewById(R.id.e_mail);
        e2= (EditText) findViewById(R.id.e_password);
        String sender=e1.getText().toString();
        String password=e2.getText().toString();
        if(sender.length() == 0||password.length() == 0){
            Toast.makeText(this,"输入不能为空",Toast.LENGTH_SHORT).show();
        }
        else{
            new Thread(){
                public void run() {
                    try {
                        Context context=User_login.this;
                        String sender=e1.getText().toString();
                        String password=e2.getText().toString();
                        String user = android.util.Base64.encodeToString(sender.substring(0, sender.indexOf("@")).getBytes(), Base64.NO_WRAP);  //截取出“cnsmtp01”并加密
                        String pass = android.util.Base64.encodeToString(password.getBytes(), Base64.NO_WRAP);   //加密 “computer”
                        Socket socket = new Socket("smtp.163.com", 25);  //smtp服务使用25号端口监听
                        InputStream inputStream = socket.getInputStream();
                        OutputStream outputStream = socket.getOutputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                        PrintWriter writter = new PrintWriter(outputStream,true);  //这个true很关键，即时发出

                        writter.println("HELO 1");
                        System.out.println(reader.readLine());

                        writter.println("auth login");
                        System.out.println(reader.readLine());
                        writter.println(user);
                        System.out.println(reader.readLine());
                        writter.println(pass);
                        System.out.println(reader.readLine());
                        writter.println("mail from:<" + sender +">");
                        String buffer=reader.readLine();
                        if(buffer.contains("235 Authentication successful")){
                            UserRepo repo=new UserRepo(context);
                            User u=new User(e1.getText().toString(),e2.getText().toString());
                            repo.insert(u);
                            Intent intent = new Intent(User_login.this, MainActivity.class);
                            startActivity(intent);
                        } else{
                            Toast.makeText(context,"账号或密码输入错误",Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }
    }
}
