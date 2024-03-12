package com.driver;

import java.util.Date;

public class Message {
    private int id;
    private String content;
    private Date timestamp;

    public Message(int id, String content, Date timeStamp){
        this.id=id;
        this.content=content;
        this.timestamp=timeStamp;
    }
    public Message(int id, String content){
        this.id=id;
        this.content=content;
    }
    public Date getDate(){
        return this.timestamp;
    }
    public String getContent(){
        return this.content;
    }
    public int getId(){
        return this.id;
    }
}
