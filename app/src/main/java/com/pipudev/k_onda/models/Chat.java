package com.pipudev.k_onda.models;

import java.util.ArrayList;

public class Chat {

    private String chatID;
    private long timeStamp;
    private ArrayList<String> chatIDs; //para un grupo con varios contactos

    public Chat() {
    }

    public Chat(String chatID, long timeStamp, ArrayList<String> chatIDs) {
        this.chatID = chatID;
        this.timeStamp = timeStamp;
        this.chatIDs = chatIDs;
    }

    public String getChatID() {
        return chatID;
    }

    public void setChatID(String chatID) {
        this.chatID = chatID;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public ArrayList<String> getChatIDs() {
        return chatIDs;
    }

    public void setChatIDs(ArrayList<String> chatIDs) {
        this.chatIDs = chatIDs;
    }
}
