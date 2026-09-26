package com.portfolio.swaraj.Model;

public class MyProfile {
    private String name;
    private String email;
    private String GithubURL;
    private String LinkedinURL;
    private String phone;
    private String address;
    private String aboutMe;
    private String profileImage;

    //constructor
    public MyProfile(String name, String email, String phone, String address, String aboutMe, String profileImage) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.aboutMe = aboutMe;
        this.profileImage = profileImage;
    }

    //getters
    public String getName() {
        return name;
    }
    public String getEmail() {
        return email;
    }
    public String getGithubURL() {
        return GithubURL;
    }
    public String getLinkedinURL() {
        return LinkedinURL;
    }
    public String getPhone() {
        return phone;
    }
    public String getAddress() {
        return address;
    }
    public String getAboutMe() {
        return aboutMe;
    }
    public String getProfileImage() {
        return profileImage;
    }

    
    //setters
    public void setName(String name) {
        this.name = name;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setGithubURL(String githubURL) {
        this.GithubURL = githubURL;
    }
    public void setLinkedinURL(String linkedinURL) {
        this.LinkedinURL = linkedinURL;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public void setAboutMe(String aboutMe) {
        this.aboutMe = aboutMe;
    }
    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }
}
