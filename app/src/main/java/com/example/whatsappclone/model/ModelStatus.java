package com.example.whatsappclone.model;

public class ModelStatus {
    String imgUrl;
    long timeStamp;
    
    public ModelStatus(String imgUrl, long timeStamp) {
        this.imgUrl = imgUrl;
        this.timeStamp = timeStamp;
    }
    
    public ModelStatus() {
    }
    
    public String getImgUrl() {
        return imgUrl;
    }
    
    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
    
    public long getTimeStamp() {
        return timeStamp;
    }
    
    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
