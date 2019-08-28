package com.example.dcris.myapplication.model;

public class Message {

    private static String usertype;
    private String nickname;
    private String message ;

    public Message(){

    }
    public Message(String usertype, String nickname, String message) {
        this.usertype =usertype;
        this.nickname = nickname;
        this.message = message;
    }

    public static String getUsertype() {
        return usertype;
    }

    public void setUsertype(String usertype) {
        this.usertype = usertype;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}