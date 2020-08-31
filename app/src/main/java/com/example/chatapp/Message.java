package com.example.chatapp;

import java.io.Serializable;

public class Message implements Serializable {
    private String text;
    private String senderId;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }
}
