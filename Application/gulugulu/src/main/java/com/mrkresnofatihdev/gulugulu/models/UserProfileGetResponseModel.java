package com.mrkresnofatihdev.gulugulu.models;

public class UserProfileGetResponseModel {
    private String username;
    private String fullname;
    private String avatar;
    private String bio;
    private String email;

    public UserProfileGetResponseModel() {
    }

    public UserProfileGetResponseModel(UserProfileEntity userProfileEntity) {
        this.username = userProfileEntity.getUsername();
        this.fullname = userProfileEntity.getFullname();
        this.avatar = userProfileEntity.getAvatar();
        this.bio = userProfileEntity.getBio();
        this.email = userProfileEntity.getEmail();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
