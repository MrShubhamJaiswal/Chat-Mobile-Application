package com.example.whatsappclone.model;

import java.util.ArrayList;

public class ModelUserStatus {
    String name,profileImage;
    long lastUpdated;
    ArrayList<ModelStatus> statuses;
    
    public ModelUserStatus(String name, String profileImage, long lastUpdated, ArrayList<ModelStatus> statuses) {
        this.name = name;
        this.profileImage = profileImage;
        this.lastUpdated = lastUpdated;
        this.statuses = statuses;
    }
    
    public ModelUserStatus() {
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getProfileImage() {
        return profileImage;
    }
    
    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }
    
    public long getLastUpdated() {
        return lastUpdated;
    }
    
    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
    
    public ArrayList<ModelStatus> getStatuses() {
        return statuses;
    }
    
    public void setStatuses(ArrayList<ModelStatus> statuses) {
        this.statuses = statuses;
    }
}
