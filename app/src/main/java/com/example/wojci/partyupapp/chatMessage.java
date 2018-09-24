package com.example.wojci.partyupapp;

import java.util.Date;

/**
 * Created by wojci on 26.04.2018.
 */

public class chatMessage {

    private String messageText;
    private String messageUser;
    private long messageTime;

    public chatMessage()
    {

    }

    public chatMessage(String messageText, String messageUser)
    {
        this.messageText = messageText;
        this.messageUser = messageUser;
        messageTime = new Date().getTime();
    }
    public String getMessageText() {
        return messageText;
    }
    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }
    public String getMessageUser() {
        return messageUser;
    }
    public void setMessageUser(String messageUser) {
        this.messageUser = messageUser;
    }
    public long getMessageTime() {
        return messageTime;
    }
    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }

}
