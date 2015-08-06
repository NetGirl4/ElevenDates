package com.elevenfifty.www.elevendates.Models;

import com.elevenfifty.www.elevendates.Constants;
import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 *
 * Created by bkeck on 7/6/15.
 *
 */

@ParseClassName("DateChat")
public class DateChat extends ParseObject {
    /*
    Three variables go here, for the following:
    A) variable named chatRoom, and is a String
    2) variable named sender, and is a DateUser
    D) variable named chatText, and is a String
     */

    private String chatRoom;
    private DateUser sender;
    private String ChatText;

    public void setChatRoom(String chatRoom) {
        put(Constants.CHAT_ROOM, chatRoom);
    }

    public String setChatRoom() {
        return getString(Constants.CHAT_ROOM);
    }

    public DateUser getSender() {
            return (DateUser)get(Constants.SENDER);
        }

    public void setSender(DateUser sender) {
        put(Constants.SENDER, sender);
    }

    public String getChatText() {
        return getString(Constants.CHAT_TEXT);
    }

    public void setChatText(String chatText) {
        put(Constants.CHAT_TEXT, chatText);
    }






    // Get some things, set some things, and don't forget this is Parse!
    // Oh, and *cough* *cough* constants *cough*



}
