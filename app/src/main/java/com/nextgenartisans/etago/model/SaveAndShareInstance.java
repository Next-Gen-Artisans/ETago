package com.nextgenartisans.etago.model;

public class SaveAndShareInstance {
    private String ssID;
    private String userID;
    private int numShareInstance;
    private int numSaveInstance;
    private Object dateShared; // Use Object type to hold either a Date or null

    public SaveAndShareInstance() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public SaveAndShareInstance(String ssID, String userID, int numShareInstance, int numSaveInstance, Object dateShared) {
        this.ssID = ssID;
        this.userID = userID;
        this.numShareInstance = numShareInstance;
        this.numSaveInstance = numSaveInstance;
        this.dateShared = dateShared;
    }

    public String getSsID() {
        return ssID;
    }

    public void setSsID(String ssID) {
        this.ssID = ssID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public int getNumShareInstance() {
        return numShareInstance;
    }

    public void setNumShareInstance(int numShareInstance) {
        this.numShareInstance = numShareInstance;
    }

    public int getNumSaveInstance() {
        return numSaveInstance;
    }

    public void setNumSaveInstance(int numSaveInstance) {
        this.numSaveInstance = numSaveInstance;
    }

    public Object getDateShared() {
        return dateShared;
    }

    public void setDateShared(Object dateShared) {
        this.dateShared = dateShared;
    }
}
