package com.app.appathon.blooddonateapp.model;

/**
 * Created by IMRAN on 8/20/2017.
 */

public class Inbox {
    private String message;
    private String sendTime;
    private String senderName;
    private String senderPhone;
    public int count;

    public Inbox(){

    }

    public Inbox(String message, String sendTime, String senderName, String senderPhone, int count) {
        this.message = message;
        this.sendTime = sendTime;
        this.senderName = senderName;
        this.senderPhone = senderPhone;
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getSenderPhone() {
        return senderPhone;
    }

    public void setSenderPhone(String senderPhone) {
        this.senderPhone = senderPhone;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }
}
