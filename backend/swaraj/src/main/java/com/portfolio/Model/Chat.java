package com.portfolio.model;

import java.time.LocalDateTime;

public class Chat {
    private String message;
    private String sender;
    private String reciever;
    private LocalDateTime time;


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
    public LocalDateTime getTime() {
        return time;
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
    public void setTime(LocalDateTime time) {
        this.time = time;
    }
}