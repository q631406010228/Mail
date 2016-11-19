package com.example.mail;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public ArrayList<String> subject;
    int i=0;
    public String s;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        Context mContext = MainActivity.this;

        List<Inbox> mData = new LinkedList<Inbox>();
        MyThread thread=new MyThread();
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        while(i<subject.size()) {
            s = subject.get(i++).substring(subject.indexOf("From:") + 7);
            mData.add(new Inbox(s, s, s.substring(0, 1)));
        }
        InboxAdapter mAdapter = new InboxAdapter((LinkedList<Inbox>) mData, mContext);
        ListView list_inbox = (ListView) findViewById(R.id.listView);
        list_inbox.setAdapter(mAdapter);
    }

    public class MyThread extends Thread {
        public void run() {
            subject=receive();
        }
    }

    public void Send(View view) {
        Intent intent = new Intent(MainActivity.this, Send.class);
        startActivity(intent);
    }

    public ArrayList<String> receive ()
    {
        ArrayList<String> subject=new ArrayList<String>();
        Socket client = null;
        try
        {
            client = new Socket ("pop.163.com", 110);       // 创建一个连接到POP3服务程序的套接字。
            InputStream is = client.getInputStream ();
            BufferedReader sockin;                          //创建一个BufferedReader对象，以便从套接字读取输出。
            sockin = new BufferedReader (new InputStreamReader (is));
            OutputStream os = client.getOutputStream ();
            PrintWriter sockout;                            //创建一个PrintWriter对象，以便向套接字写入内容。
            sockout = new PrintWriter (os, true);           // true for auto-flush

            System.out.println (sockin.readLine ());        // 显示POP3握手信息。
            String cmd = "user 13996837484@163.com";        // 将用户名发送到POP3服务程序。
            System.out.println (cmd);
            sockout.println (cmd);
            System.out.println (sockin.readLine ());        // 读取POP3服务程序的回应消息。
            sockout.println ("pass sqq13996837484");         // 将密码发送到POP3服务程序。
            System.out.println (sockin.readLine ());        // 读取POP3服务程序的回应消息。
            sockout.println ("stat");
            String reply=sockin.readLine ();
            System.out.println (reply+"A");                     // 读取POP3服务程序的回应消息。
           // if(reply==null)
           //     return
            for(int i=1;i<1000;i++) {
                cmd = "retr "+i;
                sockout.println(cmd);                     // 将接收第一封邮件命令发送到POP3服务程序。
                reply = sockin.readLine();
                if(reply.contains("-ERR Unknown message"))
                    break;

                // 输入了RETR命令并且返回了成功的回应码，持续从套接字读取输出，
                // 直到遇到<CRLF>.<CRLF>。这时从套接字读出的输出就是邮件的内容。
                if (cmd.toLowerCase().startsWith("retr") )
                    do {
                        reply = sockin.readLine();
                        System.out.println("S:" + reply);
                        if (reply.contains("From")) {
                            subject.add(reply);
                        }
                        if (reply.length() > 0)
                            if (reply.charAt(0) == '.')
                                break;
                    } while (true);
            }
            sockout.println ("quit");                        // 将命令发送到POP3服务程序。
        }
        catch (IOException e)
        {
            System.out.println (e.toString ());
        }
        finally
        {
            try
            {  if (client != null)
                client.close ();
            }
            catch (IOException e)
            {
                System.out.println (e.toString ());
            }
        }
        return subject;
    }
}
