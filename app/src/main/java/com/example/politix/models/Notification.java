package com.example.politix.models;

public class Notification {
    String text,timestamp;
    public Notification() {
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public Notification(String text,String timestamp) {
        this.text = text;
        this.timestamp=timestamp;
    }
}
