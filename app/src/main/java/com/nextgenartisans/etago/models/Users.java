package com.nextgenartisans.etago.models;

import com.google.type.Date;

public class Users {

    // Attributes

    private String userID;
    private String username;
    private String email;
    private String profilePic; // URL or path to the image
    private boolean userAgreedMedia;
    private boolean userAgreedTermsAndPrivacyPolicy;
    private int numCensoredImgs;

    public Users(){

    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public boolean isUserAgreedMedia() {
        return userAgreedMedia;
    }

    public void setUserAgreedMedia(boolean userAgreedMedia) {
        this.userAgreedMedia = userAgreedMedia;
    }

    public boolean isUserAgreedTermsAndPrivacyPolicy() {
        return userAgreedTermsAndPrivacyPolicy;
    }

    public void setUserAgreedTermsAndPrivacyPolicy(boolean userAgreedTermsAndPrivacyPolicy) {
        this.userAgreedTermsAndPrivacyPolicy = userAgreedTermsAndPrivacyPolicy;
    }

    public int getNumCensoredImgs() {
        return numCensoredImgs;
    }

    public void setNumCensoredImgs(int numCensoredImgs) {
        this.numCensoredImgs = numCensoredImgs;
    }
}
