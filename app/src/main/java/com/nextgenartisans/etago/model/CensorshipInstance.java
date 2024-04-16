package com.nextgenartisans.etago.model;

import java.util.Map;

public class CensorshipInstance {
    private String censorshipID;
    private String userID;
    private Map<String, Double> capturedClasses;
    private Object dateCensored; // Use Object type to hold either a Date or null

    public CensorshipInstance() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public CensorshipInstance(String censorshipID, String userID, Map<String, Double> capturedClasses, Object dateCensored) {
        this.censorshipID = censorshipID;
        this.userID = userID;
        this.capturedClasses = capturedClasses;
        this.dateCensored = dateCensored;
    }

    public String getCensorshipID() {
        return censorshipID;
    }

    public void setCensorshipID(String censorshipID) {
        this.censorshipID = censorshipID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public Map<String, Double> getCapturedClasses() {
        return capturedClasses;
    }

    public void setCapturedClasses(Map<String, Double> capturedClasses) {
        this.capturedClasses = capturedClasses;
    }

    public Object getDateCensored() {
        return dateCensored;
    }

    public void setDateCensored(Object dateCensored) {
        this.dateCensored = dateCensored;
    }
}