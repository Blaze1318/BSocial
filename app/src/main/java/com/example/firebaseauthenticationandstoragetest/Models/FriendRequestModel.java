package com.example.firebaseauthenticationandstoragetest.Models;

public class FriendRequestModel {
    String requestTo, requestFrom,requesterName,requesterImage,requesterEmail,requestKey,status;

    public FriendRequestModel() {
    }

    public FriendRequestModel(String requestTo, String requestFrom, String requesterName, String requesterImage, String requesterEmail,String requestKey ,String status) {
        this.requestTo = requestTo;
        this.requestFrom = requestFrom;
        this.requesterName = requesterName;
        this.requesterImage = requesterImage;
        this.requesterEmail = requesterEmail;
        this.status = status;
    }

    public String getRequestKey() {
        return requestKey;
    }

    public void setRequestKey(String requestKey) {
        this.requestKey = requestKey;
    }

    public String getRequestTo() {
        return requestTo;
    }

    public void setRequestTo(String requestTo) {
        this.requestTo = requestTo;
    }

    public String getRequestFrom() {
        return requestFrom;
    }

    public void setRequestFrom(String requestFrom) {
        this.requestFrom = requestFrom;
    }

    public String getRequesterName() {
        return requesterName;
    }

    public void setRequesterName(String requesterName) {
        this.requesterName = requesterName;
    }

    public String getRequesterImage() {
        return requesterImage;
    }

    public void setRequesterImage(String requesterImage) {
        this.requesterImage = requesterImage;
    }

    public String getRequesterEmail() {
        return requesterEmail;
    }

    public void setRequesterEmail(String requesterEmail) {
        this.requesterEmail = requesterEmail;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
