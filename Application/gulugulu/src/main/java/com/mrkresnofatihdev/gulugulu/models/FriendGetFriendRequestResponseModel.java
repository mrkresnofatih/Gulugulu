package com.mrkresnofatihdev.gulugulu.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class FriendGetFriendRequestResponseModel implements IJsonSerializable {
    private String username;
    private String createdAt;
    private UserProfileGetResponseModel profile;

    public FriendGetFriendRequestResponseModel() {
    }

    public FriendGetFriendRequestResponseModel(
            String username,
            String createdAt,
            UserProfileGetResponseModel profile) {
        this.username = username;
        this.createdAt = createdAt;
        this.profile = profile;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public UserProfileGetResponseModel getProfile() {
        return profile;
    }

    public void setProfile(UserProfileGetResponseModel profile) {
        this.profile = profile;
    }

    @Override
    public String toJsonSerialized() {
        try {
            var mapper = new ObjectMapper();
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException exception) {
            return "JsonProcessingException";
        }
    }
}
