package com.example.firebaseauthenticationandstoragetest.Models;

public class FriendsModel {
    String id, friends,date;

    public FriendsModel() {
    }

    public FriendsModel(String id, String friends, String date) {
        this.id = id;
        this.friends = friends;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFriends() {
        return friends;
    }

    public void setFriends(String friends) {
        this.friends = friends;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
