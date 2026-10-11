package com.portfolio.model;

import java.sql.Time;

public class Chat {
    private String message;
    private String sender;
    private String reciever;
    private Time time;
    private int chatId;


    //getters
    public String getMessage() {
        return message;
    }
    public String getSender() {
        return sender;
    }
    public String getReciever() {
        return reciever;
    }
    public Time getTime() {
        return time;
    }
    public int getChatId(){
        return chatId;
    }


    //setters
    public void setMessage(String message) {
        this.message = message;
    }
    public void setSender(String sender) {
        this.sender = sender;
    }
    public void setReciever(String reciever) {
        this.reciever = reciever;
    }
    public void setTime(Time time) {
        this.time = time;
    }
    public void setChatId(int chatId){
        this.chatId = chatId;
    }
}