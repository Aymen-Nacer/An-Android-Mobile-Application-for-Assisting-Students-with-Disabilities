package com.example.project;


import java.io.Serializable;
import java.util.Date;

public class Event implements Serializable {
    private String documentID;
    private int eventID;
    private String name;
    private Date time;


    public Event() { }
    public Event( String documentID,int eventID ,String name, Date time) {
        this.documentID = documentID;
        this.name=name;
        this.eventID=eventID;
        this.time=time;
    }

    public String getDocumentID() {
        return this.documentID;
    }

    public void setDocumentID(String documentID) {
        this.documentID = documentID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public int getEventID() {
        return eventID;
    }

    public void setEventID(int eventID) {
        this.eventID = eventID;
    }

    public Date getTime() {
        return time;
    }
}