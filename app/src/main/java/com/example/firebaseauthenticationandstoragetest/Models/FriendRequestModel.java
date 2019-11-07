package com.example.firebaseauthenticationandstoragetest.Models;

public class FriendRequestModel {
   String id,request_type;

    public FriendRequestModel(String id, String request_type) {
        this.id = id;
        this.request_type = request_type;
    }

    public FriendRequestModel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRequest_type() {
        return request_type;
    }

    public void setRequest_type(String request_type) {
        this.request_type = request_type;
    }
}
