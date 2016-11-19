package com.example.mail;

import android.graphics.Bitmap;

/**
 * Created by 秦 on 2016/11/17.
 */

public class Inbox {
    private String sender;
    private String content;
    private String icon;

    public Inbox(String sender, String content,String icon) {
        this.sender =sender;
        this.content = content;
        this.icon=icon;
    }

    public String getSender() {
        return sender;
    }

    public String getContent() {
        return content;
    }

    public String getIcon() {
       return icon;
    }
}
