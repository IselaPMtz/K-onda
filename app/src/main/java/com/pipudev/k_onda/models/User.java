package com.pipudev.k_onda.models;

/**
 * Clase que almacena los usuarios a registrar en Firebase
 */

public class User {

    private String userID;
    private String userName;
    private String phoneNumber;
    private String userImage;
    private String userInfo;

    public User() {
    }

    public User(String userID, String userName, String phoneNumber, String userImage, String userInfo) {
        this.userID = userID;
        this.userName = userName;
        this.phoneNumber = phoneNumber;
        this.userImage = userImage;
        this.userInfo = userInfo;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(String userInfo) {
        this.userInfo = userInfo;
    }
}
