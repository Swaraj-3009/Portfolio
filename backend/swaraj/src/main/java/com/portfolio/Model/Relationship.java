package com.portfolio.model;

public class Relationship {
    private int followers;
    private int friends;


    //getters
    public int getFollowers() {
        return followers;
    }
    public int getFriends(){
        return friends;
    }

    //setters
    public void setFollowers(int followers) {
        this.followers = followers;
    }
    public void setFriends(int friends){
        this.friends = friends;
    }
}
