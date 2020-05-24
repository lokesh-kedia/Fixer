package com.fixer.fixer;

public class CommentMessage {
    String uid;
    String message;
    String name;
    String time;

    public CommentMessage() {

    }

    public CommentMessage(String uid, String message, String name, String time) {
        this.uid = uid;
        this.message = message;
        this.name = name;
        this.time = time;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
