package com.example.whatsappclone.model;

public class User {
    String uid,name,mobileNumber,profileImage;

    public User() {
    }

    public User(String uid, String name, String mobileNumber, String profileImage) {
        this.uid = uid;
        this.name = name;
        this.mobileNumber = mobileNumber;
        this.profileImage = profileImage;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }
}
