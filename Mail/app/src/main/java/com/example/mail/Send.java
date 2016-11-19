package com.example.mail;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by 秦 on 2016/11/17.
 */

public class Send extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.s);
    }

    public void Send(View view) {
        new Thread() {
            @Override
            public void run() {
                try {
                    send();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
        Intent intent = new Intent(Send.this, MainActivity.class);
        startActivity(intent);
        Toast.makeText(this,"发送成功",Toast.LENGTH_SHORT).show();
    }

    void send() throws IOException{
        String sender;
        String password;
        EditText e_sender=(EditText)findViewById(R.id.e_sender);
        EditText e_subject=(EditText)findViewById(R.id.e_subject);
        EditText e_content=(EditText)findViewById(R.id.e_content);
        UserRepo repo=new UserRepo(this);
        ArrayList<HashMap<String,String>> studentList=repo.getUserList();
        HashMap<String,String> u= studentList.get(0);
        sender=u.get("account");
        password=u.get("password");

        String user = android.util.Base64.encodeToString(sender.substring(0, sender.indexOf("@")).getBytes(), Base64.NO_WRAP);  //截取出“cnsmtp01”并加密
        String pass = android.util.Base64.encodeToString(password.getBytes(),Base64.NO_WRAP);   //加密 “computer”

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
        System.out.println(reader.readLine()+"1");
        writter.println("rcpt to:<" + e_sender.getText().toString() +">");
        System.out.println(reader.readLine());
        writter.println("data");
        System.out.println(reader.readLine());
        writter.println("subject:"+e_subject.getText().toString());
        writter.println("from:" + sender);
        writter.println("to:" + e_sender.getText().toString());
        writter.println("Content-Type: text/plain;charset=\"UTF-8\"");  //如果发送正文必须加这个，而且下面要有一个空行
        writter.println();
        writter.println(e_content.getText().toString());
        writter.println(".");                                           //告诉服务器我发送的内容完毕了
        writter.println("");
        System.out.println(reader.readLine());
        writter.println("rset");
        System.out.println(reader.readLine());
        writter.println("quit");
        System.out.println(reader.readLine());
    }

    public void Back(View view) {
        Intent intent = new Intent(Send.this, MainActivity.class);
        startActivity(intent);
    }
}
